Component({
    options: {
        multipleSlots: true // 在组件定义时的选项中启用多slot支持
    },
    properties: {
        title: {
            type: String,
            value: '请输入收件箱名'
        },
        createCancel: {
            type: String,
            value: '取消'
        },
        createConfirm: {
            type: String,
            value: '确定'
        },
        createFailText: {
            type: String,
            value: '不能出现特殊字符'
        },
        placeholderText: {
            type: String,
            value: '不能输入特殊字符'
        },
        createFailTextBtn: {
            type: String,
            value: '知道了'
        }
    },

    data: {
        showPopup: false,
        showInputModal: false,
        showErrorModal: false,
        folderName: ''
    },

    // 组件的方法列表
    methods: {
        // 显示输入框
        showInputModal: function () {
            this.setData({
                showPopup: true,
                showInputModal: true,
            });
        },

        // 显示报错
        showErrorModal: function () {
            this.setData({
                showPopup: true,
                showInputModal: false,
                showErrorModal: true,
            });
        },

        // 关闭弹出框
        hideModal: function () {
            this.setData({
                showPopup: false,
                showInputModal: false,
                showErrorModal: false,
            });
        },

        inputChange: function (e) {
            this.setData({
                folderName: e.detail.value
            });
        },

        getInputValue: function (e) {
            return this.data.folderName
        },
        
        // 清空输入框
        clearInput: function (e) {
            this.setData({
                folderName: ''
            });
        },

        // 输入界面的关闭按钮触发事件
        _onCreateFolderCancel: function () {
            this.triggerEvent("onCreateFolderCancel");
        },

        // 输入界面的确定按钮触发事件
        _onCreateFolderConfirm: function () {
            this.triggerEvent("onCreateFolderConfirm");
        },

        // 创建文件夹失败的弹出框，文本提示关闭按钮触发事件
        _onCreateFolderFail: function () {
            this.triggerEvent("onCreateFolderFail");
        },

    }
})