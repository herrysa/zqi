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
	
	var searchFormHeight = $("#jgdSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-36;
    $("#jgd_gridtable_div").height(gridHeight);
    
    $("#importJgd").click(function(){
    	$('#importDialog').modal('show');
    	var _thisGrid = $(this);
    	setTimeout(function(){
        	//$('.modal-body','#importDialog').load("report/show?code="+code);
    	},1000);
    });
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
			<div id="jgdSearchForm" class="pageSearch">
				<label>报表编码：</label><input id="reportCode" name="reportCode" type="text" maxlength="20" class="input-small"/>
				<label>报表名称：</label><input id="reportName" name="reportName" type="text" maxlength="20" class="input-small"/>
				<label>报表类别：</label><input id="reportType" name="gpCode" type="text"style="width:100px"/>
				<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
				</button>
				<button id="importJgd" type="button" class="btn btn-primary" >
					交割单导入
				</button>
			</div>
		<div id="jgd_gridtable_div" style="margin:2px">
			<table id="jgd_gridtable"></table>
			<div id="jgd_gridpager"></div>
			<script type="text/javascript"> 
			$(document).ready(function () {
				$("#jgd_gridtable").jqGrid({
					url: 'trade/jgdGridList',
					mtype: "GET",
					styleUI : 'Bootstrap',
					datatype: "json",
					colModel: [
						{ name: 'tradeCode', label: '成交编号', key: true, width: 75 },
						{ name: 'businessType', label: '业务名称', width: 100 },
						{ name: 'period', label: '成交日期', width: 100 },
						{ name: 'code', label: '证券代码', width: 100 },
						{ name: 'name', label: '证券名称', width: 100 },
						{ name: 'price', label: '成交价格', width: 100 },
						{ name: 'amount', label: '成交数量', width: 100 },
						{ name: 'remainder', label: '剩余数量', width: 100 },
						{ name: 'money', label: '成交金额', width: 100 },
						{ name: 'cmoney', label: '清算金额', width: 100 },
						{ name: 'cash', label: '剩余金额', width: 150 },
						{ name: 'stampTex', label: '印花税', width: 100 },
						{ name: 'transferFee', label: '过户费', width: 100 },
						{ name: 'commission', label: '净佣金', width: 100 },
						{ name: 'transferFee2', label: '交易规费', width: 100 }
					],
					sortname : 'period',  
					viewrecords : true,
					sortorder : 'desc',
					page: 1,
					autowidth:false,
					shrinkToFit :false,
					height: 400,
					rowNum: 20,
					pager: "#jgd_gridpager",
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
	<div class="modal fade" id="importDialog" tabindex="-1" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
				  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				  
				</div>
				<div class="modal-body">
					<input id="jgdFile" name="jgdFile" class="file" type="file" multiple data-min-file-count="1">
					<script>
						$("#jgdFile").fileinput({
							uploadUrl: 'trade/importJgd',
							language: 'zh',
					        'allowedFileExtensions' : ['xls', 'xlsx'],
					    });
					</script>
				</div>
    </div>
  </div>
</div>
</body>
</html>