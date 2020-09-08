using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;
using System.Collections.ObjectModel;
using log4net;
using Microsoft.Win32;
using System.IO;

namespace Onebox
{
    class Startup
    {
        [STAThread]
        public static void Main(string[] args)
        {
            try
            {
                SingleInstanceApplicationWrapper wrapper = new SingleInstanceApplicationWrapper();
                wrapper.Run(args);
            }
            catch { }
        }
    }

    public class SingleInstanceApplicationWrapper : Microsoft.VisualBasic.ApplicationServices.WindowsFormsApplicationBase
    {
        private App app;       
        public SingleInstanceApplicationWrapper()
        {
            this.IsSingleInstance = true;
        }
    
        // 第一次打开调这个方法        
        protected override bool OnStartup(Microsoft.VisualBasic.ApplicationServices.StartupEventArgs e)
        {
            try
            {
                 ILog Log = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
                 Log.Info("<<<======================Startup====================>>>");
                if (e.CommandLine.Count == 1)
                {
                    if (e.CommandLine.ElementAt(0) == "stop")
                    {
                        return false;
                    }
                    else if (e.CommandLine.ElementAt(0) == "start" || e.CommandLine.ElementAt(0) == "autorun" || e.CommandLine.ElementAt(0) == "schtask")
                    {
                        app = new App();
                        app.InitializeComponent();
                        app.Run();
                        return false;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    app = new App();
                    app.InitializeComponent();
                    app.Run();
                    return false;
                }
            }
            catch { return false; }
        }
        
        // 再次打开调这个方法       
        protected override void OnStartupNextInstance(Microsoft.VisualBasic.ApplicationServices.StartupNextInstanceEventArgs e)
        {

            base.OnStartupNextInstance(e);
            ILog Log = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
            Log.Info("<<<====================== Second Startup====================>>>");

            if (e.CommandLine.Count == 1)
            {
                if (e.CommandLine.ElementAt(0) == "stop")
                {
                    MainWindow mainWindow = (MainWindow)App.Current.MainWindow;
                    mainWindow.Exit();
                }
                else
                {
                    return;
                }
            }
            else
            {
                MainWindow mainWindow = (MainWindow)App.Current.MainWindow;
                mainWindow.EnterSyncDir(true);
            }
        }
    }
}
