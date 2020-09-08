<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title> 
<link href="${ctx}/static/selectJS/css/index.css" rel="Stylesheet" type='text/css' />
<script src="${ctx}/static/selectJS/js/jquery-2.1.4.min.js" type="text/javascript"></script>
<script src="${ctx}/static/selectJS/js/json2.js" type="text/javascript"></script>
<script src="${ctx}/static/selectJS/js/sen.js" type="text/javascript"></script>
<script src="${ctx}/static/selectJS/js/index.js" type="text/javascript"></script>

</head>
<body onload='ClientInit()'>   
  <div class="searchZone">
   <div class='blockStart'></div>
   <input type='text' class='searchInput' id='searchInput' value='请输入人员名称'/>
   <div class='searchBtn' id='searchBtn' ></div>
   <div class='blockEnd'></div>
  </div>  
  
  <div style="background-color:#17B4FF; height:3px;">
  </div>
  
  <div id = "peopleList"  class="peopleList" > 
  </div>  
  <div class="people">
   <div class="name" style="text-align:center; float:none;">
    <input id='ok' type="button" onclick="builePeopleList()" style="color:red;font-family:宋体;" value="确定" /> 
   </div>
  </div>
  
</body>
</html>