using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.ComponentModel;
using System.Collections.ObjectModel;
using System.IO;
using System.Diagnostics;
using System.Windows.Threading;
using System.Windows.Input;

using Onebox.RegisterValue;
using Onebox.ZipHelp;
using Onebox.ProcessManager;

namespace Onebox.Update
{
    /// <summary>
    /// UpgradeNotice.xaml 的交互逻辑
    /// </summary>

    struct UPGRADEMSGCONF
    {
        public const string CONF_CONFIGURE_SECTION = "VERSION";
        public const string CONF_UPGRADE_KEY = "Version";
        public const string CONF_FORCEUPGRADE_KEY = "UpdateType";
        public const string CONF_UPDATEMSG_SECTION = "UPDATEMSG";
        public const string CONF_MSG_EN_KEY = "Msg_en";
        public const string CONF_MSG_CN_KEY = "Msg_cn";
    }

    public partial class UpgradeNotice : Window
    {
        private ObservableCollection<UpgradeMsg> UpgradeMsgCollection = new ObservableCollection<UpgradeMsg>();
        private string strPackageDesPath = Path.GetTempPath() + "OneboxUpgrade";
        private string strZipFileName = "OneboxSetup.zip";
        private const string strUpgradeFileName = "updateVersion.ini";
        private string strZipFilePath = "";
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private Common common = new Common();
        private bool IsForceUpgrade = false;

        public UpgradeNotice()
        {
            InitializeComponent();
            this.LB_UpgradeMsg.ItemsSource = UpgradeMsgCollection;
            GetUprageMsg();
        }

        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            this.DragMove();
            base.OnMouseLeftButtonDown(e);
        }

        private void BN_Close_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void BN_Notice_Upgrade_Click(object sender, RoutedEventArgs e)
        {
            this.Hide();
            DownLoadUpgradePackage();
            RunUpgradeProc();
        }

