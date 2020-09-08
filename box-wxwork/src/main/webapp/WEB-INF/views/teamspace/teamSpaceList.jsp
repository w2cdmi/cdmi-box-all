<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>

	<head>
		<title>协作空间</title>
		<%@ include file="../common/include.jsp"%>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inviteShare.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/teamSpaceList.css" />
		<script src="${ctx}/static/js/teamspace/teamSpaceList.js"></script>
	</head>

	<body ontouchstart style="overflow: hidden;">
		<div class="box">
			<div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div>
			<%--<div class="file-view-toolbar">--%>
                <%--<span style="font-size: 0.75rem;color: #999;display: inline-block;line-height: 1.7rem;margin-left: 0.6rem"></span>--%>
				<%--<div class="new-folder-button pull-right" ></div>--%>
			<%--</div>--%>
            <div class="fillBackground"></div>
            <div class="not-space">

            </div>
			<div class="space-list-view" id="teamSpaceList"></div>

		</div>
		<%--新建空间--%>
		<div class="add-new-space" onclick="openCreateTeam()">
			<p class="add-new-space-title">新建空间</p>
		</div>
		<%--移交空间人员--%>
		<div class="add-leaguer" style="display: none;"  id="addLeaguer">
			<div class="add-leaguer-nav">
				<ul>
					<li onclick="showAddressListChooser()">
						<i><img src="${ctx}/static/skins/default/img/add-leaguer-group.png"/></i>
						<span>企业通讯录</span>
						<p><img src="${ctx}/static/skins/default/img/putting-more.png"/></p>
					</li>
				</ul>
			</div>
		 	<%--<div class="add-leaguer-tail">--%>
				<%--<ul>--%>
					<%--<li>--%>
						<%--<h1>最近联系的人</h1>--%>
					<%--</li>--%>
				<%--</ul>--%>
			<%--</div> --%>
		</div>
		
		<!---->
		<div class="staff-picker" style="display: none; padding-top: 2.85rem; width: 100%; height: 100%; background: #FFFFFF;" id="staffPicker">
		<!--<div>
				<spring:message code='teamSpace.changeOwer.content' />
			</div>-->
			<div class="staff-picker-prompt">空间将移交给</div>
			<div id="selectedMemeber"></div>
			<div class="staff-picker-remarks-brother">
				<p>移交后您将自动降级为空间管理员</p>
			</div>
			<div class="re-seletion-father">
				<input id="re-seletion" onclick="reseletion()" type="button"  value="重新选择"/>
			</div>

			<div class="staff-picker-member-operation">
				<div onclick="submitChangeOwner()"> 确定 </div>
			</div>
			<a href="javascript:" id="searchClose" onclick="closeStaffChooser()">关闭</a>
		</div>
		
		<!---->
		<div class="share-address-list" style="display: none;position:relative;height:100%" id="teamSpaceAddressList">
			<div class="share-address-content" style="position: absolute;top: 0;bottom: 0;overflow-y: auto;bottom: 3rem;background: #f5f5f5">
				<input type="hidden" id="parentDeptId" value="0"/>
				<div class="return-father" onclick="backward()" style="background: #fff">
					<div class="historyBack-return">返回</div>
					<b>|</b>
					<span id="department"></span>
				</div>
				<ul id="shareList" style="background: #fff">
					
				</ul>
			</div>
		</div>
		<%--空间列表模板--%>
		<script id="spaceTemplate" type="text/template7">
			<%--<div class="space-row" >--%>
				<%--<div class="space-row-son" id="space_{{id}}" onclick="gotoTeamSpace({{id}})">--%>
					<%--<div class="team-icon">--%>
						<%--<img src="${ctx}/static/skins/default/img/space-row-icon.png" />--%>
					<%--</div>--%>
					<%--<div class="team-info">--%>
						<%--<div class="team-name">--%>
							<%--<span>{{name}}</span>--%>
							<%--<!--<span>({{curNumbers}})</span>-->--%>
						<%--</div>--%>
						<%--<div class="space-list">--%>
							<%--<i>拥有者：{{ownedByUserName}}</i>--%>
							<%--<!--<p>2017/08/25<span>12:25</span></p>-->--%>
							<%--<p>成员数：<span>{{curNumbers}}</span>--%>
						<%--</div>--%>
					<%--</div>--%>
				<%--</div>--%>
			<%--</div>--%>
            <div class="weui-cell weui-cell_swiped">
                <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="gotoTeamSpace({{id}})">
                    <div class="weui-cell weui-cell-change">
                        <div class="weui-cell__bd" >
                            <div class="index-recent-left team-img">
                                <img src="${ctx}/static/skins/default/img/space-row-icon.png" />
                            </div>
                            <div class="index-recent-middle">
                                <div class="recent-detail-name">
                                    <p>{{name}}</p>
                                </div>
                                <div class="recent-detail-other">
                                    <span>拥有者：{{ownedByUserName}}</span>
                                    <span>|</span>
                                    <span>成员数：{{curNumbers}}</span>
                                    <span>|</span>
                                    <span>{{createdAt}}</span>
                                </div>
                            </div>
                            <div class="index-recent-right" id="space_{{id}}" >
                                <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
		</script>

		<%--员工列表模板--%>
		<script id="pickerMemberTemplate" type="text/template7">
			{{#js_compare "this.type == 'department'"}}
			<li onclick="showDepAndUsers({{userId}},'{{name}}')">
				<p><img src="${ctx}/static/skins/default/img/department-icon.png"></p>
				<span>{{name}}</span>
				<h1><img src="${ctx}/static/skins/default/img/putting-more2.png"></h1>
			</li>
			{{else}}
			<li onclick="setSelectedMember({{id}}, '{{alias}}')">
				<p><img src="${ctx}/userimage/getUserImage/{{id}}"></p>
				<span>{{alias}}</span>
			</li>
			{{/js_compare}}
		</script>

		<script type="text/javascript" src="${ctx}/static/js/common/line-scroll-animate.js"></script>

		<script type="text/javascript">

</script>
</body>

</html>