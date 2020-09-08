#include "CommonDefine.h"
#include "SkinConfMgr.h"
#include "InILanguage.h"
#include "Utility.h"
#include <fstream>
#include <IFile.h>
#include <map>

using namespace SD;
#define FOLDER_ICON_COMPUTER (L"computer")
#define FOLDER_ICON_DISK (L"disk")

namespace Onebox
{
	enum ConfType
	{
		Conf_FileIcon,
		Conf_Root
	};

	SkinConfMgr* SkinConfMgr::instance_ = NULL;

	class SkinConfMgrImpl : public SkinConfMgr
	{
	public:
		SkinConfMgrImpl();

		virtual std::wstring getIconPath(int32_t type, const std::wstring& fileName, int32_t extraType = 0);

		virtual std::wstring getIconPath(int32_t type, const std::wstring& fileName,int32_t flags,const std::wstring& folderExtraType);

		virtual std::wstring getBigIconPath(int32_t type, const std::wstring& fileName);

		virtual std::wstring getBigIconPath(int32_t type, const std::wstring& fileName, int32_t flags,const std::wstring& folderExtraType);

		virtual bool isUnknownType(const std::wstring& fileExt);
	private:
		void getFileIconInfo(const std::string& temp, ConfType& confType);

		std::string getValue(const std::string& str, const std::string& attrKey);

	private:
		std::wstring skinConfPath_;
		std::wstring iconBasePath_;
		std::map<std::wstring, std::wstring> fileIconInfos_;
		std::map<std::wstring, std::wstring> fileBigIconInfos_;
	};

	SkinConfMgr* SkinConfMgr::getInstance()
	{
		if (NULL == instance_)
		{
			instance_ = static_cast<SkinConfMgr*>(new SkinConfMgrImpl());
		}
		return instance_;
	}

	SkinConfMgrImpl::SkinConfMgrImpl()
	{
		try
		{
			if(skinConfPath_.empty())
			{
					iconBasePath_ = GetInstallPath() + L"skin\\Image\\";	//iniLanguageHelper.GetSkinFolderPath();
					skinConfPath_ = GetInstallPath() + L"skin\\Image\\skinConf.ini";
			}

			if (!Utility::FS::is_exist(skinConfPath_))
			{
				//TODO …Ë÷√ƒ¨»œ÷µ
				return;
			}

			std::ifstream infile(skinConfPath_, std::ios::in);
			std::string temp;
			ConfType confType = Conf_Root;

			while(getline(infile,temp))
			{
				switch (confType)
				{
				case Conf_FileIcon:
					getFileIconInfo(temp, confType);
					break;
				case Conf_Root:
					if(-1 != temp.find("<FileIconInfos>"))
					{
						confType = Conf_FileIcon;
					}
					break;
				default:
					break;
				}
			}
		}
		catch(...)
		{
		}
	}

	void SkinConfMgrImpl::getFileIconInfo(const std::string& temp, ConfType& confType)
	{
		if(-1 != temp.find("</FileIconInfos>"))
		{
			confType = Conf_Root;
			return;
		}
		std::wstring type = Utility::String::string_to_wstring(getValue(temp, "FileType"));
		std::wstring path = Utility::String::string_to_wstring(getValue(temp, "IconPath"));
		std::wstring bigPath = Utility::String::string_to_wstring(getValue(temp, "BigIconPath"));
		if(!type.empty())
		{
			fileIconInfos_.insert(std::make_pair(type, iconBasePath_+path));
			fileBigIconInfos_.insert(std::make_pair(type, iconBasePath_+bigPath));
		}
	}

	std::string SkinConfMgrImpl::getValue(const std::string& str, const std::string& attrKey)
	{
		std::string value = "";
		size_t pos = str.find(attrKey);
		if(-1!=pos)
		{
			size_t posStart = str.find("\"", pos) + 1;
			size_t posEnd = str.find("\"", posStart);
			if(posEnd>posStart)
			{
				value = str.substr(posStart, posEnd-posStart);
			}
		}
		return value;
	}

	std::wstring SkinConfMgrImpl::getIconPath(int32_t type, const std::wstring& fileName, int32_t extraType)
	{
		std::wstring iconPath = iconBasePath_+L"icon\\icon_defualt.png";
		std::wstring str_type;
		if (type == FILE_TYPE_FILE)
		{
			str_type = SD::Utility::FS::get_extension_name(fileName);
		}
		else
		{
			if(extraType == FOLDER)
			{
				str_type = L"folder";
			}
			else if(extraType == COMPUTER)
			{
				str_type = L"computer";
			}
			else if(extraType == DISK)
			{
				str_type = L"disk";
			}
		}

		std::map<std::wstring, std::wstring>::const_iterator it = fileIconInfos_.find(SD::Utility::String::to_lower(str_type));
		if(it!=fileIconInfos_.end())
		{
			iconPath = it->second;
		}
		return iconPath;
	}

