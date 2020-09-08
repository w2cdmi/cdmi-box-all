<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/price.css "/>
</head>
<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<%
    request.setAttribute("page",2) ;
%>
<jsp:include page="include/menu.jsp"></jsp:include>
<div id="layer-1" style="height: 340px;width: 100%;">
    <div style="background: url(${pageContext.request.contextPath}/static/assets/images/bg1_02.png);background-size:cover;width: 100%;height: 340px">
        <div style="width: 1280px;margin: 0 auto;padding-top: 1px;">
            <h1 style="width: 100%;font-size: 18px;font-weight: 200;color: #4e4e4e;margin-top:180px;text-align:center;top:47%;">企业文件宝，做我能做的，给你我有的。</h1>
        </div>
    </div>
</div>
<div id="container" class="container">

    <div id="layer-2">
        <h1>会员套餐</h1>
        <h3>文件宝提供三种会员套餐，可根据团队规模随时调整定制</h3>
        <div  class="cl" style="width:1050px;height:750px;margin: 0 auto ">
            <span class="box" style="float: left;width: 325px;height: 620px">
                <div class="header">
                    <h1>企业高级版</h1>
                    <h3>适合于初创／小型企业</h3>
                    <div class="line"></div>
                </div>
                <div class="body">
                    <div class="desc">
                        <dl>
                            <dt class="cl"><label>容量</label><span>2G/人,10G共享容量</span></dt>
                            <dt class="cl"><label>账号</label><span>40员工账号，可扩展(价格为6元/人/月)</span></dt>
                            <dt class="cl"><label>特权</label><span>企业微信云盘</span></dt>
                            <dt class="cl"><label></label><span>个人微信云盘</span></dt>
                            <dt class="cl"><label></label><span>单文件历史版本50</span></dt>
                        </dl>
                    </div>
                    <div class="line"></div>
                    <h3>选择服务时长</h3>
                    <div class="cl">
                        <span class="pricebox" style="float: left;margin-left: 16px;" price="1620.00">6个月</span>
                        <span class="pricebox" style="float: left;margin-left: 36px;" price="2916.00">1年<i class="after"></i><u>9折</u></span>
                        <span class="pricebox" style="float: left;margin-left: 36px;" price="5184.00" >2年<i class="after"></i><u>8折</u></span>
                    </div>
                    <div class="price-count">
                        <label>合计</label>
                        <span></span>
                    </div>
                    <button class="ply-button">立即支付</button>
                </div>
            </span>
            <span class="box" style="float: left;width: 325px;height: 620px;margin-left: 25px">
                <div class="header">
                    <h1>企业专业版</h1>
                    <h3>适合于小型／中型企业</h3>
                    <div class="line"></div>
                </div>
                <div class="body">
                    <div class="desc">
                        <dl>
                            <dt class="cl"><label>容量</label><span>10G/人,50G共享容量</span></dt>
                            <dt class="cl"><label>账号</label><span>80个账号，可扩展(价格为30元/人/月)</span></dt>
                            <dt class="cl"><label>特权</label><span>企业微信云盘</span></dt>
                            <dt class="cl"><label></label><span>个人微信云盘</span></dt>
                            <dt class="cl"><label></label><span>单文件历史版本100</span></dt>
                            <dt class="cl"><label></label><span>可调整用户配额</span></dt>
                        </dl>
                    </div>
                    <div class="line"></div>
                    <h3>选择服务时长</h3>
                    <div class="cl">
                       <span class="pricebox" style="float: left;margin-left: 16px;" price="15300.00">6个月</span>
                        <span class="pricebox" style="float: left;margin-left: 36px;" price="27540.00">1年<i class="after"></i><u>9折</u></span>
                        <span class="pricebox" style="float: left;margin-left: 36px;" price="48960.00" >2年<i class="after"></i><u>8折</u></span>
                    </div>
                    <div class="price-count">
                        <label>合计</label>
                        <span></span>
                    </div>
                    <button class="ply-button">立即支付</button>
                </div>
            </span>
            <span class="box" style="float: left;width: 325px;height: 620px;margin-left: 25px">
            <div class="header">
                <h1>企业旗舰版</h1>
                <h3>适合于中型／大型公司</h3>
                <div class="line"></div>
            </div>
            <div class="body">
                <div class="desc">
                    <dl>
                        <dt class="cl"><label>容量</label><span>50G/每用户,100G共享容量</span></dt>
                        <dt class="cl"><label>账号</label><span>200个账号，可扩展(价格为150元/人/月)</span></dt>
                        <dt class="cl"><label>特权</label><span>企业微信云盘</span></dt>
                        <dt class="cl"><label></label><span>个人微信云盘</span></dt>
                        <dt class="cl"><label></label><span>单文件历史版本200</span></dt>
                        <dt class="cl"><label></label><span>可调整用户配额</span></dt>
                        <dt class="cl"><label></label><span>支持存储混合云模式</span></dt>
                        <dt class="cl"><label></label><span>企业形象自定义，开放API接口</span></dt>
                    </dl>
                </div>
                <div class="line"></div>
                <h3>选择服务时长</h3>
                <div class="cl">
                    <span class="pricebox" style="float: left;margin-left: 16px;" price="198000.00">6个月</span>
                    <span class="pricebox" style="float: left;margin-left: 36px;" price="356400.00">1年<i class="after"></i><u>9折</u></span>
                    <span class="pricebox" style="float: left;margin-left: 36px;" price="633600.00" >2年<i class="after"></i><u>8折</u></span>
                </div>
                <div class="price-count">
                    <label>合计</label>
                    <span></span>
                </div>
                <button class="ply-button">立即支付</button>
            </div>
            </span>
        </div>
    </div>
</div>
<div id="ad-3">
    <div>
        <p class="title">企业文件宝，做我能做的，给你我有的。</p>
        <span class="line"></span>
        <p></p>
<%--
        <a class="btn" href="/register/wxwork">立即体验</a>
--%>
    </div>
    <!-- <img src="./assets/images/index_10.png" /> -->
</div>

<%--<div id="ad-1" style="width:100%;overflow: hidden">
    <div  style="background: url(${pageContext.request.contextPath}/static/assets/images/index_10.png);width: 100%;height: 180px;background-size: cover"/>
</div>--%>
<jsp:include page="include/buttom.jsp"></jsp:include>
<script>
    $(document).ready(function(){
        $.fn.extend({
            priceCount:function () {
                var self=this;
                self.init=function () {
                    self.find(".pricebox").click(function () {
                        self.find(".pricebox").attr("class","pricebox");
                        $(this).attr("class","pricebox active");
                        self.priceCountText.text("¥"+$(this).attr("price"));
                    })
                    self.priceCountText=self.find(".price-count span");
                }
                self.active=function(index){
                    self.find(".pricebox:eq("+index+")").click();
                }
                return self;

            }
        });

        $("#layer-2 .box").each(function(){
            var pricecount=$(this).priceCount();
            pricecount.init();
            pricecount.active(1);
        });

        $("#layer-2 .ply-button").click(function () {
            window.location = "/enterprise";
        });
    })
</script>
</body>
</html>