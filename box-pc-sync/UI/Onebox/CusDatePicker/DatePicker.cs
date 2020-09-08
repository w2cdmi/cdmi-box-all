#region Directives
using System;
using System.Windows;
using System.Windows.Media;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Media.Imaging;
using System.Reflection;
using System.Collections;
using System.ComponentModel;
using Onebox.CustomControls;
using System.Text.RegularExpressions;
using vhCalendar;
using Onebox;
#endregion

namespace vhDatePicker
{
    [DefaultEvent("SelectedDateChanged"),
    DefaultProperty("SelectedDate"),
    //TemplatePart(Name = "Part_DateCheckBox", Type = typeof(CheckBox)),
    TemplatePart(Name = "Part_DateTextBox", Type = typeof(TextBox)),
    TemplatePart(Name = "Part_CalendarButton", Type = typeof(Button)),
    TemplatePart(Name = "Part_CalendarGrid", Type = typeof(Grid)),
    TemplatePart(Name = "Part_CalendarPopup", Type = typeof(Popup))]

    public class DatePicker : Control, INotifyPropertyChanged
    {
        #region Fields
        //private string FormatString = "{0:yyyy/MM/dd HH:mm:ss}";
        private string FormatString = "{0:yyyy/MM/dd HH:mm}";
        //正则表达式，全数字
        private readonly Regex _numMatch = new Regex("^[0-9]*$");
        private DateTime dateNow = DateTime.Now;
        //数字范围：小时（0~23），分钟（0~59）
        private int MaxNumber = int.MaxValue;
        private int MinNumber = 0;
        //更改类型：1-时间，2-分钟，3-秒钟；
        private int changeType = 1;
        public RunNoticeWindow runNoticeWindow = new RunNoticeWindow();
        #endregion

        #region Local Properties
        private bool HasInitialized { get; set; }
        #endregion

        #region Local Event Handlers
        /// <summary>
        /// Handles button click, launches Calendar
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void CalendarButton_Click(object sender, RoutedEventArgs e)
        {
            Popup popCalendarPopup = (Popup)FindElement("Part_CalendarPopup");
            if (popCalendarPopup != null)
            {
                //更改:popup对齐方式，以父border框对齐(xmal中处理)，原来为日历按钮对齐；
                //if (CalendarPlacement != PlacementType.Left)
                //{
                //Button button = sender as Button;
                //popCalendarPopup.Placement = PlacementMode.RelativePoint;
                //popCalendarPopup.HorizontalOffset = -(this.ActualWidth + 4);
                //popCalendarPopup.VerticalOffset = this.ActualHeight;
                //}
                //else
                //{
                //popCalendarPopup.HorizontalOffset = 0;
                //popCalendarPopup.VerticalOffset = 0;
                //popCalendarPopup.Placement = PlacementMode.Bottom;
                //}
                popCalendarPopup.IsOpen = true;
            }

            //更改:pop弹出显示时初始化。无指定时间时显示当前时间，有指定时间则显示指定时间
            if (popCalendarPopup.IsOpen == true)
            {
                dateNow = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, dateNow.Hour, dateNow.Minute, 0);
                TextBox hourBox = (TextBox)FindElement("part_Hour");
                TextBox minuteBox = (TextBox)FindElement("part_Minute");

                if (this.Text.Equals(""))
                {
                    hourBox.Text = dateNow.Hour.ToString();
                    minuteBox.Text = dateNow.Minute.ToString();
                }
                else
                {
                    DateTime dtTmp = new DateTime();
                    bool isPaseErr = (DateTime.TryParse(this.Text, out dtTmp));
                    if (isPaseErr)
                    {
                        hourBox.Text = dtTmp.Hour.ToString();
                        minuteBox.Text = dtTmp.Minute.ToString();
                        this.SelectedDate = dtTmp;
                        this.DisplayDate = dtTmp;
                    }
                    else
                    {
                        DateTime NowTime = DateTime.Now;
                        hourBox.Text = NowTime.Hour.ToString();
                        minuteBox.Text = NowTime.Minute.ToString();
                        this.SelectedDate = NowTime;
                        this.DisplayDate = NowTime;
                    }
                }
            }
        }

        /// <summary>
        /// Handles lost focus
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DateTextBox_LostFocus(object sender, RoutedEventArgs e)
        {
            ValidateText();
        }

