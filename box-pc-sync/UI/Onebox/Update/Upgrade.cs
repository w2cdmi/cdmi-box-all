using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Windows;

using Onebox.RegisterValue;
using Onebox.ZipHelp;
using Onebox.ProcessManager;

namespace Onebox.Update
{
    class Upgrade
    {
        //string strUpgradeConfig = "[VERSION]\r\nVersion=1.2.3.2317\r\nServerURL=127.0.0.1\r\nForceUpgradeFlage=0\r\n";
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private string strPackageDesPath = Path.GetTempPath() + "OneboxUpgrade";
        private RegValue varRegValue = new RegValue();
        private RunUpgradeNoticeWindow runUpgradeNoticeWindow = new RunUpgradeNoticeWindow();
        private const string strUpgradeFileName = "updateVersion.ini";

        public void Run()
        {
            SaveUpgradeINIFile();
            RunUpgradeNotice();
        }

        private void SaveUpgradeINIFile()
        {
            try
            {
                ThriftClient.Update_Info UpdateInfo = thriftClient.getUpdateInfo();
                if (null != UpdateInfo)
                {
                    string strUpgradeConPath = Path.GetTempPath() + strUpgradeFileName;
                    strUpgradeConPath = strUpgradeConPath.Replace("/", "\\");
                    StreamWriter sw = new StreamWriter(strUpgradeConPath, false, Encoding.Unicode);
                    sw.Write(UpdateInfo.VersionInfo);
                    sw.Close();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void RunUpgradeNotice()
        {
            runUpgradeNoticeWindow.RunUpgradeNotice();
        }

        public bool  IsUpgradeSucess()
        {
            bool bRet = false;
            object ObjUpdageSuccess = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM,RegValue.REG_APPINFO_SUB_KEY,RegValue.REG_UPGRADE_SUCCESS);

            if (null != ObjUpdageSuccess)
            {
                if (1 == (int)ObjUpdageSuccess)
                {
                    varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_UPGRADE_SUCCESS);
                    bRet = true;
                }
            }

            return bRet;
        }
    }
}
