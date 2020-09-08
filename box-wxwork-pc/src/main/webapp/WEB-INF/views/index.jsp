<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title><spring:message code='main.title'/></title>
    <%@ include file="common/include.jsp" %>
    <script src="${ctx}/static/js/index.js?v=${version}"></script>
</head>
<body>
<div class="container">
    <jsp:include page="common/menubar.jsp">
        <jsp:param name="activeId" value="index" />
    </jsp:include>
    <div id="toolbox" class="cl">
    </div>
    <div class="cl abslayout" style="bottom: 16px;top:180px;right: 0;left: 0">
        <div class="col12" style="height: 100%">
            <div class="panel" style="height: 100%">
                <div class="head">快捷目录</div>
                <div class="body" id="quickDirList">
                    <div class="abslayout" style="left:0;top:0;right: 0px;bottom: 0;overflow: auto;">
                        <dl class="cl" style="margin-right: 10px;margin-bottom:10px">
                        </dl>
                        <div class="notfind" style="width:252px;height:88px;display: none">
                            <a href="javascript:void(0)" id="add_foder">
                                <i class="fa fa-folder-o"></i>
                                <span>未设置快捷目录</span>
                            </a>
                            <p>
                                添加快捷目录可以让你方面的进入你关注的目录</br>
                                需要在所处的目录位置中点击“添加”
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col12" style="height: 100%;padding-right: 16px;">
            <div class="panel" style="height: 100%">
                <div class="head">最近浏览文件</div>
                <div class="body" id="recentFileList">
                    <dl class="listbox abslayout" id="datagrid" style="left:0;top:0;right: 0;bottom: 0;overflow: auto">

                    </dl>
                    <div class="notfind" style="width:72px;height:18px;display: none">
                        <p>暂无浏览文件</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="./common/video.jsp" %>
<a id="downloadFile" download style="display:none"></a>
</body>
</html>