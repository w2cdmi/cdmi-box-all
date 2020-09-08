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
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Collections.ObjectModel;
using System.Drawing;

using Onebox.ProcessManager;
using Onebox.ZipHelp;
using Onebox.MsgDefine;
using Onebox.NSTrayIconStatus;
using Onebox.RegisterValue;
using Onebox.Update;
using Onebox.SyncDirTreeView;
using Onebox.XmlDocumentOperation;

namespace Onebox
{
    public partial class MainWindow : Window
    {
        IntPtr WndProc(IntPtr hwnd, int msg, IntPtr wParam, IntPtr lParam, ref bool handled)
        {
            try
            {
                /// <summary>
                ///为避免屏幕解锁后出现界面假死，处理了屏幕锁定消息（.net 3.5的BUG）
                /// </summary>
                if (msg == Messager.WM_WTSSESSION_CHANGE)
                {
                    if (wParam.ToInt32() == Messager.WTS_SESSION_LOCK)
                    {
                        if (this.Visibility == Visibility.Hidden)
                        {
                            IsShowMainWindow = true;
                            this.ShowInTaskbar = false;
                            this.Opacity = 0;
                            this.Show();
                        }
                    }
                    else if (wParam.ToInt32() == Messager.WTS_SESSION_UNLOCK)
                    {
                        Thread.Sleep(1000);
                        if (IsShowMainWindow)
                        {
                            IsShowMainWindow = false;
                            this.Hide();
                        }
                    }
                }

                if (msg == Messager.WM_POWERBROADCAST)
                {
                    if (wParam.ToInt32() == Messager.PBT_APMSUSPEND)
                    {
                        try
                        {
                            App.Log.Info("The computer will sleep");
                            if (this.Visibility == Visibility.Hidden)
                            {
                                IsShowMainWindow = true;
                                this.ShowInTaskbar = false;
                                this.Opacity = 0;
                                this.Show();
                            }

                            StopThreadAndResettingMark(true);
                            processManager.StopAll();
                            ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                            App.Log.Info("before sleep stop all server succeed.");
                        }
                        catch (Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                        }
                    }
                    else if (wParam.ToInt32() == Messager.PBT_APMRESUMEAUTOMATIC)
                    {
                        try
                        {
                            App.Log.Info("The computer has been wake up.");
                            Thread.Sleep(1000);

                            if (IsShowMainWindow)
                            {
                                IsShowMainWindow = false;
                                this.Hide();
                            }

                            string strSyncModel = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY);
                            if (strSyncModel == ((int)SyncModel.Sync_Local).ToString() || strSyncModel == "")
                            {
                                this.CBFileDownloadPolicy.IsChecked = false;
                            }
                            else
                            {
                                this.CBFileDownloadPolicy.IsChecked = true;
                            }

                            for (int i = 0; i < 10; i++)
                            {
                                StopThreadAndResettingMark(true);
                                processManager.StopAll();
                                ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                                if (StartServer(false))
                                {
                                    App.Log.Info("resume server succeed.");
                                    break;
                                }
                                Thread.Sleep(1000);
                            }
                        }
                        catch (Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                        }
                    }
                }

                if (msg == Messager.WM_COPYDATA)
                {
                    CopyDataStruct data = (CopyDataStruct)System.Runtime.InteropServices.Marshal.PtrToStructure(lParam, typeof(CopyDataStruct));
                    Msg rMsg = (Msg)System.Runtime.InteropServices.Marshal.PtrToStructure(data.lpData, typeof(Msg));
                    int type = data.dwData.ToInt32();
                    handled = true;

                    if (type == Messager.NOTIFY_MSG_CHANGE_WORK_MODE)
                    {
                        UpdateServerStatus(rMsg.msg1);
                    }
                    else if (type == Messager.NOTIFY_MSG_TRANS_TASK_INSERT)
                    {
                        InsertTask(rMsg.msg1, rMsg.msg2, rMsg.msg3, rMsg.msg4, rMsg.msg5);
                    }
                    else if (type == Messager.NOTIFY_MSG_TRANS_TASK_UPDATE)
                    {
                        UpdateTask(rMsg.msg1, rMsg.msg2, rMsg.msg3, rMsg.msg4, rMsg.msg5, rMsg.msg6);
                    }
                    else if (type == Messager.NOTIFY_MSG_TRANS_TASK_DELETE)
                    {
                        DeleteTask(rMsg.msg1, rMsg.msg2, rMsg.msg3);
                    }
                    else if (type == Messager.NOTIFY_MSG_MENU_SHARE_LINK)
                    {
                        RunShareLinkWindow(rMsg.msg1);
                    }
                    else if (type == Messager.NOTIFY_MSG_MENU_SHARE)
                    {
                        RunInviteShareWindow(rMsg.msg1);
                    }
                    else if (type == Messager.NOTIFY_MSG_MENU_LISTREMOTEDIR)
                    {
                        RunSyncDirTreeWindow(rMsg.msg1);
                    }
                    else if (type == Messager.NOTIFY_MSG_SPEED)
                    {
                        ShowTransferSpeed(rMsg.msg1, rMsg.msg2);
                    }
                    else if (type == Messager.NOTIFY_MSG_DIFF_CNT)
                    {
                        ShowDiffCntAndErrorCnt(rMsg.msg1, rMsg.msg2);
                    }
                    else if (type == Messager.NOTIFY_MSG_CNT_LIMIT)
                    {
                        DisposeCntLimit(rMsg.msg1, rMsg.msg2);
                    }
                    else if (type == Messager.NOTIFY_MSG_SCAN)
                    {
                        ShowScan(rMsg.msg1);
                    }
                    else if (type == Messager.NOTIFY_MSG_ERROR_CHANGED)
                    {
                        GetErrorList();
                    }
                    else if (type == Messager.NOTIFY_MSG_ROOT_CHANGE)
                    {
                        RootChange();
                    }
                    else if (type == Messager.NOTIFY_MSG_SHOW_TRANSTASKS)
                    {
                        ShowTransTasksTable();
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return hwnd;
        }

        /// <summary>
        /// 用于更新服务端状态消息消息处理 
        /// </summary>
        /// <returns></returns>
        private void UpdateServerStatus(string strServerStauts)
        {
            try
            {
                if (IsLogin)
                {
                    if ((int)ThriftClient.Service_Status.Service_Status_Online == int.Parse(strServerStauts))
                    {
                        this.SyncMenu.IsEnabled = true;
                        varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE, int.Parse(strServerStauts));
                        SettingSyncingAndFailedForTrayIcon();
                        CheckPauseSyncStauts();
                        ShowTrayIcon(TrayIconStatus.ONLINE_ICO);
                    }
                    else if ((int)ThriftClient.Service_Status.Service_Status_Offline == int.Parse(strServerStauts))
                    {
                        varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE, int.Parse(strServerStauts));
                        ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                    }
                    else if ((int)ThriftClient.Service_Status.Service_Status_Error == int.Parse(strServerStauts))
                    {
                        varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE, int.Parse(strServerStauts));
                        ShowTrayIcon(TrayIconStatus.ERROR_ICO);
                    }
                    else if ((int)ThriftClient.Service_Status.Service_Status_Pause == int.Parse(strServerStauts))
                    {
                        this.SyncMenu.IsEnabled = true;
                        varRegValue.WriteReg(RegValue.REG_ROOT_HKLM, RegValue.REG_APPINFO_SUB_KEY, RegValue.REG_LOGIN_STATE, int.Parse(strServerStauts));
                        ShowTrayIcon(TrayIconStatus.SUSPEND_ICO);
                    }

                    ConvertTransShow();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        ///插入任务消息处理 
        /// </summary>
        /// <returns></returns>
        private void InsertTask(string TaskId, string strGroup, string strTransType, string strFileSize, string strPath)
        {
            try
            {
                if (strTransType == "" || strFileSize == "")
                {
                    App.Log.Error("Insert task type is empty or file size is empty.");
                    return;
                }

                foreach (MyTask mytask in tasks)
                {
                    int iTaskType = int.Parse(strTransType);
                    if (mytask.Id == TaskId && mytask.TaskGroup == strGroup && (int)mytask.TaskType == iTaskType)
                    {
                        mytask.FileSize = long.Parse(strFileSize);
                        return;
                    }
                }

                MyTask task = null;
                int iTransType = int.Parse(strTransType);
                if (iTransType == (int)AsyncTaskType.ATT_Upload)
                {
                    task = new MyTask(TaskId, strGroup, AsyncTaskType.ATT_Upload, strPath, long.Parse(strFileSize), DateTime.Now);
                }
                else if (iTransType == (int)AsyncTaskType.ATT_Upload_Manual)
                {
                    task = new MyTask(TaskId, strGroup, AsyncTaskType.ATT_Upload_Manual, strPath, long.Parse(strFileSize), DateTime.Now);
                }
                else if (iTransType == (int)AsyncTaskType.ATT_Download)
                {
                    task = new MyTask(TaskId, strGroup, AsyncTaskType.ATT_Download, strPath, long.Parse(strFileSize), DateTime.Now);
                }
                else if (iTransType == (int)AsyncTaskType.ATT_Download_Manual)
                {
                    task = new MyTask(TaskId, strGroup, AsyncTaskType.ATT_Download_Manual, strPath, long.Parse(strFileSize), DateTime.Now);
                }
                else if (iTransType == (int)AsyncTaskType.ATT_Upload_Attachements)
                {
                    task = new MyTask(TaskId, strGroup, AsyncTaskType.ATT_Upload_Attachements, strPath, long.Parse(strFileSize), DateTime.Now);
                }
                else
                {
                    return;
                }
              
                AddandDeleteTransList(task, System.Collections.Specialized.NotifyCollectionChangedAction.Add);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        ///任务状态更新消息处理 
        /// </summary>
        /// <returns></returns>
        private void UpdateTask(string strTaskId, string strGroup, string strTaskType, string strProgress, string strPath, string strSize)
        {
            if (strTaskType == "" || strProgress == "")
            {
                App.Log.Error("Task type is empty or update progress is empty.");
                return;
            }

            int iTaskType = int.Parse(strTaskType);
            try
            {
                Boolean isFind = false;
                for (int i = 0; i < tasks.Count; i++)
                {
                    if (tasks.ElementAt(i).Id == strTaskId && tasks.ElementAt(i).TaskGroup == strGroup && (int)tasks.ElementAt(i).TaskType == iTaskType)
                    {
                        isFind = true;
                        if (double.Parse(strProgress) > 1.0)
                        {
                            tasks.ElementAt(i).Progress = 1.0 * 120;
                        }
                        else
                        {
                            tasks.ElementAt(i).Progress = double.Parse(strProgress) * 120;
                        }

                        tasks.ElementAt(i).CreateorUpdateTime = DateTime.Now;
                    }
                }
                if (!isFind && double.Parse(strProgress) < 1.0)
                {
                    InsertTask(strTaskId, strGroup, strTaskType, strSize, strPath);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error("The Progress is " + strProgress);
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        ///任务删除消息处理 
        /// </summary>
        /// <returns></returns>
        private void DeleteTask(string strTaskId, string strGroup, string strTaskType)
        {
            try
            {
                int iTaskType = int.Parse(strTaskType);
                for (int i = 0; i < tasks.Count; i++)
                {
                    if (tasks.ElementAt(i).Id == strTaskId && tasks.ElementAt(i).TaskGroup == strGroup && (int)tasks.ElementAt(i).TaskType == iTaskType)
                    {
                        AddandDeleteTransList(tasks.ElementAt(i), System.Collections.Specialized.NotifyCollectionChangedAction.Remove);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        // <summary>
        /// 运行共享外链窗口
        /// </summary>
        private void RunShareLinkWindow(string FilePah)
        {
            if (LSWindow == null || this.LSWindow.IsLoaded == false)
            {
                LSWindow = new LinkShare(this);
                ChildWindowCollection.Add(LSWindow);
            }

            try
            {
                if (!common.IsDir(FilePah))
                {
                    LSWindow.linkShareData.FilePath = FilePah;
                    Icon icon = common.GetFileIcon(@FilePah);
                    ImageSource imageSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHIcon(icon.Handle, Int32Rect.Empty, BitmapSizeOptions.FromEmptyOptions());
                    LSWindow.Shmgr_image_fileIcon.Source = imageSource;
                }
                else
                {
                    LSWindow.linkShareData.FilePath = FilePah;
                    Icon icon = common.GetIcon(IconRelativePath.DocumentIconPath);
                    ImageSource imageSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHIcon(icon.Handle, Int32Rect.Empty, BitmapSizeOptions.FromEmptyOptions());
                    LSWindow.Shmgr_image_fileIcon.Source = imageSource;
                }

                //获取外链：有将返回已有数据，若没有则创建. 
                string effectTimeStr = "";
                string expireTimeStr = "";
                LSWindow.ShareLinkInfo = thriftClient.getShareLink(FilePah);

                //初始化界面信息
                if ("" == LSWindow.ShareLinkInfo.Url)
                {
                    LSWindow.runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["linkShare"], NoticeType.Error, (string)Application.Current.Resources["getLinkFailedInfo"], "");
                    return;
                }
                else
                {
                    LSWindow.linkShareData.LinkURL = LSWindow.ShareLinkInfo.Url;

                    if (LSWindow.ShareLinkInfo.EffectAt != 0)
                    {
                        LSWindow.ckb_CusPeriod.IsChecked = true;
                        DateTime dtBase = TimeZone.CurrentTimeZone.ToLocalTime(new System.DateTime(1970, 1, 1));
                        //日期、时间、时区：注意跨时区时间处理
                        long effect = LSWindow.ShareLinkInfo.EffectAt;
                        DateTime effectTime = dtBase.AddMilliseconds(effect);
                        effectTimeStr = string.Format(DATETIME_FORMART_STRING, effectTime);
                        LSWindow.Dpk_Start.Text = effectTimeStr;

                        if (LSWindow.ShareLinkInfo.ExpireAt != 0)
                        {
                            long expire = LSWindow.ShareLinkInfo.ExpireAt;
                            DateTime expireTime = dtBase.AddMilliseconds(expire);
                            expireTimeStr = string.Format(DATETIME_FORMART_STRING, expireTime);
                            LSWindow.Dpk_End.Text = expireTimeStr;
                        }
                        else
                        {
                            expireTimeStr = "";
                            LSWindow.Dpk_End.Text = expireTimeStr;
                        }
                    }
                    else
                    {
                        LSWindow.Dpk_Start.Text = "";
                        LSWindow.Dpk_End.Text = "";
                        LSWindow.ckb_CusPeriod.IsChecked = false;
                    }

                    if (LSWindow.ShareLinkInfo.AccessCode != "")
                    {
                        //注：多选框赋值在前，提取码赋值在后。
                        LSWindow.linkShareData.IsNeedCode = true;
                        LSWindow.linkShareData.ReadCode = LSWindow.ShareLinkInfo.AccessCode;
                    }
                    else
                    {
                        LSWindow.linkShareData.IsNeedCode = false;
                    }
                }

                LSWindow.runNoticeWindow.CloseOwnNoticeWindow();
                LSWindow.CloseChildElementNotice();
                LSWindow.linkLimitInfoShow(LSWindow.linkShareData.ReadCode, effectTimeStr, expireTimeStr);
                LSWindow.UIElementDefaultShow(); //初始化界面展示
                LSWindow.Show();
                LSWindow.Activate();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 运行邀请共享人窗口
        /// </summary>
        public void RunInviteShareWindow(string FilePah)
        {
            if (ShareWindow == null || this.ShareWindow.IsLoaded == false)
            {
                ShareWindow = new Share(this);
                ChildWindowCollection.Add(ShareWindow);
            }

            try
            {
                //show元素准备
                ShareWindow.shareData.OpenFilePath = FilePah;
                if (!common.IsDir(FilePah))
                {
                    Icon icon = common.GetFileIcon(@FilePah);
                    ImageSource imageSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHIcon(icon.Handle, Int32Rect.Empty, BitmapSizeOptions.FromEmptyOptions());
                    ShareWindow.Shmgr_image_IconFolder.Source = imageSource;
                }
                else
                {
                    Icon icon = common.GetIcon(IconRelativePath.DocumentIconPath);
                    ImageSource imageSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHIcon(icon.Handle, Int32Rect.Empty, BitmapSizeOptions.FromEmptyOptions());
                    ShareWindow.Shmgr_image_IconFolder.Source = imageSource;
                }

                ShareWindow.refreshRecipientsInfo();

                //显示区域控制
                ObservableCollection<MySharePerson> MySharedPersions = ShareWindow.MySharedUsers;
                if (MySharedPersions.Count > 0)
                {
                    ShareWindow.hasRecipients = true;
                    ShareWindow.UI_RecipientShow();
                }
                else
                {
                    ShareWindow.hasRecipients = false;
                    ShareWindow.UI_inviteShareShow();
                }

                ShareWindow.runNoticeWindow.CloseOwnNoticeWindow();
                ShareWindow.Show();
                ShareWindow.Activate();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void RunSyncDirTreeWindow(string FilePah)
        {
            if (null == syncDirTree || false == syncDirTree.IsLoaded)
            {
                syncDirTree = new SyncDirTree(FilePah);
                ChildWindowCollection.Add(syncDirTree);
            }

            syncDirTree.Show();
            syncDirTree.Activate();
        }

        /// <summary>
        ///传输列表速度显示
        /// </summary>
        /// <returns></returns>
        private void ShowTransferSpeed(string UploadSpeed, string DownLoadSpeed)
        {
            string strSize = "0";
            string strUnit = "B";
            this.uploadSpeed.Content = strSize;
            this.uploadSpeedUnit.Content = strUnit + "/s";
            if ("" != UploadSpeed)
            {
                common.ConvertSpaceSize(UploadSpeed, ref strSize, ref strUnit);
                this.uploadSpeed.Content = strSize;
                this.uploadSpeedUnit.Content = strUnit + "/s";
            }
            if ("" != DownLoadSpeed)
            {
                common.ConvertSpaceSize(DownLoadSpeed, ref strSize, ref strUnit);
                this.downloadSpeed.Content = strSize;
                this.downloadSpeedSpeedUnit.Content = strUnit + "/s";
            }
        }
        /// <summary>
        /// 显示待同步的文件或文件夹数量
        /// </summary>
        /// <returns></returns>
        private void ShowDiffCntAndErrorCnt(string strDiffCnt, string strErrorCnt)
        {
            if (strErrorCnt == "0")
            {
                Errors.Clear();
            }

            this.LbFileNum.Content = strDiffCnt;
            this.LbErrorNum.Content = strErrorCnt;

            ConvertTransShow();
            SettingSyncingAndFailedForTrayIcon();
        }


        private void DisposeCntLimit(string strLimitCnt, string strCurCnt)
        {
            try
            {
                if (IsNoticeCntLimit)
                {
                    App.Log.Info("the user number of files over limit.");
                    IsNoticeCntLimit = false;

                    if ((string)this.SyncMenu.Header == (string)Application.Current.Resources["syncPause"])
                    {
                        PauseSync();
                    }

                    StopThreadAndResettingMark(true);
                    processManager.StopAll();
                    runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["OneboxSysInfo"], NoticeType.Info, (string)Application.Current.Resources["FileCntLimitHead"] + " " + strCurCnt + " " + (string)Application.Current.Resources["FileCntLimitBetween"] + strLimitCnt + (string)Application.Current.Resources["FileCntLimitTail"], "");
                    AbnormalExit();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void ShowScan(string strStaus)
        {
            if (strStaus == "begin")
            {
                IsScanning = true;
                int iNum = 1;
                System.Windows.Threading.DispatcherTimer _timer = new System.Windows.Threading.DispatcherTimer();
                _timer.Interval = new TimeSpan(0, 0, 1);
                _timer.Tick += (s, e) =>
                {
                    string strPoint = "";
                    strPoint = common.GetPiontShow(iNum);
                    this.LBPoint.Content = strPoint;
                    iNum++;

                    if (iNum == 7)
                    {
                        iNum = 1;
                    }
                };
                _RecScanTimer = _timer;
                _timer.Start();
            }
            else if (strStaus == "end")
            {
                _RecScanTimer.Stop();
                IsScanning = false;
            }

            ConvertTransShow();
        }

        private void GetErrorList()
        {
            changeFlag = true;
        }

        private void RootChange()
        {
            runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["appNameTitle"], NoticeType.Info, (string)Application.Current.Resources["rootChangeMsg"], "");
            string authUserName = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);

            this.Hide();
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
            catch { }

            string strSyncModel = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_CONFIGURE_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_SYNC_MODEL_KEY);
            if (strSyncModel == ((int)SyncModel.Sync_Local).ToString() || strSyncModel == "")
            {
                this.CBFileDownloadPolicy.IsChecked = false;
            }
            else
            {
                this.CBFileDownloadPolicy.IsChecked = true;
            }

            for (int i = 0; i < 10; i++)
            {
                ShowTrayIcon(TrayIconStatus.OFFLINE_ICO);
                StopThreadAndResettingMark(true);
                processManager.StopAll();
                if (!common.DeleteUserData())
                {
                    Thread.Sleep(1000);
                    continue;
                }

                IsOpenMonitorDir = false;
                IsCheckUpgrade = false;
                IsSkipUseWizard = true;
                if (StartServer(false))
                {
                    App.Log.Info("resume server succeed in RootChange.");
                    break;
                }

                Thread.Sleep(1000);
            }
        }
        
        private void ShowTransTasksTable()
        {
            this.TransTab.IsSelected = true;
            this.ShowInTaskbar = true;
            this.Opacity = 1;
            this.WindowState = WindowState.Normal;
            this.Show();
            this.Activate();
        }
    }
}
