using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;

namespace Onebox
{
    public partial class MainWindow : Window
    {
        private delegate void DelegateLoginTask(int iRet);
        private delegate void DelegateClearTansTab();
        private delegate void DelegateCloseWindow(Window childWindow);
        private delegate void DelegateMonitorTask();
        private delegate void DelegateNotEnableExportButton(Button exportButton);
        private delegate void DelegateEnableExportButton(Button exportButton);
        private delegate void DelegateCollectLogTask();
        private delegate void DelegateUpgradeTask();
    }
}
