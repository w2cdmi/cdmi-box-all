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
using System.IO;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Threading;
using Onebox.ThriftClient;

namespace Onebox.SyncDirTreeView
{
    /// <summary>
    /// SyncDirTree.xaml 的交互逻辑
    /// </summary>
    public partial class SyncDirTree : Window
    {
        private string strFilePath = "";
        private ThriftClient.Client thriftClient = new ThriftClient.Client();
        private ThriftClient.List_Info TopFolderInfo = new ThriftClient.List_Info();
        private ThriftClient.List_Info TopTeamSpaceInfo = new ThriftClient.List_Info();
        private Thread ExpandTreeThread = null;
        private DirectoryRecord dirRecordTeamSpace = null;
        private DirectoryRecord dirRecordTop = null;
        private ObservableCollection<DirectoryRecord> directory = new ObservableCollection<DirectoryRecord>();
        private System.DateTime RecordTime = new System.DateTime();
        private RunNoticeWindow runNoticeWindow = new RunNoticeWindow();
        private DirectoryRecord FixedNode = new DirectoryRecord(-1, (string)Application.Current.Resources["emptyDir"], (int)ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_DEFAULT, false);


        public SyncDirTree(string FilePah)
        {
            strFilePath = FilePah;
            TopFolderInfo.Id = 0;
            TopFolderInfo.Name = "My Onebox";
            TopFolderInfo.Flags = (int)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_SYNC;
            TopTeamSpaceInfo.Id = 0;
            TopTeamSpaceInfo.Name = "My TeamSpace";
            TopTeamSpaceInfo.Flags = (int)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_SYNC;
            InitializeComponent();
            Directory_Load();
        }

        private void Directory_Load()
        {
            dirRecordTeamSpace = new DirectoryRecord(TopTeamSpaceInfo.Id, TopTeamSpaceInfo.Name, TopTeamSpaceInfo.Flags, true);
            dirRecordTeamSpace.Children.Clear();
            dirRecordTeamSpace._isTeamSpaceTopFolder = true;
            long userId = thriftClient.getUserId();
            List<ThriftClient.Teamspace_Membership> UserTeamSpaceList = thriftClient.listTeamspacesByUser(userId);

            long NoMemberTSCnt = 0;

            if (UserTeamSpaceList.Count != 0)
            {
                foreach (ThriftClient.Teamspace_Membership UserTeamSpaceinfoTemp in UserTeamSpaceList)
                {
                    if (UserTeamSpaceinfoTemp.Role == "viewer")
                    {
                        continue;
                    }
                    NoMemberTSCnt++;
                    DirectoryRecord dirRecord1 = new DirectoryRecord(0, UserTeamSpaceinfoTemp.Teamspace.Name, (int)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_SYNC, false);
                    dirRecord1.OwnerId = UserTeamSpaceinfoTemp.Teamspace.Id;
                    dirRecordTeamSpace.Children.Add(dirRecord1);
                    dirRecord1.Children.Add(FixedNode);
                }
            }

            if (NoMemberTSCnt == 0)
            {
                dirRecordTeamSpace.Children.Add(FixedNode);
            }
            directory.Add(dirRecordTeamSpace);

            dirRecordTop = new DirectoryRecord(TopFolderInfo.Id, TopFolderInfo.Name, TopFolderInfo.Flags, true);
            dirRecordTop.Children.Clear();
            dirRecordTop.OwnerId = thriftClient.getUserId();
            LoadingTree_ChideNode(TopFolderInfo, ref dirRecordTop, false);
            directory.Add(dirRecordTop);
            directoryTreeView.ItemsSource = directory;
        }

        private string TransferEncoding(string srcStr)
        {
            byte[] bytes = Encoding.UTF8.GetBytes(srcStr);
            byte[] ret = Encoding.Convert(Encoding.UTF8, Encoding.Unicode, bytes);
            return Encoding.Unicode.GetString(ret);
        }

        private void LoadingTree_ChideNode(ThriftClient.List_Info listInfo, ref DirectoryRecord dirRecord, bool isLoadTwo)
        {
               this.Cursor = Cursors.Wait;
               List<ThriftClient.List_Info> FolderInfoList = thriftClient.listRemoteDir(listInfo.Id, dirRecord.OwnerId);
               dirRecord.Children.Clear();
               if (FolderInfoList.Count != 0)
               {
                   foreach (ThriftClient.List_Info listinfoTemp in FolderInfoList)
                   {
                       int iFlags = 0;
                       if (dirRecord.FolderId != TopFolderInfo.Id)
                       {
                           iFlags = dirRecord.FolderFlags;
                       }
                       else
                       {
                           iFlags = listinfoTemp.Flags;
                       }
                       DirectoryRecord dirRecord1 = new DirectoryRecord(listinfoTemp.Id, listinfoTemp.Name, iFlags, false);
                       dirRecord1.OwnerId = dirRecord.OwnerId;
                       dirRecord.Children.Add(dirRecord1);
                       dirRecord1.Children.Add(FixedNode);
                   }
               }
               else
               {
                   dirRecord.Children.Add(FixedNode);
               }

               this.Cursor = Cursors.Arrow;
        }

        private static bool FindDir(ThriftClient.List_Info listinfo)
        {
            if (listinfo.Type == (int)ThriftClient.File_Type.File_Type_Dir)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            this.DragMove();
            base.OnMouseLeftButtonDown(e);
        }

        protected override void OnClosed(EventArgs e)
        {
            try
            {
                runNoticeWindow.CloseOwnNoticeWindow();
                if (null != ExpandTreeThread)
                {
                    ExpandTreeThread.Interrupt();
                }
            }
            catch { }
            base.OnClosed(e);
        }

