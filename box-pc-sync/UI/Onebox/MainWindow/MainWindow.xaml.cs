using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Collections.ObjectModel;
using System.Runtime.InteropServices;
using Microsoft.Win32;
using System.ComponentModel;
using System.IO;
using System.Net.NetworkInformation;
using Microsoft.Win32.SafeHandles;
using System.Drawing;
using System.Diagnostics;
using System.Security.Cryptography;

using Onebox.ProcessManager;
using Onebox.MsgDefine;
using Onebox.NSTrayIconStatus;
using Onebox.RegisterValue;
using Onebox.Update;
using Onebox.SyncDirTreeView;
using Onebox.XmlDocumentOperation;
using Onebox.TrayIcon;
namespace Onebox
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    #region Message define
    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
    public struct Msg
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg1;
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg2;
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg3;
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg4;
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg5;
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32768)]
        public string msg6;
    }

    [StructLayout(LayoutKind.Sequential)]
    public struct CopyDataStruct
    {
        public IntPtr dwData;
        public int cbData;
        public IntPtr lpData;
    }

    #endregion

    public partial class MainWindow : Window
    {
        #region define mark bit
        //是否显示使用向导和目录选择窗口
        public Boolean IsShowWorCWindow = false;
        //是否第一次使用
        public Boolean isFirstUse = false;
        //是否信息确认，便于控制状态栏双击是否弹出确认框
        public Boolean IsLogin = false;
        public Boolean IsAuthSuccess = false;
        public Boolean IsSkipUseWizard = false;
        //是否开始show登录窗口
        public Boolean IsShowLoginWindow = false;
        public Boolean Is500MNotice = true;
        public Boolean Is100MNotice = true;
        public Boolean IsStopSync = false;
        public Boolean IsOpenMonitorDir = true;
        public Boolean IsShowMainWindow = false;
        public Boolean IsNoticeCntLimit = true;
        public Boolean IsCheckUpgrade = true;
        public Boolean IsExit = false;
        public Boolean changeFlag = true;
        public Boolean IsScanning = false;
        #endregion

        #region define  observablecollection
        private ObservableCollection<MyTask> tasks = new ObservableCollection<MyTask>();
        private ObservableCollection<MyError> Errors = new ObservableCollection<MyError>();
        public ObservableCollection<Window> ChildWindowCollection = new ObservableCollection<Window>();
        public ObservableCollection<Thread> ThreadCollection = new ObservableCollection<Thread>();
        #endregion

        #region const data
        public const string DEFAULTSYNCDIRNAME = "Onebox";
        public const string DEFAULTCACHEDIR = "/.OneboxCache";
        public const string DEFAULTAPIVWESION = "/api/v2";
        public const string DEFAULTUSERDATA = "UserData";
        private const string SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT = "9A5FCF8C-6316-4101-AD83-35CF1DE9D526";
        private const string SHARE_DRIVE_FILE_SYSTEM_MONITOR_STOP_EVENT = "DD2D3CB4-F71F-4FDC-9530-6A70CE9471EA";
        public const string DATETIME_FORMART_STRING = "{0:yyyy/MM/dd HH:mm}";
        #endregion

        #region other variable
        public Int32 skipTimes = 0;
        public ProcessManage processManager = new ProcessManage();
        public ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW();
        public string strDefaultSyncPath = "";
        public string strRecordUserName = "";
        private static Object _LockObj = new Object();
        private System.Windows.Threading.DispatcherTimer _RecScanTimer = null;
        private System.Windows.Threading.DispatcherTimer _RecAnalyseTimer = null;
        private IntPtr UIHwnd = IntPtr.Zero;
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private RegValue varRegValue = new RegValue();
        private Common common = new Common();
        public TrayIconManager trayIconManager = new TrayIconManager();
        private Rect rcNormal;
        private Int16 RecordWindowState = (int)WindowState.Normal;
        #endregion

        #region define child window
        private ConfirmWindow confirmWindow = null;
        private LoginWindow loginWindow = null;
        private RunNoticeWindow runNoticeWindow = new RunNoticeWindow();
        public LinkShare LSWindow = null;
        public Share ShareWindow = null;
        private SyncDirTree syncDirTree = null;
        private UsingWizard WizardWindow = null;
        private About AboutWindow = null;
        private Upgrade update = null;
        #endregion

        #region define child thread
        public Thread Monitorthread = null;
        private Thread LoginThread = null;
        private Thread CollectLogThead = null;
        private Thread MonitorServerthread = null;
        private Thread Upgradethread = null;
        #endregion

        public MainWindow()
        {
            try
            {
                InitializeComponent();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
        
        #region start oneboxsync server
        private bool StartServer(bool IsShowNotice)
        {
            try
            {
                if (processManager.CheckProcessExist(ProcessManager.ProcessName.SyncProcess))
                {
                    while (!processManager.StopAll())
                    {
                        Thread.Sleep(5000);
                    }
                }

                if (!processManager.Start(Environment.CurrentDirectory, ProcessName.SyncProcess, "start"))
                {
                    if (IsShowNotice)
                    {
                        runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["startErr"], NoticeType.Error,
                      (string)Application.Current.Resources["startErrInfo"], (string)Application.Current.Resources["startErrRecover"]);
                        AbnormalExit();
                    }
                    else
                    {
                        App.Log.Info("OneboxSyncSevervice.exe is not exist,start syncserver failed");
                        return false;
                    }
                }

                MonitorServerthread = new Thread(new ThreadStart(MonitorServerTask));
                MonitorServerthread.IsBackground = true;
                MonitorServerthread.Start();
                ThreadCollection.Add(MonitorServerthread);
                /// <summary>
                /// 查看Thrift端口是否打开，如果20秒钟未打开，提示用户启动失败
                /// </summary>
                //Thread.Sleep(5000);
                DateTime NowDatetimeS = DateTime.Now.AddSeconds(20);

                while (true)
                {
                    object objport = varRegValue.ReadReg(RegValue.REG_ROOT_HKCR, RegValue.REG_PORT_SUB_KEY, RegValue.REG_PORT);
                    if (objport == null || objport.ToString() == RegValue.REG_PORT_DEFAULTVALUE)
                    {
                        Thread.Sleep(1000);
                        if (!processManager.CheckProcessExist(ProcessManager.ProcessName.SyncProcess))
                        {
                            if (IsShowNotice)
                            {
                                runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["startErr"], NoticeType.Error,
                              (string)Application.Current.Resources["startErrInfo"], (string)Application.Current.Resources["startErrRecover"]);
                                AbnormalExit();
                            }
                            else
                            {
                                App.Log.Info("OneboxSyncSevervice.exe is not exist,start syncserver failed");
                                return false;
                            }

                        }
                        continue;
                    }
                    else
                    {
                        ThriftClient.Client.ThriftPort = System.Convert.ToInt32(objport);
                        if (PortInUse(ThriftClient.Client.ThriftPort))
                        {
                            string strConfigureFilePath = common.GetInstallPath() + "\\" + ConfigureFileOperation.ConfigureFileRW.CONFIGUREFILENAME;
                            thriftClient.initUserContext(UIHwnd.ToInt64(), strConfigureFilePath);
                            break;
                        }

                        if (-1 == DateTime.Compare(NowDatetimeS, DateTime.Now))
                        {
                            if (IsShowNotice)
                            {
                                runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["startErr"], NoticeType.Error,
                                                           (string)Application.Current.Resources["startErrInfo"], (string)Application.Current.Resources["startErrRecover"]);
                                AbnormalExit();
                            }
                            else
                            {
                                App.Log.Info("Thrift port open failed,start syncserver failed");
                                return false;
                            }
                        }
                    }
                }

                LoginThread = new Thread(new ThreadStart(LoginTask));
                LoginThread.Start();
                ThreadCollection.Add(LoginThread);
                return true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }
        }

        /// <summary>
        /// 用于检查端口是否被占用
        /// </summary>
        /// <param name="strIpOrDName">输入参数,表示需要检查的端口</param>
        /// <returns>true:表示端口被占用，false：表示端口未被占用</returns>
        public static bool PortInUse(int port)
        {
            bool inUse = false;
            try
            {
                IPGlobalProperties ipProperties = IPGlobalProperties.GetIPGlobalProperties();
                System.Net.IPEndPoint[] ipEndPoints = ipProperties.GetActiveTcpListeners();
                foreach (System.Net.IPEndPoint endPoint in ipEndPoints)
                {
                    if (endPoint.Port == port)
                    {
                        inUse = true;
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
            return inUse;
        }
        #endregion

        #region login task
        /// <summary>
        ///登录线程任务
        /// </summary>
        /// <returns></returns>
        private void LoginTask()
        {
            try
            {
                int iRet = (int)RetunCode.DefaultError;
                while (true)
                {
                    if ((int)RetunCode.Success == iRet || IsAuthSuccess || IsExit ||
                        (int)RetunCode.BindError == iRet || (int)RetunCode.DeviceError == iRet)
                    {
                        break;
                    }

                    iRet = Authenticate();
                    this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Send, new DelegateLoginTask(Login), iRet);
                    Thread.Sleep(5000);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        ///登录委托，如果自动鉴权失败，启动手动登录
        /// </summary>
        /// <param name="iRet">自动鉴权结果</param>
        /// <returns></returns>
        private void Login(int iRet)
        {
            try
            {
                if ((int)RetunCode.DeviceError == iRet)
                {
                    runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["deviceErr"], NoticeType.Error,
                        (string)Application.Current.Resources["deviceErrInfo"], "");
                    this.Exit();
                }
                else if ((int)RetunCode.BindError == iRet)
                {
                    string authUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                   ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                    runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["userErr"], NoticeType.Info,
                                                                   (string)Application.Current.Resources["currentUser"] + " " +
                                                                   authUserName + " " +
                                                                   (string)Application.Current.Resources["bindingUserErr"], "");
                    this.Exit();
                }
                else if ((int)RetunCode.Success == iRet)
                {
                    CloseLoginWindow();
                    AuthSucessAction(false);
                }
                else if ((int)RetunCode.CouldntConnect == iRet || (int)RetunCode.CouldntResolvHost == iRet)
                {
                    ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                }
                else
                {
                    IsShowLoginWindow = true;
                    ShowTrayIcon(TrayIconStatus.ERROR_ICO);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void CloseLoginWindow()
        {
            if (null != this.loginWindow && this.loginWindow.IsLoaded != false && this.loginWindow.IsVisible)
            {
                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Right, (string)Application.Current.Resources["autoAuthSuccess"], "");
                System.Windows.Threading.DispatcherTimer _timer = new System.Windows.Threading.DispatcherTimer();
                _timer.Interval = new TimeSpan(0, 0, 5);
                _timer.Tick += (s, e) =>
                {
                    runNoticeWindow.noticeWindow.Close();
                    this.loginWindow.Close();
                    _timer.Stop();
                };
                _timer.Start();
            }
        }

        /// <summary>
        /// 鉴权成功后事务处理
        /// </summary>
        /// <param name="bShowWizardWindow">true：显示使用向导，false：不显示使用向导窗口</param>
        public void AuthSucessAction(bool bShowWizardWindow)
        {
            if (isFirstUse)
            {
                IsShowWorCWindow = true;
                CreateDefaultSyncDisk();
                if (!common.IsSlientInstall() || bShowWizardWindow)
                {
                    if (IsSkipUseWizard)
                    {
                        RunComfirmWindow();
                    }
                    else
                    {
                        RunWizardWindow();
                    }
                }
                else
                {
                    //读认证用户名 userID
                    string authAccountGuid = thriftClient.getUserId().ToString();
                    SHA1 sha1 = new SHA1CryptoServiceProvider();
                    byte[] bytes_sha1_in = UTF8Encoding.Default.GetBytes(authAccountGuid);
                    byte[] bytes_sha1_out = sha1.ComputeHash(bytes_sha1_in);
                    string authUserHash = BitConverter.ToString(bytes_sha1_out);

                    //添加注册项；
                    try
                    {
                        Registry.SetValue(RegValue.REG_BINDUSER_KEYNAME, null, authUserHash);
                    }
                    catch
                    {
                        return;
                    }

                    AuthSucessAfterAction();
                }
            }
            else
            {
                AuthSucessAfterAction();
            }
        }

        /// <summary>
        /// 用于在最大的逻辑盘上创建默认的同步文件夹
        /// </summary>
        public void CreateDefaultSyncDisk()
        {
            try
            {
                System.Management.SelectQuery selectQuery = new System.Management.SelectQuery("select * from win32_logicaldisk");
                System.Management.ManagementObjectSearcher searcher = new System.Management.ManagementObjectSearcher(selectQuery);
                System.Management.ManagementObject MaxFreeSpaceDisk = null;
                ObservableCollection<System.Management.ManagementObject> diskCollection = new ObservableCollection<System.Management.ManagementObject>();

                foreach (System.Management.ManagementObject disk in searcher.Get())
                {
                    if (common.GetSystemDisk() == disk["Name"].ToString())
                    {
                        MaxFreeSpaceDisk = disk;
                        continue;
                    }
                    else if (disk["DriveType"].ToString().Equals("3"))
                    {
                        diskCollection.Add(disk);
                    }
                }

                if (diskCollection.Count != 0)
                {
                    MaxFreeSpaceDisk = diskCollection[0];
                    foreach (System.Management.ManagementObject disk in diskCollection)
                    {
                        try
                        {
                            string DiskSize = disk["FreeSpace"].ToString();
                            string MaxDiskSize = MaxFreeSpaceDisk["FreeSpace"].ToString();
                            if (long.Parse(DiskSize) > long.Parse(MaxDiskSize) && "3" == disk["DriveType"].ToString())
                            {
                                MaxFreeSpaceDisk = disk;
                            }
                        }
                        catch { }
                    }
                }

                strDefaultSyncPath = MaxFreeSpaceDisk["Name"] + "\\" + DEFAULTSYNCDIRNAME;

                //如果是静默安装则创建缺省同步文件夹
                if (common.IsSlientInstall())
                {
                    if (!Directory.Exists(strDefaultSyncPath))
                    {
                        Directory.CreateDirectory(strDefaultSyncPath);
                    }
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY, strDefaultSyncPath);
                    thriftClient.updateConfigure();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 鉴权成功后的统一事务处理
        /// </summary>
        public void AuthSucessAfterAction()
        {
            IsLogin = true;
            ShowTrayIcon(TrayIconStatus.ONLINE_ICO);
            thriftClient.changeServiceWorkMode(ThriftClient.Service_Status.Service_Status_Online);
            AddVirtualFolder();
            isFirstUse = false;
            CheckPauseSyncStauts();
            InitSettingInfo();
            EnterSyncDir(IsOpenMonitorDir);
            Monitorthread = new Thread(new ThreadStart(MonitorTask));
            Monitorthread.IsBackground = true;
            Monitorthread.Start();
            ThreadCollection.Add(Monitorthread);
            if (IsCheckUpgrade)
            {
                Upgradethread = new Thread(new ThreadStart(UpgradeTask));
                Upgradethread.IsBackground = true;
                Upgradethread.Start();
                ThreadCollection.Add(Upgradethread);
            }
        }

        private void UpgradeTask()
        {
            this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateUpgradeTask(UpgradeProc));
        }

        private void UpgradeProc()
        {
            if (null == update)
            {
                update = new Upgrade();
            }

            if (update.IsUpgradeSucess())
            {
                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["UpgradeTitle"], NoticeType.Info, (string)Application.Current.Resources["UpgradeSuccess"], "");
            }
            else
            {
                update.Run();
            }
        }

        /// <summary>
        /// 运行同步文件夹选择窗口
        /// </summary>
        public void RunComfirmWindow()
        {
            try
            {

                if (confirmWindow == null || this.confirmWindow.IsLoaded == false)
                {
                    confirmWindow = new ConfirmWindow(this);
                    ChildWindowCollection.Add(confirmWindow);
                }

                string strUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                string strPCName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PCNAME_KEY);
                this.confirmWindow.ConfimUserName.Content = strUserName;
                this.confirmWindow.ConfirmComputerName.Text = strPCName;
                this.confirmWindow.CfirmPCNameTP.Text = strPCName;
                this.confirmWindow.TBSyncPath.Text = strDefaultSyncPath;

                if (confirmWindow.IsVisible == false)
                {
                    this.confirmWindow.Activate();
                    this.confirmWindow.ShowDialog();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public void RunWizardWindow()
        {
            if (WizardWindow == null || this.WizardWindow.IsLoaded == false)
            {
                WizardWindow = new UsingWizard(this);
                ChildWindowCollection.Add(WizardWindow);
            }

            WizardWindow.Show();
            WizardWindow.Activate();
        }
        #endregion

        #region monitor task
        /// <summary>
        ///监控StorageServer线程任务，当StorageServer挂掉负责将其拉起
        /// </summary>
        /// <returns></returns>
        public void MonitorServerTask()
        {
            try
            {
                SafeWaitHandle hanle = new SafeWaitHandle(processManager.GetProcess(ProcessName.SyncProcess).Handle, false);
                AutoResetEvent waitHandle = new AutoResetEvent(false);
                waitHandle.SafeWaitHandle = hanle;
                waitHandle.WaitOne();
                if (!IsExit)
                {
                    StopThreadAndResettingMark(false);
                    if (StartServer(false))
                    {
                        App.Log.Info("Pull syncserver service success");
                    }
                    else
                    {
                        App.Log.Info("Pull syncserver service failed");
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 监控任务，监控时间间隔5秒。监控包括任务列表刷新、C盘空间、同步文件夹所在盘空间
        /// </summary>
        /// <returns></returns>
        public void MonitorTask()
        {
            try
            {
                while (!IsExit)
                {
                    this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateMonitorTask(MonitorHandle));
                    Thread.Sleep(5000);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void MonitorHandle()
        {
            MonitorFreeSpace();
            UpdateTask();
            InsertErrorList();
        }

        /// <summary>
        /// 监控C盘、同步文件夹所在盘的剩余空间按 
        /// </summary>
        /// <returns></returns>
        private void MonitorFreeSpace()
        {
            try
            {
                //更新同步文件夹所在盘剩余空间
                string strSyncPath = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY);

                if ("" == strSyncPath || !Directory.Exists(strSyncPath))
                {
                    return;
                }

                string strRootInfo = Directory.GetDirectoryRoot(strSyncPath);
                string strRoot = strRootInfo.TrimEnd('\\');
                long FreeSpace = common.GetFreeSpace(strRoot);

                if (524288000 > FreeSpace && 104857600 < FreeSpace && Is500MNotice)
                {
                    Is500MNotice = false;
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["remainSpaceTile"], NoticeType.Info, (string)Application.Current.Resources["RemainSpace500M"], "");
                }
                else if (524288000 < FreeSpace)
                {
                    Is500MNotice = true;
                }

                if (104857600 > FreeSpace && Is100MNotice)
                {
                    Is100MNotice = false;
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["remainSpaceTile"], NoticeType.Info, (string)Application.Current.Resources["RemainSpace100M"], "");

                    if ((string)this.SyncMenu.Header == (string)Application.Current.Resources["syncPause"])
                    {
                        PauseSync();
                    }
                }
                else if (104857600 < FreeSpace && !Is100MNotice)
                {
                    Is100MNotice = true;

                    if ((string)this.SyncMenu.Header == (string)Application.Current.Resources["syncRecover"])
                    {
                        if (trayIconManager.CrruentIcon == TrayIconStatus.SUSPEND_ICO && !IsStopSync)
                        {
                            StartSync();
                        }
                    }
                }

                LBFreeSpace.Dispatcher.BeginInvoke(new Action(() =>
                {
                    string strSize = "";
                    string strUnit = "";
                    common.ConvertSpaceSize(FreeSpace.ToString(), ref strSize, ref strUnit);
                    this.LBFreeSpace.Content = strSize;
                    this.SpaceUint.Content = strUnit;
                }));
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        ///更新完成任务和监控C盘以及同步文件夹所在目录的剩余空间
        /// </summary>
        /// <returns></returns>
        private void UpdateTask()
        {
            try
            {
                if (0 != tasks.Count)
                {
                    for (int i = 0; i < tasks.Count; i++)
                    {
                        if (120.0 == tasks.ElementAt(i).Progress || 0 > tasks.ElementAt(i).CreateorUpdateTime.AddMinutes(10).CompareTo(DateTime.Now))
                        {
                            AddandDeleteTransList(tasks.ElementAt(i), System.Collections.Specialized.NotifyCollectionChangedAction.Remove);
                        }
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 插入错误列表
        /// </summary>
        /// <param name="strFilePath"></param>
        /// <param name="strErrorCode"></param>
        private void InsertErrorList()
        {
            try
            {
                Action action = new Action(() =>
                {
                    this.Dispatcher.BeginInvoke(new Action(() =>
                    {
                        try
                        {
                            if (changeFlag || skipTimes >= 60)
                            {
                                Errors.Clear();
                                List<ThriftClient.Error_Info> errorList = thriftClient.listError(0, 200);
                                XmlNodeInfo.ErrorXmlNodeInfo NodeInfo = new XmlNodeInfo.ErrorXmlNodeInfo();
                                XmlDocumentOper XmlOperation = new XmlDocumentOper(NodeInfo.ErrorMsgFilePath);
                                if (XmlNodeInfo.ErrorXmlNodeInfo.attributeList.Count == 0)
                                {
                                    XmlOperation.ReadParseErrorXml(XmlNodeInfo.ErrorXmlNodeInfo.RootNode);
                                }
                                if (errorList.Count != 0)
                                {
                                    ObservableCollection<MyError> temp = GetDesAndAdvice(errorList);
                                    foreach (MyError myerror in temp)
                                    {
                                        Errors.Add(myerror);
                                    }

                                    for (int i = 0; i < Errors.Count; i++)
                                    {
                                        if ((i % 2) == 0)
                                        {
                                            Errors.ElementAt(i).ListColor = 0;
                                            Errors.ElementAt(i).ListSideColor = 0;
                                        }
                                        else
                                        {
                                            Errors.ElementAt(i).ListColor = 1;
                                            Errors.ElementAt(i).ListSideColor = 1;
                                        }
                                    }
                                }

                                changeFlag = false;
                                skipTimes = 0;
                            }
                            else
                            {
                                ++skipTimes;
                            }
                        }
                        catch (System.Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                        }
                    }), System.Windows.Threading.DispatcherPriority.Normal, null);
                });
                action.BeginInvoke(null, null);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
        #endregion

        #region  exit mainwindow
        public void Exit()
        {
            this.Hide();
            IsExit = true;
            this.trayMenu.IsOpen = false;
            this.trayMenu.IsEnabled = false;
            runNoticeWindow.CloseOwnNoticeWindow();
            Thread stopThread = new Thread(new ThreadStart(closeApp));
            stopThread.Start();
        }

        private void closeApp()
        {
            try
            {
                StopThreadAndResettingMark(true);
                foreach (Window childWindow in ChildWindowCollection)
                {
                    this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateCloseWindow(closeWindow), childWindow);
                }

                varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE);
                ClearPassword();

                while (!processManager.StopAll())
                {
                    Thread.Sleep(5000);
                }

                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateCloseWindow(closeWindow), this);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                IsExit = false;
            }
        }

        private void closeWindow(Window childWindow)
        {
            childWindow.Close();
        }

        private void ClearPassword()
        {
            string strRemPassword = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_REMPASSWORD_KEY);
            if (strRemPassword != ((int)RemeberPsd.Yes).ToString())
            {
                varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                 ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY, "");
            }
        }

        /// <summary>
        /// 用于处理程序异常退出
        /// </summary>
        public void AbnormalExit()
        {
            try
            {
                foreach (Thread ChildThread in ThreadCollection)
                {
                    if (null != ChildThread)
                    {
                        ChildThread.Interrupt();
                    }
                }

                foreach (Window ChildWindow in ChildWindowCollection)
                {
                    if (null != ChildWindow)
                    {
                        ChildWindow.Close();
                    }
                }

                this.trayMenu.IsOpen = false;
                this.notifyIcon.Dispose();
                varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE);
                runNoticeWindow.CloseOwnNoticeWindow();
                ClearPassword();

                processManager.StopAll();
                App.Log.Info("The program is abnormal exit.");
                Environment.Exit(0);
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                Environment.Exit(0);
            }
        }
        #endregion


        bool bInit = false;
        private void TabControl_SelectionChanged_1(object sender, SelectionChangedEventArgs e)
        {
            if (e.Source is TabControl)
            {
                if (!bInit)
                {
                    bInit = true;
                    return;
                }
            }
        }


    }
}
