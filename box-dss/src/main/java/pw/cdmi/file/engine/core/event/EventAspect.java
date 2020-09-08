package pw.cdmi.file.engine.core.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pw.cdmi.file.engine.core.event.annotation.Event;
import pw.cdmi.file.engine.core.event.annotation.EventSource;

@Component
@Aspect
public class EventAspect
{
    /** 存储已解析过的事件 方法签名-->事件 */
    private static final Map<String, List<Definition<DefaultEvent>>> DEFAULT_EVENT_DEFINITION = new HashMap<String, List<Definition<DefaultEvent>>>(1);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventAspect.class);
    
    /**
     * 存放所有已经解析出来的方法 方法签名 --> Method对象
     */
    private static final Map<String, Method> METHOD = new HashMap<String, Method>(1);
    
    /**
     * 存放已经解析出来的放在方法参数上的事件标签，即针对EventSource标签进行解析<br>
     * 存放个格式为: 方法签名-->参数坐标-->事件的构造函数
     */
    private static final Map<String, Map<Integer, List<Definition<AbstractEvent>>>> PARAMETER_EVENT_DEFINITION = new HashMap<String, Map<Integer, List<Definition<AbstractEvent>>>>(1);
    
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    @Around(value = "@annotation(event)", argNames = "event")
    public Object publishEvent(ProceedingJoinPoint pjp, Event event)
    {
        boolean hasNotError = true;
        try
        {
            return pjp.proceed();
        }
        catch (Throwable e)
        {
            LOGGER.error("do process failed.", e);
            hasNotError = false;
            throw new RuntimeException("Do Failed.", e);
        }
        finally
        {
            List<AbstractEvent> eventObjects = parseEvent(pjp, event, hasNotError);
            
            if (null != eventObjects && !eventObjects.isEmpty())
            {
                // 发送事件
                EventPublisher.publishEvent(eventObjects);
            }
        }
    }
    
    /**
     * @param definition
     * @param i
     * @param temp
     * @throws NoSuchMethodException
     */
    private void addDefinition(Map<Integer, List<Definition<AbstractEvent>>> definition, int i, Annotation[] temp) throws NoSuchMethodException
    {
        EventSource eventSource = null;
        Class<? extends AbstractEvent>[] eventTypes = null;
        List<Definition<AbstractEvent>> def = null;
        Definition<AbstractEvent> newDefinition = null;
        for (Annotation a : temp)
        {
            if (!(a instanceof EventSource))
            {
                continue;
            }
            eventSource = (EventSource) a;
            
            eventTypes = eventSource.event();
            
            // 如果标签中定义的事件为空,则不解析该标签
            if (null == eventTypes || 0 == eventTypes.length)
            {
                continue;
            }
            
            def = new ArrayList<Definition<AbstractEvent>>(eventTypes.length);
            for (Class<? extends AbstractEvent> type : eventTypes)
            {
                newDefinition = new Definition<AbstractEvent>(type.getConstructor(Object.class, boolean.class), eventSource.isAsync());
                def.add(newDefinition);
            }
            definition.put(i, def);
        }
    }
    
    /**
     * @param event
     * @param methodSignature
     * @param types
     * @param definition
     */
    private void addEvent(Event event, String methodSignature, Class<? extends DefaultEvent>[] types, List<Definition<DefaultEvent>> definition)
    {
        for (Class<? extends DefaultEvent> type : types)
        {
            addDefinitionWithOutException(event, methodSignature, definition, type);
        }
    }
    
    private void addDefinitionWithOutException(Event event, String methodSignature, List<Definition<DefaultEvent>> definition, Class<? extends DefaultEvent> type)
    {
        try
        {
            // 生成一个新的事件定义
            definition.add(new Definition<DefaultEvent>(type.getConstructor(Method.class, Object[].class, boolean.class), event.isAsync()));
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Create Default Event For [" + methodSignature + " ] Failed.", e);
        }
        catch (Exception e)
        {
            LOGGER.error("Create Default Event For [" + methodSignature + " ] Failed.", e);
        }
    }
    
    /**
     * 根据方法定义，生成事件列表
     * 
     * @param definition
     * @param pjp
     * @return
     * @throws Exception
     */
    private List<DefaultEvent> createDefaultEvents(List<Definition<DefaultEvent>> definitions, ProceedingJoinPoint pjp)
    {
        List<DefaultEvent> events = null;
        
        if (null == definitions)
        {
            events = new ArrayList<DefaultEvent>(0);
            return events;
        }
        
        events = new ArrayList<DefaultEvent>(definitions.size());
        
        for (Definition<DefaultEvent> definition : definitions)
        {
            addEventWithOutException(pjp, events, definition);
        }
        
        return events;
    }
    
    private void addEventWithOutException(ProceedingJoinPoint pjp, List<DefaultEvent> events, Definition<DefaultEvent> definition)
    {
        try
        {
            events.add(definition.getConstructor().newInstance(parseMethod(pjp), pjp.getArgs(), definition.isAsync()));
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("createDefaultEvents For [ " + ReflectionToStringBuilder.toString(definition) + " ] Failed. ", e);
        }
        catch (Exception e)
        {
            LOGGER.warn("createDefaultEvents For [ " + ReflectionToStringBuilder.toString(definition) + " ] Failed. ", e);
        }
    }
    
    /**
     * 根据方法定义，生成事件列表
     * 
     * @param definitions
     * @param pjp
     * @return
     * @throws Exception
     */
    private List<AbstractEvent> createParameterEvents(Map<Integer, List<Definition<AbstractEvent>>> definitions, ProceedingJoinPoint pjp)
    {
        List<AbstractEvent> events = new ArrayList<AbstractEvent>(0);
        
        if (null == definitions)
        {
            return events;
        }
        
        Object[] args = pjp.getArgs();
        
        for (Map.Entry<Integer, List<Definition<AbstractEvent>>> entry : definitions.entrySet())
        {
            if (null == entry.getValue())
            {
                continue;
            }
            
            for (Definition<AbstractEvent> definition : entry.getValue())
            {
                addEventWithOutException(definitions, events, args, entry, definition);
            }
        }
        
        return events;
    }
    
    private void addEventWithOutException(Map<Integer, List<Definition<AbstractEvent>>> definitions, List<AbstractEvent> events, Object[] args, Map.Entry<Integer, List<Definition<AbstractEvent>>> entry, Definition<AbstractEvent> definition)
    {
        try
        {
            events.add(definition.getConstructor().newInstance(args[entry.getKey()], definition.isAsync()));
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("createParameterEvents For [ " + ReflectionToStringBuilder.toString(definitions) + " ] Failed. ", e);
        }
        catch (Exception e)
        {
            LOGGER.warn("createParameterEvents For [ " + ReflectionToStringBuilder.toString(definitions) + " ] Failed. ", e);
        }
    }
    
    /**
     * 获取方法签名
     * 
     * @param pjp
     * @return
     */
    private String getMethodSignature(ProceedingJoinPoint pjp)
    {
        return pjp.toLongString();
    }
    
    /**
     * 解析出参数类型列表
     * 
     * @param pjp
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Class[] parseArgsTypes(ProceedingJoinPoint pjp)
    {
        Class[] argsType = null;
        Object[] args = pjp.getArgs();
        if (null != args)
        {
            argsType = new Class[args.length];
            for (int i = 0; i < args.length; i++)
            {
                argsType[i] = args[i].getClass();
            }
        }
        
        return argsType;
    }
    
    /**
     * 解析默认事件列表
     * 
     * @param pjp
     * @return
     */
    private List<DefaultEvent> parseDefaultEvents(ProceedingJoinPoint pjp, Event event)
    {
        String methodSignature = getMethodSignature(pjp);
        if (DEFAULT_EVENT_DEFINITION.containsKey(methodSignature))
        {
            return createDefaultEvents(DEFAULT_EVENT_DEFINITION.get(methodSignature), pjp);
        }
        
        Class<? extends DefaultEvent>[] types = event.defalutEvent();
        
        List<Definition<DefaultEvent>> definition = new ArrayList<Definition<DefaultEvent>>(0);
        
        if (null != types)
        {
            addEvent(event, methodSignature, types, definition);
        }
        
        DEFAULT_EVENT_DEFINITION.put(methodSignature, definition);
        
        return createDefaultEvents(definition, pjp);
    }
    
    /**
     * 生成本次请求的事件列表
     * 
     * @param pjp
     * @param event
     * @param isSuccess
     * @return
     */
    private List<AbstractEvent> parseEvent(ProceedingJoinPoint pjp, Event event, boolean isSuccess)
    {
        if (!isSuccess && event.successOnly())
        {
            // 如果方法执行不成功，并且方法标识为只有成功才发送事件，那么就不发送事件
            return null;
        }
        
        List<AbstractEvent> eventObjects = parseParameterEvents(pjp);
        
        List<DefaultEvent> defaultEvents = parseDefaultEvents(pjp, event);
        eventObjects.addAll(defaultEvents);
        
        return eventObjects;
    }
    
    /**
     * 解析出当前方法的定义
     * 
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Method parseMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException, SecurityException
    {
        String methodSignature = getMethodSignature(pjp);
        if (METHOD.containsKey(methodSignature))
        {
            return METHOD.get(methodSignature);
        }
        
        Signature signature = pjp.getSignature();
        
        Class[] argsType = parseArgsTypes(pjp);
        
        Method method = signature.getDeclaringType().getDeclaredMethod(signature.getName(), argsType);
        METHOD.put(methodSignature, method);
        
        return method;
    }
    
    /**
     * 解析标注在参数上的事件列表
     * 
     * @param pjp
     * @return
     */
    private List<AbstractEvent> parseParameterEvents(ProceedingJoinPoint pjp)
    {
        String methodSignature = getMethodSignature(pjp);
        if (PARAMETER_EVENT_DEFINITION.containsKey(methodSignature))
        {
            return createParameterEvents(PARAMETER_EVENT_DEFINITION.get(methodSignature), pjp);
        }
        
        Map<Integer, List<Definition<AbstractEvent>>> definition = parseParameterEventsDefinition(pjp);
        PARAMETER_EVENT_DEFINITION.put(methodSignature, definition);
        
        return createParameterEvents(PARAMETER_EVENT_DEFINITION.get(methodSignature), pjp);
    }
    
    /**
     * 解析出该方法的事件定义
     * 
     * @return
     */
    private Map<Integer, List<Definition<AbstractEvent>>> parseParameterEventsDefinition(ProceedingJoinPoint pjp)
    {
        Map<Integer, List<Definition<AbstractEvent>>> definition = null;
        
        // 如果方法没有参数，就直接返回空
        if (null == pjp.getArgs() || 0 == pjp.getArgs().length)
        {
            definition = new HashMap<Integer, List<Definition<AbstractEvent>>>(0);
            return definition;
        }
        
        int argsLength = pjp.getArgs().length;
        definition = new HashMap<Integer, List<Definition<AbstractEvent>>>(argsLength);
        
        try
        {
            Method method = parseMethod(pjp);
            Annotation[][] annotations = method.getParameterAnnotations();
            Annotation[] temp = null;
            for (int i = 0; i < argsLength; i++)
            {
                temp = annotations[i];
                // 如果该参数没有定义标签,就继续解析下一个标签
                if (null == temp || 0 == temp.length)
                {
                    continue;
                }
                addDefinition(definition, i, temp);
            }
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("ParseParameterEvents Definition For [ " + pjp.toShortString() + " ] Failed.", e);
        }
        catch (Exception e)
        {
            LOGGER.warn("ParseParameterEvents Definition For [ " + pjp.toShortString() + " ] Failed.", e);
        }
        
        LOGGER.info("Parse Parameter Event Definition : " + ReflectionToStringBuilder.toString(definition));
        
        return definition;
    }
}
