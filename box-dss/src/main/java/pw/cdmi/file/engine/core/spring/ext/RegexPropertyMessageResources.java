package pw.cdmi.file.engine.core.spring.ext;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 国际化资源加载类 覆盖了父类的setBasenames方法，提供支持通配符配置
 */

public class RegexPropertyMessageResources extends ResourceBundleMessageSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexPropertyMessageResources.class);
    
    /**
     * 资源文件扩展名
     */
    public final static String PROPERTY_POSTFIX = ".properties";
    
    private PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    
    private static final String[] BASE = new String[0];
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.support.ResourceBundleMessageSource#setBasenames(java .lang.String[])
     */
    @Override
    public void setBasenames(String... baseNames)
    {
        if (null == baseNames)
        {
            return;
        }
        try
        {
            Resource[] resources = null;
            List<String> baseNamesList = null;
            String fileName = null;
            for (String baseName : baseNames)
            {
                // 根据通配符获取资源对象集合
                resources = patternResolver.getResources(baseName); // 通过通配符取得到所有对应的source
                
                // 申明文件名集合
                baseNamesList = new ArrayList<String>(resources.length);
                for (Resource resource : resources)
                {
                    fileName = resource.getFilename();
                    
                    // 修改Coverity检查结果，显式地判空
                    // if(StringUtils.isBlank(fileName))
                    if (StringUtils.isEmpty(fileName))
                    {
                        continue;
                    }
                    baseNamesList.add(fileName.substring(0, fileName.indexOf(PROPERTY_POSTFIX)));
                }
                super.setBasenames(baseNamesList.toArray(BASE));
            }
        }
        catch (Exception e)
        {
            LOGGER.error("set beannames failed.", e);
            throw new RuntimeException(e);
        }
    }
}
