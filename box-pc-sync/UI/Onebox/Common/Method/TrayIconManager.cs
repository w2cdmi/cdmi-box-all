using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media.Imaging;

using Onebox.NSTrayIconStatus;
namespace Onebox.TrayIcon
{
   public class TrayIconManager
    {
        private Dictionary<string, List<string>> TrayIcon = new Dictionary<string, List<string>>();
        private List<string> OffLinePrerequisite = new List<string>();
        private List<string> OneLineLinePrerequisite = new List<string>();
        private List<string> ErrorPrerequisite = new List<string>();
        private List<string> FailedPrerequisite = new List<string>();
        private List<string> SyncingPrerequisite = new List<string>();
        private List<string> SuspendPrerequisite = new List<string>();
        public  string CrruentIcon = TrayIconStatus.OFFLINE_ICO;

        public TrayIconManager()
        {
            PrepareTrayIconShow();
        }

        public BitmapImage GetTrayIcon(string Icon)
        {
            BitmapImage bitimg = new BitmapImage();
            if (0 == TrayIcon[Icon].Count || TrayIcon[Icon].Contains(CrruentIcon))
            {
                CrruentIcon = Icon;
            }

            bitimg.BeginInit();
            bitimg.UriSource = new Uri(CrruentIcon, UriKind.Absolute);
            bitimg.EndInit();
            return bitimg;
        }

        private void PrepareTrayIconShow()
        {
            PrepareTrayIconShowPrerequisite();
            TrayIcon.Add(TrayIconStatus.OFFLINE_ICO, OffLinePrerequisite);
            TrayIcon.Add(TrayIconStatus.ONLINE_ICO, OneLineLinePrerequisite);
            TrayIcon.Add(TrayIconStatus.ERROR_ICO, ErrorPrerequisite);
            TrayIcon.Add(TrayIconStatus.FAILED_ICO, FailedPrerequisite);
            TrayIcon.Add(TrayIconStatus.SUSPEND_ICO, SuspendPrerequisite);
            TrayIcon.Add(TrayIconStatus.SYNCING_ICO, SyncingPrerequisite);
        }

        private void PrepareTrayIconShowPrerequisite()
        {
            FailedPrerequisite.Add(TrayIconStatus.ONLINE_ICO);
            FailedPrerequisite.Add(TrayIconStatus.SYNCING_ICO);

            SyncingPrerequisite.Add(TrayIconStatus.ONLINE_ICO);
            SyncingPrerequisite.Add(TrayIconStatus.FAILED_ICO);
            SyncingPrerequisite.Add(TrayIconStatus.SUSPEND_ICO);

            SuspendPrerequisite.Add(TrayIconStatus.ONLINE_ICO);
            SuspendPrerequisite.Add(TrayIconStatus.FAILED_ICO);
            SuspendPrerequisite.Add(TrayIconStatus.SYNCING_ICO);
        }
    }
}
