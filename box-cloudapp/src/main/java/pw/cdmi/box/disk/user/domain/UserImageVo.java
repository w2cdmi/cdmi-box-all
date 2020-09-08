package pw.cdmi.box.disk.user.domain;


public class UserImageVo extends UserImage
{
    /**
     * 
     */
    private static final long serialVersionUID = -2998544504532632934L;
    
    private boolean exitUserImage;
    
    
    public boolean isExitUserImage()
    {
        return exitUserImage;
    }
    
    public void setExitUserImage(boolean exitUserImage)
    {
        this.exitUserImage = exitUserImage;
    }

    
    @Override
    public String toString()
    {
        return super.toString();
    }
    
}
