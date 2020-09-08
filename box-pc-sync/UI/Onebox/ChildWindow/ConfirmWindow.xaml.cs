using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using Microsoft.WindowsAPICodePack.Dialogs;
using Microsoft.Win32;
using System.Security.Cryptography;
using System.ComponentModel;
using System.IO;

using Onebox.NSTrayIconStatus;
using Onebox.RegisterValue;
using Onebox.ProcessManager;

namespace Onebox
{
    /// <summary>
    /// ConfirmWindow.xaml 的交互逻辑
    /// </summary>
    public partial class ConfirmWindow : Window
    {
        CommonOpenFileDialog FileExplorer;
        MainWindow ParentWindow;
        bool IsClose;
        private RunNoticeWindow runNoticeWindow = null;
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private RegValue varRegValue = new RegValue();

        public ConfirmWindow(MainWindow parentWindow)
        {
            try
            {
                ParentWindow = parentWindow;
                IsClose = false;
                InitializeComponent();
                if (null == runNoticeWindow)
                {
                    runNoticeWindow = new RunNoticeWindow();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            this.DragMove();
            base.OnMouseLeftButtonDown(e);
        }

        protected override void OnClosed(EventArgs e)
        {
            runNoticeWindow.CloseOwnNoticeWindow();
            base.OnClosed(e);
        }

        private void Button_SyncPathChange_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                this.CloseButton1.IsEnabled = false;
                this.ConfirmOK.IsEnabled = false;

                if (null == this.FileExplorer)
                {
                    this.FileExplorer = new CommonOpenFileDialog();
                    this.FileExplorer.Title = (string)Application.Current.Resources["choiceSyncRoot"];
                    this.FileExplorer.IsFolderPicker = true;
                    this.FileExplorer.ShowDialog();
                    string strTextMonitorRoot = this.FileExplorer.FileName;

                    if (!CheckParam(strTextMonitorRoot))
                    {
                        this.CloseButton1.IsEnabled = true;
                        this.ConfirmOK.IsEnabled = true;
                        return;
                    }

                    strTextMonitorRoot = strTextMonitorRoot.Replace('/', '\\');
                    this.TBSyncPath.Text = strTextMonitorRoot;
                }
                else
                {
                    this.FileExplorer.ShowDialog();
                    string strTextMonitorRoot = this.FileExplorer.FileName;

                    if (!CheckParam(strTextMonitorRoot))
                    {
                        this.CloseButton1.IsEnabled = true;
                        this.ConfirmOK.IsEnabled = true;
                        return;
                    }

                    strTextMonitorRoot = strTextMonitorRoot.Replace('/', '\\');
                    this.TBSyncPath.Text = strTextMonitorRoot;
                }

                this.CloseButton1.IsEnabled = true;
                this.ConfirmOK.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                this.CloseButton1.IsEnabled = true;
                this.ConfirmOK.IsEnabled = true;
            }
        }

        private void Button_Close(object sender, RoutedEventArgs e)
        {
            this.Hide();
            runNoticeWindow.CloseOwnNoticeWindow();
        }

        private void Button_InfoConfirm(object sender, RoutedEventArgs e)
        {
            try
            {
                string strTextMonitorRoot = this.TBSyncPath.Text.Replace('/', '\\');
                string strCachePath = strTextMonitorRoot + MainWindow.DEFAULTCACHEDIR;

                try
                {

                    if (!CheckParam(strTextMonitorRoot))
                    {
                        return;
                    }

                    if (!Directory.Exists(strTextMonitorRoot))
                    {
                        Directory.CreateDirectory(strTextMonitorRoot);
                    }

                    string DEFAULT_QUICKGUIDE_FILENAME = (string)Application.Current.Resources["wizardFile"];
                    string strSQuickGuideFilePath = System.Environment.CurrentDirectory + "\\" + DEFAULT_QUICKGUIDE_FILENAME;
                    string strDQuickGuideFilePath = strTextMonitorRoot + "\\" + DEFAULT_QUICKGUIDE_FILENAME;

                    if (File.Exists(strSQuickGuideFilePath) && Directory.Exists(strTextMonitorRoot))
                    {
                        try
                        {
                            File.Copy(strSQuickGuideFilePath, strDQuickGuideFilePath, true);
                        }
                        catch { }
                    }
                }
                catch
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error,
                                                                        (string)Application.Current.Resources["syncPathSetErrr"], "");
                    return;
                }

                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_MONITOR_PATH_KEY, strTextMonitorRoot);
                thriftClient.updateConfigure();

