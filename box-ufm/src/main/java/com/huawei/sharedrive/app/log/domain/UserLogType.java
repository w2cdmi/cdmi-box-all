package com.huawei.sharedrive.app.log.domain;

import java.util.Locale;

import com.huawei.sharedrive.app.logconfig.listener.LogListener;

import pw.cdmi.core.utils.BundleUtil;

/**
 * 节点操作类型
 * 
 * @author l90003768
 * 
 */
public enum UserLogType
{
    
    /**
     * 添加节点访问控制
     */
    ADD_NODE_ACL("node.add.acl", 1, true),
    
    /**
     * 添加节点访问控制失败
     */
    ADD_NODE_ACL_ERR("node.add.acl.error", 1001, true),
    
    ADD_SHARE("share.add", 2, true),
    
    ADD_SHARE_ERR("share.add.error", 1002, true),
    
    /**
     * 添加团队空间成员
     */
    ADD_TEAMSPACE_MEMBER("teamspace.member.add", 3, true),
    
    /**
     * 添加团队空间成员失败
     */
    ADD_TEAMSPACE_MEMBER_ERR("teamspace.member.add.error", 1003, true),
    
    /**
     * 
     */
    ASYNC_TASK_STATUS("async.task.result", 200, false),
    
    /**
     * 取消所有共享
     */
    DELETE_ALL_SHARE("share.all.delete", 4, true),
    
    /**
     * 取消所有共享失败
     */
    DELETE_ALL_SHARE_ERR("share.all.delete.error", 1004, true),
    
    /**
     * 取消节点的同步状态
     */
    CANCLE_INODE_SYNC("inode.cancel.sync", 5, true),
    
    /**
     * 取消节点的同步状态失败
     */
    CANCLE_INODE_SYNC_ERR("inode.cancel.sync.error", 1005, true),
    
    /**
     * 异步清理
     */
    CLEAN_TRASH_ASYNC("async.clean.trash", 7, true),
    
    /**
     * 异步清理失败
     */
    CLEAN_TRASH_ASYNC_ERR("async.clean.trash.error", 1007, true),
    
    /**
     * 异步复制
     */
    COPY_ASYNC("async.copy", 8, true),
    
    /**
     * 异步复制失败
     */
    COPY_ASYNC_ERR("async.copy.error", 1008, true),
    
    /**
     * 文件的复制
     */
    COPY_FILE("file.copy", 9, true),
    
    /**
     * 文件的复制失败
     */
    COPY_FILE_ERR("file.copy.error", 1009, true),
    
    /**
     * 文件夹的复制
     */
    COPY_FOLDER("folder.copyFolder", 10, true),
    /**
     * 文件夹复制失败
     */
    COPY_FOLDER_ERR("folder.copyFolder.error", 1010, true),
    
