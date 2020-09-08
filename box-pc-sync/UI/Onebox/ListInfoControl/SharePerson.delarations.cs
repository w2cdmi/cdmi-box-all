using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Onebox
{
    public partial class MySharePerson : INotifyPropertyChanged
    {

        public MySharePerson(string userName, string styleVisibility)
        {
            UserName = userName;
            StyleVisibility = styleVisibility;
            LoginName = ""; 
            UserId = -1;
            UserName = "";
            Department = "";
            Email="";
            UserType=0; //未经注册]
            ShareRW = 0;

        }

        public MySharePerson()
        {
            UserName = "";
            StyleVisibility = "";
            LoginName = "";
            UserId = -1;
            UserName = "";
            Department = "";
            Email = "";
            UserType = 0; //未经注册
            ShareRW = 0;
        }




        private string styleVisibility;
        public string StyleVisibility
        {
            get { return this.styleVisibility; }
            set
            {
                this.styleVisibility = value;
                OnPropertyChanged(new PropertyChangedEventArgs("StyleVisibility"));
            }
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

        private long userId;
        public long UserId
        {
            get { return this.userId; }
            set
            {
                this.userId = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UserId"));
            }
        }


        private string loginName;
        public string LoginName
        {
            get { return this.loginName; }
            set
            {
                this.loginName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("LoginName"));
            }
        }


        private string email;
        public string Email
        {
            get { return this.email; }
            set
            {
                this.email = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Email"));
            }
        }


        //用户类型：预留 0-未开户shareDrive,1-已开户；
        private UInt16 userType;
        public UInt16 UserType
        {
            get { return this.userType; }
            set
            {
                this.userType = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UserType"));
            }
        }

        private int shareRW;
        public int ShareRW
        {
            get { return this.shareRW; }
            set
            {
                this.shareRW = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ShareRW"));
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
