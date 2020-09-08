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
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Text.RegularExpressions;

namespace calendarTest.NumericUpDownControl
{
    /// <summary>
    /// Interaction logic for NumericBox.xaml
    /// </summary>

    public partial class NumericBox : UserControl
    {
        private readonly Regex _numMatch;
        private int type = 1;

        /// <summary>Initializes a new instance of the NumericBoxControlLib.NumericBox class.</summary>
        public NumericBox()
        {
            InitializeComponent();
            _numMatch = new Regex(@"^-?\d+$");
            Maximum = int.MaxValue;
            Minimum = 0;
            //初始化
            DateTime dateNow = DateTime.Now;
            hhValue = dateNow.Hour;
            mmValue = dateNow.Minute;
            ssValue = dateNow.Second;
            formatToTimeValue();
        }

        //private void ResetText(TextBox tb)
        //{
        //    tb.Text = 0 < Minimum ? Minimum.ToString() : "0";

        //    tb.SelectAll();
        //}

#region UI event.
        private void Increase_Click(object sender, RoutedEventArgs e)
        {
            if (type ==2)
            {
                if (mmValue < Maximum)
                {
                    mmValue++;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            }
            else if (type ==3)
            {
                if (hhValue < Maximum)
                {
                    hhValue++;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            } 
            else if ( type ==4)
            {
                if (ssValue < Maximum)
                {
                    ssValue++;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            }

           
        }

        private void Decrease_Click(object sender, RoutedEventArgs e)
        {
           if (type == 2)
            {
                if (mmValue > Minimum)
                {
                    mmValue--;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            }
            else if (type == 3)
            {
                if (hhValue > Minimum)
                {
                    hhValue--;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            }
            else if (type == 4)
            {
                if (ssValue > Minimum)
                {
                    ssValue--;
                    RaiseEvent(new RoutedEventArgs(IncreaseClickedEvent));
                }
            }
        }
#endregion

#region  control property

        /// <summary>The Value property represents the TextBoxValue of the control.</summary>
        /// <returns>The current TextBoxValue of the control</returns>      
        public string TimeValue
        {
            get
            {
                return (string)GetValue(TimeValueProperty);
            }
            set
            {
                SetValue(TimeValueProperty, value);
            }
        }


        // Using a DependencyProperty as the backing store for Value.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty TimeValueProperty =
            DependencyProperty.Register("TimeValue", typeof(string), typeof(NumericBox),
              new PropertyMetadata("", new PropertyChangedCallback(OnSomeValuePropertyChanged)));


        private static void OnSomeValuePropertyChanged(
        DependencyObject target, DependencyPropertyChangedEventArgs e)
        {
            //NumericBox numericBox = target as NumericBox;
            //numericBox.TextBoxValue.Text = e.NewValue.ToString();
        }


        /// <summary>
        /// Maximum value for the Numeric Up Down control
        /// </summary>
        public int Maximum
        {
            get { return (int)GetValue(MaximumProperty); }
            set { SetValue(MaximumProperty, value); }
        }

        // Using a DependencyProperty as the backing store for Maximum.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty MaximumProperty =
            DependencyProperty.Register("Maximum", typeof(int), typeof(NumericBox), new UIPropertyMetadata(100));

        /// <summary>
        /// Minimum value of the numeric up down conrol.
        /// </summary>
        public int Minimum
        {
            get { return (int)GetValue(MinimumProperty); }
            set { SetValue(MinimumProperty, value); }
        }

        // Using a DependencyProperty as the backing store for Minimum.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty MinimumProperty =
            DependencyProperty.Register("Minimum", typeof(int), typeof(NumericBox), new UIPropertyMetadata(0));

#endregion



        // Value changed
        private static readonly RoutedEvent ValueChangedEvent =
            EventManager.RegisterRoutedEvent("ValueChanged", RoutingStrategy.Bubble,
            typeof(RoutedEventHandler), typeof(NumericBox));

        /// <summary>The ValueChanged event is called when the TextBoxValue of the control changes.</summary>
        public event RoutedEventHandler ValueChanged
        {
            add { AddHandler(ValueChangedEvent, value); }
            remove { RemoveHandler(ValueChangedEvent, value); }
        }

        //Increase button clicked
        private static readonly RoutedEvent IncreaseClickedEvent =
            EventManager.RegisterRoutedEvent("IncreaseClicked", RoutingStrategy.Bubble,
            typeof(RoutedEventHandler), typeof(NumericBox));

        /// <summary>The IncreaseClicked event is called when the Increase button clicked</summary>
        public event RoutedEventHandler IncreaseClicked
        {
            add { AddHandler(IncreaseClickedEvent, value); }
            remove { RemoveHandler(IncreaseClickedEvent, value); }
        }

        //Increase button clicked
        private static readonly RoutedEvent DecreaseClickedEvent =
            EventManager.RegisterRoutedEvent("DecreaseClicked", RoutingStrategy.Bubble,
            typeof(RoutedEventHandler), typeof(NumericBox));

        /// <summary>The DecreaseClicked event is called when the Decrease button clicked</summary>
        public event RoutedEventHandler DecreaseClicked
        {
            add { AddHandler(DecreaseClickedEvent, value); }
            remove { RemoveHandler(DecreaseClickedEvent, value); }
        }

#region  依赖属性 hh,mm,ss
        public static readonly DependencyProperty hhValueProperty =
           DependencyProperty.Register("hhValue", typeof(int), typeof(NumericBox),
             new PropertyMetadata(0));

        public static readonly DependencyProperty mmValueProperty =
            DependencyProperty.Register("mmValue", typeof(int), typeof(NumericBox),
              new PropertyMetadata(0));

        public static readonly DependencyProperty ssValueProperty =
            DependencyProperty.Register("ssValue", typeof(int), typeof(NumericBox),
              new PropertyMetadata(0));


        public int hhValue
        {
            get
            {

                return (int)GetValue(hhValueProperty);
            }
            set
            {
                if (value < 10)
                {
                    part_Hour.Text = "0"+ value.ToString();
                }
                else
                {
                    part_Hour.Text = value.ToString();
                }
                
                SetValue(hhValueProperty, value);
                formatToTimeValue();
            }
        }


        public int mmValue
        {
            get
            {

                return (int)GetValue(mmValueProperty);
            }
            set
            {
                if (value < 10)
                {
                    part_Minute.Text = "0" + value.ToString();
                }
                else
                {
                    part_Minute.Text = value.ToString();
                }
                SetValue(mmValueProperty, value);
                formatToTimeValue();
            }
        }

        public int ssValue
        {
            get
            {

                return (int)GetValue(ssValueProperty);
            }
            set
            {
                if (value < 10)
                {
                    part_Second.Text = "0" + value.ToString();
                }
                else
                {
                    part_Second.Text = value.ToString();
                }
                SetValue(ssValueProperty, value);
                formatToTimeValue();
            }
        }
        #endregion


#region mm 文本框切换

        private void mm_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 2;
        }

        private void mm_TextChanged(object sender, TextChangedEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 2;
        }

        private void mm_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 2;
        }
#endregion

#region hh 文本框切换

        private void hh_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            Maximum = 23;
            Minimum = 0;
            type = 3;
        }

        private void hh_TextChanged(object sender, TextChangedEventArgs e)
        {
            Maximum = 23;
            Minimum = 0;
            type = 3;
        }

        private void hh_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            Maximum = 23;
            Minimum = 0;
            type = 3;
        }
#endregion

#region ss 文本框切换
       
        private void ss_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 4;
        }