    /**
     * 创建文件夹成功
     */
    CREATE_FOLDER("folder.createFolder", 11, true),
    /**
     * 创建文件夹失败
     */
    CREATE_FOLDER_ERR("folder.createFolder.error", 1011, true),
    /**
     * 创建外链
     */
    CREATE_LINK("link.create", 12, true),
    /**
     * 创建外链失败
     */
    CREATE_LINK_ERR("link.create.error", 1012, true),
    /**
     * 创建团队空间
     */
    CREATE_TEAMSPACE("teamspace.create", 13, true),
    /**
     * 创建团队空间失败
     */
    CREATE_TEAMSPACE_ERR("teamspace.create.error", 1013, true),
    /**
     * 异步删除
     */
    DELETE_ASYNC("async.delete", 14, true),
    /**
     * 异步删除失败
     */
    DELETE_ASYNC_ERR("async.delete.error", 1014, true),
    /**
     * 删除文件
     */
    DELETE_FILE("file.delete", 15, true),
    /**
     * 删除文件失败
     */
    DELETE_FILE_ERR("file.delete.error", 1015, true),
    /**
     * 删除文件夹
     */
    DELETE_FOLDER("folder.delete", 16, true),
    /**
     * 删除文件夹失败
     */
    DELETE_FOLDER_ERR("folder.delete.error", 1016, true),
    /**
     * 删除外链
     */
    DELETE_LINK("link.delete", 17, true),
    DELETE_LINK_ERR("link.delete.error", 1017, true),
    /**
     * 删除节点访问控制
     */
    DELETE_NODE_ACL("node.delete.acl", 18, true),
    /**
     * 删除节点访问控制失败
     */
    DELETE_NODE_ACL_ERR("node.delete.acl.error", 1018, true),
    /**
     * 删除共享
     */
    DELETE_SHARE("share.delete", 19, true),
    /**
     * 删除共享失败
     */
    DELETE_SHARE_ERR("share.delete.error", 1019, true),
    /**
     * 删除团队空间
     */
    DELETE_TEAMSPACE("teamspace.delete", 20, true),
    /**
     * 删除团队空间失败
     */
    DELETE_TEAMSPACE_ERR("teamspace.delete.error", 1020, true),
    /**
     * 删除团队空间成员
     */
    DELETE_TEAMSPACE_MEMBER("teamspace.member.delete", 21, true),
    /**
     * 修改团队空间成员失败
     */
    DELETE_TEAMSPACE_MEMBER_ERR("teamspace.member.delete.error", 1021, true),
    /**
     * 彻底删除回收站的状态节点
     */
    DELETE_TRASH_ITEM("trash.delete.item", 22, true),
    /**
     * 彻底删除回收站的状态节点失败
     */
    DELETE_TRASH_ITEM_ERR("trash.delete.item.error", 1022, true),
    /**
     * 删除版本文件
     */
    DELETE_VERSION_FILE("file.version.delete", 23, true),
    /**
     * 删除版本文件失败
     */
    DELETE_VERSION_FILE_ERR("file.version.delete.error", 1023, true),
    /**
     * 获取文件下载地址
     */
    DOWN_URL_FILE("file.down.url", 25, true), /**
    /**
     * 获取文件下载地址失败
     */
    DOWN_URL_FILE_ERR("file.down.url.error", 1025, true),
    /**
     * 异步获取增量元数据
     */
    GET_DELTA_SYNC_META_DATA("meta.data.get.sync", 27, false),
    /**
     * 异步获取增量元数据失败
     */
    GET_DELTA_SYNC_META_DATA_ERR("meta.data.get.sync.error", 1027, true),
    /**
     * 获取文件详细信息
     */
    GET_FILE("file.get", 28, false),
    /**
     * 获取文件详细信息失败
     */
    GET_FILE_ERR("file.get.error", 1028, true),
    /**
     * 获取文件夹详细信息
     */
    GET_FOLDER("folder.get", 29, false),
    /**
     * 获取文件夹失败
     */
    GET_FOLDER_ERR("folder.get.errorr", 1029, true),
    /**
     * 通过文件夹名获取文件夹
     */
    GET_FOLDER_BY_NAME("folder.get.name", 30, false),
    /**
     * 通过文件夹名获取文件夹失败
     */
    GET_FOLDER_BY_NAME_ERR("folder.get.name.error", 1030, false),
    /**
     * 获取文件夹及其子文件夹的元数据
     */
    GET_FOLDER_META_DATA("meta.data.get.folder", 31, false),
    /**
     * 获取文件夹及其子文件夹的元数据失败
     */
    GET_FOLDER_META_DATA_ERR("meta.data.get.folder.error", 1031, true),
    /**
     * 获取外链
     */
    GET_LINKINFO("link.get", 32, false),
    GET_LINKINFO_ERR("link.get.error", 1032, true),
    GET_LINK_FILE_INFO("link.file.get", 94, false),
    GET_LINK_FILE_INFO_ERR("link.file.get.error", 1094, true),
    /**
     * 获取节点下载路径
     */
    GET_NODE_PATH("node.get.path", 33, false),
    /**
     * 获取节点下载路径失败
     */
    GET_NODE_PATH_ERR("node.get.path.error", 1033, true),
    /**
     * 获取指定用户对某资源的权限项信息
     */
    GET_NODE_PERMISSION("node.get.permission", 34, false),
    /**
     * 获取指定用户对某资源的权限项信息失败
     */
    GET_NODE_PERMISSION_ERR("node.get.permission.error", 1034, true),
    /**
     * 获取节点信息
     */
    GET_NODEINFO("nodeinfo.get", 35, false),
    /**
     * 获取节点信息失败
     */
    GET_NODEINFO_ERR("nodeinfo.get.error", 1035, true),
    /**
     * 获取外链指向的文件夹或文件信息 该接口可提供给匿名用户访问外链
     */
    GET_NODEINOF_LINK("link.get.nodeinfo", 36, false),
    /**
     * 获取外链指向的文件夹或文件信息失败 该接口可提供给匿名用户访问外链
     */
    GET_NODEINOF_LINK_ERR("link.get.nodeinfo.error", 1036, true),
    /**
     * 获取团队空间
     */
    GET_TEAMSPACE("teamspace.get", 37, false),
    /**
     * 获取团队空间失败
     */
    GET_TEAMSPACE_ERR("teamspace.get.error", 37, true), /**
     * 获取团队空间成员
     */
    GET_TEAMSPACE_MEMBER("teamspace.member.get", 38, false),
    /**
     * 获取团队空间成员失败
     */
    GET_TEAMSPACE_MEMBER_ERR("teamspace.member.get.error", 1038, true),
    /**
     * 获取文件缩略图地址
     */
    GET_THUMBNAIL_URL_FILE("file.get.thumbnail.url", 39, false),
    /**
     * 获取文件缩略图地址失败
     */
    GET_THUMBNAIL_URL_FILE_ERR("file.get.thumbnail.url.error", 1039, true),
    /**
     * 列举文件夹
     */
    LIST_FOLDER("folder.list", 40, false),
    /**
     * 列举文件夹失败
     */
    LIST_FOLDER_ERR("folder.list.error", 1040, true),
    LIST_MY_SHARES("share.list.my", 41, false),
    LIST_MY_SHARES_ERR("share.list.my.error", 1041, true),
    /**
     * 列举节点所有的访问控制
     */
    LIST_NODE_ALLLIST_ACL("node.listl.all.acl", 42, false),
    /**
     * 列举节点所有的访问控制失败
     */
    LIST_NODE_ALLLIST_ACL_ERR("node.listl.all.acl.error", 1042, true),
    /**
     * 列举节点访问控制
     */
    LIST_NODE_LIST_ACL("node.list.acl", 43, false),
    /**
     * 列举节点访问控制失败
     */
    LIST_NODE_LIST_ACL_ERR("node.list.acl.error", 1043, true),
    LIST_REGION("region.list", 44, false),
    LIST_REGION_ERR("region.list.error", 1044, true),
    /**
     * 列举共享关系
     */
    LIST_SHARE_USERS("share.list.users", 45, false),
    /**
     * 列举共享关系失败
     */
    LIST_SHARE_USERS_ERR("share.list.users.error", 1045, true),
    /**
     * 列举共享给我的资源
     */
    LIST_SHARETO_ME("share.list.me", 46, false),
    /**
     * 列举共享给我的资源失败
     */
    LIST_SHARETO_ME_ERR("share.list.me.error", 1046, true),
    /**
     * 列举团队空间成员
     */
    LIST_TEAMSPACE_MEMBER("teamspace.member.list", 47, false),
    /**
     * 列举团队空间成员失败
     */
    LIST_TEAMSPACE_MEMBER_ERR("teamspace.member.list.error", 1047, true),
    /**
     * 列举回收站中的文件及文件夹
     */
    LIST_TRASH_ITEMS("trash.items.list", 48, false),
    /**
     * 列举回收站中的文件及文件夹失败
     */
    LIST_TRASH_ITEMS_ERR("trash.items.list.error", 1048, true),
    /**
     * 获取指定用戶的空间列表
     */
    LIST_USER_TESMSPACE("teamspace.list.user", 49, false),
    /**
     * 获取指定用戶的空间列表失败
     */
    LIST_USER_TESMSPACE_ERR("teamspace.list.user.error", 1049, true),
    /**
     * 列举文件的版本
     */
    LIST_VERSION_FILE("file.list.version", 50, false),
    /**
     * 列举文件的版本失败
     */
    LIST_VERSION_FILE_ERR("file.list.version.error", 1050, true),
    /**
     * 修改访问节点控制
     */
    MODIFY_NODE_ACL("node.modify.acl", 51, true),
    /**
     * 修改访问节点控制失败
     */
    MODIFY_NODE_ACL_ERR("node.modify.acl.error", 1051, true),
    /**
     * 修改团队空间
     */
    MODIFY_TEAMSPACE("teamspace.modify", 52, true),
    /**
     * 修改团队空间
     */
    MODIFY_TEAMSPACE_ERR("teamspace.modify.error", 1052, true),
    /**
     * 修改团队空间成员
     */
    MODIFY_TEAMSPACE_MEMBER("teamspace.member.modify", 53, true),
    /**
     * 修改团队空间成员失败
     */
    MODIFY_TEAMSPACE_MEMBER_ERR("teamspace.member.modify.error", 1053, true),
    /**
     * 异步移动
     */
    MOVE_ASYNC("async.move", 54, true),
    /**
     * 异步移动失败
     */
    MOVE_ASYNC_ERR("async.move.error", 1054, true),
    /**
     * 移动文件
     */
    MOVE_FILE("file.move", 55, true),
    /**
     * 移动文件失败
     */
    MOVE_FILE_ERR("file.move.error", 1055, true),
    /**
     * 移动文件夹
     */
    MOVE_FOLDER("folder.move", 56, true),
    /**
     * 移动文件夹失败
     */
    MOVE_FOLDER_ERR("folder.move.error", 1056, true),
    /**
     * 预上传文件（非闪传）
     */
    UPLOADURL_PROVIDE("file.prepare.upload.begin", 57, true),
    /**
     * 预上传文件（闪传）
     */
    UPLOAD_QUICK("file.prepare.upload.end", 157, true),
    /**
     * 预上传文件失败
     */
    UPLOADURL_PROVIDE_ERR("file.prepare.upload.error", 1057, true),
    /**
     * 刷新文件下载地址
     */
    REFRESH_UPLOAD_URL("file.refresh.upload", 58, false),
    /**
     * 刷新文件下载地址失败
     */
    REFRESH_UPLOAD_URL_ERR("file.refresh.upload.error", 1058, false),
    /**
     * 异步恢复回收站资源
     */
    RESTORE_TRASH_ASYNC("async.restore.trash", 59, true),
    /**
     * 异步恢复失败
     */
    RESTORE_TRASH_ASYNC_ERR("async.restore.trash.error", 1059, true),
    /**
     * 还原指定内容
     */
    RESTORE_TRASH_ITEM("trash.item.restore", 60, true),
    /**
     * 还原指定内容失败
     */
    RESTORE_TRASH_ITEM_ERR("trash.item.restore.error", 1060, true),
    /**
     * 根据名称搜索文件/文件夹
     */
    SEARCH_NODE_LISTS("node.search.lists", 61, false),
    /**
     * 根据名称搜索文件/文件夹失败
     */
    SEARCH_NODE_LISTS_ERR("node.search.lists.error", 1061, false),
    /**
     * 更新文件名
     */
    UPDATE_FILE_NAME("file.update.name.sync", 63, true),
    
