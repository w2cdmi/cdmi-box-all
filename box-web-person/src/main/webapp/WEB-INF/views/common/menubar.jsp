<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <style>
                * {
                    text-decoration: none;
                    list-style: none;
                }

                img {
                    border: 0px;
                }

                ul,
                li,
                p,
                h1 {
                    padding: 0;
                    margin: 0;
                }

                .bt0 {
                    border-top: 0;
                }

                .bb0 {
                    border-bottom: 0 !important;
                }

                .find-middle {
                    width: 215px;
                    font-size: 0.6rem;
                    padding: 0;
                    margin: 0;
                }

                .find-middle li {
                    width: 100%;
                    height: 50px;
                    margin-top: 10px;
                    cursor: pointer;
                    list-style: none;
                    padding: 0 10px;
                }

                .find-img {
                    width: 40px;
                    height: 40px;
                    float: left;
                }

                .find-img img {
                    width: 100%;
                }

                .find-content {
                    float: left;
                    height: 1rem;
                    padding-left: 15px;
                }

                .find-content h1 {
                    font-size: 14px;
                    margin: 0;
                    color: #333333;
                    font-weight: 500;
                }

                .find-content p {
                    font-size: 12px;
                    color: #999999;
                    line-height: 20px;
                }

                .find-number {
                    float: right;
                    padding: 0rem 0.2rem;
                    background: #e53750;
                    border-radius: 0.2rem;
                    color: #FFFFFF;
                    position: absolute;
                    bottom: 0.5rem;
                    right: 0;
                }

                .per-header ul {
                    width: 100%;
                    height: 50px;
                    padding-bottom: 15px;
                }

                .per-header .per-header-portrait {
                    float: left;
                }

                .per-header p img {
                    float: left;
                    width: 50px;
                    height: 50px;
                }

                .per-header .per-name {
                    width: 140px;
                    height: 2rem;
                    float: left;
                    padding-left: 20px;
                    margin-top: -.7rem;
                }

                .per-header .per-name i {
                    display: inline-block;
                    font-style: normal;
                    font-size: 16px;
                    color: #333333;
                    margin-top: 31px;
                    font-weight: 600;
                }

                .per-header .per-name span {
                    display: inline-block;
                    width: 100%;
                    height: 1.2rem;
                    line-height: 1.2rem;
                    color: #333333;
                    font-size: 0.6rem;
                }

                .per-name p {
                    line-height: 30px;
                }

                .per-content {
                    width: 100%;
                    margin-right: 30px;
                }

                .per-content li {
                    width: 94%;
                    height: 41px;
                    /*margin-left: 5%;*/
                    margin: auto;
                    font-size: 14px;
                    color: #333333;
                    cursor: pointer;
                }

                .per-content li:hover {
                    background: #f9f9f9;
                }

                .per-content li:last-child {
                    border-bottom: 0;
                }

                .per-content li p {
                    float: left;
                }

                .per-content li p img {
                    width: 20px;
                    height: 20px;
                    margin-top: 0.2rem;
                }

                .per-content li span {
                    float: left;
                    margin-left: 15px;
                    line-height: 36px;
                }

                .per-content li i {
                    width: 0.3rem;
                    margin-top: 0.2rem;
                    float: right;
                }

                .per-content li i img {
                    width: 0.3rem;
                    height: 0.5rem;
                }

                .per-content li:nth-child(5) {
                    border-bottom: none;
                }

                .per-name p {
                    color: #333333;
                }

                .dialogs {}

                .userdialog {
                    position: absolute;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    z-index: 100;
                    display: none;
                }

                .userdialog .model {
                    position: absolute;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    background: #94948f;
                    opacity: 0.3;
                }

                .userdialog .dialogrect {
                    position: absolute;
                    background: #fff;
                    border: solid 1px #e3e3e3;
                    border-radius: 5px;
                    box-shadow: 2px 2px 5px #cecece;
                    margin-left: 16%;
                    margin-top: 14%;
                    width: 68%;
                }

                .userdialog .dialogrect .head {
                    line-height: 40px;
                    font-size: 14px;
                    border-bottom: solid 1px #e3e3e3;
                    padding-left: 16px;
                }

                .userdialog .dialogrect .head>#close {
                    position: absolute;
                    right: 10px;
                    top: 10px;
                    cursor: pointer;
                }

                .userdialog .dialogrect .head #close:hover {
                    color: #4875d6;
                }

                .userdialog .dialogrect .body {
                    width: 100%;
                    position: absolute;
                    left: 0;
                    background: #fff;
                }

                .content::-webkit-scrollbar {
                    width: 0px
                }

                .userdialog .dialogrect .body .ok-btn {
                    width: 100px;
                    background: rgb(215, 91, 65);
                    text-align: center;
                    height: 35px;
                    border-radius: 3px;
                    line-height: 35px;
                    float: right;
                    margin-right: 24px;
                    margin-top: 20px;
                    color: #fff;
                    cursor: pointer;
                    margin-bottom: 20px;
                }

                .userdialog .dialogrect .body .content {
                    width: 92%;
                    background: #F9F9F9;
                    height: 0;
                    margin: 0 auto;
                    margin-top: 30px;
                    border: 1px solid #ccc;
                    overflow-y: scroll;
                    padding-bottom: 30%;
                }

                .userdialog .body .content .title {
                    text-align: center;
                    font-size: 18px;
                    line-height: 3;
                }

                .userdialog .body .content .innertitle {
                    text-align: center;
                    font-size: 16px;
                    line-height: 1.5;
                    margin-top: 20px;
                }

                .userdialog .body .content div {
                    font-size: 16px;
                    padding: 15px 40px 0px 40px;
                    text-indent: 35px
                }

                /* **************************************** */

                .menubar {
                    float: left;
                    width: 240px;
                    height: 100%;
                    margin-right: -100%;
                    background: #F9F9F9;
                }

                .menubar li {
                    font-size: 14px;
                    color: #333333;
                    padding-left: 20px;
                    cursor: pointer;
                }

                .menubar li:hover {
                    background: #EAEAEA;
                }

                .menubar .hover {
                    background: #EAEAEA;
                }

                .menubar li {
                    height: 45px;
                    line-height: 45px;
                }

                .menubar li i {
                    width: 16px;
                    height: 16px;
                    display: inline-block;
                    margin-right: 11px;
                }

                .menubar li img {
                    width: 100%;
                    vertical-align: middle;
                    display: block;
                }

                .perpsomasg {
                    padding: 15px 0 20px 15px;
                    border-bottom: 1px solid #F1F1F1;
                }

                .perpsomasg .img {
                    float: left;
                    width: 64px;
                    height: 64px;
                    margin-right: 10px;
                }

                .perpsomasg .img img {
                    width: 100%;
                    height: 100%;
                }

                .perpsomasg .name {
                    font-size: 16px;
                    color: #333333;
                }

                .perpsomasg .level {
                    margin-top: 5px;
                    font-size: 12px;
                }

                .perpsomasg .level i {
                    width: 21px;
                    height: 16px;
                    display: inline-block;
                    margin-left: 9px;
                }

                .perpsomasg .level i img {
                    width: 100%;
                    height: 100%;
                }

                .perpsomasg .size {
                    font-size: 12px;
                    color: #999999;
                    margin-top: 5px;
                }

                .perpsomasg .btn {
                    width: 190px;
                }

                .perpsomasg .btn button {
                    border-radius: 20px;
                    background: #36C777;
                    width: 100%;
                    height: 30px;
                    line-height: 30px;
                    color: #fff;
                    margin-top: 25px;
                }
            </style>
            <div id="menubar" class="menubar">
                <div class="perpsomasg">
                    <div class="head">
                        <div class="img" >
                            <img id="headimgs" src="" />
                        </div>
                        <p class="name" id="name"></p>
                        <p class="level">
                            <span id="level"></span>
                            <i>
                                <img src="${ctx}/static/skins/default/img/crown.png" alt="">
                            </i>
                        </p>
                        <p class="size">
                            <span id="useSpace"></span>/
                            <span id="spaceBar"></span>
                        </p>
                    </div>
                    <!-- <div class="btn">
                        <button>扩容</button>
                    </div> -->
                </div>
                <ul>
                    <li onclick="gotoPage('${ctx}/folder?rootNode=0')">
                        <i>
                            <img src="${ctx}/static/skins/default/img/people.png">
                        </i>
                        <span>个人文件</span>
                    </li>
                    <li command="1">
                        <i></i>
                        <span>文档</span>
                    </li>
                    <li command="2">
                        <i></i>
                        <span>图片</span>
                    </li>
                    <li command="3">
                        <i></i>
                        <span>视频</span>
                    </li>
                    <li command="4">
                        <i></i>
                        <span>音乐</span>
                    </li>
                    <li command="5">
                        <i></i>
                        <span>其他</span>
                    </li>
                </ul>
                <!-- <ul>
                    <li>
                        <i>
                            <img src="${ctx}/static/skins/default/img/share.png">
                        </i>
                        <span>分享达人</span>
                    </li>
                </ul>
                <ul>
                    <li>
                        <i>
                            <img src="${ctx}/static/skins/default/img/wechat_33.png">
                        </i>
                        <span>微信备份</span>
                    </li>
                </ul> -->

                <ul>
                    <li onclick="gotoPage('${ctx }/trash')">
                        <i>
                            <img src="${ctx}/static/skins/default/img/pcicon_89.png">
                        </i>
                        <span>回收站</span>
                    </li>
                </ul>
                <ul>
                    <li onclick="gotoPage('${ctx }/wxRobot/createQrCode')">
                        <i>
                            <img src="${ctx}/static/skins/default/img/wechat-backup.png">
                        </i>
                        <span>微信备份</span>
                    </li>
                </ul>
                <!-- <ul>
                    <li onclick="gotoPage('${ctx }/logout')">
                        <i>
                            <img src="${ctx}/static/skins/default/img/exit.png">
                        </i>
                        <span>退出</span>
                    </li>
                </ul> -->
            </div>
            <script>
                (function ($) {
                    $.fn.extend({
                        Menubar: function (options) {
                            var self = this
                            self.init = function () {
                                getUserSpaceInfo();
                                setLocalStroageSize();

                                $("#menubar li").click(function () {
                                    $('.left').show();
                                    $(this).addClass('hover').siblings().removeClass(
                                        'hover');
                                    if ($(this).attr("command") == "1") {
                                        docType = '1';
                                        console.log(self);
                                        self.onsearchfile(docType);
                                    }
                                    if ($(this).attr("command") == "2") {
                                        docType = '2';
                                        self.onsearchfile(docType);
                                    }
                                    if ($(this).attr("command") == "3") {
                                        docType = '4';
                                        self.onsearchfile(docType);
                                    }
                                    if ($(this).attr("command") == "4") {
                                        docType = '3';
                                        self.onsearchfile(docType);
                                    }
                                    if ($(this).attr("command") == "5") {
                                        docType = '5';
                                        self.onsearchfile(docType);
                                    }

                                });

                                $('#openUser').show();
                            }

                            return self
                        }
                    })
                })(jQuery)
                /*搜索类型*/
                var docType = '';

                function getUserSpaceInfo() {
                    $.ajax({
                        type: "GET",
                        url: host + "/ecm/api/v2/users/" + curUserId,
                        error: function () {
                            $.Tost("获取用户存储空间信息失败", "cancel");
                        },
                        success: function (data) {
                            
                            if (data.spaceQuota == -1) {
                                $("#name").html(data.name);
                                $("#spaceBar").html(formatFileSize(data.spaceQuota));
                                $("#useSpace").html(formatFileSize(data.spaceUsed))
                            } else {
                                $("#name").html(data.name);
                                $("#spaceBar").html(formatFileSize(data.spaceQuota));
                                $("#useSpace").html(formatFileSize(data.spaceUsed))
                            }
                        },
                        complete: function () {
                            $('.load').css('display', 'none');
                        }

                    });
                    $.ajax({
                        type: "POST",
                        url: host + "/ecm/api/v2/wxmp/authCode/info",
                        error: function () {
                            $.Tost("获取用户信息失败", "cancel");
                        },
                        success: function (data) {
                            console.log(data.avatarUrl);
                            $('#headimgs').attr('src',data.avatarUrl)
                            if (data.type == 0) {
                                $('#level').html('普通会员');
                            }
                            if (data.type == 101) {
                                $('#level').html('黄金会员');
                            }
                            if (data.type == 102) {
                                $('#level').html('铂金会员');
                            }
                            if (data.type == 103) {
                                $('#level').html('钻石会员');
                            }
                        },

                    });
                }

                //获取本地存储大小
                function setLocalStroageSize() {
                    var size = 0;
                    for (var i = 0; i < localStorage.length; i++) {
                        var key = localStorage.key(i);
                        size += localStorage.getItem(key).length;
                    }
                    if (size != 0) {
                        $("#localStroageSize").html(formatFileSize(size));
                    } else {
                        $("#localStroageSize").html("0B");
                    }
                }
            </script>