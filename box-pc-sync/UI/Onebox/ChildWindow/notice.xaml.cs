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
using System.Collections.ObjectModel;

namespace Onebox
{
	/// <summary>
	/// notice.xaml 的交互逻辑
	/// </summary>
	public partial class NoticeWindow : Window
	{
        public  NoticeData noticeData=new NoticeData();
        public bool IsModeWindow = false;
        private Common common = new Common();
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
    
        public NoticeWindow()
		{
            try
            {
                this.InitializeComponent();
                this.DataContext = noticeData;
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

        private void BN_OK_Click(object sender, RoutedEventArgs e)
        {
            if (IsModeWindow)
            {
                this.Hide();
            }
            else
            {
                this.Close();
            }
        }

        private void BN_Close_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void Notice_Content1_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            MainWindow parentWindow = (MainWindow)App.Current.MainWindow;
            parentWindow.TransTab.IsSelected = true;
            parentWindow.Hide();
            parentWindow.ShowInTaskbar = true;
            parentWindow.Opacity = 1;
            parentWindow.Show();
            parentWindow.Activate();
            this.Close();
        }

        private void Notice_Content3_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            try
            {
                string strWebERL = common.GetWebURL();
                if (strWebERL != "")
                {
                    System.Diagnostics.Process.Start(strWebERL);
                }

                this.Close();
            }
            catch
            { }
        }

        private void bt_UserRemove(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
	}

    #region
    public partial class NoticeData : INotifyPropertyChanged
    {
        private string _Title;
        private NoticeType  _Noticetype;
        private string _Content;
        private string _Info;

        public NoticeData()
        {
            Title = "";
            noticeType = NoticeType.Ask;
            NoticeCotent = "";
            NoticeInfo = "";
        }

        public NoticeData(string strTitle,NoticeType nt,string strNoticeContent, string strNoticeInfo)
        {
            Title = strTitle;
            noticeType = nt;
            NoticeCotent = strNoticeContent;
            NoticeInfo = strNoticeInfo;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }

        public string Title
        {
            get { return _Title; }
            set
            {
                this._Title= value;
                OnPropertyChanged(new PropertyChangedEventArgs("Title"));
            }
        }

        public NoticeType noticeType
        {
            get { return this._Noticetype; }
            set
            {
                this._Noticetype = value;
                OnPropertyChanged(new PropertyChangedEventArgs("noticeType"));
            }
        }

        public string NoticeCotent
        {
            get { return this._Content; }
            set
            {
                this._Content = value;
                OnPropertyChanged(new PropertyChangedEventArgs("NoticeCotent"));
            }
        }

        public string NoticeInfo
        {
            get { return _Info; }
            set
            {
                this._Info = value;
                OnPropertyChanged(new PropertyChangedEventArgs("NoticeInfo"));
            }
        }
    }
    #endregion

    public class RunNoticeWindow
    {
        private ObservableCollection<NoticeWindow> NoticeWindowCollection = new ObservableCollection<NoticeWindow>();
        public NoticeWindow MDnoticeWindow = null;
        public NoticeWindow MDnoticeCfWindow = null;
        public NoticeWindow noticeWindow = null;
        private Common common = new Common();

        public RunNoticeWindow()
        {
            NoticeWindowCollection.Clear();
        }