    /**
     * 同步文件名
     */
    UPDATE_FILE_SYNC("file.update.sync", 63, true),
    
    /**
     * 同步更新文件名失败
     */
    UPDATE_FILE_NAME_SYNC_ERR("file.update.name.sync.error", 1063, true),
    
    /**
     * 重命名并设置同步状态
     */
    UPDATE_FOLDER_NAME("folder.update.name", 64, true),
    
    UPDATE_FOLDER_SYNC("folder.update.sync", 64, true),
    
    /**
     * 重命名并设置同步状态失败
     */
    UPDATE_FOLDER_NAME_SYNC_ERR("folder.update.name.sync.error", 1064, true),
    
    /**
     * 更新节点外链
     */
    UPDATE_INODE_LINK("link.inode.update", 65, true),
    
    /**
     * 更新节点外链
     */
    UPDATE_INODE_LINK_ERR("link.inode.update.error", 1065, true),
    
    /**
     * 获取用户信息
     */
    GET_USER("user.get", 68, false),
    
    /**
     * 获取用户信息失败
     */
    GET_USER_ERR("user.get.error", 1068, true),
    
    /**
     * 删除用户
     */
    DELETE_USER("user.delete", 69, true),
    /**
     * 删除用户失败
     */
    DELETE_USER_ERR("user.delete.error", 1069, true),
    
