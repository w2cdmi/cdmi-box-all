#include "OutlookTable.h"
#include "CppSQLite3.h"
#include <boost/thread/mutex.hpp>
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("OutlookTable")
#endif

// outlook table name
#define TN_OUTLOOK ("tb_outlook")
// outlook table row name
#define TRN_EMAILID ("emailId")
#define TRN_GROUP ("groupId")
#define TRN_LOCALPATH ("localpath")
#define TRN_REMOTEID ("remoteId")
#define TRN_SHARELINK ("shareLink")

#define TABLE_ROW_ALL \
	TRN_EMAILID,\
	TRN_GROUP,\
	TRN_LOCALPATH,\
	TRN_SHARELINK

class OutlookTable::Impl
{
public:
	Impl(const std::wstring& path)
		:path_(path)
	{
		(void)create();
	}

	int32_t addNode(const OutlookNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s) VALUES\
											('%s','%s','%s','%s')", 
											TN_OUTLOOK, 
											TABLE_ROW_ALL,
											Utility::String::wstring_to_utf8(node->emailId).c_str(),
											Utility::String::wstring_to_utf8(node->group).c_str(),
											CppSQLiteUtility::formatSqlStr(node->localPath).c_str(),
											CppSQLiteUtility::formatSqlStr(node->shareLink).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNode(const std::wstring& group, OutlookNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s FROM %s WHERE %s='%s' LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TN_OUTLOOK, 
				TRN_GROUP, Utility::String::wstring_to_utf8(group).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.reset(new (std::nothrow)st_OutlookNode);
			if (NULL == node.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}

			node->emailId = Utility::String::utf8_to_wstring(q.getStringField(0));
			node->group = group;
			node->localPath = Utility::String::utf8_to_wstring(q.getStringField(2));
			node->shareLink = Utility::String::utf8_to_wstring(q.getStringField(3));

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodes(const std::wstring& emailId, OutlookNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s FROM %s WHERE %s='%s'", 
				TABLE_ROW_ALL, 
				TN_OUTLOOK, 
				TRN_EMAILID, Utility::String::wstring_to_utf8(emailId).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				OutlookNode node(new (std::nothrow)st_OutlookNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}

				node->emailId = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->group = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->localPath = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->shareLink = Utility::String::utf8_to_wstring(q.getStringField(3));

				nodes.push_back(node);

				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getUnShareLinkNodes(const std::wstring& emailId, OutlookNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s FROM %s WHERE %s='' AND %s='%s'", 
				TABLE_ROW_ALL, 
				TN_OUTLOOK, 
				TRN_SHARELINK, 
				TRN_EMAILID, Utility::String::wstring_to_utf8(emailId).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				OutlookNode node(new (std::nothrow)st_OutlookNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}

				node->emailId = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->group = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->localPath = Utility::String::utf8_to_wstring(q.getStringField(2));

				nodes.push_back(node);

				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getShareLinkedNodes(const std::wstring& emailId, OutlookNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s FROM %s WHERE %s<>'' AND %s='%s'", 
				TABLE_ROW_ALL, 
				TN_OUTLOOK, 
				TRN_SHARELINK, 
				TRN_EMAILID, Utility::String::wstring_to_utf8(emailId).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				OutlookNode node(new (std::nothrow)st_OutlookNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}

				node->emailId = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->group = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->localPath = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->shareLink = Utility::String::utf8_to_wstring(q.getStringField(3));

				nodes.push_back(node);

				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateShareLink(const std::wstring& group, const std::wstring& shareLink)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s' WHERE %s='%s'", 
				TN_OUTLOOK, 
				TRN_SHARELINK, CppSQLiteUtility::formatSqlStr(shareLink).c_str(), 
				TRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNode(const std::wstring& group)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TN_OUTLOOK, 
				TRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNodes(const std::wstring& emailId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TN_OUTLOOK, 
				TRN_EMAILID, CppSQLiteUtility::formatSqlStr(emailId).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	int32_t create()
	{
		if (path_.empty())
		{
			return RT_INVALID_PARAM;
		}

		try
		{
			db_.open(Utility::String::wstring_to_utf8(path_).c_str());
			if(!db_.tableExists(TN_OUTLOOK))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
								 %s TEXT NOT NULL ,\
								 %s TEXT PRIMARY KEY NOT NULL ,\
								 %s TEXT NOT NULL ,\
								 %s TEXT DEFAULT '')", 
								 TN_OUTLOOK, 
								 TABLE_ROW_ALL);
				(void)db_.execDML(sql);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
};

OutlookTable::OutlookTable(const std::wstring& path)
	:impl_(new Impl(path))
{

}

int32_t OutlookTable::addNode(const OutlookNode& node)
{
	return impl_->addNode(node);
}

int32_t OutlookTable::getNode(const std::wstring& group, OutlookNode& node)
{
	return impl_->getNode(group, node);
}

int32_t OutlookTable::getNodes(const std::wstring& emailId, OutlookNodes& nodes)
{
	return impl_->getNodes(emailId, nodes);
}

int32_t OutlookTable::getUnShareLinkNodes(const std::wstring& emailId, OutlookNodes& nodes)
{
	return impl_->getUnShareLinkNodes(emailId, nodes);
}

int32_t OutlookTable::getShareLinkedNodes(const std::wstring& emailId, OutlookNodes& nodes)
{
	return impl_->getShareLinkedNodes(emailId, nodes);
}

int32_t OutlookTable::updateShareLink(const std::wstring& group, const std::wstring& shareLink)
{
	return impl_->updateShareLink(group, shareLink);
}

int32_t OutlookTable::deleteNode(const std::wstring& group)
{
	return impl_->deleteNode(group);
}

int32_t OutlookTable::deleteNodes(const std::wstring& emailId)
{
	return impl_->deleteNodes(emailId);
}
