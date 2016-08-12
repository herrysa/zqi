<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	var searchFormHeight = $("#gpPoolSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-36;
    $("#gpPool_gridtable_div").height(gridHeight);
});
function changeReport(code){
	$("#reportFormContent").load("report/reportForm?id="+code);
	$('#reportTab a[id="reportForm"]').tab('show');
}
function deleteReport(code){
	$.ajax({
        url: "report/delete",
        type: 'post',
        dataType: 'json',
        data:{code:code},
        async:false,
        error: function(data){
        	alert("系统错误！");
        },
        success: function(data){
        	alert(data.message);
        	$("#report_gridtable").jqGrid("setGridParam", {
				search : true
			}).trigger("reloadGrid", [ {
					page : 1
			}]);
        }
    });
}
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<div id="gpPoolSearchForm" class="pageSearch">
				<label>板块名称：</label><input id="gppool_gpbk" name="pool_gpbk" type="text" maxlength="20" class="input-small"/>
				<label>股票名称：</label><input id="gppool_gpCode" name="pool_gpCode" type="text"style="width:100px"/>
				<label>股票编码：</label><input id="gppool_gpName" name="pool_gpName" type="text" maxlength="20" class="input-small"/>
				<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
				</button>
			</div>
		<div id="gpPool_gridtable_div" style="margin:2px">
			<table id="gpPool_gridtable"></table>
			<script type="text/javascript"> 
			$(document).ready(function () {
				$("#gpPool_gridtable").jqGrid({
					url: 'trade/gpPoolGridList',
					mtype: "GET",
					styleUI : 'Bootstrap',
					datatype: "json",
					colModel: [
						{ name: 'code', label: '编码', key: true, width: 75 },
						{ name: 'name', label: '名称', width: 200 },
						{ name: 'amount', label: '关注时间', width: 100 },
						{ name: 'cost', label: '关注价格', width: 200 },
						{ name: 'close', label: '买入价格', width: 150 },
						{ name: 'floatpl', label: '卖出价格', width: 100 },
						{ name: 'plpercent', label: '盈亏比例', width: 100,formatter:optFormatter },
					],
					page: 1,
					autowidth:false,
					shrinkToFit :false,
					height: 400,
					rowNum: 20,
					ondblClickRow:function(rowid, iRow, iCol, e){
					},
					gridComplete:function(){
	                	//alert(gridHeight);
	                	$(this).setGridWidth(gridWidth);
	                	$(this).setGridHeight(gridHeight-85);
	                }
				});
			});
			function optFormatter (cellvalue, options, rowObject)	{
				var code= rowObject.code;
				try{
					cellvalue= '<a href="javaScript:changeGpPool(\''+code+'\')">修改</a> <a href="javaScript:deleteGpPool(\''+code+'\')">删除</a>';
				}catch(err)
				{
					return cellvalue;
				}
				return cellvalue;
			}
			</script>
		</div>
		</div>
	</div>
</body>
</html>