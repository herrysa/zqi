<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
	
</head>
<script>
$(function() {
	$("#dayDataContent").load("primaryData/primaryDataList");
	$("#dayData").click(function(){
		$("#dayDataContent").load("primaryData/primaryDataList");
	});
	$("#tradeData").click(function(){
		$("#tradeDataContent").load("tradeData/tradeDataList");
	});
	$("#bkData").click(function(){
		$("#bkDataContent").load("bkData/bkDataList");
	});
	$("#financeData").click(function(){
		$("#dayDataContent").load("primaryData/primaryDataList");
	});
	$("#fhData").click(function(){
		$("#fhDataContent").load("primaryData/fhDataList");
	});
}); 
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<ul id="reportTab" class="nav nav-tabs">
				<li class="active">
					<a id="dayData" href="#dayDataContent" data-toggle="tab">
						日数据
					</a>
				</li>
				<li>
					<a id="tradeData" href="#tradeDataContent" data-toggle="tab">
						明细数据
					</a>
				</li>
				<li>
					<a id="bkData" href="#bkDataContent" data-toggle="tab">
						板块数据
					</a>
				</li>
				<li>
					<a id="financeData" href="#financeDataContent" data-toggle="tab">
						财务数据
					</a>
				</li>
				<li>
					<a id="fhData" href="#fhDataContent" data-toggle="tab">
						历史分红
					</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane fade in active" id="dayDataContent">
				</div>
				<div class="tab-pane fade" id="tradeDataContent">
				</div>
				<div class="tab-pane fade" id="bkDataContent">
				</div>
				<div class="tab-pane fade" id="financeDataContent">
				</div>
				<div class="tab-pane fade" id="fhDataContent">
				</div>
			</div>
		</div>
	</div>
</body>
</html>