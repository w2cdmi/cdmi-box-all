#include "DataTransfer.h"
#include "TransTableDefine.h"
#include "TransCompleteTable.h"
#include "TransRootTable.h"
#include "TransDetailTable.h"
#include "BackupAllTaskTable.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"

using namespace SD;

#define MODULE_NAME ("DataTransfer")
#define USER_DATA_DIR_NAME (L"UserData")

enum R3_type
{
	R3_ATT_Upload_Manual = 0x04,	
	R3_ATT_Download_Manual = 0x08,
	R3_ATT_Upload_Attachements = 0x10,
	R3_ATT_Upload_File = 0x20,
	R3_ATT_Upload_Backup = 0x40	,
	R3_ATT_Invalid = 0xff	
};

enum R3_UserContextType
{
	R3_UserContext_User,
	R3_UserContext_Teamspace,
	R3_UserContext_ShareUser,
	R3_UserContext_Group,
	R3_UserContext_Invalid
};

class CDataTransfer::Impl
{
public:
	Impl(const std::wstring& userId)
		:userId_(userId)
	{
	}

	~Impl()
	{

	}

	bool UpdateDB()
	{
		SERVICE_INFO(MODULE_NAME, RT_OK, "start update userid:%s.", 
			Utility::String::wstring_to_string(userId_).c_str());

		// first remove the old trash data
		std::wstring userDataPath = getAppUserDataPath() + PATH_DELIMITER + userId_;
		if (Utility::FS::is_directory(userDataPath))
		{
			if (RT_OK != Utility::FS::remove_all(userDataPath))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "remove old trash data failed.");
				return false;
			}
		}

		if (!updateTransTable(userId_))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "update trans table failed.");
			return false;
		}

		if (!updateBackupTable(userId_))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "update backup table failed.");
			return false;
		}
		return true;
	}

