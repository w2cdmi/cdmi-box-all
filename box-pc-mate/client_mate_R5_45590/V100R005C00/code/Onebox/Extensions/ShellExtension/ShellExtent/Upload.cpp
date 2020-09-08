#include "Upload.h"
#include "ThriftClient.h"
#include "Global.h"

class Upload : public UploadImpl 
{
public:
	Upload();

	virtual void UploadFile(std::wstring FilePath);

private:
	void resultHandler(const Onebox::CommonFileDialogListResult& result);
private:
	std::wstring m_filePath;
};

Upload ::Upload()
{

}

void Upload::UploadFile(std::wstring FilePath)
{
	m_filePath = FilePath;
	IniLanguageHelper iniLanguageHelper;
	std::wstring strTiltle = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY) + L" \""
		+ m_filePath  +  L"\"";
	
	Onebox::CommonFileDialogPtr fileDailog = Onebox::CommonFileDialog::createInstance(GetInstallPath()+ iniLanguageHelper.GetSkinFolderPath(),NULL,NULL,iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY)
		,RIGHT_UPLOADFRAME_CLSNAME,iniLanguageHelper.GetCommonString(COMMENT_SELECTCLOUD_KEY),strTiltle.c_str(),m_filePath);
	
	fileDailog->setNotify(new Onebox::MyFileTeamspaceMixedCommonFileDialogThriftNotify(SyncServiceClientWrapper::getInstance()->getCurrentUserId(), iniLanguageHelper.GetLanguage()));
	fileDailog->setOption(Onebox::CFDO_only_show_folder);
	fileDailog->show(std::bind(&Upload::resultHandler, this, std::placeholders::_1));
	fileDailog.release();
}

void Upload::resultHandler(const Onebox::CommonFileDialogListResult& result)
{
	if (result.empty())
	{
		return;
	}

	Onebox::CommonFileDialogItem item = result.front();
	if (NULL == item.get())
	{
		return;
	}
	Onebox::RemoteCommonFileDialogThriftData *data = (Onebox::RemoteCommonFileDialogThriftData*)item->data.get();
	if (NULL == data)
	{
		return;
	}
	SyncServiceClientWrapper::getInstance()->upload(m_filePath, data->id, data->userId, data->userType);
}

std::auto_ptr<UploadImpl> UploadImpl::instance_;

UploadImpl* UploadImpl::getInstance()
{
	if (NULL == instance_.get())
	{
		instance_.reset(new Upload());
	}
	return instance_.get();
}