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

                input::-webkit-input-placeholder {
                    color: #999999;
                }

                input::-moz-placeholder {
                    /* Mozilla Firefox 19+ */
                    color: #999999;
                }

                input:-moz-placeholder {
                    /* Mozilla Firefox 4 to 18 */
                    color: #999999;
                }

                input:-ms-input-placeholder {
                    /* Internet Explorer 10-11 */
                    color: #999999;
                }

                #header {
                    width: 100%;
                    height: 58px;
                    background: #EA5036;
                }

                #header p {
                    width: 300px;
                    height: 58px;
                    line-height: 55px;
                    margin-left: 20px;
                    float: left;
                }

                #header p img {
                    vertical-align: middle;
                }

                /*搜索框6*/

                .search {
                    float: right;
                    line-height: 58px;
                    margin-right: 30px;
                }

                .search input {
                    width: 206px;
                    height: 32px;
                    border-radius: 5px;
                    border: 0;
                    padding-left: 22px;
                }

                .search .submit {
                    display: inline-block;
                    margin-left: -49px;
                    width: 35px;
                    height: 21px;
                    vertical-align: middle;
                    position: relative;
                    background: #fff;
                    padding-left: 10px;
                    cursor: pointer;
                }

                .search .submit img {
                    display: block;
                }

                .loginOut {
                    float: right;
                    margin-right: 100px;
                    overflow: hidden;
                    line-height: 53px;
                    cursor: pointer;
                }

                .loginOut .image {
                    width: 18px;
                    height: 18px;
                    float: left;
                    vertical-align: middle;
                }

                .loginOut .image img {
                    display: inline-block;
                    width: 100%;
                    vertical-align: middle;
                }

                .loginOut span {
                    color: #fff;
                    font-size: 16px;
                    margin-left: 10px;
                }
            </style>
            <div id="header">
                <p>
                    <img src="${ctx}/static/skins/default/img/logofildpro.png" alt="">
                </p>
                <div class="loginOut" onclick="gotoPage('${ctx }/logout')">
                    <div class="image">
                        <img src="${ctx}/static/skins/default/img/out_96.png" alt="" srcset="">
                    </div>
                    <span>退出</span>
                </div>
                <!-- <div class="search">
                    <input type="text" placeholder="请输入" name="cname">
                    <div class="submit">
                        <img src="${ctx}/static/skins/default/img/searchfilepro.png" alt="" srcset="">
                    </div>
                </div> -->

            </div>