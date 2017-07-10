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
    	var accountCode = $("#accountCode").val();
    	$.ajax({
			url: 'strategy/delAccountData?accountCode='+accountCode,
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
    	var accountCode = $("#accountCode").val();
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
    $("#runStrategy").click(function(){
    	$('.modal-body','#modalDialog').html("");
    	$('#modalDialog').modal('show');
    	var _thisGrid = $("#strategy_gtidtable");
    	var sid = _thisGrid.jqGrid('getGridParam','selrow');
    	if(!sid){
    		alert("请选择一个策略。");
    	}else{
    		setTimeout(function(){
        		var rowData = _thisGrid.jqGrid('getRowData',sid);
            	//var code = rowData.code;
            	var type = rowData.type;
            	/* var param = rowData.param; */
            	//$('.modal-body','#modalDialog').load("strategy/strategyResult?code="+code+"&type="+type+"&param="+param);
            	$('.modal-body','#modalDialog').load("strategy/strategyForm?code="+sid+"&type="+type);
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
			<label>策略代码：</label>
			<input id="gpName" name="gpName" type="text"style="width:100px"/>
			<button id="reloadStrategyGrid" type="button" class="btn btn-primary" >
				查询
			</button>
			<button id="runStrategy" type="button" class="btn btn-primary" >
				运行策略
			</button>
			<select id="accountCode" name="accountCode">
				<c:forEach items="${accountCodes}" var="ac">
					<option value="${ac.accountCode }">${ac.accountCode }</option>
				</c:forEach>
			</select>
			<button id="hisStrategy" type="button" class="btn btn-primary" >
				历史策略
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
					<li><a id="delAccountData">删除账户数据</a></li>
					<li><a id="delTransData">删除调仓数据</a></li>
					<li><a id="delJgdData">删除交易数据</a></li>
				</ul>
			</div>
			<button id="transReport" type="button" class="btn btn-primary" >
				交易报表
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
                    { name: 'code', label: '编码', width: 80 ,key:true},
                    { name: 'name', label: '名称', width: 80 },
                    { name: 'type', label: '类型', width: 40 },
                    { name: 'param', label: '参数', width: 250,editable:true },
                    { name: 'group', label: '分组', width: 80 },
                    { name: 'desc', label: '描述', width: 250 }
                ],
				page: 1,
				rownumbers :true,
                autowidth:false,
                height: '100%',
                cellEdit : false,
    			cellsubmit : 'clientArray',
                rowNum: 20,
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