                //读认证用户名 objectSID
                string authAccountGuid =  thriftClient.getUserId().ToString();
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

                this.Hide();
                //通知StorageService启动同步
                runNoticeWindow.CloseOwnNoticeWindow();
                IsClose = true;
                ParentWindow.AuthSucessAfterAction();
                Close();            
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Window_Closing_1(object sender, CancelEventArgs e)
        {
            if (!ParentWindow.IsExit && !IsClose)
            {
                this.Hide();
                e.Cancel = true;
            }
        }

        private bool IsIncludeSpecialCh(string strPath)
        {
            bool iRet = false;
          
            try
            {
                for (int i = 3; i < strPath.Length; i++)
                {
                    string strSub = strPath.Substring(i, 1);
                    char cResult = char.Parse(strSub);
                    int iResult = (int)cResult;
                    if (127 < iResult)
                    {
                        if (161 == iResult)
                        {
                            iRet = true;
                        }
                    }
                    else if (('a' <= cResult && 'z' >= cResult) || ('A' <= cResult && 'Z' >= cResult))
                        continue;
                    else if ('0' <= cResult && '9' >= cResult)
                        continue;
                    else if (' ' == cResult || '_' == cResult)
                        continue;
                    else if ('/' == cResult || '\\' == cResult)
                        continue;
                    else if ('(' == cResult || ')' == cResult)
                        continue;
                    else
                        iRet = true;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

           return iRet;
        }

        private bool IsInvalidPath(string strPath)
        {
            bool iRet = false;
            try
            {
                strPath = strPath.Replace('/', '\\');

                if (3 > strPath.Length)
                {
                    iRet = true;
                }
                else if (!strPath.Substring(1, 2).Equals(":\\"))
                {
                    iRet = true;
                }
                else if ((strPath[0] < 'a' || strPath[0] > 'z') && (strPath[0] < 'A' || strPath[0] > 'Z'))
                {
                    iRet = true;
                }
                else if (3 < strPath.Length)
                {
                    for (int i = 3; i < strPath.Length; i++)
                    {
                        string strSub = strPath.Substring(i, 1);
                        string strSubTemp = strPath.Substring(i - 1, 2);
                        char cResult = char.Parse(strSub);
                        int iResult = (int)cResult;
                        if ('\\' == cResult)
                        {
                            if ("\\\\" == strSubTemp)
                            {
                                iRet = true;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    iRet = true;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                iRet = true;
            }

            return iRet;
        }

        private bool CheckParam(string strPath)
        {
            try
            {
                if (100 < strPath.Length)
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["pathTooLong"], "");
                    return false;
                }

                if ("" == strPath)
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["noSyncPath"], "");
                    return false;
                }

                if (IsInvalidPath(strPath))
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["illegalSyncPath"], "");
                    return false;
                }

                if (IsIncludeSpecialCh(strPath))
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["SyncPathSupportCh"] + strPath + (string)Application.Current.Resources["illegalSyncPath2"], "");
                    return false;
                }

                string strRootInfo = Directory.GetDirectoryRoot(strPath);
                string FreeSpace = "";

                try
                {
                    System.Management.SelectQuery selectQuery = new System.Management.SelectQuery("select * from win32_logicaldisk");
                    System.Management.ManagementObjectSearcher searcher = new System.Management.ManagementObjectSearcher(selectQuery);
                    foreach (System.Management.ManagementObject disk in searcher.Get())
                    {
                        if ((strRootInfo.TrimEnd('\\')).ToUpper() == disk["Name"].ToString())
                        {
                            if (!disk["DriveType"].ToString().Equals("3"))
                            {
                                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["networkSyncPath"], "");
                                return false;
                            }

                            FreeSpace = disk["FreeSpace"].ToString();
                            break;
                        }
                    }

                    if ("" == FreeSpace)
                    {
                        runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["noExistDiskHead"] +" "+ strRootInfo.Substring(0, 1) +" " + (string)Application.Current.Resources["noExistDiskEnd"], "");
						return false;
					}

                    if (long.Parse(FreeSpace) < 524288000)
                    {
                        runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["choiceSyncRoot"], NoticeType.Error, (string)Application.Current.Resources["lackOfSpaceHead"] +" " + strRootInfo.Substring(0, 1) + " " + (string)Application.Current.Resources["lackOfSpaceEnd"], "");
                        return false;
                    }
                }
                catch (System.Exception ex)
                {
                    App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                }

            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }

            return true;
        }
    }
}
