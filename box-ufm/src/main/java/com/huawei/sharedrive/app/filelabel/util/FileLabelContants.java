package com.huawei.sharedrive.app.filelabel.util;
/**
 * 
 * Desc  : filelabel使用到的常量信息
 * Author: 77235
 * Date	 : 2016年11月29日
 */
public interface FileLabelContants {
    /************************* 异常代码 *****************************************/
    String FL_EXCEPTION_LACK_LABELNAME = "fl.lack.labelname";
    String FL_EXCEPTION_UNKNOW = "fl.unknow.exception";
    String FL_EXCEPTION_INVALID_LABELID = "fl.invalid.labelid";
    String FL_EXCEPTION_INVALID_USER = "fl.invalid.user";
    String FL_EXCEPTION_INVALID_USER_STATUS = "fl.invalid.user.status";
    String FL_EXCEPTION_ADD_NEW = "fl.add.new.fail";
    String FL_EXCEPTION_BIND = "fl.bind.fail";
    String FL_EXCEPTION_UNBIND = "fl.unbind.fail";
    String FL_EXCEPTION_OBTAIN_BY_ID = "fl.obtain.byid.fail"; 
    String FL_EXCEPTION_OBTAIN_BY_NAME = "fl.obtain.byname.fail";
    String FL_EXCEPTION_LIST_BY_ID = "fl.list.byid.fail";
    String FL_EXCEPTION_LIST_BY_NAME = "fl.list.byname.fail";
    String FL_EXCEPTION_LIST_BY_NODE = "fl.list.bynode.fail";
    String FL_EXCEPTION_LIST_BY_ENTERPRISE = "fl.list.byenterprise.fail";
    String FL_EXCEPTION_DELETE_BY_ID = "fl.delete.byid.fail";
    String FL_EXCEPTION_BIND_OUTOF_MAX_TIME = "fl.bind.outof.maxtimes";
    String FL_EXCEPTION_BIND_HAS_BINDED = "fl.bind.view.tag.hasbind";
    /************************* 异常代码 *****************************************/
    
    /************************* FileLabelCacheDao类中常量  ************************/
    /** 锁key前缀 */
    String CONST_LOCK_PREFIX_FLAG = "lock:";
    /** 緩存标签key前缀 */
    String CONST_CACHE_PREFIX_FLAG = "fl:";
    /** 名称之间的分隔符 */
    String CONST_KEY_DELIMETER_FLAG = ":";
    /** 锁默认值 */
    int CONST_DEFAULT_LOCKED_VALUE = -1;
    /** 默认轮询次数 */
    int CONST_DEFAULT_CYCLE_TIMES = 5;
    /** 默认休眠时间 */
    int CONST_DEFAULT_SLEEP_TIME = 300;
    /************************* FileLabelCacheDao类中常量  ************************/
    
    /************************* 业务常量 *****************************************/
    long CONST_BIZ_UNBIND_FL_ID = 0;
    /** 個人文件中單個文件最大绑定标签数 */
    int CONST_BIZ_MAX_FL_BIND_FOR_FOLDER = 5;
    /** 團隊空間文件允許綁定標簽數 */
    int CONST_BIZ_MAX_FL_BIND_FOR_TEAMSPACE = 10;
    /** 用戶最大緩存標簽數 */
    int CONST_BIZ_MAX_CACHE_ITEM_FOR_USER = 5;
    /** 分表数量 */
    int CONST_BIZ_TABLE_COUNT = 500;
    /** 文件夾綁定類型 */
    int CONST_BIZ_FOLDER_BIND_TYPE = 1;
    /** 團隊空間綁定類型 */
    int CONST_BIZ_TEAMSPACE_BIND_TYPE = 2;
    
    /************************* 业务常量 *****************************************/

    /************************* ibatis配置  *****************************************/
    /** 新增文件标签key */
    String CONST_FL_INSERT = "com.huawei.sharedrive.app.filelabel.insertFilelabel";
    /** 根据主键检索标签 */
    String CONST_FL_QUERY_BY_ID = "com.huawei.sharedrive.app.filelabel.queryByPrimaryKey";
    /** 根据主键列表检索标签 */
    String CONST_FL_QUERY_BY_ID_LIST = "com.huawei.sharedrive.app.filelabel.queryFilelabelByIds";
    /** 根据名称检索标签 */
    String CONST_FL_QUERY_BY_LABELNAME = "com.huawei.sharedrive.app.filelabel.queryByLabelName";
    /** 根据名称检索标签 模糊匹配模式 */
    String CONST_FL_QUERY_BY_LABELNAME_LIKE_MODE = "com.huawei.sharedrive.app.filelabel.queryByLikeLabelName";
    /** 检索企业标签列表 */
    String CONST_FL_QUERY_BY_ENTERPRISEID = "com.huawei.sharedrive.app.filelabel.queryFilelabelByEnterprise";
    /** 检索用户最近添加的标签信息 */
    String CONST_FL_QUERY_BY_USERID = "com.huawei.sharedrive.app.filelabel.queryFilelabelByUser";
    /** 检索企业标签总数 */
    String CONST_FL_QUERY_TOTAL_BY_ENTERPRISEID = "com.huawei.sharedrive.app.filelabel.queryTotalCountFilelabelByEnterprise";
    /** 解除文件标签绑定的最大ID值 */
    String CONST_FL_MAX_ID = "com.huawei.sharedrive.app.filelabel.queryMaxFilelabelId";
    /** 更新关联次数 */
    String CONST_FL_UPDATE_BINDTIME = "com.huawei.sharedrive.app.filelabel.updateBindTimes";
    /** 根据文件标签编号列表删除文件标签 */
    String CONST_FL_DELETE_BY_ID_LIST = "com.huawei.sharedrive.app.filelabel.deleteFilelabelByIds";
    /** 删除绑定次数为0的文件标签 */
    String CONST_FL_DELETE_WITH_BINDTIMES_LESS_ONE = "com.huawei.sharedrive.app.filelabel.deleteFilelabelsWithBindtimesLessThanOne";
    /** 默认filelabelLink 新增key */
    String CONST_FLL_INSERT = "com.huawei.sharedrive.app.filelabel.insertFilelabelLink";
    /** 检索标签id列表 */
    String CONST_FLL_QUERY_FLIDS_BY_INODE = "com.huawei.sharedrive.app.filelabel.queryLabelidsByInode";
    /** 检索文件绑定的标签数 */
    String CONST_FLL_COUNTS_BY_INODE = "com.huawei.sharedrive.app.filelabel.queryLabelCountByInode";
    /** 检索文件绑定的标签列表信息 */
    String CONST_FLL_QUERY_LABELIDS_BY_INODE = "com.huawei.sharedrive.app.filelabel.queryLabelIdsByInode";
    /** 解除文件标签绑定 */
    String CONST_FLL_UNBIND_FOR_INODE = "com.huawei.sharedrive.app.filelabel.unbindFileLabelForInode";
    /** 解除文件的所有标签绑定 */
    String CONST_FLL_UNBIND_ALL_FOR_INODE = "com.huawei.sharedrive.app.filelabel.unbindAllLabelForInode";
    /** 解除文件标签绑定的最大ID值 */
    String CONST_FLL_MAX_ID = "com.huawei.sharedrive.app.filelabel.queryMaxFilelabelLinkId";
    /************************* ibatis配置 *****************************************/
}
