package pw.cdmi.box.uam.user.manager;

import pw.cdmi.box.uam.user.domain.Admin;

public interface AdminManager
{
    
    /**
     * 
     * @param user
     */
    void create(Admin admin);
    
    
    /**
     * 
     * @param id
     */
    void delete(Long id);
    
    
    /**
     * 
     * @param loginName
     */
    Admin queryByLoginName(String loginName);
    
}
