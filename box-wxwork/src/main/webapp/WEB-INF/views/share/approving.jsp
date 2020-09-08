<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="../common/include.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>正在审批</title>
<style type="text/css">
	.handle{
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
	}
	.handle-header{
		width: 100%;
		height: 2.4rem;
		margin-top: 4rem;
	}
	.handle-header-icon{
		float: left;
		margin-left: 2.75rem;
	}
	.handle-header-name{
		float: left;
		color: #333333;
		font-size: 0.8rem;
	}
	.handle-information{
		line-height: 1.2rem;
		color: #333333;
		font-size: 0.6rem;
		padding-left: 2.75rem;
	}
	.in-process{
		width: 100%;
		height: 4.35rem;
		position: relative;
		margin-top: 4rem;
	}
	.in-process .in-process-icon{
		width: 2.2rem;
		height: 2.2rem;
		position: absolute;
		top: 0;
		left: 50%;
		transform: translateX(-50%);
	}
	.in-process .in-process-icon img{
		width: 2.2rem;
		height: 2.2rem;
	}
	.in-process .in-process-text{
		width: 3.9rem;
		height: 1.5rem;
		border: 0.075rem solid #4f77ab;
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		color: #4f77ab;
		font-size: 0.6rem;
		line-height: 1.5rem;
		text-align: center;
	}
	.handle-tail{
		height: 1.6rem;
		width: 100%;
		position: absolute;
		bottom: 0;
	}
	.handle-tail .handle-tail-texts{
		height: 0.8rem;
		text-align: center;
		font-size: 0.5rem;
		color: #4f77ab;
		line-height: 0.8rem;
	}
</style>
</head>
<body>
	<div class="handle">
		<div class="handle-header">
			<div class="handle-header-icon"></div>
			<div class="handle-header-name"></div>
		</div>
		<div class="handle-information">
			<div class="information-size"></div> <!--文件大小：-->
			<div class="information-owner"></div><!--分享者：-->
			<div class="information-time"></div><!--分享时间：-->
		</div>
		<div class="in-process">
			<div class="in-process-icon"><img src="${ctx}/static/skins/default/img/In-process .png"/></div>
			<div class="in-process-text">审批中....</div>
		</div>
		<div class="handle-tail">
			<div class="handle-tail-text handle-tail-texts"><spring:message code='main.title'/></div>
			<div class="handle-tail-link handle-tail-texts"><spring:message code='corpright'/></div>
		</div>
	</div>
</body>
</html>