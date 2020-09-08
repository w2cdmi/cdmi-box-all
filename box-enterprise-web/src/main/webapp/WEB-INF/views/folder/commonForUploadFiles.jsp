<%@ page contentType="text/html;charset=UTF-8" %>
<HEAD>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
</HEAD>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<script src="${ctx}/static/uploadify/jquery.uploadify.js" type="text/javascript"></script>

<%-- IE10+ --%>
<script src="${ctx}/static/jQueryFileUpload/jquery.ui.widget.js" type="text/javascript"></script>
<script src="${ctx}/static/jQueryFileUpload/jquery.fileupload.js" type="text/javascript"></script>
<script src="${ctx}/static/jQueryFileUpload/jquery.fileupload-process.js" type="text/javascript"></script>
<script src="${ctx}/static/jQueryFileUpload/jquery.fileupload-validate.js" type="text/javascript"></script>
<%-- end --%>

<%-- pop uploadfile --%>
<div class="modal hide" id="uploadModal">
    <div class="modal-header">
        <h3><spring:message code="file.index.uploadList"/>(<span id="showUploadedNum">0</span>/<span
                id="showUploadTotalNum">0</span>)</h3>
    </div>
    <div class="modal-body">
        <div id="uploadQueue"></div>
    </div>
</div>

<%-- pop uploadfile failure --%>
<div class="modal hide" id="uploadFileFail" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3><spring:message code='file.title.uploadFailed'/></h3>
    </div>
    <div class="modal-body">
        <div class='upload-error-title' id="uploadFileFailTitle"></div>
        <div class='upload-error-list' id="uploadFileFailList"></div>
        <div class='upload-error-descript'><spring:message code='file.errorMsg.failedMsg'/></div>
    </div>
</div>

<%-- file upload dropzone --%>
<div id="dropzone" class="dropzone"></div>
<div id="dropzoneBlock" class="dropzone-block"></div>
<div id="dropzoneText" class="dropzone-text"><spring:message code="common.howtodragupload"/></div>


