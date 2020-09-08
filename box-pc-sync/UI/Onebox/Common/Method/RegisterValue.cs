using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Win32;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Windows;
using System.Management;

namespace Onebox.RegisterValue
{
    public class RegValue
    {
        public const string REG_ROOT_HKLM = "HKEY_LOCAL_MACHINE";
        public const string REG_ROOT_HKCR = "HKEY_CLASSES_ROOT";
        public const string REG_ROOT_HKCU = "HKEY_CURRENT_USER";
        public const string REG_ROOT_HKUR = "HKEY_USER";
        public const string REG_ROOT_HKCC = "HKEY_CURRENT_CONFIG";
        public const string REG_BINDUSER_SUB_KEY = "CLSID\\{1D84B808-6135-47E7-9D12-C391C99AD8A0}";
        public static string REG_PORT_SUB_KEY = "";
        public const string REG_BINDUSER_KEYNAME = REG_ROOT_HKCR + "\\" + REG_BINDUSER_SUB_KEY;
        public static string REG_APPINFO_SUB_KEY = "";
        public const string REG_AUTORUN_SUB_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        public const string REG_64BIT_AUTORUN_SUB_KEY = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Run";
        public static string REG_SPES_SUB_KEY = "";
        public const string REG_APP_PATH = "AppPath";
        public const string REG_INSTALL_TYPE = "InstallType";
        public const string REG_LOGIN_STATE = "LoginState";
        public const string REG_MAIN_VERSION = "MainVersion";
        public const string REG_UPGRADE_SUCCESS = "IsUpgradeSuccess";
        public const string REG_HACCAGENTPATH = "Path";
        public const string REG_SPESPATH = "InstallPath";
        public const string REG_DONET_VERSION = "DoNetVersion";
        public const string REG_PORT = "Port";
        public const string REG_PORT_DEFAULTVALUE = "0";

        public RegValue()
        {
            SetAppSubKey();
        }

