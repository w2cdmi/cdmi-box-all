using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using Microsoft.Win32;
using System.Security.Cryptography;
using System.ComponentModel;
using System.IO;
using System.Threading;
using Onebox.NSTrayIconStatus;
using Onebox.RegisterValue;

namespace Onebox
{
	/// <summary>
	/// login.xaml 的交互逻辑
	/// </summary>
    public partial class LoginWindow : Window
    {
        MainWindow ParentWindow;
        private RunNoticeWindow runNoticeWindow  = new RunNoticeWindow();
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private RegValue varRegValue = new RegValue();
        private Common common = new Common();
        private bool IsRetKeyDown;
        private bool IsClose;

        public LoginWindow(MainWindow parentWindow)
        {
            try
            {
                ParentWindow = parentWindow;
                this.InitializeComponent();
                Init();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Init()
        {
            IsRetKeyDown = true;
            IsClose = false;

            string strUserName = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
            string strPassword = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY);
            string strRemPassword = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_REMPASSWORD_KEY);
            string strAutoLogin = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_AUTOLOGINE_KEY);

            if ("" == strUserName)
            {
                SolidColorBrush myBrush = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xBB, 0x66, 0x66, 0x66));
                this.TBUserName.Foreground = myBrush;
                this.TBUserName.Text = (string)Application.Current.Resources["userNameNotice"];
            }
            else
            {
                this.TBUserName.Text = strUserName;
            }

            if (strRemPassword == ((int)RemeberPsd.Yes).ToString() && strRemPassword != "")
            {
                this.cb_rempsd.IsChecked = true;
                this.TBPassWord.Password = thriftClient.decyptString(strPassword);
            }
            else
            {
                this.cb_rempsd.IsChecked = false;
            }

            if (strAutoLogin == ((int)AutoLogin.Yes).ToString())
            {
                this.cb_autologin.IsChecked = true;
            }
            else
            {
                this.cb_autologin.IsChecked = false;
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

        private void Button_Close_Click(object sender, RoutedEventArgs e)
        {
            this.Hide();
            runNoticeWindow.CloseOwnNoticeWindow();
        }

        private void Button_LoginOK_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (!ParentWindow.IsAuthSuccess)
                {
                    string strUserName = this.TBUserName.Text;
                    string strPassWord = this.TBPassWord.Password;

                    int iRet = (int)RetunCode.Success;
                    this.LoginOK.IsEnabled = false;
                    this.LoginCancel.IsEnabled = false;
                    IsRetKeyDown = false;

                    this.Cursor = Cursors.Wait;
                    this.TBUserName.IsEnabled = false;
                    this.TBPassWord.IsEnabled = false;
                    string strNotice = (string)Application.Current.Resources["userNameNotice"];
                    if ("" == strUserName || "" == strPassWord || strNotice == strUserName)
                    {
                        runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["loninInfoEmpty"], "");
                        this.LoginOK.IsEnabled = true;
                        this.LoginCancel.IsEnabled = true;
                        IsRetKeyDown = true;
                        this.Cursor = Cursors.Arrow;
                        this.TBUserName.IsEnabled = true;
                        this.TBPassWord.IsEnabled = true;
                        return;
                    }

                    this.Dispatcher.BeginInvoke(new Action(() =>
                    {
                        try
                        {
                            int ServerStatus = thriftClient.getServiceStatus();

                            if (ServerStatus == (int)ThriftClient.Service_Status.Service_Status_Offline)
                            {
                                iRet = Auth_Offline_Login(strUserName, strPassWord);
                            }
                            else if (ServerStatus == (int)ThriftClient.Service_Status.Service_Status_Uninitial)
                            {
                                iRet = Auth_Online_Login(strUserName, strPassWord);
                            }
                            else
                            {
                                iRet = (int)RetunCode.ServiceError;
                                ParentWindow.ShowTrayIcon(TrayIconStatus.ERROR_ICO);
                                LoginFailedUIShow();
                                return;
                            }

                            if ((int)RetunCode.CouldntConnect == iRet)
                            {
                                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["couldntConnect"], "");
                                LoginFailedUIShow();
                                return;
                            }

                            if ((int)RetunCode.CouldntResolvHost == iRet)
                            {
                                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["requestFailed"], "");
                                LoginFailedUIShow();
                                return;

                            }

                            if ((int)RetunCode.LockError == iRet)
                            {
                                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["loginLockError"], "");
                                LoginFailedUIShow();
                                return;

                            }

                            if ((int)RetunCode.BindError == iRet)
                            {
                                runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["userErr"], NoticeType.Info,
                                        (string)Application.Current.Resources["currentUser"] + " " + strUserName + " " + (string)Application.Current.Resources["bindingUserErr"], "");
                                this.Hide();
                                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USER_LOGINTYPE_KEY, "");
                                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY, "");
                                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY, "");
                                ParentWindow.AbnormalExit();
                            }

                            else if ((int)RetunCode.Success != iRet)
                            {
                                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["loginFailedInfo"], "");
                                LoginFailedUIShow();
                                return;
                            }

                            ParentWindow.strRecordUserName = strUserName;
                            Hide();
                            ParentWindow.AuthSucessAction(true);
                            IsClose = true;
                            Close();
                            runNoticeWindow.CloseOwnNoticeWindow();
                        }
                        catch (Exception ex)
                        {
                            App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                            runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["login"], NoticeType.Error, (string)Application.Current.Resources["requestFailed"], "");
                            LoginFailedUIShow();
                            return;
                        }
                    }), System.Windows.Threading.DispatcherPriority.SystemIdle, null);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                LoginFailedUIShow();
                return;
            }
        }

        private void Button_LoginCancel_Click(object sender, RoutedEventArgs e)
        {
            this.LoginOK.IsEnabled = true;
            this.Cursor = Cursors.Arrow;
            this.TBUserName.IsEnabled = true;
            this.TBPassWord.IsEnabled = true;
            this.Hide();
            runNoticeWindow.CloseOwnNoticeWindow();
        }

        protected override void OnKeyDown(KeyEventArgs e)
        {
            try
            {
                if (Key.Return == e.Key)
                {
                    this.LoginOK.Focus();

                    if (IsRetKeyDown)
                    {
                        IsRetKeyDown = false;
                        Button_LoginOK_Click(null, e);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void Window_Closing(object sender, CancelEventArgs e)
        {
            if (!ParentWindow.IsExit && !IsClose)
            {
                this.Hide();
                e.Cancel = true;
            }
        }

       
        /// <summary>
        ///离线登陆
        /// </summary>
        /// <param name="userName">输入参数,表示域账户</param>
        /// <param name="pwd">输入参数,表示域密码</param>
        /// <returns>0:登录成功  非零：登录失败</returns>
        private int Auth_Offline_Login(string userName,string pwd)
        {
            int iRet = (int)RetunCode.Success;
            try
            {
                //使用配置文件信息鉴权：
                string authNamd = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                   ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                string authPwd = ParentWindow.varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                                                                   ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY);
                string inputPwd = thriftClient.encyptString(pwd);

                if (!(authNamd.Equals(userName) && authPwd.Equals(inputPwd)))
                {
                    iRet = (int)RetunCode.DefaultError;
                    return iRet;
                }

                //设备关联关系
                if (!ParentWindow.CheckDeviceLink())
                {
                    //已关联，不匹配
                    iRet = (int)RetunCode.BindError;
                    if (ParentWindow.isFirstUse)
                    {
                        //未关联时， 离线显示为校验失败
                        iRet = (int)RetunCode.DefaultError;
                    }
                    return iRet;
                }
            }
            catch (System.Exception ex)
            {
                iRet = (int)RetunCode.DefaultError;
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return iRet;
        }

        /// <summary>
        ///在线登陆处理
        /// </summary>
        /// <param name="strUserName">输入参数,表示域账户</param>
        /// <param name="strPassWrod">输入参数,表示域密码</param>
        /// <returns>0:登录成功  非零：登录失败</returns>
        private int Auth_Online_Login(string strUserName,string strPassWrod )
        {
            int iRet = (int)RetunCode.Success;
            try
            {
                iRet = thriftClient.login((int)ThriftClient.Authen_Type.Authen_Type_Normal, strUserName, strPassWrod, "");

                if ((int)RetunCode.Success != iRet)
                {
                    ClearLoginSettings();
                    return iRet;
                }


                /// <summary>
                ///配合stroageService更改loginType, 通知更改
                /// </summary>
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                    ConfigureFileOperation.ConfigureFileRW.CONF_USER_LOGINTYPE_KEY, ((int)LoginType.Normal).ToString());
                thriftClient.updateConfigure();
                CheckLoginSettings();
                ParentWindow.IsAuthSuccess = true;
                if (!ParentWindow.CheckDeviceLink())
                {
                    if (ParentWindow.isFirstUse)
                    {
                        iRet = (int)RetunCode.Success;
                        return iRet;
                    }

                    iRet = (int)RetunCode.BindError;
                    return iRet;
                }
            }
            catch (System.Exception ex)
            {
                iRet = (int)RetunCode.DefaultError;
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
            
            return iRet;
        }

        private void CheckLoginSettings()
        {
            if (this.cb_rempsd.IsChecked == true)
            {
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                ConfigureFileOperation.ConfigureFileRW.CONF_USER_REMPASSWORD_KEY, ((int)RemeberPsd.Yes).ToString());
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                   ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY, thriftClient.encyptString(this.TBPassWord.Password));
            }
            else
            {
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                ConfigureFileOperation.ConfigureFileRW.CONF_USER_REMPASSWORD_KEY, ((int)RemeberPsd.No).ToString());
            }

            if (this.cb_autologin.IsChecked == true)
            {
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                    ConfigureFileOperation.ConfigureFileRW.CONF_USER_AUTOLOGINE_KEY, ((int)AutoLogin.Yes).ToString());
            }
            else
            {
                ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                   ConfigureFileOperation.ConfigureFileRW.CONF_USER_AUTOLOGINE_KEY, ((int)AutoLogin.No).ToString());
            }
        }

        private void ClearLoginSettings()
        {
            ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
                   ConfigureFileOperation.ConfigureFileRW.CONF_PASSWORD_KEY, "");
            ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
            ConfigureFileOperation.ConfigureFileRW.CONF_USER_REMPASSWORD_KEY, ((int)RemeberPsd.No).ToString());
            ParentWindow.varIniFileRW.IniFileWriteValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION,
            ConfigureFileOperation.ConfigureFileRW.CONF_USER_AUTOLOGINE_KEY, ((int)AutoLogin.No).ToString());
        }

        private void TBUserName_GotFocus(object sender, RoutedEventArgs e)
        {
            try
            {
                if (this.TBUserName.Text == (string)Application.Current.Resources["userNameNotice"])
                {
                    SolidColorBrush myBrush = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xFF, 0x33, 0x33, 0x33));
                    this.TBUserName.Foreground = myBrush;
                    this.TBUserName.Text = "";
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TBUserName_LostFocus(object sender, RoutedEventArgs e)
        {
            try
            {
                if (this.TBUserName.Text == "")
                {
                    SolidColorBrush myBrush = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xBB, 0x66, 0x66, 0x66));
                    this.TBUserName.Foreground = myBrush;
                    this.TBUserName.Text = (string)Application.Current.Resources["userNameNotice"];
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 登录失败后登陆界面显示
        /// </summary>
        private void LoginFailedUIShow()
        {
            this.LoginOK.IsEnabled = true;
            this.LoginCancel.IsEnabled = true;
            IsRetKeyDown = true;
            this.Cursor = Cursors.Arrow;
            this.TBUserName.IsEnabled = true;
            this.TBPassWord.IsEnabled = true;
            ParentWindow.ShowTrayIcon(TrayIconStatus.ERROR_ICO);
        }

        private void cb_rempsd_unchecked(object sender, RoutedEventArgs e)
        {
            this.cb_autologin.IsChecked = false;
        }

        private void cb_autologin_checked(object sender, RoutedEventArgs e)
        {
            this.cb_rempsd.IsChecked = true;
        }
    }
}