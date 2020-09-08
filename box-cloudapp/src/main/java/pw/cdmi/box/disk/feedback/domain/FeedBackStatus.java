package pw.cdmi.box.disk.feedback.domain;



/**
 * 问题状态类
 *
 */
public class FeedBackStatus
{

	/**
	 * 问题状态值
	 */
     private String value;
     
     /**
      * 问题状态名
      */
     private String name;

     
     
     public String getValue()
     {
          return value;
     }

     
     
     public void setValue(String value)
     {
          this.value = value;
     }

     
     
     public String getName()
     {
          return name;
     }

     
     
     public void setName(String name)
     {
          this.name = name;
     }
     
     
}
