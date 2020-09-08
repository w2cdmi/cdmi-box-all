package pw.cdmi.box.disk.user.web;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePswRequest implements Serializable
{
    
    private static final long serialVersionUID = -495807343439213619L;
    
    @NotNull
    @Size(max = 30, min = 6)
    private String newPsw;
    
    @NotNull
    @Size(max = 30, min = 4)
    private String oldPsw;
    
    @NotNull
    @Size(max = 30, min = 6)
    private String reNewPsw;
    
    public String getNewPsw()
    {
        return newPsw;
    }
    
    public String getOldPsw()
    {
        return oldPsw;
    }
    
    public String getReNewPsw()
    {
        return reNewPsw;
    }
    
    public void setNewPsw(String newPsw)
    {
        this.newPsw = newPsw;
    }
    
    public void setOldPsw(String oldPsw)
    {
        this.oldPsw = oldPsw;
    }
    
    public void setReNewPsw(String reNewPsw)
    {
        this.reNewPsw = reNewPsw;
    }
    
}
