package pw.cdmi.file.engine.core.spring.ext;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@SuppressWarnings("rawtypes")
public class ApplicationEventListener implements ApplicationListener, ApplicationContextAware
{
    private ApplicationContext applicationContext;
    
    private Map<String, EventExecutor> eventExecutors = new HashMap<String, EventExecutor>(1);
    
    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        EventExecutor executor = eventExecutors.get(event.getClass().getName());
        
        if (null != executor)
        {
            executor.execute(event);
        }
    }
    
    /**
     * 发布事件
     * 
     * @param event
     * @param executor
     */
    public void publishEvent(ApplicationEvent event, EventExecutor executor)
    {
        if(null == this.applicationContext)
        {
            return;
        }
        this.applicationContext.publishEvent(event);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    public void setEventExecutors(Map<String, EventExecutor> eventExecutors)
    {
        this.eventExecutors = eventExecutors;
    }
    
}