    /**
     * 创建用户
     */
    CREATE_USER("user.create", 67, true),
    
    /**
     * 创建用户失败
     */
    CREATE_USER_ERR("user.create.error", 1067, true),
    
    /**
     * 更新用户
     */
    UPDATE_USER("user.update", 71, true),
    
    /**
     * 更新用户失败
     */
    UPDATE_USER_ERR("user.update.error", 1071, true),
    
    /**
     * 获取用户详情
     */
    GET_USER_POST("user.get.post", 70, false),
    
    /**
     * 获取用户详情
     */
    GET_USER_POST_ERR("user.get.post.error", 1070, true),
    
    /**
     * 恢复文件版本
     */
    RESTORE_FILE_VERSION("file.version.restore", 72, true),
    
    /**
     * 恢复文件版本失败
     */
    RESTORE_FILE_VERSION_ERR("file.version.restore.error", 1072, true),
    
    /**
     * 创建群组
     */
    CREATE_GROUP("group.create", 73, true),
    
    /**
     * 创建群组失败
     */
    CREATE_GROUP_ERR("group.create.error", 1073, true),
    
    /**
     * 删除群组
     */
    DELETE_GROUP("group.delete", 74, true),
    
    /**
     * 删除群组失败
     */
    DELETE_GROUP_ERR("group.delete.error", 1074, true),
    
