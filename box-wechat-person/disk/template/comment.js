var commentClient = require("../module/comment.js");
var utils = require("../module/utils.js");

var commentCount = 10;  //一级评论分页条数
var childCommentCount = 4;  //子评论分页条数
var currentPage = 0;    //当前页
var commentList = [];   //评论列表信息
var commentTargetId = "";  //评论对象id
var commentOwnerId = "";   //评论对象所属人

/**
 * 初始化评论页面
 * targetId: 评论对象编号
 * ownerId: 评论对象所属人
 * page: 页面
 * commentCount: 一级评论每页条数
 * childCommentCount: 评论子评论显示条数
 */
function init(targetId, ownerId, page, commentCount, childCommentCount) {
    if (targetId == undefined || targetId == "" || page == undefined) {
        return;
    }
    commentTargetId = targetId;
    commentOwnerId = ownerId;

    commentCount = commentCount || 10;
    childCommentCount = childCommentCount || 4;
    currentPage = 0;
    commentList = [];

    isPraise(targetId, commentClient.TARGET_FILE, page);
    praiseCount(targetId, commentClient.TARGET_FILE, page);
    getPraiseList(targetId, commentClient.TARGET_FILE, page);
    getCommentCount(targetId, commentClient.TARGET_FILE, page);
    getCommentAndChildrenListForPage(page);
}


//判断是否已经点赞
function isPraise(targetId, targetType, page) {
    commentClient.isPraise(targetId, targetType, function (data) {
        page.setData({
            isPraised: data.praised,
            praisedId: data.id || ''
        })
    });
}
//获取点赞总数
function praiseCount(targetId, targetType, page) {
    commentClient.praiseCount(targetId, targetType, function (data) {
        page.setData({
            amount: data.amount || 0
        })
    });
}
//获取点赞头像
function getPraiseList(targetId, targetType, page) {
    //获取后五条点赞人信息
    commentClient.praiseList(targetId, targetType, 0, 5, function (data) {
        page.setData({
            praiseList: data
        })
    })
}
//获取总评论条数
function getCommentCount(targetId, targetType, page) {
    commentClient.commentsCount(targetId, targetType, function (data) {
        page.setData({
            commentCount: data.amount || 0,
            pageSize: Math.floor(data.amount / page.count)
        })
    });
}
//获取评论列表
// function getCommentListForPage(targetId, page) {
//     var tempCommentList = [];
//     //获取文件评论
//     commentClient.commentList(targetId, commentClient.TARGET_FILE, currentPage, commentCount, function (data) {
//         if (data != undefined && data != "" && data.length > 0) {
//             tempCommentList = data;
//             var fileCommentChildPromises = [];
//             tempCommentList.forEach((fileComment) => {
//                 //转换评论信息
//                 fileComment = convertFileComment(fileComment);
//                 //自己是否对评论点赞
//                 var isPraisePromise = new Promise(function (resolve, reject) {
//                     commentClient.isPraise(fileComment.id, commentClient.TARGET_COMMENT, function (data) {
//                         // page.setData({
//                         //     isPraised: data.praised
//                         // })
//                         resolve(data);
//                     });
//                 });
//                 fileCommentChildPromises.push(isPraisePromise);
//                 //评论的点赞总数
//                 var praiseCountPromise = new Promise(function (resolve, reject) {
//                     commentClient.praiseCount(fileComment.id, commentClient.TARGET_COMMENT, function (data) {
//                         // page.setData({
//                         //     amount: data.amount || 0
//                         // })
//                         resolve(data);
//                     });
//                 });
//                 fileCommentChildPromises.push(praiseCountPromise);
//                 //评论的子评论总数
//                 var commentCountPromise = new Promise(function (resolve, reject) {
//                     commentClient.commentsCount(fileComment.id, commentClient.TARGET_COMMENT, function (data) {
//                         // that.setData({
//                         //     commentCount: data.amount,
//                         //     pageSize: Math.floor(data.amount / page.count)
//                         // })
//                         resolve(data);
//                     });
//                 });
//                 fileCommentChildPromises.push(commentCountPromise);
//                 //评论的子评论列表
//                 var fileCommentChildPromise = new Promise(function (resolve, reject) {
//                     //根据一层评论请求二层评论
//                     commentClient.commentList(fileComment.id, commentClient.TARGET_COMMENT, 0, childCommentCount, function (data) {
//                         resolve(data);
//                     })
//                 });
//                 fileCommentChildPromises.push(fileCommentChildPromise);
//             });
//             Promise.all(fileCommentChildPromises).then(function (childComments) {
//                 if ((tempCommentList.length * 4) == childComments.length) {
//                     tempCommentList.map((item, index) => {
//                         tempCommentList[index].isPraised = childComments[index * 4].praised;
//                         tempCommentList[index].praiseId = childComments[index * 4].id || 0;
//                         tempCommentList[index].praiseCount = childComments[index * 4 + 1].amount || 0;

