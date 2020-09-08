package com.huawei.sharedrive.app.filelabel.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.filelabel.dao.IFileLabelDAO;
import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.domain.FileLabelLink;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelQueryCondition;
import com.huawei.sharedrive.app.filelabel.exception.FileLabelException;
import com.huawei.sharedrive.app.filelabel.util.FileLabelContants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

/**
 * 
 * Desc  : filelabe 数据访问操作实现
 * Author: 77235
 * Date	 : 2016年11月28日
 */
@Repository("fileLabelDao")
public class FileLabelDaoImpl extends AbstractDAOImpl implements IFileLabelDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLabelDaoImpl.class);

    @SuppressWarnings("deprecation")
    @Override
    public void createFileLabel(final FileLabel fileLabel) throws FileLabelException {   
        try{
            Object retObj = sqlMapClientTemplate.insert(FileLabelContants.CONST_FL_INSERT, fileLabel); 
            
            LOGGER.info("[FileLabelDaoImpl] create Filelabel result:" + (retObj != null ? retObj : ""));
        } catch (Exception e) {
        	LOGGER.error("[FileLabelDaoImpl] createFileLabel error:" + e.getMessage(), e);
            FileLabelException.throwFilelabelException(e,  FileLabelContants.FL_EXCEPTION_ADD_NEW);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FileLabel queryFileLabelById(long enterpriseId, long labelId) throws FileLabelException {
        FileLabel retFilelabel = null;
        
        try{
            FileLabel paramBean = new FileLabel();
            paramBean.setId(labelId);
            paramBean.setEnterpriseId(enterpriseId);
            
            retFilelabel = (FileLabel) sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FL_QUERY_BY_ID, paramBean);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryFileLabelById error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e, FileLabelContants.FL_EXCEPTION_LIST_BY_ID);
        }
        
        return retFilelabel;    
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public FileLabel queryFileLabelByNameEqualMode(long enterpriseId, String labelName) throws FileLabelException {
        FileLabel retFilelabel = null;
        
        try{
            FileLabel paramBean = new FileLabel();
            paramBean.setLabelName(labelName);
            paramBean.setEnterpriseId(enterpriseId);
            
            retFilelabel = (FileLabel) sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FL_QUERY_BY_LABELNAME, paramBean);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryFileLabelByNameEqualMode error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e, FileLabelContants.FL_EXCEPTION_OBTAIN_BY_NAME);
        }
        
        return retFilelabel;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<FileLabel> queryFileLabelByNameLikeMode(FileLabelQueryCondition queryCondition) throws FileLabelException {
        List<FileLabel> retList = null;
        
        try{
            retList =  sqlMapClientTemplate.queryForList(FileLabelContants.CONST_FL_QUERY_BY_LABELNAME_LIKE_MODE, queryCondition);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryFileLabelByNameLikeMode error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e, FileLabelContants.FL_EXCEPTION_LIST_BY_NAME);
        }
        
        return retList;    
    }
    
    
    @Override
    public boolean checkFileLabelExist(long enterpriseId, String labelName) throws FileLabelException {
        
        return queryFileLabelByNameEqualMode(enterpriseId, labelName) != null;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateBindTimesForFileLabel(long enterpriseId, List<Long> labelIds, int times) {
        int result = 0;
        
        try{
            Map<Object, Object> paramMap = new HashMap<Object, Object>();
            
            paramMap.put("enterpriseId", enterpriseId);
            paramMap.put("keys", labelIds);
            paramMap.put("increTimes", times);
            
            result = sqlMapClientTemplate.update(FileLabelContants.CONST_FL_UPDATE_BINDTIME, paramMap);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] updateBindTimesForFileLabel error:" + e.getMessage(), e);
            // ignore
        }
        
        return result;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteFilelabeByIds(List<Long> fileLabelIds) throws FileLabelException {
        int retTag = 0;
        
        try{
            Map<Object, Object> paramMap = new HashMap<Object, Object>();
            paramMap.put("keys", fileLabelIds);
            
            retTag = sqlMapClientTemplate.delete(FileLabelContants.CONST_FL_DELETE_BY_ID_LIST, paramMap);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] deleteFilelabeByIds error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e, FileLabelContants.FL_EXCEPTION_DELETE_BY_ID);
        }

        return retTag;
    }
    
	@SuppressWarnings("deprecation")
	@Override
	public int deleteFilelabelsWithBindtimesLessThanOne() throws FileLabelException {
		int result = sqlMapClientTemplate.delete(FileLabelContants.CONST_FL_DELETE_WITH_BINDTIMES_LESS_ONE);
		
		return result;
	}

    @SuppressWarnings("deprecation")
    @Override
    public long getMaxFilelabelId() {
        long retVal = 0;
        try {
            retVal =  Long.valueOf(sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FL_MAX_ID).toString());
        } catch (Exception e) {
        	LOGGER.error("[FileLabelDaoImpl] getMaxFilelabelId error:" + e.getMessage(), e);
        }
        
        return retVal;
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<FileLabel> queryFilelabelsByIds(List<Long> flids) throws FileLabelException {
        List<FileLabel> retList = null;
        
        try{
            Map<Object, Object> paramMap = new HashMap<Object, Object>();
            paramMap.put("keys", flids);
            
            retList = sqlMapClientTemplate.queryForList(FileLabelContants.CONST_FL_QUERY_BY_ID_LIST, paramMap);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryFilelabelsByIds error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e,
                FileLabelContants.FL_EXCEPTION_LIST_BY_ID);
        }
        
        return retList;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<BaseFileLabelInfo> queryFilelabelByEnterprise(FileLabelQueryCondition queryCondition) throws FileLabelException {
        List<BaseFileLabelInfo> retList = null;
        
        try{
            retList = sqlMapClientTemplate.queryForList(FileLabelContants.CONST_FL_QUERY_BY_ENTERPRISEID, queryCondition);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryFilelabelByEnterprise error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e,  FileLabelContants.FL_EXCEPTION_LIST_BY_ENTERPRISE);
        }
        
        return retList;
    }  
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<FileLabel> queryUserLatestViewedFilelabel(long enterpriseId, long userId) {
        List<FileLabel> retList = null;
        
        try{
            Map<Object, Object> paramMap = new HashMap<Object, Object>(2);
            paramMap.put("enterpriseId", enterpriseId);
            paramMap.put("userId", userId);
            
            retList = sqlMapClientTemplate.queryForList(FileLabelContants.CONST_FL_QUERY_BY_USERID, paramMap);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryUserLatestViewedFilelabel error:" + e.getMessage(), e);
            // ignore
        }
        
        return retList;
    }
    
    @SuppressWarnings("deprecation")
    public long queryFilelabelCountByEnterprise(FileLabelQueryCondition queryCondition){
        long totalCount = 0;
        
        try {
            totalCount  = (long) sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FL_QUERY_TOTAL_BY_ENTERPRISEID, queryCondition);
        } catch (DataAccessException e) {
        	LOGGER.error("[FileLabelDaoImpl] queryFilelabelCountByEnterprise error:" + e.getMessage(), e);
        }
        
        return totalCount;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void bindFileLabelForInode(FileLabelLink fileLabelLink) throws FileLabelException {
        fileLabelLink.setTableSuffix(getTableSuffix(fileLabelLink.getOwnedBy()));
        
        try{
            sqlMapClientTemplate.insert(FileLabelContants.CONST_FLL_INSERT, fileLabelLink);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] bindFileLabelForInode error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e, FileLabelContants.FL_EXCEPTION_BIND);
        }
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<Long> queryLabelIdsByNodeId(long ownerId, long nodeId) throws FileLabelException {
        List<Long> retList = null;
        
        try{
            Map<Object, Object> paramMap = new HashMap<Object, Object>();
            paramMap.put("ownedBy", ownerId);
            paramMap.put("inodeId", nodeId);
            paramMap.put("tableSuffix", getTableSuffix(ownerId));
            
            retList = sqlMapClientTemplate.queryForList(FileLabelContants.CONST_FLL_QUERY_FLIDS_BY_INODE, paramMap);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] queryLabelIdsByNodeId error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e,  FileLabelContants.FL_EXCEPTION_LIST_BY_NODE);
        }
        
        return retList;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void unbindFileLabelForInode(long nodeId, long enterpriseId, long labelId, long ownerId) throws FileLabelException {
        FileLabelLink fll = new FileLabelLink(labelId, nodeId);
        
        try{
            fll.setOwnedBy(ownerId);
            fll.setTableSuffix(getTableSuffix(ownerId));
            sqlMapClientTemplate.delete(FileLabelContants.CONST_FLL_UNBIND_FOR_INODE, fll);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] unbindFileLabelForInode error:" + e.getMessage(), e);

            FileLabelException.throwFilelabelException(e,  FileLabelContants.FL_EXCEPTION_UNBIND);
        }
        
        List<Long> labelIds = new ArrayList<Long>();
        labelIds.add(labelId);
      
        updateBindTimesForFileLabel(enterpriseId, labelIds, -1);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int unbindAllFileLabelForInode(long ownerId, long nodeId) {
        int retVal = 0;
        
        try{
            FileLabelLink fll = new FileLabelLink();
            fll.setInodeId(nodeId);
            fll.setOwnedBy(ownerId);
            fll.setTableSuffix(getTableSuffix(ownerId));

            retVal = sqlMapClientTemplate.delete(FileLabelContants.CONST_FLL_UNBIND_ALL_FOR_INODE, fll);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] unbindAllFileLabelForInode error:" + e.getMessage(), e);
        }
        
        return retVal;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getFilelabelBindCount(long ownerId, long nodeId) throws FileLabelException {
        FileLabelLink fll = new FileLabelLink();
        int bintTimes = 0;
        fll.setInodeId(nodeId);
        
        try{
            fll.setTableSuffix(getTableSuffix(ownerId));
            fll.setOwnedBy(ownerId);
            
            bintTimes = (int) sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FLL_COUNTS_BY_INODE, fll);
        } catch(Exception e){
        	LOGGER.error("[FileLabelDaoImpl] getFilelabelBindCount error:" + e.getMessage(), e);
        }
        
        return bintTimes;
    }

    @SuppressWarnings("deprecation")
    @Override
    public long getMaxFilelabelLinkId(long ownerId) {
        long retVal = 0;
        try {
            Map<Object, Object> paramMap = new HashMap<Object, Object>();
            paramMap.put("tableSuffix", getTableSuffix(ownerId));
            paramMap.put("ownedBy", ownerId);
            
            retVal =  Long.valueOf(sqlMapClientTemplate.queryForObject(FileLabelContants.CONST_FLL_MAX_ID, paramMap).toString());
        } catch (Exception e) {
        	LOGGER.error("[FileLabelDaoImpl] getMaxFilelabelLinkId error:" + e.getMessage(), e);
        }
        
        return retVal;
    }

    /**
     * 获取分表后缀
     * @param fileLabelLink
     * @return
     */
    private int getTableSuffix(long ownerId) {
        if (ownerId <= 0) {
            throw new InvalidParamException("illegal owner id " + ownerId);
        }
        
        return (int) (HashTool.apply(String.valueOf(ownerId)) % FileLabelContants.CONST_BIZ_TABLE_COUNT);
    }
}
