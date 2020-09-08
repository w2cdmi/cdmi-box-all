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

namespace Onebox
{
    /// <summary>
    /// About.xaml 的交互逻辑
    /// </summary>
    public partial class About : Window
    {
        public Common common = new Common();

        private ThriftClient.Client thriftClient = new ThriftClient.Client();

        public About()
        {
            InitializeComponent();
        }

        protected override void OnInitialized(EventArgs e)
        {
                //英文
            System.Globalization.CultureInfo currentCultureInfo =    System.Globalization.CultureInfo .CurrentCulture;

            if (!currentCultureInfo.Name.Equals("zh-CN"))
            {
                Thickness th_appname = new Thickness(40, 0, 0, 0);
                Thickness th_appVersion = new Thickness(40, 0, 0, 0);
                Thickness th_copyRight = new Thickness(24, 0, 0, 0);
                Thickness th_contactIT = new Thickness(8, 0, 0, 0);
                Thickness th_contantDep = new Thickness(94, 0, 0, 0);

                this.lb_appName.Margin = th_appname;
                this.appVersion.Margin = th_appVersion;
                this.lb_copyRight.Margin = th_copyRight;
                this.lb_contactIT.Margin = th_contactIT;
                this.lb_contantDep.Margin = th_contantDep;
            }
            else
            {

                Thickness th_appnameCN = new Thickness(10, 0, 0, 0);
                Thickness th_appVersionCN = new Thickness(38, 0, 0, 0);
                Thickness th_copyRightCN = new Thickness(8, 0, 0, 0);
                Thickness th_contactITCN = new Thickness(8, 0, 0, 0);
                Thickness th_contantDepCN = new Thickness(66, 0, 0, 0);

                this.lb_appName.Margin = th_appnameCN;
                this.appVersion.Margin = th_appVersionCN;
                this.lb_copyRight.Margin = th_copyRightCN;
                this.lb_contactIT.Margin = th_contactITCN;
                this.lb_contantDep.Margin = th_contantDepCN;
            }

            //读取客户端版本号
            ConfigureFileOperation.ConfigureFileRW varIniFileRW = new ConfigureFileOperation.ConfigureFileRW();
            string version = varIniFileRW.IniFileReadValues(ConfigureFileOperation.ConfigureFileRW.CONF_VERSION_SECTION,
                ConfigureFileOperation.ConfigureFileRW.CONF_VERSION_KEY);
            appVersion.Text = version;      
            base.OnInitialized(e);
        }

        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            this.DragMove();
            base.OnMouseLeftButtonDown(e);
        }

        private void CloseButton1_Click_1(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }
}
