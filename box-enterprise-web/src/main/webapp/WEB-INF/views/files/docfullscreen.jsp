<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<head>


    <style>
        .ndy {
            width: 100% important;
            height: 100% !important;
        }

        #index_layer2 {
            width: 100%;
            height: 100%;
            top: 0px;
            left: 0px;
            position: fixed;
            filter: Alpha(opacity=80);
            opacity: 0.8;
            background: #333;
            display: none;
            z-index: 99998;
        }

        #filedoc {
            background: #383c52;
            width: 1000px;
            height: 600px;
            top: 50%;
            left: 50%;
            margin-left: -500px;
            margin-top: -300px;
            position: fixed;
            z-index: 99999;
            display: none;
            border-radius: 2px;
            border-radius: 6px;
        }

        #filedoc .close {
            height: 30px;
            width: 100%;
            background: #383C52;
            border-radius: 6px 6px 0 0;
        }

        #filedoc .close span#close_doc {
            display: block;
            float: right;
            line-height: 30px;
            cursor: pointer;
            text-align: center;
            height: 30px;
            width: 30px;
            color: #fff;
            font-size: 14px;
        }

        #filedoc .content {
            width: 980px;
            height: 560px;
            margin: 0 10px 10px;
        }

        .cl {
            clear: both;
        }

        .max {
            left: -260px;
            z-index: 999;
            top: -120px;
            position: absolute !important;
        }

        .min {
            width: 967px;
            height: 512px;
        }

        .fullscreen {
            background: #222;
            position: relative;
            z-index: 998;
            width: 980px;
            height: 560px;
            overflow: hidden;
        }

        #filedoc .content .img {
            padding: 0px 0px;
            margin: 0px 0px;
            overflow: hidden;
            position: relative;
            height: auto;

        }

        #filedoc .content .img img {
            width: 100%;
        }

        .content .img .prev, .fullscreen .content .img .next {
            display: block;
            position: absolute;
            width: 35%;
            height: 100%;
            z-index: 1;
            top: 0;
            cursor: pointer;
        }

        .fullscreen .content .img .prev {
            left: 20px;
            cursor: url(static/skins/default/img/arrow_left.ico), default;
        }

        .fullscreen .content .img .next {
            right: 20px;
            cursor: url(static/skins/default/img/arrow_right.ico), default;
        }

        .content .img .prev {
            left: 0;
            background-color: #fff;
            opacity: 0;
            filter: Alpha(opacity=0);
        }

        .fullscreen_max {
            width: 100%;
            height: 100%;
            position: relative;
        }

        .prev_hover {
            background: url(/csic/csic/resource/mwc/img/prev.png) no-repeat center center;
        }

        .next_hover {
            background: url(/csic/csic/resource/mwc/img/next.png) no-repeat center center;
        }

        .content .img .next {
            right: 0;
            background-color: #fff;
            opacity: 0;
            filter: Alpha(opacity=0);

        }

        .content_min {
            min-width: 980px;
            min-height: 560px;
            overflow: auto;
            z-index: 10;
            margin: 0 !important;
        }

        .content_max {
            width: 100%;
            height: 99%;
        }

        .fullscreen .tool_bar {
            z-index: 9999999;
            width: 100%;
            overflow: Hidden;
            font-family: "Microsoft Yahei";
            font-size: 14px;
            position: absolute;
            bottom: -5px;
            left: 0px;
            background-color: #555;
            opacity: .75;
            filter: Alpal(opacity=75%);
        }

        .tool_bar .tool_bar_page {
            height: 24px;
            margin: 10px 0 10px 40%;
            overflow: hidden;
        }

        .tool_bar_page .tb-prev {
            display: block;
            width: 20px;
            height: 20px;
            float: left;
        }

        .tool_bar_page .tb-prev .tb-prev-icon, .tool_bar_page .tb-prev {
            display: block;
            width: 24px;
            height: 24px;
            background: url(static/skins/default/img/arrow_left_m.png) no-repeat 0 0;
        }

        .tb-next-icon {
            display: block;
            width: 24px;
            height: 24px;
            background: url(static/skins/default/img/arrow_right_m.png) no-repeat 0 0;
        }

        .tool_bar_page .tb-prev {
            background-position: -94px -2px;
        }

        .tool_bar_page .tb-page {
            margin: 0 20px;
            height: 20px;
            display: block;
            float: left;
            overflow: hidden;
        }

        .tb-page .tb-page-input {
            border-radius: 0;
            padding: 0;
            background: #fff;
            width: 30px;
            height: 18px;
            line-height: 18px;
            border: 1px solid #ccc;
            line-height: 20px;
            text-align: center;
        }

        .tb-page span {
            color: #fff !important;
            vertical-align: top;
        }

        .tool_bar .tb-zoom {
            display: block;
            float: right;
            width: 40px;
            height: 40px;
            cursor: pointer;
            position: absolute;
            right: 0;
            bottom: 0px;
        }

        .tb-zoom span {
            display: block;
            width: 24px;
            height: 24px;
            background: #fff url(static/skins/default/img/full_screen.png) no-repeat 0 0;
            margin-top: 5px;
            border-radius: 3px;
        }

        .tb-zoomin span {
            background-image: url(static/skins/default/img/full_screen_exit.png);
        }

        .max .tb-zoom span {
            background-position: -36px 8px;
        }

        .doc_view_prev {
            cursor: pointer;
        }

        .doc_view_next {
            cursor: pointer;
        }

        .fullscreen_ie_max {
            z-index: 998;
            width: 100%;
            height: 100%;
            position: absolute;
        }

        .ie8_fullscreen {
            top: 0;
            position: fixed !important;
            z-index: 1;
            background: #000;
            left: 0;
        }

        .ie8_fullscreen .img img {

        }

        .ie8_fullscreen .ie8_content {
            width: 98%;
            height: 93%;
            margin: 20px auto;
            background: #fff;
        }

        .ie8_fullscreen .doc {
            overflow: auto;
        }

        .ie8_fullscreen .ppt {
            overflow: hidden;
        }

        .ie8_fullscreen .ie8_bar {
            height: 45px;
            background: #666;
            width: 100%;
            position: fixed;
            bottom: 0;
        }
    </style>
