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
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form role="form" class="form-horizontal">
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
					<input id="saveReport" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="清空" onclick="history.go(-1)"/>
				</div>
			</form>
		</div>
	</div>
</body>
</html>