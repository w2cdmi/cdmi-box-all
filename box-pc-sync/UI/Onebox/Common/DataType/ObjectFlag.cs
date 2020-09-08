using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox.ObjectFlag
{
    enum OBJECT_FLAG
    {
        OBJECT_FLAG_ENCRYPT = 0x00000001,
        OBJECT_FLAG_SHARELINK = 0x00000002,
        OBJECT_FLAG_SHARED = 0x00000004,
        OBJECT_FLAG_SYNC = 0x00000008,
        OBJECT_FLAG_DEFAULT = -1
    };
}
