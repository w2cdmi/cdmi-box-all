/**
 * 
 */
package com.huawei.sharedrive.isystem.core.annotation;

import java.lang.annotation.Documented;

/**
 * @author d00199602
 *
 */
@Documented
public @interface IPAddress
{
    String message() default "{validator.iPAddress}";
}
