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
using System.Globalization;

namespace Onebox
{
    /// <summary>
    /// UsingWizard.xaml 的交互逻辑
    /// </summary>
    public partial class UsingWizard : Window
    {
        public int Num = 1;
        MainWindow ParentWindow;
        private ThriftClient.Client thriftClient = new ThriftClient.Client();

        public UsingWizard(MainWindow parentWindow)
        {
            try
            {
                ParentWindow = parentWindow;
                InitializeComponent();
                //主体内容
                wizard2.Visibility = Visibility.Collapsed;
                wizard3.Visibility = Visibility.Collapsed;
                wizard4.Visibility = Visibility.Collapsed;
                //按钮
                PreviousStep.Visibility = Visibility.Collapsed;
                finishLearning.Visibility = Visibility.Collapsed;


                //step标识
                step2.Visibility = Visibility.Collapsed;
                step3.Visibility = Visibility.Collapsed;
                step4.Visibility = Visibility.Collapsed;

                //国际化处理——图片:非中文显示英文图片
                CultureInfo currentCultureInfo = CultureInfo.CurrentCulture;
                if (!currentCultureInfo.Name.Equals("zh-CN"))
                {
                    string folderName = "pack://application:,,,/Onebox;component/ImageResource/en-US/";
                    string picPath1 = folderName + "Wizard1.png";
                    string picPath2 = folderName + "Wizard2.png";
                    string picPath3 = folderName + "Wizard3.png";
                    string picPath4 = folderName + "Wizard4.png";
                    picForWizard1.Source = new BitmapImage(new Uri(picPath1, UriKind.Absolute));
                    picForWizard2.Source = new BitmapImage(new Uri(picPath2, UriKind.Absolute));
                    picForWizard3.Source = new BitmapImage(new Uri(picPath3, UriKind.Absolute));
                    picForWizard4.Source = new BitmapImage(new Uri(picPath4, UriKind.Absolute));
                }
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

        private void nextUserGuid(object sender, RoutedEventArgs e)
        {
            try
            {
                if (Num == 1)
                {
                    //主体内容
                    this.wizard1.Visibility = Visibility.Collapsed;
                    wizard2.Visibility = Visibility.Visible;
                    //按钮
                    PreviousStep.Visibility = Visibility.Visible;
                    //step标识
                    step1.Visibility = Visibility.Collapsed;
                    step2.Visibility = Visibility.Visible;

                    Num = 2;
                }
                else if (2 == Num)
                {
                    wizard2.Visibility = Visibility.Collapsed;
                    wizard3.Visibility = Visibility.Visible;
                    //setp标识：
                    step2.Visibility = Visibility.Collapsed;
                    step3.Visibility = Visibility.Visible;

                    Num = 3;
                }
                else if (3 == Num)
                {
                    wizard3.Visibility = Visibility.Collapsed;
                    wizard4.Visibility = Visibility.Visible;
                    //step 
                    step3.Visibility = Visibility.Collapsed;
                    step4.Visibility = Visibility.Visible;
                    //按钮
                    exitLearning.Visibility = Visibility.Collapsed;
                    nextStep.Visibility = Visibility.Collapsed;
                    finishLearning.Visibility = Visibility.Visible;
                    Num = 4;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void PreviousUseGuid(object sender, RoutedEventArgs e)
        {
            try
            {
                if (4 == Num)
                {
                    wizard4.Visibility = Visibility.Collapsed;
                    wizard3.Visibility = Visibility.Visible;
                    //setp标识：
                    step4.Visibility = Visibility.Collapsed;
                    step3.Visibility = Visibility.Visible;
                    //按钮
                    finishLearning.Visibility = Visibility.Collapsed;
                    nextStep.Visibility = Visibility.Visible;
                    PreviousStep.Visibility = Visibility.Visible;
                    exitLearning.Visibility = Visibility.Visible;
                    Num = 3;
                }
                else if (3 == Num)
                {
                    wizard3.Visibility = Visibility.Collapsed;
                    wizard2.Visibility = Visibility.Visible;
                    //step 
                    step3.Visibility = Visibility.Collapsed;
                    step2.Visibility = Visibility.Visible;
                    //按钮
                    Num = 2;
                }
                else if (2 == Num)
                {
                    wizard2.Visibility = Visibility.Collapsed;
                    wizard1.Visibility = Visibility.Visible;
                    //setp标识：
                    step2.Visibility = Visibility.Collapsed;
                    step1.Visibility = Visibility.Visible;
                    //按钮
                    PreviousStep.Visibility = Visibility.Collapsed;
                    Num = 1;
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }

        private void exitWizard(object sender, RoutedEventArgs e)
        {
            try
            {
                this.Hide();
                ParentWindow.RunComfirmWindow();
                this.Close();
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }

}