</head>


<div id="index_layer2"></div>
<div id="filedoc">
    <div class="close">
        <span id="close_doc" onclick="closeDoc();">X</span>
    </div>
    <div class="content" id="contentHeight">
        <div class="video_right_box"
             style="display: block; width: 976px; height: 560px">
            <div class="fullscreen">
                <div class="fullscreen_max">
                    <div class="content content_h1 content_min">
                        <div class="img">
                            <img id="doc_ppt_img" src="" alt="" width="100%" height="100%"/>
                            <span class="prev"></span> <span class="next"></span>
                        </div>

                    </div>
                    <div class="tool_bar">
                        <div class="tool_bar_page">
							<span class="tb-prev doc_view_prev"><span
                                    class="tb-prev-icon" title="前一页" onclick="prePage()"></span></span> <span
                                class="tb-page">
								<input id="doc_view_current_page" name="" type="text" value="50"
                                       class="tb-page-input"> <span>/</span> <span
                                id="doc_view_totap_page">100</span>
							</span> <span class="tb-prev doc_view_next"><span
                                class="tb-next-icon" title="后一页" onclick="nextPage()"></span></span>
                        </div>
                        <div class="tb-zoom" maxFlag="1"
                             onclick="requestFullscreen();">
                            <span class="tb-zoom-icon-min" id="maxAndMinCicon"></span>
                        </div>
                    </div>
                    <div class="cl"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var element = $(".fullscreen_max")[0];
    var previewUrl;

    function nextPage() {

        if (curPage == curTotalPage) {

            curPage = 1;
        }
        else {
            curPage = parseInt(curPage) + 1;

        }

        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/views/viewMetaInfo/" + cutOwnerId + "/" + curNodeId + "/" + curPage,
            error: function (request) {
            },
            success: function (data) {

                previewUrl = data.url;
                if (previewUrl) {
                    document.getElementById("doc_ppt_img").src = previewUrl;
                    $("#doc_view_current_page").val(curPage);
                }
                else {
                    location.href = "${ctx}/login";
                }

                $('#filedoc .content').scrollTop(0);
            }
        });
    }

    function prePage() {

        if (curPage == 1) {

            curPage = curTotalPage;
        }
        else {
            curPage = parseInt(curPage) - 1;
        }

        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/views/viewMetaInfo/" + cutOwnerId + "/" + curNodeId + "/" + curPage,
            error: function (request) {
                doDownLoadLinkError(request);
            },
            success: function (data) {
                previewUrl = data.url;
                document.getElementById("doc_ppt_img").src = previewUrl;
                $("#doc_view_current_page").val(curPage);
                $('#filedoc .content').scrollTop(0);
            }
        });
    }

    function closeDoc() {
        $("#index_layer2").css("display", "none");
        $("#filedoc").css("display", "none");
        curPage = 0;
        totalPage = 0;
        $("#doc_view_current_page").val(0);
        $("#doc_view_totap_page").html(0);
        document.getElementById("doc_ppt_img").src = "#";
    }

    //requestFullscreen(element);

    function requestFullscreen() {
        var el = element;
        if ($('.tb-zoom').hasClass('tb-zoomin')) {
            exitFullscreen();
        } else {
            $('.tb-zoom').addClass('tb-zoomin');
            $(".content_h1").attr("style", "width:100%!important;height:100%!important;margin:0;overflow:auto");
            var _requestFullscreen;
            launchFullscreen(el);
        }
    }

    function _IEFullScreenHandler() {
        var $windowHeight = $(window).height() - 10;
        var $windowWidth = $(window).width() - 10;
        $("#mainFrameHeadId").hide();
        $(".fullscreen").addClass("ie8_fullscreen").css({"height": $(window).height(), "width": $(window).width()});
    }

    function launchFullscreen(element) {
        $(".content_h1").attr("style", "width:100%!important;height:100%!important;margin:0;overflow:auto");
        //此方法不可以在異步任務中執行，否則火狐無法全屏
        if (element.requestFullscreen) {
            element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
            element.msRequestFullscreen();
        } else if (element.oRequestFullscreen) {
            element.oRequestFullscreen();
        } else if (element.webkitRequestFullscreen) {
            element.webkitRequestFullScreen();
        } else {
            _IEFullScreenHandler();
        }
    }

    function _IEexitFullscreen() {
        $("#mainFrameHeadId").show();
        $(".fullscreen").removeClass('ie8_fullscreen').css({height: '100%', width: '100%'});
    }
    function exitFullscreen() {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.oRequestFullscreen) {
            document.oCancelFullScreen();
        } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        } else {//IE
            _IEexitFullscreen();
        }
        $('.tb-zoom').removeClass('tb-zoomin');
    }
    //键盘事件绑定
    $(document).keyup(function (e) {
        if (getBrowserInfo()) {
            var oEvent = e || event;
            keyFn(oEvent.keyCode);
        } else {
            event.preventDefault();
            var key = e.which;
            keyFn(key);
        }

    });
    //键盘事件
    function keyFn(key) {
        if (key == 27) {
            exitFullscreen();
        } else if (key == 37) {
            prePage();
        } else if (key == 39) {
            nextPage();
        }
    }
    //判断浏览器是否为IE
    function getBrowserInfo() {

        var agent = navigator.userAgent.toLowerCase();
        var regStr_ie = /msie [\d.]+;/gi;
        //IE
        if (agent.indexOf("msie") > 0) {
            return true;
        } else {
            return false;
        }

    }


    $("#doc_view_current_page").keydown(function (event) {
        switch (event.keyCode) {

            case 13:
                curPage = $("#doc_view_current_page").val();
                $.ajax({
                    type: "GET",
                    async: false,
                    url: "${ctx}/views/viewMetaInfo/" + cutOwnerId + "/" + curNodeId + "/" + curPage,
                    error: function (request) {

                    },
                    success: function (data) {
                        previewUrl = data.url;
                        document.getElementById("doc_ppt_img").src = previewUrl;
                        $("#doc_view_current_page").val(curPage);
                        $('#filedoc .content').scrollTop(0);
                    }
                });
        }
    });


    $(".prev").on('click', function () {

        prePage();

    });

    $(".next").on('click', function () {

        nextPage();

    });


</script>
