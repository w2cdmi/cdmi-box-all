// dllmain.h : 模块类的声明。

class COutlookAddinModule : public ATL::CAtlDllModuleT< COutlookAddinModule >
{
public :
	DECLARE_LIBID(LIBID_OutlookAddinLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_OUTLOOKADDIN, "{A93EA4B7-C44D-4795-9570-CEF6CC7F4AF0}")
};

extern class COutlookAddinModule _AtlModule;
