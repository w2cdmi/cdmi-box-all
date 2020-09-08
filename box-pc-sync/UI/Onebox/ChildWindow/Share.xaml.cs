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
using System.Collections.ObjectModel;
using System.Windows.Controls.Primitives;
using System.ComponentModel;
using System.Threading;
using Onebox.CustomControls;

namespace Onebox
{
    /// <summary>
    /// Share.xaml 的交互逻辑
    /// </summary>
    public partial class Share : Window
    {
        #region Variable define
        //记录用户输入框输入内容；同时做为对像key，供遍历获得当前操作的对像；
        public string SearchKey;
        //待邀请人列表
        public ObservableCollection<MySharePerson> MySharePersons = null;

        public ObservableCollection<ThriftClient.Share_User_Info> MatichPersons = new ObservableCollection<ThriftClient.Share_User_Info>();
        //域用户列表
        private ObservableCollection<MySharePerson> searchUsers =  new ObservableCollection<MySharePerson>();
        //已共享用户列表
        public ObservableCollection<MySharePerson> MySharedUsers = new ObservableCollection<MySharePerson>();
        //界面数据绑定OBJ
        public ShareData shareData;
        //作用：协助生成待邀请输入框唯一标识(取消时清0)
        private int invite_tb_num;
        //暂存：弹出层用户选择结果；
        private MySharePerson mySelectedPerson = new MySharePerson();
        //当前登陆用户：
        //初始时：是否有共享人标识，便于逐个移除被共享人后，此区域还需显示
        public bool hasRecipients = false;
        public string strremained = "";
        //email提示信息时：tag为0；输入内容后:tag为1；
        private const string msgFlag = "0";
        private object myLock = new object();  
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        MainWindow ParentWindow;
        public RunNoticeWindow runNoticeWindow  = new RunNoticeWindow();
        private ObservableCollection<Thread> ThreadCollection = new ObservableCollection<Thread>();
        
        #endregion  //Variable define

        public Share(MainWindow parentWindow)
        {
            try
            {
                ParentWindow = parentWindow;
                InitializeComponent();
            
                //数据绑定OBJ
                if (null == shareData)
                {
                    shareData = new ShareData();
                }
                this.DataContext = shareData;

                //待邀请人员列表
                InitalInviteArea();
                this.LBSharePerson.ItemsSource = MySharePersons;
                MatichPersons.CollectionChanged += MatichPersons_CollectionChanged;
                this.lb_Recipients.ItemsSource = MySharedUsers;
              
                //已共享人员列表：
                //AddSharedPersion_test(); //添加测试数据
                if (MySharedUsers.Count > 0)
                {
                    sharedZebraHandle();
                    UI_RecipientShow();
                }
                else
                {
                    UI_inviteShareShow();
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }



        #region 窗体属性方法
        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            this.DragMove();
            base.OnMouseLeftButtonDown(e);
        }

        protected override void OnClosed(EventArgs e)
        {
            runNoticeWindow.CloseOwnNoticeWindow();
            foreach (Thread ChildThread in ThreadCollection)
            {
                if (null != ChildThread)
                {
                    ChildThread.Interrupt();
                }
            }
            base.OnClosed(e);
        }

        #endregion  //窗体属性方法



        #region 按钮事件处理

        private void Button_Close(object sender, RoutedEventArgs e)
        {
            //隐藏
            InitalInviteArea();
            InitialEmailMsgArea();
            this.Visibility = Visibility.Hidden;
            runNoticeWindow.CloseOwnNoticeWindow();
        }

        // 按钮：邀请 执行，注意判断场景：当在没有已共享人时，邀请后为关闭窗口;存在已共享人时，邀请后为返回列表显示；
        private void btn_inviteShare_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                this.btn_Share_invite.IsEnabled = false;
                this.btn_Share_Cancel.IsEnabled = false;

                //判断是否关闭；
                bool isClose = true;
                if (MySharedUsers.Count > 0 ||hasRecipients==true)
                {
                    isClose = false;
                }
                if (1 == MySharePersons.Count)
                {
                    if (MySharePersons.ElementAt(0).UserName == "" || MySharePersons.ElementAt(0).UserName == (string)Application.Current.Resources["inputnotice"] ||
                         MySharePersons.ElementAt(0).UserName == (string)Application.Current.Resources["nextinputnotice"])
                    {
                        runNoticeWindow.RunNoticeChildWindow(this, (string)Application.Current.Resources["emptyinvitenotice"]);
                        this.btn_Share_invite.IsEnabled = true;
                        this.btn_Share_Cancel.IsEnabled = true;
                        MySharePersons.Clear();
                        InitalInviteArea();
                        return;
                    }
                }
                //邀请共享人列表为 MySharePersons 排除最后一对象为空
                List<ThriftClient.Share_User_Info> Share_User_list = new List<ThriftClient.Share_User_Info>();
                foreach (MySharePerson myShareInfo in MySharePersons)
                {
                    if (myShareInfo.StyleVisibility == "1")
                    {
                        ThriftClient.Share_User_Info ShareUserInfo = new ThriftClient.Share_User_Info();
                        ShareUserInfo.Id = myShareInfo.UserId;
                        ShareUserInfo.LoginName = myShareInfo.LoginName;
                        ShareUserInfo.Department = myShareInfo.Department;
                        ShareUserInfo.Email = myShareInfo.Email;
                        ShareUserInfo.Right = myShareInfo.ShareRW;
                        Share_User_list.Add(ShareUserInfo);
                    }
                }

                string emailMsg = "";
                if(tx_share_emailMsg.Tag.ToString()!= msgFlag)
                {
                    emailMsg = tx_share_emailMsg.Text;
                }

                if (0 != thriftClient.setShareMember(shareData.OpenFilePath, Share_User_list, emailMsg))
                {
                    runNoticeWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["share"], NoticeType.Error, (string)Application.Current.Resources["inviteFailed"], "");
                    this.btn_Share_invite.IsEnabled = true;
                    this.btn_Share_Cancel.IsEnabled = true;
                    return;
                }