	std::wstring SkinConfMgrImpl::getIconPath(int32_t type, 
		const std::wstring& fileName,
		int32_t flags,
		const std::wstring& folderExtraType)
	{

		if(flags & OBJECT_FLAG_SHARED)
		{
			if (type != FILE_TYPE_FILE)
			{
				if(FOLDER_ICON_COMPUTER == folderExtraType)
				{
					return (iconBasePath_ + L"icon\\icon_share_folderpc32.png");
				}
				else if(FOLDER_ICON_DISK == folderExtraType)
				{
					return (iconBasePath_ + L"icon\\icon_share_folderdisk32.png");
				}
				else
				{
					return (iconBasePath_ + L"icon\\icon_folder_share.png");
				}
			}
		}
		else
		{
			if(FOLDER_ICON_COMPUTER == folderExtraType)
			{
				return (iconBasePath_ + L"icon\\icon_folderpc32.png");
			}
			else if(FOLDER_ICON_DISK == folderExtraType)
			{
				return (iconBasePath_ + L"icon\\icon_folderdisk32.png");
			}
		}

		std::wstring iconPath = iconBasePath_+L"icon\\icon_defualt.png";
		std::wstring str_type;

		if (type == FILE_TYPE_FILE)
		{
			str_type = SD::Utility::FS::get_extension_name(fileName);
		}
		else
		{
			str_type = L"folder";
		}

		std::map<std::wstring, std::wstring>::const_iterator it = fileIconInfos_.find(SD::Utility::String::to_lower(str_type));
		if(it!=fileIconInfos_.end())
		{
			iconPath = it->second;
		}
		return iconPath;
	}

	std::wstring SkinConfMgrImpl::getBigIconPath(int32_t type, const std::wstring& fileName)
	{
		std::wstring iconPath = iconBasePath_+L"icon\\icon_defualt_large.png";
		std::wstring str_type;
		if (type == FILE_TYPE_DIR)
		{
			str_type = L"folder";
		}
		else
		{
			str_type = SD::Utility::FS::get_extension_name(fileName);
		}

		std::map<std::wstring, std::wstring>::const_iterator it = fileBigIconInfos_.find(SD::Utility::String::to_lower(str_type));
		if(it!=fileBigIconInfos_.end())
		{
			iconPath = it->second;
		}
		return iconPath;
	}

	std::wstring SkinConfMgrImpl::getBigIconPath(int32_t type, const std::wstring& fileName, int32_t flags,const std::wstring& folderExtraType)
	{
		if(flags & OBJECT_FLAG_SHARED)
		{
			if (type == FILE_TYPE_DIR)
			{				
				if(FOLDER_ICON_COMPUTER == folderExtraType)
				{
					return (iconBasePath_ + L"icon\\icon_share_folderpc72.png");
				}
				else if(FOLDER_ICON_DISK == folderExtraType)
				{
					return (iconBasePath_ + L"icon\\icon_share_folderdisk72.png");
				}
				else
				{
					return (iconBasePath_ + L"icon\\icon_folder_share_large.png");
				}
			}
		}
		else
		{
			if(FOLDER_ICON_COMPUTER == folderExtraType)
			{
				return (iconBasePath_ + L"icon\\icon_folderpc72.png");
			}
			else if(FOLDER_ICON_DISK == folderExtraType)
			{
				return (iconBasePath_ + L"icon\\icon_folderdisk72.png");
			}
		}

		std::wstring iconPath = iconBasePath_+L"icon\\icon_defualt_large.png";
		std::wstring str_type;
		if (type == FILE_TYPE_DIR)
		{
			str_type = L"folder";
		}
		else
		{
			str_type = SD::Utility::FS::get_extension_name(fileName);
		}

		std::map<std::wstring, std::wstring>::const_iterator it = fileBigIconInfos_.find(SD::Utility::String::to_lower(str_type));
		if(it!=fileBigIconInfos_.end())
		{
			iconPath = it->second;
		}
		return iconPath;
	}

	bool SkinConfMgrImpl::isUnknownType(const std::wstring& fileExt)
	{
		bool isUnknown = true;

		std::map<std::wstring, std::wstring>::const_iterator it = fileIconInfos_.find(SD::Utility::String::to_lower(fileExt));
		if(it!=fileIconInfos_.end())
		{
			isUnknown = false;
		}

		return isUnknown;
	}
}