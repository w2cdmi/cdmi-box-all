#include "BackupAllFullScan.h"
#include "Utility.h"
#include "SysConfigureMgr.h"
#include "BackupAllDbMgr.h"
#include "BackupAllLocalFile.h"
#include <boost/thread.hpp>
#include "MFTReader.h"
#include "SmartHandle.h"
#include "BackupAllUtility.h"
#include "BackupAllFilterMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllFullScan")
#endif

#define UR_BUF_LEN 1024

using namespace SD::Utility;

class BackupAllFullScanImpl : public BackupAllFullScan
{
public:
	BackupAllFullScanImpl(UserContext* userContext):userContext_(userContext)
	{
		pFilterMgr_ = BackupAllFilterMgr::create(userContext_);
		pTaskDb_ = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb();
	}

	virtual ~BackupAllFullScanImpl()
	{
		writeThread_.interrupt();
		writeThread_.join();
	}

	void backupDisk(const std::wstring& disk, const USN& lastUsn, const int64_t& rootRemoteId)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, String::format_string("backupDisk %s, %I64d", String::wstring_to_string(disk).c_str(), lastUsn));
		
		curVolume_ = disk;
		pLocalDb_ = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(curVolume_);

		//添加根节点
		BATaskLocalNode node;
		node.baseInfo.path = disk;
		node.baseInfo.localId = VOLUME_ROOTID;
		node.baseInfo.localParent = 0;
		node.baseInfo.type = FILE_TYPE_DIR;

		if(pFilterMgr_->isFilter(node.baseInfo.path))
		{
			node.baseInfo.opType = node.baseInfo.opType|BAO_Filter;
		}

		scanNodes_.clear();
		scanningDirs_.clear();
		scannedDirs_.clear();
		
		if(0==lastUsn)
		{
			//如果云端根路径不存在，创建根路径
			if(-1==pLocalDb_->getRemoteIdById(node.baseInfo.localId))
			{
				std::auto_ptr<BackupAllLocalFile> pLocalFile = BackupAllLocalFile::create(userContext_);
				if(RT_OK!=pLocalFile->create(rootRemoteId, node))
				{
					return;
				}
				pLocalDb_->addRootNode(node);
			}
			//获取当前USN
			USN nextUsn;
			std::auto_ptr<MFTReader> pMFTReader = MFTReader::create(userContext_, curVolume_);
			if(RT_OK!=pMFTReader->getNextUsn(nextUsn))
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "getNextUsn failed.");
				return;
			}
			//先保存负值，全量完成后再设为正值，用于区分是否已完成全量
			pTaskDb_->updateVolumeUsn(disk,	-nextUsn);
			scanningDirs_.insert(node.baseInfo.localId);
			pFilterMgr_->getSelectInfo(curVolume_, scanningDirs_);
			pLocalDb_->markExist(true);
			pLocalDb_->initIncInfo();
		}
		else
		{
			//获取断点续扫信息
			pLocalDb_->getBrokenPoint(scanningDirs_, scannedDirs_);
			if(scanningDirs_.empty())
			{
				//When the data is lost, restart backupDisk.
				backupDisk(disk, 0, rootRemoteId);
			}
		}
		boost::this_thread::interruption_point();

		//未开始扫描、或还存在未扫描完成文件夹时，启动扫描
		if(!scanningDirs_.empty())
		{
			isScanning_ = true;
			isWriteDb_ = true;
			writeThread_ = boost::thread(boost::bind(&BackupAllFullScanImpl::writeDbAsync, this));
			
			if(scanningDirs_.end()!=scanningDirs_.find(node.baseInfo.localId))
			{
				listFromRoot(node.baseInfo);
				scanningDirs_.erase(node.baseInfo.localId);
			}
			BATaskBaseNode nextNode;
			nextNode.type = FILE_TYPE_DIR;
			while(RT_OK==pTaskDb_->getNextSelectPath(scanningDirs_, nextNode))
			{
				nextNode.localParent = BackupAll::getIdByPath(userContext_, SD::Utility::FS::get_parent_path(nextNode.path));
				{
					boost::mutex::scoped_lock lock(mutex_);
					scanNodes_.push_back(nextNode);
				}
				listLocal(nextNode);
				{
					boost::mutex::scoped_lock lock(mutex_);
					scanningDirs_.erase(nextNode.localId);
				}
			}

			isScanning_ = false;
			while(isWriteDb_)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(100));
			}
		}
	}

