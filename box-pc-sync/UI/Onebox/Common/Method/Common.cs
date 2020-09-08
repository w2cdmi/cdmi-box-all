using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Runtime.InteropServices;
using System.Drawing;
using Onebox.RegisterValue;

namespace Onebox
{
    public class Common
    {
        private const string spesName = "spes.exe";
        private static Object _LockObj = new Object();
/********************2016/9/26 lidonghai
                public bool IsHuaweiMachine()
                {
                    bool bRet = true;
                    try
                    {
                        RegValue varRegValue = new RegValue();
                        object objSpesPath = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_SPES_SUB_KEY, RegValue.REG_SPESPATH);
                        string strSpesPath = objSpesPath.ToString();
                        if (strSpesPath.Last() != '\\' || strSpesPath.Last() != '/')
                        {
                            strSpesPath += "\\" + spesName;
                            strSpesPath = strSpesPath.Replace('/', '\\');
                        }

                        if (!File.Exists(strSpesPath))
                        {
                            bRet = false;
                        }
                    }
                    catch
                    {
                        bRet = false;
                    }
                    return bRet;
                }
        ***********************/
        /// <summary>
        /// To determine whether the target is a folder or directory (including the disk directory)
        /// </summary>
        /// <param name="filepath">file name</param>
        /// <returns></returns>
        [DllImport("kernel32.dll", CharSet = CharSet.Unicode)]
        private static extern uint GetFileAttributes([MarshalAs(UnmanagedType.LPWStr)]string path);

        public bool IsDir(string filepath)
        {
            if (filepath.Length == 0)
            {
                return false;
            }

            uint attr = GetFileAttributes("\\\\?\\" + filepath);
            if (attr == unchecked((uint)-1))
            {
                return false;
            }
            return ((attr & 0x00000010) > 0);
            //FileInfo fi = new FileInfo("\\\\?\\"+filepath);
            //if ((fi.Attributes & FileAttributes.Directory) != 0)
            //    return true;
            //else
            //{
            //    return false;
            //}
        }

        public string GetSystemDisk()
        {
            string strPath = Environment.GetFolderPath(Environment.SpecialFolder.System);
            if ("" != strPath)
            {
                return (strPath[0].ToString() + strPath[1].ToString());
            }
            else
            {
                return "";
            }
        }

        public string GetWebURL()
        {
            string retwebURL = "";
            try
            {
                ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW();
                string webURL = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_NETWORK_SECTION,
                    ConfigureFileOperation.ConfigureFileRW.CONF_SERVER_URL_KEY);

                if (webURL != "" && webURL.EndsWith(MainWindow.DEFAULTAPIVWESION))
                {
                    int end = webURL.Length - MainWindow.DEFAULTAPIVWESION.Length;
                    retwebURL = webURL.Substring(0, end);
                }
            }
            catch { }
            return retwebURL;
        }

        public string GetInstallPath()
        {
            string sRet = "";
            try
            {
                RegValue varRegValue = new RegValue();
                object ObjRegValue = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_APP_PATH);
                if (null != ObjRegValue)
                {
                    sRet = ObjRegValue.ToString();
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                sRet = "";
            }

            return sRet;
        }

        public bool DeleteUserData()
        {
            bool bRet = true;
            try
            {
                string strUserData = GetInstallPath() + "\\" + MainWindow.DEFAULTUSERDATA;

                if (Directory.Exists(strUserData))
                {
                    Directory.Delete(strUserData, true);
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                bRet = false;
            }

            return bRet;
        }

        public bool IsSlientInstall()
        {
            bool bRet = false;

            try
            {
                RegValue varRegValue = new RegValue();
                object ObjRegValue = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_APP_PATH);
                object ObjInstallType = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_INSTALL_TYPE);
                string BindValue = (string)Microsoft.Win32.Registry.GetValue(RegValue.REG_BINDUSER_KEYNAME, "", "");
                if (null != ObjInstallType && 1 == (int)ObjInstallType && (null == BindValue || "" == BindValue))
                {
                    bRet = true;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                bRet = false;
            }

            return bRet;
        }

        public string GetPiontShow(int iNum)
        {
            string strPoint = "";
            if (iNum == 1)
            {
                strPoint = ".";
            }
            else if (iNum == 2)
            {
                strPoint = "..";
            }
            else if (iNum == 3)
            {
                strPoint = "...";
            }
            else if (iNum == 4)
            {
                strPoint = "....";
            }
            else if (iNum == 5)
            {
                strPoint = ".....";
            }
            else if (iNum == 6)
            {
                strPoint = "......";
            }

            return strPoint;
        }

        /// <summary>
        /// Used for converting space size
        /// </summary>
        /// <param name="strIpOrDName">The input parameters, indicating the size of space need to be converted, unit (bit)</param>
        /// <returns>the string which size of the space converted</returns>
        public void ConvertSpaceSize(string strlen, ref string strOutSize, ref string strOutUnit)
        {
            Int64 size_KB = (Int64)1024;
            Int64 size_MB = (Int64)1024 * size_KB;
            Int64 size_GB = (Int64)1024 * size_MB;

            if (strlen == "")
            {
                strOutSize = "0";
                strOutUnit = "B";
                return;
            }

            long len = long.Parse(strlen);
            if (size_GB <= len)
            {
                decimal tmp = Math.Round((decimal)len / size_GB, 2);
                strOutSize = System.Convert.ToString(tmp);
                strOutUnit = "GB";
            }
            else if (size_MB <= len)
            {
                decimal tmp = Math.Round((decimal)len / size_MB, 2);
                strOutSize = System.Convert.ToString(tmp);
                strOutUnit = "MB";
            }
            else if (size_KB <= len)
            {
                decimal tmp = Math.Round((decimal)len / size_KB, 2);
                strOutSize = System.Convert.ToString(tmp);
                strOutUnit = "KB";
            }
            else
            {
                strOutSize = System.Convert.ToString(len);
                strOutUnit = "B";
            }
        }

