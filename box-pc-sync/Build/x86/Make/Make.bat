@ECHO OFF
IF /i "%VS2012_THIRD_PART%" == "" @SET VS2012_THIRD_PART=..\..\..\..\..\..\..\third_party_groupware\3rd
@SET path=%path%;C:\Program Files\NSIS;D:\Python26
@SET Version=%1
@SET UpdateType=%2
@SET BuildParam=%3
@SET BuildType=%4

IF /i "%Version%" == "" @SET Version=1.0.0.0
IF /i "%UpdateType%" == "" @SET UpdateType=0
IF /i "%BuildParam%" == "" @SET BuildParam=Rebuild
IF /i "%BuildType%" == "" @SET BuildType=Release
python.exe install.py %Version% %UpdateType% %BuildParam% %BuildType%

CALL pdbcollect.bat