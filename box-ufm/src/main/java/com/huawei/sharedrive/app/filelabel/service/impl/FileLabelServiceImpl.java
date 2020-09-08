package com.huawei.sharedrive.app.filelabel.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.filelabel.dao.IFileLabelDAO;
import com.huawei.sharedrive.app.filelabel.dao.impl.FileLabelCacheDao;
import com.huawei.sharedrive.app.filelabel.dao.impl.FilelabelIdGenerator;
import com.huawei.sharedrive.app.filelabel.dao.impl.FilelabelLinkIdGenerator;
import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.domain.FileLabelLink;
import com.huawei.sharedrive.app.filelabel.domain.LatestViewFileLabel;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelQueryCondition;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelRequestDto;
import com.huawei.sharedrive.app.filelabel.exception.FileLabelException;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FileLabelContants;
/**
 * 
 * Desc  : 文件标签服务接口标签实现 
 * Author: 77235
 * Date	 : 2016年11月28日
 */
@Service("fileLabelService")
public class FileLabelServiceImpl implements IFileLabelService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLabelServiceImpl.class);

    @Autowired
    private IFileLabelDAO fileLabelDao;
    
    @Autowired
    private FileLabelCacheDao fileLabelCacheDao;

    @Autowired
    private FilelabelIdGenerator filelabelIdGenerator;
    
    @Autowired
    private FilelabelLinkIdGenerator filelabelLinkIdGenerator;
    
    @Override
    public long bindFileLabel(FileLabelRequestDto fileLabelRequest) throws FileLabelException {
        String labelName = fileLabelRequest.getLabelName();
        long fileLabelId = fileLabelRequest.getLabelId();
        long enterpriseId = fileLabelRequest.getEnterpriseId();
        long ownerId = fileLabelRequest.getOwnerId();
        boolean isNew = false;
        
        /** 已绑定标签编号 */
        List<Long> bindedLabelids = isOutofTime(fileLabelRequest);
        FileLabel fileLabel = new FileLabel(fileLabelRequest);
        try {
            if (fileLabelId == FileLabelRequestDto.CONST_FILELABEL_UNBIND_ID) {
                FileLabel persistFilelabel = fileLabelDao.queryFileLabelByNameEqualMode(enterpriseId,
                    labelName);
                
                if (persistFilelabel != null) {
                    fileLabelId = persistFilelabel.getId();
                } else {
                    isNew = createFileLabel(fileLabel);
                    fileLabelId = fileLabel.getId();
                }
            } else {
                fileLabelId = fileLabelRequest.getLabelId();
            }
            
            if (fileLabelId == FileLabelRequestDto.CONST_FILELABEL_UNBIND_ID) {
                FileLabelException.throwFilelabelException(null,  FileLabelContants.FL_EXCEPTION_ADD_NEW);
            } 
            checkHasBinded(bindedLabelids, fileLabelId);

            /** 标签绑定过程 */
            FileLabelLink beBindLabel =  new FileLabelLink(fileLabelId, fileLabelRequest.getNodeId(), ownerId);
            beBindLabel.setId(filelabelLinkIdGenerator.getNextFilelabelLinkId(ownerId));
            fileLabelDao.bindFileLabelForInode(beBindLabel);
            
            /** 更新绑定次数 */
            List<Long> flidList = new ArrayList<Long>(1); 
            flidList.add(fileLabelId);
            fileLabelDao.updateBindTimesForFileLabel(enterpriseId, flidList, CONST_DEFAULT_ADD_TIMES);  
        } catch (FileLabelException ex) {
            throw ex;
        } catch (Exception t) {
        	LOGGER.info("[Filelabel] bindFileLabel error :" + t.getMessage(), t);

            throw new FileLabelException(t);
        } 
        
        try {
            /** 缓存操作过程 */
            if (isNew) {
                fileLabelCacheDao.addCacheFilelabel(fileLabel, true);
            } else {
                fileLabelCacheDao.updateFilelabelBindtimes(enterpriseId, fileLabelId, CONST_DEFAULT_ADD_TIMES);
            }
            
            LatestViewFileLabel latestViewFl = new LatestViewFileLabel(fileLabelId, labelName);
            fileLabelCacheDao.updateFilelabelForUserLatestViewed(enterpriseId, fileLabelRequest.getBindUserId(), latestViewFl);
        } catch (Exception t) {
        	LOGGER.info("[Filelabel] bindFileLabel error :" + t.getMessage(), t);
        }
        
        return fileLabelId;
    }
    
    /**
     * 是否已经绑定
     * @param bindedLabelids
     * @param fileLabelId
     */
    private void checkHasBinded(List<Long> bindedLabelids, long fileLabelId) {
        if (bindedLabelids != null) {
            for (long fid : bindedLabelids) {
                if (fileLabelId == fid) {
                    FileLabelException.throwFilelabelException(null,
                        FileLabelContants.FL_EXCEPTION_BIND_HAS_BINDED);
                }
            }
        }
    }

    /**
     * 是否超过绑定限制
     * @param fileLabelRequest
     * @return
     */
    private List<Long> isOutofTime(FileLabelRequestDto fileLabelRequest){
        List<Long> flIds = fileLabelDao.queryLabelIdsByNodeId(fileLabelRequest.getOwnerId(), fileLabelRequest.getNodeId());
        int bindType = fileLabelRequest.getBindType();
        int bindTimes = 0;
        
        if(null != flIds  && !flIds.isEmpty()){
            bindTimes = flIds.size();
        }
        
        switch (bindType) {
        	case FileLabelContants.CONST_BIZ_TEAMSPACE_BIND_TYPE:
	            if (bindTimes > FileLabelContants.CONST_BIZ_MAX_FL_BIND_FOR_TEAMSPACE ){
	                FileLabelException.throwFilelabelException(null, FileLabelContants.FL_EXCEPTION_BIND_OUTOF_MAX_TIME);
	            }
	            break;
            case FileLabelContants.CONST_BIZ_FOLDER_BIND_TYPE:
            default:
            	if (bindTimes > FileLabelContants.CONST_BIZ_MAX_FL_BIND_FOR_FOLDER ){
                    FileLabelException.throwFilelabelException(null, FileLabelContants.FL_EXCEPTION_BIND_OUTOF_MAX_TIME);
                }
                break;    
        }
        
        return flIds;
    }
    
    @Override
    public List<LatestViewFileLabel> retrivalUserLatestVistedLabels(long enterpriseId, long userId){
        List<LatestViewFileLabel> latestViewFls = fileLabelCacheDao.queryFilelabelForUserLatestViewed(enterpriseId, userId);
        
        if (latestViewFls == null){
            latestViewFls = new ArrayList<LatestViewFileLabel>(5);
            
            List<FileLabel> storedFilelabels = fileLabelDao.queryUserLatestViewedFilelabel(enterpriseId, userId);
            for(FileLabel fl : storedFilelabels){
                latestViewFls.add(new LatestViewFileLabel(fl.getId(), fl.getLabelName()));
            }
            
            fileLabelCacheDao.initUserLatestViewedFilelabel(enterpriseId, userId, latestViewFls);
        }
        return  latestViewFls;
    }

    @Override
    public void unbindFileLabel(FileLabelRequestDto fileLabelRequest) throws FileLabelException {
        long enterpriseId = fileLabelRequest.getEnterpriseId();
        long labelId = fileLabelRequest.getLabelId();
        long ownerId = fileLabelRequest.getOwnerId();
        long nodeId = fileLabelRequest.getNodeId();
        
        fileLabelDao.unbindFileLabelForInode(nodeId, enterpriseId, labelId, ownerId);
        try{
            List<Long> flidList = new ArrayList<Long>(1); 
            flidList.add(labelId);
            
            fileLabelDao.updateBindTimesForFileLabel(enterpriseId, flidList, CONST_DEFAULT_SUB_TIMES);
            fileLabelCacheDao.updateFilelabelBindtimes(enterpriseId, labelId, CONST_DEFAULT_SUB_TIMES);
        } catch (FileLabelException ex) {
            throw ex;
        } catch (Exception t) {
			LOGGER.info("[Filelabel] unbindFileLabel error :" + t.getMessage());
            throw new FileLabelException(t);
        }
    }

    @Override
    public List<FileLabel> retrivalFileLabelByNameLikeMode(long enterpriseId, String labelName, int pageNum, int pageSize) throws FileLabelException {
        FileLabelQueryCondition queryCondition = new FileLabelQueryCondition(pageNum, pageSize);
        queryCondition.setEnterpriseId(enterpriseId)
            .setLabelName(labelName)
            .setLabelNameLike(true)
            .setFields(" id, labelName ");
        
        return fileLabelDao.queryFileLabelByNameLikeMode(queryCondition);
    }

    @Override
    public List<Long> retrivalFileLabelIdsByNode(long nodeId, long ownerId) throws FileLabelException {
        
        return fileLabelDao.queryLabelIdsByNodeId(ownerId, nodeId);
    }

  
    public FileLabel retrivalLabelById(long enterpriseId, long labelId) throws FileLabelException {
        FileLabel retLabel = fileLabelCacheDao.getCacheFilelabel(enterpriseId, labelId);
        
        if (retLabel == null){
            retLabel = fileLabelDao.queryFileLabelById(enterpriseId, labelId);
            if (retLabel != null){
                fileLabelCacheDao.addCacheFilelabel(retLabel, false);
            }
        }
        
        return retLabel;
    }
    
    @Override
    public List<FileLabel> retrivalFileLabelByIds(long enterpriseId, List<String> labelIds) throws FileLabelException {
        List<FileLabel> retList = new ArrayList<FileLabel>(10);
        
        for(String labelId : labelIds){
            FileLabel fl = retrivalLabelById(enterpriseId, Long.valueOf(labelId));
            if (fl != null){
                retList.add(fl);
            }
        }
        
        return retList;
    }

    /**
     * 新增文件标签
     * @param fileLabel
     * @return
     * @throws InterruptedException
     */
    private boolean createFileLabel(FileLabel fileLabel) throws InterruptedException {
        String lockKey = FileLabelContants.CONST_LOCK_PREFIX_FLAG + fileLabel.getEnterpriseId() + 
                FileLabelContants.CONST_KEY_DELIMETER_FLAG + fileLabel.getLabelName().hashCode();
        long fileLabelId = -1;
        boolean isObtainedLock = false;
        boolean isNew = false;
        
        try{
            if(fileLabelCacheDao.tryLock(lockKey, FileLabelContants.CONST_DEFAULT_SLEEP_TIME, TimeUnit.MILLISECONDS, 1)){
                isObtainedLock = true;
                fileLabelId =  filelabelIdGenerator.getNextFilelabelId();
                fileLabel.setId(fileLabelId);
                
                fileLabelDao.createFileLabel(fileLabel);
                isNew = true;
            } else {
                int waitTimes = 3;
                FileLabel tempFileLabel = null;
                
                /** 等待获取结果 */
                while (waitTimes -- > 0){
                    tempFileLabel = fileLabelDao.queryFileLabelByNameEqualMode(fileLabel.getEnterpriseId(), fileLabel.getLabelName());
                    if (tempFileLabel != null){
                        fileLabelId = tempFileLabel.getId();
                        break;
                    }
                }
            }
        }finally{
            if (isObtainedLock){
                fileLabelCacheDao.unlock(lockKey);
            }
        }
        
        // 补救措施
        if (fileLabelId == FileLabelRequestDto.CONST_FILELABEL_UNBIND_ID || fileLabelId == -1){
            fileLabelId =  filelabelIdGenerator.getNextFilelabelId();
            fileLabel.setId(fileLabelId);
            fileLabelDao.createFileLabel(fileLabel);
        }
        
        return isNew;
    }

    @Override
    public List<BaseFileLabelInfo> retrivalFilelabelsByEnterprise(FileLabelQueryCondition queryCondition)
        throws FileLabelException {
        int pageSize = queryCondition.getPageSize();
        int currPage = queryCondition.getCurrPage();
        long totalCount = fileLabelDao.queryFilelabelCountByEnterprise(queryCondition);
        int totalPage = (int) ((totalCount % pageSize == 0) ? (totalCount / pageSize)
            : (totalCount / pageSize + 1));
        
        if (currPage > totalPage) {
            currPage = 1;
            queryCondition.setCurrPage(currPage);
        }
        queryCondition.setTotalCount(totalCount);
        queryCondition.setTotalPage(totalPage);
        queryCondition.setFields(" id, labelName ");
        List<BaseFileLabelInfo> flList = fileLabelDao.queryFilelabelByEnterprise(queryCondition);
        
        return flList;
    }

    @Override
    public void unbindAllFileLabelForInode(long ownerId, long nodeId, long enterpriseId)
        throws FileLabelException {
        List<Long> allLabels = fileLabelDao.queryLabelIdsByNodeId(ownerId, nodeId);
        
        if (null != allLabels && !allLabels.isEmpty()) {
            int size = fileLabelDao.unbindAllFileLabelForInode(ownerId, nodeId);
            if (size > 0) {
                fileLabelDao.updateBindTimesForFileLabel(enterpriseId, allLabels, CONST_DEFAULT_SUB_TIMES);
                
                for (long labelId : allLabels) {
                    fileLabelCacheDao.updateFilelabelBindtimes(enterpriseId, labelId,
                        CONST_DEFAULT_SUB_TIMES);
                }
            }
        }
    }

	@Override
	public void clearFilelabelsWithBindtimesLessThanOne() throws FileLabelException {
		try {
			int result = fileLabelDao.deleteFilelabelsWithBindtimesLessThanOne();
			LOGGER.info("[Filelabel] clear filelabels which bindedtimes attribute are less than one, affected rows is :" + result);
		} catch (Exception e) {
			LOGGER.error("[Filelabel] clear filelabels which bindedtimes attribute are less than one, exception:" + e.getMessage(), e);
		}
	} 
}
