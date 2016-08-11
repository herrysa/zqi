<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#positionListContent").load("trade/positionList");
	$("#positionList").click(function(){
		$("#positionListContent").load("trade/positionList");
	});
	
	$("#tradeForm").click(function(){
		$("#tradeFormContent").load("trade/tradeForm");
	});
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
		<ul id="reportTab" class="nav nav-tabs">
			<li class="active">
				<a id="positionList" href="#positionList" data-toggle="tab">
					列表
				</a>
			</li>
			<li>
				<a id="tradeForm" href="#tradeForm" data-toggle="tab">
					调仓
				</a>
			</li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane fade in active" id="positionListContent">
			</div>
			<div class="tab-pane fade" id="tradeFormContent">
			</div>
		</div>
	</div>
</div>
</body>
</html>