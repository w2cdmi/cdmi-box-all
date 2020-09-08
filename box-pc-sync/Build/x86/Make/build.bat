@ECHO off

@CD ../v3.5
@DEL /F OneBox.exe & DEL /F vhCalendar.dll
@CD ../v4.0
@DEL /F OneBox.exe & DEL /F vhCalendar.dll
@CD ../v4.5
@DEL /F OneBox.exe & DEL /F vhCalendar.dll
@CD ..
@DEL /F OneboxSyncHelper.dll
@DEL /F OneboxSyncService.exe & DEL /F OneboxSDK.dll
@DEL /F OneboxAutoStart.exe
@DEL /F /Q ShellExtentbk*.dll
@REN ShellExtent.dll ShellExtentbk%time:~9,2%.dll  & REN  ShellExtent_x64.dll ShellExtentbk_x64%time:~9,2%.dll & DEL /F /Q OneboxShExtCmd.exe & DEL /F /Q OneboxShExtCmd_x64.exe
@REN OutlookAddin.dll OutlookAddinbk%time:~9,2%.dll  & REN  OutlookAddin_x64.dll OutlookAddinbk_x64%time:~9,2%.dll

@CD ../..
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

IF /i "%BuildArch%" == "x64" @SET BuildType=x64


@ECHO building OneboxSync_%BuildParam%_%BuildType%_%BuildArch%...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\OneboxSync\OneboxSync.sln" /%BuildParam% "%BuildType%|%BuildArchX86%"
IF %ERRORLEVEL% EQU 1 DEL /F /Q "..\OneboxSyncService.exe" & DEL /F "..\OneboxSDK.dll"  & DEL /F "..\OneboxSyncHelper.dll"


@ECHO building OneboxUI_%BuildParam%_%BuildType%_%BuildArch%...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\UI\Onebox.sln" /%BuildParam% "%BuildType%_v3.5|Any CPU"



@ECHO building OneboxUI_%BuildParam%_%BuildType%_%BuildArch%...




@ECHO building OneboxUI_%BuildParam%_%BuildType%_%BuildArch%...




@ECHO building OneboxShellExtension_%BuildParam%_%BuildType%_%BuildArch%...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\Extensions\ShellExtension\ShellExtent.sln" /%BuildParam% "%BuildType%|%BuildArchX86%"





@ECHO building OneboxAutoStart_%BuildParam%_%BuildType%_%BuildArch%...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\AutoStartOnebox\AutoStartOnebox.sln" /%BuildParam% "%BuildType%|%BuildArchX86%"





@ECHO building TerminateProcess_%BuildParam%_%BuildType%_%BuildArch%...




@ECHO building OneboxOutlookAddin_%BuildParam%_%BuildType%_%BuildArch%...
"%DevEnvDir%\devenv.com" "%SrcFilePath%\Extensions\OutlookExtension\OutlookAddin\OutlookAddin.sln" /%BuildParam% "%BuildType%|%BuildArchX86%"
IF %ERRORLEVEL% EQU 1  DEL /F /Q "..\OutlookAddin.dll"

"%DevEnvDir%\devenv.com" "%SrcFilePath%\Extensions\OutlookExtension\OutlookAddin\OutlookAddin.sln" /%BuildParam% "%BuildType%|%BuildArchX64%"
IF %ERRORLEVEL% EQU 1  DEL /F /Q "..\OutlookAddin_x64.dll