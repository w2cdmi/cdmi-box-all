using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.IO;
using System.Windows.Input;
using System.ComponentModel;
using System.Windows.Threading;
using System.Threading;
using Microsoft.Win32;
using System.Security.Cryptography;

using Onebox.NSTrayIconStatus;
using Onebox.RegisterValue;
using Onebox.TrayIcon;
using Microsoft.WindowsAPICodePack.Dialogs;

namespace Onebox
{
    public struct SaveDialogResult
    {
        public CommonFileDialogResult ret;
        public  string FileName;
    }

    public partial class MainWindow : Window
    {
        #region main window event
        protected override void OnInitialized(EventArgs e)
        {
            try
            {
                Init();
                ClearEnvironment();
                base.OnInitialized(e);
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

        private void Window_Loaded_1(object sender, RoutedEventArgs e)
        {
            try
            {
                Hide();
                System.Windows.Interop.WindowInteropHelper wndHelper = new System.Windows.Interop.WindowInteropHelper(this);
                UIHwnd = wndHelper.Handle;
                (PresentationSource.FromVisual(this) as System.Windows.Interop.HwndSource).AddHook(new System.Windows.Interop.HwndSourceHook(WndProc));
                StartServer(true);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 选项窗口关闭事件，处理在任务栏点击关闭隐藏选项窗口
        /// </summary>
        private void Window_Closing_1(object sender, CancelEventArgs e)
        {
            if (true != IsExit)
            {
                this.Hide();
                e.Cancel = true;
            }
        }

        /// <summary>
        /// 选项界面最小化按钮点击事件
        /// </summary>
        private void Button_Min(object sender, RoutedEventArgs e)
        {
            this.WindowState = WindowState.Minimized;
            RecordWindowState =(int) WindowState.Minimized;
        }

        /// <summary>
        /// 选项界面最大化按钮点击事件
        /// </summary>
        private void Button_Max(object sender, RoutedEventArgs e)
        {
            SetWindowToMaximized();
        }

        private void RestoreButton_Clicked(object sender, RoutedEventArgs e)
        {
            SetWindowToNormal();
        }

        private void OptionWd_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            try
            {
                if (this.ActualHeight > SystemParameters.WorkArea.Height || this.ActualWidth > SystemParameters.WorkArea.Width)
                {
                    this.WindowState = System.Windows.WindowState.Normal;
                    SetWindowToMaximized();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 选项界面关闭按钮点击事件
        /// </summary>
        private void Button_Close(object sender, RoutedEventArgs e)
        {
            this.Hide();
        }

        private void WindowHeader_Border_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ClickCount == 2)
            {
                 if (RecordWindowState == (int)WindowState.Normal)
                 {
                     SetWindowToMaximized();
                 }
                 else if (RecordWindowState == (int)WindowState.Maximized)
                {
                    SetWindowToNormal();
                }
            }
        }
        #endregion

        #region settings table event
        private void Button_RelieveSyncRelation_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                this.Button_RelieveSyncRelation.IsEnabled = false;
                string authUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                   ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                string deviceName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                   ConfigureFileOperation.ConfigureFileRW.CONF_PCNAME_KEY);

                runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["unbind2"],
                                                                    NoticeType.Ask,
                                                                    (string)Application.Current.Resources["unbindConfirm"],
                                                                    (string)Application.Current.Resources["unbindConfirm_Device"]
                                                                   + deviceName
                                                                   + (string)Application.Current.Resources["unbindConfirm_stetFolder"]);

                if (runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused == true)
                {
                    //检查绑定
                    string bindUserHash = "";
                    try
                    {
                        bindUserHash = (string)Registry.GetValue(RegValue.REG_BINDUSER_KEYNAME, "", "");
                        if ("" == bindUserHash || null == bindUserHash)
                        {
                            return;
                        }
                    }
                    catch
                    {
                        if ("" == bindUserHash)
                        {
                            return;
                        }
                    }

                    //检查绑定用户与认证用户是否相同；通常情况为相同；
                    SHA1 sha1 = new SHA1CryptoServiceProvider();
                    byte[] bytes_sha1_in = UTF8Encoding.Default.GetBytes(authUserName.ToLower());
                    byte[] bytes_sha1_out = sha1.ComputeHash(bytes_sha1_in);
                    string authUserHash = BitConverter.ToString(bytes_sha1_out);

                    //解除绑定,图标置灰，
                    try
                    {
                        Registry.ClassesRoot.DeleteSubKeyTree(RegValue.REG_BINDUSER_SUB_KEY);
                    }
                    catch
                    {
                        runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["unbind2"], NoticeType.Error, (string)Application.Current.Resources["unbindFailedInfo"], "");
                        this.Button_RelieveSyncRelation.IsEnabled = true;
                        return;
                    }

                    varRegValue.DeleteRegValue(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_INSTALL_TYPE);
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_LOGINTYPE_KEY, "");
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY, "");
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY, "");
                    varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY, ((int)SyncModel.Sync_All).ToString());
                    DeleteVirtualFolder();
                    runNoticeWindow.MDnoticeWindow.Close();
                    this.Exit();
                }
                else
                {
                    this.Button_RelieveSyncRelation.IsEnabled = true;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                this.Button_RelieveSyncRelation.IsEnabled = true;
            }
        }

        private void CBFileDownloadPolicy_Clicked(object sender, RoutedEventArgs e)
        {
            if (this.CBFileDownloadPolicy.IsChecked == true)
            {
                runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["openDownloadTitle"], NoticeType.Ask, (string)Application.Current.Resources["openDownloadMsg"], "");
                if (runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused == true)
                {
                    App.Log.Info("open file auto download begin.");
                    ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                    StopThreadAndResettingMark(true);
                    this.Hide();
                    if (processManager.StopAll())
                    {
                        try
                        {
                            string strUserData = System.Environment.CurrentDirectory + "\\" + DEFAULTUSERDATA;
                            if (Directory.Exists(strUserData))
                            {
                                Directory.Delete(strUserData, true);
                            }

                            varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY, ((int)SyncModel.Sync_All).ToString());
                        }
                        catch (System.Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                            this.CBFileDownloadPolicy.IsChecked = false;
                        }
                    }
                    if (!StartServer(false))
                    {
                        App.Log.Info("open file auto download failed");
                    }

                    App.Log.Info("open file auto download success.");
                }
                else
                {
                    e.Handled = true;
                    this.CBFileDownloadPolicy.IsChecked = false;
                }
            }
            else
            {
                runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["closeDownloadTitle"], NoticeType.Ask, (string)Application.Current.Resources["closeDownloadMsg"], "");
                if (runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused == true)
                {
                    App.Log.Info("close file auto download begin.");
                    ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                    StopThreadAndResettingMark(true);
                    this.Hide();
                    if (processManager.StopAll())
                    {
                        try
                        {
                            varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY, ((int)SyncModel.Sync_Local).ToString());
                        }
                        catch (System.Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                            this.CBFileDownloadPolicy.IsChecked = true;
                        }
                    }
                    if (!StartServer(false))
                    {
                        App.Log.Info("close file auto download failed");
                    }

                    App.Log.Info("close file auto download success.");
                }
                else
                {
                    e.Handled = true;
                    this.CBFileDownloadPolicy.IsChecked = true;
                }
            }
        }

        /// <summary>
        /// 选项窗口中设置页中的开机自动运行设置
        /// </summary>
        private void Auto_Run_Clicked(object sender, RoutedEventArgs e)
        {
            if (this.Setting_BootStartRun_CheckBox.IsChecked == true)
            {
                if (!settAutoRun())
                {
                    e.Handled = true;
                }
            }
            else
            {
                if (!cancelAutoRun())
                {
                    e.Handled = true;
                }
            }
        }
        #endregion

        #region translate table event
        private void sp_Analyse_IsVisibleChanged(object sender, DependencyPropertyChangedEventArgs earg)
        {
            if (this.sp_Analyse.IsVisible)
            {
                int iNum = 1;
                System.Windows.Threading.DispatcherTimer _timer = new System.Windows.Threading.DispatcherTimer();
                _timer.Interval = new TimeSpan(0, 0, 1);
                _timer.Tick += (s, e) =>
                {
                    string strPoint = "";
                    strPoint = common.GetPiontShow(iNum);
                    this.LBPoint_Analyse.Content = strPoint;
                    iNum++;

                    if (iNum == 7)
                    {
                        iNum = 1;
                    }
                };
                _RecAnalyseTimer = _timer;
                _timer.Start();
            }
            else
            {
                _RecAnalyseTimer.Stop();
            }
        }

        private void TextBlock_Translist_FilePath_Initialized(object sender, EventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.Width * 0.13);
                MyTask mytask = (MyTask)textblock.DataContext;
                if (Encoding.Default.GetBytes(mytask.SourcePath).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(mytask.SourcePath, fixedlength);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TextBlock_Translist_FilePath_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.ActualWidth * 0.13);
                MyTask mytask = (MyTask)textblock.DataContext;
                textblock.Text = common.CutFixedLengthString(mytask.SourcePath, fixedlength);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void tasks_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            try
            {
                  ConvertTransShow();
                  SettingSyncingAndFailedForTrayIcon();
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
        #endregion

        #region errorlist  table event
        private void ExportErrorList_Clicked(object sender, RoutedEventArgs e)
        {
            try
            {
                CommonSaveFileDialog FileExplorer = new CommonSaveFileDialog();
                FileExplorer.DefaultExtension = "csv";
                FileExplorer.DefaultFileName = "errorListData";
                CommonFileDialogFilter filter = new CommonFileDialogFilter();
                filter.Extensions.Add("csv files");
                filter.Extensions.Add("csv");
                FileExplorer.Filters.Add(filter);
                CommonFileDialogResult ret = FileExplorer.ShowDialog();
                SaveDialogResult saveRet = new SaveDialogResult();
                saveRet.ret = ret;
                saveRet.FileName = FileExplorer.FileName;
                Thread ExportErrorListThread = new Thread(new ParameterizedThreadStart(ExportErrorList));
                ExportErrorListThread.IsBackground = true;
                ExportErrorListThread.Start((object)saveRet);
                ThreadCollection.Add(ExportErrorListThread);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TexBlock_Errorlist_FilePath_Initialized(object sender, EventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.Width * 0.13);
                MyError myerror = (MyError)textblock.DataContext;

                if (Encoding.Default.GetBytes(myerror.FilePath).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(myerror.FilePath, fixedlength);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TexBlock_Errorlist_FilePath_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.ActualWidth * 0.13);
                MyError myerror = (MyError)textblock.DataContext;
                if (Encoding.Default.GetBytes(myerror.FilePath).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(myerror.FilePath, fixedlength);
                }
                else
                {
                    textblock.Text = myerror.FilePath;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }


        private void TextBlock_Errorlist_ErrorMsg_Initialized(object sender, EventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.Width * 0.13);
                MyError myerror = (MyError)textblock.DataContext;
                if (Encoding.Default.GetBytes(myerror.ErrorDes).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(myerror.ErrorDes, fixedlength);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
           
        private void TextBlock_Errorlist_ErrorMsg_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.ActualWidth * 0.13);
                MyError myerror = (MyError)textblock.DataContext;
                if (Encoding.Default.GetBytes(myerror.ErrorDes).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(myerror.ErrorDes, fixedlength);
                }
                else
                {
                    textblock.Text = myerror.ErrorDes;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TextBlock_ErrorList_Suggest_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            try
            {
                TextBlock tb = sender as TextBlock;
                MyError myerror = (MyError)tb.DataContext;
                string strFilePath = myerror.FilePath;
                if (strFilePath.Length != 0)
                {
                    if (strFilePath[0] == '\\' || 2 == e.ClickCount)
                    {
                        e.Handled = true;
                        return;
                    }
                }
                else
                {
                    e.Handled = true;
                    return;
                }

                strFilePath = strFilePath.Replace('/', '\\');
                DirectoryInfo dirinfo = Directory.GetParent(strFilePath);
                if ("" != strFilePath)
                {
                    if (File.Exists(strFilePath) || Directory.Exists(strFilePath))
                    {
                        string strArguments = "/n,/select," + strFilePath;
                        System.Diagnostics.Process.Start("explorer", strArguments);
                    }
                    else if (dirinfo.Exists)
                    {
                        string strArguments = "/n,/select," + dirinfo.FullName;
                        System.Diagnostics.Process.Start("explorer", strArguments);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TextBlock_ErrorList_Suggest_Initialized(object sender, EventArgs e)
        {
            try
            {
                TextBlock tb = sender as TextBlock;
                MyError myerror = (MyError)tb.DataContext;
                string strFilePath = myerror.FilePath.Replace('/', '\\');
                int fixedlength = Convert.ToInt32(tb.Width * 0.13);
                if (strFilePath.Length != 0)
                {
                    if (strFilePath[0] != '\\')
                    {
                        tb.Style = (Style)Application.Current.Resources["tx_style_textblock"];
                    }
                }

                if (Encoding.Default.GetBytes(myerror.Suggest).Length > fixedlength)
                {
                    tb.Text = common.CutFixedLengthString(myerror.Suggest, fixedlength);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TextBlock_ErrorList_Suggest_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            try
            {
                TextBlock textblock = sender as TextBlock;
                int fixedlength = Convert.ToInt32(textblock.ActualWidth * 0.13);
                MyError myerror = (MyError)textblock.DataContext;
                if (Encoding.Default.GetBytes(myerror.Suggest).Length > fixedlength)
                {
                    textblock.Text = common.CutFixedLengthString(myerror.Suggest, fixedlength);
                }
                else
                {
                    textblock.Text = myerror.Suggest;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Errors_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            try
            {
              if (0== Errors.Count)
              {
                  this.GD_FileNumAndExport.Visibility = Visibility.Hidden;
                  this.BDErrorListEmpty.Visibility = Visibility.Visible;
                  this.ErrorInfo.Visibility = Visibility.Hidden;
              }
              else
              {
                  this.GD_FileNumAndExport.Visibility = Visibility.Visible;
                  this.BDErrorListEmpty.Visibility = Visibility.Hidden;
                  this.ErrorInfo.Visibility = Visibility.Visible;
              }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        #endregion

        #region mainwindow menu event
        //         private void Button_Menu_Click(object sender, RoutedEventArgs e)
        //         {
        //             //this.MainMenu.IsOpen = true;
        //         }
        // 
        //         private void MainMenu_Help_Click(object sender, RoutedEventArgs e)
        //         {
        //             try
        //             {
        //                 string helpUri = (string)Application.Current.Resources["helpUrl"];
        //                 string webURL = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_NETWORK_SECTION,
        //                     ConfigureFileOperation.ConfigureFileRW.CONF_SERVER_URL_KEY);
        //                 if (webURL != "" && webURL.EndsWith(DEFAULTAPIVWESION))
        //                 {
        //                     int end = webURL.Length - DEFAULTAPIVWESION.Length;
        //                     webURL = webURL.Substring(0, end) + helpUri;
        //                     System.Diagnostics.Process.Start(webURL);
        //                 }
        //             }
        //             catch
        //             { }
        //         }
        // 
        //         private void MainMenu_About_Click(object sender, RoutedEventArgs e)
        //         {
        //             if (this.AboutWindow == null || this.AboutWindow.IsLoaded == false)
        //             {
        //                 AboutWindow = new About();
        //                 ChildWindowCollection.Add(AboutWindow);
        //             }
        // 
        //             if (this.AboutWindow.IsVisible != true)
        //             {
        //                 this.AboutWindow.Show();
        //                 this.AboutWindow.Activate();
        //             }
        //         }
        //      
        //         private void MainMenu_Exit_Click(object sender, RoutedEventArgs e)
        //         {
        //             this.Exit();
        //         }
        #endregion

        #region TrayIcon menu event
        private void Taskbar_SyncMenu_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string strTemp = (string)Application.Current.Resources["syncRecover"];
                if (strTemp == this.SyncMenu.Header.ToString())
                {
                    this.SyncMenu.IsEnabled = false;
                    IsStopSync = false;
                    StartSync();
                }
                else
                {
                    IsStopSync = true;
                    this.SyncMenu.IsEnabled = false;
                    PauseSync();
                }
            }
            catch (System.Exception ex)
            {
                this.SyncMenu.IsEnabled = true;
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Taskbar_OpenWebMenu_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string strWebERL = common.GetWebURL();
                if (strWebERL != "")
                {
                    System.Diagnostics.Process.Start(strWebERL);
                }
            }
            catch
            { }
        }

        private void Taskbar_CollectLogMenu_Click(object sender, RoutedEventArgs e)
        {
            this.CollectLogMenu.IsEnabled = false;
            CollectLogThead = new Thread(new ThreadStart(CollectLogTask));
            CollectLogThead.Start();
            ThreadCollection.Add(CollectLogThead);
        }

        //帮助界面
        private void Taskbar_HelpMenu_Click(object sender, RoutedEventArgs e)
        {
            //TODO::考虑语言不同时的处理；需与服务端约定
            try
            {

                string helpUri = (string)Application.Current.Resources["helpUrl"];
                string webURL = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_NETWORK_SECTION,
                    ConfigureFileOperation.ConfigureFileRW.CONF_SERVER_URL_KEY);

                if (webURL != "" && webURL.EndsWith(DEFAULTAPIVWESION))
                {
                    int end = webURL.Length - DEFAULTAPIVWESION.Length;
                    webURL = webURL.Substring(0, end) + helpUri;
                    System.Diagnostics.Process.Start(webURL);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Taskbar_AboutMenu_Click(object sender, RoutedEventArgs e)
        {
            if (this.AboutWindow == null || this.AboutWindow.IsLoaded == false)
            {
                AboutWindow = new About();
                ChildWindowCollection.Add(AboutWindow);
            }

            if (this.AboutWindow.IsVisible != true)
            {
                this.AboutWindow.Show();
                this.AboutWindow.Activate();
            }
        }

        private void Taskbar_OptionMenu_Click(object sender, RoutedEventArgs e)
        {
            this.TransTab.IsSelected = true;
            this.ShowInTaskbar = true;
            this.Opacity = 1;
            this.WindowState = WindowState.Normal;
            this.Show();
            this.Activate();
        }

        private void Taskbar_OpenSynDirMenu_Click(object sender, RoutedEventArgs e)
        {
            EnterSyncDir(true);
        }

        private void Taskbar_TaskBarMenu_Exit_Click(object sender, RoutedEventArgs e)
        {
            this.Exit();
        }
        #endregion

        #region TrayIcon  Mouse event
        /// <summary>
        /// 控制托盘图标单击事件，控制登录窗口、向导窗口和同步文件夹选择窗口的显示
        /// </summary>
        private void notifyIcon_TrayLeftMouseDown_1(object sender, RoutedEventArgs e)
        {
            if (IsExit)
            {
                return;
            }

            if (!IsLogin)
            {
                if (IsShowWorCWindow == false && IsShowLoginWindow && (null == this.confirmWindow || this.confirmWindow.IsLoaded == false))
                {
                    if (loginWindow == null || this.loginWindow.IsLoaded == false)
                    {
                        loginWindow = new LoginWindow(this);
                        ChildWindowCollection.Add(loginWindow);
                    }
                    this.loginWindow.Hide();
                    this.loginWindow.Activate();
                    this.loginWindow.Show();
                }
                else if (IsShowWorCWindow == true && isFirstUse)
                {
                    if (null == this.confirmWindow || this.confirmWindow.IsLoaded == false)
                    {
                        RunWizardWindow();
                    }
                    else
                    {
                        RunComfirmWindow();
                    }
                }
            }
            else if (trayIconManager.CrruentIcon.Equals(TrayIconStatus.FAILED_ICO))
            {
                this.Hide();
                this.ErrorListTab.IsSelected = true;
                this.ShowInTaskbar = true;
                this.Opacity = 1;
                this.Show();
                this.Activate();
            }
        }

        private void notifyIcon_TrayMouseDoubleClick_1(object sender, RoutedEventArgs e)
        {
            if (trayIconManager.CrruentIcon.Equals(TrayIconStatus.FAILED_ICO))
            {
                this.Hide();
                this.ErrorListTab.IsSelected = true;
                this.ShowInTaskbar = true;
                this.Opacity = 1;
                this.Show();
                this.Activate();
                return;
            }

            EnterSyncDir(true);
        }
        #endregion

        #region add control ID
        /// <summary>
        /// Add error list item ID
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ListViewItem_ErrorList_Loaded(object sender, RoutedEventArgs e)
        {
            try
            {
                ListViewItem item = sender as ListViewItem;
                MyError myerror = (MyError)item.DataContext;
                item.Uid = myerror.FilePath + "|" + myerror.TaskErrorCode + "|" + myerror.ErrorDes + "|" + myerror.Suggest;
            }
            catch { }
        }

        /// <summary>
        /// Add task list item ID
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ListViewItem_TaskList_Loaded(object sender, RoutedEventArgs e)
        {
            try
            {
                ListViewItem item = sender as ListViewItem;
                MyTask mytask = (MyTask)item.DataContext;
                item.Uid = mytask.TaskType + "|" + mytask.SourcePath + "|" + mytask.FileSize + "|" + mytask.Progress;
            }
            catch { }
        }
        #endregion
    }
}

