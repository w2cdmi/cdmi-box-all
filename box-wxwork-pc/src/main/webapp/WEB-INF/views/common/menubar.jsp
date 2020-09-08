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

                ul {
                    background-color: #fff;
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

                /* .totalSize {
        width: 12rem;
        background: #4E4E4E;
        height: 0.3rem;
    } */

                #spaceBar {
                    width: 0%;
                    background: #18b4ed;
                    height: 0.3rem;
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
            <div id="menubar">
                <dl>
                    <dt>
                        <a <c:if test="${param.activeId == 'index'}">class="active"</c:if> href="javascript:void(0);" onclick="gotoPage('${ctx}/')">首页</a>
                    </dt>
                    <dt>
                        <a <c:if test="${param.activeId == 'teamspace'}">class="active"</c:if> href="javascript:void(0);" onclick="gotoPage('${ctx}/teamspace')">协作</a>
                    </dt>
                    <!-- <dt><a>发现</a> -->
                    <dt style="position: relative">
                        <!--  -->
                        <a id="discan" <c:if test="${param.activeId == 'notification'}">class="active"</c:if> href="javascript:void(0);">发现
                            <i class="fa fa-angle-down"></i>
                        </a>
                        <div class="popover bottom" id="discan_popover" style="top:50px">
                            <div class="arrow" style="left: 60px;"></div>
                            <ul class="find-middle bt0">
                                <li class="find-lnbox" onclick="gotoPage('${ctx}/share/shareLinks')">
                                    <div class="find-img">
                                        <img src="${ctx}/static/skins/default/img/find-lnbox.png" />
                                    </div>
                                    <div class="find-content">
                                        <h1>收件箱</h1>
                                        <p>您收到的文件将放在这里</p>
                                    </div>
                                    <!-- <div class="find-number">
                            <span>99+</span>
                        </div> -->
                                </li>
                                <li class="find-share" onclick="gotoPage('${ctx}/shared')">
                                    <div class="find-img">
                                        <img src="${ctx}/static/skins/default/img/find-share.png" />
                                    </div>
                                    <div class="find-content">
                                        <h1>收到的共享</h1>
                                        <p>您收到的共享将放在这里</p>
                                    </div>
                                    <!-- <div class="find-number">
                            <span>50</span>
                        </div> -->
                                </li>
                                <!-- <li class="find-examine bb0" onclick="gotoPage('${ctx}/share/linkApproveList')">
									<div class="find-img">
										<img src="${ctx}/static/skins/default/img/find-examine.png" />
									</div>
									<div class="find-content">
										<h1>审批</h1>
										<p>您需要审批的外发文件将放在这里</p>
									</div>
									<div class="find-number">
										<span>13</span>
									</div>
								</li> -->
                            </ul>
                        </div>

                    </dt>
                    <!-- </dt> -->
                    <!-- <dt><a>我的</a> -->
                    <dt style="position: relative">
                        <!-- onclick="gotoPage('${ctx}/notification')" -->
                        <a id="discans" <c:if test="${param.activeId == 'personal'}">class="active"</c:if> href="javascript:void(0);">我的
                            <i class="fa fa-angle-down"></i>
                        </a>
                        <div class="popover bottom" id="discan_popovers" style="top:50px">
                            <div class="arrow" style="left: 60px;"></div>
                            <div class="per-header">
                                <ul class="bt0">
                                    <li>
                                        <div class="per-header-portrait">
                                            <%--
                                                                            <p>
                                                                                <img src="${ctx}/userimage/getLogo" />
                                                                            </p>
                                --%>
                                        </div>
                                        <div class="per-name">
                                            <i>
                                                <shiro:principal property="name" />
                                            </i>
                                            <%-- <span>研发部</span>--%>
                                                <div>
                                                    <p id="useSpace"></p>
                                                    <!-- <div class="totalSize" style="display:none">
                                        <div id="spaceBar"></div>
                                    </div> -->
                                                </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>

                            <div class="per-content">
                                <div class="per-gap"></div>
                                <ul class="bt0">
                                    <!-- <li onclick="gotoPage('${ctx }/uploadFolder/getUploadFilePage')">
										<p>
											<img src="${ctx}/static/skins/default/img/pre-transfer.png" />
										</p>
										<span>传输列表</span>
									</li> -->
                                    <li onclick="gotoPage('${ctx}/myShares')">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/pre-issue.png" />
                                        </p>
                                        <span>我发出的共享</span>
                                    </li>
                                    <li onclick="gotoPage('${ctx }/sharedlinks')">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/pre-issue-w.png" />
                                        </p>
                                        <span>我外发的文件</span>
                                    </li>
                                </ul>
                                <div class="per-gap"></div>
                                <ul class="bt0">
                                    <li onclick="gotoPage('${ctx }/trash')">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/per-recycle.png" />
                                        </p>
                                        <span>回收站</span>
                                    </li>
                                    <li onclick="clearLocalStroage()">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/clear.png" />
                                        </p>
                                        <span>清除本地缓存</span>
                                        <span style="float:right" id="localStroageSize">0B</span>
                                    </li>
                                </ul>
                                <div class="per-gap"></div>
                                <ul class="bt0">
                                    <li onclick="gotoPage('${ctx }/wxRobot/createQrCode')">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/wechat-backup-pc.png" />
                                        </p>
                                        <span>微信备份</span>
                                    </li>
                                </ul>
                                <div class="per-gap"></div>
                                <ul class="bt0">
                                    <li onclick="gotoPage('${ctx }/user/account')">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/personal-icon.png" />
                                        </p>
                                        <span>设置</span>
                                    </li>
                                </ul>
                                <div class="per-gap"></div>
                                <ul class="bt0">
                                    <li onclick="openUserAgreement()">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/pre-protocol.png" />
                                        </p>
                                        <span>用户协议</span>
                                    </li>
                                </ul>
                                <div class="per-gap"></div>
                                <ul class="bt0" id="EcmManagement">
                                    <li onclick="EcmManagement()">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/pre-protocol.png" />
                                        </p>
                                        <span>企业后台管理</span>
                                    </li>
                                </ul>

                                <!-- <ul class="bt0">
                                    <li onclick="logout()">
                                        <p>
                                            <img src="${ctx}/static/skins/default/img/exit.png" />
                                        </p>
                                        <span>退出</span>
                                    </li>
                                </ul> -->
                            </div>
                        </div>

                    </dt>
                    <!-- </dt> -->
                </dl>
                <div class="loginOut" onclick="logout()">
                    <div class="image">
                        <img src="${ctx}/static/skins/default/img/out_96.png" alt="" srcset="">
                    </div>
                    <span>退出</span>
                </div>
            </div>
            <div class="userdialog" id="openUser">
                <div class="model"></div>
                <div class="dialogrect">
                    <div class="head">用户协议
                        <i class="fa fa-close" id='close' onclick="close()"></i>
                    </div>
                    <div class="body">
                        <div class="content">
                            <p class="title">企业文件宝用户服务协议</p>
                            <div>本协议是由华一云网科技成都有限公司（以下简称“华一云网”）与所有使用企业文件宝服务的主体（包括但不限于个人、企业、其他组织和团队等主体）（以下简称“用户”）对企业文件宝服务的使用及相关服务所订立的有效合约。使用企业文件宝服务的任何服务即表示接受本协议的全部条款。本协议适用于任何企业文件宝服务，包括本协议期限内的用户所使用的各项服务和软件的升级和更新</div>
                            <p class="innertitle">服务内容及使用须知</p>
                            <div>1.企业文件宝服务是一个向广大用户提供上传空间和技术的信息存储空间服务平台，通过企业文件宝服务技术为用户提供用户数据存储、同步、管理和分享等在线服务。企业文件宝服务本身不直接上传、提供内容，对用户传输内容不做任何修改或编辑。</div>
                            <div>2.用户理解，企业文件宝服务仅提供相关的网络服务，除此之外与相关网络服务有关的设备（如个人电脑、手机、及其他与接入互联网或移动网有关的装置）及所需的费用（如为接入互联网而支付的电话费及上网费、为使用移动网而支付的手机费）均应由用户自行负担</div>
                            <div>3.用户不得滥用企业文件宝服务的服务，华一云网在此郑重提请用户注意，任何经由本服务以上传、张贴、发送即时信息、电子邮件或任何其他方式传送的资讯、资料、文字、软件、音乐、音讯、照片、图形、视讯、信息、用户的注册登记资料、用户徽标(LOGO)、URL或其他资料（以下简称“内容”），无论系公开还是私下传送，均由内容提供者、使用者对其上传、使用行为自行承担责任。企业文件宝服务作为信息存储空间服务平台，无法控制经由本服务传送之内容，也无法对用户的使用行为进行全面控制，因此不能保证内容的合法性、正确性、完整性、真实性或品质；用户已预知使用本服务时，可能会接触到令人不快、不适当等内容，并同意将自行加以判断并承担所有风险，而不依赖于企业文件宝服务</div>
                            <div>4.若用户使用企业文件宝服务的行为不符合本协议，华一云网在经由通知、举报等途径发现时有权做出独立判断，且有权在无需事先通知用户的情况下立即终止向用户提供部分或全部服务。用户若通过企业文件宝服务散布和传播侵权、反动、色情或其他违反国家法律、法规的信息，企业文件宝服务的系统记录有可能作为用户违反法律法规的证据；因用户进行上述内容在企业文件宝服务的上载、传播而导致任何第三方提出索赔要求或衍生的任何损害或损失，由用户自行承担全部责任。</div>
                            <div>5.华一云网有权对用户使用企业文件宝服务网络服务的情况进行监督，如经由通知、举报等 途径发现用户在使用企业文件宝服务所提供的网络服务时违反任何本协议的规定，华一云网有权要求用户改正或直接采取一切华一云网认为必要的措施（包括但不限于更改或删除用户上载的内容、暂停或终止用户使用网络服务，和/或公示违法或违反本协议约定使用企业文件宝服务用户账户的权利）以减轻用户不当行为造成的影响。</div>
                            <div>6.企业文件宝体验版为每个用户账号提供2GB的数据存储空间，体验时间为30天(从初次使用当天开始计算)。</div>
                            <div>7.华一云网有权根据实际情况自行决定单个用户在本软件及服务中数据的最长储存期限，并在服务器上为其分配数据最大存储空间等。用户可根据自己的需要自行备份本软件及服务中的相关数据。</div>
                            <p class="innertitle">所有权</p>
                            <div>华一云网保留对以下各项内容、信息完全的、不可分割的所有权及知识产权</div>
                            <div>1.除用户自行上载、传播的内容外，企业文件宝服务及其所有元素，包括但不限于所有内容、数据、技术、软件、代码、用户界面以及与其相关的任何衍生作品；</div>
                            <div>2.用户信息；</div>
                            <div>3.用户向企业文件宝服务提供的与该平台服务相关的任何信息及反馈。</div>
                            <div>未经华一云网同意，上述资料均不得在任何媒体直接或间接发布、播放、出于播放或发布目的而改写或再发行，或者被用于其他任何商业目的。上述资料或其任何部分仅可作为私人用途而保存在某台计算机内。华一云网不就由上述资料产生或在传送或递交全部或部分上述资料过程中产生的延误、不准确、错误和遗漏或从中产生或由此产生的任何损害赔偿，以任何形式向用户或任何第三方负法律、经济责任；华一云网为提供企业文件宝服务而使用的任何软件（包括但不限于软件中所含的任何图像、照片、动画、录像、录音、音乐、文字和附加程序、随附的帮助材料）的一切权利均属于该软件的著作权人，未经该软件的著作权人许可，用户不得对该软件进行反向工程（reverse
                                engineer）、反向编译（decompile）或反汇编（disassemble），或以其他方式发现其原始编码，以及实施任何涉嫌侵害著作权的行为。
                            </div>
                            <p class="innertitle">承诺与保证</p>
                            <div>1.用户保证，其向企业文件宝服务上传的内容不得并禁止直接或间接的：</div>
                            <div>1.1 删除、隐匿、改变企业文件宝服务上显示或其中包含的任何专利、版权、商标或其他所有权声明；</div>
                            <div>2.1 以任何方式干扰或企图干扰企业文件宝服务或华一云网网站任何部分或功能的正常运行；</div>
                            <div>3.1 避开、尝试避开或声称能够避开任何内容保护机制或者企业文件宝服务数据度量工具；</div>
                            <div>4.1 未获得华一云网事先书面同意以书面格式或图形方式使用源自华一云网的任何注册或未注册的作品、服务标志、公司徽标(LOGO)、URL或其他标志；</div>
                            <div>5.1 使用任何标志，包括但不限于以对此类标志的所有者的权利的玷污、削弱和损害的方式使用华一云网标志，或者以违背本协议的方式为自己或向其他任何人设定或声明设定任何义务或授予任何权利或权限，除非华一云网以书面方式指明，否则，用户不得导出任何用户信息，并且必须在获取任何用户信息或其他企业文件宝服务内容后的
                                24 小时内停止使用和删除它们；
                            </div>
                            <div>6.1 未事先经过原始用户的同意向任何非原始用户显示或以其他方式提供任何用户信息；</div>
                            <div>7.1 请求、收集、索取或以其他方式从任何用户那里获取对企业文件宝帐号、密码或其他身份验证凭据的访问权；</div>
                            <div>8.1 为任何用户自动登录到企业文件宝帐号代理身份验证凭据；</div>
                            <div>9.1 提供跟踪功能，包括但不限于识别其他用户在个人主页上查看或操作；</div>
                            <div>10.1 自动将浏览器窗口定向到其他网页；</div>
                            <div>11.1 未经授权冒充他人或获取对企业文件宝服务的访问权；或者未经用户明确同意，让任何其他人亲自识别该用户。</div>
                            <div>12.1 用户违反上述任何一款的保证，华一云网均有权就其情节，对其做出警告、屏蔽、直至取消资格的处罚；如因用户违反上述保证而给企业文件宝服务、企业文件宝服务用户或华一云网的任何合作伙伴造成损失，用户自行负责承担一切法律责任并赔偿损失。</div>
                            <div>2.用户的承诺：</div>
                            <div>2.1 其一经取得利用企业文件宝服务提供的网络服务上传、发布、传送或通过其他方式传播的内容的权利人（如有）的书面授权，并已与前述权利人就权益分配达成内部协议，保证其在将相关内容提交、上传至企业文件宝服务前拥有充分、完整无瑕疵、排他的所有权及知识产权。</div>
                            <div>2.2 用户利用企业文件宝服务提供的网络服务上传、发布、传送或通过其他方式传播的内容，不得含有任何违反国家法律法规政策的信息，包括但不限于下列信息：</div>
                            <div>2.2.1 反对宪法所确定的基本原则的；</div>
                            <div>2.2.2 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</div>
                            <div>2.2.3 损害国家荣誉和利益的；</div>
                            <div>2.2.4 煽动民族仇恨、民族歧视，破坏民族团结的；</div>
                            <div>2.2.5 破坏国家宗教政策，宣扬邪教和封建迷信的；</div>
                            <div>2.2.6 散布谣言，扰乱社会秩序，破坏社会稳定的；</div>
                            <div>2.2.7 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；</div>
                            <div>2.2.8 侮辱或者诽谤他人，侵害他人合法权益的；</div>
                            <div>2.2.9 含有法律、行政法规禁止的其他内容的。</div>
                            <div>2.3 用户不得为任何非法目的而使用本网络服务系统；不得以任何形式使用企业文件宝服务存储网络服务侵犯华一云网的商业利益，包括并不限于发布非经华一云网许可的商业广告；不得利用企业文件宝服务网络服务系统进行任何可能对互联网或移动网正常运转造成不利影响的行为；</div>
                            <div>2.4 用户不得利用企业文件宝服务的服务从事以下活动：</div>
                            <div>2.4.1 未经允许，进入计算机信息网络或者使用计算机信息网络资源的；</div>
                            <div>2.4.2 未经允许，对计算机信息网络功能进行删除、修改或者增加的；</div>
                            <div>2.4.3 未经允许，对进入计算机信息网络中存储、处理或者传输的数据和应用程序进行删除、修改或者增加的；</div>
                            <div>2.4.4 故意制作、传播计算机病毒等破坏性程序的；</div>
                            <div>2.4.5 其他危害计算机信息网络安全的行为。</div>
                            <div>2.5 如因用户利用企业文件宝服务提供的网络服务上传、发布、传送或通过其他方式传播的内容存在权利瑕疵或侵犯了第三方的合法权益（包括但不限于专利权、商标权、著作权及著作权邻接权、肖像权、隐私权、名誉权等）而导致华一云网或与华一云网合作的其他单位面临任何投诉、举报、质询、索赔、诉讼；或者使华一云网或者与华一云网合作的其他单位因此遭受任何名誉、声誉或者财产上的损失，用户应积极地采取一切可能采取的措施，以保证华一云网及与华一云网合作的其他单位免受上述索赔、诉讼的影响。同时用户对华一云网及与华一云网合作的其他单位因此遭受的直接及间接经济损失负有全部的损害赔偿责任。</div>
                            <p class="innertitle">知识产权保护</p>
                            <div>如果用户上传的内容允许其他用户下载、查看、收听或以其他方式访问或分发，其必须保证该内容的发布和相关行为实施符合相关知识产权法律法规中相关的版权政策，包括但不限于：</div>
                            <div>1.用户在收到侵权通知之时，应立即删除或禁止访问声明的侵权内容，并同时联系递送通知的人员以了解详细信息。</div>
                            <div>2.用户知悉并同意华一云网将根据相关法律法规对第三方发出的合格的侵权通知进行处理，并按照要求删除或禁止访问声明的侵权内容，采用并实施适当的政策，以期杜绝在相应条件下重复侵权。</div>
                            <p class="innertitle">遵守当地法律监管</p>
                            <div>1.用户使用本服务过程中应当遵守当地相关的法律法规，并尊重当地的道德和风俗习惯。如果用户的行为违反了当地法律法规或道德风俗，用户应当为此独立承担责任。</div>
                            <div>2.用户应避免因使用本服务而使华一云网卷入政治和公共事件，否则华一云网有权暂停或终止对用户的服务。</div>
                            <p class="innertitle">数据隐私</p>
                            <div>1.用户存储在企业文件宝服务上的任何文件，包括但不仅限于各类文档、图片、音乐、视频、应用程序等数据资料（下简称“数据资料”）拥有完整的所有权和使用权，华一云网和任何第三方都无权获取或使用这些数据资料；</div>
                            <div>2.企业文件宝会对用户的数据资料进行加密传输和加密存储，在技术可控的范围内保护用户的数据隐私；</div>
                            <div>3.企业文件宝的运营团队内部具有严格的管理制度，保证任何成员都无法从系统后台获取到用户的数据资料；</div>
                            <div>4.企业文件宝具备完善的权限管理功能，用户可以对各类数据资料授予不同的访问权限，而企业文件宝将按照用户所设置的权限赋予相应数据资料的访问范围；</div>
                            <div>5.企业文件宝不收集有关用户的任何特定信息，除非用户特意提供了此类信息或者用户授权企业文件宝收集此类信息。除用户注册信息之外，企业文件宝所记录和保存的信息包括：时间、浏览器类型、浏览器语言、用户IP地址、操作系统和软硬件环境信息等。这些数据将用于为用户提供详尽的用户使用报告和为用户提供更加优质的服务；</div>
                            <div>6.在不透露用户任何隐私资料的前提下，华一云网布会对包云服务的整体用户访问量、访问时段等行为数据进行分析，进而为用户提供更好的服务；</div>
                            <div>7.如果是因为用户或者用户的成员的失误造成的损失，包括但不限于管理员的操作失误、用户的操作失误以及帐号/密码泄露等，华一云网不承担任何责任；</div>
                            <div>8.华一云网有权在下列情况公开或向第三方提供用户存储在企业文件宝服务上的非公开内容:</div>
                            <div>8.1 有关法律、法规规定或企业文件宝服务合法服务程序规定；</div>
                            <div>8.2 在紧急情况下，为维护用户及公众的权益；</div>
                            <div>8.3 为维护企业文件宝的商标权、专利权及其他任何合法权益；</div>
                            <div>8.4 其他依法需要公开、编辑或透露个人信息的情况。</div>
                            <p class="innertitle">免责声明</p>
                            <div>1.鉴于网络服务的特殊性，用户同意华一云网有权随时变更、中断或终止部分或全部的网络服务。如变更、中断或终止的网络服务属于免费网络服务，华一云网无需通知用户，也无需对任何用户或任何第三方承担任何责任。</div>
                            <div>2.用户理解，华一云网需要定期或不定期地对提供网络服务的平台或相关的设备进行检修或者维护，如因此类情况而造成收费网络服务在合理时间内的中断，华一云网无需为此承担任何责任，但华一云网应尽可能事先进行通告。</div>
                            <div>3.华一云网可在任何时候为任何原因变更本服务或删除其部分功能。华一云网可在任何时候取消或终止对用户的服务。华一云网取消或终止服务的决定不需要理由或通知用户。一旦服务取消，用户使用企业文件宝服务的权利立即终止。
                                一旦企业文件宝服务取消或终止，用户在企业文件宝服务中储存的任何信息可能无法恢复。
                            </div>
                            <div>4.华一云网不保证（包括但不限于）：</div>
                            <div>4.1 企业文件宝服务适合用户的使用要求；</div>
                            <div>4.2 企业文件宝服务不受干扰，及时、安全、可靠或不出现错误；及用户经由企业文件宝服务取得的任何产品、服务或其他材料符合用户的期望。</div>
                            <div>4.3 用户使用经由企业文件宝服务下载或取得的任何资料，其风险由用户自行承担；因该等使用导致用户电脑系统损坏或资料流失，用户应自己负完全责任；</div>
                            <div>5.基于以下原因而造成的利润、商业信誉、资料损失或其他有形或无形损失， 华一云网不承担任何直接、间接的赔偿：</div>
                            <div>5.1 对企业文件宝服务的使用或无法使用；</div>
                            <div>5.2 经由企业文件宝服务购买或取得的任何产品、资料或服务；</div>
                            <div>5.3 用户资料遭到未授权的使用或修改；及其他与企业文件宝服务相关的事宜。</div>
                            <div>5.4 用户必须选择与所安装终端设备相匹配的软件版本，否则，由于软件与终端设备型号不相匹配所导致的任何问题或损害，均由用户自行承担；</div>
                            <div>5.5 由于无线网络信号不稳定、无线网络带宽小等原因，所引起的企业文件宝登录失败、资料同步不完整、页面打开速度慢等风险。</div>
                            <div>6.由于用户授权第三方（包括第三方应用）访问/使用其企业文件宝服务空间的内容所导致的纠纷或损失，应由用户自行负责，与华一云网无关。</div>
                            <p class="innertitle">不可抗力</p>
                            <div>用户需要自行承担下述责任和赔偿费用</div>
                            <div>1.因不可抗力或者其他意外事件，使得本服务条款的履行不可能、不必要或者无意义的，遭受不可抗力、意外事件的一方不承担责任。</div>
                            <div>2.不可抗力、意外事件是指不能预见、不能克服并不能避免且对一方或双方当事人造成重大影响的客观事件，包括但不限于自然灾害如洪水、地震、瘟疫流行等以及社会事件如战争、动乱、政府行为、电信主干线路中断、黑客、网路堵塞和政府管制等</div>
                            <p class="innertitle">服务变更</p>
                            <div>1.华一云网可能会对服务内容进行变更，也可能会中断、中止或终止服务。</div>
                            <div>2.华一云网有权单方按需要修改或变更所提供的收费服务、收费标准、收费方式、服务费、及服务条款。华一云网在提供服务时，可能现在或日后对部分服务的用户开始收取一定的费用如用户拒绝支付该等费用，则不能在收费开始后继续使用相关的服务。</div>
                            <div>3.用户有责任自行备份存储在本服务中的数据。如果您的服务被终止，华一云网可以从服务器上永久地删除您的数据, 但法律法规另有规定的除外。服务终止后，华一云网没有义务向您返还数据。</div>
                            <p class="innertitle">其他</p>
                            <div>1.本协议最终解释权归华一云网所有。</div>
                            <div>2.本协议一经公布即生效，华一云网有权随时对协议内容进行修改。</div>
                            <div>3.本协议所有条款的标题仅为阅读方便，本身并无实际涵义，不能作为本协议涵义解释的依据。</div>
                            <div>4.本协议项下华一云网对于用户所有的通知均通过官方网站（www.filepro.cn）公告方式进行；该等通知于发送之日视为已送达用户。如果不同意华一云网对本协议相关条款所做的修改，用户有权停止使用网络服务。如果用户继续使用网络服务，则视为用户接受华一云网对本协议相关条款所做的修改。</div>
                            <div>5.本协议的订立、执行和解释及争议的解决均应适用中国法律并受中国法院管辖。如双方就本协议内容或其执行发生任何争议，双方应尽量友好协商解决；协商不成时，任何一方均可向华一云网所在地的人民法院提起诉讼。</div>
                            <div>6.本协议构成双方对本协议之约定事项及其他有关事宜的完整协议，除本协议规定的之外，未赋予本协议各方其他权利。</div>
                            <div>7.如本协议中的任何条款无论因何种原因完全或部分无效或不具有执行力，本协议的其余条款仍应有效并且有约束力。</div>
                            <p class="innertitle" style="margin-top: 20px;">华一云网科技成都有限公司</p>
                        </div>
                        <div id="closebtn" class="ok-btn">已阅</div>
                    </div>
                </div>
            </div>
            <script>
                (function ($) {
                    $("#discan").popover($("#discan_popover"), true);
                    $("#discans").popover($("#discan_popovers"), true);

                })(jQuery)
                $(function () {
                    getUserSpaceInfo();
                    setLocalStroageSize();
                });

                function getUserSpaceInfo() {
                    $.ajax({
                        type: "GET",
                        url: host + "/ecm/api/v2/users/" + curUserId,
                        error: function () {
                            $.toast("获取用户存储空间信息失败", "cancel");
                        },
                        success: function (data) {
                            if (data.spaceQuota == -1) {
                                $("#spaceBar").css("width", "0%");
                                $("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;无限制");
                                $(".totalSize").css("display", "none");
                            } else {
                                $("#spaceBar").css("width", formatFileSize(data.spaceUsed) / formatFileSize(
                                    data.spaceQuota));
                                $("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;" +
                                    formatFileSize(data.spaceQuota));
                                $(".totalSize").css("display", "block");
                            }
                        },
                        complete: function () {
                            $('.load').css('display', 'none');
                        }

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
                //进入ECM企业管理

                if (isAdmin == '1') {
                    function EcmManagement() {
                        window.open(`/admin/#/login?token=` + userToken);
                    }
                } else {
                    $('#EcmManagement').css('display', 'none');
                };
                function clearLocalStroage() {
                    $.Confirm("清除缓存不会影响已经下载文件，下载文件碎片以及传输列表内容会被清除", function () {
                        localStorage.clear();
                        setLocalStroageSize();
                        $.Alert('清除完成');
                    });
                    // var r = confirm("清除缓存不会影响已经下载文件，下载文件碎片以及传输列表内容会被清除")
                    // if (r == true) {
                    // 	localStorage.clear();
                    // 	setLocalStroageSize();
                    // 	alert('清除完成');
                    // } else {
                    // 	alert('您取消了清除');
                    // }
                }

                function openUserAgreement() {
                    $('#openUser').show();
                }

                function logout() {
                    gotoPage("${ctx}/logout")
                }
                $('#close').on('click', function () {
                    $('#openUser').hide();
                })
                $('#closebtn').on('click', function () {
                    $('#openUser').hide();
                })
                $('.model').on('click', function () {
                    $('#openUser').hide();
                })
            </script>