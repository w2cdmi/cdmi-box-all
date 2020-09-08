package pw.cdmi.box.ufm.tools;





public class Doctype
{
     /**
      * 
      * 类型与doctypes.json文件一一对应
      *
      * Project Name:ufm
      *
      * File Name:DocType.java
      *
      * @author guoz
      *
      * 修改时间：2016年7月12日 下午5:58:48
      */
     public enum Doctypes {
          // 无效值使用小于0的随即数
          extensions(-32142132),  //只使用 extensions.name(),不使用doctype.getKey()
          doctype(-123412236), //只使用 doctype.name(),不使用doctype.getKey()
          appId(-123443324), //appId：全填Constants.UFM_DEFAULT_APP_ID
          Check(-1221334),
          /* 这里的类型与数据库sysdb中system_config id=doctype.*对应  */
          /*
           * 数据库不能填写说明：这里补充id填入规则：
           *    工程.类名.属性.属性值 ,      value:                          appId：全填Constants.UFM_DEFAULT_APP_ID
           *    ufm.DocType.document.1  jpg,jpeg,gif,psd,png,bmp,tiff   -1
           *    ufm.DocType.picture.2   doc,docx,ppt,pptx,pdf           -1
           *    ufm.DocType.audio.3     mp3,wma,wav,ra,cd,md            -1
           *    ufm.DocType.video.4     mp4,rmvb,rm,avi                 -1
           */
          document(1), //文档
          picture(2), //图片
          audio(3),//音频
          video(4),//视频 
          other(5)//其它
          
          ;
          private int value;
          
          private Doctypes(int value) {
               this.value = value;
          }
          
          public int getValue() {
             return value;
          }
     }
     
}
