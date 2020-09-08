package pw.cdmi.box.uam.enterprise.domain;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public class EnterpriseAccountV1 extends EnterpriseAccount{
	 /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /** 当前空间容量配额 */
    private Long currentMaxSpace;
    
    /** 当前成员（用户）数量配额 */
    private Integer currentMaxMember;
    
    /** 当前文件数量配额 */
    private Integer currentMaxFiles;
    
    /** 当前团队空间数量配额 */
    private Integer currentMaxTeamspace;
    
    /** 当前使用空间 */
    private long spaceUsed;

	public Long getCurrentMaxSpace() {
		return currentMaxSpace;
	}

	public void setCurrentMaxSpace(Long currentMaxSpace) {
		this.currentMaxSpace = currentMaxSpace;
	}

	public Integer getCurrentMaxMember() {
		return currentMaxMember;
	}

	public void setCurrentMaxMember(Integer currentMaxMember) {
		this.currentMaxMember = currentMaxMember;
	}

	public Integer getCurrentMaxFiles() {
		return currentMaxFiles;
	}

	public void setCurrentMaxFiles(Integer currentMaxFiles) {
		this.currentMaxFiles = currentMaxFiles;
	}

	public Integer getCurrentMaxTeamspace() {
		return currentMaxTeamspace;
	}

	public void setCurrentMaxTeamspace(Integer currentMaxTeamspace) {
		this.currentMaxTeamspace = currentMaxTeamspace;
	}

	public long getSpaceUsed() {
		return spaceUsed;
	}

	public void setSpaceUsed(long spaceUsed) {
		this.spaceUsed = spaceUsed;
	}
    
    
}
