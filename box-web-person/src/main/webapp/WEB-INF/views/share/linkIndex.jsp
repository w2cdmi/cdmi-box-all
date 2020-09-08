<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<%@page import="pw.cdmi.box.disk.utils.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>
<head>
	<link rel="stylesheet" href="https://at.alicdn.com/t/font_234130_nem7eskcrkpdgqfr.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkIndex.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/linkMain.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/default/magic-input.min.css"/>
	<script src="${ctx}/static/components/schedule.js?v=${version}"></script>
	<title>外发</title>
</head>
<script type="text/javascript">
	var folderId = "<c:out value='${folderId}'/>";
	var token = "<c:out value='${token}'/>";
	var ownerId = "<c:out value='${ownerId}'/>";
	var iNodeId = "<c:out value='${folderId}'/>";
	var userName = "<c:out value='${userName}'/>";
	var linkStatus = "<c:out value='${linkStatus}'/>";
	var isComplexCode = "<c:out value='${isComplexCode}'/>";
	var type = "<c:out value='${type}'/>";
	var imgPathUrl = "";
</script>
<!--外发首页-->
<div style="overflow-y: auto;">
<div class="share-home-page" style="width: 500px;" id="linkFileListDiv" style="display:none">
	<div class="share-homepage-header" style="overflow:hidden">
		<div class="share-homepage-top" style="float:left">
			<span><i id="linkFileImg" style="width: 32px;height: 32px;display: inline-block;"></i></span>
		</div>
		<div class="share-homepage-right" style="float:left">
			<div class="share-homepage-middle">
				<i style="font-style:normal;font-size: 14px;margin-left: 8px;line-height: 32px;white-space: nowrap;text-overflow: ellipsis;overflow: hidden;width: 450px;display: inline-block">${name}</i>
			</div>
			<%--<div class="share-homepage-bottom">--%>
				<%--<div class="share-homepage-bottom-size" id="nodeSize"></div>--%>
				<%--<span></span>--%>
				<%--<div class="share-homepage-bottom-time" id="nodeDate"></div>--%>
			<%--</div>--%>
		</div>
		<div class="share-homepage-header-icon"></div>
	</div>
	<!-- <div class="share-home-page-nav"><span>请选择一个外发链接</span></div> -->
	<div class="putting-link" id="linkDiv"></div>
	 <script id="linkTemplate" type="text/template7"> 
	     <div class="weui-flex div-border-bottom">
		      <div class="placeholder">
                 <div class="putting-link">
					 {{#js_compare "this.plainAccessCode != undefined"}}
						<div class="puttinglink-title" style="margin-top: 1.6rem;margin-left: 1rem;">
					 {{else}}
                      	<div class="puttinglink-title" style="margin-top: 1.1rem;margin-left: 1rem;">
					 {{/js_compare}}
		                <span style="margin:0rem"  name="selectSpan" value="{{id}}" id="{{id}}" url="{{url}}"></span>
                      </div>
                 </div>
		      </div>
		      <div class="weui-flex__item linkinfo-top-bottom"><div class="placeholder">
		         <%--链接列表模板--%>
							<div class="putting-link">
								<div class="puttinglink-title">
									<div class="all_url">{{url}}</div>
								</div>
								<div class="puttinglink-one" did="{{id}}">
									<div>
										{{#js_compare "this.accessCodeMode=='static'"}}
											{{#js_compare "this.plainAccessCode!=null"}}
												提取码访问
											{{else}}
												匿名访问
											{{/js_compare}}
										{{else}}
											动态码访问
										{{/js_compare}}
										<span>|</span>
									</div>
									<div>
										{{#js_compare "this.role== 'viewer'"}}
										预览 下载
										{{else}}
										预览
										{{/js_compare}}
										<span>|</span>
									</div>
									<div>
										{{#js_compare "this.expireAt != undefined && this.expireAt != ''"}}
										  {{expireString}}
										{{else}}
										       永久有效
										{{/js_compare}}
									</div>
									
								</div>
								{{#js_compare "this.plainAccessCode != undefined"}}
								<div class="puttinglink-two">
									<span>提取码</span>
									<span>{{plainAccessCode}}</span>
									<!-- <span id="plainAccessCode{{id}}" style="color:#18b4ed;">复制</span> -->
								</div>
								{{/js_compare}}
                                <div class="puttinglink-two">
                                    <span>创建时间：</span>
                                    <span>{{effectiveAt}}</span>
                                </div>
							</div>
	              
		          </div>
		      </div>
		      <div class="copyDiv">
				{{#js_compare "this.plainAccessCode != undefined"}}
					<div class="placeholder" style=""><span class="copy_btn" data-clipboard-text="链接:{{url}};提取码:{{plainAccessCode}}">复制</span></div>
			    {{else}}
                    <div class="placeholder" style=""><span class="copy_btn" data-clipboard-text="链接:{{url}}">复制</span></div>
			    {{/js_compare}}
			  </div>
		      <div class="deleteDiv">
				{{#js_compare "this.plainAccessCode != undefined"}}
					<div class="placeholder" style=""><span onclick="deleteLink(this)">删除</span></div>
			    {{else}}
                    <div class="placeholder" style=""><span onclick="deleteLink(this)">删除</span></div>
			    {{/js_compare}}
			  </div>
			  
         </div>
	
	    </script> 
	<div class="setting-tail">
		<div class="setting-bottom">
			<div class="setting-bottom-left"></div>
			<div class="setting-bottom-middle"><i class="weui-icon-info-circle"></i></div>
			<div class="setting-bottom-right"></div>
		</div>
	</div>
	<div class="putting-tail">

	</div>
	<div class="link_all_operate" style="overflow: hidden; text-align: right;">
		<div class="setting-delete" id="batchDeleteLink">删除所有</div>
		<div class="tail-add-links" id="gotoCreatePage">添加外链</div>
		
	</div>
	
</div>

<div id="addLinkFileDiv" style="display:none;width: 312px; margin-left: 20px;">
<div class="share-senior">
		<div class="share-senior-content">
			<div class="weui-cells_radio">
				<label class="weui-cell weui-check__label" for="staticMode">
					<div class="weui-cell__bd">
						<p>匿名访问&nbsp;&nbsp;&nbsp;</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check mgr mgr-success" name="accessCodeMode"
							id="staticMode" value="staticMode" checked="checked"> <span
							class="weui-icon-checked"></span>
					</div>
				</label> <label class="weui-cell weui-check__label" for="emailMode">

					<div class="weui-cell__bd">
						<p>动态码访问</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check mgr mgr-success" name="accessCodeMode"
							id="emailMode" value="emailMode"> <span
							class="weui-icon-checked"></span>
					</div>
				</label> <label class="weui-cell weui-check__label" for="randomMode">
					<div class="weui-cell__bd">
						<p>提取码访问</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check mgr mgr-success" name="accessCodeMode"
							id="randomMode" value="randomMode"> <span
							class="weui-icon-checked"></span>
					</div>
				</label>
				<li style="display: none" id="accessCodeDiv">
					<span>提取码：</span> 
					<i id="accessCode"></i>
					<p style="margin-left: 30px;" id="btn_copy"
						data-clipboard-target="#accessCode">复制</p>
					<p id="refreshAccessCode">刷新</p>
				</li>
			</div>
			<ul>
				<li>
					<div class="weui-cell__bd">允许下载</div> 
					<input hidden="hidden" id="download" type="checkbox" value="off" checked="checked" /> 
					<input class=" mgc mgc-success" type="checkbox" id="downloadSwitch"/>
				</li>
				<li style="vertical-align: middle;"><span>有效期：</span>
					<input
					class="weui-input" id="validityChoose" type="text" value="一周"
					style="width:100px;text-align: left; margin-top: -6px; line-height: 30px;margin-left: 7px;outline:none;border:none;pointer-events: none">
					<%--<span style="margin-left:42px;">永久</span>--%>
					<select id="select_time" style="margin-left: 58px;">
						<option value="一天">一天</option>
						<option value="一周">一周</option>
						<option value="一个月">一个月</option>
						<option value="永久有效">永久有效</option>
						<option>选择时间</option>
					</select>
					<div id='schedule-box' style="display:none" class="boxshaw">

					</div>
					 <input class="weui-input" id="chooseDate" type="hidden">
			    </li>
			</ul>
		</div>

	</div>
	<div class="form-control" style="text-align: right;margin-top: 16px; margin-right: 20px;">
	            <button type="button" id="cancel_button">取消</button>
	            <button type="button" id="ok_button">确定</button>
	        </div>
	<!-- <div class="button_sp_area share-senior-input" style="margin-left: 1%;">
		<a href="javascript:;" onclick="setLink()">确定</a> <a
			href="javascript:;" onclick="cancelCreateLink()">取消</a>
	</div> -->
</div>
</div>
<script type="text/javascript">
	$(function () {
		var name = "${name}";
		$("#linkFileImg").addClass(getImgHtml(type,name));
		imgPathUrl = $("#linkFileImg").css("background-image");
		imgPathUrl = imgPathUrl.substring(5,imgPathUrl.length-2);

	});
	function deleteLink(th) {
		$.Confirm("确认删除链接？",function (onOk) {
           var linkId=$(th).parent().parent().parent().find('span').attr("id");
					$.ajax({
						type: "DELETE",
						url:host + "/ufm/api/v2/links/${ownerId}/${folderId}" + "?linkCode=" + linkId,
						error: function(request) {
							console.log(request);
                    if(request.responseText == 'NoSuchLink' && request.status == 400) {
						$.Alert("链接已删除");
						$(th).parent().parent().parent().remove();
						$("#linkDiv").find("div").first().click();
                    }else{
                        $.Alert("删除失败");
                    }
						},
						success: function() {
							// $.Alert("删除成功");
							$(th).parent().parent().parent().remove();
							$("#linkDiv").find("div").first().click();
						}
					});
       });
		// $.Alert("你好");
		// console.log(userName)
		// var r = confirm("确认删除链接");
		// if (r==true){

		// 	} else{
		// 	}
	}
	function copyLinkss(){
		var linkCodes=getSelectSpan();
		var linkCodeArr=linkCodes.split(",");
		var urls="";
		for(var i=0;i<1;i++){
			if(linkCodeArr[i]!=""){
				urls=$("#"+linkCodeArr[i]).data("data").url;
			}
		}
		return urls;

	}
	function selectNumber(){
		var num = $("#validityChoose").val();   //获取input中输入的数字
		var numbers = $("#select_time").find("option"); //获取select下拉框的所有值
		for (var j = 1; j < numbers.length; j++) {
			if ($(numbers[j]).val() == num) {
			$(numbers[j]).attr("selected", "selected");
			}
		}
	}
	selectNumber()
$('#select_time').on('click',function(e){
		e.stopPropagation();
		if($(this).val()==="选择时间"){
			$('#schedule-box').css({"display":"block"})
			var mySchedule = new Schedule({
				el: '#schedule-box',
				//date: '2018-9-20',
				clickCb: function (y,m,d) {
				document.querySelector('#validityChoose').value = y+'-'+m+'-'+d
				},
				nextMonthCb: function (y,m,d) {
				document.querySelector('#validityChoose').value = y+'-'+m+'-'+d
				},
				nextYeayCb: function (y,m,d) {
				document.querySelector('#validityChoose').value = y+'-'+m+'-'+d
				},
				prevMonthCb: function (y,m,d) {
				document.querySelector('#validityChoose').value = y+'-'+m+'-'+d
				},
				prevYearCb: function (y,m,d) {
				document.querySelector('#validityChoose').value = y+'-'+m+'-'+d
				}
			});
		}else{
			$('#schedule-box').css({"display":"none"})
			$('#validityChoose').val($(this).val());
		}
		$('#schedule-box').click(function(e){
			e.stopPropagation()
            $(this).css({"display":"none"})
		})
		$('body').click(function(){
			var reg = /^[1-9]\d{3}-([1-9]|1[0-2])-([1-9]|[1-2][0-9]|3[0-1])$/;
			var regExp = new RegExp(reg);
			var num = $("#validityChoose").val();  //获取input中输入的数字
			if(regExp.test(num)){
				$('#select_time').val('选择时间')
			}else{
				$("#select_time").val(num)
			}
			$('#schedule-box').css({"display":"none"})
		})
	})
	
</script>