private:
	void writeDbAsync()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, String::format_string("writeDbAsync %s", SD::Utility::String::wstring_to_string(curVolume_).c_str()));
		BATaskBaseNodeList nodes;
		std::set<int64_t> dirList;
		while(true)
		{
			boost::this_thread::interruption_point();
			if(scanNodes_.size()>500||!isScanning_)
			{
				{
					boost::mutex::scoped_lock lock(mutex_);
					nodes.swap(scanNodes_);
					dirList = scanningDirs_;
				}
				pLocalDb_->addNodes(nodes, dirList);
				nodes.clear();
				dirList.clear();
			}
	
			boost::this_thread::sleep(boost::posix_time::milliseconds(10));

			if(!isScanning_ && scanNodes_.empty())
			{
				break;
			}
		}
		//完成扫描后，删除未命中的旧节点
		pLocalDb_->deleteMarkNodes();
		pLocalDb_->markExist(false);
		pTaskDb_->updateVolumeUsn(curVolume_);
		isWriteDb_ = false;
	}

	void listFromRoot(const BATaskBaseNode& parentNode)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, String::format_string("listFromRoot %s", SD::Utility::String::wstring_to_string(parentNode.path).c_str()));

		WIN32_FIND_DATA wfd;
        HANDLE hFind = FindFirstFile(std::wstring(parentNode.path + L"\\*").c_str(), &wfd);
		
        if(hFind == INVALID_HANDLE_VALUE)
        {
			HSLOG_ERROR(MODULE_NAME, GetLastError(), "list local folder %s.", String::wstring_to_string(parentNode.path).c_str());
            return;
        }

		BATaskBaseNodeList fileNodes;
        while(FindNextFile(hFind, &wfd))
        {
			boost::this_thread::interruption_point();
			std::wstring tempName = wfd.cFileName;
            if((L"." != tempName) && (L".." != tempName) 
				&& !userContext_->getSysConfigureMgr()->isBackupDisableAttr(wfd.dwFileAttributes))
            {
				std::wstring path = parentNode.path + PATH_DELIMITER + tempName;
				if(FS::is_directory(path))
				{
					//忽略禁止备份目录
					if(userContext_->getSysConfigureMgr()->isBackupDisable(path))
					{
						continue;
					}
					//自身为过滤对象、或父为过滤对象且自身非选择对象时，设置为过滤
					if(pFilterMgr_->isFilter(path, (0!=(parentNode.opType&BAO_Filter))))
					{
						continue;
					}
				}
				else
				{
					//继承父的过滤状态
					if(0!=(parentNode.opType&BAO_Filter))
					{
						continue;
					}
				}

				SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(), 
					GENERIC_READ, 
					FILE_SHARE_READ|FILE_SHARE_WRITE, 
					NULL, 
					OPEN_EXISTING, 
					FILE_ATTRIBUTE_NORMAL | FILE_FLAG_BACKUP_SEMANTICS, 
					NULL);
				if (INVALID_HANDLE_VALUE == hFile)
				{
					BATaskLocalNode errorNode;
					errorNode.baseInfo.path = path;
					errorNode.errorCode = GetLastError();
					pLocalDb_->addRootNode(errorNode);
					HSLOG_ERROR(MODULE_NAME, errorNode.errorCode, "%s CreateFile failed.", String::wstring_to_string(path).c_str());
					continue;
				}

				BY_HANDLE_FILE_INFORMATION bhfi;
				(void)memset_s(&bhfi, sizeof(BY_HANDLE_FILE_INFORMATION), 0, sizeof(BY_HANDLE_FILE_INFORMATION));
				if (!GetFileInformationByHandle(hFile, &bhfi))
				{
					BATaskLocalNode errorNode;
					errorNode.baseInfo.path = path;
					errorNode.errorCode = GetLastError();
					pLocalDb_->addRootNode(errorNode);
					HSLOG_ERROR(MODULE_NAME, errorNode.errorCode, "%s GetFileInformationByHandle failed.", String::wstring_to_string(path).c_str());
					continue;
				}
				BATaskBaseNode node;
				int64_t id = bhfi.nFileIndexHigh;
				id = (id<<32)+bhfi.nFileIndexLow;
				node.localId = id;
				node.localParent = parentNode.localId;
				if(bhfi.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)
				{
					node.path = path;
					if(scannedDirs_.end()!=scannedDirs_.find(node.localId))
					{
						scannedDirs_.erase(node.localId);
						continue;
					}
					node.type = FILE_TYPE_DIR;
					if(scanningDirs_.end()==scanningDirs_.find(node.localId))
					{
						boost::mutex::scoped_lock lock(mutex_);
						scanNodes_.push_back(node);
						scanningDirs_.insert(node.localId);
					}
					listLocal(node);
					{
						boost::mutex::scoped_lock lock(mutex_);
						scanningDirs_.erase(node.localId);
					}
				}
				else
				{
					node.path = tempName;
					node.type = FILE_TYPE_FILE;
					int64_t size = bhfi.nFileSizeHigh;
					size = (size<<32)+bhfi.nFileSizeLow;
					node.size = size;
					int64_t mtime = bhfi.ftLastWriteTime.dwHighDateTime;
					mtime = (mtime<<32)+bhfi.ftLastWriteTime.dwLowDateTime;
					node.mtime = mtime;
					fileNodes.push_back(node);
				}
            }
        }
		{
			boost::mutex::scoped_lock lock(mutex_);
			scanNodes_.splice(scanNodes_.end(), fileNodes);
		}
		if ( INVALID_HANDLE_VALUE != hFind )
		{
			(void)FindClose(hFind);
			hFind = NULL;
		}
	}

	void listLocal(const BATaskBaseNode& parentNode)
	{
		SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\" + parentNode.path).c_str(),
			GENERIC_READ,
			FILE_SHARE_READ,
			NULL,
			OPEN_ALWAYS,
			FILE_ATTRIBUTE_NORMAL | FILE_FLAG_BACKUP_SEMANTICS,
			NULL);

		if(INVALID_HANDLE_VALUE == hFile)
		{
			BATaskLocalNode errorNode;
			errorNode.baseInfo = parentNode;
			errorNode.errorCode = GetLastError();
			pLocalDb_->updateExInfo(errorNode);
			HSLOG_ERROR(MODULE_NAME, errorNode.errorCode, "%s CreateFile failed.", String::wstring_to_string(parentNode.path).c_str());
			return;
		}

		std::auto_ptr<char> buf(new char[UR_BUF_LEN]);
		(void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);
		FILE_ID_BOTH_DIR_INFO *fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();

		fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo + fileInfo->NextEntryOffset);
        if(!GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN))
        {
			BATaskLocalNode errorNode;
			errorNode.baseInfo = parentNode;
			errorNode.errorCode = GetLastError();
			pLocalDb_->updateExInfo(errorNode);
            HSLOG_ERROR(MODULE_NAME, errorNode.errorCode, "%s list folder faild, get parent information failed.", String::wstring_to_string(parentNode.path).c_str());
            return;
        }
        std::wstring name = L"";
		BATaskBaseNodeList fileNodes;
        do
        {
            while(true)
            {
				boost::this_thread::interruption_point();
                name = L"";
                name.append(fileInfo->FileName, fileInfo->FileNameLength / sizeof(wchar_t));
                if(L"." != name && L".." != name
					&& !userContext_->getSysConfigureMgr()->isBackupDisableAttr(fileInfo->FileAttributes))
                {
					addScanNode(parentNode, name, fileNodes, fileInfo);
                }

                if(0 == fileInfo->NextEntryOffset)
                {
                    break;
                }

                fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo + fileInfo->NextEntryOffset);
            }

            fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();
            (void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);
        }
		while(GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN));

		{
			boost::mutex::scoped_lock lock(mutex_);
			scanNodes_.splice(scanNodes_.end(), fileNodes);
		}
	}

	void addScanNode(const BATaskBaseNode& parentNode, const std::wstring& name, BATaskBaseNodeList& fileNodes, FILE_ID_BOTH_DIR_INFO *fileInfo)
	{
		BATaskBaseNode node;
		node.localId = BackupAll::li64toll(fileInfo->FileId);
		node.localParent = parentNode.localId;
		if(fileInfo->FileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		{
			if(scannedDirs_.end()!=scannedDirs_.find(node.localId))
			{
				scannedDirs_.erase(node.localId);
				return;
			}
			node.path = parentNode.path + PATH_DELIMITER + name;
			//忽略禁止备份目录
			if(userContext_->getSysConfigureMgr()->isBackupDisable(node.path))
			{
				return;
			}

			while(scanNodes_.size()>1000)
			{
				boost::this_thread::sleep(boost::posix_time::microseconds(10));
			}
			//自身为过滤对象、或父为过滤对象且自身非选择对象时，设置为过滤
			if(pFilterMgr_->isFilter(node.path, (0!=(parentNode.opType&BAO_Filter))))
			{
				return;
			}
			node.type = FILE_TYPE_DIR;
			if(scanningDirs_.end()==scanningDirs_.find(node.localId))
			{
				boost::mutex::scoped_lock lock(mutex_);
				scanNodes_.push_back(node);
				scanningDirs_.insert(node.localId);
			}
			listLocal(node);
			{
				boost::mutex::scoped_lock lock(mutex_);
				scanningDirs_.erase(node.localId);
			}
		}
		else
		{
			//继承父的过滤状态
			if(0!=(parentNode.opType&BAO_Filter))
			{
				return;
			}
			node.path = name;
			node.type = FILE_TYPE_FILE;
			node.size = BackupAll::li64toll(fileInfo->EndOfFile);
			node.mtime = BackupAll::li64toll(fileInfo->LastWriteTime);
			fileNodes.push_back(node);
		}	
	}

private:
	UserContext* userContext_;
	std::auto_ptr<BackupAllFilterMgr> pFilterMgr_;
	BackupAllTaskDb* pTaskDb_;
	BackupAllLocalDb* pLocalDb_;
	std::wstring curVolume_;

	std::set<int64_t> scanningDirs_;
	std::set<int64_t> scannedDirs_;
	boost::thread writeThread_;
	BATaskBaseNodeList scanNodes_;
	bool isScanning_;
	bool isWriteDb_;
	boost::mutex mutex_;
};

std::auto_ptr<BackupAllFullScan> BackupAllFullScan::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllFullScan>(new BackupAllFullScanImpl(userContext));
}