                //邀请成功：
                InitalInviteArea();
                InitialEmailMsgArea();
                this.btn_Share_invite.IsEnabled = true;
                this.btn_Share_Cancel.IsEnabled = true;
                //场景1：直接关闭窗口
                if (isClose)
                {
                    this.Close();
                    return;
                }
                //场景2：刷新已共享人信息
                refreshRecipientsInfo();
                UI_RecipientShow();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        // 按钮：取消 执行. 无已邀请人：取消为退出;存在已邀请人时:取消为退出邀请show，展示已共享人show; 
        private void btn_Cancle_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                //按钮禁用；
                this.btn_Share_Cancel.IsEnabled = false;
                //控件恢复初始化
                InitalInviteArea();
                InitialEmailMsgArea();
                //不同场境下的不同响应
                if (MySharedUsers.Count > 0 || hasRecipients == true)
                {
                    UI_RecipientShow();
                }
                else
                {
                    this.Close();
                }
                //按钮恢复
                this.btn_Share_Cancel.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //按钮：取消文件共享
        private void btn_RemoveAll_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                this.btn_Share_RemoveAll.IsEnabled = false;

                if (0 == MySharedUsers.Count)
                {
                    MySharedUsers.Clear();
                    this.btn_Share_RemoveAll.IsEnabled = true;
                    this.Close();
                    return;
                }
                runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["shareMgr"], NoticeType.Ask, (string)Application.Current.Resources["removeConfirm"], "");
                    //提示消息，非"确定"操作

