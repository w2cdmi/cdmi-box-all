<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${title}</title>
<style type="text/css">
html, body{ background:#eee; font-family: 'Microsoft Yahei', '微软雅黑', STHeiti, '宋体';}
.logo{ height:10px; no-repeat center bottom;}
h1{ margin:0; padding:100px 0 0; text-align:center; font-size:32px; font-weight:normal; color:#666;}
.btn-con{ text-align:center; padding-top:48px; padding-bottom:40px;}
.btn-con a{
  display: inline-block;
  *display: inline;
  width: 340px;
  padding: 18px 0;
  margin: 0 auto;
  font-size: 32px;
  line-height: 36px;
  font-family: 'Microsoft Yahei', '微软雅黑', STHeiti, '宋体';
  color: #fff;
  text-align: center;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.35);
  vertical-align: middle;
  background-color: #3890d8;
  *background-color: #3890d8;
  background-image: -moz-linear-gradient(top, #40a3f4, #3282c4);
  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#40a3f4), to(#3282c4));
  background-image: -webkit-linear-gradient(top, #40a3f4, #3282c4);
  background-image: -o-linear-gradient(top, #40a3f4, #3282c4);
  background-image: linear-gradient(to bottom, #40a3f4, #3282c4);
  background-repeat: repeat-x;
  border:0 none;
  -webkit-border-radius: 5px;
     -moz-border-radius: 5px;
          border-radius: 5px;
  *zoom: 1;
  -webkit-box-shadow: inset 0 2px 5px rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.35);
     -moz-box-shadow: inset 0 2px 5px rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.35);
          box-shadow: inset 0 2px 5px rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.35);
}
.btn-con a, .btn-con a:hover{ text-decoration:none;}
p{ margin:0; padding: 0; text-align:center; font-size:24px; color:#666; line-height:48px;}
p.lang{ padding-top:30px;}
#zh{ display:none;}
</style>
<script type="text/javascript">
function switchLang(code){
	if(code == "zh"){
		document.getElementById("en").style.display = "none";
		document.getElementById("zh").style.display = "block";
	}else{
		document.getElementById("zh").style.display = "none";
		document.getElementById("en").style.display = "block";
	}
}
</script>
</head>

<body>
<div class="logo"></div>
<div id="zh">
	<h1>Android 客户端</h1>
	<div class="btn-con"><a href="${androidDownloadUrl}">立即下载</a></div>
	<p>大小 : ${androidSize}</p>
	<p>版本 : ${androidVersion}</p>
	<p>适用系统 : ${androidSupportSys}</p>
	
	<p class="lang"><a onclick="switchLang('en')" href="#none">English</a></p>
</div>
<div id="en">
	<h1>Android client</h1>
	<div class="btn-con"><a href="${androidDownloadUrl}">Download Now</a></div>
	<p>Size : ${androidSize}</p>
	<p>Version : ${androidVersion}</p>
	<p>Applicable to : ${androidSupportSys}</p>
	
	<p class="lang"><a onclick="switchLang('zh')" href="#none">简体中文</a></p>
</div>
</body>
</html>