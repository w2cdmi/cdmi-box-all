using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Onebox
{
    public partial class MyError : INotifyPropertyChanged
    {
        public MyError(string strFilePath, string strErrorCode,string strErrorDes, string strSuggest )
        {
            FilePath = strFilePath;
            TaskErrorCode = strErrorCode;
            ErrorDes = strErrorDes;
            Suggest = strSuggest;
            ListColor = listColor;
            ListSideColor = listSideColor;
        }

        private string filepath;
        public string FilePath
        {
            get { return this.filepath; }
            set
            {
                this.filepath = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FilePath"));
            }
        }

        private string taskerrorcode;
        public string TaskErrorCode
        {
            get { return this.taskerrorcode; }
            set
            {
                this.taskerrorcode = value;
                OnPropertyChanged(new PropertyChangedEventArgs("TaskErrorCode"));
            }
        }

        private string errordes;
        public string ErrorDes
        {
            get { return this.errordes; }
            set
            {
                this.errordes = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ErrorDes"));
            }
        }

        private string suggest;
        public string Suggest
        {
            get { return this.suggest; }
            set
            {
                this.suggest = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Suggest"));
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
