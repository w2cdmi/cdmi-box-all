// dllmain.h : 模块类的声明。

class COfficeAddinModule : public ATL::CAtlDllModuleT< COfficeAddinModule >
{
public :
	DECLARE_LIBID(LIBID_OfficeAddinLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_OFFICEADDIN, "{96EBA1A4-8430-4223-9576-C790E59C01F1}")
};

extern class COfficeAddinModule _AtlModule;
