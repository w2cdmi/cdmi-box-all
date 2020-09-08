@ECHO OFF

@SET DesDir="symbols%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%%time:~9,2%"

@MD symbols 

@COPY /Y ..\OneboxSDK.pdb  .\symbols\OneboxSDK.pdb  
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y ..\OneboxStart.pdb  .\symbols\OneboxStart.pdb
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  ..\OneboxSyncService.pdb  .\symbols\OneboxSyncService.pdb  
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  ..\OneboxSyncHelper.pdb  .\symbols\OneboxSyncHelper.pdb
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  ..\OneboxShExtCmd.pdb  .\symbols\OneboxShExtCmd.pdb
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  ..\OneboxShExtCmd_x64.pdb  .\symbols\OneboxShExtCmd_x64.pdb
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@COPY /Y  ..\ShellExtent.pdb  .\symbols\ShellExtent.pdb  
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT
@REN symbols %DesDir%
@IF %ERRORLEVEL% NEQ 0 @PAUSE&EXIT