        /// <summary>
        /// Used to write the registry
        /// </summary>
        /// <param name="strKey">Registry Root types</param>
        /// <param name="strKey">The registry key values</param>
        /// <param name="strName">The registry key name</param>
        /// <param name="strKey">The registry values</param>
        /// <returns></returns>
        public  bool WriteReg(string RegRoot, string strKey, string strName, object Value)
        {
            try
            {
                if (RegValue.REG_ROOT_HKLM == RegRoot)
                {
                    RegistryKey setKey = Registry.LocalMachine.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName +" VALUE:" + Value);
                        return false;
                    }
                    setKey.SetValue(strName, Value);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCR == RegRoot)
                {
                    RegistryKey setKey = Registry.ClassesRoot.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName + " VALUE:" + Value);
                        return false;
                    }
                    setKey.SetValue(strName, Value);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCC == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentConfig.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName + " VALUE:" + Value);
                        return false;
                    }
                    setKey.SetValue(strName, Value);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCU == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentUser.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName + " VALUE:" + Value);
                        return false;
                    }
                    setKey.SetValue(strName, Value);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKUR == RegRoot)
                {
                    RegistryKey setKey = Registry.Users.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName + " VALUE:" + Value);
                        return false;
                    }
                    setKey.SetValue(strName, Value);
                    setKey.Close();
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }

            return true;
        }

        ///<summary>
        /// used to read the registry
        /// </summary>
        /// <param name="strKey">Registry Root types</param>
        /// <param name="strKey">The registry key values</param>
        /// <param name="strName">The registry key name</param>
        /// <param name="strKey">The registry values</param>
        /// <returns></returns>
        public  object ReadReg(string RegRoot, string strKey, string strName)
        {
            object Value = null;

            try
            {
                if (RegValue.REG_ROOT_HKLM == RegRoot)
                {
                    RegistryKey setKey = Registry.LocalMachine.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot +" KEY:"+ strKey + " NAME:" + strName);
                        return null;
                    }
                    Value = setKey.GetValue(strName, null);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCR == RegRoot)
                {
                    RegistryKey setKey = Registry.ClassesRoot.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName);
                        return null;
                    }
                    Value = setKey.GetValue(strName, null);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCC == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentConfig.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName);
                        return null;
                    }
                    Value = setKey.GetValue(strName, null);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCU == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentUser.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName);
                        return null;
                    }
                    Value = setKey.GetValue(strName, null);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKUR == RegRoot)
                {
                    RegistryKey setKey = Registry.Users.OpenSubKey(strKey, true);
                    if (null == setKey)
                    {
                        App.Log.Error("ROOT:" + RegRoot + " KEY:" + strKey + " NAME:" + strName);
                        return null;
                    }
                    Value = setKey.GetValue(strName, null);
                    setKey.Close();
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return null;
            }

            return Value;
        }

        ///<summary>
        /// To delete the specified registry value
        /// </summary>
        /// <param name="strKey">Registry Root types</param>
        /// <param name="strKey">The registry key values</param>
        /// <param name="strName">The registry key name</param>
        /// <param name="strKey">The registry values</param>
        /// <returns></returns>
        public  void DeleteRegValue(string RegRoot, string strKey, string strName)
        {
            try
            {
                if (RegValue.REG_ROOT_HKLM == RegRoot)
                {
                    RegistryKey setKey = Registry.LocalMachine.OpenSubKey(strKey, true);
                    setKey.DeleteValue(strName,false);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCR == RegRoot)
                {
                    RegistryKey setKey = Registry.ClassesRoot.OpenSubKey(strKey, true);
                    setKey.DeleteValue(strName, false);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCC == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentConfig.OpenSubKey(strKey, true);
                    setKey.DeleteValue(strName, false);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKCU == RegRoot)
                {
                    RegistryKey setKey = Registry.CurrentUser.OpenSubKey(strKey, true);
                    setKey.DeleteValue(strName, false);
                    setKey.Close();
                }

                else if (RegValue.REG_ROOT_HKUR == RegRoot)
                {
                    RegistryKey setKey = Registry.Users.OpenSubKey(strKey, true);
                    setKey.DeleteValue(strName, false);
                    setKey.Close();
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public bool Is64Bit()
        {
            if ("32" == Distinguish64or32System())
            {
                return false;
            }

            return true;
        }

        private  string Distinguish64or32System()
        { 
            try 
            { 
                string addressWidth = String.Empty; 
                ConnectionOptions mConnOption = new ConnectionOptions();
                ManagementScope mMs = new ManagementScope("\\\\localhost", mConnOption);
                ObjectQuery mQuery = new ObjectQuery("select AddressWidth from Win32_Processor"); 
                ManagementObjectSearcher mSearcher = new ManagementObjectSearcher(mMs, mQuery); 
                ManagementObjectCollection mObjectCollection = mSearcher.Get(); 
                foreach (ManagementObject mObject in mObjectCollection) 
                {
                    addressWidth = mObject["AddressWidth"].ToString();
                }

                return addressWidth;
            } 
            catch (Exception ex) 
            {
                Console.WriteLine(ex.ToString()); return String.Empty; 
            } 
        }

        private  void SetAppSubKey()
        {
            if (!Is64Bit())
            {
                REG_APPINFO_SUB_KEY = "SOFTWARE\\Chinasoft\\Onebox\\Setting";
                REG_PORT_SUB_KEY = "CLSID\\{2DB5975A-C37B-4BB3-88CE-1F91E82A09D5}";
                //REG_SPES_SUB_KEY = "SOFTWARE\\Huawei\\SPES5.0\\Composites\\spes";
            }
            else
            {
                REG_APPINFO_SUB_KEY = "SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting";
                REG_PORT_SUB_KEY = "Wow6432Node\\CLSID\\{2DB5975A-C37B-4BB3-88CE-1F91E82A09D5}";
                //REG_SPES_SUB_KEY = "SOFTWARE\\Wow6432Node\\Huawei\\SPES5.0\\Composites\\spes";
            }
        }
    }
}

