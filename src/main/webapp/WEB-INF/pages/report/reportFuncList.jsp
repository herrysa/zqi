<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	
	var searchFormHeight = $("#reportFuncSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-36;
    $("#reportFunc_gridtable_div").height(gridHeight);
});
function changeReportFunc(code){
	$("#reportFuncFormContent").load("reportFunc/funcForm?id="+code);
	$('#reportFuncTab a[id="reportFuncForm"]').tab('show');
}
function deleteReportFunc(code){
	$.ajax({
        url: "reportFunc/delete",
        type: 'post',
        dataType: 'json',
        data:{code:code},
        async:false,
        error: function(data){
        	alert("系统错误！");
        },
        success: function(data){
        	alert(data.message);
        	$("#reportFunc_gridtable").jqGrid("setGridParam", {
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
			<div id="reportFuncSearchForm" class="pageSearch">
				<label>函数编码：</label><input id="reportFuncCode" name="reportFuncCode" type="text" maxlength="20" class="input-small"/>
				<label>函数名称：</label><input id="reportFuncName" name="reportFuncName" type="text" maxlength="20" class="input-small"/>
				<label>函数类别：</label><input id="reportFuncType" name="gpCode" type="text"style="width:100px"/>
				<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
				</button>
			</div>
		<div id="reportFunc_gridtable_div" style="margin:2px">
			<table id="reportFunc_gridtable"></table>
			<div id="reportFunc_gridpager"></div>
			<script type="text/javascript"> 
			$(document).ready(function () {
				$("#reportFunc_gridtable").jqGrid({
					url: 'reportFunc/reportFuncGridList',
					mtype: "GET",
					styleUI : 'Bootstrap',
					datatype: "json",
					colModel: [
						{ name: 'code', label: '函数编码', key: true, width: 75 },
						{ name: 'name', label: '函数名称', width: 200 },
						{ name: 'category', label: '函数分类', width: 100 },
						{ name: 'type', label: '函数类别', width: 70 },
						{ name: 'rsType', label: '返回值类别', width: 70 },
						{ name: 'returntype', label: '返回值类型', width: 70 },
						{ name: 'params', label: '参数', width: 200 },
						{ name: 'funcSql', label: '函数体', width: 150 },
						{ name: 'remark', label: '备注', width: 100 },
						{ name: 'opt', label: '操作', width: 100,formatter:optFormatter }
					],
					page: 1,
					autowidth:false,
					shrinkToFit :false,
					height: 400,
					rowNum: 20,
					pager: "#reportFunc_gridpager",
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
					cellvalue= '<a href="javaScript:changeReportFunc(\''+code+'\')">修改</a> <a href="javaScript:deleteReportFunc(\''+code+'\')">删除</a>';
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