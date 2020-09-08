using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Collections;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Onebox
{
    namespace ConfigureFileOperation
    {
        public class ConfigureFileRW
        {
            public const string CONF_CONFIGURE_SECTION = "CONFIGURE";
            public const string CONF_USER_DATA_PATH_KEY = "UserDataPath";
            public const string CONF_METADATA_PATH_KEY = "MetaDataRootPath";
            public const string CONF_MONITOR_PATH_KEY = "MonitorRootPath";
            public const string CONF_CACHE_PATH_KEY = "CachePath";
            public const string CONF_MAX_TRANS_TASK = "MaxTransTask";
            public const string CONF_MAX_TRANS_THREAD = "MaxTransThread";
            public const string CONF_MAX_ASYNC_TASK = "MaxAsyncTask";
            public const string CONF_MAX_ASYNC_THREAD = "MaxAsyncThread";
            public const string CONF_VIRTUAL_FOLDER_NAME_KEY = "VirtualFolderName";
            public const string CONF_SYNC_MODEL_KEY = "SyncModel";

            public const string CONF_USERINFO_SECTION = "USERINFO";
            public const string CONF_USERNAME_KEY = "UserName";
            public const string CONF_ACCOUNTGUID_KEY = "AccountGuid";
            public const string CONF_PASSWORD_KEY = "PassWord";
            public const string CONF_PCNAME_KEY = "DeviceName";
            public const string CONF_STORAGEDOMAIN_KEY = "StorageDomain";
            public const string CONF_BOOTSTARTRUN_KEY = "BootStartRun";
            public const string CONF_USER_LOGINTYPE_KEY = "LoginType";
            public const string CONF_USER_REMPASSWORD_KEY = "RemPassword";
            public const string CONF_USER_AUTOLOGINE_KEY = "AutoLogin";

            public const string CONF_NETWORK_SECTION = "NETWORK";
            public const string CONF_SERVER_URL_KEY = "StorageServerURL";
            public const string CONF_USESSL_KEY = "UseSSL";
            public const string CONF_MAX_SPEED_KEY = "MaxSpeedLimit";
            public const string CONF_MIN_SPEED_KEY = "MinSpeedLimit";
            public const string CONF_USE_PROXY_KEY = "UseProxy";
            public const string CONF_PROXY_SEVER_URL_KEY = "ProxyServerURL";
            public const string CONF_PROXY_SEVER_PORT_KEY = "ProxyServerPort";
            public const string CONF_USE_AUTHENTICATION_KEY = "UseProxyAuthen";
            public const string CONF_PROXY_USERNAME_KEY = "ProxyUsername";
            public const string CONF_PROXY_PASSWORD_KEY = "ProxyPassword";

            public const string CONF_VERSION_SECTION = "VERSION";
            public const string CONF_VERSION_KEY = "Version";
            public const string CONFIGUREFILENAME = "Config.ini";

            public string ConfigureFilePath;
            private Common common = new Common();
            //INIFilePath;

            public ConfigureFileRW()
            {
                ConfigureFilePath = common.GetInstallPath() + "\\" + CONFIGUREFILENAME;
            }

            public ConfigureFileRW(string path)
            {
                ConfigureFilePath = path;
            }

            [DllImport("kernel32",CharSet =CharSet.Unicode)]
            private static extern int WritePrivateProfileString(string section, string key, string value, string filepath);

            [DllImport("kernel32",CharSet = CharSet.Unicode)]
            private static extern int GetPrivateProfileString(string section, string key, string def, StringBuilder retVal, int size, string filepath);

            [DllImport("kernel32",CharSet = CharSet.Unicode)]
            private static extern int GetPrivateProfileString(string section, string key, string defVal, Byte[] retVal, int size, string filePath);

            /*****************************************************************************************
            Function Name: IniFileWriteValues
            Description  : 向INI文件写值
            Input        : Section: 段名称 ，Key:键名称，  Value: 键值
            Output       : 无
            Return       : void
            Created By   : lWX83437, 2013.12.17
            Modification :
            Others       :
            *******************************************************************************************/
            public void IniFileWriteValues(string Section, string Key, string Value)
            {
                WritePrivateProfileString(Section, Key, Value, this.ConfigureFilePath);
            }

            /*****************************************************************************************
            Function Name: IniFileReadValues
            Description  : 向INI文件写值
            Input        : Section: 段名称 ，Key:键名称
            Output       : 读取到的键值
            Return       : 
            Created By   : lWX83437, 2013.12.17
            Modification :
            Others       :
            *******************************************************************************************/
            public string IniFileReadValues(string Section, string Key)
            {
                StringBuilder temp = new StringBuilder(255);
                int i = GetPrivateProfileString(Section, Key, "", temp, 255, this.ConfigureFilePath);
                return temp.ToString();
            }

            /*****************************************************************************************
          Function Name: IniFileReadValues
          Description  : 向INI文件写值
          Input        : Section: 段名称 ，Key:键名称
          Output       : 读取到的键值
          Return       : 
          Created By   : lWX83437, 2013.12.17
          Modification :
          Others       :
          *******************************************************************************************/
            public string IniFileReadValues(string Section, string Key,string strdefault)
            {
                StringBuilder temp = new StringBuilder(255);
                int i = GetPrivateProfileString(Section, Key, strdefault, temp, 255, this.ConfigureFilePath);
                return temp.ToString();
            }

            /*****************************************************************************************
            Function Name: ClearAllSection
            Description  : 清除所有键值
            Input        : 
            Output       :
            Return       : 
            Created By   : lWX83437, 2013.12.17
            Modification :
            Others       :
            *******************************************************************************************/
            public void ClearAllSection()
            {
                IniFileWriteValues(null, null, null);
            }

            /*****************************************************************************************
            Function Name: ClearSection
            Description  : 清除某个段下面所有键值
            Input        : section：段名字
            Output       :
            Return       : 
            Created By   : lWX83437, 2013.12.17
            Modification :
            Others       :
            *******************************************************************************************/
            public void ClearSection(string section)
            {
                IniFileWriteValues(section, null, null);
            }

        }
    }
}