        /// <summary>
        /// Handles KeyUp
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DateTextBox_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                ValidateText();
            }
        }

        #endregion

        #region Overrides
        /// <summary>
        /// Apply template and bindings
        /// </summary>
        public override void OnApplyTemplate()
        {
            base.OnApplyTemplate();
            // templates was applied
            HasInitialized = true;

            // initialize style
            SetButtonStyle();

            Grid grdCalendar = (Grid)FindElement("Part_CalendarGrid");
            if (grdCalendar != null)
            {
                vhCalendar.Calendar calendar = grdCalendar.Children[0] as vhCalendar.Calendar;
                if (calendar != null)
                {
                    this.Calendar = calendar;
                    calendar.Theme = CalendarTheme;
                    calendar.Height = CalendarHeight;
                    calendar.Width = CalendarWidth;
                }
            }

            // set element bindings
            SetBindings();
            // startup date：注释原因-改为POP显示时进行时间展示初始化
            //dateNow = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, dateNow.Hour, dateNow.Minute, 0);
            //this.Text = String.Format(FormatString, dateNow);
            //TextBox hourBox = (TextBox)FindElement("part_Hour");
            //hourBox.Text = dateNow.Hour.ToString();
            //TextBox minuteBox = (TextBox)FindElement("part_Minute");
            //minuteBox.Text = dateNow.Minute.ToString();
        }
        #endregion

        #region Constructors
        public DatePicker()
        {
            // defaults
            this.MinHeight = 24;
            this.MinWidth = 90;
        }

        static DatePicker()
        {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(DatePicker), new FrameworkPropertyMetadata(typeof(DatePicker)));

            // CalendarPlacement
            PropertyMetadata calendarPlacementMetadata = new PropertyMetadata
            {
                DefaultValue = PlacementType.Right
            };
            CalendarPlacementProperty = DependencyProperty.Register("CalendarPlacement", typeof(PlacementType), typeof(DatePicker), calendarPlacementMetadata);

            //// IsCheckable
            //FrameworkPropertyMetadata isCheckablePropertyMetadata = new FrameworkPropertyMetadata
            //{
            //    DefaultValue = false,
            //    PropertyChangedCallback = new PropertyChangedCallback(OnIsCheckableChanged), 
            //    AffectsRender = true
            //};
            //IsCheckableProperty = DependencyProperty.Register("IsCheckable", typeof(bool), typeof(DatePicker), isCheckablePropertyMetadata);

            //// IsCheckedProperty
            //FrameworkPropertyMetadata isCheckedPropertyMetadata = new FrameworkPropertyMetadata
            //{
            //    DefaultValue = false,
            //    PropertyChangedCallback = new PropertyChangedCallback(OnIsCheckedChanged), 
            //};
            //IsCheckedProperty = DependencyProperty.Register("IsChecked", typeof(bool), typeof(DatePicker), isCheckedPropertyMetadata);

            // DateFormat
            FrameworkPropertyMetadata dateFormatMetadata = new FrameworkPropertyMetadata
            {
                DefaultValue = DateFormatType.yyyymmddHHmmss,
                PropertyChangedCallback = new PropertyChangedCallback(OnDateFormatChanged), 
                AffectsRender = true
            };
            DateFormatProperty = DependencyProperty.Register("DateFormat", typeof(DateFormatType), typeof(DatePicker), dateFormatMetadata);

            // ButtonStyle
            FrameworkPropertyMetadata buttonStyleMetadata = new FrameworkPropertyMetadata
            {
                DefaultValue = ButtonType.Image,
                PropertyChangedCallback = new PropertyChangedCallback(OnButtonStyleChanged),
                AffectsRender = true
            };
            ButtonStyleProperty = DependencyProperty.Register("ButtonStyle", typeof(ButtonType), typeof(DatePicker), buttonStyleMetadata);

            // SelectedDate
            PropertyMetadata selectedDateMetadata = new PropertyMetadata
            {
                CoerceValueCallback = CoerceDateToBeInRange,
                //DefaultValue = DateTime.Today,
                PropertyChangedCallback = new PropertyChangedCallback(OnSelectedDateChanged)
            };
            SelectedDateProperty = DependencyProperty.Register("SelectedDate", typeof(DateTime), typeof(DatePicker), selectedDateMetadata);

            // DisplayDate
            PropertyMetadata displayDateMetadata = new PropertyMetadata
            {
                CoerceValueCallback = CoerceDateToBeInRange,
                //DefaultValue = DateTime.Today,
                PropertyChangedCallback = new PropertyChangedCallback(OnDisplayDateChanged)
            };
            DisplayDateProperty = DependencyProperty.Register("DisplayDate", typeof(DateTime), typeof(DatePicker), displayDateMetadata);

            // Text
            PropertyMetadata textMetadata = new PropertyMetadata
            {
                //DefaultValue = DateTime.Today.ToString(),
                PropertyChangedCallback = new PropertyChangedCallback(OnTextChanged)
            };
            TextProperty = DependencyProperty.Register("Text", typeof(string), typeof(DatePicker), textMetadata);
            
            #region  添加属性   ---添加人：刘国霞
            //Value:供调用DatePicker方，可通过value获取组件值；
            PropertyMetadata ValueMetadata = new PropertyMetadata
            {
                //DefaultValue = DateTime.Today.ToString(),
                PropertyChangedCallback = new PropertyChangedCallback(OnValueChanged)
            };
            ValueProperty = DependencyProperty.Register("Value", typeof(string), typeof(DatePicker), ValueMetadata);

            //SelectDateTime
            PropertyMetadata SelectDateTimeMetadate = new PropertyMetadata
            {
                DefaultValue = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day, DateTime.Now.Hour, DateTime.Now.Minute, 0),
                PropertyChangedCallback = new PropertyChangedCallback(OnSelectDateTimeChanged)
            };
            SelectDateTimeProperty = DependencyProperty.Register("SelectDateTime", typeof(DateTime), typeof(DatePicker), SelectDateTimeMetadate);
            #endregion 添加属性   ---添加人：刘国霞

            // DisplayDateStart
            PropertyMetadata displayDateStartMetaData = new PropertyMetadata
            {
                CoerceValueCallback = CoerceDateToBeInRange,
                //DefaultValue = new DateTime(1, 1, 1),
                DefaultValue = DateTime.Today,
            };
            DisplayDateStartProperty = DependencyProperty.Register("DisplayDateStart", typeof(DateTime), typeof(DatePicker), displayDateStartMetaData);

            // DisplayDateEnd
            PropertyMetadata displayDateEndMetaData = new PropertyMetadata
            {
                DefaultValue = new DateTime(2199, 1, 1),
                CoerceValueCallback = new CoerceValueCallback(CoerceDisplayDateEnd)
            };
            DisplayDateEndProperty = DependencyProperty.Register("DisplayDateEnd", typeof(DateTime), typeof(DatePicker), displayDateEndMetaData);

            // CalendarTheme
            FrameworkPropertyMetadata calendarThemeMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = "AeroNormal",
                CoerceValueCallback = new CoerceValueCallback(CoerceCalendarTheme),
                PropertyChangedCallback = new PropertyChangedCallback(OnThemeChanged),
                AffectsRender = true
            };
            CalendarThemeProperty = DependencyProperty.Register("CalendarTheme", typeof(string), typeof(DatePicker), calendarThemeMetaData);

            // CalendarHeight
            FrameworkPropertyMetadata calendarHeightMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = (double)175,
                CoerceValueCallback = new CoerceValueCallback(CoerceCalendarSize),
                AffectsRender = true
            };
            CalendarHeightProperty = DependencyProperty.Register("CalendarHeight", typeof(double), typeof(DatePicker), calendarHeightMetaData);

            // CalendarWidth
            FrameworkPropertyMetadata calendarWidthMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = (double)175,
                CoerceValueCallback = new CoerceValueCallback(CoerceCalendarSize),
                AffectsRender = true
            };
            CalendarWidthProperty = DependencyProperty.Register("CalendarWidth", typeof(double), typeof(DatePicker), calendarWidthMetaData);

            // FooterVisibility
            FrameworkPropertyMetadata footerVisibilityMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = Visibility.Collapsed,
                AffectsRender = true
            };
            FooterVisibilityProperty = DependencyProperty.Register("FooterVisibility", typeof(Visibility), typeof(DatePicker), footerVisibilityMetaData);

            // IsReadOnly
            PropertyMetadata isReadOnlyMetaData = new PropertyMetadata
            {
                DefaultValue = true,
                PropertyChangedCallback = new PropertyChangedCallback(OnReadOnlyChanged)
            };
            IsReadOnlyProperty = DependencyProperty.Register("IsReadOnly", typeof(bool), typeof(DatePicker), isReadOnlyMetaData);

            // WeekColumnVisibility
            FrameworkPropertyMetadata weekColumnVisibilityMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = Visibility.Collapsed,
                AffectsRender = true
            };
            WeekColumnVisibilityProperty = DependencyProperty.Register("WeekColumnVisibility", typeof(Visibility), typeof(DatePicker), weekColumnVisibilityMetaData);

            // ButtonBackgroundBrushProperty
            FrameworkPropertyMetadata buttonBackgroundBrushMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = null,
                AffectsRender = true
            };
            ButtonBackgroundBrushProperty = DependencyProperty.Register("ButtonBackgroundBrush", typeof(Brush), typeof(DatePicker), buttonBackgroundBrushMetaData);

            // ButtonBorderBrushProperty
            FrameworkPropertyMetadata buttonBorderBrushPropertyMetaData = new FrameworkPropertyMetadata
            {
                DefaultValue = null,
                AffectsRender = true
            };
            ButtonBorderBrushProperty = DependencyProperty.Register("ButtonBorderBrush", typeof(Brush), typeof(DatePicker), buttonBorderBrushPropertyMetaData);


            // SelectedDateChanged event registration
            SelectedDateChangedEvent =
                EventManager.RegisterRoutedEvent("SelectedDateChanged", RoutingStrategy.Bubble, typeof(RoutedEventHandler), typeof(DatePicker));
        }

        #region INotifyPropertyChanged Members
        /// <summary>
        /// Event raised when a property is changed
        /// </summary>
        public event PropertyChangedEventHandler PropertyChanged;

        /// <summary>
        /// Raises the property changed event
        /// </summary>
        /// <param name="e">The arguments to pass</param>
        protected void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, e);
            }
        }
        #endregion
        #endregion

        #region Properties
        #region ButtonStyle
        /// <summary>
        /// Gets/Sets the button appearence
        /// </summary>
        public static readonly DependencyProperty ButtonStyleProperty;

        public ButtonType ButtonStyle
        {
            get { return (ButtonType)GetValue(ButtonStyleProperty); }
            set { SetValue(ButtonStyleProperty, value); }
        }

        private static void OnButtonStyleChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            dp.SetButtonStyle();
        }

        private void SetButtonStyle()
        {
            if (this.Template != null)
            {
                Button button = (Button)FindElement("Part_CalendarButton");
                Image buttonImage = (Image)FindElement("Part_ButtonImage");
                if (button != null || buttonImage != null)
                {
                    if (ButtonStyle == ButtonType.Brush)
                    {
                        buttonImage.Source = null;
                        button.Style = (Style)this.TryFindResource("ButtonBrushStyle");
                    }
                    else
                    {
                        button.Style = (Style)this.TryFindResource("ButtonImageStyle");
                        BitmapImage img = new BitmapImage();
                        img.BeginInit();
                        img.UriSource = new Uri("pack://application:,,,/Onebox;component/ImageResource/calendar.png", UriKind.Absolute);
                        img.EndInit();
                        buttonImage.Source = img;
                    }
                }
            }
        }
        #endregion

        #region ButtonBackgroundBrush
        public static readonly DependencyProperty ButtonBackgroundBrushProperty;

        public Brush ButtonBackgroundBrush
        {
            get { return (Brush)GetValue(ButtonBackgroundBrushProperty); }
            set { SetValue(ButtonBackgroundBrushProperty, value); }
        }

        private static void OnButtonBackgroundBrushChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            Button button = (Button)dp.FindElement("Part_CalendarButton");
            Brush brush = (Brush)e.NewValue;

            if (button != null && brush != null)
            {
                button.Background = brush;
            }
        }
        #endregion

        #region ButtonBorderBrush
        public static readonly DependencyProperty ButtonBorderBrushProperty;

        public Brush ButtonBorderBrush
        {
            get { return (Brush)GetValue(ButtonBorderBrushProperty); }
            set { SetValue(ButtonBorderBrushProperty, value); }
        }

        private static void OnButtonBorderBrushChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            Button button = (Button)dp.FindElement("Part_CalendarButton");
            Brush brush = (Brush)e.NewValue;

            if (button != null && brush != null)
            {
                button.BorderBrush = brush;
            }
        }
        #endregion

        #region Calendar Object
        /// <summary>
        /// Gets/Sets Calendar object
        /// </summary>
        public vhCalendar.Calendar Calendar { get; set; }
        #endregion

        #region CalendarHeight
        /// <summary>
        /// Gets/Sets calendar height
        /// </summary>
        public static readonly DependencyProperty CalendarHeightProperty;

        public double CalendarHeight
        {
            get { return (double)GetValue(CalendarHeightProperty); }
            set { SetValue(CalendarHeightProperty, value); }
        }

        private static object CoerceCalendarSize(DependencyObject d, object o)
        {
            DatePicker pdp = d as DatePicker;
            double value = (double)o;

            if (value < 160)
            {
                return 160;
            }

            if (value > 350)
            {
                return 350;
            }

            return o;
        }
        #endregion

        #region CalendarPlacement
        /// <summary>
        /// Gets/Sets calendar relative position
        /// </summary>
        public static readonly DependencyProperty CalendarPlacementProperty;

        public PlacementType CalendarPlacement
        {
            get { return (PlacementType)GetValue(CalendarPlacementProperty); }
            set { SetValue(CalendarPlacementProperty, value); }
        }
        #endregion

        #region CalendarWidth
        /// <summary>Gets/Sets calendar width</summary>
        public static readonly DependencyProperty CalendarWidthProperty;

        public double CalendarWidth
        {
            get { return (double)GetValue(CalendarWidthProperty); }
            set { SetValue(CalendarWidthProperty, value); }
        }
        #endregion

        #region CalendarTheme
        /// <summary>
        /// Change the Calendar element theme
        /// </summary>
        public static readonly DependencyProperty CalendarThemeProperty;

        public string CalendarTheme
        {
            get { return (string)GetValue(CalendarThemeProperty); }
            set { SetValue(CalendarThemeProperty, value); }
        }

        private static object CoerceCalendarTheme(DependencyObject d, object o)
        {
            DatePicker pdp = d as DatePicker;
            string value = (string)o;
            if (string.IsNullOrEmpty(value))
                return "AeroNormal";
            return o;
        }

        private static void OnThemeChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;

            if (dp.Template != null)
            {
                Grid grid = (Grid)dp.FindElement("Part_CalendarGrid");
                if (grid != null)
                {
                    vhCalendar.Calendar cld = grid.Children[0] as vhCalendar.Calendar;
                    if (cld != null)
                    {
                        // test against legit value
                        ArrayList themes = GetEnumArray(typeof(Themes));
                        foreach (string ot in themes)
                        {
                            if ((string)e.NewValue == ot)
                            {
                                cld.Theme = (string)e.NewValue;
                                break;
                            }
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Converts enum members to string
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        private static ArrayList GetEnumArray(Type type)
        {
            FieldInfo[] info = type.GetFields();
            ArrayList fields = new ArrayList();

            foreach (FieldInfo fInfo in info)
            {
                fields.Add(fInfo.Name);
            }

            return fields;
        } 
        #endregion

        #region SelectedDate
        /// <summary>
        /// Gets/Sets currently selected date
        /// </summary>
        public static readonly DependencyProperty SelectedDateProperty;

        public DateTime SelectedDate
        {
            get { return (DateTime)GetValue(SelectedDateProperty); }
            set { SetValue(SelectedDateProperty, value); }
        }

        private static object CoerceDateToBeInRange(DependencyObject d, object o)
        {
            DatePicker dp = d as DatePicker;
            DateTime value = (DateTime)o;

            if (value < dp.DisplayDateStart)
            {
                return dp.DisplayDateStart;
            }
            if (value > dp.DisplayDateEnd)
            {
                return dp.DisplayDateEnd;
            }

            return o;
        }
        #endregion

        #region DateFormat
        /// <summary>
        /// Gets/Sets date display format
        /// </summary>
        public static readonly DependencyProperty DateFormatProperty;

        public DateFormatType DateFormat
        {
            get { return (DateFormatType)GetValue(DateFormatProperty); }
            set { SetValue(DateFormatProperty, value); }
        }

        private static void OnDateFormatChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            DateFormatType df = (DateFormatType)e.NewValue;
            
            switch (df)
            {
                case DateFormatType.MMMMddddyyyy:
                    dp.FormatString = "{0:dddd MMMM dd, yyyy}";
                    break;
                case DateFormatType.ddMMMyy:
                    dp.FormatString = "{0:dd MMM, yy}";
                    break;
                case DateFormatType.ddMMMyyyy:
                    dp.FormatString = "{0:dd MMM, yyyy}";
                    break;
                case DateFormatType.Mddyyyy:
                    dp.FormatString = "{0:M d, yyyy}";
                    break;
                case DateFormatType.Mdyy:
                    dp.FormatString = "{0:M d, yy}";
                    break;
                case DateFormatType.Mdyyyy:
                    dp.FormatString = "{0:M d, yyyy}";
                    break;
                case DateFormatType.MMMddyyyy:
                    dp.FormatString = "{0:MMM dd, yyyy}";
                    break;
                case DateFormatType.yyMMdd:
                    dp.FormatString = "{0:yy MM, dd}";
                    break;
                case DateFormatType.yyyyMMdd:
                    dp.FormatString = "{0:yyyy MM, dd}";
                    break;
                case DateFormatType.yyyymmddHHmmss:
                    //dp.FormatString = "{0:yyyy/MM/dd HH:mm:ss}";
                    dp.FormatString = "{0:yyyy/MM/dd HH:mm}";
                    break;
            }
            dp.Text = String.Format(dp.FormatString, dp.DisplayDate);
        }
        #endregion

        #region DispayDate
        /// <summary>
        /// Gets/Sets currently displayed date
        /// </summary>
        public static readonly DependencyProperty DisplayDateProperty;

        public DateTime DisplayDate
        {
            get { return (DateTime)GetValue(DisplayDateProperty); }
            set { SetValue(DisplayDateProperty, value); }
        }

        private static void OnDisplayDateChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
        }
        #endregion

        #region DisplayDateStart
        /// <summary>
        /// Gets/Sets the minimum date that can be displayed
        /// </summary>
        public static readonly DependencyProperty DisplayDateStartProperty;

        public DateTime DisplayDateStart
        {
            get { return (DateTime)GetValue(DisplayDateStartProperty); }
            set { SetValue(DisplayDateStartProperty, value); }
        }
        #endregion

        #region DisplayDateEnd
        /// <summary>
        /// Gets/Sets the maximum date that is displayed, and can be selected
        /// </summary>
        public static readonly DependencyProperty DisplayDateEndProperty;

        public DateTime DisplayDateEnd
        {
            get { return (DateTime)GetValue(DisplayDateEndProperty); }
            set { SetValue(DisplayDateEndProperty, value); }
        }

        private static object CoerceDisplayDateEnd(DependencyObject d, object o)
        {
            DatePicker dp = d as DatePicker;
            DateTime value = (DateTime)o;

            if (value < dp.DisplayDateStart)
            {
                return dp.DisplayDateStart;
            }
            return o;
        }
        #endregion

        #region FooterVisibility
        /// <summary>
        /// Gets/Sets the footer visibility</summary>
        public static readonly DependencyProperty FooterVisibilityProperty;

        public Visibility FooterVisibility
        {
            get { return (Visibility)GetValue(FooterVisibilityProperty); }
            set { SetValue(FooterVisibilityProperty, value); }
        }
        #endregion

        //#region IsCheckable
        ///// <summary>
        ///// Gets/Sets control contains checkbox control
        ///// </summary>
        //public static readonly DependencyProperty IsCheckableProperty;

        //public bool IsCheckable
        //{
        //    get { return (bool)GetValue(IsCheckableProperty); }
        //    set { SetValue(IsCheckableProperty, value); }
        //}

        //private static void OnIsCheckableChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        //{
        //    DatePicker dp = o as DatePicker;
        //    bool value = (bool)e.NewValue;
        //    CheckBox cb = (CheckBox)dp.FindElement("Part_DateCheckBox");

        //    if (cb != null)
        //    {
        //        if (value == false)
        //        {
        //            cb.Visibility = Visibility.Collapsed;
        //        }
        //        else
        //        {
        //            cb.Visibility = Visibility.Visible;
        //        }
        //    }

        //}
        //#endregion

        //#region IsChecked
        ///// <summary>OnIsCheckedChanged
        ///// Gets/Sets checkbox control check state
        ///// </summary>
        //public static readonly DependencyProperty IsCheckedProperty;

        //public bool IsChecked
        //{
        //    get { return (bool)GetValue(IsCheckedProperty); }
        //    set { SetValue(IsCheckedProperty, value); }
        //}

        //private static void OnIsCheckedChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        //{
        //    DatePicker dp = o as DatePicker;
        //    bool value = (bool)e.NewValue;
        //    CheckBox cb = (CheckBox)dp.FindElement("Part_DateCheckBox");

        //    if (cb != null)
        //    {
        //        if (value == false)
        //        {
        //            cb.IsChecked = false;
        //        }
        //        else
        //        {
        //            cb.IsChecked = true;
        //        }
        //    }

        //}
        //#endregion

        #region IsReadOnly
        /// <summary>
        /// Gets/Sets calendar text edit
        /// </summary>
        public static readonly DependencyProperty IsReadOnlyProperty;

        public bool IsReadOnly
        {
            get { return (bool)GetValue(IsReadOnlyProperty); }
            set { SetValue(IsReadOnlyProperty, value); }
        }

        private static void OnReadOnlyChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            if (dp.Template != null)
            {
                TextBox tb = (TextBox)dp.FindElement("Part_DateTextBox");
                if (tb != null)
                {
                    tb.IsReadOnly = (bool)e.NewValue;
                }
            }
        }
        #endregion

        #region Text
        /// <summary>
        /// Gets/Sets text displayed in textbox
        /// </summary>
        public static readonly DependencyProperty TextProperty;

        public string Text
        {
            get { return (string)GetValue(TextProperty); }
            set { SetValue(TextProperty, value); }
        }

        private static void OnTextChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            dp.Value = dp.Text;
        }
        #endregion

