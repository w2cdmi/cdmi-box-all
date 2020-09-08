@REM *************************************************************
@REM 参数说明
@REM Version：产品版本号
@REM MainVersion: 产品迭代版本号，例如：V100R003C10B010
@REM UpdateType: 升级类型，0表示普通升级，1表示强制升级
@REM BuildParam：编译参数，Build表示增量编译，Rebuild表示重新编译
@REM BuildType：编译类型，例如：Release、Debug等等
@REM *************************************************************

@ECHO OFF
@SET CurrentPath=%CD%
@SET Version=%1
@SET MainVersion=%2
@SET UpdateType=%3
@SET BuildParam=%4
@SET BuildType=%5

IF /i "%Version%" == "" @SET Version=1.0.0.0
IF /i "%MainVersion%" == "" @SET MainVersion=V100R005C00
IF /i "%UpdateType%" == "" @SET UpdateType=0
IF /i "%BuildParam%" == "" @SET BuildParam=Rebuild
IF /i "%BuildType%" == "" @SET BuildType=Release
DEL /F build.log
CALL CopyRes.bat >> build.log
CD %CurrentPath%
python.exe install.py %Version% %MainVersion% %UpdateType% %BuildParam% %BuildType% >> build.log
CALL pdbcollect.bat