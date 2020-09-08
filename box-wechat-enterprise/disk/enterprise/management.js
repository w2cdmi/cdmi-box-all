// disk/enterprise/management.js
Page({
  toEmployeesDepartments:function(){
        wx.navigateTo({
            url: 'employees-departments/employees-departments',
        })
    },
    toLeaveManagements:function(){
      wx.navigateTo({
        url: 'leaveManagement/leaveManagement',
      })
    }
})