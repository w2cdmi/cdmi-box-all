#ifndef _ONEBOX_SKIN_CONFIGURE_MGR_H_
#define _ONEBOX_SKIN_CONFIGURE_MGR_H_

namespace Onebox
{
	class SkinConfMgr
	{
	public:
		static SkinConfMgr* getInstance();

		virtual ~SkinConfMgr(){}

		virtual std::wstring getIconPath(int32_t type, const std::wstring& fileName, int32_t extraType = 0) = 0;

		virtual std::wstring getIconPath(int32_t type, const std::wstring& fileName,int32_t flags,const std::wstring& folderExtraType) = 0;

		virtual std::wstring getBigIconPath(int32_t type, const std::wstring& fileName) = 0;

		virtual std::wstring getBigIconPath(int32_t type, const std::wstring& fileName, int32_t flags,const std::wstring& folderExtraType) = 0;

		virtual bool isUnknownType(const std::wstring& fileExt) = 0;
	private:
		static SkinConfMgr* instance_;
	};
}

#endif
