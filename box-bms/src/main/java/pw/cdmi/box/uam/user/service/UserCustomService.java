package pw.cdmi.box.uam.user.service;

import pw.cdmi.box.uam.user.domain.UserCustom;

public interface UserCustomService
{
    
    /**
     * 
     * @param id
     * @return
     */
    UserCustom getUserCustom(long id);
    
    /**
     * 
     * @param userCustom
     */
    void saveUserCustom(UserCustom userCustom);
}
