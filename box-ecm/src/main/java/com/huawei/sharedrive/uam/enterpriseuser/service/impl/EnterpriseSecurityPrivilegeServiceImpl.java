package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseSecurityPrivilegeDao;
import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseUserDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseSecurityPrivilegeService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.teamspace.domain.ChangeOwnerRequest;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamMember;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamMemberCreateRequest;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnterpriseSecurityPrivilegeServiceImpl implements EnterpriseSecurityPrivilegeService {
    @Autowired
    EnterpriseSecurityPrivilegeIdGenerator enterpriseSecurityPrivilegeIdGenerator;

    @Autowired
    private EnterpriseSecurityPrivilegeDao privilegeDao;

    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private DepartmentAccountService  departmentAccountService;
    
 	@Autowired
    private UserAccountService userAccountService;
 	
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private DepartmentAccountService deptAccountService;

    @Override
    public long create(EnterpriseSecurityPrivilege privilege) {
        return privilegeDao.create(privilege);
    }

    @Override
    public void update(EnterpriseSecurityPrivilege privilege) {

    }

    @Override
    public void deleteBy(long enterpriseId) {
    	
    }

    @Override
    public void deleteBy(long enterpriseId, long departmentId) {

    }

    @Override
    public void deleteBy(long enterpriseId, long departmentId, byte role) {

    }

    @Override
    public  List<EnterpriseUser> listInfoSecurityManager(long enterpriseId) {
        List<EnterpriseUser> userList = privilegeDao.getUserByEnterpriseAndDepartmentAndRole(enterpriseId, 0L, EnterpriseSecurityPrivilege.ROLE_SECURITY_MANAGER);
        return userList;
    }

    public void setInfoSecurityManager(long enterpriseId, long enterpriseUserId,long deptId) {
        List<EnterpriseSecurityPrivilege> list = privilegeDao.getIdByEnterpriseAndDepartmentAndRole(enterpriseId,deptId, EnterpriseSecurityPrivilege.ROLE_SECURITY_MANAGER);
        if(list != null && !list.isEmpty()) {
        	boolean userExist=false;
        	for(int i=0;i<list.size();i++){
        		if(list.get(i).getEnterpriseId()==enterpriseId&&list.get(i).getEnterpriseUserId()==enterpriseUserId
        				&&list.get(i).getRole()==EnterpriseSecurityPrivilege.ROLE_SECURITY_MANAGER){
        			userExist=true;
        		}
        	}
        	if(!userExist){
        		 EnterpriseSecurityPrivilege privilege = new EnterpriseSecurityPrivilege();
                 privilege.setId(enterpriseSecurityPrivilegeIdGenerator.getNextId());
                 privilege.setEnterpriseId(enterpriseId);
                 privilege.setDepartmentId(deptId); //0表示公司
                 privilege.setRole(EnterpriseSecurityPrivilege.ROLE_SECURITY_MANAGER);
                 privilege.setEnterpriseUserId(enterpriseUserId);
                 privilegeDao.create(privilege);
        	}
        }else{
        	 EnterpriseSecurityPrivilege privilege = new EnterpriseSecurityPrivilege();
             privilege.setId(enterpriseSecurityPrivilegeIdGenerator.getNextId());
             privilege.setEnterpriseId(enterpriseId);
             privilege.setDepartmentId(deptId); //0表示公司
             privilege.setRole(EnterpriseSecurityPrivilege.ROLE_SECURITY_MANAGER);
             privilege.setEnterpriseUserId(enterpriseUserId);
             privilegeDao.create(privilege);
        }
    }

    @Override
    public int countWithFilter(long enterpriseId, Long deptId, String authServerId, String filter,byte roleType) {
        return privilegeDao.countWithFilter(enterpriseId, deptId, authServerId, filter, roleType);
    }

    @Override
    public List<EnterpriseUser> getWithFilter(long enterpriseId, Long deptId, String authServerId, String filter, Order order, Limit limit,byte roleType) {
        return privilegeDao.getWithFilter(enterpriseId, deptId, authServerId, filter,roleType, order, limit);
    }

    @Override
    public void addArchiveOwner(long enterpriseId, long deptId, long enterpriseUserId,UserToken userToken) {
        EnterpriseUser user = enterpriseUserDao.get(enterpriseUserId, enterpriseId);
        if(user != null) {
			DepartmentAccount departmentAccount =departmentAccountService.getByDeptIdAndAccountId(deptId, userToken.getAccountId());
            List<EnterpriseSecurityPrivilege> privilegeList=  privilegeDao.getIdByEnterpriseAndDepartmentAndRole(enterpriseId, deptId, EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER);
            for(int i=0;i<privilegeList.size();i++){
                //首先删除原有的空间管理员
            	UserAccount userAccount = userAccountService.get(privilegeList.get(i).getEnterpriseUserId(), userToken.getAccountId());
            	try {
            		 teamspaceDeleteManager(userAccount,privilegeList.get(i),departmentAccount,userToken.getAppId());
				} catch (Exception e) {
					// TODO: handle exception
				}
            }
            
            privilegeDao.deleteByDeptAndRole(enterpriseId, deptId, EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER);
          //添加新的管理员
            teamspaceAddManager(enterpriseUserId,enterpriseId,deptId,userToken.getAppId());
            EnterpriseSecurityPrivilege privilege = new EnterpriseSecurityPrivilege();
            privilege.setId(enterpriseSecurityPrivilegeIdGenerator.getNextId());
            privilege.setEnterpriseId(enterpriseId);
            privilege.setDepartmentId(deptId);
            privilege.setRole(EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER);
            privilege.setEnterpriseUserId(enterpriseUserId);
            privilegeDao.create(privilege);
      
            
          
        }
    }

	@Override
	public void deletePrivilegeOwner(long enterpriseId,long enterpriseUserId,byte roleType) {
		// TODO Auto-generated method stub
		privilegeDao.delete(enterpriseId,enterpriseUserId,roleType);
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void teamspaceAddManager(long enterpriseUserId,long enterpriseId,long deptId,UserToken userToken){
			UserAccount userAccount = userAccountService.get(enterpriseUserId, userToken.getAccountId());
			DepartmentAccount dbDeptAccount = deptAccountService.getByDeptIdAndAccountId(deptId, userToken.getAccountId());
			if(dbDeptAccount!=null){
				    RestTeamMember restTeamMember=new RestTeamMember();
				    restTeamMember.setId(userAccount.getCloudUserId()+"");
			        restTeamMember.setLoginName(userAccount.getName());
			        restTeamMember.setName(userAccount.getName());
			        restTeamMember.setType(RestTeamMember.TYPE_USER);
			        RestTeamMemberCreateRequest memberRequest=new RestTeamMemberCreateRequest();
			        List<RestTeamMember> memberList = new ArrayList<>();
			        memberList.add(restTeamMember);
			        memberRequest.setMemberList(memberList);
			        memberRequest.setTeamRole(RestTeamMemberCreateRequest.ROLE_MANAGER);
			        memberRequest.setRole("auther");
			        teamSpaceService.addTeamSpaceMember(enterpriseId, userToken.getAppId(),dbDeptAccount.getCloudUserId(), memberRequest);	
			}
		}
	
	private void teamspaceAddManager(long enterpriseUserId,long enterpriseId,long deptId,String appId){
		long accountId = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId).getAccountId();
		UserAccount userAccount = userAccountService.get(enterpriseUserId, accountId);
		DepartmentAccount dbDeptAccount = deptAccountService.getByDeptIdAndAccountId(deptId, accountId);
		if(dbDeptAccount!=null){
			    RestTeamMember restTeamMember=new RestTeamMember();
			    restTeamMember.setId(userAccount.getCloudUserId()+"");
		        restTeamMember.setLoginName(userAccount.getName());
		        restTeamMember.setName(userAccount.getName());
		        restTeamMember.setType(RestTeamMember.TYPE_USER);
		        RestTeamMemberCreateRequest memberRequest=new RestTeamMemberCreateRequest();
		        List<RestTeamMember> memberList = new ArrayList<>();
		        memberList.add(restTeamMember);
		        memberRequest.setMemberList(memberList);
		        memberRequest.setTeamRole(RestTeamMemberCreateRequest.ROLE_MANAGER);
		        memberRequest.setRole("auther");
		        teamSpaceService.addTeamSpaceMember(enterpriseId, appId,dbDeptAccount.getCloudUserId(), memberRequest);	
		}
	}
	  
	  
	  
	   
	   private void teamspaceDeleteManager(UserAccount userAccount,EnterpriseSecurityPrivilege enterpriseSecurityPrivilege,DepartmentAccount departmentAccount,String appId){
			teamSpaceService.deleteTeamSpaceMemberByCloudUserId(departmentAccount.getEnterpriseId(), appId, departmentAccount.getCloudUserId(), userAccount.getCloudUserId());
		}

	@Override
	public EnterpriseSecurityPrivilege get(EnterpriseSecurityPrivilege securityPrivilege) {
		// TODO Auto-generated method stub
		return privilegeDao.get(securityPrivilege);
	}

	@Override
	public List<EnterpriseSecurityPrivilege> listSecurityPrivilege(EnterpriseSecurityPrivilege filter,Limit limit) {
		// TODO Auto-generated method stub
		return privilegeDao.listSecurityPrivilege(filter,limit);
	}
	
	
    @Override
    public EnterpriseSecurityPrivilege getDeptDirector(long enterpriseId, long deptId) {
    	EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
    	filter.setDepartmentId(deptId);
    	filter.setEnterpriseId(enterpriseId);
    	filter.setRole(EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR);
    	EnterpriseSecurityPrivilege deptDirector = privilegeDao.get(filter);
        return deptDirector;
    }

    @Override
    public void addDeptDirector (long enterpriseId, long deptId, long enterpriseUserId) {
    	
    	//删除原有
    	privilegeDao.deleteBy(enterpriseId, deptId, EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR);
        //将部门中已有的主管修改为部门成员
    	EnterpriseSecurityPrivilege securityPrivilege = new EnterpriseSecurityPrivilege();
    	securityPrivilege.setId(enterpriseSecurityPrivilegeIdGenerator.getNextId());
    	securityPrivilege.setDepartmentId(deptId);
    	securityPrivilege.setEnterpriseId(enterpriseId);
    	securityPrivilege.setEnterpriseUserId(enterpriseUserId);
    	securityPrivilege.setRole(EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR);
    	privilegeDao.create(securityPrivilege);
    	
    }

	@Override
	public void deletePrivilege(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege) {
		// TODO Auto-generated method stub
		privilegeDao.deletePrivilege(enterpriseSecurityPrivilege);
	}

	@Override
	public int listSecurityPrivilegeTotal(EnterpriseSecurityPrivilege filter, Limit limitObj) {
		// TODO Auto-generated method stub
		return privilegeDao.listSecurityPrivilegeTotal(filter,limitObj);
	}


}
