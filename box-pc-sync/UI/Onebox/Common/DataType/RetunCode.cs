using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox
{
    public  enum RetunCode
    {
        Success = 0,
        ServiceError = -3,
        BindError = -600,
        LockError = -1403,
        DeviceError = -9999,
        CouldntResolvHost = -2006,
        CouldntConnect = -2007,
        DefaultError = -1
    }
}
