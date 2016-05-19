<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	var trans = "${strategyOption}";

});

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
		<div id="strategyRsLeft" style="float:left;width:150px">
			<ul class="nav nav-pills nav-stacked">
				<li class="active">
				<a href="javaScript:">策略概况</a></li>   
				<li><a href="#">Tutorials</a></li>
				<li><a href="#">Practice Editor </a></li> 
				<li><a href="#">Gallery</a></li> 
				<li><a href="#">Contact</a></li> 
			</ul>
		</div>
		<div id="strategyRsContent" style="float:left;width:1000px">
			<div id="strategy_chart" style="height:400px"></div>
			<script>
			var strategyChart = echarts.init(document.getElementById('strategy_chart')); 
			var strategyOption = eval('(${strategyOption})');
			strategyChart.setOption(strategyOption);
			</script>
		</div>
		</div>
	</div>
</body>
</html>