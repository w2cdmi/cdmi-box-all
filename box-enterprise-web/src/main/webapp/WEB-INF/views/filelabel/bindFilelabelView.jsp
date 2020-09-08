<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../common/messages.jsp" %>
    <link href="${ctx}/static/skins/default/css/filelabel.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/filelabel/filelabel.admin.js?sid=999" type="text/javascript"></script>
</head>

<body>
<div class="pop-content bind-fl-view" style="display: block;padding: 4px;">
    <form class="form-horizontal label-w100" id="creatTeamSpaceForm">

        <div class="control-group fl-header">
            <label class="control-label fl-header-label">
                <spring:message code="fl.bind.view.opt.obj"/>
            </label>
            <div class="controls fl-header-body">
                <div class="file-name fl-header-content">
                    <div class="file-icon fl-header-icon inline-block"></div>
                    <div class="file-name-desc inline-block fl-header-desc"></div>
                </div>
            </div>
        </div>

        <div class="control-group fl-body">
            <div class="marks" style="display: block;">
                <div class="create-mark">
                    <div class="title">
							<span class="title-name">
								<spring:message code="fl.bind.view.tag.name"/>&nbsp;&nbsp; 
							</span>
                    </div>

                    <div class="labels-input">
                        <ul class="file-label-list labels-list-ul inline-block">
                        </ul>

                        <div class="label-input">
                            <input type="text" name="fileLabelName" id="fileLabelName" class="marks-input"
                                   placeholder="<spring:message code='fl.bind.view.input.tip'/>" maxlength="10">&nbsp;
                            <input type="hidden" name="fileLabelId" id="fileLabelId" value="0">
                            <span class="btn btn-blue confirmAdd"><spring:message code="button.ok"/>
								</span>&nbsp;
                        </div>
                    </div>
                </div>

                <div class="latest-label-list">
                    <div class="title">
                        <spring:message code="fl.bind.view.latest.view.tip"/>:&nbsp;
                        <ul class="latest-label-ul">

                        </ul>
                    </div>
                </div>

                <div class="mark-list">
                    <div class="title">
                        <spring:message code="fl.bind.view.lib.tag"/>:&nbsp;<span class="light"></span>
                        <div class="input-append">
                            <input type="text" name="vagueFilelabelName" id="vagueFilelabelName"
                                   class="marks-input search-filelabel"
                                   placeholder="<spring:message code='fl.bind.search.label.tip'/>" maxlength="10">&nbsp;
                            <button class="btn search-filelabel-btn confirmSearch" style="border-color:#bbbbbb;"
                                    title="<spring:message code='button.search'/>">
                                <i class="icon-search"></i>
                            </button>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="span11">
                            <ul class="marks-list-ul labels-list-ul">
                            </ul>
                        </div>

                        <div class="span1">
                            <a class="expand-btn" onclick="queryEnterpriseFileLabel()">
									<span title="<spring:message code='fl.bind.view.changepage.btn'/>">
										<spring:message code='fl.bind.view.changepage.btn'/>
									</span>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <input type="hidden" id="ownerId" name="ownerId" value="${ownerId}"/>
        <input type="hidden" id="labelId" name="labelId" value="${labelId}"/>
        <input type="hidden" id="nodeId" name="nodeId" value="${nodeId}"/>
        <input type="hidden" id="token" name="token" value="${token}"/>
    </form>
</div>
</body>

