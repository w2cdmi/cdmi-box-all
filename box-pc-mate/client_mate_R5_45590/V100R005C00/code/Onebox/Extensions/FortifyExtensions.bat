@echo off
REM ###########################################################################
REM Script generated by HP Fortify SCA Scan Wizard (c) HP Fortify 2011
REM Created on 2015/09/24 16:34:06
REM ###########################################################################
REM Generated for the following languages:
REM 	XML
REM ###########################################################################
REM DEBUG - if set to true, runs SCA in debug mode
REM SOURCEANALYZER - the name of the SCA executable
REM BUILDID - the SCA build id
REM LAUNCHERSWITCHES - the launcher settings that are used to invoke SCA
REM ARGFILE - the name of the argument file that's extracted and passed to SCA
REM MEMORY - the memory settings for SCA
REM OLDFILENUMBER - this defines the file which contains the number of files within the project, it is automatically generated
REM FILENOMAXDIFF - this is the percentage of difference between the number of files which will trigger a warning by the script
REM ###########################################################################

set DEBUG=false
set SOURCEANALYZER="D:\agent\plugins\CodeCC\tool\fortify\bin\sourceanalyzer"
set BUILDID="fortify_build_pc"
set ARGFILE="FortifyExtensions.bat.args"
set MEMORY=-Xmx1200M -Xms600M -Xss24M 
set LAUNCHERSWITCHES=""
set OLDFILENUMBER=FortifyExtensions.bat.fileno
set FILENOMAXDIFF=10
set DEVENV="C:\Program Files (x86)\Microsoft Visual Studio 11.0\Common7\IDE\devenv"

set PROJECTROOT0="D:\build\test\code\current\clent_v2\projects\client\pc\V100R005C00\code\Onebox\Extensions"
IF NOT EXIST %PROJECTROOT0% (
   ECHO  ERROR: This script is being run on a different machine than it was
   ECHO         generated on or the targeted project has been moved. This script is 
   ECHO         configured to locate files at
   ECHO            %PROJECTROOT0%
   ECHO         Please modify the %%PROJECTROOT0%% variable found
   ECHO         at the top of this script to point to the corresponding directory
   ECHO         located on this machine.
   GOTO :FINISHED
)

IF %DEBUG%==true set LAUNCHERSWITCHES=-debug %LAUNCHERSWITCHES%
echo Extracting Arguments File


echo. >FortifyExtensions.bat.args
SETLOCAL ENABLEDELAYEDEXPANSION
IF EXIST %0 (
   set SCAScriptFile=%0
) ELSE (
  set SCAScriptFile=%0.bat
)

set PROJECTROOT0=%PROJECTROOT0:)=^)%
FOR /f "delims=" %%a IN ('findstr /B /C:"REM ARGS" %SCAScriptFile%' ) DO (
   set argVal=%%a
   set argVal=!argVal:PROJECTROOT0_MARKER=%PROJECTROOT0:~1,-1%!
   echo !argVal:~9! >> %ARGFILE%
)
ENDLOCAL

REM ###########################################################################
echo Running Build Integration
%SOURCEANALYZER% %MEMORY% %LAUNCHERSWITCHES% -b %BUILDID%  %DEVENV% "D:\build\test\code\current\clent_v2\projects\client\pc\V100R005C00\code\Onebox\Extensions\Extensions.sln" /REBUILD Release

IF %ERRORLEVEL%==1 (
echo Sourceanalyzer failed, exiting
GOTO :FINISHED
)


REM ###########################################################################
echo Finished
:FINISHED
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.exe"
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.dll"
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.mod"
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.mdl"
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.aspx"
REM ARGS -exclude "PROJECTROOT0_MARKER\**\*.master"
REM ARGS "PROJECTROOT0_MARKER"