        private void ss_TextChanged(object sender, TextChangedEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 4;
            var tb = sender as TextBox;

            //_numMatch.IsMatch(tb.Text);
            ssValue = Convert.ToInt32(tb.Text);
            if (ssValue < Minimum) ssValue = Minimum;
            if (ssValue > Maximum) ssValue = Maximum;
            string strValue = Convert.ToString(ssValue);
            tb.Text = strValue;
            RaiseEvent(new RoutedEventArgs(ValueChangedEvent));
        }

        private void ss_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 4;
        }
#endregion

        private void hh_MouseIn(object sender, MouseButtonEventArgs e)
        {
            Maximum = 23;
            Minimum = 0;
            type = 3;
        }


        private void mm_MouseIn(object sender, MouseButtonEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 2;
        }

        private void ss_MouseIn(object sender, MouseButtonEventArgs e)
        {
            Maximum = 59;
            Minimum = 0;
            type = 4;
        }

        private void timeList_Click(object sender, RoutedEventArgs e)
        {
            if (timeList.Visibility ==  Visibility.Collapsed)
            {
                timeList.Visibility =Visibility.Visible;
                timeList.SelectedIndex = 0;
            }
        }

        private void list_choicetime(object sender, SelectionChangedEventArgs e)
        {
            int index = (sender as ListBox).SelectedIndex;
            if (index < 24 && index>=0)
            {
                hhValue = index;
                mmValue = 0;
                ssValue = 0;
                timeList.Visibility = Visibility.Collapsed;
            }
        }

        private void hh_check(object sender, RoutedEventArgs e)
        {
            if (hhValue > Maximum)
            {
               hhValue=Maximum;
            }
            else if (hhValue < Minimum)
            {
                hhValue = Minimum;
            }
        }

        private void mm_check(object sender, RoutedEventArgs e)
        {

        }
        private void ss_check(object sender, RoutedEventArgs e)
        {

        }

        private void formatToTimeValue()
        {
            TimeValue = "";
            if (hhValue < 10)
            {
                TimeValue += "0";
            }
            TimeValue += hhValue.ToString() + ":";
            if (mmValue < 10)
            {
                TimeValue += "0";
            }
            TimeValue += mmValue.ToString() + ":";
            if (ssValue < 10)
            {
                TimeValue += "0";
            }
            TimeValue += ssValue.ToString();
        }




    }
}

