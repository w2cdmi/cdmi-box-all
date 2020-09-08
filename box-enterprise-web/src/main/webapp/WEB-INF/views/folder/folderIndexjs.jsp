<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript">

    /******************************* 文件标签操作 **************************/
    var filelabelSearch = new FilelabelAdmin(ownerId, 1, '${token}');
    var currFlPageNum = 0, currFlPageSize = 10;
    var isFirstLoadEnterpriseFls = true;

    /** 针对高级搜索刷新页面的处理 */
    function beforeInit() {
        var hash = window.location.hash;

        if (hash && hash.indexOf('#') != -1) {
            var m = hash.substring(hash.indexOf("#") + 1);
            if (!m || m == 'none') {
                return;
            }

            m = m.split('/');
            viewMode = m[0];

            if (viewMode == "highSearch") {
                showHighSearchView();
            }
        } else {
            delCookie("beforeHighSearchView");
            delCookie('beforeRefreshFilelabelPage');
        }
    }

    /** 初始化高级搜索中的文档类型 */
    function inintHighSearchDocType() {
        filelabelSearch.searchDocTypes(function (data) {
            if (data) {
                var docTypes = data.docUserConfigs;
                var pipeSpan = '<span class="pipe">|</span>';
                var doctypeLen = docTypes.length;
                var conditionArea = $('div.high-search-type');

                for (var index = 0; index < doctypeLen; index++) {
                    var tempDocType = docTypes[index];
                    var tempDocTypeName = transDocType(tempDocType.name);
                    var tempSpanEl = $('<span class="cs-label label-trans-blue common-type"  value = "' +
                        tempDocType.id + '">' + tempDocTypeName + '</span>');

                    var thashDocTypeId = addActiveClassIfNeeded('docType');
                    if (thashDocTypeId) {
                        if (tempDocType.id == thashDocTypeId) {
                            var beappendElement = buildSelectedDoctype(tempDocType.id, tempDocTypeName);

                            tempSpanEl.addClass('active');
                            $(beappendElement).addClass('active');
                            conditionArea.find("span.selected-type[type=docType]").remove();
                            conditionArea.append(beappendElement);
                        }
                    }

                    $('.search-type-item').append(tempSpanEl);
                    if (index != (doctypeLen - 1)) {
                        $('.search-type-item').append(pipeSpan);
                    }
                }

                $('.search-type-item').unbind('click').on('click', 'span.common-type', chooseDocType);
            }
        });
    }

    /** 选择文档类型事件处理  */
    function chooseDocType() {
        var vthis = $(this);
        var conditionArea = $('div.high-search-type');
        vdocId = vthis.attr("value");
        vdocName = vthis.text();

        if (vthis.hasClass('active')) {
            vthis.removeClass('active');
            $('span.selected-type[value=' + vdocId + '][type=docType]').removeClass('active').remove();
        } else {
            var tempSpan = buildSelectedDoctype(vdocId, vdocName);

            $('span.common-type').removeClass("active");
            conditionArea.find("span.selected-type[type=docType]").remove();
            vthis.addClass("active");
            conditionArea.append(tempSpan);
        }
    }

    function buildSelectedDoctype(docId, docName) {
        var tempSpan = $('<span type ="docType" class="cs-label label-white-blue selected-type active" value=\"' + docId + '\"><i>'
            + docName
            + '</i>&nbsp;<a class="delete"><i class="icon-custom-remove">X</i></a></span>');
        return tempSpan;
    }

    /** 是否需要添加初始化状态 */
    function addActiveClassIfNeeded(type) {
        var hashVal = window.location.hash;

        if (hashVal.indexOf('#') != -1) {
            var m = hashVal.substring(hashVal.indexOf("#") + 1);

            if (m && m != 'none') {
                m = m.split('/');
                var tempViewMode = m[0];

                if (tempViewMode == 'highSearch') {
                    var beretVal;
                    $('#highSearchFilename').val(m[2]);

                    switch (type) {
                        case 'docType':
                            beretVal = m[3];
                            break;
                        case 'fileLabel':
                            beretVal = m[4];
                            break;
                    }

                    return beretVal;
                }
            }
        }

        return false;
    }

    /** 文件类型转换 */
    function transDocType(name) {
        if ("ufm.DocType.document" == name) {
            return "<spring:message code='ufm.DocType.document'/>"
        } else if ("ufm.DocType.picture" == name) {
            return "<spring:message code='ufm.DocType.picture'/>"
        } else if ("ufm.DocType.audio" == name) {
            return "<spring:message code='ufm.DocType.audio'/>"
        } else if ("ufm.DocType.video" == name) {
            return "<spring:message code='ufm.DocType.video'/>"
        } else if ("ufm.DocType.other" == name) {
            return "<spring:message code='ufm.DocType.other'/>"
        } else {
            return name;
        }
    }

    /** 是否需要添加初始化状态 */
    function addActiveClassForLabelIfNeeded() {
        if (isFirstLoadEnterpriseFls) {
            return addActiveClassIfNeeded('fileLabel');
        }

        return false;
    }

    /** 初始化企业标签信息 */
    function queryHighSearchLabels() {
        var requestPageNum = currFlPageNum + 1;
        if (isFirstLoadEnterpriseFls && getCookie("beforeRefreshFilelabelPage")) {
            requestPageNum = getCookie("beforeRefreshFilelabelPage");
        }
        if (requestPageNum == "0") {
            requestPageNum = 1;
        }

        filelabelSearch.searchByEnterprise("div.enterprise-label-list", requestPageNum, currFlPageSize, function (resp) {
            var enterpriseLabels = resp.fileLabelList;
            if (!enterpriseLabels || enterpriseLabels.length <= 0) {
                $("div.enterprise-label-list").empty().append("<spring:message code='fl.view.no.labels'/>");
                return;
            }
            currFlPageNum = resp.currPage;

            var tsearchTypeDiv = $('div.high-search-type');
            var tselFilelabels = tsearchTypeDiv.find('span[type=fileLabel]');
            var tlabelId = addActiveClassForLabelIfNeeded();
            var searchtypeArea = $('div.high-search-type');

            for (var index in resp.fileLabelList) {
                var tempObj = resp.fileLabelList[index];
                var tempSpan = $('<span type ="fileLabel" value = "' + tempObj.id + '" class="cs-label label-blue file-label-item">'
                    + tempObj.labelName + '</span>');

                if (tlabelId && tlabelId == tempObj.id) {
                    var selTempSpan = buildSelectedLabelElement(tempObj.id, tempObj.labelName);

                    tempSpan.addClass('active');
                    searchtypeArea.find('span.selected-file-label[type=fileLabel]').remove();
                    selTempSpan.addClass('active');
                    searchtypeArea.append(selTempSpan);
                }

                if (tselFilelabels && tselFilelabels.length > 0) {
                    for (var i = 0; i < tselFilelabels.length; i++) {
                        var tFilelabelId = $(tselFilelabels[i]).attr('value');
                        if (tFilelabelId == tempObj.id) {
                            tempSpan.addClass('active');
                            break;
                        }
                    }
                }

                $(".enterprise-label-list").append(tempSpan);
            }

            $(".enterprise-label-list").unbind('click').on('click', 'span.file-label-item', chooseFileLabel);
            isFirstLoadEnterpriseFls = false;
        });
    }

    /** 选择标签事件处理  */
    function chooseFileLabel() {
        var vthis = $(this);
        var searchtypeArea = $('div.high-search-type');
        var vflId = vthis.attr('value');
        var vflName = vthis.text();

        if (vthis.hasClass('active')) {
            vthis.removeClass('active');
            $('span.selected-file-label[value=' + vflId + ']').removeClass('active').remove();
        } else {
            var tempSpan = buildSelectedLabelElement(vflId, htmlEncode(vflName));

            $('span.file-label-item').removeClass("active");
            vthis.addClass("active");
            searchtypeArea.find('span.selected-file-label').remove();
            searchtypeArea.append(tempSpan);
        }
    }

    function buildSelectedLabelElement(flId, flName) {
        var tempSpan = $('<span type ="fileLabel" class="cs-label label-white-blue selected-file-label active" value=\"' + flId + '\"><i>'
            + flName + '</i>&nbsp;<a class="delete"><i class="icon-custom-remove">X</i></a></span>');
        return tempSpan;
    }

    /** 展示標簽绑定页面 */
    function showBindFileLabelUI(nodeId) {
        var url = '${ctx}/filelabel/showBindFilelabelView?bindType=1&nodeId=' + nodeId + '&ownerId=' + ownerId;

        top.ymPrompt.win({
            message: url, width: 742, height: 475,
            title: '<spring:message code="fl.title.bindfilelabel"/>',
            iframe: true,
            btn: [
                ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]
            ],
            handler: function (tp) {
                if (tp == 'ok') {
                    top.ymPrompt.getPage().contentWindow.submitBindFilelabel();
                } else {
                    top.ymPrompt.close();
                }
            }
        });
    }

    /** 标签操作回掉 */
    function filelabelOptCallback(optType, paramObj) {
        var vfilelabelDiv = $('div[fid=' + paramObj.nodeId + ']');

        switch (optType) {
            case 'ADD':
                var tempSpan = '<span class="label" flid = "' + paramObj.id + '">' + paramObj.name +
                    '<a class="delMark del-mark float-clear" onclick=unbindFileLabel(this)><span class = "icon-custom-del-mark"></span>&nbsp;</a></span>';
                vfilelabelDiv.append(tempSpan);

                break;
            case 'DELETE':
                vfilelabelDiv.find('span[flid=' + paramObj.id + ']').remove();
                break;
        }
    }

    /** 得到請求參數 */
    function obtainRequestParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);

        if (r != null)return unescape(r[2]);
        return null;
    }

    /** 展示高级搜索页面 */
    function showHighSearchView() {
        viewMode == "highSearch";
        vlabelIds = "", vdocType = "";
        setCookie("beforeHighSearchView", window.location.hash);

        var requestDocType = obtainRequestParam('docType');
        if (requestDocType) {
            $('li.search-type').hide();
        }

        $('#highSearchFilename').val("");
        $('.form-search').hide();
        $('div.files-block').css('margin-top', '0px');
        $('.high-search-container').show();
    }

    function beforeExitHighSearch() {
        vlabelIds = "", vdocType = "";

        $('#highSearchFilename').val("");
        $('div.high-search-type').empty();
        $('ul.search-type-list').find('span.active').removeClass('active');
        $('.enterprise-label-list').find('span.active').removeClass('active');
        $('.high-search-container').hide();
        $('.form-search').show();
    }

    /** 退出高级搜索页面 */
    function exitHighSearch() {
        var historyHash = getCookie("beforeHighSearchView");
        $('div.files-block').css('margin-top', '50px');
        beforeExitHighSearch();
        delCookie("beforeHighSearchView");
        delCookie('beforeRefreshFilelabelPage');

        if (historyHash) {
            if (historyHash.indexOf('#') != -1) {
                var hashVal = historyHash.substring(historyHash.indexOf("#") + 1);

                if (hashVal && hashVal != 'none') {
                    hashVal = hashVal.split('/');
                    var tviewMode = hashVal[0];
                    var tcurrentPage = hashVal[1];
                    var tcatalogParentId, tkeyword;

                    if (tviewMode == "file") {
                        tcatalogParentId = hashVal[2];
                    } else {
                        if (hashVal.length > 2) {
                            tkeyword = hashVal[2];
                        } else {
                            tkeyword = orgStr.substring(tviewMode.length + tcurrentPage.length + 2);
                        }

                        $("#searchBar").val(keyword);
                    }

                    if (tviewMode == 'file') {
                        changeHash("file", tcurrentPage, tcatalogParentId);
                        return;
                    } else {
                        if (tkeyword) {
                            changeHash("search", tcurrentPage, tkeyword);
                            return;
                        }
                    }
                }
            }
        }

        changeHash("file", 1, 0);
    }

    /** 解除标签绑定操作  */
    function unbindFileLabel(pthis) {
        var vthis = $(pthis);

        ymPrompt.confirmInfo({
            title: "<spring:message code='fl.view.unbind.tip'/>",
            message: "<spring:message code='fl.view.confirm.delete'/>",
            btn: [
                ["<spring:message code='button.ok'/>", "ok", true, "btn-focus"],
                ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]
            ],
            handler: function (tp) {
                if (tp == 'ok') {
                    var tclosestSpan = vthis.closest('span');
                    var tclosestDiv = vthis.closest('div');
                    var tlabelId = tclosestSpan.attr('flId');
                    var tnodeId = tclosestDiv.attr('fid');

                    $.ajax({
                        type: "POST",
                        url: "${ctx}/filelabel/unbindFilelabel",
                        data: {
                            nodeId: tnodeId,
                            labelId: tlabelId,
                            token: '${token}',
                            ownerId: ownerId
                        },
                        success: function (resp) {
                            if (resp.status == 'OK') {
                                tclosestSpan.remove();
                                handlePrompt("success", "<spring:message code='operation.success'/>");
                            } else {
                                handlePrompt("error", "<spring:message code='operation.failed'/>");
                            }
                        }
                    });
                }
            }
        });
    }

    /** 移除选择的标签 */
    function removeSelectedItem() {
        var vthis = $(this);
        var vSpanEl = vthis.closest('span');
        var vdocTypeId = vSpanEl.attr('value');
        var vtype = vSpanEl.attr("type");

        vSpanEl.removeClass('active').remove();
        switch (vtype) {
            case 'docType':
                $('span.common-type[value=' + vdocTypeId + ']').removeClass('active');
                break;
            case 'fileLabel':
                $('span.file-label-item[value=' + vdocTypeId + ']').removeClass('active');
                break;
        }
    }

    /** 提交高级搜索操作 */
    function submitHighSearch() {
        var vfilename = $('#highSearchFilename').val();
        var vselDocTypeId, vselLabelId, vchoosedCondition = $('div.high-search-type').find('span.active');
        vdocType = vlabelIds = '', currentPage = 1;

        if ($("#witchSelect").attr("value")) {
            vdocType = $("#witchSelect").attr("value");
        }

        setCookie("beforeRefreshFilelabelPage", currFlPageNum);
        if (vchoosedCondition.size() > 0) {
            for (var i = 0; i < vchoosedCondition.size(); i++) {
                var tempSel = $(vchoosedCondition[i]);
                var tempType = tempSel.attr('type');
                var vtempVal = tempSel.attr('value');

                switch (tempType) {
                    case 'docType':
                        if (vtempVal && vtempVal != '-1') {
                            vdocType = vtempVal;
                        }
                        break;
                    case 'fileLabel':
                        vlabelIds = vtempVal;
                        break;
                }
            }
        }

        doSearch(currentPage, vfilename, vdocType, vlabelIds);
    }

    $(document).ready(function () {
        queryHighSearchLabels();
        inintHighSearchDocType();
        $('div.high-search-type').on('click', 'i.icon-custom-remove', removeSelectedItem);
    });
    /******************************* 文件标签操作 **************************/

</script>