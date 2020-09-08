package pw.cdmi.file.engine.core.ibatis;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Inherited
public @interface Namingspace
{
    /**
     * 命名空间
     */
    String value();
}
