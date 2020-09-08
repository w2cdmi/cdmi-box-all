<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<style type="text/css">
    .user-contacts-nav, .user-contacts-nav a {
        height: 30px;
        line-height: 30px;
        color: rgba(0, 0, 0, 0.45);
        -webkit-transition: color .3s;
        transition: color .3s;
    }

    .user-contacts-nav > ol,.user-contacts-nav > ol > dd {
        margin: 0;
        padding: 0;
        display: inline-block;
    }

    .user-contacts-nav a {
        display: inline-block;
        text-decoration: none;
    }

    .user-contacts-nav > a:after {
        padding: 0 10px 0 10px;
        content: '|';
        color: rgba(0, 0, 0, 0.45);
        -webkit-transition: color .3s;
        transition: color .3s;
    }

    .user-contacts-nav dd + dd:before {
        padding: 0 5px;
        color: #ccc;
        content: ">";
        display: inline-block;
    }

    .user-contacts-nav a:hover {
        text-decoration: none;
        color: #1890ff;
    }

    .user-contacts, .user-contacts-permission, .user-contacts-selected {
        list-style: none;
        margin: 0;
        padding: 0;
        border: 1px solid #d9d9d9;
    }

    .user-contacts, .user-contacts-selected {
        height: 300px;
        overflow: auto;
    }

    .user-contacts > li {
        padding: 2px 2px 2px 10px;
        border-bottom: 1px solid #e8e8e8;
        height:44px;
        line-height:44px;
    }

    .user-contacts > li > span, .user-contacts-selected > li > span {
        display: inline-block;
        color: rgba(0, 0, 0, 0.65);
        margin-left: 5px;
    }

    .user-contacts > li > img, .user-contacts-selected > li > img {
        height: 32px;
        width: 32px;
        border-radius: 50px;
        vertical-align:middle;
    }

    .user-contacts > li > img {
        margin-left: 5px;
    }

    .user-contacts-selected, .user-contacts-permission {
        float: left;
        width: 100%;
    }

    .user-contacts-selected > li, .user-contacts-permission > li {
        float: left;
        margin: 10px;
    }

    .user-contacts-selected > li {
        position: relative;
        padding: 5px;
        width: 185px;
        border:1px solid #ccc;
        margin:10px 0 0 10px;
    }

    .user-contacts-selected > li:hover, .user-contacts > li:hover {
        background: #f5f5f5;
        cursor: pointer;
    }

    .user-contacts-title {
        height:30px;
        line-height: 30px;
        font-weight: bold;
        color: rgba(0, 0, 0, 0.65);
        -webkit-transition: all .3s;
        transition: all .3s;
    }

    .user-contacts-selected a {
        font-size:12px;
        text-decoration: none;
        color:#999;
    }
    .user-contacts-selected a:hover {
        color: #ea5036;
    }

    .user-contacts-tools {
        display: inline-block;
        position: absolute;
        top:0;
        right: 5px;
    }
</style>

<div id="shareDialog" style="width:750px;display:none">
    <%--<div class="user-contacts-nav" id="deptAndUsersNavbar">--%>
        <%--&lt;%&ndash;<a href="javascript:void(0);">返回上级</a>--%>
        <%--<ol>--%>
            <%--<dd><a href="javascript:void(0);">华一云网</a></dd>--%>
            <%--<dd><span>研发部</span></dd>--%>
        <%--</ol>&ndash;%&gt;--%>
    <%--</div>--%>
    <div id="accessControlInput">



    </div>
    <div id="teamPersonList" style="display:none;width:750px;height:460px">
        <button id="secret" style="background-color:#ea5036;color:#fff;cursor:pointer;">
    机密、部分成员公开</button>
        <ul id="teamPersonLists" class="user-contacts" style="height: 400px;overflow-y: auto;margin-top:16px;">
            <%--<li>--%>
                <%--&lt;%&ndash;<img src="css/img/head-photo-default.png"/>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<span>刘文华</span>&ndash;%&gt;--%>
            <%--</li>--%>
        </ul>
    </div>
    <div id="addAccessControl" style="display:none;width:750px;height:490px">
        <button id="public" class="" style="background-color:#ea5036;color:#fff;cursor:pointer;">成员公开</button>
        <div style="clear:both;"></div>
        <div style="float:left;width: 300px;">
        <div class="user-contacts-title">成员列表</div>
            <ul class="user-contacts" id="deptAndUsersList">
            <%-- <li>
                 <input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle"/>
                 <img src="css/img/head-photo-default.png"/>
                 <span>刘文华</span>
             </li>
             <li>
                 <input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle"/>
                 <img src="css/img/head-photo-default.png"/>
                 <span>刘文华</span>
             </li>--%>
            </ul>
        </div>
        <div style="margin-left: 310px;">
        <div class="user-contacts-title">已添加的成员</div>
        <ul class="user-contacts-selected" style="margin-right: 10px;" id="sharedUserList">
        <%--<li>
            <img src="css/img/head-photo-default.png"/>
            <span>刘文华</span>
            <span class="user-contacts-tools">
                   <a href="javascript:void(0)">删除</a>
                   <a href="javascript:void(0)">修改</a>
               </span>
            <div style="font-size:12px;margin-top:5px;">
                <span>已共享</span>
                <span style="color:#ccc;">预览 | 下载 | 上传</span>
            </div>
        </li>--%>
        </ul>
        </div>
        <div style="clear: both"></div>
        <div class="user-contacts-title" style="margin-top:15px">权限设置</div>
        <ul class="user-contacts-permission" id="permissions">
        <li>
        <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" checked disabled/>&nbsp;预览</label>
        </li>
        <li>
        <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" checked id="downloader"/>&nbsp;下载</label>
        </li>
        <li>
        <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" checked id="uploader"/>&nbsp;上传</label>
        </li>
        </ul>
        <div style="clear: both"></div>
        <div class="form-control" style="text-align: right;margin-top:10px;">
        <button type="button" id="cancel_button">取消</button>
        <button type="button" id="ok_button">确定</button>
        </div>
    </div>

</div>

<div id="updateShareRoleDialog" style="display:none">
    <ul class="user-contacts-permission" style="margin-bottom: 20px">
        <li>
            <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" disabled id="previewer"/>&nbsp;预览</label>
        </li>
        <li>
            <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" id="downloader"/>&nbsp;下载</label>
        </li>
        <li>
            <label><input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle" id="uploader"/>&nbsp;上传</label>
        </li>
    </ul>
    <div class="form-control" style="text-align: right;margin-top:10px;">
        <button type="button" id="cancel_button">取消</button>
        <button type="button" id="ok_button">确定</button>
    </div>
</div>