package com.huawei.sharedrive.app.plugins.preview.util;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.favorite.domain.Node;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.favor.Param;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pw.cdmi.core.utils.JsonUtils;

import java.util.*;

@Service("previewFileUtil")
@Lazy(false)
public class PreviewFileUtil {
    private static Set<String> fileTypeSet = new HashSet<String>(BusinessConstants.INITIAL_CAPACITIES);

    static {
        fileTypeSet.add("doc");
        fileTypeSet.add("docx");

        fileTypeSet.add("xls");
        fileTypeSet.add("xlsx");

        fileTypeSet.add("ppt");
        fileTypeSet.add("pptx");

        fileTypeSet.add("pdf");
    }

    @Autowired
    private UserDAOV2 userDAO;

    @Autowired
    private AccountService accountService;

    /**
     * 判断INode是否可以预览
     *
     * @param node
     * @return
     */
    public boolean isPreviewable(INode node) {
        if (!isPreviewEnable(node)) {
            return false;
        }
        return isSupportPreview(node);
    }

    /**
     * 判断FavoriteNode是否可以预览
     *
     * @param favoriteNode
     * @return
     */
    public boolean isPreviewable(FavoriteNode favoriteNode) {
        if (!isPreviewEnable(favoriteNode)) {
            return false;
        }
        return isSupportPreview(favoriteNode);
    }

    public boolean isPreviewable(INodeShare iNodeShare) {
        if (!isPreviewEnable(iNodeShare)) {
            return false;
        }
        return isSupportPreview(iNodeShare);
    }

    /**
     * 根据Inode所属account,判断是否允许预览
     *
     * @param node
     * @return
     */
    public boolean isPreviewEnable(INode node) {
        return isPreviewEnable(node.getOwnedBy());
    }

    /**
     * 根据FavoriteNode对应Inode所属account,判断是否允许预览
     *
     * @param favoriteNode
     * @return
     */
    public boolean isPreviewEnable(FavoriteNode favoriteNode) {
        Node node = favoriteNode.getNode();
        if (node == null) {
            return false;
        }
        if (node.getOwnedBy() == null) {
            return false;
        }
        return isPreviewEnable(node.getOwnedBy());
    }

    /**
     * 根据INodeShare所属account,判断是否允许预览
     *
     * @param favoriteNode
     * @return
     */
    public boolean isPreviewEnable(INodeShare iNodeShare) {
        return isPreviewEnable(iNodeShare.getOwnerId());
    }

    /**
     * 根据用户所属account,判断是否允许预览
     *
     * @param ownerId
     * @return
     */
    public boolean isPreviewEnable(long ownerId) {
        User ownedUser = userDAO.get(ownerId);
        if (ownedUser == null) {
            return false;
        }
        long accountId = ownedUser.getAccountId();
        Account account = accountService.getById(accountId);
        if (account == null) {
            return false;
        }
        return account.getFilePreviewable();
    }

    /**
     * 根据文件扩展名,判断是否支持预览
     *
     * @param fileNameSuffix
     * @return
     */
    public static boolean isSupportPreview(String fileNameSuffix) {
        if (fileTypeSet.contains(fileNameSuffix)) {
            return true;
        }
        return false;
    }

    /**
     * 根据文件扩展名,判断是否支持预览
     *
     * @param node
     * @return
     */
    public static boolean isSupportPreview(INode node) {
        String suffix = getFileNameSuffix(node);
        return isSupportPreview(suffix);
    }

    /**
     * 根据文件扩展名,判断是否支持预览
     *
     * @param favoriteNode
     * @return
     */
    public static boolean isSupportPreview(FavoriteNode favoriteNode) {
        String suffix = getFileNameSuffix(favoriteNode);
        return isSupportPreview(suffix);
    }

    /**
     * 根据文件扩展名,判断是否支持预览
     *
     * @param favoriteNode
     * @return
     */
    public static boolean isSupportPreview(INodeShare iNodeShare) {
        String suffix = getFileNameSuffix(iNodeShare);
        return isSupportPreview(suffix);
    }

    /**
     * 获取文件扩展名
     *
     * @param node
     * @return
     */
    public static String getFileNameSuffix(INode node) {
        if (FilesCommonUtils.isFolderType(node.getType())) {
            return "";
        }
        String fileName = node.getName();
        return getFileNameSuffix(fileName);
    }

    /**
     * 获取文件扩展名
     *
     * @param favoriteNode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getFileNameSuffix(FavoriteNode favoriteNode) {
        Node node = favoriteNode.getNode();
        if (node == null) {
            return "";
        }
        if (node.getType() == null) {
            return "";
        }
        if (node.getType() != Node.FILE_NUM) {
            return "";
        }
        if (StringUtils.isBlank(favoriteNode.getParams())) {
            return "";
        }
        List<Param> list = (List<Param>) JsonUtils.stringToList(favoriteNode.getParams(),
                List.class,
                Param.class);
        if (list == null) {
            return "";
        }
        Map<String, String> params = new HashMap<String, String>(list.size());
        for (Param param : list) {
            params.put(param.getName(), param.getValue());
        }
        String fileName = params.get(Param.Name.ORGINNAME.getName());
        return getFileNameSuffix(fileName);
    }

    /**
     * 获取文件扩展名
     *
     * @param node
     * @return
     */
    public static String getFileNameSuffix(INodeShare iNodeShare) {
        if (FilesCommonUtils.isFolderType(iNodeShare.getType())) {
            return "";
        }
        String fileName = iNodeShare.getName();
        return getFileNameSuffix(fileName);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return
     */
    public static String getFileNameSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastPos = fileName.lastIndexOf(".");
        if (-1 == lastPos) {
            return "";
        }
        if (lastPos == fileName.length() - 1) {
            return "";
        }
        String suffix = fileName.substring(lastPos + 1);
        return suffix;
    }

}
