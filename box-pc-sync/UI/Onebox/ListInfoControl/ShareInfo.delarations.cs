using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace ShareDriverUI
{
    public partial class MyShareInfo : INotifyPropertyChanged
    {
 
        public MyShareInfo(string userName, string department)
        {
            UserName = userName;
            Department = department;

            ListColor = listColor;
            ListSideColor = listSideColor;
        }

 
        private string userName;
        public string UserName
        {
            get { return this.userName; }
            set
            {
                this.userName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UserName"));
            }
        }

        private string department;
        public string Department
        {
            get { return this.department; }
            set
            {
                this.department = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Department"));
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
