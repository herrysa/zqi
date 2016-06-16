<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$("#tradeParse").click(function(){
	var dateFrom = $("#dateFrom").val();
	$.ajax({
		url: 'hq/tradeParse',
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

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<button id="tradeParse" type="button" class="btn btn-primary" >
				明细数据分析
			</button>
		</div>
	</div>
</body>
</html>