    /**
     * 获取群组信息
     */
    GET_GROUP_INFO("group.get.info", 75, false),
    
    /**
     * 获取群组信息失败
     */
    GET_GROUP_INFO_ERR("group.get.info.error", 1075, true),
    
    /**
     * 修改群组信息
     */
    MODIFY_GROUP("group.modify", 76, true),
    
    /**
     * 修改群组信息失败
     */
    MODIFY_GROUP_ERR("group.modify.error", 1076, true),
    
    /**
     * 添加群组成员
     */
    ADD_GROUP_MEMBER("group.member.add", 77, true),
    
    /**
     * 添加群组成员失败
     */
    ADD_GROUP_MEMBER_ERR("group.member.add.error", 1077, true),
    
    /**
     * 删除群组成员
     */
    DELETE_GROUP_MEMBER("group.member.delete", 78, true),
    
    /**
     * 删除群组成员失败
     */
    DELETE_GROUP_MEMBER_ERR("group.member.delete.error", 1078, true),
    /**
     * 修改群组成员
     */
    MODIFY_GROUP_MEMBER("group.member.modify", 79, true),
    /**
     * 修改群组成员失败
     */
    MODIFY_GROUP_MEMBER_ERR("group.member.modify.error", 1079, true),
    LIST_GROUP_MEMBER("group.member.list", 92, false),
    LIST_GROUP_MEMBER_ERR("group.member.list.error", 1092,
        true),
    MIRROR_COPY_OBJECT_SUCCESS("mirror.copy.object.success", 122, false),
    MODIFY_ACCOUNT_CONFIGURATION("account.modify.success", 80, true),
    MODIFY_ACCOUNT_CONFIGURATION_ERR("account.modify.error", 1080, true),
    CREATE_ACCOUNT_CONFIGURATION("account.create.success", 81, true),
    CREATE_ACCOUNT_CONFIGURATION_ERR("account.create.error", 1081, true),
    SET_WATERMARK("set.watermark.success", 82, true),
    SET_WATERMARK_ERR("set.watermark.error", 1082, true),
    GET_ACCOUNT_INFO("get.account.info", 83, false),
    GET_ACCOUNT_INFO_ERR("get.account.info.error", 1083, true),
    // 添加收藏
    ADD_FAVORITE_NODE("favorite.add", 85, true),
    ADD_FAVORITE_NODE_ERR("favorite.add.error", 1085, true),
    // 刪除收藏
    DELETE_FAVORITE_NODE("favorite.delete", 86, true),
    DELETE_FAVORITE_NODE_ERR("favorite.delete.error", 1086, true),
    // 列举收藏
    LIST_FAVORITE_NODE("favorite.list", 87, false),
    LIST_FAVORITE_NODE_ERR("favorite.list.error", 1087, true),
    // 获取收藏
    GET_FAVORITE_NODE("favorite.get", 88, false),
    GET_FAVORITE_NODE_ERR("favorite.get.error", 1088, true),
    // 根据id获取重定向下载地址
    GET_DOWNLOAD_OBJ("download.get.obj", 89, true),
    GET_DOWNLOAD_OBJ_ERR("download.get.obj.error", 1089, true),
    // 設置對象屬性
    SET_ATTRIBURE_OBJ("attribute.set.obj", 90, true),
    SET_ATTRIBURE_OBJ_ERR("attribute.set.obj.error", 1090, true),
    // 設置預覽轉換失敗
    PREVIEW_CONVERT("preview.convert", 91, true),
    
