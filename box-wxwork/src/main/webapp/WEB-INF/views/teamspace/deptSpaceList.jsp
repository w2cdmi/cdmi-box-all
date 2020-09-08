<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>

	<head>
		<title>部门文件</title>
		<%@ include file="../common/include.jsp"%>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inviteShare.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/teamSpaceList.css" />
		<script src="${ctx}/static/js/teamspace/deptSpaceList.js"></script>
	</head>

	<body ontouchstart style="overflow: hidden;">
		<div class="box">
			<div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div>
            <div class="not-space">

            </div>
			<div class="space-list-view" id="teamSpaceList"></div>

		</div>
		<%@ include file="../common/footer.jsp"%>

		<%--部门空间列表模板--%>
		<script id="deptSpaceTemplate" type="text/template7">
            <div class="weui-cell weui-cell_swiped xx">
                <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="gotoTeamSpace({{id}})">
                    <div class="weui-cell weui-cell-change">
                        <div class="weui-cell__bd" >
                            <div class="index-recent-left team-img">
                                <img src="${ctx}/static/skins/default/img/department-icon.png" />
                            </div>
                            <div class="index-recent-middle">
                                <div class="recent-detail-name">
                                    <p>{{name}}</p>
                                </div>
                                <div class="recent-detail-other">
                                    <span>成员数：{{curNumbers}}</span>
                                </div>
                            </div>
                            <%--<div class="index-recent-right" id="space_{{id}}" >--%>
                                <%--<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>--%>
                            <%--</div>--%>
                        </div>
                    </div>
                </div>
            </div>
		</script>
		<script type="text/javascript" src="${ctx}/static/js/common/line-scroll-animate.js"></script>
</body>

</html>