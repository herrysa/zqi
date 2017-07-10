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
    $("#trans_gtidtable_div").height(gridHeight);
    
    $("#reloadTransGrid").click(function(){
    	var transCode = $("#transCode").val();
    	var url = 'strategy/transGridList?filter_EQS_transCode='+transCode;
    	jQuery('#trans_gtidtable').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
    
    $("#parseTrans").click(function(){
    	var transCode = $("#transCode").val();
    	$.ajax({
			url: 'strategy/parseTrans?transCode='+transCode,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data.message);
			}
		});
    });
    $("#transReport").click(function(){
    	$('.modal-body','#modalDialog').html("");
    	$('#modalDialog').modal('show');
    	setTimeout(function(){
        	$('.modal-body','#modalDialog').load("strategy/transParse");
    	},500);
    });
    $("#delTransData").click(function(){
    	var transCode = $("#transCode").val();
    	$.ajax({
			url: 'strategy/delTransData?transCode='+transCode,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data.message);
			}
		});
    });
    $("#delJgdData").click(function(){
    	var transCode = $("#transCode").val();
    	$.ajax({
			url: 'strategy/delJgdData?transCode='+transCode,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data.message);
			}
		});
    });
    
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form id="strategySearchForm" action="strategy/strategy" method="post" class="breadcrumb form-search">
			<label>交易名称：</label>
			<!-- <input id="transCode" name="transCode" type="text"style="width:100px"/> -->
			<select id="transCode" name="transCode">
				<c:forEach items="${transCodes}" var="tc">
					<option value="${tc.transCode }">${tc.transCode }</option>
				</c:forEach>
			</select>
			<button id="reloadTransGrid" type="button" class="btn btn-primary" >
				查询
			</button>
			<div class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					删除数据
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="delAccountData">删除策略数据</a></li>
					<li><a id="delTransData">删除调仓数据</a></li>
					<li><a id="delJgdData">删除交易数据</a></li>
				</ul>
			</div>
			<button id="parseTrans" type="button" class="btn btn-primary" >
				交易分析
			</button>
			<button id="transReport" type="button" class="btn btn-primary" >
				交易报表
			</button>
			</form>
			<div id="trans_gtidtable_div" style="margin:2px;">
			<table id="trans_gtidtable"></table>
			<div id="trans_gridpager"></div>
			</div>
			<script type="text/javascript">
			$("#trans_gtidtable").jqGrid({
                url: '',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'tradeCode', label: '交易编号', width: 40 , key:true},
                    { name: 'transCode', label: '交易名称', width: 40 },
                    { name: 'code', label: '编码', width: 40 },
                    { name: 'name', label: '名称', width: 40 },
                    { name: 'period', label: '买入日期', width: 40 },
                    { name: 'price', label: '买入价格', width: 40 },
                    { name: 'amount', label: '买入数量', width: 40 },
                    { name: 'remainder', label: '剩余数量', width: 40 },
                    { name: 'money', label: '交易金额', width: 40 },
                    { name: 'cash', label: '剩余金额', width: 50 },
                    { name: 'cost', label: '交易费用', width: 60},
                    { name: 'speriod', label: '卖出日期', width: 40 },
                    { name: 'sprice', label: '卖出价格', width: 40 },
                    { name: 'samount', label: '卖出数量', width: 40 },
                    { name: 'sremainder', label: '剩余数量', width: 40 },
                    { name: 'smoney', label: '交易金额', width: 40 },
                    { name: 'scash', label: '剩余金额', width: 50 },
                    { name: 'scost', label: '交易费用', width: 60},
                    { name: 'profit', label: '盈亏百分比', width: 60}
                ],
				page: 1,
				rownumbers :true,
                autowidth:false,
                height: '100%',
                cellEdit : true,
    			cellsubmit : 'clientArray',
                rowNum: 20,
                pager: "#trans_gridpager",
                ondblClickRow:function(rowid,iRow,iCol, e){
                   
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