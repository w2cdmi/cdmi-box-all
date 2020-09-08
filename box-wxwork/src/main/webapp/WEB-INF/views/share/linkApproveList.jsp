<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>审批</title>
	<%@ include file="../common/include.jsp" %>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkApproveList.css"/>
</head>
<body>
<div class="box">
	<div class="load">
		<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
		<div class="load-text">正在加载</div>
	</div>
	<div class="careful-header">
		<ul>
			<li class="tab-active" id="1">
				<i><img src="${ctx}/static/skins/default/img/careful-header-left.png"/></i>
				<span>待我审批</span>
			</li>
			<li class="tab" id="2">
				<i><img src="${ctx}/static/skins/default/img/careful-header-right.png"/></i>
				<span>我已审批</span>
			</li>
			<li class="tab" id="3">
				<i><img src="${ctx}/static/skins/default/img/careful-header-middle.png"/></i>
				<span>抄送我的</span>
			</li>
		</ul>
	</div>
	<div id="blank"></div>
	
	<a id="downloadFile" download style="display:none"></a>
	<div class="fs-view-file-list" id="fileListWrapper"
			style="background-size: 5rem 5rem; top: 5.2rem">
			<div class="weui-pull-to-refresh__layer">
				<div class="weui-pull-to-refresh__preloader"></div>
				<div class="up" style="text-align: center;">释放刷新</div>
				<div class="refresh" style="text-align: center;">正在刷新</div>
			</div>

			<div id="careful-content">
				<ul id="linkList"></ul>
			</div>
		</div>
    <%@ include file="../common/footer3.jsp" %>
</div>
<script id="linkTemplate" type="text/template7">
    <li class="line-scroll-wrapper" id="link_{{linkCode}}" onclick="gotoLinkApproveDetail(this)">
        <div class="line-content">
            <i class="{{divClass}}"></i>
            <div class="careful-content-bottoms">
                <div class="careful-content-top">
                    <p>{{nodeName}}</p>
                </div>
                <div class="careful-content-bottom">
                    <p>{{linkOwnerName}}</p>
                    <h1>{{startTime}}</h1>
                    <h2></h2>
                    <h3>{{status}}</h3>
                </div>
            </div>
        </div>
        <div class="line-buttons">
			{{#js_compare "this.type==1"}}
            	<div class="line-button line-button-detail" onclick="downloadFile({{nodeId}},{{linkOwner}})">下载</div>
			{{/js_compare}}
            {{#js_compare "this.status=='未审批'"}}
            <div class="line-button line-button-delete">详情</div>
            {{/js_compare}}
        </div>
    </li>
</script>

<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>

<script type="text/javascript">
    $(function() {
        //增加切换效果
        $(".careful-header li").on("click", function(e) {
            var $li = $(e.currentTarget);
            $li.parent().find(".tab-active").addClass("tab");
            $li.parent().find(".tab-active").removeClass("tab-active");
            $li.removeClass("tab")
            $li.addClass("tab-active")
            listLink($li.attr("id"));
        });


        //初始化第一个tab
		listLink(1, 1);
    });
    var currentTab = 1;
	var currentPage = 1;
    var __loadmore = false;
    var __loading = false;

    /*审批中[1]、审批完成[2], 驳回[3]*/
    function listLink(tab, page) {
        currentTab = tab || 1;
        currentPage = page || 1;
        var params;
        var url=host+"/ufm/api/v2/links/approve/items";
        if(currentTab == 1) {
            params = {
                limit: pageSize,
                offset: (currentPage-1)*pageSize,
                "accountId": "${accountId}",
                "type": "1",
                "status": 1,

            }
        } else if(currentTab == 2) {
        	 params = {
                    limit: pageSize,
                    offset: (currentPage-1)*pageSize,
                     "accountId": "${accountId}",
                     "approveBy": "${cloudId}",
                     "status": 2
              }
        } else if(currentTab == 3) {
            params = {
                    limit: pageSize,
                    offset: (currentPage-1)*pageSize,
            		"accountId": "${accountId}",
                    "type": "2",
                    "status": 1,
                }
        }else {
            $("#linkList").children().remove();
            $('#careful-content').addClass('careful-content-background3');
            return;
        }
        
        $.ajax({
            type: "POST",
            data: JSON.stringify(params),
            async: false,
            url: url,
            error: handleError,
            success: function (data) {
                var linkList = data.linkApproveList;
                currentPage = page;

                __loadmore = data.totalCount > currentPage * pageSize;
                var $list = $("#linkList");
                var $template = $("#linkTemplate");

                //加载第一页，清除以前的记录
                if(currentPage == 1) {
                    $list.children().remove();
                }
                $('#careful-content').removeClass();
                $('#careful-content').addClass('careful-content-background'+currentTab);
                if(linkList.length>0){
                	$('#careful-content').removeClass('careful-content-background'+currentTab);
                }
                for (var i in linkList) {
                    var item = linkList[i];
//                    item.size = formatFileSize(item.size);
                    item.status = translateStatus(item.status);
                    item.startTime = getFormatDate(new Date(item.startTime), "yyyy/MM/dd");
                    item.divClass = getImgHtml(item.type, item.nodeName);
                    $template.template(item).appendTo($list);

                    //设置数据
                    var $row = $("#link_" + item.linkCode);
                    $row.data("node", item);
                }
                

                //增加左滑显示按钮效果
                $(".line-scroll-wrapper").addLineScrollAnimate();

            },complete:function(){
            	$('.load').css('display','none');
            }
        });
    }

    function translateStatus(status) {
        switch (status || 0) {
            case 2: return "已审批";
            case 3: return "已驳回";
            default : return "未审批";
        }
    }

    function gotoLinkApproveDetail(e) {
        var node = $(e).data("node");
        
        if(currentTab == 1) {
            gotoPage("${ctx}/share/approveLinkDetail/" + node.linkCode+"?user=approveBy");
        } else if(currentTab == 2) {
            gotoPage("${ctx}/share/myLinkApproveDetail/" + node.linkCode);
        } else if(currentTab == 3) {
        	gotoPage("${ctx}/share/approveLinkDetail/" + node.linkCode+"?user=duplicateTo");
        } 
    }

	function downloadFile(nodeId,linkOwner) {
	    $.ajax({
	        type: "GET",
	        async: false,
	        url: ctx + "/files/getDownloadUrl/" + linkOwner + "/" + nodeId + "?" + Math.random(),
	        error: handleError,
	        success: function (data) {
	            $("#downloadFile").attr("href",data);
	            document.getElementById("downloadFile").click();
	        }
	    });
	    window.event.stopPropagation();
	}
	
	
    //下拉刷新
    var $listWrapper = $("#fileListWrapper");
    $listWrapper.pullToRefresh().on("pull-to-refresh", function() {
        //console.log("pulltorefresh triggered...");
        listLink(currentTab, currentPage);
        setTimeout(function() {
            $("#fileListWrapper").pullToRefreshDone();
        }, 200);
    });

    //上滑加载
    $listWrapper.infinite().on("infinite", function() {
        if(__loading) return;

        if(__loadmore) {
            __loading = true;
            $.showLoading();
            listLink(currentTab, ++currentPage);
            setTimeout(function() {
                __loading = false;
                $.hideLoading();
            }, 200);
        }
    });
    
</script>
</body>
</html>
