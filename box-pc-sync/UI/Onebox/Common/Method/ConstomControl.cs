using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Onebox.CustomControls
{
    public class ImageButton : Button
    {
        static ImageButton()
        {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(ImageButton), new FrameworkPropertyMetadata(typeof(ImageButton)));
        }

        #region Dependency Properties

        private static void ImageSourceChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e)
        {
            Application.GetResourceStream(new Uri("pack://application:,,," + (string)e.NewValue));
        }

        public static readonly DependencyProperty ImageSizeProperty =
            DependencyProperty.Register("ImageSize", typeof(double), typeof(ImageButton),
            new FrameworkPropertyMetadata(0.0, FrameworkPropertyMetadataOptions.AffectsRender));

        public static readonly DependencyProperty NormalImageProperty =
            DependencyProperty.Register("NormalImage", typeof(string), typeof(ImageButton),
            new FrameworkPropertyMetadata("", FrameworkPropertyMetadataOptions.AffectsRender, ImageSourceChanged));

        public static readonly DependencyProperty OverImageProperty =
            DependencyProperty.Register("OverImage", typeof(string), typeof(ImageButton),
            new FrameworkPropertyMetadata("", FrameworkPropertyMetadataOptions.AffectsRender, ImageSourceChanged));

        public static readonly DependencyProperty PresseImageProperty =
            DependencyProperty.Register("PresseImage", typeof(string), typeof(ImageButton),
            new FrameworkPropertyMetadata("", FrameworkPropertyMetadataOptions.AffectsRender, ImageSourceChanged));

        public static readonly DependencyProperty DisableImageProperty =
            DependencyProperty.Register("DisableImage", typeof(string), typeof(ImageButton),
            new FrameworkPropertyMetadata("", FrameworkPropertyMetadataOptions.AffectsRender, ImageSourceChanged));

        public static readonly DependencyProperty BorderVisibilityProperty =
            DependencyProperty.Register("BorderVisibility", typeof(Visibility), typeof(ImageButton),
            new FrameworkPropertyMetadata(Visibility.Hidden, FrameworkPropertyMetadataOptions.AffectsRender, ImageSourceChanged));

        public double ImageSize
        {
            get
            {
                return (double)GetValue(ImageSizeProperty);
            }
            set
            {
                SetValue(ImageSizeProperty, value);
            }
        }

        public string NormalImage
        {
            get
            {
                return (string)GetValue(NormalImageProperty);
            }
            set
            {
                SetValue(NormalImageProperty, value);
            }
        }

        public string OverImage
        {
            get
            {
                return (string)GetValue(OverImageProperty);
            }
            set
            {
                SetValue(OverImageProperty, value);
            }
        }

        public string PresseImage
        {
            get
            {
                return (string)GetValue(PresseImageProperty);
            }
            set
            {
                SetValue(PresseImageProperty, value);
            }
        }

        public string DisableImage
        {
            get
            {
                return (string)GetValue(DisableImageProperty);
            }
            set
            {
                SetValue(DisableImageProperty, value);
            }
        }

        public Visibility BorderVisibility
        {
            get
            {
                return (Visibility)GetValue(BorderVisibilityProperty);
            }
            set
            {
                SetValue(BorderVisibilityProperty, value);
            }
        }

        #endregion
    }
}
