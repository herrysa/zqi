<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	
	$("#initReportData").click(function(){
		var days = $("#reportDays").val();
		$.ajax({
            url: 'report/initData?days='+days,
            type: 'post',
            dataType: 'json',
            async:false,
            error: function(data){
           	 alert("系统错误！");
            },
            success: function(data){
                alert(data.message);
            }
        });
	});
	
	var searchFormHeight = $("#reportSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-36;
    $("#report_gridtable_div").height(gridHeight);
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
			<div id="reportSearchForm" class="pageSearch">
				<label>报表编码：</label><input id="reportCode" name="reportCode" type="text" maxlength="20" class="input-small"/>
				<label>报表名称：</label><input id="reportName" name="reportName" type="text" maxlength="20" class="input-small"/>
				<label>报表类别：</label><input id="reportType" name="gpCode" type="text"style="width:100px"/>
				<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
				</button>
				<label>初始化天数：</label><input id="reportDays" name="reportDays" type="text"style="width:100px"/>
				<button id="initReportData" type="button" class="btn btn-primary" >
					初始化
				</button>
			</div>
		<div id="report_gridtable_div" style="margin:2px">
			<table id="report_gridtable"></table>
			<div id="report_gridpager"></div>
			<script type="text/javascript"> 
			$(document).ready(function () {
				$("#report_gridtable").jqGrid({
					url: 'report/reportGridList',
					mtype: "GET",
					styleUI : 'Bootstrap',
					datatype: "json",
					colModel: [
						{ name: 'code', label: '报表编码', key: true, width: 75 },
						{ name: 'name', label: '报表名称', width: 200 },
						{ name: 'type', label: '报表类别', width: 100 },
						{ name: 'dataSource', label: '数据源SQL', width: 200 },
						{ name: 'dsDesc', label: '数据源描述', width: 150 },
						{ name: 'remark', label: '备注', width: 100 },
						{ name: 'opt', label: '操作', width: 100,formatter:optFormatter },
					],
					page: 1,
					autowidth:false,
					shrinkToFit :false,
					height: 400,
					rowNum: 20,
					pager: "#report_gridpager",
					ondblClickRow:function(rowid, iRow, iCol, e){
						$('.modal-body','#modalDialog').html("");
	                	$('#modalDialog').modal('show');
	                	var _thisGrid = $(this);
	                	setTimeout(function(){
	                		var rowData = _thisGrid.jqGrid('getRowData',rowid);
	                    	var code = rowData.code;
	                    	//var name = rowData.name;
	                    	$('.modal-body','#modalDialog').load("report/show?code="+code);
	                	},1000);
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
					cellvalue= '<a href="javaScript:changeReport(\''+code+'\')">修改</a> <a href="javaScript:deleteReport(\''+code+'\')">删除</a>';
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