        ///<summary>
        ///运行模态提示信息选择窗口
        /// </summary>
        /// <param name="strTitle">窗口标题</param>
        /// <param name="noticeType">提示类型</param>
        /// <param name="strContent">提示内容</param>
        /// <param name="strInfo">提示的备注信息</param>
        /// <returns></returns>
        public void RunNoticeChooseWindow(string strTitle, NoticeType noticeType, string strContent, string strInfo)
        {
            try
            {
                MDnoticeWindow = new NoticeWindow();
                NoticeWindowCollection.Add(MDnoticeWindow);
                MDnoticeWindow.noticeData.Title = strTitle;
                MDnoticeWindow.noticeData.noticeType = noticeType;
                MDnoticeWindow.noticeData.NoticeCotent = strContent;
                MDnoticeWindow.noticeData.NoticeInfo = strInfo;
                MDnoticeWindow.BN_Notice_Cancel.Visibility = Visibility.Visible;
                MDnoticeWindow.IsModeWindow = true;
                MDnoticeWindow.Activate();
                MDnoticeWindow.ShowDialog();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public void RunMDUploadNoticeWindow(string strTitle, NoticeType noticeType, string strContent, string strInfo)
        {
            try
            {
                MDnoticeWindow = new NoticeWindow();
                NoticeWindowCollection.Add(MDnoticeWindow);
                MDnoticeWindow.noticeData.Title = strTitle;
                MDnoticeWindow.noticeData.noticeType = noticeType;
                MDnoticeWindow.noticeData.NoticeCotent = strContent;
                MDnoticeWindow.noticeData.NoticeInfo = strInfo;
                MDnoticeWindow.Notice_Content3.Text = common.GetWebURL();
                MDnoticeWindow.BN_Notice_Cancel.Visibility = Visibility.Collapsed;
                MDnoticeWindow.Notice_Content1.Visibility = Visibility.Visible;
                MDnoticeWindow.Notice_Content2.Visibility = Visibility.Visible;
                MDnoticeWindow.Notice_Content3.Visibility = Visibility.Visible;

                MDnoticeWindow.IsModeWindow = true;
                MDnoticeWindow.Activate();
                MDnoticeWindow.ShowDialog();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public void RunNoticeChildWindow(Window parentWindow, string strContent)
        {
            try
            {
                if (!parentWindow.IsVisible)
                {
                    return;
                }

                foreach (Window ChildWindow in NoticeWindowCollection)
                {
                    noticeWindow = ChildWindow as NoticeWindow;
                    if (noticeWindow.TB_Content.Text == strContent)
                    {
                        noticeWindow.Close();
                    }
                }

                noticeWindow = new NoticeWindow();
                NoticeWindowCollection.Add(noticeWindow);
                noticeWindow.NoticeChildBody.Visibility = Visibility.Visible;
                noticeWindow.NoticeHead.Visibility = Visibility.Collapsed;
                noticeWindow.NoticeBody.Visibility = Visibility.Collapsed;
                noticeWindow.TB_Content.Text = strContent;
                noticeWindow.IsModeWindow = false;
                noticeWindow.NoticeChildBody.Background = new SolidColorBrush(Color.FromArgb(255, 240, 240, 240));
                noticeWindow.Notice_Content.FontSize = 10;
                noticeWindow.Owner = parentWindow;
                noticeWindow.Show();
                noticeWindow.Activate();
                noticeWindow.WindowStartupLocation = WindowStartupLocation.Manual;
                noticeWindow.Left = parentWindow.Left + (parentWindow.Width - noticeWindow.NoticeChildBody.ActualWidth) / 2;
                noticeWindow.Top = parentWindow.Top + 36;

                System.Windows.Threading.DispatcherTimer _timer = new System.Windows.Threading.DispatcherTimer();
                _timer.Interval = new TimeSpan(0, 0, 5);
                _timer.Tick += (s, e) =>
                {
                    this.noticeWindow.Close();
                    _timer.Stop();
                };
                _timer.Start();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        ///<summary>
        ///运行非模态提示信息窗口
        /// </summary>
        /// <param name="strTitle">窗口标题</param>
        /// <param name="noticeType">提示类型</param>
        /// <param name="strContent">提示内容</param>
        /// <param name="strInfo">提示的备注信息</param>
        /// <returns></returns>
        public void RunNoticeConfirmWindow(string strTitle, NoticeType noticeType, string strContent, string strInfo)
        {
            try
            {
                foreach (Window ChildWindow in NoticeWindowCollection)
                {
                    noticeWindow = ChildWindow as NoticeWindow;
                    if (noticeWindow.noticeData.NoticeCotent == strContent)
                    {
                        noticeWindow.Close();
                    }
                }

                noticeWindow = new NoticeWindow();
                NoticeWindowCollection.Add(noticeWindow);
                noticeWindow.noticeData.Title = strTitle;
                noticeWindow.noticeData.noticeType = noticeType;
                noticeWindow.noticeData.NoticeCotent = strContent;
                noticeWindow.noticeData.NoticeInfo = strInfo;
                noticeWindow.BN_Notice_Cancel.Visibility = Visibility.Collapsed;
                noticeWindow.IsModeWindow = false;
                noticeWindow.Show();
                noticeWindow.Activate();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }


        ///<summary>
        ///运行模态提示信息窗口
        /// </summary>
        /// <param name="strOwner">提示窗口的拥有者</param>
        /// <param name="strTitle">窗口标题</param>
        /// <param name="noticeType">提示类型</param>
        /// <param name="strContent">提示内容</param>
        /// <param name="strInfo">提示的备注信息</param>
        /// <returns></returns>
        public void RunMdNoticeConfirmWindow(string strTitle, NoticeType noticeType, string strContent, string strInfo)
        {
            try
            {
                MDnoticeCfWindow = new NoticeWindow();
                NoticeWindowCollection.Add(MDnoticeCfWindow);
                MDnoticeCfWindow.noticeData.Title = strTitle;
                MDnoticeCfWindow.noticeData.noticeType = noticeType;
                MDnoticeCfWindow.noticeData.NoticeCotent = strContent;
                MDnoticeCfWindow.noticeData.NoticeInfo = strInfo;
                MDnoticeCfWindow.BN_Notice_Cancel.Visibility = Visibility.Collapsed;
                MDnoticeCfWindow.IsModeWindow = true;
                MDnoticeCfWindow.Activate();
                MDnoticeCfWindow.ShowDialog();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        public void CloseOwnNoticeWindow()
        {
            try
            {
                foreach (NoticeWindow noticeWindow in NoticeWindowCollection)
                {
                    noticeWindow.Close();
                }

                NoticeWindowCollection.Clear();
            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }

}