//                         tempCommentList[index].children = convertChildrenComment(childComments[index * 4 + 3]) || {};
//                         tempCommentList[index].totalCount = childComments[index * 4 + 2].amount || 0;
//                         if (tempCommentList[index].totalCount > childCommentCount) {
//                             tempCommentList[index].nextPage = 2;
//                         }
//                         tempCommentList[index].pageSize = Math.floor(childComments[index * 4 + 2].amount / childCommentCount) || 0;
//                     });
//                 } else {
//                     reject("get comment children fail");
//                 }
//                 if (currentPage > 0) {
//                     commentList = concat(commentList, tempCommentList);
//                 } else {
//                     commentList = tempCommentList;
//                 }
//                 //记录下次起始位置
//                 page.setData({
//                     commentList: commentList
//                 })
//             }).catch((errMsg) => {
//                 console.log(errMsg);
//             });
//         }
//     });
// }

//获取所有评论
function getCommentAndChildrenListForPage(page) {
    if (page.data.commentCount == 0){
        return;
    }
    if (page.data.commentCount != 0 && currentPage * commentCount > page.data.commentCount){
        // wx.showToast({
        //     title: '已经到没有了',
        // })
        return;
    }
    commentClient.getCommentAndChildrenListForPage(commentTargetId, currentPage, commentCount, function (tempCommentList) {
        if (tempCommentList == undefined || tempCommentList.length == []) {
            return;
        }

        var fileCommentChildPromises = [];
        tempCommentList.forEach((fileComment) => {
            //转换评论信息
            fileComment = convertFileComment(fileComment);
            //自己是否对评论点赞
            var isPraisePromise = new Promise(function (resolve, reject) {
                commentClient.isPraise(fileComment.id, commentClient.TARGET_COMMENT, function (data) {
                    resolve(data);
                });
            });
            fileCommentChildPromises.push(isPraisePromise);
        });
        Promise.all(fileCommentChildPromises).then(function (childComments) {
            if (tempCommentList.length == childComments.length) {
                tempCommentList.map((item, index) => {
                    var commentSize = 0;
                    if (commentList != undefined && commentList.length > 0) {
                        commentSize = commentList.length;
                    }
                    tempCommentList[index].index = commentSize + index;
                    tempCommentList[index].isPraised = childComments[index].praised;
                    tempCommentList[index].praiseId = childComments[index].id || 0;

                    tempCommentList[index].children = convertChildrenComment(tempCommentList[index].children) || {};
                    if (tempCommentList[index].commentNumber > childCommentCount) {
                        tempCommentList[index].cursor = 1;      //显示更多回复
                    }
                });
            } else {
                reject("get comment praised fail");
            }

            if (commentList == undefined || commentList.length == 0) {
                commentList = tempCommentList;
            } else {
                commentList = commentList.concat(tempCommentList);
            }

            currentPage = currentPage + 1;

            page.setData({
                commentList: commentList
            });
        })
    });
}

