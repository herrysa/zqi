<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>

var tableStr = '${table}';
var tableJson = '';
if(tableStr.indexOf("{")!=-1){
	tableJson = eval("("+tableStr+")");
}
$(function() {
	//var trans = "${strategyOption}";
	if(tableJson){
		//jQuery("#strategy_table").empty();
		try{
			var gridDefine = supcanGridMap['strategyTable'];
			if(!gridDefine){
				var strategyTableDefine = {
						key:"strategyTable",
						main:{
							Build : '',
							Load :''
						},
						event:{
						},
						callback:{
							onComplete:function(id){
								var grid = eval("("+id+")");
								//grid.func("callfunc", "102 \r\n id=Table1;asForm=true");
							}
						}
				}
				supcanGridMap['strategyTable'] = strategyTableDefine; 
				insertTreeListToDiv("strategy_table","strategyTable");
			}else{
				//strategyTable = null;
				insertTreeListToDiv("strategy_table","strategyTable");
			}
		}catch(e){
			alert(e);
		}
		if("${account}"!="true"){
			$.each(tableJson,function(key,value){
				$("#strategyRsLeftUL").append('<li><a strategy="'+key+'" href="javaScript:">'+key+'</a></li> ');
			});
		}
		
	}
	
	jQuery("#strategyRsLeftUL a").click(function(){
		var $this = jQuery(this);
		var strategy = $this.attr("strategy");
		if(strategy=='chart'){
			jQuery("#strategy_chart_div").show();
			jQuery("#strategy_table").height(1);
		}else{
			jQuery("#strategy_chart_div").hide();
			showStrategyTable(strategy);
			var tableHeight = jQuery("#strategyRsContent").height();
			jQuery("#strategy_table").height(tableHeight);
		}
		jQuery("#strategyRsLeftUL li").removeClass("active");
		$this.parent().addClass('active');
	});
	setTimeout(function(){
		jQuery("#strategyRsLeftUL a").eq(0).trigger("click");
	},300);
});
function showStrategyTable(tableName){
	var tableObj = tableJson[tableName];
	if(tableObj){
		var tableCol = tableObj.tableCol;
		var colArr = null;
		if(tableCol instanceof Array){
			colArr = tableCol;
		}else{
			colArr = tableCol.split(",");
		}
		var tableData = tableObj.tableData;
		var colModel = new Array();
		for(var c in colArr){
			var col = colArr[c];
			colModel.push({
				name : col,
				text : col,
				width : 100,
				align:'left'
			});
		}
		var grid = eval("(strategyTable)");
		var gzContentGrid = jQuery.extend(true, {}, supCanTreeListGrid);
		gzContentGrid.Cols = colModel;
		//gzContentGridDefine.main.Build = JSON.stringify(gzContentGrid);
		grid.func("Build",JSON.stringify(gzContentGrid));
		grid.func("Load",JSON.stringify(tableData));
	}
}
function showStrategyTable1(tableName,o){
		var tableObj = tableJson[tableName];
		if(tableObj){
			var tableCol = tableObj.tableCol;
			var colArr = null;
			if(tableCol instanceof Array){
				colArr = tableCol;
			}else{
				colArr = tableCol.split(",");
			}
			var tableData = tableObj.tableData;
			var colModel = new Array();
			for(var c in colArr){
				var col = colArr[c];
				colModel.push({
					name : col,
					index : col,
					label : col,
					width : 100,
					align:'left'
				});
			}
			if($("#strategy_gridtable")[0].grid){
				$.jgrid.gridUnload("strategy_gridtable");
			}
			$("#strategy_gridtable").jqGrid({
				url: '',
				mtype: "GET",
				styleUI : 'Bootstrap',
				datatype : "local",
				colModel: colModel,
				page: 1,
				autowidth:false,
				shrinkToFit :false,
				height: 400,
				rowNum: 20,
				rownumbers : true,
				gridComplete:function(){
	            	//alert(gridHeight);
	            	//$(this).setGridWidth(gridWidth);
	            	//$(this).setGridHeight(gridHeight-85);
	            	
	            }
			});
			for(var i=0; i<tableData.length; i++)  //循环给每行添加数据
			{
				$("#strategy_gridtable").jqGrid('addRowData',i+1,tableData[i]);
			}
		}
		jQuery("#strategy_chart_div").hide();
		jQuery("#strategy_table").show();
		jQuery("li","#strategyRsLeft").removeClass("active");
		jQuery(o).parent().addClass('active');
}
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
		<div id="strategyRsLeft" style="float:left;width:150px">
			<ul id="strategyRsLeftUL" class="nav nav-pills nav-stacked">
				<c:choose>
					<c:when test="${account==true}">
						<li class="active"><a strategy="chart" href="javaScript:" >策略概况</a></li>
						<li><a strategy="trans" href="javaScript:">调仓记录</a></li>
						<li><a strategy="posi" href="javaScript:">持仓纪录</a></li> 
						<li><a strategy="trans" href="javaScript:">收益率</a></li> 
						<li><a strategy="trans" href="javaScript:">阿尔法</a></li> 
						<li><a strategy="trans" href="javaScript:">贝塔</a></li> 
						<li><a strategy="trans" href="javaScript:">夏普比率</a></li> 
						<li><a strategy="trans" href="javaScript:">收益波动率</a></li> 
						<li><a strategy="trans" href="javaScript:">信息比率</a></li> 
						<li><a strategy="trans" href="javaScript:">最大回撤</a></li> 
					</c:when>
					<c:otherwise>
						<c:if test="${chartName!=null}">
							<li><a strategy="chart" href="javaScript:">${chartName}</a></li>
						</c:if>
						<%-- <c:if test="${table!=null}">
							<li><a href="javaScript:">${chartName}</a></li>
						</c:if> --%>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
		<div id="strategyRsContent" style="float:left;width:1000px;height:100%">
			<c:if test="${chartName!=null}">
				<div id="strategy_chart_div">
					<div id="strategy_chart" style="height:400px"></div>
					<script>
						var strategyChart = echarts.init(document.getElementById('strategy_chart')); 
						var strategyOption = eval('(${strategyOption})');
						strategyChart.setOption(strategyOption);
					</script>
				${wholeIndex}
				</div>
			</c:if>
			<c:if test="${table!=null}">
				<div id="strategy_table" style="margin:2px;height:400px">
				<!-- <div id="strategy_gridtable_div" style="margin:2px">
					<table id="strategy_gridtable"></table>
				</div> -->
			</div>
			</c:if>
		</div>
		</div>
	</div>
</body>
</html>