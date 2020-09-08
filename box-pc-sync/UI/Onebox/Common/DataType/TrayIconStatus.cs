using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox.NSTrayIconStatus
{
    struct TrayIconStatus
    {
        public const string OFFLINE_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_inactive.ico";
        public const string ONLINE_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_normal.ico";
        public const string SYNCING_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_syncing.ico";
        public const string ERROR_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_error.ico";
        public const string SUSPEND_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_suspend.ico";
        public const string FAILED_ICO = "pack://application:,,,/Onebox;component/ImageResource/tsk_failed.ico";
    }
}
