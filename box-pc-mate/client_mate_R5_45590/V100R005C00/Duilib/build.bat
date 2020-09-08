@ECHO OFF

@DEL /F ".\Bin\DuiLib.dll" >> build.log
@DEL /F ".\Bin\DuiLib.pdb" >> build.log
@DEL /F ".\Bin\DuiLib_d.dll" >> build.log
@DEL /F ".\Bin\DuiLib_d.pdb" >> build.log      
@DEL /F ".\Bin\DuiLib_d.exp" >> build.log 
@DEL /F ".\Bin\DuiLib_d.lib" >> build.log 
@DEL /F ".\Lib\DuiLib.exp" >> build.log
@DEL /F ".\Lib\DuiLib_d.exp" >> build.log
@DEL /F ".\Lib\DuiLib_d.lib" >> build.log
@DEL /F ".\Lib\DuiLibLib_d.lib" >> build.log
@DEL /F ".\Lib\DuiLibLib_mt.lib" >> build.log
@DEL /F ".\Lib\DuiLibLib_mt_d.lib" >> build.log
@DEL /F ".\Lib\DuiLibLib_mt_x64.lib" >> build.log
@DEL /F ".\Lib\DuiLibLib.lib" >> build.log
@DEL /F ".\Lib\DuiLib.lib" >> build.log

@SET SrcFilePath=%CD%
@SET DevEnvDir=%VS110COMNTOOLS%

IF "%DevEnvDir%" == "" (@ECHO Can not find the install path of Visual Studio 2012 ,make sure you have installed it.
			@PAUSE&EXIT )

@CD /d %DevEnvDir% 
@CD ..
@SET DevEnvDir=%CD%\IDE
@CD /d %SrcFilePath%

@SET BuildParam=%1
@SET BuildType=%2
@SET BuildArchX86=Win32
@SET BuildArchX64=x64

IF /i "%BuildParam%" == "" @SET BuildParam=Build
IF /i "%BuildType%" == "" @SET BuildType=Release
IF /i "%BuildArch%" == "x64" @SET BuildType=x64


@ECHO building Onebox_%BuildParam%_Debug_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "Debug|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_DebugLib_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "DebugLib|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_DebugLib_mt_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "DebugLib_mt|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_Release_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "Release|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_ReleaseLib_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "ReleaseLib|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_ReleaseLib_mt_Win32...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "ReleaseLib_mt|Win32" >> build.log

@ECHO building Onebox_%BuildParam%_ReleaseLib_mt_x64...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "ReleaseLib_mt|x64" >> build.log

@ECHO building Onebox_%BuildParam%_DebugLib_mt_x64...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\DuiLib.vcxproj" /%BuildParam% "DebugLib_mt|x64" >> build.log


@XCOPY %SrcFilePath%\Bin\DuiLib_d.lib  %SrcFilePath%\Lib /E /F /Y
@XCOPY %SrcFilePath%\Bin\DuiLib_d.exp  %SrcFilePath%\Lib /E /F /Y