    // 上傳預覽對象
    PRE_UPLOAD_PREVIEW("pre.upload.preview", 93, true),
    PRE_UPLOAD_PREVIEW_ERR("pre.upload.preview.error", 1093, true),
    GET_DIRECT_LINK("direct.link.get", 95, false),
    GET_DIRECT_LINK_ERR("direct.link.get.error", 1095, true),
    // 获取文件下载地址
    GET_FILE_DOWNLOAD_URL("file.get.download", 96, true),
    GET_FILE_DOWNLOAD_URL_ERR("file.get.download.error", 1096, true),
    LIST_UERlOG("userlog.list", 97, true),
    LIST_UERlOG_ERR("userlog.list.error", 1097, true),
    // 获取邮件信息
    GET_MAIL_MESSAGE("mail.get.message", 98, false),
    GET_MAIL_MESSAGE_ERR("mail.get.message.error", 1098, true),
    // 设置邮件信息
    SET_MAIL_MESSAGE("mail.set.message", 99, true),
    SET_MAIL_MESSAGE_ERR("mail.set.message.error", 1099, true),
    
    // 删除信息
    DELETE_MESSAGE("massage.delete", 100, true),
    DELETE_MESSAGE_ERR("massage.delete.error", 1100, true),
    // 获取消息通知地址
    GET_MESSAGE_ADDRESS("message.get.address", 101, false),
    GET_MESSAGE_ADDRESS_ERR("message.get.address.error", 1101, true),
    // 列举收到的消息
    LIST_MESSAGE_RECEIVE("message.receive.list", 102, false),
    LIST_MESSAGE_RECEIVE_ERR("message.receive.list.error", 1102, true),
    
    // 发布消息
    PUBLISH_MESSAGE("message.publish", 103, true),
    PUBLISH_MESSAGE_ERR("message.publish.error", 1103, true),
    // 更新消息
    UPDATE_MESSAGE("message.update", 104, true),
    UPDATE_MESSAGE_ERR("message.update.error", 1104, true),
    // 列举系统角色
    LIST_ROLE("role.list", 105, false),
    LIST_ROLE_ERR("role.list.error", 1105, true),
    
    // 获取团队空间属性
    GET_TEAMSPACE_ATTRIBUTE("teamspace.get.attribure", 106, false),
    GET_TEAMSPACE_ATTRIBUTE_ERR("teamspace.get.attribure.error", 1106, true),
    
