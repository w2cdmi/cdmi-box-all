#include "ThumbMgr.h"
#include "UserContextMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "CppSQLite3.h"
#include <boost/thread.hpp>
#include <fstream>
#include <set>
#include "NotifyMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ThumbMgr")
#endif

using namespace SD::Utility;

#define THUMB_SMALL_SIZE (32)	//小图大小32*32
#define THUMB_LARGE_SIZE (72)	//大图大小72*72

class ThumbMgrImpl : public ThumbMgr
{
public:
	ThumbMgrImpl():isThreadAlive_(false)
	{
		userContext_ = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL!=userContext_)
		{
			parentPath_ = String::wstring_to_string(userContext_->getConfigureMgr()->getConfigure()->appUserDataPath()) + PATH_DELIMITER_STR
				+ String::type_to_string<std::string>(userContext_->id.id) + PATH_DELIMITER_STR;	
		}
		parentPath_ = parentPath_ + "thumb" + PATH_DELIMITER_STR;
		if (!FS::is_exist(String::string_to_wstring(parentPath_)))
		{
			(void)FS::create_directories(String::string_to_wstring(parentPath_));
		}
		createTable(String::string_to_wstring(parentPath_));
	}

	~ThumbMgrImpl()
	{
		if(isThreadAlive_)
		{
			queryThread_.interrupt();
			queryThread_.join();
		}
	}

	//type 0:小图、1:大图
	virtual int32_t addThumb(int64_t ownerId, int64_t fileId, int32_t type)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		std::string thumbUrl;
		int32_t size = (0==type)?THUMB_SMALL_SIZE:THUMB_LARGE_SIZE;
		ret = client().getThumbUrl(ownerId, fileId, size, size, thumbUrl);
		if(RT_OK!=ret)
		{
			return ret;
		}

		std::string fileName = String::type_to_string<std::string>(ownerId) + "_" 
				+ String::type_to_string<std::string>(fileId) + ((0==type)?"_s.jpg":"_l.jpg");
		ret = client().downloadByUrl(thumbUrl, parentPath_+fileName);
		if(RT_OK!=ret)
		{
			return ret;
		}

		std::ifstream ffin(parentPath_+fileName, std::ios::binary);
		char s1[2] = {0}, s2[2] = {0};
		ffin.seekg(164);        
		ffin.read(s1, 2);
		ffin.read(s2, 2);       
		int32_t height = (unsigned int)(s1[1])<<8|(unsigned int)(s1[0]);
		int32_t width = (unsigned int)(s2[0]);    

		std::stringstream temp;
		if(0==type)
		{
			temp << "'" << (THUMB_SMALL_SIZE-width)/2 << "," << (THUMB_SMALL_SIZE-height)/2
				<< "," << (THUMB_SMALL_SIZE+width+1)/2 << "," << (THUMB_SMALL_SIZE+height+1)/2 << "'"; 
		}
		else
		{
			temp << "'" << (THUMB_LARGE_SIZE-width)/2 << "," << (THUMB_LARGE_SIZE-height)/2
				<< "," << (THUMB_LARGE_SIZE+width+1)/2 << "," << (THUMB_LARGE_SIZE+height+1)/2 << "'"; 
		}
			
		std::stringstream key;
		key << ownerId << "_" << fileId << "_" << type;
		ret = addThumbInfo(key.str(), fileName, temp.str());

		return ret;
	}

	virtual std::wstring getThumbPath(int64_t ownerId, int64_t fileId, int32_t type)
	{
		std::stringstream key;
		key << ownerId << "_" << fileId << "_" << type;
		std::string showPath = getShowPath(key.str());
		if(showPath.empty())
		{
			boost::mutex::scoped_lock lock(dataMutex_);
			callSet_.insert(key.str());
			if(!isThreadAlive_)
			{
				isThreadAlive_ = true;
				queryThread_ = boost::thread(boost::bind(&ThumbMgrImpl::getThumbPathAsync, this));
			}
		}
		
		return String::utf8_to_wstring(showPath);
	}

	virtual void getThumbPathAsync()
	{
		try
		{
			while(!callSet_.empty())
			{
				{
					boost::mutex::scoped_lock lock(dataMutex_);
					workSet_.swap(callSet_);
				}
				for(std::set<std::string>::iterator it = workSet_.begin();
					it != workSet_.end(); ++it)
				{
					boost::this_thread::interruption_point();
					std::vector<std::string> keyInfo;
					String::split(*it, keyInfo, "_");

					addThumb(String::string_to_type<int64_t>(keyInfo[0]), String::string_to_type<int64_t>(keyInfo[1]), 
						String::string_to_type<int32_t>(keyInfo[2]));
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_REFRESH_THUMB,SD::Utility::String::utf8_to_wstring(*it)));
				}
				workSet_.clear();
			}
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "getThumbPathAsync interrupt");
		}
		isThreadAlive_ = false;
	}

	virtual int32_t deleteThumb(int64_t ownerId, int64_t fileId)
	{
		return RT_OK;
	}

	virtual std::string getShowPath(const std::string key)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s FROM %s WHERE %s = '%s' LIMIT 0, 1", 
								THUMB_ROW_FILENAME,
								THUMB_ROW_DEST,
								TABLE_THUMB,
								THUMB_ROW_KEY, CppSQLiteUtility::formatSqlStr(key).c_str());
			CppSQLite3Query qSet = db_.execQuery(sql);
			if(!qSet.eof())
			{
				std::stringstream temp;
				temp << "file='" << parentPath_+qSet.getStringField(0) << "' dest=" << qSet.getStringField(1); 
				return temp.str();
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return "";
	}

private:
	int32_t addThumbInfo(const std::string& key, const std::string& fileName, const std::string& dest)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("REPLACE INTO %s (%s,%s,%s) VALUES ('%s','%s','%s')", 
				TABLE_THUMB, 
				THUMB_ROW_KEY,
				THUMB_ROW_FILENAME,
				THUMB_ROW_DEST,
				CppSQLiteUtility::formatSqlStr(key).c_str(),
				CppSQLiteUtility::formatSqlStr(fileName).c_str(),
				CppSQLiteUtility::formatSqlStr(dest).c_str());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	void createTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_THUMB;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_THUMB))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s VARCHAR PRIMARY KEY NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR);", 
									TABLE_THUMB,
									THUMB_ROW_KEY,
									THUMB_ROW_FILENAME,
									THUMB_ROW_DEST);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_thumb_idx1", TABLE_THUMB, THUMB_ROW_KEY);
				(void)db_.execDML(bufSQLIdx1);
			}	
		}
		CATCH_SQLITE_EXCEPTION;
	}

private:
	UserContext* userContext_;
	std::string parentPath_;

	std::set<std::string> callSet_;
	std::set<std::string> workSet_;

	boost::mutex mutex_;
	CppSQLite3DB db_;
	boost::mutex dataMutex_;
	boost::thread queryThread_;
	bool isThreadAlive_;
};

std::auto_ptr<ThumbMgr> ThumbMgr::instance_(NULL);

ThumbMgr* ThumbMgr::getInstance()
{
	if (NULL == instance_.get())
	{
		instance_.reset(new ThumbMgrImpl());
	}
	return instance_.get();
}


