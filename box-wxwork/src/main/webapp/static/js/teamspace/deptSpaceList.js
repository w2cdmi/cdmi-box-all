$(document).ready(function() {
    listTeam()
});

function listTeam() {
    var url = host + "/ufm/api/v2/teamspaces/items";
    var params = {
        'type': 1,
        "userId": curUserId
    };
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(params),
        error: function(xhr, status, error){
            if(xhr.status == 404) {
                $.toast("<spring:message code='error.notfound'/>");
            } else {
                $.toast("<spring:message code='file.errorMsg.listFileFailed'/>");
            }
        },
        success: function(data) {
            console.log(data)
            if(typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                window.location.href = "${ctx}/logout";
                return;
            }
            //清空现有的列表
            var $list = $("#teamSpaceList");
            $list.children().remove();

            catalogData = data.memberships;
            if(catalogData.length == 0) {
                $(".not-space").css('display', 'block')
                $('.load').css('display', 'none');
                if(curPage > 1) {
                    curPage--;
                    changeHash(curPage);
                } else {

                }
                return;
            }
            //TODO: setCookie("teamListPage", curPage);
            var $spaceTemplate = $("#deptSpaceTemplate");
            for(var i in catalogData) {
                var space = catalogData[i].teamspace;

                space.teamRole=catalogData[i].teamRole
                space.createdAt=getFormatDate(new Date(space.createdAt), "yyyy-MM-dd");
                if(space.type != 5) {
                    $(".not-space").css('display', 'none')
                    $spaceTemplate.template(space).appendTo($list);

                    //设置数据
                    var $row = $("#space_" + space.id);
                    $row.data("obj", space);
                    //增加点击事件事件
                    // $row.on('click', onPress);
                }
            }
        },
        complete: function() {
            $('.load').css('display', 'none');
            spatialBodyBackground();
        }
    });
}
function spatialBodyBackground() {
    if($('#teamSpaceList>.space-row').length > 0) {
        $('body').css('background-size', '');
    } else {
        $('body').css('background', 'url(' + ctx + '/static/skins/default/img/join-space.png)no-repeat center 10rem');
        $('body').css('background-size', '5rem 5rem');
    }
}

function gotoTeamSpace(spaceId) {
    gotoPage(ctx+'/teamspace/file/' + spaceId);
}