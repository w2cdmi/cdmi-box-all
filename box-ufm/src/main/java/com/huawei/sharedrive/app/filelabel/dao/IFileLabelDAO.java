package com.huawei.sharedrive.app.filelabel.dao;

import java.util.List;

import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.domain.FileLabelLink;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelQueryCondition;
import com.huawei.sharedrive.app.filelabel.exception.FileLabelException;

/**
 * 
 * Desc  : 文件标签数据访问接口
 * Author: 77235
 * Date	 : 2016年11月28日
 */
public interface IFileLabelDAO {
    /**
     * 针对企业创建文件标签
     * @param fileLabel
     * @return
     * @throws FileLabelException
     */
    void createFileLabel(FileLabel fileLabel) throws FileLabelException;
    
    /**
     * 根据主键检索标签
     * @param enterpriseId
     * @param labelName
     * @return
     * @throws FileLabelException
     */
    FileLabel queryFileLabelById(long enterpriseId, long labelId) throws FileLabelException;
    
    /**
     * 根据标签名称和企业名称检索标签
     * @param enterpriseId
     * @param labelName
     * @return
     * @throws FileLabelException
     */
    FileLabel queryFileLabelByNameEqualMode(long enterpriseId, String labelName) throws FileLabelException;
    
    /**
     * 根据标签名称和企业名称检索标签
     * @param enterpriseId
     * @param labelName
     * @return
     * @throws FileLabelException
     */
    List<FileLabel> queryFileLabelByNameLikeMode(FileLabelQueryCondition queryCondition) throws FileLabelException;
    
    /**
     * 根据文件标签主键查询文件标签列表
     * @param flids
     * @return
     */
    List<FileLabel> queryFilelabelsByIds(List<Long> flids) throws FileLabelException;
    
    /**
     * 获取企业所有的标签信息
     * @param enterpriseId
     * @return
     * @throws FileLabelException
     */
    List<BaseFileLabelInfo> queryFilelabelByEnterprise(FileLabelQueryCondition queryCondition) throws FileLabelException;
    
    /**
     * 获取用户最近添加的标签
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<FileLabel> queryUserLatestViewedFilelabel(long enterpriseId, long userId);
    
    /**
     * 获取企业总标签数
     * @param enterpriseId
     * @return
     */
    long queryFilelabelCountByEnterprise(FileLabelQueryCondition queryCondition);
    
    /**
     * 更新文件标签绑定次数
     * @param enterpriseId
     * @param labelId
     * @param cloudUserId
     */
    int updateBindTimesForFileLabel(long enterpriseId, List<Long> labelIds, int times) throws FileLabelException;
    
    /**
     * 根据文件标签编号列表删除文件标签
     * @param fileLabelIds
     * @return
     * @throws FileLabelException
     */
    int deleteFilelabeByIds(List<Long> fileLabelIds) throws FileLabelException;
    
    /**
     * 删除文件标签绑定次数不大于0的标签
     * @return
     * @throws FileLabelException
     */
    int deleteFilelabelsWithBindtimesLessThanOne() throws FileLabelException;
    
    /**
     * 校验文件标签是否存在
     * @param enterpriseId
     * @param labelName
     * @return
     * @throws FileLabelException
     */
    boolean checkFileLabelExist(long enterpriseId, String labelName) throws FileLabelException;

    /**
     * 绑定文件标签
     * @param fileLabelLink
     * @throws FileLabelException
     */
    void bindFileLabelForInode(FileLabelLink fileLabelLink) throws FileLabelException;

    /**
     * 检索标签列表
     * @param nodeId
     * @param ownerId
     * @return
     */
    List<Long> queryLabelIdsByNodeId(long ownerId, long nodeId) throws FileLabelException;
    
    /**
     * 解除文件标签绑定
     * @param enterpriseId
     * @param labelId
     * @param cloudUserId
     */
    void unbindFileLabelForInode(long nodeId, long enterpriseId, long labelId, long ownerId) throws FileLabelException;
    
    /**
     * 解除文件的所有綁定標簽
     * @param enterpriseId
     * @param labelId
     * @param cloudUserId
     */
    int unbindAllFileLabelForInode(long ownerId, long nodeId) throws FileLabelException;
    
    /**
     * 校驗文件綁定數是否超過限制
     * @param ownerId
     * @param nodeId
     * @param bindType
     * @throws FileLabelException
     */
    int getFilelabelBindCount(long ownerId, long nodeId) throws FileLabelException;
    
    /**
     * 得到文件标签的最大值
     * @return
     */
    long getMaxFilelabelId();
    
    /**
     * 得到文件标签关联表
     * @param ownerId
     * @return
     */
    long getMaxFilelabelLinkId(long ownerId);
}