//获取评论的子评论信息
function getChildrenCommentList(targetId, nextPage, page) {
    var childrenCommentList = [];
    commentClient.commentList(fileComment.id, commentClient.TARGET_COMMENT, 0, childCommentCount, function (data) {
        childrenCommentList = convertChildrenComment(data);
        return childrenCommentList;
    })
};

//转换评论信息
function convertFileComment(data) {
    var comment = data;
    comment.content = unescape(data.content);

    return comment;
}
//转换二级评论信息 data: 数组
function convertChildrenComment(data) {
    var childrenComment = [];
    if (data == undefined || data.length == 0) {
        return childrenComment;
    }
    var comment = {};
    for (let i = 0; i < data.length; i++) {
        comment = data[i];
        var contents = unescape(data[i].content).split("@><@");
        comment.content = contents[0];
        if (contents.length >= 2) {
            comment.owner.showName = unescape(data[i].owner.name) + " 回复 " + contents[1];
        } else {
            comment.owner.showName = data[i].owner.name;
        }
        childrenComment.push(comment);
    }
    return childrenComment;
}


//点赞
function giveOrCancelPraise(e) {
    var page = this;
    let isPraised = e.currentTarget.dataset.isPraised || false;
    let praisedId = e.currentTarget.dataset.id || "";
    if (isPraised) {
        commentClient.cancelPraise(praisedId, function (data) {
            isPraise(commentTargetId, commentClient.TARGET_FILE, page);
            praiseCount(commentTargetId, commentClient.TARGET_FILE, page);
            getPraiseList(commentTargetId, commentClient.TARGET_FILE, page);
        });
    } else {
        commentClient.givePraise(commentTargetId, commentClient.TARGET_FILE, commentOwnerId, function (data) {
            isPraise(commentTargetId, commentClient.TARGET_FILE, page);
            praiseCount(commentTargetId, commentClient.TARGET_FILE, page);
            getPraiseList(commentTargetId, commentClient.TARGET_FILE, page);
        });
    }
}
//评论点赞
function commentGiveOrCancelPraise(e) {
    var page = this;
    var isPraised = e.currentTarget.dataset.isPraised || false;
    var target = e.currentTarget.dataset.target || {};
    var praiseId = e.currentTarget.dataset.praiseId || "";
    if (isPraised) {
        commentClient.cancelPraise(praiseId, function (data) {
            commentList[target.index].isPraised = false;
            commentList[target.index].praiseId = 0;
            if(commentList[target.index].praiseNumber > 0){
                commentList[target.index].praiseNumber = commentList[target.index].praiseNumber - 1;
            }
            page.setData({
                commentList: commentList
            });
        });
    } else {
        commentClient.givePraise(target.id, commentClient.TARGET_COMMENT, target.owner.id, function (data) {
            commentList[target.index].isPraised = true;
            commentList[target.index].praiseId = data.newid;
            commentList[target.index].praiseNumber = commentList[target.index].praiseNumber + 1;
            page.setData({
                commentList: commentList
            });
        });
    }
}

