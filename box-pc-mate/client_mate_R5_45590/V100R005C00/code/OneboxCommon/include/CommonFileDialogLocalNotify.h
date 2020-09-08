#ifndef _LOCAL_FILE_DIALOG_H_
#define _LOCAL_FILE_DIALOG_H_

#include "CommonFileDialog.h"
#include "SkinConfMgr.h"
#include <Shlobj.h>
#include "Utility.h"
#include "InILanguage.h"

namespace Onebox
{
	using namespace SD;

	enum LocalCommonFileDialogItemDataType
	{
		LCFDIDT_desktop,
		LCFDIDT_my_documents,
		LCFDIDT_my_music,
		LCFDIDT_my_video,
		LCFDIDT_my_picture,
		LCFDIDT_my_computer,
		LCFDIDT_common_path
	};

	class LocalCommonFileDialogNotify : public ICommonFileDialogNotify
	{
	public:
		LocalCommonFileDialogNotify(uint32_t languageId)
			:languageId_(languageId)
		{

		}

		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			// root
			if (NULL == parent.get())
			{
				return listRoot(result);
			}

			if (parent->dataType != CFDDT_local || parent->type != CFDFT_DIR)
			{
				return E_CFD_INVALID_PARAM;
			}
			LocalCommonFileDialogItemDataType type = *(LocalCommonFileDialogItemDataType*)parent->data.get();
			switch (type)
			{
			case Onebox::LCFDIDT_desktop:
				return listDesktop(result);
			case Onebox::LCFDIDT_my_documents:
			case Onebox::LCFDIDT_my_music:
			case Onebox::LCFDIDT_my_video:
			case Onebox::LCFDIDT_my_picture:
				return listVirtualPath(result, type);
			case Onebox::LCFDIDT_my_computer:
				return listMyComputer(result);
			case Onebox::LCFDIDT_common_path:
				return listPath(result, parent->path);
			default:
				break;
			}

			return E_CFD_INVALID_PARAM;
		}

		virtual std::wstring loadIcon(const CommonFileDialogItem& item)
		{
			if (NULL == item.get())
			{
				return L"";
			}
			return SkinConfMgr::getInstance()->getIconPath(item->type, item->name);
		}

		virtual CommonFileDialogItem createItem(const CommonFileDialogItem& parent)
		{
			CommonFileDialogItem item(NULL);
			if (NULL == parent.get())
			{
				item;
			}

			LocalCommonFileDialogItemDataType type = *(LocalCommonFileDialogItemDataType*)(parent->data.get());
			std::wstring parentPath = L"";

			if ( type==LCFDIDT_common_path && parent->type==CFDFT_DIR && !parent->path.empty() )
			{
				parentPath = parent->path;
			}
			else if( type==LCFDIDT_desktop || 
					 type==LCFDIDT_my_documents || 
					 type==LCFDIDT_my_music || 
					 type==LCFDIDT_my_video || 
					 type==LCFDIDT_my_picture)
			{
				int32_t csid = 0;
				switch (type)
				{
				case Onebox::LCFDIDT_desktop:
					{
						csid = CSIDL_DESKTOP;
					}
					break;
				case Onebox::LCFDIDT_my_documents:
					{
						csid = CSIDL_MYDOCUMENTS;
					}
					break;
				case Onebox::LCFDIDT_my_music:
					{
						csid = CSIDL_MYMUSIC;
					}
					break;
				case Onebox::LCFDIDT_my_video:
					{
						csid = CSIDL_MYVIDEO;
					}
					break;
				case Onebox::LCFDIDT_my_picture:
					{
						csid = CSIDL_MYPICTURES;
					}
					break;
				}

				WCHAR szPath[MAX_PATH] = {0};
				if (!SHGetSpecialFolderPath(NULL, szPath, csid, FALSE))
				{
					return item;
				}
				parentPath = szPath;
			}

			if (parentPath.empty())
			{
				return item;
			}

			std::wstring newItemPath = L"", newItemName = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_new_folder_name), 
				newTmpName = newItemName;
			int32_t suffix = 2;
			// get new path
			newItemPath += parentPath + PATH_DELIMITER + newTmpName;
			while (Utility::FS::is_exist(newItemPath))
			{
				newTmpName = newItemName + Utility::String::format_string(L" (%d)", suffix++);
				newItemPath = parentPath + PATH_DELIMITER + newTmpName;
			}
			// create new path
			if (E_CFD_SUCCESS != Utility::FS::create_directory(newItemPath))
			{
				return item;
			}
			// fill in item information
			item.reset(new st_CommonFileDialogItem);
			item->name = newTmpName;
			item->path = newItemPath;
			item->type = CFDFT_DIR;
			item->dataType = CFDDT_local;
			item->data.reset(new LocalCommonFileDialogItemDataType);
			*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_common_path;
			item->notify = this;

