using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Onebox
{

    public enum AsyncTaskType
    {
        ATT_Upload,
        ATT_Download,
        ATT_Upload_Manual,
        ATT_Download_Manual,
        ATT_Upload_Attachements,
        ATT_Invalid
    };

    public partial class MyTask : INotifyPropertyChanged  
    {
        public MyTask(string id, string group, AsyncTaskType keyType, string source, long filesize,DateTime createorUpdateTime)
        {
            Id = id;
            TaskGroup = group;
            TaskType = keyType;
            SourcePath = source;
            Progress = 0;
            FileSize=filesize;
            ListColor = listColor;
            ListSideColor = listSideColor;
            CreateorUpdateTime = createorUpdateTime;
        }

        private string id;
        public string Id
        {
            get { return this.id; }
            set 
            { 
                this.id = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Id"));
            }
        }

        private string group;
        public string TaskGroup
        {
            get { return this.group; }
            set
            {
                this.group = value;
                OnPropertyChanged(new PropertyChangedEventArgs("TaskGroup"));
            }
        }

        private string sourcePath;
        public string SourcePath
        {
            get { return this.sourcePath; }
            set 
            { 
                this.sourcePath = value;
                OnPropertyChanged(new PropertyChangedEventArgs("SourcePath"));
            }
        }

        private double progress;
        public double Progress
        {
            get { return this.progress; }
            set 
            { 
                this.progress = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Progress"));
            }
        }

        private AsyncTaskType taskType;
        public AsyncTaskType TaskType
        {
            get { return this.taskType; }
            set 
            {
                this.taskType = value;
                OnPropertyChanged(new PropertyChangedEventArgs("TaskType"));
            }
        }

        private long filesize;
        public long FileSize
        {
            get { return this.filesize; }
            set
            {
                this.filesize = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FileSize"));
            }
        }

        private UInt16 listColor;
        public UInt16 ListColor
        {
            get { return this.listColor; }
            set
            {
                this.listColor = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ListColor"));
            }
        }

        private UInt16 listSideColor;
        public UInt16 ListSideColor
        {
            get { return this.listSideColor; }
            set
            {
                this.listSideColor = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ListSideColor"));
            }
        }

        private DateTime createorUpdateTime;
        public DateTime CreateorUpdateTime
        {
            get { return this.createorUpdateTime; }
            set
            {
                this.createorUpdateTime = value;
                OnPropertyChanged(new PropertyChangedEventArgs("CreateorUpdateTime"));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }
    }
}
