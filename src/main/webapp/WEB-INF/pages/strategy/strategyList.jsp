<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	var searchFormHeight = $("#strategySearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-10;
    $("#strategy_gtidtable_div").height(gridHeight);
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form id="strategySearchForm" action="strategy/strategy" method="post" class="breadcrumb form-search">
			<label>策略代码：</label>
			<input id="gpName" name="gpName" type="text"style="width:100px"/>
			<button id="reloadStrategyGrid" type="button" class="btn btn-primary" >
				查询
			</button>
			</form>
			<div id="strategy_gtidtable_div" style="margin:2px;">
			<table id="strategy_gtidtable"></table>
			</div>
			<script type="text/javascript">
			$("#strategy_gtidtable").jqGrid({
                url: 'strategy/strategyGridList',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'code', label: '编码', width: 100 },
                    { name: 'name', label: '名称', width: 100 },
                    { name: 'group', label: '分组', width: 100 },
                    { name: 'desc', label: '描述', width: 200 }
                ],
				page: 1,
				rownumbers :true,
                autowidth:false,
                height: '100%',
                rowNum: 20,
                ondblClickRow:function(rowid,iRow,iCol, e){
                   	$('.modal-body','#modalDialog').html("");
                	$('#modalDialog').modal('show');
                	var _thisGrid = $(this);
                	setTimeout(function(){
                		var rowData = _thisGrid.jqGrid('getRowData',rowid);
                    	var code = rowData.code;
                    	$('.modal-body','#modalDialog').load("strategy/strategyResult?code="+code);
                	},500);
                },
                gridComplete:function(){
                	//alert(gridHeight);
                	$(this).setGridWidth(gridWidth);
                	$(this).setGridHeight(gridHeight-85);
                }
            });
			</script>
	</div>
	</div>
</body>
</html>