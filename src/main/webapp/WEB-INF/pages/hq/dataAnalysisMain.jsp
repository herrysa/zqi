<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
	$("#dayDataAnalysisContent").load("hq/dayDataAnalysisList");
	$("#dayDataAnalysis").click(function(){
		$("#dayDataAnalysisContent").load("hq/dayDataAnalysisList");
	});
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<ul id="reportTab" class="nav nav-tabs">
				<li class="active">
					<a id="dayDataAnalysis" href="#dayDataAnalysisContent" data-toggle="tab">
						日数据分析
					</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane fade in active" id="dayDataAnalysisContent">
				</div>
			</div>
		</div>
	</div>
</body>
</html>