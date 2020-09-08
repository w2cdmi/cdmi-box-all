using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;
using System.Collections.ObjectModel;
using Onebox.XmlNodeAttribute;

namespace Onebox.XmlNodeInfo
{
    public class ErrorXmlNodeInfo
    {
        public const string RootNode = "ErrorInfos";
        public const string ChildNode = "ErrorMsg ";
        public const string DefaultCode = "-1";
        public const string cd = "cd";
        public const string des = "des";
        public const string advice = "advice";
        public const string ChineseFileName = "\\Language\\Error_Chinese.xml";
        public const string EnglishFileName = "\\Language\\Error_English.xml";
        public string ErrorMsgFilePath = "";
        public static ObservableCollection<ErrorNodeAttribute> attributeList = new ObservableCollection<ErrorNodeAttribute>();
        Dictionary<string, string> aaa = new Dictionary<string, string>();
       

        public ErrorXmlNodeInfo()
        {
            CultureInfo currentCultureInfo = CultureInfo.CurrentCulture;

            try
            {
                if (currentCultureInfo.Name.Equals("zh-CN"))
                {
                    ErrorMsgFilePath = System.Environment.CurrentDirectory + ChineseFileName;
                }
                else
                {
                    ErrorMsgFilePath = System.Environment.CurrentDirectory + EnglishFileName;
                }

            }
            catch (Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }
}
