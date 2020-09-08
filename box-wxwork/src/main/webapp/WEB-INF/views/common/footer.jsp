<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    <div class="tabbar">
        <ul>
            <li class="tabbar-item inactive1" onclick="gotoPage('${ctx}/')">
                <i></i>
                <a>首页</a>
            </li>
            <li class="tab-space inactive2" id="goToBussiness">
                <i></i>
                <a>文库</a>
            </li>
            <li class="add_operation" id="add_operation" onClick="footAddLayel(this)">
                <i class="add_operation_img"></i>
            </li>
            <li class="tab-message inactive3" onclick="gotoPage('${ctx}/notification')">
                <i></i>
                <a>发现</a>
            </li>
            <li class="tab-person inactive4" onclick="gotoPage('${ctx}/user/personal')">
                <i></i>
                <a>我的</a>
            </li>
        </ul>
    </div>
    <div class="add-layel" id="add_layel" style="display:none">
        <div class="add-content">
            <ul>
                <li class="add-folder" id="createFolderId">
                    <i><img src="${ctx}/static/skins/default/img/header-tape.png" /></i>
                    <p>新建文件夹</p>
                </li>
                <li class="add-uploader" onclick="clickUpload()">
                    <i><img src="${ctx}/static/skins/default/img/header-upload.png" /></i>
                    <p>上传</p>
                </li>
                <li class="add-photo" onclick="uploadPhoto()">
                    <i><img src="${ctx}/static/skins/default/img/header-photograph.png" /></i>
                    <p>拍照</p>
                </li>
            </ul>
            <div class="cancel-mask" id="cancel-mask" onClick="cancelLayel()">
                <i><img src="${ctx}/static/skins/default/img/cancel-add.png"/></i>
                <p></p>
            </div>
        </div>
    </div>
    <%@ include file="../common/uploader.jsp" %>
<script type="text/javascript">
    <%--查询权限, 没有权限的操作，显示禁用图标。--%>

    $(function() {
        //加载企业文库URL
        bindEnterpriseLibraryUrl()
    })
</script>