			return item;
		}

		virtual int32_t renameItem(const CommonFileDialogItem& item, const std::wstring& name)
		{
			if (NULL == item.get())
			{
				return E_CFD_INVALID_PARAM;
			}
			if (item->path.empty() || name.empty())
			{
				return E_CFD_INVALID_PARAM;
			}			
			if (E_CFD_SUCCESS != Utility::FS::rename(item->path, name))
			{
				return E_CFD_ERROR;
			}
			return E_CFD_SUCCESS;
		}

		virtual int32_t removeItem(const CommonFileDialogItem& item)
		{
			if (NULL == item.get())
			{
				return E_CFD_INVALID_PARAM;
			}
			if (item->path.empty())
			{
				return E_CFD_INVALID_PARAM;
			}			
			if (E_CFD_SUCCESS != Utility::FS::remove(item->path))
			{
				return E_CFD_ERROR;
			}
			return E_CFD_SUCCESS;
		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}

			LocalCommonFileDialogItemDataType type = *(LocalCommonFileDialogItemDataType*)(item->data.get());
			return ((type==LCFDIDT_common_path && item->type==CFDFT_DIR) || 
				type==LCFDIDT_desktop || 
				type==LCFDIDT_my_documents || 
				type==LCFDIDT_my_music || 
				type==LCFDIDT_my_video || 
				type==LCFDIDT_my_picture);
		}

	private:
		int32_t listRoot(CommonFileDialogListResult& result)
		{
			// desktop
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_desktop_name);
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_local;
				item->data.reset(new LocalCommonFileDialogItemDataType);
				*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_desktop;
				item->notify = this;

				result.push_back(item);
			}
			// my computer
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_computer_name);
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_local;
				item->data.reset(new LocalCommonFileDialogItemDataType);
				*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_my_computer;
				item->autoExpand = true;
				item->notify = this;

				result.push_back(item);
			}
			//// my document
			//{
			//	CommonFileDialogItem item(new st_CommonFileDialogItem);
			//	item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_document_name);
			//	item->type = CFDFT_DIR;
			//	item->dataType = CFDDT_local;
			//	item->data = new LocalCommonFileDialogItemDataType;
			//	*(LocalCommonFileDialogItemDataType*)(item->data) = LCFDIDT_my_documents;
			//	item->notify = this;

			//	result.push_back(item);
			//}
			//// my music
			//{
			//	CommonFileDialogItem item(new st_CommonFileDialogItem);
			//	item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_music_name);
			//	item->type = CFDFT_DIR;
			//	item->dataType = CFDDT_local;
			//	item->data = new LocalCommonFileDialogItemDataType;
			//	*(LocalCommonFileDialogItemDataType*)(item->data) = LCFDIDT_my_music;
			//	item->notify = this;

			//	result.push_back(item);
			//}
			//// my video
			//{
			//	CommonFileDialogItem item(new st_CommonFileDialogItem);
			//	item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_video_name);
			//	item->type = CFDFT_DIR;
			//	item->dataType = CFDDT_local;
			//	item->data = new LocalCommonFileDialogItemDataType;
			//	*(LocalCommonFileDialogItemDataType*)(item->data) = LCFDIDT_my_video;
			//	item->notify = this;

			//	result.push_back(item);
			//}
			//// my picture
			//{
			//	CommonFileDialogItem item(new st_CommonFileDialogItem);
			//	item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_picture_name);
			//	item->type = CFDFT_DIR;
			//	item->dataType = CFDDT_local;
			//	item->data = new LocalCommonFileDialogItemDataType;
			//	*(LocalCommonFileDialogItemDataType*)(item->data) = LCFDIDT_my_picture;
			//	item->notify = this;

			//	result.push_back(item);
			//}
			return E_CFD_SUCCESS;
		}

		int32_t listDesktop(CommonFileDialogListResult& result)
		{
			// my computer
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_my_computer_name);
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_local;
				item->data.reset(new LocalCommonFileDialogItemDataType);
				*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_my_computer;
				item->notify = this;

				result.push_back(item);
			}
			// desktop files
			WCHAR szPath[MAX_PATH] = {0};
			if (!SHGetSpecialFolderPath(NULL, szPath, CSIDL_DESKTOP, FALSE))
			{
				return E_CFD_ERROR;
			}
			return listPath(result, szPath);
		}

		int32_t listMyComputer(CommonFileDialogListResult& result)
		{
			DWORD drivers = GetLogicalDrives();
			if (0 == drivers)
			{
				return E_CFD_ERROR;
			}
			WCHAR volume[4] = L"C:\\";
			WCHAR volumeName[MAX_PATH] = {0};
			for (int32_t i = 0; i < 26; ++i)
			{
				if ((drivers&(0x01<<i)))
				{
					volume[0] = L'A' + i;
					(void)memset_s(volumeName, sizeof(volumeName), 0, sizeof(volumeName));
					if (!GetVolumeInformation(volume, volumeName, MAX_PATH, NULL, NULL, NULL, NULL, 0))
					{
						continue;
					}
					CommonFileDialogItem item(new st_CommonFileDialogItem);
					item->name = volumeName;
					if (item->name.empty())
					{
						item->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_disk_name);
					}
					item->name += L" (" + std::wstring(volume).substr(0, 2) + L")";
					item->path = std::wstring(volume).substr(0, 2);
					item->type = CFDFT_DIR;
					item->dataType = CFDDT_local;
					item->data.reset(new LocalCommonFileDialogItemDataType);
					*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_common_path;
					item->notify = this;

					result.push_back(item);
				}
			}
			return E_CFD_SUCCESS;
		}

		int32_t listPath(CommonFileDialogListResult& result, const std::wstring path)
		{
			WIN32_FIND_DATA wfd;
			HANDLE hFind = FindFirstFile(std::wstring(path+L"\\*").c_str(), &wfd);
			if(hFind==INVALID_HANDLE_VALUE)
			{
				return E_CFD_ERROR;
			}
			std::wstring tempName = L"";
			CommonFileDialogListResult files, folders;
			while(FindNextFile(hFind, &wfd))
			{
				tempName = wfd.cFileName;
				if (L"." == tempName || L".." == tempName)
				{
					continue;
				}

				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = tempName;
				item->path = path+L"\\"+tempName;
				item->dataType = CFDDT_local;
				item->data.reset(new LocalCommonFileDialogItemDataType);
				*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_common_path;
				item->notify = this;
				if (wfd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)
				{
					item->type = CFDFT_DIR;
					folders.push_back(item);
				}
				else
				{
					item->type = CFDFT_FILE;
					files.push_back(item);
				}
			}
			FindClose(hFind);

			result.insert(result.end(), folders.begin(), folders.end());
			result.insert(result.end(), files.begin(), files.end());

			return E_CFD_SUCCESS;
		}

		int32_t listVirtualPath(CommonFileDialogListResult& result, LocalCommonFileDialogItemDataType type)
		{
			WCHAR szPath[MAX_PATH] = {0};
			int csidl = 0;
			switch (type)
			{
			case Onebox::LCFDIDT_my_documents:
				csidl = CSIDL_MYDOCUMENTS;
				break;
			case Onebox::LCFDIDT_my_music:
				csidl = CSIDL_MYMUSIC;
				break;
			case Onebox::LCFDIDT_my_video:
				csidl = CSIDL_MYVIDEO;
				break;
			case Onebox::LCFDIDT_my_picture:
				csidl = CSIDL_MYPICTURES;
				break;
			default:
				return E_CFD_NOT_IMPLEMENT;
			}
			if (!SHGetSpecialFolderPath(NULL, szPath, csidl, FALSE))
			{
				return E_CFD_ERROR;
			}
			return listPath(result, szPath);
		}

	private:
		uint32_t languageId_;
	};
}

#endif