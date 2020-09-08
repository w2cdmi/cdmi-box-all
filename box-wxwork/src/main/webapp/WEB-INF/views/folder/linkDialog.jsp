<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
<div class="share-putting-out">
	<div class="putting-middle">
		<div class="putting-header">
			<img src="${ctx}/static/skins/default/img/icon/file-doc.png"/>
			<span>新项目适用研究方案.DAC</span>
		</div>
		<div class="putting-options">
			<div class="weui-cell__bd">高级选项</div>
			<div class="weui-cell__ft">
            	<input class="weui-switch" type="checkbox" />
        	</div>
        </div>
		<div class="putting-core">
			<ul>
				<li class="putting-access">
					<div>访问权限</div>
					<span></span>
					<i>下载</i>
					<span></span>
					<i>预览</i>
				</li>
				<li>
					<div class="weui-cell__bd">提取码</div>
					<div class="weui-cell__ft">
                    <input class="weui-switch" type="checkbox"/>
				</li>
				<li class="putting-static">
					<div>静态码</div>
					<span></span>
					<i>1234</i>
					<p></p>
				</li>
				<li class="putting-trends">
					<div>动态码</div>
					<span></span>
					<p>
						<input type="text" id="" placeholder="请输入邮箱或手机号"/>
					</p>
				</li>
				<li>
					<div class="weui-cell__bd">有效期</div>
					<div class="weui-cell__ft">
                    <input class="weui-switch" type="checkbox"/>
				</li>
				<li class="putting-term">
					<input type="text" placeholder="请输入有效期"/>
				</li>
			</ul>
		</div>
		<div class="putting-relady">
			<img src="${ctx}/static/skins/default/img/putting-QQ.png"/>
			<img src="${ctx}/static/skins/default/img/putting-WB.png"/>
			<img src="${ctx}/static/skins/default/img/putting-WX.png"/>
		</div>
		<div class="putting-tail">
			<div>取消</div>
			<div>外发</div>
		</div>
	</div>
</div>

</body>
</html>