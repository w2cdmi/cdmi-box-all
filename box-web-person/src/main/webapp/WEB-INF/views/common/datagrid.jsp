<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
    <style>
        #uploadModal {
            width: 630px;
            height: 400px;
        }

        #uploadFinishedModal {
            top: 50%;
            margin-left: -380px;
            width: 800px;
            height: 500px;
        }

        .modal {
            position: fixed;
            bottom: 16px;
            right: 10px;
            z-index: 1050;
            width: 560px;
            background-color: #fff;
            -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
            -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
            box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
        }

        #uploadModal .modal-header {
            padding: 15px 15px;
            border-bottom: 1px solid #eee;
            background: #F9F9F9;
        }

        #uploadModal .modal-header h3 {
            font-size: 18px;
            font-weight: normal;
            margin: 0;
            line-height: 30px;
        }

        .modal-body {
            height: 338px;
            overflow-y: auto;
        }

        #uploadQueue {
            padding-bottom: 20px;
        }

        #uploadQueue table>thead>tr>th {
            background: #fff;
            color: #A5A5A5;
        }

        #uploadQueue .uploadlistul {
            padding: 17px 20px;
            border-bottom: 1px solid #e8e8e8;
            color: #A5A5A5;
            font-weight: bold;
            line-height: 3px;
        }

        #uploadQueue .uploadlist ul {
            overflow: hidden;
            padding: 0 20px;
            height: 30px;
            line-height: 30px;
            border-bottom: 1px solid #e8e8e8;
        }

        #uploadQueue .uploadlist li {
            float: left;
        }

        #uploadQueue .uploadlistul li {
            float: left;
        }

        .inneruploadQueue tr {
            width: 0px;
            height: 25px;
            background: url('${ctx}/static/assets/images/pro.png') no-repeat;
            text-align: center;

            font-family: Tahoma;
            font-size: 14px;
            line-height: 25px;
        }

        #keyword::-ms-clear {
            display: none;
            width: 0;
            height: 0;
        }

        .left>div span {
            display: inline-block;
            border: 1px solid #EAEAEA;
            padding: 0px 15px;
            height: 30px;
            line-height: 30px;
        }

        .pb-wrapper {
            position: relative;
            background: #fff;
            border-bottom: 1px solid #EAEAEA;
        }

        .pb-container {
            height: 30px;
            position: relative;
        }

        .pb-text {
            width: 100%;
            position: absolute;
        }

        .pb-value {
            height: 100%;
            width: 0;
            background: #F5F5F5;
        }
    </style>
    <div id="toolbar" class="cl">
        <div class="left">
            <!-- <input type="file" webkitdirectory name="上传" id=""> -->
            <span id="upload_button"></span>
            <div>
                <span id="upload_buttons">
                    <i class="fa fa-cloud-upload"></i>上传文件夹</span>
                <span id="upload_file">
                    <i class="fa fa-cloud-upload"></i>上传文件</span>
                <button id="newFolder_button">
                    <i class="fa fa-plus"></i>新建文件夹
                </button>
            </div>


        </div>
        <div class="right">
            <button id="sort_button">
                <i class="fa fa-sort-alpha-asc"></i>排序
            </button>
            <div class="popover bottom" id="sort_popover">
                <div class="arrow" style="left: 50px;"></div>
                <dl class="menu">
                    <dt id="orderField_modifiedAt">
                        <i class="fa fa-long-arrow-down" style="visibility: hidden"></i>创建时间
                    </dt>
                    <dt id="orderField_name">
                        <i class="fa fa-long-arrow-down" style="visibility: hidden"></i>文件名
                    </dt>
                </dl>
            </div>
            <form id="searchform" style="display: inline-block" onsubmit="return false;">
                <div style="position: relative;">
                    <div style="position: relative;display: inline-block;">
                        <div class="cancel" style="display: none">
                            <i class="fa fa-times"></i>
                        </div>
                        <input id="keyword" placeholder="请输入关键字查询" />
                        <button type="button" id="search">
                            <i class="fa fa-search"></i>查找
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div id="breadcrumd" class="cl">
    </div>
    <div class="modal hide" id="uploadModal" style="display: none;">
        <div class="modal-header">
            <h3>上传列表(
                <span id="showUploadedNum">0</span> /
                <span id="showUploadTotalNum">0</span>)
                <i id="closeModal" style="float: right;
                font-size: 23px;
                line-height: 30px;" class="fa fa-times"></i>
            </h3>
            <!-- <p id="Successlist" style="display: none;">上传完成：<span id="Success"></span>个成功，<span id="Error"></span>个失败</p> -->
        </div>
        
        <div class="modal-body">
            <div id="uploadQueue">
                <!-- <table>
                    <thead>
                        <th>文件名</th>
                        <th>大小</th>
                        <th>状态</th>
                    </thead>
                    <tbody id="inneruploadQueue">
                    </tbody>
                </table> -->
                <ul class="uploadlistul">
                    <li style="width: 50%;">文件名</li>
                    <li style="width: 20%; margin-left: 20px;">大小</li>
                    <li>状态</li>
                </ul>
                <div class="uploadlist">
                </div>
            </div>
        </div>

    </div>
    <div class="abslayout" style="bottom: 16px;top:160px;right: 0;left: 240px;min-height: 200px">
        <table style="padding: 16px;padding-bottom: 0;padding-top: 0;">
            <thead>
                <tr>
                    <th style="min-width: 280px">文件名</th>
                    <th style="width: 100px"> 大小</th>
                    <th style="width: 180px">创建时间</th>
                </tr>
            </thead>
        </table>
        <div class="abslayout" style="left:16px;right:16px;overflow: auto;bottom: 50px;top:40px;" id="datagrid">
            <table style="padding-top: 0">
                <tbody>
                </tbody>
            </table>
            <div class="notfind" style="display: none;height: 18px;">
                <p>暂无数据</p>
            </div>
        </div>
        <div class="abslayout perpagebar" style="left:16px;right:16px;bottom: 0px;" id="pagination">
            <div class="pageleft" style="display: inline-block">
                <span id="totalSize">总记录数：0</span>&nbsp;&nbsp;
                <span id="currentPage">当前页：1</span>&nbsp;&nbsp;
                <span id="totalPages">总页数：1</span>
            </div>
            <div class="right" style="display: inline-block;margin-left: 16px;">
                <button id="firstPage">首页</button>
                <button id="prePage">上页</button>
                <button id="nextPage">下页</button>
                <button id="lastPage">尾页</button>
            </div>
        </div>
    </div>