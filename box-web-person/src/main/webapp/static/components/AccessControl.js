(function ($) {
    $.fn.extend({
        DeptAndUsersNavbar: function () {
            var self = this
            var _data = []
            function goback_Click() {
                _data.pop()
                create()
                if (self.onChange) {
                    if (_data.length == 0) {
                        self.onChange({id: -1})
                    } else {
                        self.onChange(_data[_data.length - 1])
                    }
                }
            }


            function item_Click() {
                var row = $(this).data('row')
                _data.splice(row.index + 1)
                create()
                if (self.onChange) {
                    self.onChange(row)
                }
            }

            function create() {
                self.empty()
                var len = _data.length
                if (len > 0) {
                    self.append('<a href="javascript:void(0);">返回上级</a>')
                    self.find('a:first').on('click', goback_Click)
                }
                self.append('<ol></ol>')
                var ol = self.find('ol')
                var dd
                var lastIndex = len - 1
                $.each(_data, function (i, row) {
                    row.index = i
                    if (lastIndex == i) {
                        dd = $('<dd><span>' + row.name + '</span></dd>')
                    } else {
                        dd = $('<dd><a href="javascript:void(0);">' + row.name + '</a></dd>')
                    }
                    dd.data('row', row)
                    ol.append(dd)
                })

                self.find('dd:has(a)').on('click', item_Click)
            }

            self.init = function () {

            }

            self.clear = function () {
                _data = []
                self.empty()
            }

            self.add = function (item) {
                var flag = false
                $.each(_data, function (i, row) {
                    if (item.userId == row.userId) {
                        flag = true
                        return
                    }
                })
                if (!flag) {
                    _data.push(item)
                    create()
                }
            }

            return self
        }
    })

    $.fn.extend({
        DeptAndUsersList: function () {
            var self = this
            function listItem_Click() {
                $(this).find(':checkbox').click();
                var row = $(this).data('row')
                if (row.type == 'department') {
                    loadDeptAndUsers(row.id)
                    if (self.onItemClick) {
                        self.onItemClick(row)
                    }
                }
            }

            function checkbox_Click(e) {
                e.stopPropagation()
                // $(this).prop('checked') ? oCk.parent().css('background-color', 'blue') : oCk.parent().css('background-color', '');
                if(self.onCheck){
                    var row = $(this).parents('li').data('row')
                    self.onCheck(e.target.checked, row)
                }
            }

            function loadDeptAndUsers() {
                var _loading = $.Tost('数据加载中...').show()
                $.ajax({
                    type: "POST",
                    url: host+"/ufm/api/v2/teamspaces/"+teamId+"/memberships/items",
                    data: JSON.stringify({
                        "keyword": ""
                    }),
                    dataType: 'json',
                    error: function (request) {
                        _loading.hide()
                        $.Alert('获取成员列表失败')
                    },
                    success: function (data) {
                        self.empty()
                        var listItem
                        var url
                        var name
                        var usersData = data.memberships
                        $.each(usersData, function (i, row) {
                            row.userId = row.member.id
                            row.userType = row.member.type;
                            row.username = row.member.name
                            if(row.id == ownerId){
                                return
                            }
                            if(teamType == '1') {
                                if(row.member.type == 'department') {
                                    return;
                                }
                            }

                            if (row.userType == 'user') {
                                url = ctx + '/userimage/getUserImage/' + row.userId
                                name = row.username
                            } else if (row.userType == 'department') {
                                url = ctx + '/static/skins/default/img/department-icon.png'
                                name = row.username
                            }
                            if (row.role == 'auther' || row.role == 'manager') {
                                listItem = ''
                            }else{
                                listItem = '<li role="' + row.role + '" id="' + row.userId + '">'
                                    + '<input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle"/>'
                                    + '<img src="' + url + '"/>'
                                    + '<span>' + name + '</span>'
                                    + '</li>'
                            }
 
                            listItem = $(listItem)
                            listItem.data('row', row)
                            self.append(listItem)
                        })

                        self.find('input').on('click', checkbox_Click)
                        self.find('li').on('click', listItem_Click)
                        _loading.hide()
                        if (self.onLoadSuccess) {
                            self.onLoadSuccess(usersData)
                        }

                    }
                });
            }

            self.setCheckedAndDisabled = function (items) {
                $.each(items, function (i, row) {
                    self.find('li[id="' + row.userId + '"] > input').prop('checked', true).prop('disabled', true)
                })
            }

            self.setCheckedAndEnabled = function (items) {
                $.each(items, function (i, row) {
                    self.find('li[id="' + row.userId + '"] > input')
                        .prop('checked', true)
                        .prop('disabled', false)
                })
            }

            self.setUncheckedAndEnabled = function (item) {
                self.find('li[id="' + item.userId + '"] > input')
                    .prop('checked', false)
                    .prop('disabled', false)
            }

            self.init = function () {

            }

            self.clear = function () {
                self.empty()
            }

            self.load = function (deptId) {
                loadDeptAndUsers(deptId)
            }

            return self
        }
    })
    $.fn.extend({
        TeamPersonList: function () {
            var self = this

            function getRoleList(roleName,teamRole) {
                if (roleName == "previewer") {
                    return '预览'
                } else if (roleName == "uploadAndView") {
                    return '预览 | 上传 | 下载'
                } else if (roleName == "viewer") {
                    return '预览 | 下载'
                } else if (roleName == "uploader") {
                    return '预览 | 上传'
                }else if(roleName == "auther" && teamRole=="admin"){
                    return '拥有者'
                }else if(roleName == "auther" && teamRole=="manager"){
                    return '管理者'
                }
            }
            function loadpersonlists() {
                $.ajax({
                    type: "POST",
                    url: host+"/ufm/api/v2/teamspaces/"+teamId+"/memberships/items",
                    data: JSON.stringify({
                        "keyword": ""
                    }),
                    dataType: 'json',
                    error: function (request) {
                        $.Alert('获取成员列表失败')
                    },
                    success: function (data) {
                        self.empty()
                        var listItem
                        var url
                        var name
                        var roles
                        var usersData = data.memberships
                        $.each(usersData, function (i, row) {
                            row.userType = row.member.type;
                            row.username=row.member.name;
                            row.userId=row.member.id;
                            if(row.id == ownerId){
                                return
                            }
                            if(teamType == '1') {
                                if(row.userType == 'department') {
                                    return true;
                                }
                            }

                            if (row.userType == 'user') {
                                url = ctx + '/userimage/getUserImage/' + row.userId
                                name = row.username
                            } else if (row.userType == 'department') {
                                url = ctx + '/static/skins/default/img/department-icon.png'
                                name = row.username
                            }
                            roles= getRoleList(row.role,row.teamRole)
                            listItem = '<li id="' + row.userId + '">'
                                + '<img src="' + url + '"/>'
                                + '<span style="margin-left: 16px">' + name + '</span>'
                                +'<span style="float: right;display: inline-block;margin-right: 16px">'+ roles + '</span>'
                                + '</li>'
                            listItem = $(listItem)
                            listItem.data('row', row)
                            self.append(listItem)
                        })
                    }
                });
            }


            self.init = function () {

            }

            self.clear = function () {
                self.empty()
            }
            self.load = function () {
                loadpersonlists()
            }

            return self
        }
    })
    $.fn.extend({

        SharedUserList: function () {
            var self = this
            var _nodeId = null;
            function getRoleList(roleName,teamRole) {
                if (roleName == "previewer") {
                    return '预览'
                } else if (roleName == "uploadAndView") {
                    return '预览 | 上传 | 下载'
                } else if (roleName == "viewer") {
                    return '预览 | 下载'
                } else if (roleName == "uploader") {
                    return '预览 | 上传'
                }else if(roleName == "auther" && teamRole=="admin"){
                    return '拥有者'
                }else if(roleName == "auther" && teamRole=="manager"){
                    return '管理者'
                }
            }

            function deleteItem_Click() {
                var row = $(this).parents('li').data('row')
                if(row.isShared){
                    $.Confirm('确认删除吗？', function(){
                        var params= {
                            "ownerId": row.ownerId,
                            "aclId": row.id,
                            "token":token
                        };
                        $.ajax({
                            type: "DELETE",
                            url: host + "/ufm/api/v2/acl/"+row.resource.ownerId+"/"+row.id,
                            error: function() {
                                $.Alert('删除失败')
                            },
                            success: function() {
                                self.remove(row.userId, row.id)
                                if(self.onChange){
                                    self.onChange(row)
                                }
                                $.Alert("删除成功");
                            }
                        })
                    })
                }else{
                    self.remove(row.userId, row.id)
                    if(self.onChange){
                        self.onChange(row)
                    }
                }


            }

            function updateItem_Click() {
                var row = $(this).parents('li').data('row')
                if(self.onUpdate){
                    self.onUpdate(row)
                }
            }

            function createItem(row) {
                var url;
                if (row.type === 'user') {
                    url = ctx + '/userimage/getUserImage/' + row.userId
                } else if (row.type === 'department') {
                    url = ctx + '/static/skins/default/img/department-icon.png'
                }
                return '<li ids="'+ row.id + '" id="' + row.userId + '">'
                + '<img src="' + url + '"/>'
                + '<span style="display: inline-block;width:90px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">' + row.username + '</span>'
                + '<span class="user-contacts-tools">'
                + ((row.userId === ownerId && row.teamRole === "manager") || row.teamRole === "admin" ? '' : '&nbsp<a href="javascript:void(0)" id="delete">删除</a>')
                + ((row.userId === ownerId && row.teamRole === "manager") || (row.teamRole === "admin") || !row.isadd ? '' : '&nbsp<a href="javascript:void(0)" id="update">修改</a>')
                + '</span>'
                + '<div style="font-size:12px;margin-top:5px;">'
                + (row.isShared ? '<span style="color:#999;">已添加</span>' : '<span style="color:#ea5036;">未添加</span>') + '&nbsp;<span style="color:#ccc;" id="roleList">' + (row.isShared ? getRoleList(row.role,row.teamRole) : '') + '</span>'
                + '</div>'
                + '</li>'
            }

            function loadSharedUserList() {

                var params = {
                    "nodeId":_nodeId,
                    "limit": 100,
                    "offset":0
                };

                $.ajax({
                    type: "POST",
                    data: JSON.stringify(params),
                    url: host + "/ufm/api/v2/acl/"+ownerId,
                    error: function (XHR) {
                        $.Alert("获取添加用户失败")
                    },
                    success: function (data) {
                        self.empty()
                        var users = data.acls
                        var listItem
                        $.each(users, function (i, row) {
                            row.type = row.user.type;
                            row.username = row.user.name;
                            row.userId = row.user.id
                            row.index = i;
                            if(row.role=="auther"){
                                row.isadd = false;
                            }else{
                                row.isadd = true;
                            }

                            row.isShared = true
                            listItem = $(createItem(row))
                            listItem.data('row', row)
                            self.append(listItem)
                        })
                        self.find('a#delete').on('click', deleteItem_Click)
                        self.find('a#update').on('click', updateItem_Click)
                        if (self.onLoadSuccess) {
                            self.onLoadSuccess(users)
                        }
                    }
                })
            }

            self.init = function () {

            }

            self.updateRoleList = function(id, roleName) {
                self.find('li[id="'+id+'"] #roleList').text(getRoleList(roleName))
            }

            self.add = function (item) {
                item.isadd = false;
                item.isShared = false
                var listItem = $(createItem(item))
                listItem.data('row', item)
                self.prepend(listItem)

                listItem.find('#delete').on('click', deleteItem_Click)
            }

            self.remove = function(userId, id) {
                self.find('li[id="'+userId+'"]').remove()
                self.find('li[ids="'+id+'"]').remove()
            }

            self.clear = function () {
                self.empty()
            }


            self.load = function (nodeId) {
                _nodeId = nodeId
                loadSharedUserList()
            }

            return self
        }
    })

    $.fn.extend({
        ShareDialog: function () {
            var self = this
            var _dialog = null
            var _deptId = null
            var _node = null
            var _updateShareRoleDialog = null
            var _sharedUsers = null
            var _unSharedUsers = null
            var _deptAndUsersNavbar = null
            var _deptAndUsersList = null
            var _teamPersonList = null
            var _sharedUserList = null
            var _permissions = null
            var _allMessageTo = null
            var _isSharedUserListLoaded = null
            var _updateItem = null
            var _filesuccess = null;
            var _accessControlInput = null;

            function getRoleName(obj){
                var roleName
                var preview  = true
                var download = obj.find('#downloader')[0].checked
                var upload = obj.find('#uploader')[0].checked

                if(download==true){
                    if(upload==true){
                        if(preview==true){
                            roleName="uploadAndView";
                        }else{
                            roleName="uploadAndView";
                        }
                    }else{
                        if(preview==true){
                            roleName="viewer";
                        }else{
                            roleName="viewer";
                        }
                    }
                }else{
                    if(upload==true){
                        if(preview==true){
                            roleName="uploader";
                        }else{
                            roleName="uploader";
                        }
                    }else{
                        if(preview==true){
                            roleName="previewer";
                        }else{
                            roleName="previewer";
                        }
                    }
                }
                return roleName
            }

            function setRoleName(roleName) {
                var previewer = _updateShareRoleDialog.find('#previewer')
                var downloader = _updateShareRoleDialog.find('#downloader')
                var uploader = _updateShareRoleDialog.find('#uploader')
                previewer.prop("checked", false)
                downloader.prop("checked", false)
                uploader.prop("checked", false)
                if(roleName=="previewer"){
                    previewer.prop("checked", true)
                }else if(roleName=="viewer"){
                    previewer.prop("checked", true)
                    downloader.prop("checked", true)
                }else if(roleName=="uploadAndView"){
                    previewer.prop("checked", true)
                    downloader.prop("checked", true)
                    uploader.prop("checked", true)
                }else if(roleName=="uploader"){
                    previewer.prop("checked", true)
                    uploader.prop("checked", true)
                }
            }

            // function isInUnSharedUsers(item) {
            //     var flag = false
            //     $.each(_unSharedUsers, function(i, row){
            //         if(item.userId == row.userId){
            //             flag = true
            //             return
            //         }
            //     })
            //     return flag
            // }

            function rebuildSharedUsersIndex() {
                $.each(_sharedUsers, function(i, row){
                    row.index = i
                })
            }

            function rebuildUnSharedUsersIndex() {
                $.each(_unSharedUsers, function(i, row){
                    row.index = i
                })
            }

            // function addMessageTo(cloudUserId, loginName,userType,userName, userEmail) {
            //     var itemValue = "["+userType+"]" + loginName + "[" + cloudUserId + "]" + userEmail;
            //     _allMessageTo.push(itemValue);
            //
            // }
            function addMessageTo(memberId, loginName, userType, userName, userEmail) {
                var member={
                    id:memberId,
                    loginName:loginName,
                    type:userType,
                    name:userName
                }
                _allMessageTo.push(member);

            }
            function pad(num, n) {
                var len = num.toString().length;
                while(len < n) {
                    num = "0" + num;
                    len++;
                }
                return num;
            }

            function getTrunckData(dataArray){
                if(dataArray == null || dataArray == ""){
                    return "";
                }
                var result = "";
                for ( var i = 0; i < dataArray.length; i++) {
                    if(dataArray[i] != ""){
                        result = result + pad(dataArray[i].length,4) + dataArray[i];
                    }
                }
                return result;
            }
            function getUsersString(dataArray) {
                var result = "";
                for (var i = 0; i < dataArray.length; i++) {
                    if (dataArray[i] != "") {
                        if (i == (dataArray.length - 1)) {
                            result = result + dataArray[i]
                        } else {
                            result = result + dataArray[i] + ";"
                        }
                    }
                }
                return result;
            }
            function addShare() {
                var userss = [];
                var _loading = $.Tost('正在添加...').show()
                $.each(_unSharedUsers, function(i, row){
                    userss.push(row)
                    if(row.type=='user'){
                        addMessageTo(row.userId, row.username, row.type, row.username, row.email)
                    }else if(row.type=='department'){
                        addMessageTo(row.userId, row.username, row.type, row.username, row.email, null)
                    }
                })
                // var shareToStr = getUsersString(_allMessageTo)
                var authType =getRoleName(_permissions)
                // var userjson =  $.parseJSON(userss);
                var params= {
                    role:authType,
                    userList:_allMessageTo,
                    resource:{"ownerId":ownerId,"nodeId":_node.id},
                };
                $.ajax({
                    type: "POST",
                    data: JSON.stringify(params),
                    url: host+"/ufm/api/v2/acl",
                    error: function() {
                        _loading.hide()
                       $.Alert('添加失败')
                    },
                    success: function() {
                       self.hide()
                        _loading.hide()
                       $.Alert('添加成功')
                       if(self.onSuccess){
                           self.onSuccess()
                       }

                    }
                })

            }

            function updateShare(item) {
                var roleName = getRoleName(_updateShareRoleDialog)
                var params= {
                    "role": roleName,
                };
                $.ajax({
                    type: "PUT",
                    data: JSON.stringify(params),
                    url: host + "/ufm/api/v2/acl/"+item.resource.ownerId+"/"+item.id,
                    error: function() {
                        $.Alert("修改权限失败");
                    },
                    success: function() {
                        $.Alert("修改权限成功");
                        _updateItem.role = roleName
                        _sharedUserList.updateRoleList(_updateItem.userId, _updateItem.role)
                    }
                })
            }
            function modifyNodeIsVisible(isshow,success) {
                var url = host+"/ufm/api/v2/acl/isVisible/" + ownerId + "/" + _node.id;

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(isshow),
                    error: function (request) {
                        $.alert("error",
                            "<spring:message code='operation.failed'/>");
                    },
                    success: function (data) {
                        // $.toast("设置成功");
                        if(success){
                            success()
                        }
                    }
                });
            }
            self.init0 = function () {
                _updateShareRoleDialog = $('#updateShareRoleDialog').dialog({title:'修改权限'})
                _updateShareRoleDialog.init()
                _updateShareRoleDialog.find('#cancel_button').on('click', function(){
                    _updateShareRoleDialog.hide()
                })
                _updateShareRoleDialog.find('#ok_button').on('click', function(){
                    _updateShareRoleDialog.hide()
                    updateShare(_updateItem)
                })

                _dialog = self.dialog({title: '权限管理'})
                _dialog.init()

                _deptAndUsersNavbar = self.find('#deptAndUsersNavbar').DeptAndUsersNavbar()
                _deptAndUsersNavbar.init()
                _deptAndUsersNavbar.onChange = function (item) {
                    _deptId = item.userId
                    _deptAndUsersList.load(_deptId)

                }
                _sharedUserList = self.find('#sharedUserList').SharedUserList()
                _sharedUserList.init()
                _sharedUserList.onChange = function (item){
                    if(item.isShared){
                        _deptAndUsersList.setUncheckedAndEnabled(item)
                        _sharedUsers.splice(item.index, 1)
                        rebuildSharedUsersIndex()
                    }else{
                        _deptAndUsersList.setUncheckedAndEnabled(item)
                        _unSharedUsers.splice(item.index, 1)
                        rebuildUnSharedUsersIndex()
                    }

                }
                _sharedUserList.onUpdate = function (item) {
                    _updateItem = item
                    setRoleName(item.role)
                    _updateShareRoleDialog.show()
                }
                _sharedUserList.onLoadSuccess = function (sharedUsers) {
                    _sharedUsers = sharedUsers
                    _deptAndUsersList.setCheckedAndDisabled(_sharedUsers)
                }
                _teamPersonList = self.find('#teamPersonLists').TeamPersonList()
                _teamPersonList.init()
                _deptAndUsersList = self.find('#deptAndUsersList').DeptAndUsersList()
                _deptAndUsersList.init()

                _deptAndUsersList.onItemClick = function (item) {
                    _deptId = item.userId
                    _deptAndUsersNavbar.add(item)
                }
                _deptAndUsersList.onCheck = function (checked, item) {
                    if(checked){
                        var newItem
                        if(item.userType == 'user'){
                            newItem = {id: item.id,userId: item.userId, username: item.username, type: 'user'}
                        }else if(item.userType == 'department'){
                            newItem = {id: item.id,userId: item.userId, username: item.username, type: 'department'}

                        }
                        // if(isInUnSharedUsers(newItem)){
                        //     return
                        // }
                        _sharedUserList.add(newItem)
                        _unSharedUsers.push(newItem)
                        newItem.index = _unSharedUsers.length - 1
                    }else{
                        _sharedUserList.remove(item.userId, item.id)
                        _deptAndUsersList.setUncheckedAndEnabled(item)
                        _unSharedUsers.splice(item.index, 1)
                        rebuildUnSharedUsersIndex()
                    }
                }

                _deptAndUsersList.onLoadSuccess = function (item) {
                    if (!_isSharedUserListLoaded) {
                        _isSharedUserListLoaded = true
                        _sharedUserList.load(_node.id)
                    } else {
                        _deptAndUsersList.setCheckedAndEnabled(_unSharedUsers)
                    }
                    // _deptAndUsersList.setCheckedAndDisabled(_sharedUsers)
                }

                _permissions = self.find('#permissions')


                self.find('#cancel_button').on('click', function(){
                    self.hide()
                })
                self.find('#ok_button').on('click', function(){
                   if(_unSharedUsers.length == 0){
                    self.hide()
                       return
                   }

                   addShare()

                })
                self.find("#secret").on("click",function () {
                    $.Confirm('您确认要修改当前目录为限制访问吗？',function () {
                        modifyNodeIsVisible(1,function () {
                            self.find("#addAccessControl").show()
                            self.find("#teamPersonList").hide()
                            _filesuccess()
                        },function () {
                            self.find("#public").attr("checked",true)
                        })
                    })


                })
                self.find("#public").on("click",function () {
                    $.Confirm('你确定要转换成全员公开文件吗？', function(){
                        modifyNodeIsVisible(0,function () {
                            self.find("#addAccessControl").hide()
                            self.find("#teamPersonList").show()
                            $("#sharedUserList").children().remove()
                            _filesuccess()
                        },function () {
                            self.find("#secret").attr("checked",true)
                        })
                    })
                })


            }

            self.show0 = function (deptId, node,teamId,filesuccess) {
                _filesuccess = filesuccess
                _deptId = deptId
                _node = node
                _updateItem = null
                _sharedUsers = []
                _unSharedUsers = []
                _allMessageTo = []
                _isSharedUserListLoaded = false

                _deptAndUsersNavbar.clear()
                _deptAndUsersList.clear()
                _sharedUserList.clear()
                _teamPersonList.clear()

                _deptAndUsersList.load(_deptId)
                _teamPersonList.load()
                if(_node.isSecret){
                    // self.find("#secret").attr("checked",false)
                    self.find("#secret").attr("checked","checked")
                    self.find("#public").attr("checked",false)
                    self.find("#addAccessControl").show()
                    self.find("#teamPersonList").hide()
                }else{
                    // self.find("#public").attr("checked",false)
                    self.find("#public").attr("checked","checked")
                    self.find("#secret").attr("checked",false)
                    self.find("#teamPersonList").show()
                    self.find("#addAccessControl").hide()
                }
                _dialog.show()
                // if(row.isSecret){
                //     $("#secret").attr("checked",true)
                //     $("#addAccessControl").show()
                //     // $("#teamPersonList").hide()
                // }else{
                //     $("#public").attr("checked",true)
                //     $("#teamPersonList").show()
                //     // $("#addAccessControl").hide()
                // }
            }

            return self
        }
    })
})(jQuery)