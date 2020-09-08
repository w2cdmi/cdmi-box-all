#ifndef _SHELL_COMMON_FILE_DIALOG_H_
#define _SHELL_COMMON_FILE_DIALOG_H_

#include <string>
#include <xstring>
#include <stdint.h>
#include <memory>
#include <map>
#include <list>
#include <windows.h>

typedef std::map<std::wstring,std::wstring> ShellCommonFileDialogFilter;
typedef std::list<std::wstring> ShellCommonFileDialogResult;

enum ShellCommonDialogType
{
	OpenAFile,
	OpenFiles,
	OpenAFolder,
	OpenFilesAndFolders
};

struct ShellCommonFileDialogParam
{
	ShellCommonDialogType type;
	std::wstring title;
	std::wstring okButtonName;
	ShellCommonFileDialogFilter filters;
	HWND parent;

	ShellCommonFileDialogParam()
	{
		type = OpenFilesAndFolders;
		title = L"";
		okButtonName = L"OK";
		filters.insert(std::make_pair(L"All Files (*.*)", L"*.*"));
		parent = NULL;
	}

	ShellCommonFileDialogParam(ShellCommonDialogType type_)
	{
		type = type_;
		title = L"";
		okButtonName = L"OK";
		filters.insert(std::make_pair(L"All Files (*.*)", L"*.*"));
		parent = NULL;
	}
};

class ShellCommonFileDialog
{
public:
	explicit ShellCommonFileDialog(const ShellCommonFileDialogParam& param);

	virtual ~ShellCommonFileDialog();
	
	bool getResults(ShellCommonFileDialogResult& results);

private:
	class Impl;
	std::auto_ptr<Impl> impl_;
};

#include "ShellCommonFileDialog.inl"

#endif