var enterpriseClient = require("../../module/enterprise.js");
var config = require("../../config.js");

var deptName = "";
var currentDeptId = 0;
var deptName = "";

// 滑动的起始坐标
var startX = 0;
var startY = 0;


Page({

    data: {
        datas: [{
            icon: "/disk/images/department-avatar.png",
            name: "成都分公司",
            departmentCount: 20,
            employeeCount: 120,
            showPopup: false,//添加部门弹窗控制参数 
            showLeftPopup: false,
            showRightPopup: false,
            showTopPopup: false,
            showBottomPopup: false
        }],
        scrollTop: 1,
        showModalStatus: false,
        scrollItem: '',

    },

    onLoad: function (options) {
        var page = this;
        currentDeptId = options.parentId;
        deptName = options.deptName;
        if (currentDeptId == undefined) {
            currentDeptId = 0;
            deptName = getApp().globalData.enterpriseName;
        }
        page.setData({
            currentDeptId: currentDeptId
        });
        var deptCrumbs = [];
        if (currentDeptId == 0) {
            var crumb = { "parentId": 0, "name": getApp().globalData.enterpriseName }
            deptCrumbs.push(crumb);
            getApp().globalData.deptCrumbs = deptCrumbs;
        } else {
            deptCrumbs = getApp().globalData.deptCrumbs;
            var crumb = { "parentId": currentDeptId, "name": deptName };
            deptCrumbs.push(crumb);
            getApp().globalData.deptCrumbs = deptCrumbs;
        }
    },

    onShow: function () {
        var page = this;
        wx.setNavigationBarTitle({
            title: deptName
        });
        this.setData({
            showModalStatus: false,
        })
        getDeptAndEmployees(currentDeptId, page);
    },

    _touchend: function (e) {
        var index = e.currentTarget.dataset.index;//当前索引
        var touchEndX = e.changedTouches[0].clientX;//滑动终止x坐标
        var touchEndY = e.changedTouches[0].clientY;//滑动终止y坐标

        //获取滑动角度，Math.atan()返回数字的反正切值
        var angle = 360 * Math.atan((touchEndY - startY) / (touchEndX - startX)) / (2 * Math.PI);

        //滑动超过30度角 return
        if (Math.abs(angle) > 30) return;

        // 滑动距离过小即是点击进入文件夹
        if (((touchEndX - startX) > -50) && ((touchEndX - startX) < 50)) return;

        //向右滑
        if (touchEndX > startX) index = -1;

        this.setData({
            isTouchMoveIndex: index,
        });
    },

    //检测到有滑动模块就将其他的归位
    bindTouchStart: function (e) {
        // 起始位置
        startX = e.changedTouches[0].clientX;
        startY = e.changedTouches[0].clientY;

        this.setData({
            isTouchMoveIndex: -1,
        });
    },

    //新增部门弹窗
    togglePopup() {
        this.setData({
            showPopup: !this.data.showPopup
        });
    },
    onUnload: function (e) {
        var page = this;
        var deptCrumbs = getApp().globalData.deptCrumbs;
        if (deptCrumbs != undefined && deptCrumbs.length > 1) {
            deptCrumbs.splice(deptCrumbs.length - 1, 1);
            getApp().globalData.deptCrumbs = deptCrumbs;
            currentDeptId = deptCrumbs[deptCrumbs.length - 1].parentId;
            deptName = deptCrumbs[deptCrumbs.length - 1].name;
            page.setData({
                currentDeptId: currentDeptId
            });
        }
    },

    // 跳转到员工详情页面
    showPersonDetail: function (e) {
        var employeeId = e.currentTarget.dataset.item.userId;
        var data = '';
        var page = this;

        wx.navigateTo({
            url: "/disk/enterprise/employees-departments/person-details/person-details?employeeId=" + employeeId,
        })
    },
    setModalStatus: function (e) {
        var page = this;
        var department = e.currentTarget.dataset.department;
        if (department != undefined) {
            page.setData({
                department: department
            });
        }

        var animation = wx.createAnimation({
            duration: 200,
            timingFunction: "linear",
            delay: 0
        })
        this.animation = animation
        animation.translateY(300).step()
        this.setData({
            animationData: animation.export()
        })
        if (e.currentTarget.dataset.status == 1) {
            this.setData({
                showModalStatus: true
            });
        }
        setTimeout(function () {
            animation.translateY(0).step()
            this.setData({
                animationData: animation
            })
            if (e.currentTarget.dataset.status == 0) {
                this.setData({
                    showModalStatus: false
                });
            }
        }.bind(this), 200)
    },
    inputChange: function (e) {
        deptName = e.detail.value;
    },
    onCreateFolderCancel: function () {
        deptName = "";
        this.setData({
            deptName: "",
            showPopup: false
        })
    },
    onCreateFolderConfirm: function () {
        var page = this;
        deptName = deptName.trim();
        if (deptName == "") {
            wx.showToast({
                title: '部门名称不能为空',
                icon: 'none'
            })
            return;
        }

        var regEn = /[\/:*"?<>|\\]/im;
        for (var i = 0; i < deptName.length; i++) {
            var deptNameChar = deptName.charAt(i);
            if (regEn.test(deptNameChar)) {
                wx.showToast({
                    title: '部门名称不能包含特殊符号',
                    icon: 'none'
                });
                return;
            }
        }
        var data = {
            parentId: currentDeptId,
            name: deptName
        };
        enterpriseClient.createDept(data, function () {
            page.onCreateFolderCancel();
            getDeptAndEmployees(data.parentId, page);
        });
    },
    onClickDept: function (e) {
        var page = this;
        var deptInfo = e.currentTarget.dataset.deptInfo;
        wx.navigateTo({
            url: '/disk/enterprise/employees-departments/employees-departments?parentId=' + deptInfo.userId + "&deptName=" + deptInfo.name
        });
    },
    onClickDeptMore: function (e) {
        var page = this;
        page.setData({

        });
    },
    onDeleteDeptOrEmploye: function (e) {
        var page = this;
        var item = e.currentTarget.dataset.item;
        var user = getApp().globalData.userId;
        if (item.userId == user) {
            wx.showToast({
                title: '不能删除自己！',
                duration: 1000
            });
        } else {
            var deptOrEmploy = item.type == 'department' ? '部门 ' : '员工 ';
            wx.showModal({
                cancelText: "取消",
                confirmText: "确认",
                title: '提示',
                content: '确认将' + deptOrEmploy + item.name + ' 从企业中删除吗？',
                success: function (res) {
                    if (res.confirm) {
                        if (item.type == "department") {
                            var deptId = item.userId;
                            enterpriseClient.deleteDept(deptId, function () {
                                wx.showToast({
                                    title: '删除部门成功！',
                                    duration: 1000
                                });
                                page.onShow();
                            });
                        } else {
                            var userId = item.userId;
                            enterpriseClient.deleteEmploy(userId, function () {
                                wx.showToast({
                                    title: '删除员工成功！',
                                    duration: 1000
                                });
                                page.onShow();
                            });
                        }

                    } else if (res.cancel) {
                        page.setData({
                            scrollLeft: 0
                        })
                    }
                }
            })
        }

    },
    onSetDocumentAudit: function (e) {
        var deptId = e.currentTarget.dataset.deptId;
        var approve = e.currentTarget.dataset.approve;
        if (deptId == undefined || approve == undefined || typeof (approve) != "boolean") {
            wx.showToast({
                title: '设置失败！',
                icon: 'none',
                duration: 1000
            });
            return;
        }
        if (approve) {
            approve = false;
        } else {
            approve = true;
        }
        enterpriseClient.setDocumentAudit(deptId, approve, function () {
            wx.showToast({
                title: '设置成功！'
            });
        });
    },

    onClickSetDeptManager: function (e) {
        var deptId = e.currentTarget.dataset.deptId;
        var name = e.currentTarget.dataset.name;
        if (deptId == undefined) {
            wx.showToast({
                title: '获取部门ID失败！',
                icon: 'none',
                duration: 1000
            });
            return;
        }
        enterpriseClient.getManager(deptId, function (data) {
            var data = data;
            if (data) { // 存在部门主管
                wx.navigateTo({
                    url: '/disk/enterprise/reselection/manager?deptId=' + deptId + "&id=" + data.enterpriseUserId + "&departmentName=" + name
                });
            } else {
                wx.navigateTo({
                    url: '/disk/enterprise/employees-departments/department/manager?deptId=' + deptId + "&departmentName=" + name
                });
            }
        });
    },
    onClickSetDeptKnowledgerManager: function (e) {
        var deptId = e.currentTarget.dataset.deptId;
        if (deptId == undefined) {
            wx.showToast({
                title: '获取部门ID失败！',
                icon: 'none',
                duration: 1000
            });
            return;
        }
        // 接口未给
        // enterpriseClient.getManager(deptId, function (data) {
        //     var data = data;
        //     if (data) { // 存在部门知识管理员
        //         wx.navigateTo({
        //             url: '/disk/enterprise/reselection/knowledger?deptId=' + deptId + "&id=" + data.enterpriseUserId + "&departmentName=" + name
        //         });
        //     } else {
        //         wx.navigateTo({
        //             url: '/disk/enterprise/employees-departments/department/knowledger?deptId=' + deptId + "&departmentName=" + name
        //         });
        //     }
        // });
        wx.navigateTo({
            url: '/disk/enterprise/reselection/knowledger?deptId=' + deptId + "&id=" + data.enterpriseUserId + "&departmentName=" + name
        });
    },
    // 设置用户的部门
    onClickMoveEmploye: function (e) {
        var employeId = e.currentTarget.dataset.employeId;
        var deptId = e.currentTarget.dataset.deptId;
        wx.navigateTo({
            url: "/disk/enterprise/template/selectDepartment?employeId=" + employeId + "&srcDeptId=" + deptId
        });
    }
})

//刷新页面数据
function getDeptAndEmployees(parentId, page) {
    var depts = [];
    var employees = [];
    var currentDeptId = page.data.currentDeptId;
    enterpriseClient.getDeptAndEmployees(parentId, function (data) {
        for (let i = 0; i < data.length; i++) {
            if (data[i].type == 'department') {
                data[i].icon = "/disk/images/department-avatar.png";
                depts.push(data[i]);
            } else {
                data[i].icon = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data[i].id + "?authorization=" + getApp().globalData.token;
                if (currentDeptId !== 0) {
                    enterpriseClient.getManager(currentDeptId, function (managerData) {
                        // 查询当前部门的管理员，与员工ID进行比对
                        var managerData = managerData;
                        if (managerData.enterpriseUserId == data[i].userId) {
                            data[i].role = '管理员';
                        } else {
                            data[i].role = '员工';
                        }
                        employees.push(data[i]);
                        page.setData({
                            depts: depts,
                            employees: employees
                        });
                    });
                } else {
                    employees.push(data[i]);
                }
            }
            page.setData({
                depts: depts,
                employees: employees
            });
        }

    });
}