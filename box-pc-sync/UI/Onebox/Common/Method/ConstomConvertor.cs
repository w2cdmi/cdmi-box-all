using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Globalization;
using  System.Windows.Controls;
using System.Windows.Data;
using System.Windows;

namespace Onebox.CustomConvertor
{
    public class ProcessToWidthConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value || null == parameter)
            {
                return 0;
            }
            double process = System.Convert.ToDouble(value);
            double width = System.Convert.ToDouble(parameter);
            return (process * width );
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ProcessToStringConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value || null == parameter)
            {
                return "0%";
            }

            double process = System.Convert.ToDouble(value);
            double width = System.Convert.ToDouble(parameter);
            double _ret = (process / width) * 100;
            return System.Convert.ToString(_ret) + "%";
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StringShow30ByteConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return "";
            }
            string strRet = "";
            string tmp = System.Convert.ToString(value);
            tmp = tmp.Replace('/', '\\');
            string strBegin = "";
            string strEnd = "";
            if (35 < Encoding.Default.GetBytes(tmp).Length)
            {
                int firstsprit = tmp.IndexOf('\\');
                int lastsprit = tmp.LastIndexOf('\\');
                string strTemp = tmp.Substring(firstsprit + 1, tmp.Length - firstsprit - 1);
                int firstsprit2 = strTemp.IndexOf('\\');
                strBegin = tmp.Substring(0, firstsprit + firstsprit2 + 2);
                strEnd = tmp.Substring(lastsprit, tmp.Length - lastsprit);
                int totalLength = Encoding.Default.GetBytes(strBegin).Length + Encoding.Default.GetBytes(strEnd).Length;
                if (Encoding.Default.GetBytes(strEnd).Length < 25)
                {
                    if (totalLength < 25)
                    {
                        strRet = strBegin + "......" + strEnd;
                    }
                    else
                    {
                        strBegin = tmp.Substring(0, firstsprit + 1);
                        strRet = strBegin + "......" + strEnd;
                    }
                }
                else
                {
                    if (firstsprit != lastsprit)
                    {
                        if (strBegin.LastIndexOf("\\") == lastsprit)
                        {
                            strBegin = strBegin.TrimEnd('\\');
                        }

                        if (Encoding.Default.GetBytes(strBegin).Length > 10)
                        {
                            strBegin = tmp.Substring(0, firstsprit + 1);
                        }

                        if (strBegin.Last() == '\\')
                        {
                            string strEnd1 = strEnd.Substring(0, 5);
                            string strEnd2 = strEnd.Substring(strEnd.Length - 6, 6);
                            strEnd = strEnd1 + "......" + strEnd2;
                            strRet = strBegin + "....." + strEnd;
                        }
                        else
                        {
                            string strEnd1 = strEnd.Substring(0, 5);
                            string strEnd2 = strEnd.Substring(strEnd.Length - 8, 8);
                            strEnd = strEnd1 + "......" + strEnd2;
                            strRet = strBegin + strEnd;
                        }
                    }
                    else
                    {
                        strBegin = tmp.Substring(0, 5);
                        strEnd = strEnd.Substring(strEnd.Length - 20, 20);
                        strRet = strBegin + "......" + strEnd;
                    }
                }
            }
            else
            {
                strRet = tmp;
            }

            return strRet;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StringShow10ByteConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return "";
            }
            string tmp = System.Convert.ToString(value);
            CultureInfo currentCultureInfo = CultureInfo.CurrentCulture;

            if (currentCultureInfo.Name.Equals("zh-CN"))
            {
                if (8 < tmp.Length)
                {
                    string strBegin = "";
                    strBegin = tmp.Substring(0, 7);
                    tmp = strBegin + "......";
                }
            }
            else
            {
                if (20 < tmp.Length)
                {
                    string strBegin = "";
                    strBegin = tmp.Substring(0, 14);
                    tmp = strBegin + "......";
                }
            }

            return tmp;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StringShow20ByteConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return "";
            }
            string tmp = System.Convert.ToString(value);

            if (tmp.Length >25)
            {
                tmp = tmp.Substring(0, 20);
                tmp =  tmp + "...";
            }

            return tmp;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class TaskStateToImagePathConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }

            AsyncTaskType TransStype = (AsyncTaskType)value;
            if (AsyncTaskType.ATT_Upload == TransStype || AsyncTaskType.ATT_Upload_Manual == TransStype || AsyncTaskType.ATT_Upload_Attachements == TransStype)
            {
                return "/Onebox;component/ImageResource/tran_upload.png";
            }
            else if (AsyncTaskType.ATT_Download == TransStype || AsyncTaskType.ATT_Download_Manual == TransStype)
            {
                return "/Onebox;component/ImageResource/tran_download.png";
            }
            else
            {
                return null;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class FileSizeToShowCoverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return 0;
            }
            //The file size is Byte
            UInt64 size_KB = (UInt64)1024;
            UInt64 size_MB = (UInt64)1024 * size_KB;
            UInt64 size_GB = (UInt64)1024 * size_MB;
         
            string result = "";
            ulong size = ulong.Parse(value.ToString());

            if (size_GB <= size)
            {
                decimal tmp = Math.Round((decimal)size / size_GB, 2);
                result = System.Convert.ToString(tmp) + " GB";
            }
            else if (size_MB <= size)
            {
                decimal tmp = Math.Round((decimal)size / size_MB, 2);
                result = System.Convert.ToString(tmp) + " MB";
            }
            else if (size_KB <= size)
            {
                decimal tmp = Math.Round((decimal)size / size_KB, 2);
                result = System.Convert.ToString(tmp) + " KB";
            }
            else
            {
                result = System.Convert.ToString(size) + " B";
            }

            return (result);
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class BackgroundConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter,
                                                CultureInfo culture)
        {

            UInt16 listColor = System.Convert.ToUInt16(value);
            if (listColor == 0)
            {
                return "#FFFFFFFF";
            }
            else
            {
                return "#FFECECEC";
            }
        }
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class BorderBrushConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter,
                                                CultureInfo culture)
        {
             
            UInt16 listSideColor = System.Convert.ToUInt16(value);
            if(listSideColor == 0)
            {
                return "#FFE2E2E2";    
            }
            else
            {
                return  "#FFFFFFFF";   
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }


    //public class FileTypeIconConverter : IValueConverter
    //{
    //    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    //    {
    //        if (null == value)
    //        {
    //            return null;
    //        }

    //        FileType state = (FileType)value;
    //        if (FileType.File == state)
    //        {
    //            return "/Onebox;component/ImageResource/file.png";
    //        }
    //        else if (FileType.Folder == state)
    //        {
    //            return "/Onebox;component/ImageResource/docment.png";
    //        }
    //        else
    //        {
    //            return null;
    //        }
    //    }

    //    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    //    {
    //        throw new NotSupportedException("Do not implement");
    //    }
    //}

    public class ShowControlConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            bool isNeedCode=(bool)value;
            if (isNeedCode)
            {
                return Visibility.Visible;
            }
            else
            {
                return Visibility.Hidden;
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StyleVisibilityConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            string iStyleVisibility = System.Convert.ToString(value);
            if ("0" == iStyleVisibility)
            {
                return Visibility.Hidden;
            }
            else
            {
                return Visibility.Visible;
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StyleEnableConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            string iStyleVisibility = System.Convert.ToString(value);
            if ("0" == iStyleVisibility)
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StyleForegroundConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            string iStyleVisibility = System.Convert.ToString(value);
            if ("0" == iStyleVisibility)
            {
                return "#FFB8B8B8";
            }
            else
            {
                return "Black";
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class StyleBordBackgroundConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            string iStyleVisibility = System.Convert.ToString(value);
            if ("0" == iStyleVisibility)
            {
                return "White";
            }
            else
            {
                return "#FFF4F4F4";
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class NoticeTypeToImagePathConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }
            NoticeType noticeType = (NoticeType)value;
            if (NoticeType.Right == noticeType)
            {
                return "/Onebox;component/ImageResource/right.png";
            }
            else if (NoticeType.Info == noticeType)
            {
                return "/Onebox;component/ImageResource/info.png";
            }
            else if (NoticeType.Ask == noticeType)
            {
                return "/Onebox;component/ImageResource/ask.png";
            }
            else if (NoticeType.Error == noticeType)
            {
                return "/Onebox;component/ImageResource/err.png";
            }
            else
            {
                return null;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ListFolderTypeConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }

            Int32 FolderFlags = System.Convert.ToInt32(value);
            if ((Int32)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_DEFAULT != FolderFlags)
            {
                if (((Int32)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_SYNC & FolderFlags) == (Int32)Onebox.ObjectFlag.OBJECT_FLAG.OBJECT_FLAG_SYNC)
                {
                    return "/Onebox;component/ImageResource/SyncFloder.png";
                }
                else
                {
                    return "/Onebox;component/ImageResource/NoSyncFolder.png";
                }
            }
            else
            {
                return "";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ListFolderShowImageConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (null == value)
            {
                return null;
            }

            Int32 FolderID = System.Convert.ToInt32(value);
            if (-1 != FolderID)
            {
                return Visibility.Visible;
            }
            else
            {
                return  Visibility.Collapsed;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ConvertNoticeLength : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
           System.Globalization.CultureInfo currentCultureInfo =   System.Globalization.CultureInfo.CurrentCulture;
           if (!currentCultureInfo.Name.Equals("zh-CN"))
           {
               return 500;
           }
           else
           {
               return 470;
           }

        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ConvertMarginForsp_Scan : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            System.Globalization.CultureInfo currentCultureInfo = System.Globalization.CultureInfo.CurrentCulture;
            if (!currentCultureInfo.Name.Equals("zh-CN"))
            {
                return "10,0,0,0";
            }
            else
            {
                return "100,0,0,0";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }

    public class ConvertMaxWidthForTB_openFileDownloadHelp : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            System.Globalization.CultureInfo currentCultureInfo = System.Globalization.CultureInfo.CurrentCulture;
            if (!currentCultureInfo.Name.Equals("zh-CN"))
            {
                return 470;
            }
            else
            {
                return 500;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotSupportedException("Do not implement");
        }
    }
}
