<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$( "#bkGpName" ).autocomplete({
	      source: function( request, response ) {
	        $.ajax({
	          url: "util/autocomplete",
	          dataType: "json",
	          data: {
	            featureClass: "P",
	            style: "full",
	            maxRows: 12,
	            name_startsWith: request.term,
	            sql:"SELECT code id,name,pinyinCode from d_gpdic where symbol like '%q%' or name like '%q%' or pinyinCode like '%q%'"
	          },
	          success: function( data ) {
	            response( $.map( data.result, function( item ) {
	              return {
	                label: item.showValue,
	                value: item.id,
	                name: item.name
	              }
	            }));
	          }
	        });
	      },
	      minLength: 1,
	      select: function( event, ui ) {
	        $("#bkGpCode").val(ui.item.value);
	        //var gpName = ui.item.label.replace(ui.item.value+",","");
	        setTimeout(function(){
		    	$("#bkGpName").val(ui.item.name);
	        },200);
	      },
	      open: function() {
	        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	      },
	      close: function() {
	        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	      }
	    });
	$( "#bkName" ).autocomplete({
	      source: function( request, response ) {
	        $.ajax({
	          url: "util/autocomplete",
	          dataType: "json",
	          data: {
	            featureClass: "P",
	            style: "full",
	            maxRows: 12,
	            name_startsWith: request.term,
	            sql:"SELECT bkName id,bkName name from d_gpbk where bkName like '%q%' GROUP BY bkName "
	          },
	          success: function( data ) {
	            response( $.map( data.result, function( item ) {
	              return {
	                label: item.name,
	                value: item.name,
	                name: item.name
	              }
	            }));
	          }
	        });
	      },
	      minLength: 1,
	      select: function( event, ui ) {
		    $("#bkName").val(ui.item.name);
	      },
	      open: function() {
	        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	      },
	      close: function() {
	        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	      }
	    });
    $("#reloadTradeDataGrid").click(function(){
    	var bkType = jQuery("#bkType").val();
    	var gpCode = jQuery("#bkGpCode").val();
    	var bkName = jQuery("#bkName").val();
    	var url = "bkData/bkDataGridList?filter_EQS_bkType="+bkType+"&filter_EQS_code="+gpCode+"&filter_EQS_bkName="+bkName;
    	jQuery('#bk_grid').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
    
    $("#downLoadTradeData").click(function(){
    	var period = $("#tradePeriod").val();
		$.ajax({
			url: 'tradeData/downLoadTradeData?period='+period,
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
	$("#downLoadTempTradeData").click(function(){
    	var period = $("#tradePeriod").val();
		$.ajax({
			url: 'tradeData/downLoadTradeData?temp=1&period='+period,
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

	
    var searchFormHeight = $("#bkDataSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-52;
    $("#bk_grid_div").height(gridHeight);
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
	<form id="bkDataSearchForm" action="primaryData/fillPrimaryData" method="post" class="breadcrumb form-search">
		<div>
			<label>股票：</label><input id="tradeGpCode" name="tradeGpCode" type="hidden"style="width:100px"/><input id="tradeGpName" name="tradeGpName" type="text"style="width:100px"/>
			<label>日期：</label><input id="tradePeriod" name="bkName" type="text" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
			<button id="reloadTradeDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<div class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					下载明细数据
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="downLoadTempTradeData">下载临时日期明细数据</a></li>
					<li><a id="downLoadTradeData">下载日期明细数据</a></li>
					<li><a id="downLoadYearTradeData">下载年度明细数据</a></li>
				</ul>
			</div>
		</div>
	</form>
	<div id="trade_grid_div" style="margin:2px;">
    <table id="trade_grid"></table>
    <div id="trade_grid_pager"></div>
	</div>
	<script type="text/javascript"> 
    
        $(document).ready(function () {
            $("#trade_grid").jqGrid({
                url: 'bkData/bkDataGridList',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'code', label: '编码', width: 75 },
                    { name: 'name', label: '名称', width: 100 },
                    { name: 'bkType', label: '板块类别', width: 100 },
                    { name: 'bkName', label: '板块名称',align:'left', width: 110 }
                ],
                sortname: 'code',
            	sortorder: 'asc',
				page: 1,
				rownumbers :true,
                autowidth:false,
                shrinkToFit:false,
                height: '100%',
                rowNum: 20,
                pager: "#trade_grid_pager",
                gridComplete:function(){
                	//alert(gridHeight);
                	$(this).setGridWidth(gridWidth);
                	$(this).setGridHeight(gridHeight-85);
                }
            });
        });

    </script>
	</div>
	</div>
</body>
</html>