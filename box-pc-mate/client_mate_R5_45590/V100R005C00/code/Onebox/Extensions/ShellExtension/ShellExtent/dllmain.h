// dllmain.h : 模块类的声明。

class CShellExtentModule : public ATL::CAtlDllModuleT< CShellExtentModule >
{
public :
	DECLARE_LIBID(LIBID_ShellExtentLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_ShellExtent, "{3747801B-4E00-42F2-BD9E-A289072EBDAC}")
};

extern class CShellExtentModule _AtlModule;
