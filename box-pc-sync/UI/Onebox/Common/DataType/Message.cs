using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox.MsgDefine
{
    struct Messager
    {
        public const Int32 WM_COPYDATA = 0x004A;
        public const Int32 WM_WTSSESSION_CHANGE = 0x02B1;
        public const Int32 WTS_SESSION_LOCK = 0x7;
        public const Int32 WTS_SESSION_UNLOCK = 0x8;
        public const Int32 WM_POWERBROADCAST = 0x218;
        public const Int32 PBT_APMSUSPEND = 0x4;
        public const Int32 PBT_APMRESUMESUSPEND = 0x7;
        public const Int32 PBT_APMRESUMEAUTOMATIC = 0x12;
        public const Int32 NOTIFY_MSG_TRANS_TASK_INSERT = 200;
        public const Int32 NOTIFY_MSG_TRANS_TASK_DELETE = 201;
        public const Int32 NOTIFY_MSG_TRANS_TASK_UPDATE = 202;
        public const Int32 NOTIFY_MSG_MENU_SHARE_LINK = 210;
        public const Int32 NOTIFY_MSG_MENU_SHARE = 211;
        public const Int32 NOTIFY_MSG_MENU_LISTREMOTEDIR = 212;
        public const Int32 NOTIFY_MSG_CHANGE_WORK_MODE = 213;
        public const Int32 NOTIFY_MSG_SPEED = 214;
        public const Int32 NOTIFY_MSG_DIFF_CNT = 215;
        public const Int32 NOTIFY_MSG_CNT_LIMIT = 216;
        public const Int32 NOTIFY_MSG_SCAN = 218;
        public const Int32 NOTIFY_MSG_ERROR_CHANGED = 219;
        public const Int32 NOTIFY_MSG_ROOT_CHANGE = 220;
        public const Int32 NOTIFY_MSG_SHOW_TRANSTASKS = 221;
    }
}