        private bool DownLoadUpgradePackage()
        {
            bool bRet = false;
            try
            {
                ThriftClient.Update_Info UpdateInfo = thriftClient.getUpdateInfo();
                if (Directory.Exists(strPackageDesPath))
                {
                    Directory.Delete(strPackageDesPath, true);
                }

                if (File.Exists(strPackageDesPath))
                {
                    File.Delete(strPackageDesPath);
                }

                DirectoryInfo DirInfo = Directory.CreateDirectory(strPackageDesPath);
                if (null != DirInfo)
                {
                    strZipFilePath = strPackageDesPath + "\\" + strZipFileName;
                    strZipFilePath = common.TransferEncoding(Encoding.Unicode, Encoding.UTF8, strZipFilePath);
                    if (0 == thriftClient.downloadClient(UpdateInfo.DownloadUrl, strZipFilePath))
                    {
                        bRet = true;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return bRet;
        }

        private void RunUpgradeProc()
        {
            try
            {
                if (File.Exists(strZipFilePath))
                {
                    if (ZipHelper.UnZip(strZipFilePath, strPackageDesPath))
                    {
                        foreach (string fileStr in Directory.GetFiles(strPackageDesPath))
                        {
                            int indexf = fileStr.LastIndexOf(".");
                            string strSubf = fileStr.Substring(indexf, 4);
                            if (strSubf == ".exe")
                            {
                                string UpgradeExePath = fileStr;
                                ProcessStartInfo StartInfo;
                                StartInfo = new System.Diagnostics.ProcessStartInfo();
                                StartInfo.WorkingDirectory = strPackageDesPath;
                                StartInfo.FileName = UpgradeExePath;
                                StartInfo.UseShellExecute = false;

                                if (IsForceUpgrade)
                                {
                                    StartInfo.Arguments = "/F";
                                }
                                else
                                {
                                    StartInfo.Arguments = "/U";
                                }

                                Process Pcs = System.Diagnostics.Process.Start(StartInfo);
                                MainWindow mainWindow = (MainWindow)App.Current.MainWindow;
                                mainWindow.Exit();
                                break;
                            }
                        }
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void GetUprageMsg()
        {
            try
            {

                string strUpgradeConfPath = Path.GetTempPath() + strUpgradeFileName;
                ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW(strUpgradeConfPath);
                if (File.Exists(strUpgradeConfPath))
                {
                    string strForceUpgrede = varIniFileRW.IniFileReadValues(UPGRADEMSGCONF.CONF_CONFIGURE_SECTION, UPGRADEMSGCONF.CONF_FORCEUPGRADE_KEY);
                    if ("1" == strForceUpgrede)
                    {
                        IsForceUpgrade = true;
                    }
                }

                System.Globalization.CultureInfo currentCultureInfo = System.Globalization.CultureInfo.CurrentCulture;
                if (currentCultureInfo.Name.Equals("zh-CN"))
                {
                    string strUpgradeMsg = varIniFileRW.IniFileReadValues(UPGRADEMSGCONF.CONF_UPDATEMSG_SECTION, UPGRADEMSGCONF.CONF_MSG_CN_KEY);
                    while (strUpgradeMsg.Length > 0)
                    {
                        UpgradeMsg upgradeMsg = new UpgradeMsg(strUpgradeMsg.Substring(0, strUpgradeMsg.IndexOf('|')));
                        strUpgradeMsg = strUpgradeMsg.Substring(strUpgradeMsg.IndexOf('|') + 1, strUpgradeMsg.Length - strUpgradeMsg.IndexOf('|') - 1);
                        UpgradeMsgCollection.Add(upgradeMsg);
                    }
                }
                else
                {
                    string strUpgradeMsg = varIniFileRW.IniFileReadValues(UPGRADEMSGCONF.CONF_UPDATEMSG_SECTION, UPGRADEMSGCONF.CONF_MSG_EN_KEY);
                    while (strUpgradeMsg.Length > 0)
                    {
                        UpgradeMsg upgradeMsg = new UpgradeMsg(strUpgradeMsg.Substring(0, strUpgradeMsg.IndexOf('|')));
                        strUpgradeMsg = strUpgradeMsg.Substring(strUpgradeMsg.IndexOf('|') + 1, strUpgradeMsg.Length - strUpgradeMsg.IndexOf('|') - 1);
                        UpgradeMsgCollection.Add(upgradeMsg);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }


    public partial class UpgradeMsg : INotifyPropertyChanged
    {
        public UpgradeMsg(string strUpgradeNotice)
        {
            UpgredeNotice = strUpgradeNotice;
        }

        private string upgradenotice;
        public string UpgredeNotice
        {
            get { return this.upgradenotice; }
            set
            {
                this.upgradenotice = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UpgredeNotice"));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }
    }

    public class RunUpgradeNoticeWindow
    {
        private const string strUpgradeFileName = "updateVersion.ini";
        private UpgradeNotice upgradeNotice = null;
        private string NewVersion = "";

        private bool IsUpgrade()
        {
            bool bRet = false;
            string strUpgradeConfPath = Path.GetTempPath() + strUpgradeFileName;
            strUpgradeConfPath = strUpgradeConfPath.Replace("/", "\\");
            RegValue varRegValue = new RegValue();

            if (File.Exists(strUpgradeConfPath))
            {
                ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW(strUpgradeConfPath);
                string strNewVersion = varIniFileRW.IniFileReadValues(UPGRADEMSGCONF.CONF_CONFIGURE_SECTION, UPGRADEMSGCONF.CONF_UPGRADE_KEY);
                object ObjOldVersion = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_MAIN_VERSION);
                if (null != ObjOldVersion && "" != strNewVersion)
                {
                    int iRet = CompareVersion(ObjOldVersion.ToString(), strNewVersion);

                    if (2 == iRet)
                    {
                        NewVersion = strNewVersion;
                        bRet = true;
                    }
                }
            }

            return bRet;
        }

        /// <summary>
        /// 比较版本大小，0：代表两个版本相等，1：代表老版本号比新版本号大，2：代表老版本号比新版本号小
        /// </summary>
        /// <param name="strOldVer">老版本号</param>
        /// <param name="strNewVer">新版本号</param>
        /// <returns>0：代表两个版本相等，1：代表老版本号比新版本号大，2：代表老版本号比新版本号小</returns>
        private static int CompareVersion(string strOldVer, string strNewVer)
        {
            int iRet = 0;

            if ("" == strOldVer && "" == strNewVer)
            {
                return -1;
            }

            strOldVer = strOldVer.Replace(".", "");
            strNewVer = strNewVer.Replace(".", "");

            int iOldVer = int.Parse(strOldVer);
            int iNewVer = int.Parse(strNewVer);

            if (iOldVer == iNewVer)
            {
                iRet = 0;
            }
            else if (iOldVer > iNewVer)
            {
                iRet = 1;
            }
            else
            {
                iRet = 2;
            }

            return iRet;
        }

        private bool IsForceUpgrede()
        {
            bool bRet = false;
            string strUpgradeConfPath = Path.GetTempPath() + strUpgradeFileName;
            if (File.Exists(strUpgradeConfPath))
            {
                ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW(strUpgradeConfPath);
                string strForceUpgrede = varIniFileRW.IniFileReadValues(UPGRADEMSGCONF.CONF_CONFIGURE_SECTION, UPGRADEMSGCONF.CONF_FORCEUPGRADE_KEY);
                if ("1" == strForceUpgrede)
                {
                    bRet = true;
                }
            }

            return bRet;
        }

        public void RunUpgradeNotice()
        {
            if (IsUpgrade())
            {
                if (IsForceUpgrede())
                {
                    upgradeNotice = new UpgradeNotice();
                    MainWindow mainWindow = (MainWindow)App.Current.MainWindow;
                    mainWindow.ChildWindowCollection.Add(upgradeNotice);
                    upgradeNotice.appVersion.Text = NewVersion;
                    upgradeNotice.BN_Notice_Cancel.Visibility = Visibility.Collapsed;
                    upgradeNotice.CloseButton1.IsEnabled = false;
                    upgradeNotice.ShowDialog();
                }
                else
                {
                    upgradeNotice = new UpgradeNotice();
                    MainWindow mainWindow = (MainWindow)App.Current.MainWindow;
                    mainWindow.ChildWindowCollection.Add(upgradeNotice);
                    upgradeNotice.appVersion.Text = NewVersion;
                    upgradeNotice.Show();
                }
            }
        }
    }

}
