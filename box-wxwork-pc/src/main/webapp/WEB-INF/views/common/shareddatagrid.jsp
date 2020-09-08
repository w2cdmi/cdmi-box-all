<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<style>
    .left>div span{
        display: inline-block;
        border: 1px solid #EAEAEA;
        padding: 0px 15px;
        height: 30px;
        line-height: 30px;
    }
</style>
<div id="toolbar" class="cl">
        <div class="left">
                <span id="upload_button"></span>
                <div>
                <span id="upload_buttons"><i class="fa fa-cloud-upload"></i>上传文件夹</span>
                <span id="upload_file"><i class="fa fa-cloud-upload"></i>上传文件</span>
                <button id="new-folder-button"><i class="fa fa-plus"></i>新建文件夹</button>
                </div>
            </div>
            <div class="right">
        <button id="sort_buttons"><i class="fa fa-sort-alpha-asc"></i>排序</button>
        <div class="popover bottom" id="sort_popover">
            <div class="arrow" style="left: 30px;"></div>
            <dl class="menu">
                <dt id="orderField_modifiedAt"><i class="fa fa-long-arrow-down" style="visibility: hidden"></i>创建时间</dt>
                <dt id="orderField_name"><i class="fa fa-long-arrow-down" style="visibility: hidden"></i>文件名</dt>
            </dl>
        </div>
    </div>
</div>
<div id="breadcrumd" class="cl">

</div>
<!-- <div class="modal hide" id="uploadModal" style="display: none;">
	<div class="modal-header">
		<h3>上传列表(<span id="showUploadedNum">0</span>/<span id="showUploadTotalNum">0</span>)</h3>
	</div>
	<div class="modal-body">
		<div id="uploadQueue">
            <ul>
            </ul>
        </div>
	</div>
</div> -->
<div class="abslayout" style="bottom: 16px;top:160px;right: 0;left: 0;min-height: 200px">
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
        <div class="notfind" style="display: none">
            <p>暂无数据</p>
        </div>
    </div>
    <div class="abslayout perpagebar" style="left:16px;right:16px;bottom: 0px;" id="pagination">
        <div class="left" style="display: inline-block">
            <span id="totalSize">总记录数：0</span>&nbsp;&nbsp;<span id="currentPage">当前页：1</span>&nbsp;&nbsp;<span id="totalPages">总页数：0</span>
        </div>
        <div class="right" style="display: inline-block;margin-left: 16px;">
            <button id="firstPage">首页</button>
            <button id="prePage">上页</button>
            <button id="nextPage">下页</button>
            <button id="lastPage">尾页</button>
        </div>
    </div>
    <div id="createNewFolderDialog" style="display:none">
        <form class="form">
            <dl>
                <dt>


                    <label>名称：</label>
                    <input id="name" name="name" placeholder="请输入文件夹名" style="width: 200px">
                </dt>
            </dl>
            <div class="form-control" style="text-align: right">
                <button type="button" id="cancel_button">取消</button>
                <button type="button" id="ok_button">确定</button>
            </div>
        </form>
    </div>
</div>
</div>