        public void ConvertTime(long iMsec, ref long iOutHour, ref long iOutMin, ref long iOutSec)
        {
            if (0 < iMsec && iMsec <= 1000)
            {
                iOutHour = 0;
                iOutMin = 0;
                iOutSec = 1;
            }
            else if (iMsec > 1000)
            {
                iOutHour = iMsec / (1000 * 60 * 60);
                iOutMin = (iMsec - iOutHour * (1000 * 60 * 60)) / (1000 * 60);
                iOutSec = (iMsec - iOutHour * (1000 * 60 * 60) - iOutMin * (1000 * 60)) / 1000;
            }
            else
            {
                iOutHour = 0;
                iOutMin = 0;
                iOutSec = 0;
            }
        }

        /// <summary>
        /// Gets the remaining space of the logical disk   
        /// </summary>
        /// <param name="strDirName">logical disk name，e.g. C:</param>
        /// <returns>0: The authentication succeeds   Non zero: authentication failure</returns>
        public long GetFreeSpace(string strDirName)
        {
            string strFreeSpace = "";

            try
            {
                System.Management.SelectQuery selectQuery = new System.Management.SelectQuery("select * from win32_logicaldisk");
                System.Management.ManagementObjectSearcher searcher = new System.Management.ManagementObjectSearcher(selectQuery);
                foreach (System.Management.ManagementObject disk in searcher.Get())
                {
                    if (strDirName.ToUpper() == disk["Name"].ToString())
                    {
                        strFreeSpace = disk["FreeSpace"].ToString();
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return long.Parse(strFreeSpace);
        }

        /// <summary>  
        /// 获取文件图标
        /// </summary>   
        /// <param name="p_Path">文件全路径</param>
        /// <returns>图标</returns> 
        [DllImport("kernel32.dll", CharSet = CharSet.Unicode)]
        private static extern uint GetShortPathName(
            [MarshalAs(UnmanagedType.LPWStr)] 
            string path,
            [MarshalAs(UnmanagedType.LPWStr)] 
            StringBuilder shortPath,
            int shortPathLength);

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        private struct SHFILEINFO
        {
            public IntPtr hIcon;
            public int iIcon;
            public uint dwAttributes;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 260)]
            public string szDisplayName;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 80)]
            public string szTypeName;
        }

        [DllImport("Shell32.dll", CharSet = CharSet.Unicode)]
        private static extern uint SHGetFileInfo(
            [MarshalAs(UnmanagedType.LPWStr)] 
            string pszPath,
            uint dwFileAttributes,
            ref SHFILEINFO psfi,
            uint cbFileInfo,
            uint uFlags);

        public Icon GetFileIcon(string path)
        {
            int MAX_PATH = 260;
            StringBuilder shortPath = new StringBuilder(MAX_PATH);
            if (0 == GetShortPathName("\\\\?\\" + path, shortPath, MAX_PATH))
            {
                return null;
            }
            SHFILEINFO fileInfo = new SHFILEINFO();
            if (0 == SHGetFileInfo(shortPath.ToString().Substring(4), 0, ref fileInfo, (uint)Marshal.SizeOf(fileInfo), 0x000000100))
            {
                return null;
            }
            return System.Drawing.Icon.FromHandle(fileInfo.hIcon);

            //Icon _Icon = System.Drawing.Icon.ExtractAssociatedIcon(tempPath);
            //return _Icon;
        }

        /// <summary>
        /// 获取文件夹图标
        /// </summary>     
        /// <returns>图标</returns> 
        public Icon GetIcon(string strFilePath)
        {
            string path = System.Environment.CurrentDirectory;
            path += @strFilePath;
            Bitmap mybitmap = new Bitmap(path);
            IntPtr hIcon = mybitmap.GetHicon();
            Icon _Icon = System.Drawing.Icon.FromHandle(hIcon);
            return _Icon;
        }

        /// <summary>
        /// 字符串编码转换
        /// </summary>
        /// <param name="srcEncoding">原编码</param>
        /// <param name="dstEncoding">目标编码</param>
        /// <param name="srcBytes">原字符串</param>
        /// <returns>字符串</returns>
        public string TransferEncoding(Encoding srcEncoding, Encoding dstEncoding, string srcStr)
        {
            byte[] srcBytes = srcEncoding.GetBytes(srcStr);
            byte[] bytes = Encoding.Convert(srcEncoding, dstEncoding, srcBytes);
            return dstEncoding.GetString(bytes);
        }

        /// <summary>
        /// 替换特殊字符
        /// </summary>
        /// <param name="input">字符串</param>
        /// <returns></returns>
        public string ReplaceSpecialChars(string input)
        {
            if (input == null) return "";
            input = input.Replace(" ", "_x0020_")
            .Replace("%", "_x0025_")
            .Replace("#", "_x0023_")
            .Replace("&", "_x0026_")
            .Replace("/", "_x002F_");
            return input;
        }

        public string CutFixedLengthString(string input, int length)
        {
            lock (_LockObj)
            {
                string retstring = input;
                try
                {
                    for (int i = 0; i < input.Length; i++)
                    {
                        if (Encoding.Default.GetBytes(retstring).Length <= length)
                        {
                            break;
                        }

                        retstring = retstring.Substring(0, retstring.Length - 1);
                    }


                    if (retstring.Length < input.Length)
                    {
                        retstring = retstring.Substring(0, retstring.Length - 6) + "......";
                    }
                }
                catch (Exception ex)
                {
                    App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                }

                return retstring;
            }
        }
    }
}
