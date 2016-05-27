<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#saveReport").click(function(){
		
	});
});
function saveForm(searchAreaId,gridId) {
	try{
		var sdata =$('#'+searchAreaId).serializeObject();
		var saveAction = $('#'+searchAreaId).attr("action");
		//console.log(sdata);
		$.ajax({
            url: saveAction,
            type: 'post',
            dataType: 'json',
            data:sdata,
            async:false,
            error: function(data){
            	alert("系统错误！");
            },
            success: function(data){
            	alert(data.message);
            	$("#"+gridId).jqGrid("setGridParam", {
    				search : true
    			}).trigger("reloadGrid", [ {
    					page : 1
    			}]);
            }
        });
	}catch(e){
		alert(e.message);
	}
}
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form id="reportform" class="form-horizontal pageForm" role="form" action="report/save">
				<div class="form-group">
					<label class="col-sm-2 control-label">报表编码:</label>
					<div class="col-sm-4">
						<input type="text" name="code" class="form-control input-sm" value="${report.code}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">报表名称:</label>
					<div class="col-sm-4">
						<input type="text" name="name" class="form-control input-sm" value="${report.name}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">报表类别:</label>
					<div class="col-sm-4">
						<input type="text" name="type" class="form-control input-sm" value="${report.type}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">数据源SQL:</label>
					<div class="col-sm-4">
						<textarea rows="6" name="dataSource" class="form-control" >${report.dataSource}</textarea>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">数据源描述:</label>
					<div class="col-sm-4">
						<textarea  name="dsDesc" class="form-control">${report.dsDesc}</textarea>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">备注:</label>
					<div class="col-sm-4">
						<textarea  name="remark" class="form-control input-sm">${report.remark}</textarea>
					</div>
				</div>
				<div class="form-group form-actions">
					<div class="col-sm-2"></div>
					<div class="col-sm-4">
					<input id="saveReport" class="btn btn-primary" type="button" onclick="saveForm('reportform','report_gridtable')" value="保 存"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="清空" onclick="history.go(-1)"/>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>