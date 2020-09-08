var config = require("../config.js");
var payClient = require("../module/pay.js");
var productClient = require("../module/product.js");
var util = require("../module/utils.js");

var storageSize = 1;  //默认3G
var dateSize = 3;     //默认时长
var unitPrice = 2;    //每月每G/每人 5元
var totalMoney = 1 * 3 * 2;     //1G存储 * 1个月 * 每月每G/每人 5元

var TYPE_PERSON = 1;    //类型： 个人购买

var buyType = 0;
var productId = 0;
var level = 0;          //产品等级

Page({

    /**
     * 页面的初始数据
     */
    data: {
        storageSize: storageSize,
        dateSize: dateSize,
        totalMoney: totalMoney
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        wx.setNavigationBarTitle({ title: "扩展容量" });
        var page = this;

        productClient.getProducts(function(data){
            if (typeof (data) != 'undefined' && data.length > 0){
                var data = convertProducts(data);
                //设置初始产品，初始存储大小
                productId = data[0].id;
                storageSize = data[0].accountSpace;
                page.setData({
                    products: data,
                    storageSize: data[0].accountSpace
                });

                getDurationsByProductId(productId, page);
            } else if (data.length == 0){
                wx.showToast({
                    title: "获取产品失败！",
                })
                wx.navigateBack({
                    delta: 1
                })
            }
        });
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    selectStorage: function(e){
        var page = this;
        storageSize = e.currentTarget.dataset.storageSize;
        productId = e.currentTarget.dataset.productId;
        page.setData({
            storageSize: storageSize
        });
        getDurationsByProductId(productId, page);
    },
    selectDate: function(e){
        var page = this;
        dateSize = e.currentTarget.dataset.dateSize;
        var productId = e.currentTarget.dataset.productId;

        previewOrder(productId, dateSize, page);
    },
    payMoney:function(){
        var page = this;
        wx.login({
            success: function (res) {
                if (res.code) {
                    //获取用户openId
                    payClient.getOpenId(res.code, function(data){
                        if (data != ""){
                            //统一下单
                            page.unifiedorder(data);
                        }else{
                            console.info("获取openId失败");
                        }
                    });
                } else {
                    console.log('获取用户登录状态失败！' + res.errMsg)
                }
            }
        });
    },
    unifiedorder: function (openId){
        var page = this;
        if (typeof (openId) != "undefined" && openId != "") {
            var data = {
                openId: openId,
                productId: productId,
                duration: dateSize,
                mpId: config.mpId
            }
            payClient.unifiedorder(data, function (data) {
                wx.requestPayment({
                    'timeStamp': data.timeStamp,
                    'nonceStr': data.nonceStr,
                    'package': data.package,
                    'signType': data.signType,
                    'paySign': data.paySign,
                    'success': function (res) {
                        wx.navigateBack({
                            delta: 1,
                        });
                    },
                    'fail': function (res) {
                        wx.showToast({
                            title: '支付失败，请稍后重试',
                            icon: 'none',
                            duration: 1000
                        })
                    }
                })
            })
        } else {
            console.info("参数异常，openId：" + openId);
        }
    }
})

function convertProducts(data){
    if (typeof (data) != 'undefined' && data.length > 0){
        for(var i = 0; i < data.length; i++ ){
            data[i].accountSpace = productClient.formatStorageSize(data[i].accountSpace)
            data[i].accountSpaceName = data[i].accountSpace + "G";
        }
        return data;
    }else{
        return [];
    }
}
//获取一个容量，有几种套餐（月份不一样）
function getDurationsByProductId(productId, page){
    productClient.getProductDuration(productId, function (data) {
        if (data == undefined) {
            page.setData({
                durations: [],
                totalMoney: 0
            })
        } else {
            previewOrder(productId, data[0].duration, page);
            page.setData({
                durations: data,
                totalMoney: data[0].price
            })
        }
    });
}
//预览订单信息
function previewOrder(productId, dateSize, page){
    page.setData({
        dateSize: dateSize
    });
    var data = {
        productId: productId,
        duration: dateSize
    }
    payClient.previewOrder(data, function (data) {
        var now = new Date();
        page.setData({
            expireDate: util.formatDate(now.setMonth(now.getMonth() + dateSize)),
            totalPrice: (data.totalPrice / 100).toFixed(2),
            surplusCost: (data.surplusCost / 100).toFixed(2),
            payMoney: (data.payMoney / 100).toFixed(2)
        });
    });
}
