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
                        self.onChange({userId: 0})
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
                    if (item.id == row.id) {
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
                var row = $(this).data('row')
                if (row.type == 'department') {
                    loadDeptAndUsers(row.userId)

                    if (self.onItemClick) {
                        self.onItemClick(row)
                    }
                }
            }

            function checkbox_Click(e) {
                e.stopPropagation()
                if(self.onCheck){
                    var row = $(this).parents('li').data('row')
                    self.onCheck(e.target.checked, row)
                }
            }

            function loadDeptAndUsers(deptId) {
                var _loading = $.Tost('数据加载中...').show()
                $.ajax({
                    type: "POST",
                    url: host + '/ecm/api/v2/users/listDepAndUsers',
                    data: JSON.stringify({
                        deptId: deptId
                    }),
                    error: function (request) {
                        _loading.hide()
                        $.Alert('获取部门用户信息失败')
                    },
                    success: function (data) {

                        var data = JSON.parse(data)
                        self.empty()
                        var listItem
                        var url
                        var name
                        $.each(data, function (i, row) {
                            if(row.id == ownerId){
                                return
                            }

                            if (row.type == 'user') {
                                url = ctx + '/userimage/getUserImage/' + row.id
                                name = row.alias
                            } else if (row.type == 'department') {
                                url = ctx + '/static/skins/default/img/department-icon.png'
                                name = row.name
                            }
                            listItem = '<li id="' + row.id + '">'
                                + '<input type="checkbox" class="mgc mgc-info mgc-lg mgc-circle"/>'
                                + '<img src="' + url + '"/>'
                                + '<span>' + name + '</span>'
                                + '</li>'
                            listItem = $(listItem)
                            listItem.data('row', row)
                            self.append(listItem)
                        })

                        self.find('input').on('click', checkbox_Click)
                        self.find('li').on('click', listItem_Click)
                        _loading.hide()
                        if (self.onLoadSuccess) {
                            self.onLoadSuccess(data)
                        }

                    }
                });
            }

            self.setCheckedAndDisabled = function (items) {
                $.each(items, function (i, row) {
                    self.find('li[id="' + row.userId + '"] > input')
                        .prop('checked', true)
                        .prop('disabled', true)
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
                        var params= [row.id]
                        $.ajax({
                            type: "DELETE",
                            data: JSON.stringify(params),
                            url: host+"/ufm/api/v2/teamspaces/"+ row.teamId + "/memberships/batch",
                            error: function() {
                                $.Alert('删除失败')
                            },
                            success: function() {
                                self.remove(row.userId)
                                if(self.onChange){
                                    self.onChange(row)
                                }
                                $.Alert("删除成功");
                            }
                        })
                    })
                }else{
                    self.remove(row.userId)
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
                return '<li id="' + row.userId + '">'
                + '<img src="' + url + '"/>'
                + '<span style="display: inline-block;width:90px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">' + row.name + '</span>'
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
                    "keyword": ""
                };

                $.ajax({
                    type: "POST",
                    data: JSON.stringify(params),
                    url: host+"/ufm/api/v2/teamspaces/"+_nodeId+"/memberships/items",
                    error: function () {
                        $.Alert("获取添加用户失败")
                    },
                    success: function (data) {
                        self.empty()
                        var users = data.memberships
                        var listItem
                        $.each(users, function (i, row) {
                            row.type = row.member.type;
                            row.userId = row.member.id
                            row.name = row.member.name
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

            self.remove = function(id) {
                self.find('li[id="'+id+'"]').remove()
            }

            self.clear = function () {
                self.empty()
            }


            self.load = function (nodeId) {
                _nodeId = nodeId
                loadSharedUserList(nodeId)
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
            var _sharedUserList = null
            var _permissions = null
            var _allMessageTo = null
            var _isSharedUserListLoaded = null
            var _updateItem = null
            var _updateTeamList = null

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

            function addMessageTo(cloudUserId, loginName,userType,userName, userEmail) {
                var itemValue ={
                    id:cloudUserId,
                    type:userType
                }
                _allMessageTo.push(itemValue);

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
            function addShare() {
                var _loading = $.Tost('正在添加...').show()
                $.each(_unSharedUsers, function(i, row){
                    if(row.type=='user'){
                        addMessageTo(row.userId, row.username, row.type, row.username, row.email)
                    }else if(row.type=='department'){
                        addMessageTo(row.userId, row.username, row.type, row.username, row.email, null)
                    }
                })
                // var shareToStr = getTrunckData(_allMessageTo)
                var authType =getRoleName(_permissions)
                var params= {
                    // "cloudUserIds": shareToStr,
                    // "teamId": _node.id,
                    // "authType" : authType,
                    // token:token
                    teamRole:"member",
                    role:authType,
                    memberList:_allMessageTo
                };
                $.ajax({
                    type: "POST",
                    data: JSON.stringify(params),
                    url: host+"/ufm/api/v2/teamspaces/"+_node.teamId+"/memberships",
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
                        _updateTeamList()
                    }
                })

            }

            function updateShare(item) {
                var roleName = getRoleName(_updateShareRoleDialog)
                var params= {
                    // "teamId": _node.id,
                    // "teamMemberId": item.id,
                    // "authType": roleName,
                    // "token":token
                    role:roleName,
                    teamRole:"member"
                };
                $.ajax({
                    type: "PUT",
                    data: JSON.stringify(params),
                    url: host+"/ufm/api/v2/teamspaces/"+_node.teamId+"/memberships/"+item.id,
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

                _dialog = self.dialog({title: '成员管理'})
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
                        _updateTeamList()
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
                _deptAndUsersList = self.find('#deptAndUsersList').DeptAndUsersList()
                _deptAndUsersList.init()
                _deptAndUsersList.onItemClick = function (item) {

                    _deptId = item.id
                    _deptAndUsersNavbar.add(item)
                }
                _deptAndUsersList.onCheck = function (checked, item) {
                    if(checked){
                        var newItem
                        if(item.type == 'user'){
                            newItem = {userId: item.id, name: item.alias, type: 'user', email: item.email}
                        }else if(item.type == 'department'){
                            newItem = {userId: item.id, name: item.name, type: 'department', email: null}

                        }
                        // if(isInUnSharedUsers(newItem)){
                        //     return
                        // }
                        _sharedUserList.add(newItem)
                        _unSharedUsers.push(newItem)
                        newItem.index = _unSharedUsers.length - 1
                    }else{
                        _sharedUserList.remove(item.id)
                        _deptAndUsersList.setUncheckedAndEnabled(item)
                        _unSharedUsers.splice(item.index, 1)
                        rebuildUnSharedUsersIndex()
                    }
                }

                _deptAndUsersList.onLoadSuccess = function () {
                    if (!_isSharedUserListLoaded) {
                        _isSharedUserListLoaded = true
                        _sharedUserList.load(_node.teamId)
                    } else {
                        _deptAndUsersList.setCheckedAndDisabled(_sharedUsers)
                    }
                    _deptAndUsersList.setCheckedAndEnabled(_unSharedUsers)
                }

                _permissions = self.find('#permissions')


                self.find('#cancel_button').on('click', function(){
                    self.hide()
                    _filesuccess()
                })
                self.find('#ok_button').on('click', function(){
                   if(_unSharedUsers.length == 0){
                    self.hide()
                       return
                   }

                   addShare()

                })

            }

            self.show0 = function (deptId, node,teamlistsuccess) {
                _updateTeamList = teamlistsuccess
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

                _deptAndUsersList.load(_deptId)

                _dialog.show()
            }

            return self
        }
    })
})(jQuery)