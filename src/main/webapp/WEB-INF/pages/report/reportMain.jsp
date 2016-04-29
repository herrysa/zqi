<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#reportList").click(function(){
		$("#reportListContent").load("report/reportList");
	});
	
	$("#reportForm").click(function(){
		$("#reportFormContent").load("report/reportForm");
	});
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
		<ul id="reportTab" class="nav nav-tabs">
			<li class="active">
				<a id="reportList" href="#reportListContent" data-toggle="tab">
					列表
				</a>
			</li>
			<li>
				<a id="reportForm" href="#reportFormContent" data-toggle="tab">
					添加
				</a>
			</li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane fade in active" id="reportListContent">
			</div>
			<div class="tab-pane fade" id="reportFormContent">
			</div>
		</div>
	</div>
</div>
</body>
</html>