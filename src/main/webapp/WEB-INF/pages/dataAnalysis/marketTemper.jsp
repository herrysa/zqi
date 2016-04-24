<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#recentlyLimitTab").click(function(){
		$("#recentlyLimit").load("marketTemper/recentlyLimit");
	});
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
		<ul id="myTab" class="nav nav-tabs">
			<li class="active">
				<a id="" href="#market" data-toggle="tab">
					市场概况
				</a>
			</li>
			<li>
				<a id="recentlyLimitTab" href="#recentlyLimit" data-toggle="tab">
					近日涨/跌停
				</a>
			</li>
			<li>
				<a href="#continueLimit" data-toggle="tab">
					连续涨/跌停
				</a>
			</li>
		  <!-- <li class="dropdown">
		     <a href="#" id="myTabDrop1" class="dropdown-toggle" 
		        data-toggle="dropdown">Java 
		        <b class="caret"></b>
		     </a>
		     <ul class="dropdown-menu" role="menu" aria-labelledby="myTabDrop1">
		        <li><a href="#jmeter" tabindex="-1" data-toggle="tab">jmeter</a></li>
		        <li><a href="#ejb" tabindex="-1" data-toggle="tab">ejb</a></li>
		     </ul>
		  </li> -->
		</ul>
		<div id="myTabContent" class="tab-content">
			<div class="tab-pane fade in active" id="market">
			</div>
			<div class="tab-pane fade" id="recentlyLimit">
			</div>
			<div class="tab-pane fade" id="continueLimit">
			</div>
		</div>
	</div>
</div>
</body>
</html>