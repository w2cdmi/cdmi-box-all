using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Onebox
{
    public enum NoticeType
    {
        /// <summary>
        /// The balloon message is displayed without an icon.
        /// </summary>
        Right,
        /// <summary>
        /// An information is displayed.
        /// </summary>
        Info,
        /// <summary>
        /// A warning is displayed.
        /// </summary>
        Ask,
        /// <summary>
        /// An error is displayed.
        /// </summary>
        Error
    }
}
