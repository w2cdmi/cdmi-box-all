#pragma once
#include "CommonFileDialogThriftNotify.h"
#include <stdint.h>
#include <UIlib.h>

using namespace DuiLib;
struct OfficeUploadInfo
{
	int64_t			id;
	std::wstring	remotePath;
	int32_t			extype;
	OfficeUploadInfo()
	{
		id = -1;
		remotePath = L"";
		extype = -1;
	}
};

typedef std::shared_ptr<OfficeUploadInfo> POFFICEUPLOADINFO;

class OfficeAddinUploadPath : public WindowImplBase
{
	DUI_DECLARE_MESSAGE_MAP()

public:
	OfficeAddinUploadPath(const std::wstring& path, HWND hwnd, POFFICEUPLOADINFO officeuploadinfo);

	virtual ~OfficeAddinUploadPath(){};

	virtual void InitWindow();

	virtual bool InitLanguage(CControlUI* control);

	virtual void OnFinalMessage(HWND hWnd);

	virtual LPCTSTR GetWindowClassName(void) const;

	virtual CDuiString GetSkinFolder();

	virtual CDuiString GetSkinFile();

	virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

	void OnClick(TNotifyUI& msg);

	void resultHandler(const Onebox::CommonFileDialogListResult& result);

	void setControlAttribute();

	void show();

	UINT showModal();

	std::wstring& GetPath();
	std::wstring& GetId();
	std::wstring& GetParentPath();
	int32_t		  GetCommonFolderEXType();

	POFFICEUPLOADINFO GetUploadInfo();

	bool		  IsOk();
private:
	std::wstring path_;
	std::wstring id_;
	std::wstring parentPath_;
	int64_t parentId_;
	int32_t commonFolderEXType_;

	bool m_state_;
	POFFICEUPLOADINFO m_officeuploadinfo_;
};