// dllmain.h : 模块类的声明。

class CShellExtentModule : public ATL::CAtlDllModuleT< CShellExtentModule >
{
public :
	DECLARE_LIBID(LIBID_ShellExtentLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_ShellExtent, "{02A20240-9638-475E-B09F-53AFE69E5175}")
};

extern class CShellExtentModule _AtlModule;
