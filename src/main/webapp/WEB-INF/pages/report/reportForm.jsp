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
            	alertMsg.error("系统错误！");
            },
            success: function(data){
            	alert(data);
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
			<form id="reportform" class="form-horizontal" action="report/save">
				<div class="control-group">
				<label class="control-label">报表编码:</label>
					<input type="text" name="code" class="input-xlarge"/>
				</div>
				<div class="control-group">
					<label class="control-label">报表名称:</label>
					<input type="text" name="name"/>
				</div>
				<div class="control-group">
					<label class="control-label">报表类别:</label>
					<input type="text" name="type"/>
				</div>
				<div class="form-actions">
					<input id="saveReport" class="btn btn-primary" type="button" onclick="saveForm('reportform','report_gridtable')" value="保 存"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="清空" onclick="history.go(-1)"/>
				</div>
			</form>
		</div>
	</div>
</body>
</html>