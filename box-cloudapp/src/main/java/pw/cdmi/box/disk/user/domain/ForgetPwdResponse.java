package pw.cdmi.box.disk.user.domain;

public class ForgetPwdResponse
{
    private int resultCode;
    
    private String resultMsg;
    
    public int getResultCode()
    {
        return resultCode;
    }
    
    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }
    
    public String getResultMsg()
    {
        return resultMsg;
    }
    
    public void setResultMsg(String resultMsg)
    {
        this.resultMsg = resultMsg;
    }
    
}
