<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <title>审计审批</title>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/notification/linkApproveList.css"/>
</head>
<body>
	<!--审计审批首页-->
    <div class="box">
        <div class="careful-header">
            <ul>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/careful-header-left.png"/></i>
                    <span>待我审批</span>
                </li>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/careful-header-middle.png"/></i>
                    <span>我提交的</span>
                </li>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/careful-header-right.png"/></i>
                    <span>抄送我的</span>
                </li>
            </ul>
        </div>
        <div class="careful-content" id="linkList">
            <ul>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></i>
                    <div class="careful-content-bottoms">
                        <div class="careful-content-top">
                            <span>研发部></span>
                            <p>欧阳</p>
                            <h1><img src="${ctx}/static/skins/default/img/putting-more.png"/></h1>
                            <h2>进入</h2>
                        </div>
                        <div class="careful-content-bottom">
                            <span><img src="${ctx}/static/skins/default/img/icon/file-doc.png"/></span>
                            <p>会议报告.PDF</p>
                            <h1>2017-08-25 21.05</h1>
                            <h2></h2>
                            <h3>30M</h3>
                        </div>
                    </div>
                </li>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></i>
                    <div class="careful-content-bottoms">
                        <div class="careful-content-top">
                            <span>研发部></span>
                            <p>欧阳</p>
                            <h1><img src="${ctx}/static/skins/default/img/putting-more.png"/></h1>
                            <h2>进入</h2>
                        </div>
                        <div class="careful-content-bottom">
                            <span><img src="${ctx}/static/skins/default/img/careful-division-icon.png"/></span>
                            <p>会议报告.PDF</p>
                            <h1>2017-08-25 21.05</h1>
                            <h2></h2>
                            <h3>30M</h3>
                        </div>
                    </div>
                </li>
                <li>
                    <i><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></i>
                    <div class="careful-content-bottoms">
                        <div class="careful-content-top">
                            <span>研发部></span>
                            <p>欧阳</p>
                            <h1><img src="${ctx}/static/skins/default/img/putting-more.png"/></h1>
                            <h2>进入</h2>
                        </div>
                        <div class="careful-content-bottom">
                            <span><img src="${ctx}/static/skins/default/img/icon/file-txt.png"/></span>
                            <p>会议报告.PDF</p>
                            <h1>2017-08-25 21.05</h1>
                            <h2></h2>
                            <h3>30M</h3>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

    </div>

    <script type="text/javascript">
        var token = '${token}';
        var currentPage = 1;
        var curTotalPage = 1;
        var params = {
            "pageNumber": currentPage,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "GET",
            data: params,
            url: "${ctx}/share/listLinkApprove",
            error: function (request) {
            },
            success: function (data) {
                curTotalPage = Math.ceil(data.totalCount / data.limit);
                var linkApproveList = data.linkApproveList;
                for (var i = 0; i < linkApproveList.length; i++) {
                    fillItem(linkApproveList[i]);
                }
            }
        });


        function fillItem(row) {
            var html = "";
            html = html + "<div class=\"weui-cell\" id='link-" + row.linkCode + "' >";
            html = html + "  <div class=\"weui-cell__bd\">";
            html = html + "     <p>" + row.nodeName + "->" + row.linkOwnerName + "</p>";
            html = html + "  </div>";
            html = html + "  <div class=\"weui-cell__ft\" onclick=\"gotoPage('${ctx}/share/linkApproveDetail/" + row.linkCode + "')\">查看详情</div>";
            html = html + "</div>";
            $("#approveListDiv").append(html);
            $("#link-" + row.linkCode).data('data', row);

        }


        function approvalLink(th) {
            console.debug(approve);
            /* $.ajax({
             type: "POST",
             data:{token:token,linkOwner:approve.linkOwner,nodeId:approve.nodeId,linkCode:approve.linkCode},
             url:"
            ${ctx}/share/approvalLink",
             error: function(request) {
             },
             success: function(data) {
             for(var i=0;i<data.length;i++){
             fillItem(data[i]);
             }

             }
             }); */
        }
    </script>
</body>
</html>