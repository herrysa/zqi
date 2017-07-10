<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	/* $("#positionListContent").load("trade/positionList");
	$("#positionList").click(function(){
		$("#positionListContent").load("trade/positionList");
	});
	
	 */
	$("#jdgList").click(function(){
		$("#jdgListContent").load("trade/jgdList");
	});
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
		<ul id="reportTab" class="nav nav-tabs">
			<li>
				<a id="jdgList" href="#jdgListContent" data-toggle="tab">
					交割单
				</a>
			</li>
			<li class="active">
				<a id="tradeSituation" href="#tradeSituationContent" data-toggle="tab">
					分析概况
				</a>
			</li>
			<li>
				<a id="tradeOptParse" href="#tradeOptParseContent" data-toggle="tab">
					操作分析
				</a>
			</li>
			<li>
				<a id="tradeTimeParse" href="#tradeTimeParseContent" data-toggle="tab">
					周期分析
				</a>
			</li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane fade" id="jdgListContent">
			</div>
			<div class="tab-pane fade  in active" id="tradeSituationContent">
			</div>
			<div class="tab-pane fade" id="tradeOptParseContent">
			</div>
			<div class="tab-pane fade" id="tradeTimeParseContent">
			</div>
		</div>
	</div>
</div>
</body>
</html>