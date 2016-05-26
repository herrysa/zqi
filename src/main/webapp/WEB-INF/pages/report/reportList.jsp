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
			<div class="pageSearch">
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
		<div style="margin:2px">
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
						{ name: 'name', label: '报表名称', width: 100 },
						{ name: 'type', label: '报表类别', width: 100 }
					],
					page: 1,
					autowidth:false,
					height: 400,
					rowNum: 20,
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
					pager: "#report_gridpager"
				});
			});

			</script>
		</div>
		</div>
	</div>
</body>
</html>