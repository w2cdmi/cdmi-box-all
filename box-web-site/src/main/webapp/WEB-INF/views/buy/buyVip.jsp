<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
	request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" />
<link href="${ctx}/static/skins/default/css/buy.css" rel="stylesheet" type="text/css" />

<style type="text/css">
 .modalheader{
    height: 48px;
    line-height: 40px;
    padding: 0 10px 0 10px;
    background-color: #f9f9f9;
    font-family: SourceHanSansCN-Regular;
    font-size: 16px;
    color: #666666;
 }
 
 .qrimage{
    width: 220px;
    height: 220px;
    border: solid 1px #eaeaea;
    margin: 0 auto;
    margin-top: 20px;
 }

</style>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>


 <script type="text/javascript">
    var token='${token}';
    var hasBuyProductId='${enterpriseVip.productId}';
    
    $(function(){
    	if(hasBuyProductId){
    		if(hasBuyProductId==1){
    			$("#hightVipBtn").text("延长服务");
    			$("#majorVipBtn").text("升级服务");
    			$("#ultimateVipBtn").text("升级服务");
    			
    		}else if(hasBuyProductId==2){
    			$("#majorVipBtn").text("延长服务");
    			$("#ultimateVipBtn").text("升级服务");
    		}else if(hasBuyProductId==3){
    			
    			$("#ultimateVipBtn").text("延长服务");
    	    }
    	}
    	
    })
    
	function selectHightVip(th,current,price) {
		$(".chooes-item").removeClass(current);
		$(th).removeClass("item-background");
		$(th).addClass(current);
		$("#hightTotal").text(price);
	}

	function selectMajorVip(th,current,price) {
		$(".chooes-item").removeClass(current);
		$(th).removeClass("item-background");
		$(th).addClass(current);
		$("#majorTotal").text(price);
	}

	function selectUltimateVip(th,current,price) {
		$(".chooes-item").removeClass(current);
		$(th).removeClass("item-background");
		$(th).addClass(current);
		$("#ultimateTotal").text(price);
	}
	
	function order(name,th){
		var productId=$(th).attr("productId");
		var type=-1;
		if(hasBuyProductId){
			if(hasBuyProductId==productId){
				type=2;
			}else{
				type=3;
			}
		}else{
			type=1;
		};
		
		if(productId<hasBuyProductId){
			handlePrompt("success",'你已经购买了更高级的套餐!');
			return;
		};
		
		$("#payDiv").css("display","block");
		$("#sucessDiv").css("display","none");
		$("#paymodal").modal('show');
		$("#paymodal").css("z-index","9999");
		
		var duration=0;
		$("div[name='"+name+"']").each(function(){
			if($(this).attr("class").indexOf("current")>-1){
				duration=$(this).attr("duration");
			}
		})
	
	   	$.ajax({
	            type: "POST",
	            url: "${ctx}/order/create?productId="+productId+"&duration="+duration+"&type="+type+"&token="+token,
	            error: function (request) {
	                handlePrompt("error", '<spring:message code="common.operationFailed" />');
	            },
	            success: function (data) {
	            	$("#payqrcode").attr("src","${ctx}/order/getPayCode?url="+data.url);
	            	 interval=setInterval('task("'+data.orderId+'")',5000);
	            }
         });
		
		 
	}
	


	function task(orderId){
		checkOrder(orderId);
	}

	function checkOrder(orderId){
		 var premater={
				 "token":token,
		 };
		 $.ajax({
	        type: "GET",
	        url: "${ctx}/order/getOrderStatus?orderId="+orderId,
	        data: premater,
	        error: function (request) {
	        },
	        success: function (data) {
	        	if(data==2||data==3){
	        		$("#payDiv").css("display","none");
	        		$("#sucessDiv").css("display","block");
	        		clearInterval(interval);
	        	}
	        }
	    });
	}

	