private:
	std::wstring getInstallPath()
	{
		// the work directory is the install path
		return Utility::FS::get_work_directory();
	}

	std::wstring getAppUserDataPath()
	{
		return (Utility::FS::get_system_user_app_path() + 
			PATH_DELIMITER + ONEBOX_APP_DIR + 
			PATH_DELIMITER + USER_DATA_DIR_NAME);
	}

	AsyncTransType R3Type2R5Type(const R3_type type)
	{
		AsyncTransType R5Type = ATT_Invalid;
		switch (type)
		{
		case R3_ATT_Upload_Manual:
			R5Type = ATT_Upload;
			break;
		case R3_ATT_Upload_File:
			R5Type = ATT_Upload_Office;
			break;
		case R3_ATT_Download_Manual:
			R5Type = ATT_Download;
			break;
		default:
			break;
		}
		return R5Type;
	}

	UserContextType R3UserType2R5UserType(const R3_UserContextType type)
	{
		UserContextType R5UserContextType = UserContextType::UserContext_Invalid;
		switch (type)
		{
		case R3_UserContext_User:
			R5UserContextType = UserContext_User;
			break;
		case R3_UserContext_Teamspace:
			R5UserContextType = UserContext_Teamspace;
			break;
		case R3_UserContext_ShareUser:
			R5UserContextType = UserContext_ShareUser;
			break;
		case R3_UserContext_Group:
			R5UserContextType = UserContext_Group;
			break;
		default:
			break;
		}
		return R5UserContextType;
	}

	bool updateTransRootAndDetailTable(const std::wstring& userId)
	{
		std::wstring olddbpath = getInstallPath() + PATH_DELIMITER + 
			USER_DATA_DIR_NAME + PATH_DELIMITER + userId + PATH_DELIMITER + L"taskInfo.db";
		if (!Utility::FS::is_exist(olddbpath))
		{
			return true;
		}

		try
		{
			CppSQLite3DB db;
			db.open(Utility::String::wstring_to_utf8(olddbpath).c_str());
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("select groupId, id, parent, name, type, fileType, \
							 userContextId, userContextType, userContextName, totalSize \
							 from tb_transTaskInfo where parentPath='0'");
			q = db.execQuery(sql);
			AsyncTransRootNodes transRootNodes;
			while (!q.eof())
			{
				AsyncTransType type = R3Type2R5Type(R3_type(q.getIntField(4)));
				if (type != ATT_Upload && type != ATT_Download)
				{
					q.nextRow();
					continue;
				}

				AsyncTransRootNode node(new (std::nothrow)st_AsyncTransRootNode);
				if (!node)
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "malloc memory failed.");
					return false;
				}					

				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = type;
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->userId = q.getInt64Field(6);
				node->userType = R3UserType2R5UserType(R3_UserContextType(q.getIntField(7)));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(8));
				if (node->fileType == FILE_TYPE_FILE)
				{
					node->size = q.getInt64Field(9);
				}
				else
				{
					node->statusEx = ATSEX_Scanning;
				}
				node->status = ATS_Waiting;

				transRootNodes.push_back(node);
				q.nextRow();
			}

			std::wstring newpath = getAppUserDataPath() + 
				PATH_DELIMITER + userId + PATH_DELIMITER + L"TransTaskTables";
			if (!Utility::FS::is_directory(newpath))
			{
				if (RT_OK != Utility::FS::create_directories(newpath))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create trans table directory failed.");
					return false;
				}
			}			

			std::auto_ptr<TransRootTable> rootTable(new TransRootTable(newpath + PATH_DELIMITER + TTFN_ROOT));
			if (RT_OK != rootTable->create())
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create root table failed.");
				return false;
			}
			if (RT_OK != rootTable->addNodes(transRootNodes))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add root nodes failed.");
				return false;
			}

			std::wstring newDetailPath = getAppUserDataPath() + 
				PATH_DELIMITER + userId + PATH_DELIMITER + L"TransTaskTables" + L"\\details";
			if (!Utility::FS::is_directory(newDetailPath))
			{
				if (RT_OK != Utility::FS::create_directories(newDetailPath))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create trans detail directory failed.");
					return false;
				}
			}
			
			for (AsyncTransRootNodes::iterator it = transRootNodes.begin(); 
				it != transRootNodes.end(); ++it)
			{
				AsyncTransRootNode& transRootNode = *it;
				if (transRootNode->fileType != FILE_TYPE_DIR)
				{
					continue;
				}
				AsyncTransDetailNode transDetailNode(new (std::nothrow)st_AsyncTransDetailNode);
				if (!transDetailNode)
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "malloc memory failed.");
					return false;
				}
				std::auto_ptr<TransDetailTable> detailTable(new TransDetailTable(
					newDetailPath + PATH_DELIMITER + transRootNode->group + L".db"));
				if (RT_OK != detailTable->create(transRootNode))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create detail table of %s failed.", 
						Utility::String::wstring_to_string(transRootNode->group).c_str());
					return false;
				}

				transDetailNode->source = transRootNode->source;
				transDetailNode->parent = transRootNode->parent;
				transDetailNode->name = transRootNode->name;
				transDetailNode->fileType = transRootNode->fileType;
				transDetailNode->status = ATS_Waiting;
				transDetailNode->statusEx = ATSEX_Scanning;

				if (RT_OK != detailTable->addNode(transDetailNode))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add detail node of %s failed.", 
						Utility::String::wstring_to_string(transDetailNode->source).c_str());
					return false;
				}
			}
			return true;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	bool updateTransCompleteTable(const std::wstring& userId)
	{
		std::wstring oldPath = getInstallPath() + PATH_DELIMITER + 
			USER_DATA_DIR_NAME + PATH_DELIMITER + userId + PATH_DELIMITER + L"taskInfo.db";
		if (!Utility::FS::is_exist(oldPath))
		{
			return true;
		}

		try
		{
			CppSQLite3DB db;
			db.open(Utility::String::wstring_to_utf8(oldPath).c_str());
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("select groupId, id, parent, name, type, fileType, \
							 userContextId, userContextType, userContextName, totalSize\
							 from tb_transTaskCompleteInfo where parentPath='0'");
			q = db.execQuery(sql);
			AsyncTransCompleteNodes transCompleteNodes;
			while (!q.eof())
			{
				AsyncTransCompleteNode node(new (std::nothrow)st_AsyncTransCompleteNode);
				if (!node)
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "malloc memory failed.");
					return false;
				}

				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = R3Type2R5Type(R3_type(q.getIntField(4)));
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->userId = q.getInt64Field(6);
				node->userType = R3UserType2R5UserType(R3_UserContextType(q.getIntField(7)));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(8));
				node->size = q.getInt64Field(9);
				node->completeTime = time(NULL);

				transCompleteNodes.push_back(node);
				q.nextRow();
			}

			std::wstring newPath = getAppUserDataPath() + 
				PATH_DELIMITER + userId + PATH_DELIMITER + L"TransTaskTables";
			std::auto_ptr<TransCompleteTable> completeTable(
				new TransCompleteTable(newPath + PATH_DELIMITER + TTFN_COMPLETE));
			if (RT_OK != completeTable->create())
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create trans complete table failed.");
				return false;
			}
			if (RT_OK != completeTable->addNodes(transCompleteNodes))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add trans complete nodes failed.");
				return false;
			}
			return true;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	bool updateTransTable(const std::wstring& userId)
	{
		if (!updateTransRootAndDetailTable(userId))
		{
			return false;
		}
		return updateTransCompleteTable(userId);
	}

	bool updateBackupTable(const std::wstring& userId)
	{
		std::wstring dbpath = getInstallPath() + PATH_DELIMITER + 
			USER_DATA_DIR_NAME + PATH_DELIMITER + userId + PATH_DELIMITER + userId + PATH_DELIMITER;
		if (!Utility::FS::is_exist(dbpath + L"backupTaskInfo.db"))
		{
			return true;
		}
		try
		{
			CppSQLite3DB db;
			std::set<std::wstring> selectList;
			std::set<std::wstring> volumeInfo;
			db.open(Utility::String::wstring_to_utf8(dbpath + L"backupTaskInfo.db").c_str());
			{
				CppSQLite3Buffer sql;
				(void)sql.format("SELECT local_path FROM tb_backupTaskInfo ORDER BY local_path");
				CppSQLite3Query q = db.execQuery(sql);
				while(!q.eof())
				{
					std::wstring curPath = Utility::String::utf8_to_wstring(q.getStringField(0));
					volumeInfo.insert(curPath.substr(0,2));
					bool isNew = true;
					for(std::set<std::wstring>::const_iterator it = selectList.begin(); it!= selectList.end(); ++it)
					{
						std::wstring parentPath = *it + PATH_DELIMITER;
						if(0==curPath.find(parentPath))
						{
							isNew = false;
							break;
						}
					}
					if(isNew)
					{
						selectList.insert(curPath);
					}
					q.nextRow();
				}
			}
			db.close();

			if(selectList.empty())
			{
				Utility::FS::remove(dbpath + L"backupTaskInfo.db");
				return true;
			}

			std::wstring newpath = getAppUserDataPath() 
				+ PATH_DELIMITER + userId + PATH_DELIMITER + userId + PATH_DELIMITER;

			BackupAllTaskTable* taskDb = BackupAllTaskTable::create(newpath);
			if(NULL==taskDb) return false;
			BATaskNode node;
			node.status = BATS_Running;
			if(RT_OK==taskDb->addNode(node)
				&&RT_OK==taskDb->updatePathInfo(selectList, volumeInfo))
			{
				//Utility::FS::remove(dbpath + L"backupTaskInfo.db");			
			}
			if(NULL!=taskDb)
			{
				delete taskDb;
				taskDb = NULL;
			}
			return true;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

private:
	std::wstring userId_;
};

CDataTransfer::CDataTransfer(const std::wstring& userId)
	:impl_(new Impl(userId))
{
	std::string oneboxAppDir = Utility::String::wstring_to_string(
		Utility::FS::get_system_user_app_path() + PATH_DELIMITER + ONEBOX_APP_DIR);
	(void)ISSP_LogInit(oneboxAppDir + "\\log4cpp.conf", TP_FILE, oneboxAppDir + "\\DataTransfer.log");
}

CDataTransfer::~CDataTransfer(void)
{
	ISSP_LogExit();
}

bool CDataTransfer::UpdateDB()
{
	return impl_->UpdateDB();
}
