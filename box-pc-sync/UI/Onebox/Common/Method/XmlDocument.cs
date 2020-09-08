using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Windows;
using Onebox.XmlNodeInfo;
using Onebox.XmlNodeAttribute;

namespace Onebox.XmlDocumentOperation
{
    class XmlDocumentOper
    {
        private string xmlDocumentPath = "";

        public XmlDocumentOper(string strxmlPath)
        {
            xmlDocumentPath = strxmlPath;
        }

        public  void ReadParseErrorXml(string strRootNode)
        {
            try
            {
                XmlDocument xmlDoc = new XmlDocument();
                XmlReaderSettings settings = new XmlReaderSettings();
                settings.IgnoreComments = true;
                XmlReader reader = XmlReader.Create(xmlDocumentPath, settings);
                xmlDoc.Load(reader);
                XmlNode root = xmlDoc.SelectSingleNode(strRootNode);
                XmlNodeList nodeList = root.ChildNodes;
                foreach (XmlNode xn in nodeList)
                {

                    ErrorNodeAttribute errorNodeAttribte = new ErrorNodeAttribute(xn.Attributes.Item(0).InnerText, xn.Attributes.Item(1).InnerText, xn.Attributes.Item(2).InnerText);
                    ErrorXmlNodeInfo.attributeList.Add(errorNodeAttribte);

                }

            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }
}
