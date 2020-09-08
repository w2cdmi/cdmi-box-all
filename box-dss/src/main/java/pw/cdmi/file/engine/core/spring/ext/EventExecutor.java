package pw.cdmi.file.engine.core.spring.ext;

import org.springframework.context.ApplicationEvent;

public interface EventExecutor
{
    void execute(ApplicationEvent event);
}