<script type="text/javascript">
    var vownerId = $('#ownerId').val();
    var vnodeId = $('#nodeId').val();
    var vtoken = $('#token').val();
    var viewModel = parent.viewModel;
    var viewType = parent.viewType;
    var vcurrLibPage = 0;
    var filelabelAdmin = new FilelabelAdmin(vownerId, viewType, vtoken);
    var intervalVal = null;
    var vagueName = '';

    $(document).ready(function () {
        initFileInfo();
        queryEnterpriseFileLabel();
        searchUserLatestViewedLabels();

        $("#bindFilelabelForm").validate({
            rules: {
                labelName: {
                    required: true,
                    rangelength: [1, 255]
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='fl.filed.labelname.input.required.tip'/>"
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });

        $(".creteLab").bind('click', function () {
            $('.label-input').toggle();
        });

        $('.confirmCancel').bind('click', function () {
            if ($('.label-input').is(":visible")) {
                $('#fileLabelName').val("");
                $('#fileLabelId').val("");
                $('.label-input').hide();
            }
        });

        $('.confirmAdd').bind('click', function () {
            bindFilelabel($("#fileLabelId").val(), $('#fileLabelName').val());
            return false;
        });

        $('.confirmSearch').on('click', function () {
            localSearchFilelabel();

            return false;
        });

        $('#fileLabelName').on('change', function () {
            $('#fileLabelId').val('0');
        });

        $("#fileLabelName").keydown(function (event) {
            if (event.keyCode == 13) {
                bindFilelabel($("#fileLabelId").val(), $('#fileLabelName').val());

                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        });

        $("#vagueFilelabelName").keydown(function (event) {
            if (event.keyCode == 13) {
                localSearchFilelabel();

                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        });

        $('ul.labels-list-ul').on('mouseover mouseout', 'li.label-item', function () {
            if (event.type == "mouseover") {
                $(this).find("a.delMark").show();
            } else if (event.type == "mouseout") {
                $(this).find("a.delMark").hide();
            }
        });

        function localSearchFilelabel() {
            vcurrLibPage = 0;
            vagueName = $('#vagueFilelabelName').val();
            queryEnterpriseFileLabel();
        }

        $("#fileLabelName").keydown(function (event) {
            if (event.keyCode == 13) {
                //$('.confirmAdd').trigger('click');
                bindFilelabel($("#fileLabelId").val(), $('#fileLabelName').val());
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }


        });
    });

    /** 初始化文件信息 */
    function initFileInfo() {
        $.get("${ctx}/files/getFileDetail/" + vownerId + "/" + vnodeId, function (data) {
            var fileName = data.name;
            var flList = data.fileLabelList;
            var rowType = _getStandardType(data.name);
            if (fileName.length > 45) {
                var prefixVal = fileName.substring(0, 30);
                var pointPosition = fileName.lastIndexOf(".");
                var lastVal = fileName.substring(pointPosition - 10);
                fileName = prefixVal + "..." + lastVal;
            }

            $('.fl-header-icon').addClass('fileImg-' + rowType);
            $('.file-name-desc').html(fileName);
            if (flList) {
                $('.file-label-list').empty();

                var tempLi;
                for (var index in flList) {
                    tempLi = '<li value = "' + flList[index].id + '" class="label-item" style="display: inline-block;"><span class="cs-label label-blue" title = "' + flList[index].labelName + '">' +
                        flList[index].labelName + '&nbsp;<a class="delMark" onclick="unbindFileLabel(' + flList[index].id + ')"><span class="icon-custom-del-mark"></span></a></span>&nbsp</li>';
                    $('.file-label-list').append(tempLi);
                }
            }
        });
    }

    /** 得到企业标签信息 */
    function queryEnterpriseFileLabel() {
        filelabelAdmin.searchByEnterprise("ul.marks-list-ul", vcurrLibPage + 1, 10, function (resp) {
            $(".marks-list-ul").empty();
            vcurrLibPage = resp.currPage;

            for (var index in resp.fileLabelList) {
                var tempLi = buildLabelHtml(resp.fileLabelList[index]);

                $(".marks-list-ul").append(tempLi);
            }
        }, vagueName);
    }

    /** 初始化用户最近使用的标签信息 */
    function searchUserLatestViewedLabels() {
        filelabelAdmin.searchUserLatestViewedLabels(function (resp) {
            if (resp.status == 'OK') {
                var latestLabels = resp.fileLabelList;

                if (latestLabels) {
                    $(".latest-label-ul").empty();

                    for (var index in latestLabels) {
                        var tempLi = buildLabelHtml(latestLabels[index]);
                        $(".latest-label-ul").append(tempLi);
                    }
                }
            }
        });
    }

    function buildLabelHtml(labelObj) {
        var labelElement = '<li value = "' + labelObj.id + '" onclick = "backFillLabelInfo(this)" ondblclick = "directBindFilelabel(this)" class="label-item" style="display: inline-block;"><span class="cs-label label-blue" title = "'
            + labelObj.labelName + '">' + labelObj.labelName
            + '</span>&nbsp;</li>';
        return labelElement;
    }

    /** 绑定已经存在的标签 */
    function backFillLabelInfo(obj) {
        if ($('.label-input').is(":visible")) {
            var tlabelId = $(obj).attr('value');
            var tlabelName = $(obj).find('span').text().trim();

            $('#fileLabelName').val(tlabelName);
            $('#fileLabelId').val(tlabelId);
        }
    }

    /** 直接绑定标签*/
    function directBindFilelabel(obj) {
        bindFilelabel($(obj).attr('value'), $(obj).find('span').text().trim());
    }

    /** 绑定标签*/
    function bindFilelabel(labelId, labelName) {
        if (!labelName) {
            handlePrompt("error", "<spring:message code='fl.lack.labelname'/>");
            return;
        }
        if (labelName.length > 10) {
            handlePrompt("error", "<spring:message code='fl.bind.outof.maxlength'/>");
            return;
        }
        var vflArea = $('ul.file-label-list');
        var vlabelItems = vflArea.find('li.label-item');
        var maxBindNum = viewType == '1' ? 5 : 10;
        if (vlabelItems.size() >= maxBindNum) {
            handlePrompt("error", "<spring:message code='fl.bind.outof.maxtimes'/>");
            $("#fileLabelName").val('');
            return;
        }

        var nodeId = $("#nodeId").val();
        if (vlabelItems.size() > 0) {
            for (var i = 0; i < vlabelItems.size(); i++) {
                var tempItem = vlabelItems[i];
                var tflid = $(tempItem).attr('value');
                var ttitle = $(tempItem).attr('title');

                if ((labelId && tflid == labelId) || $.trim(ttitle) == $.trim(labelName)) {
                    handlePrompt("error", "<spring:message code='fl.bind.view.tag.hasbind'/>");
                    return;
                }
            }
        }

        filelabelAdmin.bind(nodeId, labelId, labelName, function (resp) {
            handlePrompt("success", "<spring:message code='operation.success'/>");
            var tempLi = '<li value = "' + resp.labelId + '" class="label-item" style="display: inline-block;"><span class="cs-label label-blue" title = "' + resp.labelName + '" >' +
                resp.labelName + '&nbsp;<a class="delMark" onclick="unbindFileLabel(' + resp.labelId + ')"' + '><span class="icon-custom-del-mark"></span></a></span>&nbsp;</li>';

            $('.file-label-list').append(tempLi);
            $("#fileLabelName").val('');
            searchUserLatestViewedLabels();
            top.filelabelOptCallback('ADD', {nodeId: nodeId, id: labelId, name: htmlEncode(labelName)});
        });
    }

    /** 解除标签的绑定 */
    function unbindFileLabel(labelId) {
        filelabelAdmin.unbind(vnodeId, labelId, function () {
            handlePrompt("success", "<spring:message code='operation.success'/>");
            $('li[value=' + labelId + ']').remove();
            top.filelabelOptCallback('DELETE', {nodeId: vnodeId, id: labelId});
        });
    }
</script>
</html>
