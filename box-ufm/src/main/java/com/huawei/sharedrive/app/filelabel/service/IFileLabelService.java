package com.huawei.sharedrive.app.filelabel.service;

import java.util.List;

import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.domain.LatestViewFileLabel;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelQueryCondition;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelRequestDto;
import com.huawei.sharedrive.app.filelabel.exception.FileLabelException;

/**
 * 
 * Desc  : 文件标签业务接口
 * Author: 77235
 * Date	 : 2016年11月28日
 */
public interface IFileLabelService {
    /** 默认增加的绑定次数 */
    int CONST_DEFAULT_ADD_TIMES = 1;
    /** 默认减少的绑定次数 */
    int CONST_DEFAULT_SUB_TIMES = -1;
    /** 最近使用标签的最大次数 */
    int CONST_MAX_LATEST_USE_FL = 5;
    /** 单项标签默认缓存时长 */
    long CONST_DEFAULT_CACHE_TIME = 1800000;
    /** 分隔符 */
    String CONST_FL_SPLIT_TAG = ":";
    /** 单项缓存前缀 */
    String CONST_FL_CACHE_PREFIX = "fl";
    /** 最近5次使用的标签前缀 */
    String CONST_FL_LATEST_VISTED_PREFIX = "fl:latest_five:";

    /**
     * 绑定标签
     * @param fileLabelRequest
     */
    long bindFileLabel(FileLabelRequestDto fileLabelRequest) throws FileLabelException;
    
    /**
     * 根据标签名称检索标签
     * @return
     * @throws FileLabelException
     */
    List<FileLabel> retrivalFileLabelByNameLikeMode(long enterpriseId, String labelName, int pageNum, int pageSize) throws FileLabelException;
    
    /**
     * 根据文件标签id列表检索文件标签
     * @param labelIds
     * @return
     * @throws FileLabelException
     */
    List<FileLabel> retrivalFileLabelByIds(long enterpriseId, List<String> labelIds) throws FileLabelException;

    /**
     * 获取用户最近使用的标签数
     * @param enterpriseId
     * @param userId
     * @return
     * @throws FileLabelException
     */
    List<LatestViewFileLabel> retrivalUserLatestVistedLabels(long enterpriseId, long userId) throws FileLabelException;
    
    /**
     * 獲取企業對應的標簽列表信息
     * @param enterpriseId
     * @param userId
     * @return
     * @throws FileLabelException
     */
    List<BaseFileLabelInfo> retrivalFilelabelsByEnterprise(FileLabelQueryCondition queryCondition) throws FileLabelException;
    
    /**
     * 解绑标签
     * @param fileLabelRequest
     */
    void unbindFileLabel(FileLabelRequestDto fileLabelRequest) throws FileLabelException;
    
    /**
     * 解绑文件的所有标签
     * @param fileLabelRequest
     */
    void unbindAllFileLabelForInode(long ownerId, long nodeId, long enterpriseId) throws FileLabelException;

    /**
     * 检索文件对应的标签编号列表
     * @param nodeId
     * @param ownerId
     * @return
     * @throws FileLabelException
     */
    List<Long> retrivalFileLabelIdsByNode(long nodeId, long ownerId) throws FileLabelException;
    
    /**
     * 清除标签绑定次数不大于0的标签
     * @throws FileLabelException
     */
    void clearFilelabelsWithBindtimesLessThanOne() throws FileLabelException;
}