    // 获取所有空间列表
    LIST_ALL_TEAMSPACE("teamspace.list.all", 107, false),
    LIST_ALL_TEAMSPACE_ERR("teamspace.list.all.error", 1107, true),
    
    // 设置团队空间属性
    SET_TEAMSPACE_ATTRIBUTE("teamspace.set.attribure", 108, true),
    SET_TEAMSPACE_ATTRIBUTE_ERR("teamspace.set.attribure.error", 1108, true),
    
    // 获取用户列表
    GET_USER_LIST("user.get.list", 109, false),
    GET_USER_LIST_ERR("user.get.list.error", 1109, true),
    
    // 设置用户属性
    SET_USER_ATTRIBUTE("user.attribute.set", 110, true),
    SET_USER_ATTRIBUTE_ERR("user.attribute.set.error", 1110, true),
    
    // 获取用户属性
    GET_USER_ATTRIBUTE("user.attribute.get", 111, false),
    GET_USER_ATTRIBUTE_ERR("user.attribute.get.error", 1111, true),
    
    // 获取系统并发性能历史统计数据
    GET_SYSTEM_CONCURRENCE_HISTORY("concurrence.system.history.get", 112, true),
    GET_SYSTEM_CONCURRENCE_HISTORY_ERR("concurrence.system.history.get.error", 1112, true),
    
    // 导出统计数据
    GET_STATISTIC_DATA("statistic.data.get", 113, true),
    GET_STATISTIC_DATA_ERR("statistic.data.get.error", 1113, true),
    
    // 获取逻辑文件当前统计数据
    GET_STATISTIC_NODE_CURRENT("statistic.node.current.get", 114, true),
    GET_STATISTIC_NODE_CURRENT_ERR("statistic.node.current.get.error", 1114, true),
    
    // 获取逻辑文件历史统计数据
    GET_STATISTIC_NODE_HISTORY("statistic.node.history.get", 115, true),
    GET_STATISTIC_NODE_HISTORY_ERR("statistic.node.history.get.error", 1115, true),
    // 获取物理文件当前统计数据
    GET_STATISTIC_OBJECT_CURRENT("statistic.object.current.get", 116, true),
    GET_STATISTIC_OBJECT_CURRENT_ERR("statistic.object.current.get.error", 1116, true),
    
    // 获取物理文件历史统计数据
    GET_STATISTIC_OBJECT_HISTORY("statistic.object.history.get", 117, true),
    GET_STATISTIC_OBJECT_HISTORY_ERR("statistic.object.history.get.error", 1117, true),
    
    // 获取应用账户统计信息
    GET_STATISTIC_INFO("static.info.get", 118, true),
    GET_STATISTIC_INFO_ERR("static.info.get.error", 1118, true),
    
    // 获取当前用户数统计数据
    GET_STATISTIC_USER_CURRENT("statistic.user.current.get", 119, true),
    GET_STATISTIC_USER_CURRENT_ERR("statistic.user.current.get.error", 1119, true),
    // 获取历史用户数统计数据
    GET_STATISTIC_USER_HISTORY("statistic.user.history.get", 120, true),
    GET_STATISTIC_USER_HISTORY_ERR("statistic.user.history.get.error", 1120, true),
    
    // 获取用户分段统计信息
    GET_STATISTIC_CLUSTER("statistic.cluster.get", 121, true),
    GET_STATISTIC_CLUSTER_ERR("statistic.cluster.get.error", 1121, true),
    
    // 獲取水印
    GET_WATERMARK_INFO("get.watermark.info", 84, false),
    GET_WATERMARK_INFO_ERR("get.watermark.info.error", 1084, true),
    
    // 创建迁移任务
    CREATE_USER_MIGRATION_SUCCESS("user.data.migration.create", 150, true),
    CREATE_USER_MIGRATION_ERR("user.data.migration.create.failed", 1150, true),
    