        private void Button_Upload_Click(object sender, RoutedEventArgs e)
        {
            DirectoryRecord DirRed = (DirectoryRecord)directoryTreeView.SelectedItem;
            if (DirRed._isTeamSpaceTopFolder)
            {
                return;
            }

            if (null != DirRed)
            {
                thriftClient.upload(strFilePath, DirRed.FolderId, DirRed.OwnerId);
                runNoticeWindow.RunMDUploadNoticeWindow((string)Application.Current.Resources["uploadToCloud"], NoticeType.Right, (string)Application.Current.Resources["uploadAddNotice"], "");
                this.Close();
            }
            else
            {
                runNoticeWindow.RunMdNoticeConfirmWindow((string)Application.Current.Resources["uploadToCloud"], NoticeType.Error, (string)Application.Current.Resources["uploadToCloudMsg"], "");
            }
        }

        private void TreeViewItem_OnExpanded(object sender, RoutedEventArgs e)
        {
            TreeViewItem item = sender as TreeViewItem;
            DirectoryRecord ExpandeItem = (DirectoryRecord)item.DataContext;
            System.DateTime ExpireTime = RecordTime.AddMilliseconds(500);
            if (ExpandeItem._isTeamSpaceTopFolder)
            {
                return;
            } 

            if (-1 == DateTime.Compare(ExpireTime, System.DateTime.Now))
            {
                ThriftClient.List_Info listInfo = new ThriftClient.List_Info();
                listInfo.Id = ExpandeItem.FolderId;
                listInfo.Name = ExpandeItem.FolderName;
                listInfo.Flags = ExpandeItem.FolderFlags;
                LoadingTree_ChideNode(listInfo, ref ExpandeItem, true);
                RecordTime = System.DateTime.Now;
            }
        }

        private void TreeViewItem_Loaded(object sender, RoutedEventArgs e)
        {
            TreeViewItem item = sender as TreeViewItem;
            DirectoryRecord itemdata = (DirectoryRecord)item.DataContext;
            if (-1 == itemdata.FolderId)
            {
                item.IsEnabled = false;
            }

            item.Uid = itemdata.FolderName;
        }

        private void directoryTreeView_Loaded_1(object sender, RoutedEventArgs e)
        {
            TreeViewItem container = directoryTreeView.ItemContainerGenerator.ContainerFromItem(dirRecordTop) as TreeViewItem;
            if (null != container)
            {
                container.IsExpanded = true;
                container.IsSelected = true;
                container.Focus();
            }
        }


        private void TreeViewItem_Selected(object sender, RoutedEventArgs e)
        {
            TreeViewItem container =  sender as TreeViewItem;
            DirectoryRecord itemdata = (DirectoryRecord)container.DataContext;
            if (null != itemdata && itemdata._isTeamSpaceTopFolder)
            {
                container.IsSelected = false;
                container.Focusable = false;
                e.Handled = true;
            }
        }

        private void SyncDirTree_Cancel_Click_1(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void CloseButton1_Click_1(object sender, RoutedEventArgs e)
        {
            this.Close();
        }        
    }

    class DirectoryRecord : INotifyPropertyChanged
    {
        private int _FolderFlags;
        private string _FolderName;
        private string _FullName;
        private long _FolderId;
        private int _ExpandCount;
        private bool _isTopFolder;
        private static Object _LockObj = new Object();
        public ObservableCollection<DirectoryRecord> _Children;
        public bool _isTeamSpaceTopFolder;
        private long _OwnerId;

        public DirectoryRecord(long Id, string strFolderName, int Flags,bool istopfolder)
        {
            FolderFlags = Flags;
            FolderName = strFolderName;
            FullName = strFolderName;
            FolderId = Id;
            ExpandCount = 0;
            isTopFolder = istopfolder;
            _isTeamSpaceTopFolder = false;
            Children = new ObservableCollection<DirectoryRecord>();
        }

        //public void AddChild(DirectoryRecord ObjChild)
        //{
        //    lock (_LockObj)
        //    {
        //        ObjChild.FullName = this.FullName + "/" + ObjChild.FolderName;
        //        Children.Add(ObjChild);
        //    }
        //}

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }

        public int FolderFlags
        {
            get { return this._FolderFlags; }
            set
            {
                this._FolderFlags = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FolderFlags"));
            }
        }

        public string FolderName
        {
            get { return this._FolderName; }
            set
            {
                this._FolderName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FolderName"));
            }
        }

        public string FullName
        {
            get { return this._FullName; }
            set
            {
                this._FullName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FullName"));
            }
        }

        public long FolderId
        {
            get { return this._FolderId; }
            set
            {
                this._FolderId = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FolderId"));
            }
        }

        public long OwnerId
        {
            get { return this._OwnerId; }
            set
            {
                this._OwnerId = value;
                OnPropertyChanged(new PropertyChangedEventArgs("OwnerId"));
            }
        }

        public int ExpandCount
        {
            get { return this._ExpandCount; }
            set
            {
                this._ExpandCount = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ExpandCount"));
            }
        }

        public bool isTopFolder
        {
            get { return this._isTopFolder; }
            set
            {
                this._isTopFolder = value;
                OnPropertyChanged(new PropertyChangedEventArgs("isTopFolder"));
            }
        }

        public ObservableCollection<DirectoryRecord> Children
        {
            get { return this._Children; }
            set
            {
                this._Children = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FolderName"));
            }
        }
    }
}
