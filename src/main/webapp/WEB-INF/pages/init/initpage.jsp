<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	
	$("#createTable").click(function(){
		$.ajax({
			url: 'init/createTable',
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
		<button id="createTable" type="button" class="btn btn-primary"">
					建表
		</button>
		</div>
	</div>
</body>
</html>