</script> 
</head>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="body">
		<div class="body-con clearfix">
			<div class="content">
				<div class="content-body">
					<div class="item">
						<div class="title">
						      <img alt="" src="${ctx}/static/skins/default/img/bulevip.png" style="margin-top: -10px;">
							  <span>企业高级版</span>
						</div>
						<div class="inner-content">
						    <div style="height: 280px;">
								<p class="mb25">
									容量：<span>2G/人,10G共享容量</span>
								</p>
								<p class="mb25">
									账号：<span>40员工账号，可扩展(价格为6元/人/月)</span>
								</p>
								<p>特权：个人微信云盘</p>
								<p class="mlp">企业微信云盘</p>
								<p class="mlp">单文件历史版本50</p>
							</div>
							 <div style="height: 135px" id="hightsal">
								<p class="times">选择服务时长</p>
								<div class="chooes hight-Vip">
									<div class="chooes-item item-background currentHigh" name="hightVip"  duration='6' onclick="selectHightVip(this,'currentHigh','￥1620.00')">6个月</div>
									<div class="chooes-item item-background mlc"  name="hightVip"  duration='12' onclick="selectHightVip(this,'currentHigh','￥2916.00')">
										1&nbsp;&nbsp;年<span class="inner-after"></span><span class="sale">9折</span>
									</div>
									<div class="chooes-item item-background mlc"  name="hightVip"   duration='24' onclick="selectHightVip(this,'currentHigh','￥5184.00')">
										2&nbsp;&nbsp;年<span class="inner-after"></span><span
											class="sale">8折</span>
									</div>
								
								</div>
							
								<div style="margin-top: 20px">
									   合计：<span style="color:#36B5EA" id="hightTotal">￥1620.00</span>
								</div>
							</div>
							<div class="paybnt" style="background: #36b5ea;" onclick="order('hightVip',this)" productId='1' id="hightVipBtn">立即购买</div>
						</div>
					</div>
					<div class="item ml">
						<div class="title titred">
						    <img alt="" src="${ctx}/static/skins/default/img/redvip.png" style="margin-top: -10px;">
							<span>企业专业版</span>
						</div>
						<div class="inner-content">
						   <div style="height: 280px;">
								<p class="mb25">
									容量：<span>10G/人,50G共享容量</span>
								</p>
	
								<p class="mb25">
									账号：<span>80个账号，可扩展(价格为30元/人/月)</span>
								</p>
	
								<p>特权：个人微信云盘</p>
								<p class="mlp">企业微信云盘</p>
								<p class="mlp">单文件历史版本100</p>
								<p class="mlp">可调整用户配额</p>
							</div>
						    <div style="height: 135px" id="majorsal">
								<p class="times">选择服务时长</p>
								<div class="chooes chooes-itemred">
									<div class="chooes-item currentMajor" name="major"    duration='6' onclick="selectMajorVip(this,'currentMajor','￥15300.00')">6个月</div>
									<div class="chooes-item mlc" name="major"    duration='12'onclick="selectMajorVip(this,'currentMajor','￥27540.00')">
										1&nbsp;&nbsp;年<span class="inner-after"></span><span class="sale">9折</span>
									</div>
									<div class="chooes-item mlc" name="major"    duration='24'onclick="selectMajorVip(this,'currentMajor','￥48960.00')">
										2&nbsp;&nbsp;年<span class="inner-after"></span><span
											class="sale">8折</span>
									</div>
								</div>
									<div style="margin-top: 20px">
										   合计：<span style="color:#36B5EA" id="majorTotal">￥15300.00</span>
									</div>
							</div>
							<div class="paybnt btnred"  style="background: #ea5036 ;" onclick="order('major',this)" productId='2' id="majorVipBtn">立即购买</div>
						</div>
					</div>
					<div class="item ml">
						<div class="title titgreen">
						    <img alt="" src="${ctx}/static/skins/default/img/purplevip.png" style="margin-top: -10px;">
							<span>企业旗舰版</span>
						</div>
						<div class="inner-content">
						    <div style="height: 280px;">
								<p class="mb25">
									容量：<span>50G/每用户,100G共享容量</span>
								</p>
	
								<p class="mb25">
									账号：<span>200个账号，可扩展(价格为150元/人/月)</span>
								</p>
								<p>特权：个人微信云盘</p>
								<p class="mlp">企业微信云盘</p>
								<p class="mlp">单文件历史版本200</p>
								<p class="mlp">可调整用户配额</p>
								<p class="mlp">支持存储混合云模式</p>
								<p class="mlp">企业形象自定义，开放API接口</p>
							</div>
							<div style="height: 135px" id="ultimatesal">
								<p class="times">选择服务时长</p>
								<div class="chooes chooes-itemgreen">
									<div class="chooes-item currentUltimate"  name="Ultimate"  duration='6' onclick="selectUltimateVip(this,'currentUltimate','198000.00')">6个月</div>
									<div class="chooes-item mlc"  name="Ultimate"  duration='12'onclick="selectUltimateVip(this,'currentUltimate','356400.00')">
										1&nbsp;&nbsp;年<span class="inner-after"></span><span class="sale">9折</span>
									</div>
									<div class="chooes-item mlc"  name="Ultimate"  duration='24' onclick="selectUltimateVip(this,'currentUltimate','633600.00')">
										2&nbsp;&nbsp;年<span class="inner-after"></span><span
											class="sale">8折</span>
									</div>
								</div>
								<div style="margin-top: 20px">
									   合计：<span style="color:#36B5EA" id="ultimateTotal">￥198000.00</span>
								</div>
							</div>
							<div class="paybnt btngreen"  style="background: #63cc80 ;" onclick="order('Ultimate',this)" productId='3' id="ultimateVipBtn">立即购买</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="paymodal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="left: 55%;width: 350px;height: 430px">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header" class="modalheader">
		                <h4 class="modal-title" id="myModalLabel">微信支付</h4>
		            </div>
		            <div class="modal-body"  id="paybody">
		            
		              <div id="payDiv" style="display: none">
			              <p style=" text-align: center" id="tail">请使用微信扫描二维码支付</p>
			              <div class="qrimage">
			                <img alt="" src="" id="payqrcode" style="width: 220px">
			              </div>
			              <p style=" text-align: center;margin-top: 30px" id="message"> 为避免失效，请在300s内完成支付</p>
		              </div>
		              <div id="sucessDiv">
		                  <div class="qrimage" style="width: 150px;border:  solid 0px #eaeaea;">
			                <img alt="" src="${ctx}/static/skins/default/img/success_03.png" style="width: 150px" >
			              </div>
			              <h1 style=" text-align: center"  onclick="order(this)">支付成功</h1>
		              </div>
		            </div>
		        </div><!-- /.modal-content -->
		    </div><!-- /.modal -->
		</div>
	</div>
	
	

</body>
</html>