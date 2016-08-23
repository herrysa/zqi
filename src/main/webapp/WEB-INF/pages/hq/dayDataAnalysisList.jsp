<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$("#dayWaveAnalysis").click(function(){
		var dayDAgpCode = $("#dayDAgpCode").val();
		$.ajax({
			url: 'hq/dayWaveAnalysis?code='+dayDAgpCode,
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
	$("#dayWaveClass").click(function(){
		var dayDAgpCode = $("#dayDAgpCode").val();
		$.ajax({
			url: 'hq/waveClass?code='+dayDAgpCode,
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
			<form id="primaryDataSearchForm" action="" method="post" class="breadcrumb form-search">
			<label>股票：</label>
				<input id="dayDAgpCode" name="dayDAgpCode" type="hidden"style="width:100px"/>
				<input id="dayDAgpName" name="dayDAgpName" type="text"style="width:100px"/>
			<label>日期：</label><input id="dateFrom" name="dateFrom" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<%-- <label>结束日期：</label><input id="dateTo" name="dateTo" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> --%>
			<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<div id="hisDayDataBtn" class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					数据分析
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="importTodayData">日数据分析</a></li>
					<li><a id="dayWaveAnalysis">日数据波段分析</a></li>
					<li><a id="dayWaveClass">日数据波段分类</a></li>
				</ul>
			</div>
			</form>
		</div>
	</div>
</body>
</html>