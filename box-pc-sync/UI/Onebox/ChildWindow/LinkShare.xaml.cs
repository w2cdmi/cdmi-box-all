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
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Drawing;
using Onebox.ThriftClient;
using System.Text.RegularExpressions;
using System.Collections.ObjectModel;
using System.Windows.Controls.Primitives;

namespace Onebox
{
    /// <summary>
    /// LinkShare.xaml 的交互逻辑
    /// </summary>
    public partial class LinkShare : Window
    {
        public LinkShareData linkShareData = null;
        private string effectTime_str = ""; //用于存放原始链接信息，供取消修改后，再进入时正确显示
        private string expireTime_str = "";
        private string accesscode_str = "";
        MainWindow ParentWindow = null;
        public string SearchKey;
        //private String currentUser = ""; //当前登陆用户
        private readonly Regex CodeMatch = new Regex("^[A-Za-z0-9]+$");
        public RunNoticeWindow runNoticeWindow = new RunNoticeWindow();
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private ObservableCollection<MySharePerson> MySharePersons=null;
        private ObservableCollection<MySharePerson> searchUsers = null; //域用户列表
        private MySharePerson mySelectedPerson = new MySharePerson(); //暂存：弹出层用户选择结果
        public ThriftClient.Share_Link_Info ShareLinkInfo = new ThriftClient.Share_Link_Info();
        private int invite_tb_num = 0;
        private const string msgFlag = "0";

        #region Override Method
        public LinkShare(MainWindow parentWindow)
        {
            try
            {
                ParentWindow = parentWindow;

                if (null == linkShareData)
                {
                    linkShareData = new LinkShareData();
                }

                this.DataContext = linkShareData;
                InitializeComponent();
                InitalInviteArea();
                this.lb_EmailPerson.ItemsSource = MySharePersons;
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
            CloseChildElementNotice();
            runNoticeWindow.CloseOwnNoticeWindow();
            base.OnClosed(e);
        }
        #endregion

        #region  UI events
        private void LinkShareWd_Loaded(object sender, RoutedEventArgs e)
        {
            this.Dpk_Start.ApplyTemplate();
            this.Dpk_End.ApplyTemplate();
            UIElementDefaultShow();
        }

        #endregion

        #region UI Element events
        /// <summary>
        /// 复制链接按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_copyLink(object sender, RoutedEventArgs e)
        {
            try
            {
                this.link_btn_copyLink.IsEnabled = false;
                Clipboard.SetText(textbox_fileLink.Text);
            }
            catch (System.Exception ex)
            {
                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["linkShare"], NoticeType.Error,
                         (string)Application.Current.Resources["copyErr"], (string)Application.Current.Resources["copyErrInfo"]);
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            this.link_btn_copyLink.IsEnabled = true;
        }

        /// <summary>
        /// 提取码复制事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_copyCode(object sender, RoutedEventArgs e)
        {
            try
            {
                Clipboard.SetText(textbox_code.Text);
            }
            catch (System.Exception ex)
            {
                runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["linkShare"], NoticeType.Error,
                        (string)Application.Current.Resources["copyErr"], (string)Application.Current.Resources["copyErrInfo"]);
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

       /// <summary>
       /// 按钮点击事件
       /// </summary>
       /// <param name="sender"></param>
       /// <param name="e"></param>
        private void btn_linkCancel(object sender, RoutedEventArgs e)
        {
            try
            {
                this.link_btn_linkCancel.IsEnabled = false;
                //TODO::调用thrift进行取消文件外链接；注意相关UI绑定信息要清空；
                runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["linkCancel"], NoticeType.Ask, 
                    (string)Application.Current.Resources["linkCancelConfirm"], "");

                if (runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused == true)
                {
                    runNoticeWindow.CloseOwnNoticeWindow();
                    link_btn_linkCancel.IsEnabled = true;

                    if (0 != thriftClient.delShareLink(linkShareData.FilePath))
                    {
                        runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["linkCancel"], NoticeType.Error, 
                            (string)Application.Current.Resources["CancelLinkFailed"], "");
                    }
                    
                    linkShareData.LinkURL = "";
                    linkShareData.ReadCode = "";
                    linkShareData.IsNeedCode = false;
                    linkShareData.DateStart = null;
                    linkShareData.DateStartTime = null;
                    linkShareData.DateEnd = null;
                    linkShareData.DateEndTime = null;
                    linkShareData.IsForever = true;
                    this.Close();
                }
               
                this.link_btn_linkCancel.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 保存按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Save_Link_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                // ThriftClient.Share_Link_Info ShareLinkInfo = new ThriftClient.Share_Link_Info();
                ShareLinkInfo.Url = linkShareData.LinkURL;
                ShareLinkInfo.AccessCode = "";
                ShareLinkInfo.EffectAt = -1;
                ShareLinkInfo.ExpireAt = -1;

                //拼串变量
                string strEffectTime = "";
                string strExpireTime = "";

                if (ckb_isNeedCode.IsChecked == true)
                {
                    if (0 == linkShareData.ReadCode.Trim().Length)
                    {
                        runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["readCodeEmpty"]);
                        return;
                    }

                    if ("" == thriftClient.getRandomString() && !CodeMatch.IsMatch(linkShareData.ReadCode))
                    {
                        runNoticeWindow.RunNoticeChildWindow(this, (string)Application.Current.Resources["CodeNotMach"]);
                        return;
                    }

                   linkShareData.ReadCode = TrimSpace(linkShareData.ReadCode);
                   ShareLinkInfo.AccessCode = linkShareData.ReadCode;
                }


                if (ckb_CusPeriod.IsChecked == true)
                {
                    //时间合法性检查
                    if (!checkLinkDate())
                    {
                        return;
                    }
                    DateTime dtStart = new DateTime();
                    DateTime dtEnd = new DateTime();
                    bool isStartPaseErr = DateTime.TryParse(Dpk_Start.Value, out dtStart);
                    bool isEndPaseErr  = DateTime.TryParse(Dpk_End.Value, out dtEnd);
                    if (!isStartPaseErr && "" != Dpk_End.Value && isEndPaseErr)
                    {
                        runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["dateTimeErr"]);
                        return;
                    }

                    //java计算时间为相对于1970,1,1年的毫秒
                    DateTime dtBase = TimeZone.CurrentTimeZone.ToLocalTime(new System.DateTime(1970, 1, 1));
                    //tick与java的毫秒计数单位相差倍数：10000
                    long effect = (dtStart.Ticks - dtBase.Ticks) / 10000;
                    ShareLinkInfo.EffectAt = effect;
                    strEffectTime = Dpk_Start.Text;
                    linkShareData.DateStartTime = strEffectTime;

                    if ("" != Dpk_End.Value)
                    {
                        long expire = (dtEnd.Ticks - dtBase.Ticks) / 10000;
                        ShareLinkInfo.ExpireAt = expire;
                        strExpireTime = Dpk_End.Text;
                    }
                    else
                    {
                        ShareLinkInfo.ExpireAt = 0;
                        strExpireTime = "";
                    }

                    linkShareData.DateEndTime = strExpireTime;
                }

