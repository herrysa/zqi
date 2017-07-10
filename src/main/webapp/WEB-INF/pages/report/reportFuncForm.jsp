<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	
});
function saveReportFunc(searchAreaId,gridId) {
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
			<form id="reportFuncform" class="form-horizontal pageForm" role="form" action="reportFunc/save">
				<div class="form-group">
					<label class="col-sm-2 control-label">函数编码:</label>
					<div class="col-sm-4">
						<input type="text" name="code" class="form-control input-sm" value="${reportFunc.code}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">函数名称:</label>
					<div class="col-sm-4">
						<input type="text" name="name" class="form-control input-sm" value="${reportFunc.name}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">函数分类:</label>
					<div class="col-sm-4">
						<input type="text" name="category" class="form-control input-sm" value="${reportFunc.category}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">函数类别:</label>
					<div class="col-sm-4">
						<select class="form-control" name="type">
							<option value="sql">sql</option>
							<option value="java">java</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">返回值类别:</label>
					<div class="col-sm-4">
						<select class="form-control" name="rsType">
							<option value="1">单值</option>
							<option value="2">多值</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">返回值类型:</label>
					<div class="col-sm-4">
						<select class="form-control" name="returnType">
							<option value="string">string</option>
							<option value="duble">duble</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">参数:</label>
					<div class="col-sm-4">
						<textarea rows="3" name="params" class="form-control" >${reportFunc.params}</textarea>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">函数体:</label>
					<div class="col-sm-4">
						<textarea rows="3" name="funcSql" class="form-control" >${reportFunc.funcSql}</textarea>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">备注:</label>
					<div class="col-sm-4">
						<textarea  name="remark" class="form-control input-sm">${reportFunc.remark}</textarea>
					</div>
				</div>
				<div class="form-group form-actions">
					<div class="col-sm-2"></div>
					<div class="col-sm-4">
					<input class="btn btn-primary" type="button" onclick="saveReportFunc('reportFuncform','reportFunc_gridtable')" value="保 存"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="清空" onclick="history.go(-1)"/>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>