                if (runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused == true)
                {
                    int iRet = thriftClient.cancelShare(shareData.OpenFilePath);
                    if (iRet == 0)
                    {
                        MySharedUsers.Clear();
                        this.btn_Share_RemoveAll.IsEnabled = true;
                        this.Close();
                    }
                    else
                    {
                        runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.
                            Current.Resources["share"], NoticeType.Error, (string)Application.Current.Resources["unSharedFailed"], "");
                        this.btn_Share_RemoveAll.IsEnabled = true;
						runNoticeWindow.MDnoticeWindow.Close();
                        runNoticeWindow.MDnoticeCfWindow.Close();
                    }
                }

                this.btn_Share_RemoveAll.IsEnabled = true;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //按钮：单个移除已共享人；
        private void ImgBtn_sharePerson_remove(object sender, RoutedEventArgs e)
        {
            try
            {
                ImageButton button = sender as ImageButton;
               object objUserid= button.Tag;
                int index = -1;
                ThriftClient.Share_User_Info removeShare = new ThriftClient.Share_User_Info();
                foreach (MySharePerson person in MySharedUsers)
                {
                    if (long.Parse(objUserid.ToString()) == person.UserId)
                    {
                        index = MySharedUsers.IndexOf(person);
                        removeShare.Id = person.UserId;
                        break;
                    }
                }

                if (index < 0)
                {
                    return;
                }

                int iRet = thriftClient.delShareMember(shareData.OpenFilePath, removeShare);
                if (0 != iRet)
                {
                    App.Log.Error("remove share user failed.");
                    return;
                }

                //已共享人数信息刷新
                this.MySharedUsers.RemoveAt(index);
                sharedZebraHandle();
                string temp = MySharedUsers.Count + " " + (string)Application.Current.Resources["sharedPerson"];
                this.sh_sharedUser_num.Content = temp;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        // 按钮：单个移除待共享人
        private void bt_UserRemove(object sender, RoutedEventArgs e)
        {
            try
            {
                ImageButton but = sender as ImageButton;
                string type = but.Parent.GetType().ToString();
                StackPanel pannel = but.Parent as StackPanel;
                string username = "";
                System.Windows.Controls.UIElementCollection childrens = pannel.Children;

                foreach (UIElement ui in childrens)
                {
                    if (ui.GetType() != but.GetType())
                    {
                        TextBox box = ui as TextBox;
                        username = box.Text;
                        break;
                    }
                }

                MySharePerson removeuser = null;
                foreach (MySharePerson user in MySharePersons)
                {
                    if (user.UserName == username)
                    {
                        removeuser = user;
                        break;
                    }
                }
                MySharePersons.Remove(removeuser);

                if (0 == MySharePersons.Count())
                {
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.UserName = (string)Application.Current.Resources["inputnotice"];
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (1 == MySharePersons.Count() || MySharePersons.ElementAt(0).UserName == "")
                {
                    MySharePersons.Clear();
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.UserName = (string)Application.Current.Resources["inputnotice"];
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (1 < MySharePersons.Count())
                {
                    int index = MySharePersons.Count() - 1;
                    if (MySharePersons.ElementAt(index).UserName == "")
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

        #endregion //按钮事件处理

        #region 数据处理(文本框)
        //邀请共享人文本框获取焦点
        private void tx_invite_focused(object sender, RoutedEventArgs e)
        {
            try
            {
                //UI界面变化
                UI_inviteShareShow();
                PrepareInviteArea();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //邀请输入框：load
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

        // 邀请输入框：焦点事件
        private void TBUserContrl_GotFocus_1(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox Tb = sender as TextBox;
                Tb.CaretIndex = 0;

                if (Tb.Text == (string)Application.Current.Resources["inputnotice"])
                {
                    Tb.CaretIndex = 0;
                }
                else
                {
                    Tb.CaretIndex = Tb.Text.Length;
                    Tb.Foreground = Brushes.Black;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        //邀请输入框：回车查询域用户，其它输入则取消提示；(//用户输入回车事件，获得输入值用于查询AD域中的用户列表，并显示弹出层)
        private void TBUserContrl_KeyDown(object sender, KeyEventArgs e)
        {
            try
            {
                TextBox cur_box = sender as TextBox;
                if (e.Key == Key.Return)
                {
                    //获得输入框输入值——获取用户显示弹出层
                    SearchKey = cur_box.Text;
                    if (-1 != SearchKey.IndexOf(',') || -1 != SearchKey.IndexOf(';'))
                    {
                        Thread addBatchPersonThread = new Thread(new ParameterizedThreadStart(addBatchSharePerson));
                        addBatchPersonThread.IsBackground = true;
                        addBatchPersonThread.Start(SearchKey);
                        ThreadCollection.Add(addBatchPersonThread);
                    }
                    else
                    {
                        getUserList(SearchKey);
                        if (searchUsers.Count == 0)
                        {
                            runNoticeWindow.RunNoticeChildWindow(this, (string)Application.Current.Resources["notFind"]);
                            return;
                        }

                        //设置popup的PlacementTarget属性
                        Binding binding = new Binding();
                        binding.Source = cur_box; //绑定源
                        this.pop_userList.SetBinding(Popup.PlacementTargetProperty, binding);
                        pop_userList.IsOpen = true;
                        //初始化POP选中信息；
                        tx_shareUsers_tmp.Text = "";
                        mySelectedPerson = new MySharePerson();

                        this.searchUserList.ItemsSource = searchUsers;
                    }
                }
                else if (e.Key == Key.Down)
                {
                    //检测向下键头事件， 则新键点放于弹出层的可选用户列表
                    Keyboard.Focus(searchUserList);
                }
                else if(e.Key == Key.Back)
                {
                    if(cur_box.Text.Length<=0) //文本框无值时执行back删除前一个
                    {
                        TBUserContrl_Back_Delete(cur_box.Text);
                    }
                }
                else
                {
                    if (cur_box.Text == (string)Application.Current.Resources["inputnotice"])
                    {
                        cur_box.Text = "";
                    }
                    cur_box.Foreground = Brushes.Black;
                    this.pop_userList.IsOpen = false;
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
            if (1 == MySharePersons.Count() || MySharePersons.ElementAt(0).UserName == "")
            {
                MySharePersons.Clear();
                MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                newFirstShareInfo.UserName = (string)Application.Current.Resources["inputnotice"];
                MySharePersons.Add(newFirstShareInfo);
            }

            int removeIndex = -1;
            foreach (MySharePerson user in MySharePersons)
            {
                if (user.UserName == textBoxContent)
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
                newFirstShareInfo.UserName = (string)Application.Current.Resources["inputnotice"];
                MySharePersons.Add(newFirstShareInfo);
            }
        }

        //选择域用户处理；
        //private void lb_userSelected(object sender, SelectionChangedEventArgs e)
        //{
        //    try
        //    {
        //        return;
        //        //获取选中项
        //        ListBox userList = sender as ListBox;
        //        MySharePerson user = (MySharePerson)userList.SelectedValue;
        //        if (null == user)
        //        {
        //            return;
        //        }

        //        //将选入内容填充文本框
        //        for (int i = 0; i < MySharePersons.Count; i++)
        //        {
        //            MySharePerson sharePerson = MySharePersons.ElementAt(i);

        //            //去重处理：说明此用户已经在列表中(排除最后一个自身)
        //            if (sharePerson.UserName == user.UserName && i < MySharePersons.Count - 1)
        //            {
        //                //邀请完成，关闭POP层；清空用户查询列表
        //                pop_userList.IsOpen = false;
        //                searchUsers = new ObservableCollection<MySharePerson>();
        //                //提示已经选择
        //                MainWindow.RunNoticeConfirmWindow((string)Application.Current.Resources["share"], NoticeType.Info,
        //                    (string)Application.Current.Resources["userAreadyIn"], "");
        //                return;
        //            }

        //            //找到输入的文本框，并赋值
        //            if (sharePerson.UserName == SearchKey)
        //            {
        //                sharePerson.UserName = user.UserName;
        //                sharePerson.StyleVisibility = "1";
        //                sharePerson.UserId = user.UserId;
        //                sharePerson.LoginName = user.LoginName;
        //                sharePerson.Department = user.Department;
        //            }
        //        }

        //        //邀请完成，关闭POP层；清空用户查询列表
        //        pop_userList.IsOpen = false;
        //        searchUsers = new ObservableCollection<MySharePerson>();

        //        //准备下一个输入对像
        //        MySharePerson myShareInfo = new MySharePerson("", "0");
        //        MySharePersons.Add(myShareInfo);
        //    }
        //    catch (System.Exception ex)
        //    {
        //        App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
        //    }
        //}

        //email消息框获得gotFocus
        private void tx_emailMsg_Focus(object sender, RoutedEventArgs e)
        {
            try
            {
                TextBox Tb = sender as TextBox;
                string tbTag = Tb.Tag as string;
                if (msgFlag.Equals(tbTag))
                {
                    Tb.Text = "";
                    Tb.Foreground = Brushes.Black;
                    Tb.Tag = "1";
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //email消息框获得LostFocus
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

        //邀请共享人区域（listBox）获取焦点，作用：有数据时焦点时，输入框处理
        private void lb_inviteArea_Focused_mouse(object sender, MouseButtonEventArgs e)
        {
            try
            {
                int index = MySharePersons.Count - 1;
                if (index >= 0 && MySharePersons.ElementAt(index).UserName == "")
                {
                    MySharePersons.RemoveAt(index);
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    MySharePersons.Add(newFirstShareInfo);
                }
                else if (index >= 0
                    && MySharePersons.ElementAt(index).UserName != ""
                    && MySharePersons.ElementAt(0).UserName != (string)Application.Current.Resources["inputnotice"])
                {
                    string input = MySharePersons.ElementAt(index).UserName;
                    MySharePersons.RemoveAt(index);
                    MySharePerson newFirstShareInfo = new MySharePerson("", "0");
                    newFirstShareInfo.UserName = input;
                    MySharePersons.Add(newFirstShareInfo);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        //邀请共享人区域（listBox）失去焦点，作用：有数据时焦点时，输入框提示信息处理
        private void lb_inviteArea_lostFocused(object sender, RoutedEventArgs e)
        {
            try
            {
                if (MySharePersons.Count > 1)
                {
                    return;
                }
                else if (MySharePersons.Count == 1 &&
                    (MySharePersons.ElementAt(0).UserName == "" ||
                    MySharePersons.ElementAt(0).UserName == (string)Application.Current.Resources["inputnotice"]))
                {
                    InitalInviteArea();
                    tx_invite_msg.Visibility = Visibility.Visible;
                    lb_share_inivite.Visibility = Visibility.Collapsed;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
        #endregion  // 数据处理(文本框)



        #region 业务逻辑：功能方法
        //查询域用户
        private void getUserList(string searchKey)
        {
            try
            {
                searchUsers = new ObservableCollection<MySharePerson>();
                List<ThriftClient.Share_User_Info> Share_User_list = thriftClient.listDomainUsers(searchKey);
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

        //邀请共享输入准备
        private void PrepareInviteArea()
        {
            try
            {
                //输入listbox准备:如果有数据，继续输入，如果没数据则为提示；
                InitalInviteArea();
                //由“邀请”提示，切换为“输入”提示
                tx_invite_msg.Visibility = Visibility.Collapsed;
                lb_share_inivite.Visibility = Visibility.Visible;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //刷新已共享人列表： 
        public void refreshRecipientsInfo()
        {
            try
            {
                List<ThriftClient.Share_User_Info> ShareUserInfoList = thriftClient.listShareUsers(shareData.OpenFilePath);
                ObservableCollection<MySharePerson> MySharedPersions = new ObservableCollection<MySharePerson>();
                foreach (ThriftClient.Share_User_Info ShareUserInfo in ShareUserInfoList)
                {
                    MySharePerson mySharePerson = new MySharePerson();
                    mySharePerson.UserName = ShareUserInfo.UserName;
                    mySharePerson.Department = ShareUserInfo.Department;
                    mySharePerson.ShareRW = ShareUserInfo.Right;
                    mySharePerson.UserId = ShareUserInfo.Id;
                    mySharePerson.Email = ShareUserInfo.Email;
                    MySharedPersions.Add(mySharePerson);
                }
                MySharedUsers = MySharedPersions;
                sharedZebraHandle();
                lb_Recipients.ItemsSource = MySharedUsers;

                if (lb_Recipients.Items.Count > 0)
                {
                    lb_Recipients.ScrollIntoView(lb_Recipients.Items[0]); //解决listbox数据更新后，数据变少，滚动条不消失
                }

                //已共享人数信息
                string temp = MySharedUsers.Count + " " + (string)Application.Current.Resources["sharedPerson"];
                this.sh_sharedUser_num.Content = temp;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //初始化邀请共享人输入框
        public void InitalInviteArea()
        {
            try
            {
                if (null == MySharePersons)
                {
                    MySharePersons = new ObservableCollection<MySharePerson>();
                }
                else
                {
                    MySharePersons.Clear();
                }
                invite_tb_num = 0;
                MySharePerson myShareInfo = new MySharePerson("", "0");
                myShareInfo.UserName = (string)Application.Current.Resources["inputnotice"];
                MySharePersons.Add(myShareInfo);
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

        private void UI_inviteShareInitial()
        {
            try
            {
                //邀请提示：
                InitalInviteArea();
                tx_invite_msg.Visibility = Visibility.Visible;
                lb_share_inivite.Visibility = Visibility.Collapsed;

                //消息框提示：
                InitialEmailMsgArea();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //邀请共享人操作（界面展示）
        public void UI_inviteShareShow()
        {
            try
            {
                //总数信息：
                if (MySharedUsers.Count > 0 || hasRecipients==true)
                {
                    sh_sharedUser_num.Visibility = Visibility.Visible;
                    area_Share_Recipients.Visibility = Visibility.Visible;
                    tx_invite_msg.Text = (string)Application.Current.Resources["inviteMore"];
                }
                else
                {
                    sh_sharedUser_num.Visibility = Visibility.Collapsed;
                    area_Share_Recipients.Visibility = Visibility.Collapsed;
                    tx_invite_msg.Text = (string)Application.Current.Resources["invitePerson"];
                }
                
                //邀请提示+Msg框
                InitalInviteArea();

                if (this.tx_share_emailMsg.Text.Equals(""))
                {
                    InitialEmailMsgArea();
                }

                tx_invite_msg.Visibility = Visibility.Visible;
                lb_share_inivite.Visibility = Visibility.Collapsed;
                tx_share_emailMsg.Visibility = Visibility.Visible;

                //按钮：
                btn_Share_invite.Visibility = Visibility.Visible;
                btn_Share_Cancel.Visibility = Visibility.Visible;
                btn_Share_RemoveAll.Visibility = Visibility.Collapsed;
                btn_share_close.Visibility = Visibility.Collapsed;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //存在被共享人时（界面展示）
        public void UI_RecipientShow()
        {
            try
            {
                //控件恢复初始化
                InitalInviteArea();
                InitialEmailMsgArea();

                //总数信息：
                sh_sharedUser_num.Visibility = Visibility.Visible;
                area_Share_Recipients.Visibility = Visibility.Visible;

                //列表、提示
                tx_invite_msg.Text = (string)Application.Current.Resources["inviteMore"];
                tx_invite_msg.Visibility = Visibility.Visible;
                lb_share_inivite.Visibility = Visibility.Collapsed;
                tx_share_emailMsg.Visibility = Visibility.Collapsed;

                //按钮：
                btn_Share_invite.Visibility = Visibility.Collapsed;
                btn_Share_Cancel.Visibility = Visibility.Collapsed;
                btn_Share_RemoveAll.Visibility = Visibility.Visible;
                btn_share_close.Visibility = Visibility.Visible;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        //listBox显示斑马线处理
        public void sharedZebraHandle()
        {
            try
            {
                if (null == MySharedUsers)
                {
                    return;
                }

                for (int i = 0; i < MySharedUsers.Count; ++i)
                {
                    if ((i % 2) == 0)
                    {
                        MySharedUsers.ElementAt(i).ListColor = 0;
                        MySharedUsers.ElementAt(i).ListSideColor = 0;
                    }
                    else
                    {
                        MySharedUsers.ElementAt(i).ListColor = 1;
                        MySharedUsers.ElementAt(i).ListSideColor = 1;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void addBatchSharePerson(object strSearchKey)
        {
            lock (myLock)
            { 
                try
                {
                    ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW();
                    string currentUser = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                    this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateUpdateCursor(UpdateCursor), Cursors.AppStarting);
                    List<string> keyword = new List<string>();
                    List<string> notmatch = new List<string>();
                    MatichPersons.Clear();
                    keyword.Clear();
                    notmatch.Clear();
                    keyword = cuttingSearchKey(strSearchKey.ToString());
                    if (keyword.Count != 0)
                    {
                        if (strremained == SearchKey)
                        {
                            this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateRunNotice(RunNotice),  (string)Application.Current.Resources["notFind"]);
                            return;
                        }

                        string strInput = "";
                        Dictionary<string, ThriftClient.Share_User_Info> BatchRet = thriftClient.listBatchDomainUsers(keyword);
                        if (BatchRet.Count == 0)
                        {
                            this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateRunNotice(RunNotice), (string)Application.Current.Resources["notFind"]);
                            return;
                        }

                        foreach (string strtemp in keyword)
                        {
                            if (BatchRet.ContainsKey(strtemp))
                            {
                                if (BatchRet[strtemp].LoginName.ToLower() != currentUser.ToLower())
                                {
                                    MatichPersons.Add(BatchRet[strtemp]);
                                    continue;
                                }
                            }

                            strInput += strtemp + ",";
                        }

                        strInput = strInput.TrimEnd(',');
                        this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateUpdateCursor(UpdateCursor), Cursors.Arrow);
                        if (strInput != "")
                        {
                            ThriftClient.Share_User_Info temp = new ThriftClient.Share_User_Info();
                            temp.UserName = strInput;
                            SearchKey = strInput;
                            strremained = strInput;
                            MatichPersons.Add(temp);
                            this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateRunNotice(RunNotice), (string)Application.Current.Resources["batchAddNotice"]);
                        }
                    }
                }
                catch (System.Exception ex)
                {
                    App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                }
            }
        }

        private delegate void DelegateUpdateCursor(Cursor cur);
        private void UpdateCursor(Cursor cur)
        {
            try
            {
                this.Cursor = cur;
            }
            catch {}
            
        }

        private delegate void DelegateRunNotice(string NoticMsg);
        private void RunNotice(string NoticeMsg)
        {
            try
            {
                runNoticeWindow.RunNoticeChildWindow(this,NoticeMsg);
            }
            catch { }
        }

        private void MatichPersons_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            try
            {
                if (e.Action == System.Collections.Specialized.NotifyCollectionChangedAction.Add)
                {
                    foreach (ThriftClient.Share_User_Info temp in e.NewItems)
                    {
                        this.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.SystemIdle, new DelegateAddItem(AddItem), temp);
                    }
                }
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private delegate void DelegateAddItem(ThriftClient.Share_User_Info item);
        private void AddItem(ThriftClient.Share_User_Info item)
        {
            try
            {
                if (SearchKey == item.UserName)
                {
                    MySharePersons.Last().UserName = item.UserName;
                    return;
                }

                MySharePersons.Last().UserName = item.UserName;
                MySharePersons.Last().StyleVisibility = "1";
                MySharePersons.Last().UserId = item.Id;
                MySharePersons.Last().Email = item.Email;
                MySharePersons.Last().LoginName = item.LoginName;
                MySharePersons.Last().Department = item.Department;

                MySharePerson myShareInfo = new MySharePerson("", "0");
                MySharePersons.Add(myShareInfo);
            }
            catch {  }
        }

        private List<string> cuttingSearchKey(string strSearchKey)
        {
            char chComma =  ',';
            char chSemicolon = ';';
            List<string> keyword = new List<string>();
            try
            {
                string strTemp = strSearchKey;
                string strRet = "";
                for (int i = 0; i < strTemp.Length;i++)
                {
                    if (strTemp[i] != chComma && strTemp[i] != chSemicolon)
                    {
                        strRet += strTemp[i].ToString();
                    }
                    else
                    {
                        keyword.Add(strRet);
                        strRet = "";
                        continue;
                    }
                }

                if (""  != strRet)
                {
                    keyword.Add(strRet);
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
            

            return keyword;
        }

        //测试数据：
        //private void AddSharedPersion_test()
        //{
        //    try
        //    {
        //        for (int i = 0; i <= 3; i++)
        //        {
        //            MySharePerson mySharePerson = new MySharePerson();
        //            mySharePerson.UserName = "UserName" + i;
        //            mySharePerson.Department = "Department" + i;
        //            mySharePerson.ShareRW = 0;
        //            mySharePerson.UserId = "UserNameId_" + i;
        //            MySharedUsers.Add(mySharePerson);
        //        }
        //    }
        //    catch (System.Exception ex)
        //    {
        //        App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
        //    }
        //}
        #endregion //业务逻辑：功能方法


        //暂存：弹出层用户选择结果；
        private void lb_selectItem_Confirm(object sender, TextChangedEventArgs e)
        {
            try
            {
                TextBox textBox = sender as TextBox;

                //向待邀请人文本框赋值处理

                ////获取选中项
                //ListBox userList = sender as ListBox;
                //MySharePerson user = (MySharePerson)userList.SelectedValue;
                if (null == textBox.Text || textBox.Text != mySelectedPerson.UserName)
                {
                    return;
                }

                //不能共享给自己
                ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW();
                string currentUser = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_USERINFO_SECTION, ConfigureFileOperation.ConfigureFileRW.CONF_USERNAME_KEY);
                string inviteUser = mySelectedPerson.LoginName.ToLower();
                if (inviteUser.Equals(currentUser.ToLower()))
                {
                    //邀请完成，关闭POP层；清空用户查询列表
                    textBox.Text = ""; //原因：如果连续选择自己，不清空此文本框，则只有首次有提示，其它没有valuechange动作。
                    pop_userList.IsOpen = false;
                    searchUsers = new ObservableCollection<MySharePerson>();
                    runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["shareSelfErr"]);
                    return;
                }

                //将选入内容填充文本框
                for (int i = 0; i < MySharePersons.Count; i++)
                {
                    MySharePerson sharePerson = MySharePersons.ElementAt(i);

                    //去重处理：说明此用户已经在列表中(排除最后一个自身)
                    if (sharePerson.UserName == mySelectedPerson.UserName && i < MySharePersons.Count - 1)
                    {
                        //邀请完成，关闭POP层；清空用户查询列表
                        pop_userList.IsOpen = false;
                        searchUsers = new ObservableCollection<MySharePerson>();
                        //提示已经选择
                        runNoticeWindow.RunNoticeChildWindow(this,(string)Application.Current.Resources["userAreadyIn"]);
                        return;
                    }

                    //找到输入的文本框，并赋值
                    if (sharePerson.UserName == SearchKey)
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

        //POP可选列表：键盘事件
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
                        this.tx_shareUsers_tmp.Text = user.UserName;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

        }

        //POP可选列表：鼠标事件
        private void lb_pop_mousedown(object sender, MouseButtonEventArgs e)
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
                        this.tx_shareUsers_tmp.Text = user.UserName;
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void ShareWd_LocationChanged(object sender, EventArgs e)
        {
            foreach (NoticeWindow childWindow in this.OwnedWindows)
            {
                if (childWindow.IsLoaded)
                {
                    childWindow.Left = this.Left + (this.Width - childWindow.NoticeChildBody.ActualWidth)/2;
                    childWindow.Top = this.Top + 36;
                }
            }
        }
    }

    #region 绑定数据
    public partial class ShareData : INotifyPropertyChanged
    {
       // private FileType _type;
        private string _FilePath;
        private string _fileName;

        public ShareData()
        {
           // OpenFileType = FileType.File;
            OpenFilePath = "";
            FileName = "";
        }

        public ShareData(/*FileType fileType,*/ string FilePath)
        {
            //OpenFileType = fileType;
            OpenFilePath = FilePath;
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
        public FileType OpenFileType
        {
            get { return this._type; }
            set
            {
                this._type = value;
                OnPropertyChanged(new PropertyChangedEventArgs("OpFileType"));
            }
        }*/

        public string OpenFilePath
        {
            get { return this._FilePath; }
            set
            {
                this._FilePath = value;

                int index = _FilePath.LastIndexOf('\\');
                if (0 < index)
                {

                    FileName = "";
                    FileName = _FilePath.Substring(index + 1);
                }
                OnPropertyChanged(new PropertyChangedEventArgs("OpFilePath"));
            }
        }

        public string FileName
        {
            get { return _fileName; }
            set
            {
                this._fileName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FileName"));
            }
        }

    }
    #endregion
}