//记录输入内容
function bindTextInput(e) {
    this.setData({
        content: e.detail.value
    })
}
//点击输入框
function onClickInput(e) {
    var page = this;
    if (page.data.content == undefined || page.data.content == "") {
        page.data.currentCommentTargetId = commentTargetId;
        page.data.currentCommentTargetType = commentClient.TARGET_FILE;
        page.data.currentCommentTargetOwnerId = commentOwnerId;
    }
    this.setData({
        inputIsFocus: true
    })
}
//发送评论
function sendContent(e) {
    var page = this;
    var targetId = page.data.currentCommentTargetId;
    var commentObjectName = page.data.commentObjectName;
    var targetType = page.data.currentCommentTargetType;
    var ownerId = page.data.currentCommentTargetOwnerId;
    var index = page.data.currentCommentTargetIndex;
    var childIndex = page.data.currentCommentTargetIndex;
    var content = page.data.content;

    if (targetId != commentTargetId && commentObjectName != '') {
        content = content.substring(commentObjectName.length + 2);
        if (content.length <= 0) {
            return;
        }
        content = (content + "@><@" + commentObjectName);
    }
    content = escape(content);
    commentClient.commentTarget(targetId, targetType, ownerId, content, function (data) {
        var tempComment = {};
        if (targetType == commentClient.TARGET_FILE) {
            tempComment = convertFileComment(data);
            tempComment.isPraised = false;
            tempComment.praiseId = 0;
            tempComment.children = [];

            if (commentList == undefined || commentList.length == 0) {
                tempComment.index = 0;
                commentList = [tempComment];
            } else {
                commentList = [tempComment].concat(commentList);
                commentList.map((item, index) => {
                    item.index = index;
                })
            }
            var commentCount = (page.data.commentCount || 0) + 1;
            page.setData({
                commentCount: commentCount
            });
        }else{
            if(index != undefined && commentList != undefined){
                tempComment = commentList[index];
                var childComments = convertChildrenComment([data]);
                //更新回复评论信息
                var childrenComment = tempComment.children;
                if (childrenComment == undefined || childrenComment.length == 0){
                    childrenComment = childComments;
                }else{
                    childrenComment = childComments.concat(childrenComment);
                }
                tempComment.children = childrenComment;
                tempComment.commentNumber = (tempComment.commentNumber || 0) + 1;

                commentList[index] = tempComment;
            }
        }

        page.setData({
            content: '',
            commentList: commentList
        });
    });
}

//直接回复某人评论
function commentReply(e) {
    var page = this;
    var comment = e.currentTarget.dataset.target;

    page.data.currentCommentTargetId = comment.id;
    page.data.currentCommentTargetType = commentClient.TARGET_COMMENT;
    page.data.currentCommentTargetOwnerId = comment.owner.id;
    page.data.currentCommentTargetIndex = comment.index;

    page.setData({
        commentObjectName: '',
        inputIsFocus: true
    });
}
//@某人回复评论
function replySomeoneComment(e) {
    var page = this;
    var comment = e.currentTarget.dataset.target;

    page.data.currentCommentTargetId = e.currentTarget.dataset.targetId;
    page.data.currentCommentTargetType = commentClient.TARGET_COMMENT;
    page.data.currentCommentTargetOwnerId = e.currentTarget.dataset.targetOwnerId;
    page.data.currentCommentTargetIndex = e.currentTarget.dataset.index;

    page.setData({
        content: "@" + comment.owner.name + " ",
        commentObjectName: comment.owner.name,
        inputIsFocus: true
    });
}

//查看更多回复信息
function moreReplyMessage(e) {
    var page = this;
    var currentComment = e.currentTarget.dataset.target;
    commentClient.commentList(currentComment.id, commentClient.TARGET_COMMENT, currentComment.cursor, childCommentCount, function (data) {
        var newChildrenComment = convertChildrenComment(data);
        if (currentComment.commentNumber > (currentComment.cursor + 1) * childCommentCount) {
            currentComment.cursor += 1;      //显示更多回复
        }else{
            currentComment.cursor = undefined;
        }
        currentComment.commentNumber += 1;
        var childrenComment = currentComment.children;
        if (childrenComment.length == 0){
            childrenComment = newChildrenComment
        }else{
            childrenComment = childrenComment.concat(newChildrenComment);
        }
        currentComment.children = childrenComment;

        commentList[currentComment.index] = currentComment;
        page.setData({
            commentList: commentList
        });
    })
}

function getMorePraiseList() {
    wx.navigateTo({
        url: '/disk/comments/thumbslist/thumbslist?linkCode=' + this.data.linkCode,
    })
}

module.exports = {
    init,
    giveOrCancelPraise,
    getMorePraiseList,
    commentGiveOrCancelPraise,
    bindTextInput,
    onClickInput,
    sendContent,
    commentReply,
    replySomeoneComment,
    getCommentAndChildrenListForPage,
    moreReplyMessage
};