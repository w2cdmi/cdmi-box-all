#include "MetaDataTable.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("MetaDataTable")
#endif

class MetaDataInfoImpl : public MetaDataTable
{
public:
	MetaDataInfoImpl(UserContext* userContext, const std::wstring& parent, const long& offset)
	{
		versionTable_ = VersionTable::create(parent);
		localTable_ = LocalTable::create(parent);
		remoteTable_ = RemoteTable::create(parent);
		relationTable_ = RelationTable::create(parent);
		diffTable_ = DiffTable::create(userContext, parent);
		transTaskTable_ = TransTaskTable::create(parent);
		uploadTable_ = UploadTable::create(parent, offset);
	}

	virtual ~MetaDataInfoImpl(void)
	{
	}
	
	virtual VersionTable* getVersionTable()
	{
		return versionTable_.get();
	}

	virtual LocalTable* getLocalTable()
	{
		return localTable_.get();
	}

	virtual RemoteTable* getRemoteTable()
	{
		return remoteTable_.get();
	}

	virtual RelationTable* getRelationTable()
	{
		return relationTable_.get();
	}

	virtual DiffTable* getDiffTable()
	{
		return diffTable_.get();
	}

	virtual TransTaskTable* getTransTaskTable()
	{
		return transTaskTable_.get();
	}

	virtual UploadTable* getUploadTable()
	{
		return uploadTable_.get();
	}

	virtual int32_t checkUploadFilterInfo()
	{
		int32_t ret = RT_OK;

		IdList idList;
		CHECK_RESULT(uploadTable_->getAllUploadInfo(idList));

		for(IdList::const_iterator it = idList.begin(); it != idList.end();)
		{
			if(!localTable_->isExist(*it))
			{
				it = idList.erase(it);
			}
			else
			{
				++it;
			}
		}

		CHECK_RESULT(uploadTable_->deleteUplaodInfo(idList));

		return RT_OK;
	}
private:
	std::auto_ptr<VersionTable> versionTable_;
	std::auto_ptr<LocalTable> localTable_;
	std::auto_ptr<RemoteTable> remoteTable_;
	std::auto_ptr<RelationTable> relationTable_;
	std::auto_ptr<DiffTable> diffTable_;
	std::auto_ptr<TransTaskTable> transTaskTable_;
	std::auto_ptr<UploadTable> uploadTable_;
};

std::auto_ptr<MetaDataTable> MetaDataTable::create(UserContext* userContext, const std::wstring& parent, const long& offset)
{
	return std::auto_ptr<MetaDataTable>(
		static_cast<MetaDataTable*>(new MetaDataInfoImpl(userContext, parent, offset)));
}