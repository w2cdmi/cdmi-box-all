/**
 * Created by quxiangqian on 2017/12/28.
 */

(function ($) {
    $.fn
        .extend({
            Uploader: function (opts) {
                var self = this;
                var size = '';
                var dialogs = $("body div[dialogs='true']");
                var dropZone;
                var uploadRect;
                var filelength = []; //总数
                var initnum = 1; //当前上传数量 ++
                var innerdialog = '<div class="dialog" id="uploadmodel" z-index: 0;"><div class="model"></div></div' +
                    '>';
                var uploadSuccessFile = [];
                var uploadErrorFile = []

                function getUploadUrl(file) {

                    var preUrl
                    if (file.source.source.webkitRelativePath !== '') {
                        var par = {
                            "parent": self.folderId,
                            "name": file.name,
                            "size": file.size,
                            "path": file.source.source.webkitRelativePath
                        }
                    } else if (file.source.source.fullPath !== undefined) {
                        var par = {
                            "parent": self.folderId,
                            "name": file.name,
                            "size": file.size,
                            "path": file
                                .source
                                .source
                                .fullPath
                                .replace('/', '')
                        }
                    } else {
                        var par = {
                            "parent": self.folderId,
                            "name": file.name,
                            "size": file.size,
                            "path": ''
                        }
                    }

                    $.ajax({
                        url: host + "/ufm/api/v2/files/" + self.ownerId,
                        type: "PUT",
                        async: false,
                        data: JSON.stringify(par),
                        success: function (data) {
                            //data.fileId;
                            preUrl = data.uploadUrl + "?objectLength=" + file.size;
                        },
                        error: function (request) {
                            err = jQuery
                                .parseJSON(request.responseText)
                                .code
                        }
                    });

                    return preUrl
                }

                function getspaceQuota() {
                    $.ajax({
                        type: "GET",
                        url: host + "/ecm/api/v2/users/" + curUserId,
                        error: function () {
                            $.Tost("获取用户存储空间信息失败", "cancel");
                        },
                        success: function (data) {
                            size = data.spaceQuota - data.spaceUsed;
                            if (data.spaceQuota == -1) {
                                $("#spaceBar").html(formatFileSize(data.spaceQuota));
                                $("#useSpace").html(formatFileSize(data.spaceUsed))
                            } else {
                                $("#spaceBar").html(formatFileSize(data.spaceQuota));
                                $("#useSpace").html(formatFileSize(data.spaceUsed))
                            }
                        }
                    });
                }

                $('#closeModal').click(function () {
                    uploadErrorFile = [];
                    filelength = [];
                    initnum = 1;
                    $('#uploadmodel').css('display', 'none');
                    $('#uploadModal').hide();
                    $('#showUploadedNum').html('0'); // 清空上传
                    $('#showUploadTotalNum').html('0'); // 清空上传
                    $('.uploadlist').html(filelength); // 重置列表
                    if (self.onUploadSuccesss) {
                        self.onUploadSuccesss()
                    }
                    if (self.onUploadSuccessfile) {
                        self.onUploadSuccessfile()
                    }
                    getspaceQuota();
                })

                self.init = function () {
                    getspaceQuota();
                    var message = "上传完成";
                    if (!WebUploader.Uploader.support()) {
                        alert('Web Uploader 不支持');
                        throw new Error('WebUploader does not support the browser you are using.');
                    }
                    if (!$("body div[dialogs='true']").is("div")) {
                        $("body").append('<div dialogs="true" class="dialogs"></div>')
                    }


                    function drawUploadRect() {
                        if ($('.upload_rect').length <= 2) {
                            self.model = $('<input type="file" id="drop-zone" style=" position:absolute; right:0px; top:0px;' +
                                ' opacity:0; width:100%; height: 100%; border: 2px dashed #ccc; background: rgba(' +
                                '255,255,255,0.5);" class="upload-drop-zone" multiple webkitdirectory=""/>')
                            self.dialog = $('<div class="dialog upload_rect" id="upload_rect" style="width:99%; height: 99%; ' +
                                'margin: 3px auto; border: 2px dashed #ccc; background: rgba(255,255,255,0.5);"  ' +
                                'id="upload_rect"><p style="position:absolute; top: 50%;left: 50%;font-size: 50px' +
                                ';margin-left: -4rem;margin-top: -8%; color: #ccc;">在此释放以上传</p></div>');
                            self
                                .dialog
                                .append(self.model);
                            dialogs.append(self.dialog);
                            dropZone = document.getElementById('drop-zone');
                            uploadRect = document.getElementById('upload_rect');

                            document
                                .body
                                .addEventListener('dragenter', function (e) {

                                    e.stopPropagation();
                                    e.preventDefault();
                                    $('.upload_rect').css('display', 'none');
                                    self
                                        .dialog
                                        .show();
                                }, false);

                            self
                                .dialog[0]
                                .addEventListener('dragleave', function (e) {

                                    e.stopPropagation();
                                    e.preventDefault();
                                    self
                                        .dialog
                                        .hide()
                                }, false);

                            dropZone.addEventListener('drop', function (e) {
                                self
                                    .dialog
                                    .hide();
                            }, false);
                            $('.upload_rect').hover(function () {
                                self
                                    .dialog
                                    .hide();
                            });

                        }
                    }

                    drawUploadRect();
                    var uploader = WebUploader.create({
                        server: self.attr("server"),
                        pick: {
                            id: self.get(0),
                            // innerHTML: '<i class="fa fa-cloud-upload"></i>上传',
                            webkitdirectory: opts.webkitdirectory
                        },
                        resize: false,
                        // paste: document.body,
                        dnd: self.model[0],
                        threads: 5,
                        fileVal: 'MyFiledName',
                        auto: true,
                        compress: null,
                        duplicate: true
                    });

                    $("body").delegate("#status button", "click", function () {
                        $('#showUploadedNum').html('0');
                        var rindex = $(this).parents().parents().attr("id");
                        // var deid = $('.item-upload')
                        //     .eq(rindex)
                        //     .attr("id");
                        var file = uploader.getFile(rindex);
                        if (file != null) {
                            file.retry = true;
                            uploader.retry(file);
                        }
                    });
                    //当文件被加入队列之前触发
                    uploader.on('beforeFileQueued', function (file) {

                        $('#uploadmodel').css('display', 'block');
                        $('#uploadModal').show();
                        var file_id = file.id; //文件id

                        if (file.size == 0) {
                            return false;
                        }
                        if (file.size > size) {
                            var html = '';
                            html += '<div class="pb-wrapper">' +
                                '<div class="pb-container">' +
                                '<div class="pb-text">' +
                                '<ul id="' + file_id + '" class="item-upload">' +
                                '<li style="overflow: hidden;white-space: nowrap;text-overflow: ellipsis;max-width: 350px; width:50%;">' + file.name + '</li>' +
                                '<li style="width:20%; margin-left: 20px;">' + formatFileSize(file.size) + '</li>' +
                                '<li id="status"  class="status"></li>' +
                                '</ul>' +
                                '</div>' +
                                '<div class="pb-value" id="pb-value"></div>' +
                                '</div>' +
                                '</div>'

                            // $('#inneruploadQueue').append(html)
                            $('.uploadlist').append(html)
                            uploadErrorFile.push(file);
                            console.log(uploadErrorFile);
                            $('#' + file_id + '')
                                .find('#status')
                                .html('空间不足')
                                .css('color', 'red');
                            return false;
                        }

                    });
                    // 当文件被加入队列以后触发
                    uploader.on('fileQueued', function (file) {
                        console.log(file);
                        $('#closeModal').css('display', 'none');
                        console.log(initnum);
                        uploadErrorFile = [];
                        var file_id = file.id; //文件id
                        $('body').append(innerdialog)
                        $('#uploadmodel').css('display', 'block')
                        // $('#uploadmodel').show();
                        var filename = [file.name];
                        filelength.push(filename)
                        $('.modal').show();
                        $('#uploadFinishedModal').css('display', 'none')
                        $('#showUploadTotalNum').html(filelength.length);
                        $('#showUploadedNum').html(initnum);
                        var html = '';
                        html += '<div class="pb-wrapper">' +
                            '<div class="pb-container">' +
                            '<div class="pb-text">' +
                            '<ul id="' + file_id + '" class="item-upload">' +
                            '<li style="overflow: hidden;white-space: nowrap;text-overflow: ellipsis;max-width: 350px; width:50%;">' + file.name + '</li>' +
                            '<li style="width:20%; margin-left: 20px;">' + formatFileSize(file.size) + '</li>' +
                            '<li id="status"  class="status"></li>' +
                            '</ul>' +
                            '</div>' +
                            '<div class="pb-value" id="pb-value"></div>' +
                            '</div>' +
                            '</div>'
                        // $('#inneruploadQueue').append(html)
                        $('.uploadlist').append(html)
                    });
                    //某个文件开始上传前触发，一个文件只会触发一次。
                    uploader.on('uploadStart', function (file) {
                        var file_id = file.id; //文件id

                        $('#' + file_id + '')
                            .find('#status')
                            .html('正在上传');
                        file.timestamp = +new Date();

                        var preurl = getUploadUrl(file);
                        uploader.options.server = preurl;

                    })
                    //上传过程中触发，携带上传进度
                    uploader.on('uploadProgress', function (file, percentage) {
                        var file_id = file.id; //文件id
                        $('#' + file_id + '').parent().parent().find('#pb-value').css("width", parseInt(percentage * 100) + "%");
                    });
                    //当某个文件上传到服务端响应后
                    uploader.on('uploadAccept', function (file, response) {});
                    //当文件上传成功时触发。
                    uploader.on('uploadSuccess', function (file, response) {
                        console.log(uploadErrorFile);
                        console.log(response);
                        var file_id = file.id; //文件id
                        if (response.code == 'OK') {
                            $('#showUploadedNum').html(initnum++)
                            $('#' + file_id + '')
                                .find('#status')
                                .html('上传成功');
                        }
                    })
                    //当文件上传出错时触发
                    uploader.on('uploadError', function (file, reason) {
                        uploadErrorFile.push(file)
                        console.log(uploadErrorFile);
                        var file_id = file.id; //文件id
                    });
                    //当某个文件的分块在发送前触发
                    uploader.on('uploadBeforeSend', function (chunk, data) {
                        var file_id = data.id;

                        data.timestamp = chunk.file.timestamp;
                        // $('#showUploadedNum').html(initnum++)
                    });
                    //当validate不通过时，会以派送错误事件的形式通知调用者   保存此代码
                    // uploader.on('error', function (type) {
                    //   if(type == 'F_DUPLICATE') {
                    //     $.Alert('文件已存在，请勿重新上传');
                    //     return;
                    //   }

                    // })
                    //不管成功或者失败，文件上传完成时触发
                    uploader.on('uploadComplete', function (file) {
                        console.log(file);
                        var file_id = file.id;
                        // uploadSuccessFile.push(file);
                        if (file.retry == true) {
                            $('#showUploadedNum').html(initnum++)
                            $('#' + file_id + '')
                                .find('#status')
                                .html('上传成功');
                        } else {
                            if (file.statusText == 'abort') {
                                $('#' + file_id + '').find('#status').html('');
                                $('#' + file_id + '').find('#status').append('<button>上传失败，请重新上传</button>').addClass('qqqqq');
                            }
                            if (file.statusText == 'http') {
                                $('#' + file_id + '').find('#status').html('空间不足').css('color', 'red');
                            }
                        }

                    })
                    uploader.on('uploadFinished', function () {
                        console.log(uploadErrorFile);
                        if (uploadErrorFile.length == 0) {
                            uploadErrorFile = [];
                            filelength = [];
                            initnum = 1;
                            $('#showUploadedNum').html('0'); // 清空上传
                            $('#showUploadTotalNum').html('0'); // 清空上传
                            $('#uploadmodel').css('display', 'none');
                            $('.modal').css('display', 'none');
                            $('.uploadlist').html(filelength); // 重置列表
                            $.Tost(message, function () {
                                    self
                                        .dialog
                                        .hide();
                                    if (self.onUploadSuccesss) {
                                        self.onUploadSuccesss()
                                    }
                                    if (self.onUploadSuccessfile) {
                                        self.onUploadSuccessfile()
                                    }
                                })
                                .show()
                                .autoHide(2000);
                            getspaceQuota();
                        } else {
                            console.log(1);
                            $('body').append(innerdialog);
                            $('#uploadmodel').show();
                            $('#closeModal').css('display', 'block');
                        }
                        // uploader.reset()

                    })
                }
                return self;
            }
        })
})(jQuery)