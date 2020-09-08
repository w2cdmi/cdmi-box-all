@ECHO OFF

@SET DesDir="symbols%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%%time:~9,2%"

@RD /S /Q symbols >> build.log

@MD symbols >> build.log

@COPY /Y  "..\output\BackupTask.pdb"  ".\symbols\BackupTask.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\MessageProxy.pdb"  ".\symbols\MessageProxy.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\ShellExtent.pdb"  ".\symbols\ShellExtent.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\OutlookAddin.pdb"  ".\symbols\OutlookAddin.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\OfficeAddin.pdb"  ".\symbols\OfficeAddin.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y "..\output\Common.pdb"  ".\symbols\Common.pdb"  >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\NetSDK.pdb"  ".\symbols\NetSDK.pdb" >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  "..\output\Onebox_Mate.pdb"  ".\symbols\Onebox_Mate.pdb" >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@REN symbols %DesDir% >> build.log
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT

