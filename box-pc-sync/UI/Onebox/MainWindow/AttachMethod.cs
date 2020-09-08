using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using Microsoft.Win32;
using System.Windows.Controls;
using System.Collections.ObjectModel;
using System.IO;
using System.Windows.Documents;
using System.Security.Cryptography;
using System.Threading;
using System.Collections.Specialized;
using System.Windows.Media.Imaging;
using Onebox.XmlDocumentOperation;
using Onebox.ZipHelp;
using Onebox.RegisterValue;
using Onebox.NSTrayIconStatus;
using Onebox.ProcessManager;
using Microsoft.WindowsAPICodePack.Dialogs;

namespace Onebox
{
    public partial class MainWindow : Window
    {
        #region init
        private void Init()
        {
            try
            {
                object ObjInstallType = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_INSTALL_TYPE);
                varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE, (int)ThriftClient.Service_Status.Service_Status_Uninitial);
                varRegValue.WriteReg(RegValue.REG_ROOT_HKCR, RegValue.REG_PORT_SUB_KEY, RegValue.REG_PORT, RegValue.REG_PORT_DEFAULTVALUE);
                ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                tasks.Clear();
                this.taskInfo.ItemsSource = tasks;
                this.ErrorInfo.ItemsSource = Errors;
                Errors.CollectionChanged += Errors_CollectionChanged;
                tasks.CollectionChanged += tasks_CollectionChanged;

                if (common.IsSlientInstall())
                {
                    IsOpenMonitorDir = false;
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void ClearEnvironment()
        {
            try
            {
                string bindUserHash = (string)Registry.GetValue(RegValue.REG_BINDUSER_KEYNAME, "", "");
                //未绑定
                if ("" == bindUserHash || null == bindUserHash)
                {
                    common.DeleteUserData();
                    DeleteVirtualFolder();
                }
            }
            catch
            { }
        }

        /// <summary>
        ///  初始化选项窗口中设置页的显示
        /// </summary>
        public void InitSettingInfo()
        {
            //TODO::读取配置文件
            string strMonitorRoot = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY);
            string strBootStartRun = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_BOOTSTARTRUN_KEY);
            string strUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
            string strPCName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PCNAME_KEY);
            string strSyncModel = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY);

            strMonitorRoot = strMonitorRoot.Replace('/', '\\');
            //this.Title_UserName_Lable.Content = strUserName;
            this.Setting_SyncPath_Text.Text = strMonitorRoot;
            this.Seting_SyncPath_ToolTip.Text = strMonitorRoot;
            this.LbFileNum.Content = "0";

            Run RunHead = new Run((string)Application.Current.Resources["device"]);
            Run RunSpace1 = new Run(" ");
            Italic DeviceInfo = new Italic(new Run(strPCName));
            DeviceInfo.FontSize = 14;
            Run RunSpace2 = new Run("  ");
            Run RunMidd = new Run((string)Application.Current.Resources["already"]);
            Run RunSpace3 = new Run(" ");
            Italic ItUserName = new Italic(new Run(strUserName));
            Run RunSpace4 = new Run("  ");
            ItUserName.FontSize = 14;
            Run RunEnd = new Run((string)Application.Current.Resources["binding"]);
            this.sp_UserInfo.Inlines.Clear();
            this.sp_UserInfo.Inlines.Add(RunHead);
            this.sp_UserInfo.Inlines.Add(RunSpace1);
            this.sp_UserInfo.Inlines.Add(DeviceInfo);
            this.sp_UserInfo.Inlines.Add(RunSpace2);
            this.sp_UserInfo.Inlines.Add(RunMidd);
            this.sp_UserInfo.Inlines.Add(RunSpace3);
            this.sp_UserInfo.Inlines.Add(ItUserName);
            this.sp_UserInfo.Inlines.Add(RunSpace4);
            this.sp_UserInfo.Inlines.Add(RunEnd);
            this.LBDiskName.Content = (string)Application.Current.Resources["availableSpace"];
            System.Globalization.CultureInfo currentCultureInfo = System.Globalization.CultureInfo.CurrentCulture;

            if (strBootStartRun == ((int)BootStartRunType.BootStartRun).ToString() || string.IsNullOrEmpty(strBootStartRun))
            {
                this.Setting_BootStartRun_CheckBox.IsChecked = true;
                settAutoRun();
            }
            else
            {
                this.Setting_BootStartRun_CheckBox.IsChecked = false;
                cancelAutoRun();
            }

            if (((int)SyncModel.Sync_Local).ToString() == strSyncModel)
            {
                this.CBFileDownloadPolicy.IsChecked = false;
            }
            else
            {
                this.CBFileDownloadPolicy.IsChecked = true;
            }

            this.LbFileNum.Content = "0";
            ConvertTransShow();
        }
        #endregion

        #region  errorlist export

        private void SetWindowToMaximized()
        {
            try 
            {
                rcNormal = new Rect(this.Top, this.Left, this.Width, this.Height);
                this.Top = 0;
                this.Left = 0;
                Rect rc = SystemParameters.WorkArea;
                this.Width = rc.Width;
                this.Height = rc.Height;
                this.MaxButton.Visibility = Visibility.Collapsed;
                this.RestoreButton.Visibility = Visibility.Visible;

                this.Setting_SyncPath_Text.Width = this.Width - 50;

                if (this.taskInfo.ActualWidth != 0)
                {
                    this.taskInfo_GVC2.Width = this.taskInfo.ActualWidth - (taskInfo_GVC1.Width + taskInfo_GVC3.Width + taskInfo_GVC4.Width) - 6;
                }

                if (this.ErrorInfo.ActualWidth != 0)
                {
                    this.ErrorInfo_GVC3.Width = this.ErrorInfo.ActualWidth * 0.169;
                    this.ErrorInfo_GVC4.Width = this.ErrorInfo.ActualWidth * 0.256;
                    this.ErrorInfo_GVC1.Width = this.ErrorInfo.ActualWidth * 0.575 - this.ErrorInfo_GVC2.Width - 8;
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
          
            RecordWindowState = (int)WindowState.Maximized;
        }

        private void SetWindowToNormal()
        {
            this.Top = rcNormal.Top;
            this.Left = rcNormal.Left;
            this.Width = rcNormal.Width;
            this.Height = rcNormal.Height;

            this.MaxButton.Visibility = Visibility.Visible;
            this.RestoreButton.Visibility = Visibility.Collapsed;

            this.Setting_SyncPath_Text.Width = 400;
            this.taskInfo_GVC1.Width = 50;
            this.taskInfo_GVC2.Width = 328;
            this.taskInfo_GVC3.Width = 86;
            this.taskInfo_GVC4.Width = 180;

            this.ErrorInfo_GVC1.Width = 287;
            this.ErrorInfo_GVC2.Width = 80;
            this.ErrorInfo_GVC3.Width = 110;
            this.ErrorInfo_GVC4.Width = 167;

            RecordWindowState = (int)WindowState.Normal;
        }

        #endregion

        #region  errorlist export

        private void ExportFileToCsv(ObservableCollection<MyError> allerrorList,string fileName)
        {
            try
            {
                if (fileName != null)
                {
                    using (StreamWriter streamWriter = new StreamWriter(fileName, false, Encoding.Default))
                    {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.Append((string)Application.Current.Resources["fileItem"]).Append(",");
                        stringBuilder.Append((string)Application.Current.Resources["errorCodeItem"]).Append(",");
                        stringBuilder.Append((string)Application.Current.Resources["errorMsgItem"]).Append(",");
                        stringBuilder.Append((string)Application.Current.Resources["disposeMsgItem"]);
                        streamWriter.WriteLine(stringBuilder.ToString());

                        foreach (MyError ei in allerrorList)
                        {
                            stringBuilder = new StringBuilder();
                            stringBuilder.Append(ei.FilePath + "").Append(",");
                            stringBuilder.Append(ei.TaskErrorCode + "").Append(",");
                            stringBuilder.Append(ei.ErrorDes + "").Append(",");
                            stringBuilder.Append(ei.Suggest + "").Append(",");
                            streamWriter.WriteLine(stringBuilder.ToString());
                        }
                        stringBuilder = new StringBuilder();
                        streamWriter.WriteLine("");
                        streamWriter.WriteLine("");
                        streamWriter.WriteLine(stringBuilder.ToString()); streamWriter.Flush();
                        streamWriter.Close();
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void ExportErrorList(Object obj)
        {
            try
            {
                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Send, new DelegateNotEnableExportButton(NotEnableButton), this.Button_ExportErrorList);
                List<ThriftClient.Error_Info> errorList = thriftClient.listError(0, -1);
                XmlNodeInfo.ErrorXmlNodeInfo NodeInfo = new XmlNodeInfo.ErrorXmlNodeInfo();
                XmlDocumentOper XmlOperation = new XmlDocumentOper(NodeInfo.ErrorMsgFilePath);
                ObservableCollection<MyError> allerrorList = new ObservableCollection<MyError>();
                if (XmlNodeInfo.ErrorXmlNodeInfo.attributeList.Count == 0)
                {
                    XmlOperation.ReadParseErrorXml(XmlNodeInfo.ErrorXmlNodeInfo.RootNode);
                }

                allerrorList = GetDesAndAdvice(errorList);
                if (allerrorList.Count > 0)
                {
                    SaveDialogResult result = (SaveDialogResult)obj;
                    if (result.ret == CommonFileDialogResult.Ok)
                    {
                        ExportFileToCsv(allerrorList, result.FileName);
                    }
                }

                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Send, new DelegateEnableExportButton(EnableButton), this.Button_ExportErrorList);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Send, new DelegateEnableExportButton(EnableButton), this.Button_ExportErrorList);
            }
        }

        private void NotEnableButton(Button exportButton)
        {
            exportButton.IsEnabled = false;
        }

        private void EnableButton(Button exportButton)
        {
            exportButton.IsEnabled = true;
        }

        private ObservableCollection<MyError> GetDesAndAdvice(List<ThriftClient.Error_Info> errorList)
        {
            ObservableCollection<MyError> _return = new ObservableCollection<MyError>();
            _return.Clear();
            string strDes = XmlNodeInfo.ErrorXmlNodeInfo.attributeList[0].des;
            string strAdvice = XmlNodeInfo.ErrorXmlNodeInfo.attributeList[0].advice;

            foreach (ThriftClient.Error_Info errorInfo in errorList)
            {
                errorInfo.Path = errorInfo.Path.Replace('/', '\\');
                bool isFind = false;
                foreach (XmlNodeAttribute.ErrorNodeAttribute nodeattribute in XmlNodeInfo.ErrorXmlNodeInfo.attributeList)
                {
                    if (nodeattribute.cd.Equals(errorInfo.ErrorCode.ToString()))
                    {
                        MyError ErrorTask = new MyError(errorInfo.Path, errorInfo.ErrorCode.ToString(), nodeattribute.des, nodeattribute.advice);
                        _return.Add(ErrorTask);
                        isFind = true;
                        break;
                    }
                }
                if (!isFind)
                {
                    MyError ErrorTask = new MyError(errorInfo.Path, errorInfo.ErrorCode.ToString(), strDes, strAdvice);
                    _return.Add(ErrorTask);
                }
            }

            return _return;
        }

        #endregion

        #region  collect log
        /// <summary>
        ///日志收集任务，收集内容：.db文件、.dmp文件、.log文件
        /// </summary>
        private void CollectLogTask()
        {
            try
            {
                string strUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                string strInstallLog = System.Environment.CurrentDirectory + "\\install.log";
                string strUserData = System.Environment.CurrentDirectory + "\\UserData";
                string strZipLog = System.Environment.CurrentDirectory + "\\Log";
                string strLogDir = System.Environment.CurrentDirectory + "\\OneboxLog";
                string strLogDmp = System.Environment.CurrentDirectory + "\\*.dmp";
                string strCollectLogDir = System.Environment.CurrentDirectory + "\\OneboxLog\\OneboxLog";
                string strCollectFileName = strCollectLogDir + "_" + strUserName + DateTime.Now.Year.ToString() + DateTime.Now.Month.ToString() +
                                                                 DateTime.Now.Day.ToString() + DateTime.Now.Hour.ToString() + DateTime.Now.Minute.ToString() + DateTime.Now.Second.ToString() + ".zip";

                if (!Directory.Exists(strLogDir))
                {
                    Directory.CreateDirectory(strLogDir);
                }

                if (!Directory.Exists(strCollectLogDir))
                {
                    Directory.CreateDirectory(strCollectLogDir);
                }

                foreach (string fileStr in Directory.GetFiles(System.Environment.CurrentDirectory))
                {
                    int index = fileStr.LastIndexOf("\\");
                    string FileName = fileStr.Substring(index, fileStr.Length - index);
                    int index1 = FileName.IndexOf(".");

                    if (0 <= index1)
                    {
                        string strSub = FileName.Substring(index1, 4);
                        if (strSub == ".log" || strSub == ".dmp")
                        {
                            File.Copy(fileStr, strCollectLogDir + FileName, true);
                        }
                    }
                }

                if (Directory.Exists(strUserData))
                {
                    foreach (string fileStr in Directory.GetFiles(strUserData))
                    {
                        int index = fileStr.LastIndexOf(".");
                        int index1 = fileStr.LastIndexOf("\\");

                        if (0 <= index)
                        {
                            string strSub = fileStr.Substring(index, fileStr.Length - index);
                            string FileName = fileStr.Substring(index1, fileStr.Length - index1);
                            if (strSub == ".db")
                            {
                                if (FileName != "\\syncdata.db")
                                {
                                    File.Copy(fileStr, strCollectLogDir + FileName, true);
                                }
                            }
                        }
                    }
                }

                if (Directory.Exists(strZipLog))
                {
                    foreach (string fileStr in Directory.GetFiles(strZipLog))
                    {
                        int index = fileStr.LastIndexOf("\\");
                        string FileName = fileStr.Substring(index, fileStr.Length - index);
                        int index1 = FileName.LastIndexOf(".");

                        if (0 <= index1)
                        {
                            string strSub = FileName.Substring(index1, 4);
                            if (strSub == ".zip")
                            {
                                File.Copy(fileStr, strCollectLogDir + FileName, true);
                            }
                        }
                    }
                }

                if (ZipHelper.Zip(strCollectLogDir, strCollectFileName))
                {
                    Directory.Delete(strCollectLogDir, true);
                }

                System.Diagnostics.Process.Start(strLogDir);

                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateCollectLogTask(CollectLog));
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new DelegateCollectLogTask(CollectLog));
            }
        }

        private void CollectLog()
        {
            try
            {
                this.CollectLogMenu.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        #endregion

        #region start and stop sync

        /// <summary>
        ///恢复同步任务
        /// </summary>
        /// <returns></returns>
        private void StartSync()
        {
            try
            {
                int iRet = thriftClient.changeServiceWorkMode(ThriftClient.Service_Status.Service_Status_Online);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 暂停同步任务
        /// </summary>
        /// <returns></returns>
        private void PauseSync()
        {
            try
            {
                int iRet = thriftClient.changeServiceWorkMode(ThriftClient.Service_Status.Service_Status_Pause);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        #endregion

        #region auto authenticate
        /// <summary>
        /// 用于自动鉴权 
        /// </summary>
        /// <returns>0: 鉴权成功   非零：鉴权失败</returns>
        private int Authenticate()
        {
            int iRet = (int)RetunCode.Success;

            try
            {
                int ServerStatus = thriftClient.getServiceStatus();

                if (ServerStatus == (int)ThriftClient.Service_Status.Service_Status_Offline)
                {
                    //离线认证：
                    iRet = Auth_Offline_AutoLogin();
                }
                else if (ServerStatus == (int)ThriftClient.Service_Status.Service_Status_Uninitial)
                {
                    //在线认证；
                    iRet = Auth_Online_AutoLogin();
                }
                else
                {
                    iRet = (int)RetunCode.DefaultError;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                iRet = (int)RetunCode.DefaultError;
            }

            return iRet;
        }


        /// <summary>
        /// 用于在线鉴权 
        /// </summary>
        /// <returns>0: 鉴权成功   非零：鉴权失败</returns>
        private int Auth_Online_AutoLogin()
        {
            int iRet = (int)RetunCode.Success;
            try
            {
                iRet = thriftClient.login((int)ThriftClient.Authen_Type.Authen_Type_Domain, "", "", "");

                if ((int)RetunCode.Success != iRet)
                {
                    /// <summary>
                    ///便于StorageServer崩溃以后无感知重新登录
                    /// </summary>
                    string strLonginType = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_LOGINTYPE_KEY);
                    string strUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                    string strPassWord = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY);
                    string strAutoLogin = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_AUTOLOGINE_KEY);

                    if (strAutoLogin == ((int)AutoLogin.Yes).ToString())
                    {
                        iRet = thriftClient.login((int)ThriftClient.Authen_Type.Authen_Type_Normal, strUserName, thriftClient.decyptString(strPassWord), "");
                    }
                    else if ("" != strUserName && "" != strRecordUserName)
                    {
                        if (((int)LoginType.Normal).ToString() == strLonginType && strUserName.ToLower() == strRecordUserName.ToLower())
                        {
                            iRet = thriftClient.login((int)ThriftClient.Authen_Type.Authen_Type_Normal, strUserName, thriftClient.decyptString(strPassWord), "");
                        }
                    }

                    if ((int)RetunCode.Success != iRet)
                    {
                        return iRet;
                    }
                }
                else
                {
                    /// <summary>
                    ///配合storageService:通知登陆成功类型；无实际业务意义
                    /// </summary>
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_LOGINTYPE_KEY, ((int)LoginType.DomainLogin).ToString());
                    thriftClient.updateConfigure();
                }

                if (!CheckDeviceLink())
                {
                    if (isFirstUse)
                    {
                        return iRet;
                    }

                    iRet = (int)RetunCode.BindError;
                    return iRet;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                iRet = (int)RetunCode.DefaultError;
            }

            return iRet;
        }

        /// <summary>
        /// 用于离线鉴权 
        /// </summary>
        /// <returns>0: 鉴权成功   非零：鉴权失败</returns>
        private int Auth_Offline_AutoLogin()
        {
            int iRet = (int)RetunCode.Success;

            try
            {
                if (!CheckDeviceLink())
                {
                    iRet = (int)RetunCode.BindError;
                    if (isFirstUse)
                    {
                        //未关联时， 离线显示为校验失败
                        iRet = (int)RetunCode.DefaultError;
                    }
                    return iRet;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                iRet = (int)RetunCode.DefaultError;
            }

            //匹配：离线使用:
            return iRet;
        }

        /// <summary>
        /// 用于检查账户绑定信息
        /// </summary>
        /// <returns>true：绑定   false: 未绑定</returns>
        public bool CheckDeviceLink()
        {
            try
            {
                isFirstUse = false;
                //检验用户ObjectSid（SID区分大小写）
                string authAccountGuid = thriftClient.getUserId().ToString();
                SHA1 sha1 = new SHA1CryptoServiceProvider();
                byte[] bytes_sha1_in = UTF8Encoding.Default.GetBytes(authAccountGuid);
                byte[] bytes_sha1_out = sha1.ComputeHash(bytes_sha1_in);
                string authUserGuidHash = BitConverter.ToString(bytes_sha1_out);

                //读关联信息：
                string bindUserHash = "";
                try
                {
                    bindUserHash = (string)Registry.GetValue(RegValue.REG_BINDUSER_KEYNAME, "", "");
                    //未绑定
                    if ("" == bindUserHash || null == bindUserHash)
                    {
                        isFirstUse = true;
                    }
                }
                catch
                {
                    //TODO::读失败如何处理；
                    isFirstUse = true;
                }

                //进行匹配
                if (bindUserHash != authUserGuidHash)
                {
                    //检查是否为用户登陆名绑定
                    //读已存用户信息；
                    string authUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                       ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                    //有绑定::判断是否为同一用户
                    sha1 = new SHA1CryptoServiceProvider();
                    bytes_sha1_in = UTF8Encoding.Default.GetBytes(authUserName.ToLower());
                    bytes_sha1_out = sha1.ComputeHash(bytes_sha1_in);
                    string authUserNameHash = BitConverter.ToString(bytes_sha1_out);

                    if (bindUserHash == authUserNameHash)
                    {
                        if (authAccountGuid != "")
                        {
                            //匹配，则修改绑定信息为用户ObjectSid
                            try
                            {
                                Registry.SetValue(RegValue.REG_BINDUSER_KEYNAME, null, authUserGuidHash);
                            }
                            catch (System.Exception ex)
                            {
                                //单独catch原因：修改失败，不影响认证结果；日志记录即可
                                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                            }
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }

            return true;
        }

        #endregion

        #region setting auto run
        private bool settAutoRun()
        {
            bool bRet = true;
            try
            {
                string strProgramPath = "\"" + System.Environment.CurrentDirectory + "\\" + ProcessName.AutoStartProcess + "\"";
                string strTerminateProcessPath = System.Environment.CurrentDirectory + "\\" + "Tools";
                varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_AUTORUN_SUB_KEY, DEFAULTSYNCDIRNAME, strProgramPath + " autorun");
                processManager.Start(strTerminateProcessPath + "", ProcessName.TerminateProcess, "/createTask");
                varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_BOOTSTARTRUN_KEY, ((int)BootStartRunType.BootStartRun).ToString());
            }
            catch (System.Exception ex)
            {
                this.Setting_BootStartRun_CheckBox.IsChecked = false;
                bRet = false;
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
            return bRet;
        }

        private bool cancelAutoRun()
        {
            bool bRet = true;
            try
            {
                string strProgramPath = "\"" + System.Environment.CurrentDirectory + "\\" + ProcessName.AutoStartProcess + "\"";
                string strTerminateProcessPath = System.Environment.CurrentDirectory + "\\" + "Tools";
                varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_AUTORUN_SUB_KEY, DEFAULTSYNCDIRNAME);
                varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_64BIT_AUTORUN_SUB_KEY, DEFAULTSYNCDIRNAME);
                processManager.Start(strTerminateProcessPath, ProcessName.TerminateProcess, "/cancelTask");
                varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_BOOTSTARTRUN_KEY, ((int)BootStartRunType.BootStartNORun).ToString());
            }
            catch (System.Exception ex)
            {
                this.Setting_BootStartRun_CheckBox.IsChecked = true;
                bRet = false;
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return bRet;
        }

        #endregion

        #region add ande delete virtual folder
        /// <summary>
        /// 清除同步盘图标配置
        /// </summary>
        private void DeleteVirtualFolder()
        {
            try
            {
                System.Diagnostics.ProcessStartInfo StartInfo;
                StartInfo = new System.Diagnostics.ProcessStartInfo();
                StartInfo.WorkingDirectory = System.Environment.CurrentDirectory;
                StartInfo.FileName = ProcessName.ShExtCmdProcess;
                StartInfo.Arguments = "delete-virtual-folder";
                System.Diagnostics.Process Pcs = System.Diagnostics.Process.Start(StartInfo);
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 添加同步盘图标配置
        /// </summary>
        private void AddVirtualFolder()
        {
            string strArgument = " add-virtual-folder";
            strArgument += " icon=\"" + System.Environment.CurrentDirectory + "\\Res\\logo.ico\"";
            string name = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION,
                                                        ConfigureFileOperation.ConfigureFileRW.CONF_VIRTUAL_FOLDER_NAME_KEY, "Onebox");
            strArgument += " name=\"" + name + "\"";
            string strMonitorPath = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY);
            strMonitorPath = strMonitorPath.Replace("/", "\\");
            strArgument += " path=\"" + strMonitorPath + "\"";
            processManager.Start(Environment.CurrentDirectory, ProcessName.ShExtCmdProcess, strArgument);
        }
        #endregion

        #region translite table method

        private void ConvertTransShow()
        {
            object objloginstate = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE);
            if (null != objloginstate && ((int)ThriftClient.Service_Status.Service_Status_Online == (int)objloginstate || (int)ThriftClient.Service_Status.Service_Status_Uninitial == (int)objloginstate))
            {
                if (IsScanning)
                {
                    this.taskInfo.Visibility = Visibility.Hidden;
                    this.sp_SpeedShow.Visibility = Visibility.Collapsed;
                    this.BDTransListEmpty.Visibility = Visibility.Visible;

                    this.sp_Scan.Visibility = Visibility.Visible;
                    this.sp_Analyse.Visibility = Visibility.Collapsed;
                    this.LBNoTask.Visibility = Visibility.Collapsed;
                }
                else if (!this.LbFileNum.Content.Equals("0") && tasks.Count == 0)
                {
                    this.taskInfo.Visibility = Visibility.Hidden;
                    this.sp_SpeedShow.Visibility = Visibility.Collapsed;
                    this.BDTransListEmpty.Visibility = Visibility.Visible;

                    this.sp_Scan.Visibility = Visibility.Collapsed;
                    this.sp_Analyse.Visibility = Visibility.Visible;
                    this.LBNoTask.Visibility = Visibility.Collapsed;
                }
                else if (this.LbFileNum.Content.Equals("0") && tasks.Count == 0)
                {
                    this.taskInfo.Visibility = Visibility.Hidden;
                    this.sp_SpeedShow.Visibility = Visibility.Collapsed;
                    this.BDTransListEmpty.Visibility = Visibility.Visible;

                    this.sp_Scan.Visibility = Visibility.Collapsed;
                    this.sp_Analyse.Visibility = Visibility.Collapsed;
                    this.LBNoTask.Visibility = Visibility.Visible;
                }
                else
                {
                    this.taskInfo.Visibility = Visibility.Visible;
                    this.sp_SpeedShow.Visibility = Visibility.Visible;
                    this.BDTransListEmpty.Visibility = Visibility.Collapsed;
                }
            }
            else
            {
                this.taskInfo.Visibility = Visibility.Hidden;
                this.sp_SpeedShow.Visibility = Visibility.Collapsed;
                this.BDTransListEmpty.Visibility = Visibility.Visible;

                this.sp_Scan.Visibility = Visibility.Collapsed;
                this.sp_Analyse.Visibility = Visibility.Collapsed;
                this.LBNoTask.Visibility = Visibility.Visible;
            }
        }

        private void AddandDeleteTransList(MyTask mytask, NotifyCollectionChangedAction operation)
        {
            if (operation == NotifyCollectionChangedAction.Add)
            {
                tasks.Add(mytask);
            }
            else if (operation == NotifyCollectionChangedAction.Remove)
            {
                tasks.Remove(mytask);
            }

            for (int j = 0; j < tasks.Count; j++)
            {
                if ((j % 2) == 0)
                {
                    tasks.ElementAt(j).ListColor = 0;
                }
                else
                {
                    tasks.ElementAt(j).ListColor = 1;
                }
            }
        }
        #endregion

        #region setting tray icon and menu

        private void SettingTrayMenuEnabled()
        {
            object objloginstate = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE);
            if (null != objloginstate)
            {
                if (!IsLogin)
                {
                    this.SyncMenu.IsEnabled = false;
                    this.OptionMenu.IsEnabled = false;
                    this.OpenSyncDir.IsEnabled = false;
                }
                else
                {
                    this.OptionMenu.IsEnabled = true;
                    this.OpenSyncDir.IsEnabled = true;
                    if ((int)objloginstate == (int)ThriftClient.Service_Status.Service_Status_Offline ||
                         (int)objloginstate == (int)ThriftClient.Service_Status.Service_Status_Error ||
                         (int)objloginstate == (int)ThriftClient.Service_Status.Service_Status_Uninitial)
                    {
                        this.SyncMenu.IsEnabled = false;
                    }
                }
            }
            else
            {
                this.SyncMenu.IsEnabled = false;
                this.OptionMenu.IsEnabled = false;
                this.OpenSyncDir.IsEnabled = false;
            }

            this.OpenWebMenu.IsEnabled = true;
            this.HelpMenu.IsEnabled = true;
            this.AboutMenu.IsEnabled = true;
        }

        private void SettingTrayMenuShow(string TrayIcon)
        {
            BitmapImage Btimg = new BitmapImage();
            if (TrayIcon.Equals(TrayIconStatus.OFFLINE_ICO) || TrayIcon.Equals(TrayIconStatus.SUSPEND_ICO))
            {
                    this.SyncMenu.Header = (string)Application.Current.Resources["syncRecover"];
                    Btimg.BeginInit();
                    Btimg.UriSource = new Uri("pack://application:,,,/Onebox;component/ImageResource/menu_startSync.png", UriKind.Absolute);
                    Btimg.EndInit();
            }
            else if (TrayIcon.Equals(TrayIconStatus.SYNCING_ICO) || TrayIcon.Equals(TrayIconStatus.ONLINE_ICO) || TrayIcon.Equals(TrayIconStatus.FAILED_ICO))
            {
                    this.SyncMenu.Header = (string)Application.Current.Resources["syncPause"];
                    Btimg.BeginInit();
                    Btimg.UriSource = new Uri("pack://application:,,,/Onebox;component/ImageResource/menu_stopSync.png", UriKind.Absolute);
                    Btimg.EndInit();
            }
            else if (TrayIcon.Equals(TrayIconStatus.ERROR_ICO))
            {
                object objloginstate = varRegValue.ReadReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE);
                if (null != objloginstate)
                {
                    if ((int)ThriftClient.Service_Status.Service_Status_Error == (int)objloginstate)
                    {
                        this.SyncMenu.Header = (string)Application.Current.Resources["syncRecover"];
                        Btimg.BeginInit();
                        Btimg.UriSource = new Uri("pack://application:,,,/Onebox;component/ImageResource/menu_startSync.png", UriKind.Absolute);
                        Btimg.EndInit();
                    }
                    else
                    {
                        this.SyncMenu.Header = (string)Application.Current.Resources["syncPause"];
                        Btimg.BeginInit();
                        Btimg.UriSource = new Uri("pack://application:,,,/Onebox;component/ImageResource/menu_stopSync.png", UriKind.Absolute);
                        Btimg.EndInit();
                    }
                }
            }

            SyncMenu_icon.Source = Btimg;
            SettingTrayMenuEnabled();
        }

        public void ShowTrayIcon(string TrayIcon)
        {
            BitmapImage Btimg = trayIconManager.GetTrayIcon(TrayIcon);
            notifyIcon.IconSource = Btimg;
            SettingTrayMenuShow(TrayIcon);
        }

        private void SettingSyncingAndFailedForTrayIcon()
        {
            if (IsStopSync)
            {
                return;
            }
            if (Errors.Count != 0)
            {
                ShowTrayIcon(TrayIconStatus.FAILED_ICO);
            }
           else if (!this.LbFileNum.Content.Equals("0") || tasks.Count != 0)
            {
                ShowTrayIcon(TrayIconStatus.SYNCING_ICO);
            }
            else 
            {
                ShowTrayIcon(TrayIconStatus.ONLINE_ICO);
            }
        }

        #endregion

        #region other method
        /// <summary>
        /// 用于打开同步目录
        /// </summary>
        /// <param name="strIpOrDName">输入参数,表示是否打开同步目录的bool</param>
        public void EnterSyncDir(bool IsOpenMonitorDir)
        {
            try
            {
                string strMonitorFilePath = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION,
                        ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY);

                if (IsLogin && Directory.Exists(strMonitorFilePath) && IsOpenMonitorDir)
                {
                    System.Diagnostics.Process.Start(strMonitorFilePath);
                }
            }
            catch (System.Exception ex)
            {
                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["openSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["syncPathNotExist"], "");
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void StopThreadAndResettingMark(bool IsStopMonitorServer)
        {
            IsAuthSuccess = false;
            IsLogin = false;
            IsShowLoginWindow = false;
            IsOpenMonitorDir = false;
            IsCheckUpgrade = false;

            foreach (Thread childThread in ThreadCollection)
            {
                if (childThread != null)
                {
                    if (!IsStopMonitorServer && childThread == MonitorServerthread)
                    {
                        continue;
                    }

                    childThread.Abort();
                }
            }

            ThreadCollection.Clear();
        }

        private void CheckPauseSyncStauts()
        {
            if (IsStopSync)
            {
                thriftClient.changeServiceWorkMode(ThriftClient.Service_Status.Service_Status_Pause);
            }
        }
        #endregion
    }
}
