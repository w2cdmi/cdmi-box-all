package pw.cdmi.file.engine.core.spring.ext;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring IOC容器工具类 用于提供加载对象、国际化等接口
 */
public final class BeanHolder implements ApplicationContextAware
{
    
    private static ApplicationContext context = null;
    
    private BeanHolder()
    {
        
    }
    
    @SuppressWarnings("unchecked")
    public static <E> E getBean(String name)
    {
        if (null == getContext())
        {
            return null;
        }
        
        return (E) context.getBean(name);
    }
    
    /**
     * 返回该类型的所有实现类
     * 
     * @param <E> 泛型
     * @param clazz 类型
     * @return 返回IOC容器中所有该类型实例对象
     */
    @SuppressWarnings("unchecked")
    public static <E> Map<String, E> getBeans(Class<? extends E> clazz)
    {
        if (null == getContext())
        {
            return null;
        }
        
        return (Map<String, E>) getContext().getBeansOfType(clazz);
    }
    
    public static String getMessage(String msg)
    {
        if (null == getContext())
        {
            return msg;
        }
        
        return getContext().getMessage(msg, null, msg, Locale.getDefault());
    }
    
    public static String getMessage(String msg, Locale locale)
    {
        if (null == getContext())
        {
            return msg;
        }
        
        return getContext().getMessage(msg, null, msg, locale);
    }
    
    public static String getMessage(String msg, Object[] args)
    {
        if (null == getContext())
        {
            return msg;
        }
        
        return getContext().getMessage(msg, args, msg, Locale.getDefault());
    }
    
    public static String getMessage(String msg, Object[] args, Locale locale)
    {
        if (null == getContext())
        {
            return msg;
        }
        
        return getContext().getMessage(msg, args, msg, locale);
    }
    
    public static String getMessage(String msg, String defalut)
    {
        if (null == getContext())
        {
            return defalut;
        }
        
        return getContext().getMessage(msg, null, defalut, Locale.getDefault());
    }
    
    private static ApplicationContext getContext()
    {
        return context;
    }
    
    private static void setApplicationContextStatic(ApplicationContext arg0)
    {
        context = arg0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.
     * springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        setApplicationContextStatic(arg0);
    }
}
