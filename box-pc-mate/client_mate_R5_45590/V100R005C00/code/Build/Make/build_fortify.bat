
@SET SrcFilePath=D:\build\test\code\current\clent_v2\projects\client\pc\V100R005C00\code
set SOURCEANALYZER="D:\agent\plugins\CodeCC\tool\fortify\bin\sourceanalyzer"


%SOURCEANALYZER% -b fortify_build_pc -clean

call "%SrcFilePath%\Onebox\Solutions\FortifySolutions.bat"

call "%SrcFilePath%\Onebox\Extensions\FortifyExtensions.bat"

call "%SrcFilePath%\Onebox\AutoStartOnebox\FortifyAutoStartOnebox.bat"

rem call "%SrcFilePath%\Onebox\OneboxCMBAdapter\FortifyOneboxCMBAdapter.bat"

call "%SrcFilePath%\Onebox\Extensions\ShellExtension\ShellExtent\FortifyShellExtent.bat"


