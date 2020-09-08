<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse"
           uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token",
            CSRFTokenManager.getTokenForSession(session));
%>
<%@ include file="../files/commonForFiles.jsp" %>
<script src="${ctx}/static/lightbox/jquery.lightboxforfavorite.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        $.fn.lightboxFVR.defaults.strings = {
            closeTitle: "<spring:message code='button.close'/>",
            image: "<spring:message code='file.tips.image'/>",
            rotateR: "<spring:message code='lightbox.rotateR'/>",
            rotateL: "<spring:message code='lightbox.rotateL'/>",
            download: "<spring:message code='button.download'/>",
            prev: "<spring:message code='lightbox.previous'/>",
            next: "<spring:message code='lightbox.next'/>",
            autoplay: "<spring:message code='lightbox.autoplay'/>"
        };
        $.fn.lightboxFVR.getPreviewUrl = function (url) {
            var previewUrl;
            $.ajax({
                type: "GET",
                async: false,
                url: url,
                cache: false,
                error: function (request) {
                    if (!$("#lightErrorText").get(0)) {
                        $("#lightbox #loading").before('<div id="lightErrorText" style="position:absolute; width:350px; left:0; top:170px; text-align:center; color:#888;"></div>');
                    }

                    var responseObj = $.parseJSON(request.responseText);
                    switch (responseObj.code) {
                        case "FileScanning":
                            $("#lightErrorText").text("<spring:message code='file.errorMsg.fileNotReady'/>");
                            break;
                        case "ScannedForbidden":
                            $("#lightErrorText").text("<spring:message code='file.errorMsg.downloadNotAllowed'/>");
                            break;
                        default:
                            $("#lightErrorText").text("");
                            break;
                    }
                    previewUrl = "${ctx}/static/lightbox/img/img-broken.png";
                },
                success: function (data) {
                    if ($("#lightErrorText").get(0)) {
                        $("#lightErrorText").remove();
                    }
                    previewUrl = data;
                }
            });
            previewUrl += "|||${ctx}/static/lightbox/img/img-broken.png";
            return previewUrl;
        }

        $.fn.lightboxFVR.downloadImg = function (id, owner) {
            downloadFileForFavorite(owner, id);
        }

    })

    var favoriteType = ["myspace", "teamspace", "share", "link", "containor"];
    var canPreview =<%=PreviewUtils.isPreview()%>;
    function initFavoriteTree() {
        var setting = {
            data: {
                key: {
                    title: "extraAttr",
                    name: "name"
                }
            },
            async: {
                enable: true,
                url: "${ctx}/favorite/list",
                autoParam: ["id", "id"],
                otherParam: {"token": "<c:out value='${token}'/>"}
            },
            view: {
                addHoverDom: addHoverDomFavorite,
                removeHoverDom: removeHoverDomFavorite,
                selectedMulti: false
            },
            callback: {
                onAsyncSuccess: favoriteTreeOnAsyncSuccess,
                onClick: favoriteTreeClick
            }

        };
        var zNodes = [{
            id: "0",
            name: "<spring:message code='common.favorite.my'/>",
            open: true,
            isParent: true,
            iconSkin: "favorite"

        }];

        $.fn.zTree.init($("#favoriteArea"), setting, zNodes);
        $("#favoriteArea > li > span").click();
        $("#favoriteArea").toggle();
    }

    function favoriteTreeClick(event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        treeObj.expandNode(treeNode, true, false, false);

        if ((event.target.id) != null && (event.target.id) != "") {
            if (treeNode.isParent != true) {
                if (treeNode.type == "link" || treeNode.nodeType == "folder") {
                    locationUrL(treeNode);
                } else if (treeNode.nodeType == "file") {
                    var url;
                    if (treeNode.type == "myspace" || treeNode.type == "teamspace" || treeNode.type == "share") {
                        var fileType = _getStandardType(treeNode.name);
                        var videoType = treeNode.name.substring(treeNode.name.lastIndexOf(".") + 1).toLowerCase();
                        if (canPreview && treeNode.previewable) {
                            url = "${ctx}/files/gotoPreview/" + treeNode.ownedBy + "/" + treeNode.iNodeId + "?" + Math.random() + "&parentPageType=" + treeNode.type;
                            previewFavorite(url);
                        } else if (fileType != "img") {
                            downloadFileForFavorite(treeNode.ownedBy, treeNode.iNodeId);
                        }
                    }
                }
            }
        }
    }
    function favoriteTreeOnAsyncSuccess(event, treeId, treeNode, msg) {

        if (treeNode.id == 0 && treeNode.children[0].id == undefined && typeof (msg) == 'string' && msg.indexOf('<html>') != -1) {
            window.location.href = "${ctx}/logout";
            return;
        }

        if (treeNode.children) {
            var zTree = $.fn.zTree.getZTreeObj("favoriteArea");
            var child = treeNode.children;
            for (var i = 0; i < child.length; i++) {
                var aObj = $("#" + child[i].tId + "_a"),
                    spanObj = $("#" + child[i].tId + "_span");
                if (child[i].nodeType == "file") {
                    var fileType = _getStandardType(child[i].name);
                    var videoType = child[i].name.substring(child[i].name.lastIndexOf(".") + 1).toLowerCase();
                    if (fileType == "doc") {
                        child[i].iconSkin = "fileWORD";
                    } else if (fileType == "ppt") {
                        child[i].iconSkin = "filePPT";
                    } else if (fileType == "xls") {
                        child[i].iconSkin = "fileEXCEL";
                    } else if (fileType == "img") {
                        child[i].iconSkin = "fileIMG";
                        if (child[i].type != "link") {
                            aObj.attr("rel", "favoriteImg|/|${ctx}/files/getDownloadUrl/" + child[i].ownedBy + "/" + child[i].iNodeId + "?" + Math.random() + "|/|" + child[i].name + "|/|" + child[i].iNodeId + "|/|" + child[i].ownedBy);
                            aObj.attr("onclick", "$(this).lightboxFVR()");
                        }
                    } else if (fileType == "music") {
                        child[i].iconSkin = "fileMUSIC";
                    } else if (fileType == "video") {
                        child[i].iconSkin = "fileVIDEO";
                        if (videoType == "mp4") {

                        }
                    } else if (fileType == "pdf") {
                        child[i].iconSkin = "filePDF";
                    } else if (fileType == "txt") {
                        child[i].iconSkin = "fileTXT";
                    } else if (fileType == "rar") {
                        child[i].iconSkin = "fileRAR";
                    } else if (fileType == "zip") {
                        child[i].iconSkin = "fileZIP";
                    } else if (fileType == "default") {
                        child[i].iconSkin = "fileDEFAULT";
                    }
                } else if (child[i].nodeType == "folder") {
                    child[i].iconSkin = "fileFOLDER";
                }
                if (child[i].name == null || child[i].name == "") {
                    child[i].name = child[i].nodeName;
                }

                spanObj.addClass("favorite-tree-node-name");

                zTree.updateNode(child[i]);

            }
        }
    };
    function favoriteTreeOnAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
        if (typeof (XMLHttpRequest) == 'string'
            && data.indexOf('<html>') != -1) {
            window.location.href = "${ctx}/logout";
            return;
        }
    }

    function addHoverDomFavorite(treeId, treeNode) {
        if (treeNode.id == 0 || treeNode.parentId == 0) {
            return;
        }
        var aObj = $("#" + treeNode.tId + "_a");
        if ($("#delete_" + treeNode.id).length > 0)
            return;
        var editStr = "";
        if (treeNode.nodeType == "file" && treeNode.type != "link") {
            editStr = editStr
                + "<span class='button ico_open' id='parent_"
                + treeNode.id
                + "' title='"
                + "<spring:message code='favorite.title.enter'/>"
                + "' onfocus='this.blur();'></span>";

        }

        editStr = editStr
            + "<span class='button remove' id='delete_"
            + treeNode.id
            + "' title='"
            + "<spring:message code='favorite.title.delete'/>"
            + "' onfocus='this.blur();'></span>";

        aObj.append(editStr);
        var btn_delete = $("#delete_" + treeNode.id);
        var btn_openParent = $("#parent_" + treeNode.id);
        if (btn_delete) {
            btn_delete.bind("click", function () {
                deleteTreeNode(treeNode);
                return false;
            });
        }
        if (btn_openParent) {
            btn_openParent.bind("click", function () {
                openParent(treeNode);
                return false;
            });
        }
    };

    function reload_favoriteTree(treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj("favoriteArea");
        treeObj.reAsyncChildNodes(treeNode, "refresh");
    }
    function deleteTreeNode(treeNode) {
        var node = treeNode.getParentNode();
        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/delete",
            data: {
                "id": treeNode.id,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {
                top.ymPrompt.close();
                if (request.status == 404) {
                    top
                        .handlePrompt("error",
                            "<spring:message code='favorite.error.notfound'/>");
                } else {
                    top
                        .handlePrompt("error",
                            "<spring:message code='operation.failed'/>");
                }
            },
            success: function () {
                reload_favoriteTree(node);
            }
        });
    };

    function openParent(treeNode) {
        var json = {
            "type": treeNode.type,
            "ownedBy": treeNode.ownedBy,
            "iNodeId": treeNode.iNodeId,
            "nodeType": treeNode.nodeType,
            "token": "<c:out value='${token}'/>"

        }
        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/getParentUrl",
            data: json,
            error: function (request) {
                top.ymPrompt.close();
                if (request.status == 404 || (request.responseText) == "NoSuchFile") {
                    top
                        .handlePrompt("error",
                            "<spring:message code='favorite.error.notfound'/>");
                } else {
                    top
                        .handlePrompt("error",
                            "<spring:message code='operation.failed'/>");
                }
            },
            success: function (request) {
                window.location = "${ctx}" + request;
            }
        });
    }
    function locationUrL(treeNode) {
        var openUrl;
        var json = {
            "type": treeNode.type,
            "nodeType": treeNode.nodeType,
            "ownedBy": treeNode.ownedBy,
            "iNodeId": treeNode.iNodeId,
            "linkCode": treeNode.linkCode,
            "token": "<c:out value='${token}'/>"
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/locationUrL",
            data: json,
            async: false,
            error: function (request) {
                top.ymPrompt.close();
                if (request.status == 404 || (request.responseText) == "NoSuchFile") {
                    top
                        .handlePrompt("error",
                            "<spring:message code='favorite.error.notfound'/>");
                } else {
                    top
                        .handlePrompt("error",
                            "<spring:message code='operation.failed'/>");
                }
            },
            success: function (request) {
                openUrl = request;
            }
        });
        if (treeNode.type == "link") {
            jQuery('<form action="${ctx}' + openUrl + '" method="get" target="_blank"></form>').appendTo('body').submit().remove();
        } else {
            window.location = "${ctx}" + openUrl;
        }

    }
    function removeHoverDomFavorite(treeId, treeNode) {
        $("#parent_" + treeNode.id).unbind().remove();
        $("#parent_space_" + treeNode.id).unbind().remove();
        $("#delete_" + treeNode.id).unbind().remove();
        $("#delete_space_" + treeNode.id).unbind().remove();
    };

    function downloadFileForFavorite(ownerIdTmp, nodeIdTmp) {
        var url = "${ctx}/files/getDownloadUrl/" + ownerIdTmp + "/" + nodeIdTmp + "?" + Math.random();
        $.ajax({
            type: "GET",
            async: false,
            url: url,
            error: function (request) {
                downloadFileErrorHandler(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>')
                    .appendTo('body').submit().remove();
            }
        });
    }
    function createFavorite(type, ownedBy, iNodeId, nodeType, name, parentId, objId) {
        var json = {
            "type": type,
            "parentId": parentId,
            "ownerId": ownedBy,
            "nodeId": iNodeId,
            "nodeType": nodeType,
            "name": name,
            "token": "<c:out value='${token}'/>"
        }

        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/create",
            data: json,
            error: function (request) {
                top.ymPrompt.close();
                if ((request.responseText) == ("ExistFavoriteConflict")) {
                    top.handlePrompt("error",
                        "<spring:message code='favorite.add.exist'/>");
                } else {

                    top.handlePrompt("error",
                        "<spring:message code='operation.failed'/>");
                }
            },
            success: function (data) {
                if (typeof (data) == 'string'
                    && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top
                    .handlePrompt("success",
                        "<spring:message code='operation.success'/>");
                var zTree = $.fn.zTree.getZTreeObj("favoriteArea");
                if (null != zTree) {
                    zTree.reAsyncChildNodes(zTree.getNodes()[0],
                        "refresh");

                }

            }
        });

        if (objId == undefined) {
            favoriteAnimate(iNodeId);
        } else {
            favoriteAnimate(objId);
        }
    }

    function favoriteAnimate(nodeId) {
        var oldNode = $("#" + nodeId).parents(".rowli").find(">div"),
            node = oldNode.clone(),
            scrollT = document.documentElement.scrollTop || document.body.scrollTop,
            scrollL = document.documentElement.scrollLeft || document.body.scrollLeft,
            startL = oldNode.offset().left - scrollL,
            startT = oldNode.offset().top - scrollT,
            coefficient = 0.25,
            speed = 300,
            iconL = $("#favoriteTree").offset().left - scrollL,
            iconT = $("#favoriteTree").offset().top - scrollT,
            distance = Math.pow(((iconL - startL) * (iconL - startL) + (startT - iconT) * (startT - iconT)), 0.5);
        node.css({"position": "fixed", "top": startT, "left": startL, "z-index": 9999});
        oldNode.parent().append(node);
        node.animate({
            "left": iconL - 0.5 * $(node).width(),
            "top": iconT - 0.5 * $(node).height(),
            "opacity": 0.3
        }, parseInt(speed + coefficient * distance), function () {
            node.remove();
            var favoDiv = $('<div class="favoriteDiv"></div>');
            $(".header .favorite-con").append(favoDiv);
            favoDiv.animate({"opacity": 0}, 1000, function () {
                favoDiv.remove();
            });
        });
    }

    function previewFavorite(url) {
        window.open(url);
    }

</script>

