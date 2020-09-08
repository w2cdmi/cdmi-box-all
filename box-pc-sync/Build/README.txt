一键打包使用说明：

1、NSIS的安装：

1) 运行NSIS_2.46_UltraModernUI_1.00_2010-11-11.exe文件将NSIS安装到PC；

2) 将Include.rar、Plugins.rar、nsis-2.46-log.zip文件解压，将里面的文件替换到NSIS安装目录下相应目录的相应文件。

2、Phython的安装：

1) 运行python-2.6.6.msi文件将Python安装到PC。

3、设置环境变量：

1）设置系统变量：VS2012_THIRD_PART为shareDirve工程下的3rd路径；如 VS2012_THIRD_PART = F:\V1R3C00\client_pc\3rd；

2）将NSIS的安装路径添加到环境变量Path；

3）将Python的安装路径添加到环境变量Path。

4、打包工具使用：

1) 在 Build/x86/Make 目录下运行DOS窗口；

2）运行 Make.bat <版本号> 进行打包；

3）打包成功后，新包在 Bulid/x86/bin 目录下面。
