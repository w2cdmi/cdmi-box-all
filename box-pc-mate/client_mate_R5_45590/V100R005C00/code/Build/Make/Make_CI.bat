@REM *************************************************************
@REM ����˵��
@REM Version����Ʒ�汾��
@REM MainVersion: ��Ʒ�����汾�ţ����磺V100R003C10B010
@REM UpdateType: �������ͣ�0��ʾ��ͨ������1��ʾǿ������
@REM BuildParam�����������Build��ʾ�������룬Rebuild��ʾ���±���
@REM BuildType���������ͣ����磺Release��Debug�ȵ�
@REM *************************************************************

@ECHO OFF
@SET VS2012_THIRD_PART=D:\build\test\code\current\clent_v2\third_party_groupware\3rd
@SET path=%path%;C:\Program Files (x86)\NSIS;C:\Python26
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