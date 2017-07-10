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
    $("#hisStrategy_gtidtable_div").height(gridHeight);
    
    $("#cacheHQData").click(function(){
    	$.ajax({
			url: 'strategy/cacheHQData',
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				//alert(data);
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
    
    $("#delAccountData").click(function(){
    	var _thisGrid = $("#hisStrategy_gtidtable");
    	var sid = _thisGrid.jqGrid('getGridParam','selarrrow');
    	if(!sid){
    		alert("请选择一个策略。");
    	}else{
	    	$.ajax({
				url: 'strategy/delAccountData?accountCode='+sid,
				type: 'post',
				dataType: 'json',
				async:false,
				error: function(data){
					//alertMsg.error("系统错误！");
				},
				success: function(data){
					alert(data.message);
					$("#hisStrategy_gtidtable").jqGrid("setGridParam", {
						search : true
					}).trigger("reloadGrid", [ {
							page : 1
					}]);
				}
			});
    	}
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
    $("#parseTrans").click(function(){
    	var accountCode = $("#hisStrategy_gtidtable").jqGrid('getGridParam','selrow');
    	$.ajax({
			url: 'strategy/parseTrans?accountCode='+accountCode,
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
    $("#his_runStrategy").click(function(){
    	$('.modal-body','#modalDialog').html("");
    	$('#modalDialog').modal('show');
    	var _thisGrid = $("#hisStrategy_gtidtable");
    	var sid = _thisGrid.jqGrid('getGridParam','selrow');
    	if(!sid){
    		alert("请选择一个策略。");
    	}else{
    		setTimeout(function(){
            	$('.modal-body','#modalDialog').load("strategy/hisStrategy?accountCode="+sid);
        	},200);
    	}
    });
    $("#hisStrategy").click(function(){
    	$('.modal-body','#modalDialog').html("");
    	$('#modalDialog').modal('show');
    	var accountCode = jQuery("#accountCode").val();
    	setTimeout(function(){
	    	$('.modal-body','#modalDialog').load("strategy/hisStrategy?accountCode="+accountCode);
    	},200);
    });
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form id="strategySearchForm" action="strategy/strategy" method="post" class="breadcrumb form-search">
			<label>执行代码：</label>
			<input id="gpName" name="gpName" type="text"style="width:100px"/>
			<label>策略代码：</label>
			<input id="gpName" name="gpName" type="text"style="width:100px"/>
			<label>备注:</label>
			<input id="his_remark" name="gpName" type="text"style="width:100px"/>
			<button id="reloadStrategyGrid" type="button" class="btn btn-primary" >
				查询
			</button>
			<button id="his_runStrategy" type="button" class="btn btn-primary" >
				运行策略
			</button>
			<div class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					策略分析
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="parseTrans">交易分析</a></li>
					<li><a id="parsePosition">持仓分析</a></li>
				</ul>
			</div>
			<div class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					删除数据
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="delAccountData">删除全部数据</a></li>
					<li><a id="delTransData">删除调仓数据</a></li>
					<li><a id="delJgdData">删除交易数据</a></li>
				</ul>
			</div>
			<button id="transReport" type="button" class="btn btn-primary" >
				策略报表
			</button>
			</form>
			<div id="hisStrategy_gtidtable_div" style="margin:2px;">
			<table id="hisStrategy_gtidtable"></table>
			<div id="hisStrategy_gridpager"></div>
			</div>
			<script type="text/javascript">
			$("#hisStrategy_gtidtable").jqGrid({
                url: 'strategy/hisStrategyGridList',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'accountCode', label: '执行编号', width: 150 ,key:true},
                    { name: 'quantCode', label: '策略代码', width: 100},
                    { name: 'quantName', label: '策略名称', width: 100},
                    { name: 'baseCapital', label: '起始资金', width: 80 },
                    { name: 'benchmarkName', label: '基准名称', width: 100 },
                    { name: 'markBase', label: '基准点', width: 100 },
                    { name: 'startPeriod', label: '开始日期', width: 80},
                    { name: 'endPeriod', label: '结束日期', width: 80 },
                    { name: 'remark', label: '备注', width: 200,editable:true }
                ],
				page: 1,
				multiselect:true,
				sortname:'accountCode',
				sortorder:'desc',
				rownumbers :true,
                autowidth:false,
                cellEdit:true,
                cellsubmit : 'remote',
                cellurl : 'strategy/saveRemark',
                //shrinkToFit :true,
                height: '100%',
                rowNum: 20,
                pager: "#hisStrategy_gridpager",
                ondblClickRow:function(rowid,iRow,iCol, e){
                  /*  	$('.modal-body','#modalDialog').html("");
                	$('#modalDialog').modal('show');
                	var _thisGrid = $(this);
                	setTimeout(function(){
                		var rowData = _thisGrid.jqGrid('getRowData',rowid);
                    	var code = rowData.code;
                    	var type = rowData.type;
                    	var param = rowData.param;
                    	$('.modal-body','#modalDialog').load("strategy/strategyResult?code="+code+"&type="+type+"&param="+param);
                	},500); */
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