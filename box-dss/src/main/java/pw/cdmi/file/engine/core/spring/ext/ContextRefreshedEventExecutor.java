package pw.cdmi.file.engine.core.spring.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

public class ContextRefreshedEventExecutor implements EventExecutor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextRefreshedEventExecutor.class);
    
    @Override
    public void execute(ApplicationEvent event)
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("spring framework init succ. ");
        }
    }
}