                ShareLinkInfo = thriftClient.modifyShareLink(linkShareData.FilePath, ShareLinkInfo);
                if (ShareLinkInfo == null || ShareLinkInfo.Url == string.Empty)
                {
                    string titleStr = (string)Application.Current.Resources["linkShare"];
                    string errMsg = (string)Application.Current.Resources["updateFailedInfo"];
                    runNoticeWindow.RunNoticeChildWindow(this,errMsg);
                    return;
                }


                //修改成功后，界面显示切换；
                linkLimitInfoShow(ShareLinkInfo.AccessCode, strEffectTime, strExpireTime);
                UIElementDefaultShow();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 提取码刷新按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_refreshCode(object sender, RoutedEventArgs e)
        {
            try
            {
                this.link_btn_refreshCode.IsEnabled = false;
                linkShareData.ReadCode = thriftClient.getRandomString();
                this.link_btn_refreshCode.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// “修改”被点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_LimitChange_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                InitLimitAreaElementValue();
                VisitLimitAreaShow();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_linkChange_Cancel(object sender, RoutedEventArgs e)
        {
            try
            {
                CloseChildElementNotice();
                UIElementDefaultShow();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_link_finish(object sender, RoutedEventArgs e)
        {
           // runNoticeWindow.CloseOwnNoticeWindow();
            this.Close();
        }

        /// <summary>
        /// 提取码CheckBox点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void chk_needCode(object sender, RoutedEventArgs e)
        {
            try
            {
                //判断系统配置：链接提取码类型-复杂规则、简单规则
                //简单规则，用户手动输入任意1~20位(服务端返空串)；复杂规则：8位已返回提取码，只读，显示刷新按钮；
                if (this.ckb_isNeedCode.IsChecked == true)
                {
                    string code = thriftClient.getRandomString();
                    //判断:如果服务端返回空串，则为简单规则，有串则为复杂规则，需只读；
                    if (code.Length > 0)
                    {
                        textbox_code.IsReadOnly = true;
                        link_btn_refreshCode.Visibility = Visibility.Visible;
                        linkShareData.ReadCode = code;
                    }
                    else
                    {
                        textbox_code.IsReadOnly = false;
                        link_btn_refreshCode.Visibility = Visibility.Collapsed;
                        linkShareData.ReadCode = accesscode_str;
                    }
                }
                else
                {
                    linkShareData.ReadCode = "";
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
        #endregion

        #region Other Method
        /// <summary>
        /// 对外链访问控制信息做合法性检查，true：合法，false：不合法
        /// </summary>
        /// <returns>true：合法， false：不合法</returns>
        private bool checkLinkDate()
        {
            try
            {
                DateTime dtStart = new DateTime();
                DateTime dtEnd = new DateTime();
                bool isStartPaseErr = DateTime.TryParse(Dpk_Start.Value, out dtStart);
                bool isEndPaseErr = DateTime.TryParse(Dpk_End.Value, out dtEnd);

                if ("" == Dpk_Start.Value)
                {
                    runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                           (string)Application.Current.Resources["StartTimeBlank"], "");
                    return false;
                }

                if (!isStartPaseErr)
                {
                    runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                           (string)Application.Current.Resources["dateTimeErr"], "");
                    if (true == runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused)
                    {
                        this.Dpk_Start.Text = "";
                    }

                    return false;
                }
                else
                {
                    if (dtStart.AddMinutes(5) < DateTime.Now)
                    {
                        runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                          (string)Application.Current.Resources["dateTimeErr"], "");
                        if (true == runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused)
                        {
                            this.Dpk_Start.Text = "";
                        }

                        return false;
                    }
                }

                if ("" != Dpk_End.Value && !isEndPaseErr)
                {
                    runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                        (string)Application.Current.Resources["dateTimeErr"], "");

                    if (true == runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused)
                    {
                        this.Dpk_End.Text = "";
                    }

                    return false;
                }

                //更改需求：只检查结束时间要求
                if ("" != Dpk_End.Value && dtEnd <= dtStart)
                {
                    string titleStr = (string)Application.Current.Resources["timeError"];
                    string errMsg = (string)Application.Current.Resources["timeRule1"];
                    runNoticeWindow.RunNoticeChildWindow(this,errMsg);
                    return false;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }

            return true;
        }

       /// <summary>
       /// 外链界面的默认显示控件
       /// </summary>
        public void UIElementDefaultShow()
        {
            try
            {
                //显示区域：访问限制显示区，
                area_LinkLimit_Info.Visibility = Visibility.Visible;
                area_LinkLimit_change.Visibility = Visibility.Collapsed;
                area_Link_Email.Visibility = Visibility.Visible;
                if (MySharePersons.Count != 0 && MySharePersons.ElementAt(0).Email != "" &&  MySharePersons.ElementAt(0).Email != (string)Application.Current.Resources["emailInputnotice"])
                {
                    tb_invite_msg.Visibility = Visibility.Collapsed;
                    sp_Email_Person.Visibility = Visibility.Visible;
                    sp_Email_Users.Visibility = Visibility.Visible;

                    link_btn_linkCancel.Visibility = Visibility.Collapsed;
                    link_btn_finish.Visibility = Visibility.Collapsed;
                    link_btn_linkSet.Visibility = Visibility.Collapsed;
                    link_btn_Cancel.Visibility = Visibility.Collapsed;
                    link_btn_send.Visibility = Visibility.Visible;
                    btn_cancelsend.Visibility = Visibility.Visible;
                }
                else
                {
                    tb_invite_msg.Visibility = Visibility.Visible;
                    sp_Email_Person.Visibility = Visibility.Collapsed;
                    sp_Email_Users.Visibility = Visibility.Collapsed;
                    link_btn_linkCancel.Visibility = Visibility.Visible;
                    link_btn_finish.Visibility = Visibility.Visible;
                    link_btn_linkSet.Visibility = Visibility.Collapsed;
                    link_btn_Cancel.Visibility = Visibility.Collapsed;
                    link_btn_send.Visibility = Visibility.Collapsed;
                    btn_cancelsend.Visibility = Visibility.Collapsed;
                }

                //按钮：取消(放弃)分享、完成；
             
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 外链页面的访问控制设置区域显示
        /// </summary>
        private void VisitLimitAreaShow()
        {
            try
            {
                //显示区域：访问限制显示区，
                area_LinkLimit_Info.Visibility = Visibility.Collapsed;
                area_LinkLimit_change.Visibility = Visibility.Visible;
                area_Link_Email.Visibility = Visibility.Collapsed;

                //按钮:取消、保存；
                link_btn_linkCancel.Visibility = Visibility.Collapsed;
                link_btn_finish.Visibility = Visibility.Collapsed;
                link_btn_linkSet.Visibility = Visibility.Visible;
                link_btn_Cancel.Visibility = Visibility.Visible;
                link_btn_send.Visibility = Visibility.Collapsed;
                btn_cancelsend.Visibility = Visibility.Collapsed;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void InitEmailInviteAreaShow()
        {
            try
            {
                //输入listbox准备:如果有数据，继续输入，如果没数据则为提示；
                InitalInviteArea();
                //由“邀请”提示，切换为“输入”提示
                area_LinkLimit_Info.Visibility = Visibility.Visible;
                area_LinkLimit_change.Visibility = Visibility.Collapsed;
                area_Link_Email.Visibility = Visibility.Visible;
                tb_invite_msg.Visibility = Visibility.Collapsed;
                sp_Email_Person.Visibility = Visibility.Visible;
                sp_Email_Users.Visibility = Visibility.Visible;

                //按钮:取消、保存；
                link_btn_linkCancel.Visibility = Visibility.Collapsed;
                link_btn_finish.Visibility = Visibility.Collapsed;
                link_btn_linkSet.Visibility = Visibility.Collapsed;
                link_btn_Cancel.Visibility = Visibility.Collapsed;
                link_btn_send.Visibility = Visibility.Visible;
                btn_cancelsend.Visibility = Visibility.Visible;

            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        /// <summary>
        /// 外链访问控制详细信息显示
        /// </summary>
        /// <param name="accessCode">外链提取码</param>
        /// <param name="effectTime">访问控制起始时间</param>
        /// <param name="expireTime">访问控制结束时间</param>
        public void linkLimitInfoShow(string accessCode, string effectTime, string expireTime)
        {
            try
            {
                tt_linkLimit_str.Inlines.Clear();
                accesscode_str = "";
                expireTime_str = "";
                effectTime_str = "";

                Hyperlink hyperlink = new Hyperlink(new Run((string)Application.Current.Resources["linkLimitChange"]));//linkLimitChange
                hyperlink.Click += new RoutedEventHandler(btn_LimitChange_Click);
                hyperlink.Style = (Style)Application.Current.Resources["tx_style_Hyperlink"];

                //公开访问：
                if (accessCode.Equals("") && effectTime.Equals("") && expireTime.Equals(""))
                {
                    tt_linkLimit_none.Visibility = Visibility.Visible;
                    tt_linkLimit_str.Inlines.Add(hyperlink);
                    return;
                }
                else
                {
                    tt_linkLimit_none.Visibility = Visibility.Collapsed;
                }

                //访问条件显示
                Run codeTitle = new Run((string)Application.Current.Resources["readCode1"]);
                Button btn = new Button();
                btn.Content = accessCode;
                btn.ToolTip = (string)Application.Current.Resources["copy"];
                btn.Style = (Style)Application.Current.Resources["Button_Style_AccessCode"];
                btn.Click += new RoutedEventHandler(btn_copyCode);
                btn.Margin = new Thickness(5, 0, 10, 0);
                btn.Name = "btn_accesscode_show";

                if (!accessCode.Equals(""))
                {
                    tt_linkLimit_str.Inlines.Add(codeTitle);
                    tt_linkLimit_str.Inlines.Add(btn);
                    accesscode_str = accessCode;
                }

                //修改：允许起始时间或过期时间为空，为空则为不限制。
                if (effectTime.Equals(""))
                {
                    tt_linkLimit_str.Inlines.Add(hyperlink);
                    return;
                }

                Run dtTitle = new Run((string)Application.Current.Resources["validTimes1"] + " ");
                Italic effectDT = new Italic(new Run((string)Application.Current.Resources["current"]));
                effectDT.Foreground = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xFF, 0x88, 0x88, 0x88));
                effectDT.FontSize = 13;
                Run toTitle = new Run("  " + (string)Application.Current.Resources["to"] + " ");
                Italic expireDT = new Italic(new Run((string)Application.Current.Resources["forever"] + "   "));
                expireDT.Foreground = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xFF, 0x88, 0x88, 0x88));
                expireDT.FontSize = 13;

                //起始时间：为空则为当前时间
                if (!effectTime.Equals(""))
                {
                    effectDT = new Italic(new Run(effectTime));
                    effectTime_str = effectTime;
                }

                //过期时间:为空则为永久
                if (expireTime.Equals(""))
                {
                    expireDT = new Italic(new Run((string)Application.Current.Resources["forever"] + "   "));
                    expireTime_str = "";
                }
                else
                {
                    expireDT = new Italic(new Run(expireTime + "   "));
                    expireTime_str = expireTime;
                }

                tt_linkLimit_str.Inlines.Add(dtTitle);
                tt_linkLimit_str.Inlines.Add(effectDT);
                tt_linkLimit_str.Inlines.Add(toTitle);
                tt_linkLimit_str.Inlines.Add(expireDT);
                tt_linkLimit_str.Inlines.Add(hyperlink);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

       /// <summary>
       ///关闭由界面子元素产生的提示窗口
       /// </summary>
        public void CloseChildElementNotice()
        {
            Dpk_Start.runNoticeWindow.CloseOwnNoticeWindow();
            Dpk_End.runNoticeWindow.CloseOwnNoticeWindow();
        }

       /// <summary>
       ///初始化访问限制区域控件值
       /// </summary>
        private void InitLimitAreaElementValue()
        {
            if (!accesscode_str.Equals(""))
            {
                this.ckb_isNeedCode.IsChecked = true;

            }
            else
            {
                this.ckb_isNeedCode.IsChecked = false;
            }

            if (!effectTime_str.Equals("") || !expireTime_str.Equals(""))
            {
                this.ckb_CusPeriod.IsChecked = true;
            }
            else
            {
                this.ckb_CusPeriod.IsChecked = false;
            }

            this.textbox_code.Text = accesscode_str;
            this.Dpk_Start.Text = effectTime_str;
            this.Dpk_End.Text = expireTime_str;
        }

       /// <summary>
       /// 切掉源字符串前后空格
       /// </summary>
       /// <param name="strSource">源字符串</param>
       /// <returns></returns>
        private string TrimSpace(string strSource)
        {
            try
            {
                if ("" == strSource)
                {
                    return strSource;
                }

                if (' ' == strSource.First())
                {
                    strSource = strSource.TrimStart(' ');
                    strSource = strSource.TrimStart('　');
                }
                else
                {
                    strSource = strSource.TrimStart('　');
                    strSource = strSource.TrimStart(' ');
                }

                if (' ' == strSource.Last())
                {
                    strSource = strSource.TrimEnd(' ');
                    strSource = strSource.TrimEnd('　');
                }
                else
                {
                    strSource = strSource.TrimEnd('　');
                    strSource = strSource.TrimEnd(' ');
                }

                return strSource;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return strSource;
            }
        }
        #endregion

        private void tb_invite_msg_Focus(object sender, RoutedEventArgs e)
        {
            try
            {
                InitEmailInviteAreaShow();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void tb_EmailUsers_tmp_TextChanged(object sender, TextChangedEventArgs e)
        {
            try
            {
                TextBox textBox = sender as TextBox;

                //向待邀请人文本框赋值处理

                ////获取选中项
                if (null == textBox.Text || (textBox.Text != mySelectedPerson.UserName && textBox.Text != mySelectedPerson.Email))
                {
                    return;
                }

                //将选入内容填充文本框
                for (int i = 0; i < MySharePersons.Count; i++)
                {
                    MySharePerson sharePerson = MySharePersons.ElementAt(i);

                    //去重处理：说明此用户已经在列表中(排除最后一个自身)
                    if (sharePerson.Email == mySelectedPerson.Email && i < MySharePersons.Count - 1)
                    {
                        //邀请完成，关闭POP层；清空用户查询列表
                        pop_userList.IsOpen = false;
                        searchUsers = new ObservableCollection<MySharePerson>();
                        //提示已经选择
                        runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["userAreadyIn"]);
                        return;
                    }

                    //找到输入的文本框，并赋值
                    if (sharePerson.UserName == SearchKey || sharePerson.Email == SearchKey)
                    {
                        sharePerson.UserName = mySelectedPerson.UserName;
                        sharePerson.StyleVisibility = "1";
                        sharePerson.UserId = mySelectedPerson.UserId;
                        sharePerson.Email = mySelectedPerson.Email;
                        sharePerson.LoginName = mySelectedPerson.LoginName;
                        sharePerson.Department = mySelectedPerson.Department;
                    }
                }

                //邀请完成，关闭POP层；清空用户查询列表
                pop_userList.IsOpen = false;
                searchUsers = new ObservableCollection<MySharePerson>();


                //准备下一个输入对像
                MySharePerson myShareInfo = new MySharePerson("", "0");
                MySharePersons.Add(myShareInfo);
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void lb_EmailPerson_MouseDown(object sender, MouseButtonEventArgs e)
        {
            try
            {
                int index = MySharePersons.Count - 1;
                if (index >= 0 && MySharePersons.ElementAt(index).Email == "")
                {
                    MySharePersons.RemoveAt(index);
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (index >= 0
                    && MySharePersons.ElementAt(index).Email != ""
                    && MySharePersons.ElementAt(0).Email != (string)Application.Current.Resources["emailInputnotice"])
                {
                    string input = MySharePersons.ElementAt(index).Email;
                    MySharePersons.RemoveAt(index);
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.Email = input;
                    MySharePersons.Add(newFirstShareInfo);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void lb_pop_keydown(object sender, KeyEventArgs e)
        {
            try
            {
                //键盘完成选择
                if (e.Key == Key.Return)
                {
                    ListBox userList = sender as ListBox;
                    //获取选中项
                    MySharePerson user = (MySharePerson)userList.SelectedValue;
                    if (null == user)
                    {
                        return;
                    }
                    else
                    {
                        mySelectedPerson = user;
                        this.tb_EmailUsers_tmp.Text = user.Email;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void lb_pop_MouseUp(object sender, MouseButtonEventArgs e)
        {
            try
            {
                if (MouseButton.Left == e.ChangedButton)
                {
                    ListBox listbox = sender as ListBox;
                    //获取选中项
                    MySharePerson user = (MySharePerson)searchUserList.SelectedValue;
                    if (null == user)
                    {
                        return;
                    }
                    else
                    {
                        mySelectedPerson = user;
                        this.tb_EmailUsers_tmp.Text = user.Email;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        private void tx_emailMsg_lostFocus(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox Tb = sender as TextBox;
                if (Tb.Text.Equals(""))
                {
                    InitialEmailMsgArea();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        private void btn_linksend_clicked(object sender, RoutedEventArgs e)
        {
            try
            {
                List<string> mailto = new List<string>();
                foreach (MySharePerson myperson in MySharePersons)
                {
                    if (myperson.StyleVisibility =="1")
                    {
                        mailto.Add(myperson.Email);
                    }
                }

                if (mailto.Count == 0)
                {
                    runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["invalidRecipients"]);
                    return;
                }

                string emailMsg = tx_share_emailMsg.Text;
                if (emailMsg ==  (string)Application.Current.Resources["addMsg"])
                {
                    emailMsg = "";
                }

                if (mailto.Count !=0)
                {
                    int iRet = thriftClient.sendEmail("", linkShareData.FilePath, ShareLinkInfo, emailMsg, mailto);
                }

                MySharePersons.Clear();
                InitialEmailMsgArea();
                this.Close();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void btn_cancelsend_clicked(object sender, RoutedEventArgs e)
        {
            MySharePersons.Clear();
            InitialEmailMsgArea();
            UIElementDefaultShow();
        }

        private void TBUserContrl_KeyDown(object sender, KeyEventArgs e)
        {
            try
            {
                TextBox cur_box = sender as TextBox;
                if (cur_box.Text == (string)Application.Current.Resources["emailInputnotice"])
                {
                    cur_box.Text = "";
                }
                cur_box.Foreground = System.Windows.Media.Brushes.Black;
                this.pop_userList.IsOpen = false;

                if (e.Key == Key.Return)
                {
                    //获得输入框输入值——获取用户显示弹出层
                    SearchKey = cur_box.Text;
                    getUserList(SearchKey);

                    if (searchUsers.Count <= 0)
                    {
                        return;
                    }

                    //设置popup的PlacementTarget属性
                    Binding binding = new Binding();
                    binding.Source = cur_box; //绑定源
                    this.pop_userList.SetBinding(Popup.PlacementTargetProperty, binding);
                    pop_userList.IsOpen = true;
                    //初始化POP选中信息；
                    tb_EmailUsers_tmp.Text = "";
                    mySelectedPerson = new MySharePerson();

                    this.searchUserList.ItemsSource = searchUsers;
                }
                else if (e.Key == Key.Down)
                {
                    //检测向下键头事件， 则新键点放于弹出层的可选用户列表
                    Keyboard.Focus(searchUserList);
                }
                else if (e.Key == Key.Back)
                {
                    if (cur_box.Text.Length <= 0) //文本框无值时执行back删除前一个
                    {
                        TBUserContrl_Back_Delete(cur_box.Text);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void TBUserContrl_GotFocus_1(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox Tb = sender as TextBox;
                Tb.CaretIndex = 0;

                if (Tb.Text == (string)Application.Current.Resources["emailInputnotice"])
                {
                    Tb.CaretIndex = 0;
                }
                else
                {
                    Tb.CaretIndex = Tb.Text.Length;
                    Tb.Foreground = System.Windows.Media.Brushes.Black;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        private void TBUserContrl_Loaded_1(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox TBUser = sender as TextBox;
                invite_tb_num++;
                TBUser.Name = TBUser.Name + invite_tb_num;
                //获取焦点，并置光标于最前；
                TBUser.Focus();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void lb_EmailPerson_LostFocus(object sender, RoutedEventArgs e)
        {
            try
            {
                if (MySharePersons.Count >1)
                {
                    return;
                }
                else if (MySharePersons.Count == 1 &&
                    (MySharePersons.ElementAt(0).Email == "" ||
                    MySharePersons.ElementAt(0).Email == (string)Application.Current.Resources["emailInputnotice"]))
                {
                    InitalInviteArea();
                    //UIElementDefaultShow();
                    this.tb_invite_msg.Visibility = Visibility.Visible;
                    this.sp_Email_Users.Visibility = Visibility.Collapsed;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void tx_emailMsg_GotFocus(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox Tb = sender as TextBox;
                string tbTag = Tb.Tag as string;
                if (msgFlag.Equals(tbTag))
                {
                    Tb.Text = "";
                    Tb.Foreground = System.Windows.Media.Brushes.Black;
                    Tb.Tag = "1";
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public void InitalInviteArea()
        {
            try
            {
                if (null == MySharePersons)
                {
                    MySharePersons = new ObservableCollection<MySharePerson>();
                }

                if (MySharePersons.Count == 0)
                {
                    invite_tb_num = 0;
                    MySharePerson myShareInfo = new MySharePerson("", "0");
                    myShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                    MySharePersons.Add(myShareInfo);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //初始化邮件消息
        private void InitialEmailMsgArea()
        {
            try
            {
                SolidColorBrush myBrush = new SolidColorBrush(System.Windows.Media.Color.FromArgb(0xFF, 0xB8, 0xB8, 0xB8));
                tx_share_emailMsg.Text = (string)Application.Current.Resources["addMsg"];
                tx_share_emailMsg.Foreground = myBrush;
                tx_share_emailMsg.Tag = msgFlag;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void bt_UserRemove(object sender, RoutedEventArgs e)
        {
            try
            {
                CustomControls.ImageButton but = sender as CustomControls.ImageButton;
                string type = but.Parent.GetType().ToString();
                StackPanel pannel = but.Parent as StackPanel;
                string strEmail = "";
                System.Windows.Controls.UIElementCollection childrens = pannel.Children;

                foreach (UIElement ui in childrens)
                {
                    if (ui.GetType() != but.GetType())
                    {
                        TextBox box = ui as TextBox;
                        strEmail = box.Text;
                        break;
                    }
                }

                MySharePerson removeuser = null;
                foreach (MySharePerson user in MySharePersons)
                {
                    if (user.Email == strEmail)
                    {
                        removeuser = user;
                        break;
                    }
                }
                MySharePersons.Remove(removeuser);

                if (0 == MySharePersons.Count())
                {
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (1 == MySharePersons.Count() || MySharePersons.ElementAt(0).Email == "")
                {
                    //  MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    MySharePersons.Clear();
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (1 < MySharePersons.Count())
                {
                    int index = MySharePersons.Count() - 1;
                    if (MySharePersons.ElementAt(index).Email == "")
                    {
                        MySharePersons.Remove(MySharePersons.ElementAt(index));
                        //重新准备下一个输入对像
                        MySharePerson myShareInfo = new MySharePerson("", "0");
                        MySharePersons.Add(myShareInfo);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void getUserList(string searchKey)
        {
            try
            {
                searchUsers = new ObservableCollection<MySharePerson>();
                List<ThriftClient.Share_User_Info> Share_User_list = thriftClient.listDomainUsers(searchKey);

                if (Share_User_list.Count == 0)
                {
                    runNoticeWindow.RunNoticeChildWindow(this, (string)Application.Current.Resources["notFind"]);
                    return;
                }
                foreach (ThriftClient.Share_User_Info ShareUserInfo in Share_User_list)
                {
                    MySharePerson user = new MySharePerson("", "0");
                    user.UserId = ShareUserInfo.Id;
                    user.LoginName = ShareUserInfo.LoginName;
                    user.Department = ShareUserInfo.Department;
                    user.Email = ShareUserInfo.Email;
                    user.ShareRW = (UInt16)ShareUserInfo.Right;
                    user.UserName = ShareUserInfo.UserName;

                    searchUsers.Add(user);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //邀请共享人输入框中，back键可删除共享人元素
        private void TBUserContrl_Back_Delete(string textBoxContent)
        {
            //删除前，判断元素个数。
            //功能场景：删除最后一个已经选好的元素时，保留输入指针.若只有一个指针时还删除,则清除列表显示默认提示
            if (1 == MySharePersons.Count() || MySharePersons.ElementAt(0).Email == "")
            {
                MySharePersons.Clear();
                MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                newFirstShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                MySharePersons.Add(newFirstShareInfo);
            }

            int removeIndex = -1;
            foreach (MySharePerson user in MySharePersons)
            {
                if (user.Email == textBoxContent)
                {
                    MySharePerson tmpUser = user;
                    removeIndex = MySharePersons.IndexOf(tmpUser);
                    removeIndex = removeIndex > 0 ? (removeIndex - 1) : -1;
                    break;
                }
            }

            if (removeIndex >= 0)
            {
                MySharePersons.RemoveAt(removeIndex);
            }

            if (0 == MySharePersons.Count())
            {
                MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                newFirstShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                MySharePersons.Add(newFirstShareInfo);
            }
        }

        private void sp_Email_Person_IsVisibleChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            if (sp_Email_Person.IsVisible)
            {
                if (null != MySharePersons && MySharePersons.Count == 1 && MySharePersons.ElementAt(0).Email =="" )
                {
                    MySharePersons.Clear();
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.Email = (string)Application.Current.Resources["emailInputnotice"];
                    MySharePersons.Add(newFirstShareInfo);
                }
            }
        }

        private void LinkShareWd_LocationChanged(object sender, EventArgs e)
        {
            foreach (NoticeWindow childWindow in this.OwnedWindows)
            {
                if (childWindow.IsLoaded)
                {
                    childWindow.Left = this.Left + (this.Width - childWindow.NoticeChildBody.ActualWidth) / 2;
                    childWindow.Top = this.Top + 36;
                }
            }
        }

        private void CloseButton_Click_1(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }

    #region LinkShare's Data Object
    public partial class LinkShareData : INotifyPropertyChanged
    {
        //private FileType _type;
        private string _fileName="";
        private string _id="";
        private string _filePath="";
        private string _linkInfo="";
        private string _readCode="";
        private bool _isForever=true;
        private bool _isCustomDate = false;
        private string _dateStart="";
        private string _dateEnd="";
        private string _dateStartTime="";
        private string _dateEndTime="";
        private bool _isNeedCode = false;

        /// <summary>
        /// 构造 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="type"></param>
        /// <param name="name"></param>
        /// <param name="link"></param>

        public LinkShareData(string id, /*FileType type,*/ string filePath, string link)
        {
            FileID = id;
            //OpFileType = type;
            FilePath = filePath;
            LinkURL = link;
            ReadCode = null;
            IsForever = true;
            //TODO::有时间限制时字符串分割处理；
            DateStart=null;
            DateStartTime=null;
            DateEnd=null;//"2015/10/10";
            DateEndTime=null;//"10:10:10"
        }

        public LinkShareData()
        {
            FilePath = "";
            IsForever = true;
            ReadCode = "";
        }

        public LinkShareData(string id, /*FileType type,*/ string filePath, string link, 
            string readCode,bool isForever, string dateStart, string dateEnd)
        {
            FileID = id;
            //OpFileType = type;
            FilePath = filePath;
            LinkURL = link;
            ReadCode = readCode;
            //TODO::有时间限制时字符串分割处理；
            DateStart="2014/10/10";
            DateStartTime="12:12:12";
            DateEnd="2015/10/10";
            DateEndTime = "10:10:10"; 
        }

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }

        /*
        public FileType OpFileType
        {
            get { return this._type; }
            set
            {
                this._type = value;
                OnPropertyChanged(new PropertyChangedEventArgs("OpFileType"));
            }
        }*/

        public string FileID
        {
            get { return this._id; }
            set
            {
                this._id = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FileID"));
            }
        }

        public string FilePath
        {
            get { return this._filePath; }
            set
            {
                this._filePath = value;
                int index = _filePath.LastIndexOf('\\');
                if (0 < index)
                {
                    FileName = "";
                    FileName = _filePath.Substring(index + 1);
                }
                OnPropertyChanged(new PropertyChangedEventArgs("FilePath"));
            }
        }


        public string FileName
        {
            get { return this._fileName; }
            set
            {
                this._fileName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FileName"));
            }
        }

        public string ReadCode
        {
            get { return this._readCode; }
            set
            {
                this._readCode = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ReadCode"));
            }
        }

        public bool IsNeedCode
        {
            get{return this._isNeedCode;}
            set
            {
               
                this._isNeedCode = value;
                if (!value)
                {
                    ReadCode = "";
                }
                OnPropertyChanged(new PropertyChangedEventArgs("IsNeedCode"));
            }
        }

        public bool IsForever
        {
            get { return this._isForever; }
            set
            {
                this._isForever = value;
              // IsCustomDate = !value;
                OnPropertyChanged(new PropertyChangedEventArgs("IsForever"));
            }
        }

        public bool IsCustomDate
        {
            get { return this._isCustomDate; }
            set
            {
                this._isCustomDate = value;
               //IsForever = !value;
                OnPropertyChanged(new PropertyChangedEventArgs("IsCustomDate"));
            }
        }

        public string LinkURL
        {
            get { return this._linkInfo; }
            set
            {
                this._linkInfo = value;
                OnPropertyChanged(new PropertyChangedEventArgs("LinkURL"));
            }
        }

        public string DateStart
        {
            get { return this._dateStart; }
            set
            {
                this._dateStart = value;
                OnPropertyChanged(new PropertyChangedEventArgs("DateStart"));
            }
        }

        public string DateStartTime
        {
            get { return this._dateStartTime; }
            set
            {
                this._dateStartTime = value;
                OnPropertyChanged(new PropertyChangedEventArgs("DateStartTime"));
            }
        }

        public string DateEnd
        {
            get { return this._dateEnd; }
            set
            {
                this._dateEnd = value;
                OnPropertyChanged(new PropertyChangedEventArgs("DateEnd"));
            }
        }

        public string DateEndTime
        {
            get { return this._dateEndTime; }
            set
            {
                this._dateEndTime = value;
                OnPropertyChanged(new PropertyChangedEventArgs("DateEndTime"));
            }
        }
    }
}
    #endregion