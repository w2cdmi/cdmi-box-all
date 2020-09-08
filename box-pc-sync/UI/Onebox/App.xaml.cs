using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading;
using System.Windows;
using System.Globalization;
using  System.Collections.ObjectModel;
using log4net;
using Microsoft.Win32;
using System.IO;
using Onebox.RegisterValue;

namespace Onebox
{
    /// <summary>
    /// App.xaml 的交互逻辑
    /// </summary>
    public partial class App : Application
    {
        private RunNoticeWindow runNoticeWindow = new RunNoticeWindow();
        public static readonly ILog Log = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
        private RegValue varRegValue = new RegValue();

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
           // Log.Info("<<<======================Startup====================>>>");
            log4net.Config.XmlConfigurator.Configure();
            LoadLanguage();
            System.Security.Principal.WindowsIdentity wid = System.Security.Principal.WindowsIdentity.GetCurrent();
            System.Security.Principal.WindowsPrincipal p = new System.Security.Principal.WindowsPrincipal(wid);

            object objDoNetVersion = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_DONET_VERSION);
            if (null !=objDoNetVersion)
            {
                Log.Info("The Onebox's .Net Framework version is " + objDoNetVersion.ToString());
            }

            if (!CheckInstallFile())
             {
                 runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["StartNoticeTitle"], NoticeType.Error, (string)Application.Current.Resources["InstallFileIncomplete"], "");
                 Environment.Exit(0);
             }
             
            bool isAdmin = p.IsInRole(System.Security.Principal.WindowsBuiltInRole.Administrator);
            if (!isAdmin)
            {
                runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["OneboxSysInfo"], NoticeType.Error, (string)Application.Current.Resources["NotAdmini"], "");
                Environment.Exit(0);
            }

           object ObjRegValue = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_APP_PATH);
           if (null != ObjRegValue && !ObjRegValue.ToString().Equals(""))
            {
                System.Environment.CurrentDirectory = ObjRegValue.ToString();
            }

            MainWindow mainWindow = new MainWindow();
            if (1== e.Args.Count() && !e.Args[0].Equals("start"))
            {
                mainWindow.IsOpenMonitorDir = false;
            }
            mainWindow.Show();
            mainWindow.Activate();
        }

        protected override void OnExit(ExitEventArgs e)
        {
            Log.Info("<<<======================End====================>>>");
            base.OnExit(e);
        }

        private void LoadLanguage()
        {
            CultureInfo currentCultureInfo = CultureInfo.CurrentCulture;
            ResourceDictionary langRd = null;

            try
            {
                string path = "/Onebox;component/Language/resources_" + currentCultureInfo.Name + ".xaml";
                if (!currentCultureInfo.Name.Equals("zh-CN"))
                {
                    path = "/Onebox;component/Language/resources_" + "en-US" + ".xaml";
                }
                Uri tmp = new Uri(path, UriKind.Relative);
                langRd = Application.LoadComponent(tmp) as ResourceDictionary;

                if (langRd != null)
                {
                    Collection<ResourceDictionary> res = this.Resources.MergedDictionaries;
                    this.Resources.MergedDictionaries.Add(langRd);
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Application_DispatcherUnhandledException(object sender, System.Windows.Threading.DispatcherUnhandledExceptionEventArgs e)
        {
            Log.Error(e.Exception.TargetSite + "  " + e.Exception.Source + "  " + e.Exception.InnerException + " " + e.Exception.Message);
        }

        private bool CheckInstallFile()
        {
            bool bRet = true;
            ObservableCollection<string> ObsInstallFileList = new ObservableCollection<string>();
            ObservableCollection<string> ObsInstallFileTempList = new ObservableCollection<string>();

            ObsInstallFileTempList.Clear();
            ObsInstallFileTempList.Add("\\Language\\Chinese.ini");
            ObsInstallFileTempList.Add("\\Language\\English.ini");
            ObsInstallFileTempList.Add("\\Language\\Error_Chinese.xml");
            ObsInstallFileTempList.Add("\\Language\\Error_English.xml");
            ObsInstallFileTempList.Add("\\Res\\logo.ico");
            ObsInstallFileTempList.Add("\\Res\\outchain.bmp");
            ObsInstallFileTempList.Add("\\Res\\share.bmp");
            ObsInstallFileTempList.Add("\\Res\\SyncFailed.ico");
            ObsInstallFileTempList.Add("\\Res\\SyncIng.ico");
            ObsInstallFileTempList.Add("\\Res\\SyncOk.ico");
            ObsInstallFileTempList.Add("\\Res\\SyncNoAction.ico");
            ObsInstallFileTempList.Add("\\Res\\ShareLink.bmp");
            ObsInstallFileTempList.Add("\\Res\\cloudlogo.bmp");
            //ObsInstallFileTempList.Add("\\ShellExtent.dll");
            ObsInstallFileTempList.Add("\\OneboxSDK.dll");
            ObsInstallFileTempList.Add("\\Config.ini");
            ObsInstallFileTempList.Add("\\libcurl.dll");
            ObsInstallFileTempList.Add("\\libeay32.dll");
            ObsInstallFileTempList.Add("\\log4cpp.conf");
            ObsInstallFileTempList.Add("\\Microsoft.WindowsAPICodePack.dll");
            ObsInstallFileTempList.Add("\\Microsoft.WindowsAPICodePack.Shell.dll");
            //ObsInstallFileTempList.Add("\\msvcp110.dll");
            //ObsInstallFileTempList.Add("\\msvcr110.dll");
            ObsInstallFileTempList.Add("\\ssleay32.dll");
            ObsInstallFileTempList.Add("\\Thrift.dll");
            ObsInstallFileTempList.Add("\\SyncRules.xml");
            ObsInstallFileTempList.Add("\\zlib1.dll");
            ObsInstallFileTempList.Add("\\OneboxShExtCmd.exe");
            //ObsInstallFileTempList.Add("\\NscaMiniLib.dll");
            ObsInstallFileTempList.Add("\\log4net.dll");
            ObsInstallFileTempList.Add("\\log4net.config");
            ObsInstallFileTempList.Add("\\ICSharpCode.SharpZipLib.dll");
            ObsInstallFileTempList.Add("\\OneboxStart.exe");
            ObsInstallFileTempList.Add("\\Onebox.exe");
            ObsInstallFileTempList.Add("\\OneboxSyncHelper.dll");
            ObsInstallFileTempList.Add("\\OneboxSyncService.exe");
            ObsInstallFileTempList.Add("\\vhCalendar.dll");
            ObsInstallFileTempList.Add("\\Label32.dll");

            object objAppPath = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_APP_PATH);
            if (null != objAppPath && "" != objAppPath.ToString())
            {
                string strAppPath = objAppPath.ToString();
                foreach (string strTempFilePath in ObsInstallFileTempList)
                {
                    string strFilePath = strAppPath + strTempFilePath;
                    strFilePath = strFilePath.Replace("/", "\\");
                    if (!File.Exists(strFilePath))
                    {
                        Log.Error("missing file: " + strFilePath);
                        bRet = false;
                        break;
                    }
                }
            }

            return bRet;
        }
    }
}
