<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#reportFuncListContent").load("reportFunc/funcList");
	$("#reportFuncList").click(function(){
		$("#reportFuncListContent").load("reportFunc/funcList");
	});
	
	$("#reportFuncForm").click(function(){
		$("#reportFuncFormContent").load("reportFunc/funcForm");
	});
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
		<ul id="reportFuncTab" class="nav nav-tabs">
			<li class="active">
				<a id="reportFuncList" href="#reportFuncListContent" data-toggle="tab">
					列表
				</a>
			</li>
			<li>
				<a id="reportFuncForm" href="#reportFuncFormContent" data-toggle="tab">
					添加
				</a>
			</li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane fade in active" id="reportFuncListContent">
			</div>
			<div class="tab-pane fade" id="reportFuncFormContent">
			</div>
		</div>
	</div>
</div>
</body>
</html>