    // 获取迁移任务详情
    GET_USER_MIGRATION_SUCCESS("user.data.migration.get", 151, false),
    GET_USER_MIGRATION_ERR("user.data.migration.get.failed", 1151, true),
    
    // 删除迁移任务
    DELETE_USER_MIGRATION_SUCCESS("user.data.migration.delete", 152, true),
    DELETE_USER_MIGRATION_ERR("user.data.migration.delete.failed", 1152, true),
    /**
     * 通过thrift获取文件下载地址
     */
    DOWN_URL_FILE_THRIFT("file.down.url", 153, false),
	
    // 删除团队空间属性
//	DELETE_TEAMSPACE_ATTRIBUTE("teamspace.delete.attribute", 154, true),
//	DELETE_TEAMSPACE_ATTRIBUTE_ERR("teamspace.delete.attribute.failed", 1154, true),
	
	// 生成团队空间属性
//	CREATE_TEAMSPACE_ATTRIBUTE("teamspace.create.attribute", 155, true),
//
//	CREATE_TEAMSPACE_ATTRIBUTE_ERR("teamspace.create.attribute.failed", 1155, true),
	
	/**
	 * 获取预览文件图片url
	 */
	PREVIEW_URL("preview.url",156,true),
	
	/**
	 * 获取预览url图片地址失败
	 */
	PREVIEW_URL_ERROR("preview.url.error",1156,true),

    /**
     * 获取正转换的任务
     */
    GET_DOINGTASK_SUCCESS("doingTask.get", 157, true),
    /**
     * 获取正转换的任务失败
     */
    GET_DOINGTASK_ERR("doingTask.get.error", 1157, true),
    
    /**
     * 获取已转换的任务
     */
    GET_DONETASK_SUCCESS("doneTask.get", 158, true),
    /**
     * 获取已转换的任务失败
     */
    GET_DONETASK_ERR("doneTask.get.error", 1158, true),
    
    /**
     * 删除已转换的任务
     */
    DELETE_DONETASK_SUCCESS("doneTask.delete", 159, true),
    /**
     * 删除已转换的任务失败
     */
    DELETE_DONETASK_ERR("doneTask.delete.error", 1159, true),
    
    /**
     * 重试任务
     */
    RETRY_DONETASK_SUCCESS("retry.doing", 160, true),
    /**
     *  重试任务失败
     */
    RETRY_DONETASK_ERR("retry.doing.error", 1160, true),
	
	 /**
     * 更新文件快捷访问状态
     */
    UPDATE_FOLDERTYPE("update.folderType", 161, true),
    /**
     *  更新文件快捷访问状态错误
     */
    UPDATE_FOLDERTYPE_ERR("update.folderType.err", 1161, true),
    
	 /**
     * 创建审批
     */
    CREATE_APPROVE("CREATE.APPROVE", 162, true),
    /**
     *  创建审批失败
     */
    CREATE_APPROVE_ERR("CREATE.APPROVE.error", 1162, true);
    
    private static final String USR_LOG_FILE = "userLog";
    static
    {
        BundleUtil.addBundle(USR_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(USR_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.ENGLISH);
    }
    
    public static UserLogType build(int typeCode)
    {
        UserLogType[] allType = UserLogType.values();
        for (UserLogType tmpType : allType)
        {
            if (tmpType.getTypeCode() == typeCode)
            {
                return tmpType;
            }
        }
        return null;
    }
    
    private String modelName;
    
    private boolean enable;
    
    public boolean isEnable()
    {
        return enable;
    }
    
    private int value;
    
    UserLogType(String modelName, int value, boolean enable)
    {
        this.modelName = modelName;
        this.value = value;
        this.enable = enable;
    }
    
    UserLogType(String modelName, int value)
    {
        this.modelName = modelName;
        this.value = value;
    }
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(USR_LOG_FILE, LogListener.getLanguage(), this.modelName, params);
    }
    
    public byte getLevel()
    {
        if (this.value > 1000)
        {
            return 1;
        }
        return 0;
    }
    
    public int getTypeCode()
    {
        return this.value;
    }
    
}