#region  属性添加 by liuguoxia

        #region Value
        // Value
        public static readonly DependencyProperty ValueProperty ;
        public string Value
        {
            get { return (string)GetValue(ValueProperty); }
            set 
            { 
                SetValue(ValueProperty, value); 
            }
        }
        private static void OnValueChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
        }
        #endregion

        #region SelectDateTime
        public static readonly DependencyProperty SelectDateTimeProperty;
        public DateTime SelectDateTime
        {
            get { return (DateTime)GetValue(SelectDateTimeProperty); }
            set
            {
                SetValue(SelectDateTimeProperty, value);
            }
        }
        private static void OnSelectDateTimeChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            string xx = e.Property.GetType().ToString();
            dp.Text = String.Format(dp.FormatString,e.NewValue);
        }
        #endregion

#endregion 属性添加 by liuguoxia


        #region WeekColumnVisibility
        /// <summary>
        /// Gets/Sets the week column visibility
        /// </summary>
        public static readonly DependencyProperty WeekColumnVisibilityProperty;

        public Visibility WeekColumnVisibility
        {
            get { return (Visibility)GetValue(WeekColumnVisibilityProperty); }
            set { SetValue(WeekColumnVisibilityProperty, value); }
        }
        #endregion
        #endregion

        #region SelectedDateChangedEvent
        public static readonly RoutedEvent SelectedDateChangedEvent;

        public event RoutedEventHandler SelectedDateChanged
        {
            add { AddHandler(SelectedDateChangedEvent, value); }
            remove { RemoveHandler(SelectedDateChangedEvent, value); }
        }

        private static void OnSelectedDateChanged(DependencyObject o, DependencyPropertyChangedEventArgs e)
        {
            DatePicker dp = o as DatePicker;
            DateTime tmp = (DateTime)e.NewValue;
            dp.SelectDateTime = new DateTime(tmp.Year, tmp.Month, tmp.Day, dp.SelectDateTime.Hour, dp.SelectDateTime.Minute,dp.SelectDateTime.Second);
			//dp.Text = String.Format(dp.FormatString, e.NewValue);//add 0410
            dp.RaiseEvent(new RoutedEventArgs(SelectedDateChangedEvent, dp));
        }
        #endregion

        #region Control Methods
        /// <summary>
        /// Find element in the template
        /// </summary>
        private object FindElement(string name)
        {
            try
            {
                if (HasInitialized)
                {
                    return this.Template.FindName(name, this);
                }
                else
                {
                    return null;
                }
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// Bind elements to control
        /// </summary>
        private void SetBindings()
        {
            TextBox textbox = (TextBox)FindElement("Part_DateTextBox");
            if (textbox != null)
            {
                Binding textBinding = new Binding
                {
                    Source = textbox,
                    Path = new PropertyPath("Text"),
                    Mode = BindingMode.TwoWay,
                };
                this.SetBinding(TextProperty, textBinding);

                textbox.LostFocus += new RoutedEventHandler(DateTextBox_LostFocus);
                textbox.KeyUp += new KeyEventHandler(DateTextBox_KeyUp);
            }

            Button button = (Button)FindElement("Part_CalendarButton");
            if (button != null)
            {
                button.Click += new RoutedEventHandler(CalendarButton_Click);
            }

            //CheckBox checkbox = (CheckBox)FindElement("Part_DateCheckBox");
            //if (checkbox != null)
            //{
            //    if (IsCheckable)
            //    {
            //        checkbox.Visibility = Visibility.Visible;
            //    }
            //    else
            //    {
            //        checkbox.Visibility = Visibility.Collapsed;
            //    }
            //}


            #region 添加时间选择框 by liuguoxia
            TextBox hourbox = (TextBox)FindElement("part_Hour");
            if (hourbox != null)
            {
                //PreviewGotKeyboardFocus
                hourbox.KeyDown += new KeyEventHandler(hh_keyDown);
                hourbox.PreviewMouseLeftButtonDown += new MouseButtonEventHandler(hh_MouseIn);
                hourbox.LostFocus += new RoutedEventHandler(hh_FoucusOut);
                hourbox.TextChanged += new TextChangedEventHandler(hh_TextChanged);
            }

            TextBox minuteBox = (TextBox)FindElement("part_Minute");
            if (minuteBox != null)
            {
                minuteBox.KeyDown += new KeyEventHandler(mm_keyDown);
                minuteBox.PreviewMouseLeftButtonDown += new MouseButtonEventHandler(mm_MouseIn);
                minuteBox.LostFocus += new RoutedEventHandler(mm_FoucusOut);
                minuteBox.TextChanged += new TextChangedEventHandler(mm_TextChanged);
            }


            TextBox secondBox = (TextBox)FindElement("part_Second");
            if (secondBox != null)
            {
                secondBox.PreviewKeyDown += new KeyEventHandler(ss_keyDown);
                secondBox.PreviewMouseLeftButtonDown += new MouseButtonEventHandler(ss_MouseIn);
            }

            ImageButton btnIncrease = (ImageButton)FindElement("part_NumIncrease");
            if (btnIncrease!=null)
            {
                btnIncrease.Click += new RoutedEventHandler(Increase_Click);
            }


            ImageButton btnDecrease = (ImageButton)FindElement("part_NumDecrease");
            if (btnDecrease != null)
            {
                btnDecrease.Click += new RoutedEventHandler(Decrease_Click);
            }
            #endregion 添加时间选择框

            Binding calendarWeekColumnBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("FooterVisibility"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(FooterVisibilityProperty, calendarWeekColumnBinding);

            Binding calendarFooterBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("WeekColumnVisibility"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(WeekColumnVisibilityProperty, calendarFooterBinding);

            Binding calendarHeightBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("Height"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(CalendarHeightProperty, calendarHeightBinding);

            Binding calendarWidthBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("Width"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(CalendarWidthProperty, calendarWidthBinding);

            Binding selectedDateBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("SelectedDate"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(SelectedDateProperty, selectedDateBinding);

            Binding displayDateBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("DisplayDate"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(DisplayDateProperty, displayDateBinding);

            Binding displayDateStartBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("DisplayDateStart"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(DisplayDateStartProperty, displayDateStartBinding);

            Binding displayDateEndBinding = new Binding
            {
                Source = this.Calendar,
                Path = new PropertyPath("DisplayDateEnd"),
                Mode = BindingMode.TwoWay,
            };
            this.SetBinding(DisplayDateEndProperty, displayDateEndBinding);
        }

        /// <summary>
        /// Convert date to to text
        /// </summary>
        private void ValidateText()
        {
            DateTime date;
            TextBox textbox = (TextBox)FindElement("Part_DateTextBox");

            if (textbox != null)
            {
                if(textbox.Text.Equals(""))
                {
                    this.Text = "";
                    return;
                }

                if (DateTime.TryParse(textbox.Text, out date))
                {
                    this.SelectedDate = date;
                    this.DisplayDate = date;

                    if (date.AddMinutes(5) < DateTime.Now)
                    {
                        runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                          (string)Application.Current.Resources["dateTimeErr"], "");
                        if (true == runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused)
                        {
                            this.Text = "";
                            return;
                        }

                        return;
                    }
                }
                else
                {
                    runNoticeWindow.RunNoticeChooseWindow((string)Application.Current.Resources["timeError"], NoticeType.Error,
                         (string)Application.Current.Resources["dateTimeErr"], "");

                    if (true == runNoticeWindow.MDnoticeWindow.BN_Notice_OK.IsFocused)
                    {
                        this.Text = "";
                        return;
                    }

                    return;
                }
                this.Text = String.Format(FormatString, this.SelectedDate);
            }
        }
        #endregion


        
        #region 时间-hour框操作
        private void hh_keyDown(object sender, KeyEventArgs e)
        {
            TextBox txt = sender as TextBox;

            //屏蔽非法按键
            if ((e.Key >= Key.NumPad0 && e.Key <= Key.NumPad9) || e.Key == Key.Decimal)
            {
                if (txt.Text.Contains(".") && e.Key == Key.Decimal)
                {
                    e.Handled = true;
                    return;
                }
                e.Handled = false;
            }
            else if (((e.Key >= Key.D0 && e.Key <= Key.D9) || e.Key == Key.OemPeriod) && e.KeyboardDevice.Modifiers != ModifierKeys.Shift)
            {
                if (txt.Text.Contains(".") && e.Key == Key.OemPeriod)
                {
                    e.Handled = true;
                    return;
                }
                e.Handled = false;
            }
            else
            {
                e.Handled = true;
            }
        }

        private void hh_MouseIn(object sender, MouseButtonEventArgs e)
        {
            MaxNumber = 23;
            changeType = 1;
        }

        private void hh_FoucusOut(object sender, RoutedEventArgs e)
        {
            hour_valueCheck();
        }

        private void hh_TextChanged(object sender, TextChangedEventArgs e)
        {
            try
            {
                //屏蔽中文输入和非法字符粘贴输入
                TextBox textBox = sender as TextBox;
                TextChange[] change = new TextChange[e.Changes.Count];
                e.Changes.CopyTo(change, 0);

                int offset = change[0].Offset;
                if (change[0].AddedLength > 0)
                {
                    if (!_numMatch.IsMatch(textBox.Text))
                    {
                        textBox.Text = textBox.Text.Remove(offset, change[0].AddedLength);
                        textBox.Select(offset, 0);
                        return;
                    }
                }

                if (textBox.Text != "")
                {
                    SelectDateTime = new DateTime(SelectDateTime.Year, SelectDateTime.Month, SelectDateTime.Day,
                                                             Convert.ToInt32(textBox.Text), SelectDateTime.Minute, 0);
                    this.Text = String.Format(FormatString, this.SelectDateTime);
                }
            }
            catch { }
        }

        private bool  hour_valueCheck()
        {
            MaxNumber = 23;
            TextBox hhbox = (TextBox)FindElement("part_Hour");
            int hhValue = 0;

            if (hhbox.Text != "")
            {
                string inputTx = hhbox.Text;

                for (int i = 0; i < inputTx.Length; i++)
                {
                    if (!_numMatch.IsMatch(inputTx[i].ToString()))
                    {
                        return false;
                    }
                }

                hhValue = Convert.ToInt32(hhbox.Text);
            }
            if (hhValue > MaxNumber)
            {
                hhValue = MaxNumber;
            }
            else if (hhValue < MinNumber)
            {
                hhValue = MinNumber;
            }

            hhbox.Text = hhValue.ToString();
            SelectDateTime = new DateTime(SelectDateTime.Year, SelectDateTime.Month, SelectDateTime.Day,
                                                              hhValue, SelectDateTime.Minute, 0);

            this.Text = String.Format(FormatString, this.SelectDateTime);
            return true;
        }
        #endregion

        #region 时间-minute框操作
        private void mm_keyDown(object sender,  KeyEventArgs e)
        {
            TextBox txt = sender as TextBox;
            //屏蔽非法按键
            if ((e.Key >= Key.NumPad0 && e.Key <= Key.NumPad9) || e.Key == Key.Decimal)
            {
                if (txt.Text.Contains(".") && e.Key == Key.Decimal)
                {
                    e.Handled = true;
                    return;
                }
                e.Handled = false;
            }
            else if (((e.Key >= Key.D0 && e.Key <= Key.D9) || e.Key == Key.OemPeriod) && e.KeyboardDevice.Modifiers != ModifierKeys.Shift)
            {
                if (txt.Text.Contains(".") && e.Key == Key.OemPeriod)
                {
                    e.Handled = true;
                    return;
                }
                e.Handled = false;
            }
            else
            {
                e.Handled = true;
            }
        }

        private void mm_MouseIn(object sender, MouseButtonEventArgs e)
        {
            MaxNumber = 59;
            changeType = 2;
        }

        private void mm_FoucusOut(object sender, RoutedEventArgs e)
        {
            minute_valueCheck();
        }

        private void mm_TextChanged(object sender, TextChangedEventArgs e)
        {
            //屏蔽中文输入和非法字符粘贴输入
            try
            {
                TextBox textBox = sender as TextBox;
                TextChange[] change = new TextChange[e.Changes.Count];
                e.Changes.CopyTo(change, 0);

                int offset = change[0].Offset;
                if (change[0].AddedLength > 0)
                {
                    if (!_numMatch.IsMatch(textBox.Text))
                    {
                        textBox.Text = textBox.Text.Remove(offset, change[0].AddedLength);
                        textBox.Select(offset, 0);
                        return;
                    }
                }

                if (textBox.Text != "")
                {
                    SelectDateTime = new DateTime(SelectDateTime.Year, SelectDateTime.Month, SelectDateTime.Day,
                                                                  SelectDateTime.Hour, Convert.ToInt32(textBox.Text), 0);
                    this.Text = String.Format(FormatString, this.SelectDateTime);
                }
            }
            catch { }
        }

        private bool minute_valueCheck()
        {
            MaxNumber = 59;
            TextBox mmbox = (TextBox)FindElement("part_Minute");
            int mmValue = 0;

            if (mmbox.Text != "")
            {
                string inputTx = mmbox.Text;

                for (int i = 0; i < inputTx.Length; i++)
                {
                    if (!_numMatch.IsMatch(inputTx[i].ToString()))
                    {
                        return false;
                    }
                }

                mmValue = Convert.ToInt32(inputTx);
            }

            if (mmValue > MaxNumber)
            {
                mmValue = MaxNumber;
            }
            else if (mmValue < MinNumber)
            {
                mmValue = MinNumber;
            }

            mmbox.Text = mmValue.ToString();

            SelectDateTime = new DateTime(SelectDateTime.Year, SelectDateTime.Month, SelectDateTime.Day,
                                                              SelectDateTime.Hour, mmValue, 0);
            this.Text = String.Format(FormatString, this.SelectDateTime);
            return true;
        }
        #endregion

        #region 时间-second框操作
        private void ss_keyDown(object sender, KeyEventArgs e)
        {
            MaxNumber = 59;
            changeType = 3;
        }

        private void ss_MouseIn(object sender, MouseButtonEventArgs e)
        {
            MaxNumber = 59;
            changeType = 3;
        }
        #endregion 时间-second框操作


        private void Increase_Click(object sender, RoutedEventArgs e)
        {
            if (changeType == 1)
            {
                TextBox hhbox = (TextBox)FindElement("part_Hour");
                int hhValue = Convert.ToInt32(hhbox.Text);

                if (hhValue < MaxNumber)
                {
                    hhValue++;
                    hhbox.Text = hhValue.ToString();
                }
            }
            else if (changeType == 2)
            {
                TextBox mmbox = (TextBox)FindElement("part_Minute");
                int mmValue = Convert.ToInt32(mmbox.Text);
                if (mmValue < MaxNumber)
                {
                    mmValue++;
                    mmbox.Text = mmValue.ToString();
                }
            }
        }

        private void Decrease_Click(object sender, RoutedEventArgs e)
        {
            if (changeType == 1)
            {
                TextBox hhbox = (TextBox)FindElement("part_Hour");
                int hhValue = Convert.ToInt32(hhbox.Text);

                if (hhValue >MinNumber)
                {
                    hhValue--;
                    hhbox.Text = hhValue.ToString();
                }
            }
            else if (changeType == 2)
            {
                TextBox mmbox = (TextBox)FindElement("part_Minute");
                int mmValue = Convert.ToInt32(mmbox.Text);
                if (mmValue > MinNumber)
                {
                    mmValue--;
                    mmbox.Text = mmValue.ToString();
                }
            }
        }
    }
}