<script type="text/javascript">
    var uploadMap = new HashMap();
    var uploadErrorFiles = new Array();
    var isUpload = false;

    $(function () {

        window.onbeforeunload = function () {
            if (isUpload == true) {
                var ev = arguments[0] || window.event;
                ev.returnValue = '<spring:message code="file.uploading.leave"/>';
            }
        }
    });

    //for sendUpLoad
    function sendUpLoad(tokenType) {
        $(document).bind("drop dragover", function (e) {
            e.preventDefault();
        });

        var preUploadUrl = "";
        if (tokenType == null || tokenType == "") {
            preUploadUrl = "${ctx}/files/preUpload";
        } else if (tokenType == "link") {
            preUploadUrl = "${ctx}/share/link/preUpload" + "?linkCode=" + linkCode;
        }


        //-----For IE------
        if (!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0) {
            $("#uploadBtnBoxForJS").remove();

            $(document).bind("drop", function (e) {
                handlePrompt("error", "<spring:message code='common.notsupportdragupload'/>");
            });


            $("#fileUpload").uploadify({
                auto: true,
                multi: true,
                buttonClass: "",
                width: 60,
                height: 28,
                successTimeout: 120,
                removeTimeout: 1,
                queueID: 'uploadQueue',
                swf: "${ctx}/static/uploadify/uploadify.swf",
                buttonText: "<spring:message code='button.uploadFile'/>",
                uploader: '${ctx}/static/uploadify/uploadify.swf',
                fileSizeLimit: '2047MB',
                queueSizeLimit: 100,
                onSelect: function (file) {
                    $("#uploadModal").show();
                    var selectedTotalNum = parseInt($("#showUploadTotalNum").text());
                    $("#showUploadTotalNum").text(parseInt(selectedTotalNum + 1));
                    uploadMap.put(file.id, catalogParentId);
                },
                overrideEvents: ['onDialogClose', 'onUploadError', 'onSelectError'],
                onUploadStart: function (file) {
                    //Jeffrey----
                    isUpload = true;
                    var uploadParentId = uploadMap.get(file.id);
                    uploadMap.remove(file.id);

                    $.ajax({
                        type: "POST",
                        url: preUploadUrl,
                        async: false,
                        data: {
                            "ownerId": ownerId,
                            "parentId": uploadParentId,
                            "name": file.name,
                            "size": file.size,
                            "token": "<c:out value='${token}'/>"
                        },
                        success: function (data, textStatus, jqXHR) {
                            if (data.length < 10 || data.length > 500) {
                                isSessionTimeout = true;
                                ymPrompt.alert({
                                    title: "<spring:message code='common.tip'/>",
                                    message: "<spring:message code='error.timeout.retry'/>",
                                    handler: function () {
                                        window.top.location.reload();
                                    }
                                });
                            } else {
                                //request serverUrl
                                var reqServer = "${pageContext.request.getServerName()}";
                                var temData = data.substring(data.indexOf("\/") + 2, data.length - 1),
                                    //upload Ip
                                    uploadIp = temData.substring(0, temData.indexOf(":"));
                                if (uploadIp === "") {
                                    uploadIp = temData;
                                }

                                if (reqServer != uploadIp) {
                                    data = data.replace(uploadIp, reqServer);
                                }
                                var url = data + "?objectLength=" + file.size;
                                $("#fileUpload").uploadify("settings", "uploader", url);
                            }
                        },
                        error: function (request) {
                            var status = request.status;
                            if (status == 403) {
                                handlePrompt("error", "<spring:message code='error.forbid'/>");
                            } else {
                                uploadErrorFiles.push(file.name);
                            }
                            $("#fileUpload").uploadify("cancel", file.id);
                        }
                    });
                },
                onSelectError: function (file, errorCode, errorMsg) {
                    var settings = this.settings;
                    var stats = this.getStats();
                    var isMutiple = stats.files_queued > 0 ? true : false;
                    var addToErrorList = true;
                    var errorMsg = "";
                    var sizeMax = "2GB";
                    switch (errorCode) {
                        case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
                            errorMsg = "<spring:message code='file.errorMsg.queueLimitExceeded' arguments='" + this.settings.queueSizeLimit + "'/>";
                            addToErrorList = false;
                            break;
                        case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
                            errorMsg = "<spring:message code='file.errorMsg.fileSizeExceeded' arguments='" + file.name + "," + sizeMax + "'/>";
                            break;
                        case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
                            if (!file.size && file.size != 0) {
                                errorMsg = "<spring:message code='file.errorMsg.fileSizeExceeded' arguments='" + file.name + "," + sizeMax + "'/>";
                            } else {
                                errorMsg = "<spring:message code='file.errorMsg.fileSizeZero' arguments='" + file.name + "'/>";
                            }
                            break;
                        default:
                            errorMsg = "<spring:message code='file.errorMsg.uploadFailed'/>";
                    }
                    if (isMutiple && addToErrorList) {
                        uploadErrorFiles.push(file.name);
                        return;
                    }
                    handlePrompt("error", errorMsg);
                },
                onFallback: function () {
                    handlePrompt("error", "<spring:message code='file.errorMsg.flashNotChecked'/>");
                    $("#uploadBtnBox").remove();
                },
                onCancel: function (file) {
                    var selectedTotalNum = parseInt($("#showUploadTotalNum").text());
                    $("#showUploadTotalNum").text(parseInt(selectedTotalNum - 1));
                },
                onUploadError: function (file, errorCode, errorMsg, errorString) {
                    if (errorCode != SWFUpload.UPLOAD_ERROR.FILE_CANCELLED) {
                        uploadErrorFiles.push(file.name);
                    }
                    $("#fileUpload").uploadify("cancel", file.id);
                },
                onUploadSuccess: function (file, data, response) {
                    var num = parseInt($("#showUploadedNum").text());
                    $("#showUploadedNum").text(num + 1);
                },
                onUploadComplete: function (file) {
                    isUpload = false;
                },
                onQueueComplete: function (file) {
                    $("#menuID").css("display", "none");
                    var numDiv = $("#uploadQueue > div").length;
                    setTimeout(function () {
                        if (isUpload == false && isFolderIsUploading == false) {
                            $("#uploadModal").hide();
                            $("#showUploadedNum, #showUploadTotalNum").text("0");
                        }
                    }, 1000);
                    var lalala = uploadFolderFail.values();
                    if (uploadErrorFiles.length != 0 || uploadFolderFail.size() != 0) {
                        var fileList = "";
                        for (var i = 0; i < uploadErrorFiles.length; i++) {
                            fileList += uploadErrorFiles[i] + "<br />";
                        }
                        for (var i = 0; i < lalala.length; i++) {
                            if (lalala[i] != "" && lalala[i] != null) {
                                fileList += lalala[i] + "<br />";
                            }
                        }
                        $("#uploadFileFailTitle").html("<spring:message code='file.errorMsg.uploadErrors' arguments='" + uploadErrorFiles.length + "'/>");
                        $("#uploadFileFailList").html(fileList);
                        $("#uploadFileFail").modal('show');
                        uploadErrorFiles = [];
                    }
                    listFile(currentPage, catalogParentId);
                }
            });
        } else {
            //=============For none-IE Browsers=============
            //marked the operation is drag or not
            var isDrag = false;
            $("#uploadBtnBox").remove();
            $(document).bind("dragover", function (e) {
                isDrag = true;
                $("#dropzone, #dropzoneText, #dropzoneBlock").show();
                var ev = arguments[0] || window.event;
                var srcElement = ev.srcElement || ev.target;
                $(document).bind("dragleave", function (e) {
                    var ev_l = arguments[0] || window.event;
                    var srcElement_l = ev_l.srcElement || ev_l.target;
                    if ($(srcElement_l).attr("id") == "dropzoneBlock" && $(srcElement).attr("id") != "dropzoneText") {
                        $("#dropzoneText, #dropzone, #dropzoneBlock").hide();
                    }
                })
            });

            $(document).bind("drop", function (e) {
                $("#dropzone, #dropzoneText,#dropzoneBlock").hide();
            });
            $("#dropzoneBlock").hover(function () {
                $("#dropzone, #dropzoneText,#dropzoneBlock").hide();
            })

            var filesTotal = 0,
                filesUploaded = 0,
                fileIdIndex = 0,
                complateUpload = function () {
                    var num = $("#uploadQueue > div").length;
                    if (uploadErrorFiles.length == (filesTotal - filesUploaded)) {
                        if (num == 0) {
                            $("#uploadModal").hide();
                            $("#showUploadedNum, #showUploadTotalNum").text("0");
                        }
                        isUpload = false;
                        filesTotal = 0;
                        filesUploaded = 0;
                        fileIdIndex = 0;
                        listFile(currentPage, catalogParentId);

                        var lalala = uploadFolderFail.values();
                        if (uploadErrorFiles.length != 0 || uploadFolderFail.size() != 0) {
                            var fileList = "";
                            for (var i = 0; i < uploadErrorFiles.length; i++) {
                                fileList += uploadErrorFiles[i] + "<br />";
                            }
                            for (var i = 0; i < lalala.length; i++) {
                                if (lalala[i] != "" && lalala[i] != null) {
                                    fileList += lalala[i] + "<br />";
                                }
                            }
                            $("#uploadFileFailTitle").html("<spring:message code='file.errorMsg.uploadErrors' arguments='" + uploadErrorFiles.length + "'/>");
                            $("#uploadFileFailList").html(fileList);
                            $("#uploadFileFail").modal('show');
                            uploadErrorFiles = [];
                        }
                    }
                };

            $("input[type=file]").fileupload({
                dataType: "json",
                autoUpload: false,
                dropZone: $("#dropzoneText"),
                sequentialUploads: true,
                limitConcurrentUploads: 1,
                maxFileSize: 2147483648,
                minFileSize: 1
            }).on("fileuploadchange", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                $("#menuID").css("display", "none");
                if (data.files.length + (filesTotal - filesUploaded) > 100) {
                    data.files = [];
                    handlePrompt("error", "<spring:message code='file.errorMsg.queueLimitExceeded' arguments='100'/>");
                }

                for (var i = 0; i < data.files.length; i++) {
                    data.files[i].id = fileIdIndex + i;
                }
                fileIdIndex += data.files.length;
            }).on("fileuploaddrop", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                if (data.files.length + (filesTotal - filesUploaded) > 100) {
                    data.files = [];
                    handlePrompt("error", "<spring:message code='file.errorMsg.queueLimitExceeded' arguments='100'/>");
                }
                for (var i = 0; i < data.files.length; i++) {
                    data.files[i].id = fileIdIndex + i;
                }
                fileIdIndex += data.files.length;
            }).on("fileuploadadd", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                $("#uploadModal").show();
                data.context = $("<div/>").addClass("fileUpload-queue-item").appendTo("#uploadQueue");
                $.each(data.files, function (index, file) {
                    var node = $("<div/>")
                        .append($("<button/>")
                            .addClass("close pull-right")
                            .html("&times;")
                            .on("click", function () {
                                $(this).attr("disabled", "disabled").parent().parent().remove();
                                data.abort();

                                $("#uploadQueue").find(".submit").eq(0).click();

                                filesTotal--;
                                $("#showUploadTotalNum").text(parseInt($("#showUploadTotalNum").text()) - 1);
                            }))
                        .append($("<button/>")
                            .addClass("submit pull-right")
                            .on("click", function () {
                                data.submit();
                            }))
                        .append($("<span/>").addClass("title").text(file.name))
                        .append($("<span/>").addClass("size").text(" (" + formatFileSize(file.size) + ") - "))
                        .append($("<span/>").addClass("info").text("0%"));
                    var nodeprocess = $("<div/>").addClass("progress progress-info")
                        .append($("<div/>").addClass("bar"));

                    node.appendTo(data.context);
                    nodeprocess.appendTo(data.context);
                    uploadMap.put(file.id, catalogParentId);
                });

                filesTotal++;
                $("#showUploadTotalNum").text(parseInt($("#showUploadTotalNum").text()) + 1);

            }).on("fileuploadsubmit", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                var file = data.files[0],
                    uploadParentId = uploadMap.get(file.id);
                uploadMap.remove(file.id);
                if (!isIEBrowser() && !isFirFoxBrowser() && file.webkitRelativePath.indexOf("/") > 0) {
                    var preUploadForDirUrl = "${ctx}/uploadFolder/dirPreupload";
                    var reqData = {};
                    //For ��IE�������ļ����ϴ�
                    if (tokenType === "link") {
                        reqData = {
                            "ownerId": ownerId,
                            "parentId": uploadParentId,
                            "name": file.webkitRelativePath,
                            "size": file.size,
                            "linkCode": linkCode
                        };
                    } else {
                        reqData = {
                            "ownerId": ownerId,
                            "parentId": uploadParentId,
                            "name": file.webkitRelativePath,
                            "size": file.size
                        };
                    }

                    $.ajax({
                        type: "POST",
                        url: preUploadForDirUrl,
                        async: false,
                        data: reqData,
                        success: function (urlData, textStatus, jqXHR) {
                            if (urlData.length < 10 || urlData.length > 500) {
                                isSessionTimeout = true;
                                ymPrompt.alert({
                                    title: "<spring:message code='common.tip'/>",
                                    message: "<spring:message code='error.timeout.retry'/>",
                                    handler: function () {
                                        window.top.location.reload();
                                    }
                                });
                            } else {
                                var url = urlData + "?objectLength=" + file.size;
                                $("#dirUpload0").fileupload("option", "url", url);
                            }
                        },
                        error: function (request) {
                        }
                    });
                } else {
                    $.ajax({
                        type: "POST",
                        url: preUploadUrl,
                        async: false,
                        data: {
                            "ownerId": ownerId,
                            "parentId": uploadParentId,
                            "name": file.name,
                            "size": file.size,
                            "token": "<c:out value='${token}'/>"
                        },
                        success: function (urlData, textStatus, jqXHR) {
                            if (urlData.length < 10 || urlData.length > 500) {
                                isSessionTimeout = true;
                                ymPrompt.alert({
                                    title: "<spring:message code='common.tip'/>",
                                    message: "<spring:message code='error.timeout.retry'/>",
                                    handler: function () {
                                        window.top.location.reload();
                                    }
                                });
                            } else {
                                var url = urlData + "?objectLength=" + file.size;
                                $("#fileUpload").fileupload("option", "url", url);
                            }
                        },
                        error: function (request) {
                        }
                    });
                }
            }).on("fileuploadprocessalways", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                isUpload = true;
                var index = data.index,
                    file = data.files[index],
                    nodeCon = $(data.context);
                if (file.error) {
                    uploadErrorFiles.push(file.name);
                    nodeCon.find(".submit").remove().end().find(".info").addClass("error").text("<spring:message code='file.title.uploadFailed'/>");
                    setTimeout(function () {
                        nodeCon.fadeOut(300, function () {
                            $(this).remove();
                            complateUpload();
                        })
                    }, 2000);
                }
                $("#uploadQueue").find(".submit").eq(0).click();
            }).on("fileuploadprogress", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                var nodeCon = $(data.context);
                var progress = parseInt(data.loaded / data.total * 100, 10);
                nodeCon.find(".info").text(progress + "%");
                nodeCon.find(".bar").css("width", progress + "%");
            }).on("fileuploaddone", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                var nodeCon = $(data.context);
                nodeCon.find(".info").text("<spring:message code='uploadify.msg.complete'/>");
                nodeCon.find(".close").attr("disabled", "disabled");
                nodeCon.find(".submit").remove();
                nodeCon.parent().find(".submit").eq(0).click();
                filesUploaded++;

                if (isFolderIsUploading) {
                    var curUploading = $("#showUploadedNum").text();
                    $("#showUploadedNum").text(filesUploaded + parseInt(curUploading));
                }
                else {
                    $("#showUploadedNum").text(filesUploaded);
                }
                setTimeout(function () {
                    nodeCon.fadeOut(300, function () {
                        $(this).remove();
                        complateUpload();
                    })
                }, 2000);

                isDrag = false;
            }).on("fileuploadfail", function (e, data) {
                if (e && isInvalidForDrag(e.currentTarget.id) && isDrag) {
                    return;
                }
                var nodeCon = $(data.context),
                    file = data.files[0];

                if ((data.textStatus != "abort") && (data.textStatus != undefined)) {
                    uploadErrorFiles.push(file.name);
                }
                nodeCon.find(".info").addClass("error").text("<spring:message code='file.title.uploadFailed'/>");
                nodeCon.find(".bar").css("width", "0%");

                nodeCon.find(".submit").remove();
                nodeCon.parent().find(".submit").eq(0).click();

                setTimeout(function () {
                    nodeCon.fadeOut(300, function () {
                        $(this).remove();
                        complateUpload();
                    })
                }, 2000);
            }).prop("disabled", !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : "disabled");
        }

        /* if (showPc) {
            var flagUploadInfo = getCookie("isShowUploadInfo");
            if (flagUploadInfo != "neverShow") {
                $(".upload-btn-box").popover({
                    container: ".upload-btn-box", html: true, trigger: "hover",
                    content: "<spring:message code='file.warn.upload.maxsize'/><a href='javascript:downloadClient()'><spring:message code='common.message.clickToDownload'/></a> <div class='btn-con'><button class='btn btn-mini' onclick='neverShowUploadInfo()' type='button'><spring:message code='common.notprompt'/></button></div>",
                    animation: false
                });
            }
            $(".upload-btn-box").hover(function () {
                if ($(".upload-btn-box .popover").get(0)) {
                    $(".public-bar-con").css("z-index", "9991");
                } else {
                    $(".public-bar-con").css("z-index", "9980");
                }
            })
        } */
    }

    function isInvalidForDrag(val) {
        if (val && val === "dirUpload0") {
            return true;
        }
        return false;
    }


    function downloadClient() {
        if (pcClientDownloadUrl == "") {
            handlePrompt("error", "<spring:message code='appindex.client.not.publish'/>");
        } else {
            location.href = pcClientDownloadUrl;
        }

    }

    function neverShowUploadInfo() {
        setCookie("isShowUploadInfo", "neverShow");
        $(".upload-btn-box").popover("destroy");
    }

    function HashMap() {
        var size = 0;
        var entry = new Object();

        this.put = function (key, value) {
            if (!this.containsKey(key)) {
                size++;
            }
            entry[key] = value;
        }

        this.get = function (key) {
            if (this.containsKey(key)) {
                return entry[key];
            }
            else {
                return null;
            }
        }

        this.remove = function (key) {
            if (delete entry[key]) {
                size--;
            }
        }

        this.containsKey = function (key) {
            return (key in entry);
        }

        this.containsValue = function (value) {
            for (var prop in entry) {
                if (entry[prop] == value) {
                    return true;
                }
            }
            return false;
        }

        this.values = function () {
            var values = new Array(size);
            for (var prop in entry) {
                values.push(entry[prop]);
            }
            return values;
        }

        this.keys = function () {
            var keys = new Array(size);
            for (var prop in entry) {
                keys.push(prop);
            }
            return keys;
        }

        this.size = function () {
            return size;
        }
    }
</script>
