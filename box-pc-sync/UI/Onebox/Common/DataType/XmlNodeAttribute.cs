using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox.XmlNodeAttribute
{
    public class  ErrorNodeAttribute
   {
       public string cd = "";
       public string des = "";
       public string advice = "";

        public ErrorNodeAttribute(string strcd,string strdes,string  stradvice)
       {
           cd = strcd;
           des = strdes;
           advice = stradvice;
       }
   }
}
