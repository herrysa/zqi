<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$( "#gpName" ).autocomplete({
	      source: function( request, response ) {
	        $.ajax({
	          url: "util/autocomplete",
	          dataType: "json",
	          data: {
	            featureClass: "P",
	            style: "full",
	            maxRows: 12,
	            name_startsWith: request.term,
	            sql:"SELECT symbol id,name,pinyinCode from d_gpdic where symbol like '%q%' or name like '%q%' or pinyinCode like '%q%'"
	          },
	          success: function( data ) {
	            response( $.map( data.result, function( item ) {
	              return {
	                label: item.showValue,
	                value: item.id
	              }
	            }));
	          }
	        });
	      },
	      minLength: 1,
	      select: function( event, ui ) {
	        $("#gpCode").val(ui.item.value);
	        var gpName = ui.item.label.replace(ui.item.value+",","");
	        setTimeout(function(){
		    	$("#gpName").val(gpName);
	        },200);
	      },
	      open: function() {
	        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	      },
	      close: function() {
	        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	      }
	    });
    $("#reloadDataGrid").click(function(){
    	var gpCode = $("#gpCode").val();
    	var period = $("#dateFrom").val();
    	var url = "primaryData/primaryDataGridList?gpCode="+gpCode+"&period="+period;
    	jQuery('#jqGrid').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
    
    $("#importTodayData").click(function(){
		var dateFrom = $("#dateFrom").val();
		$.ajax({
			url: 'primaryData/fillPrimaryData?fillType=today&dateFrom='+dateFrom,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
    $("#importTenDayData").click(function(){
		var dateFrom = $("#dateFrom").val();
		$.ajax({
			url: 'primaryData/fillPrimaryData?fillType=10&dateFrom='+dateFrom,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
    $("#importHisDayData").click(function(){
		var dateFrom = $("#dateFrom").val();
		var dateTo = $("#dateTo").val();
		$.ajax({
			url: 'primaryData/fillPrimaryData?fillType=date&dateFrom='+dateFrom+'&dateTo='+dateTo,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
	$("#importSeasonData").click(function(){
		var dateFrom = $("#dateFrom").val();
		$.ajax({
			url: 'primaryData/fillPrimaryData?fillType=jidu&dateFrom='+dateFrom,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
	$("#createTable").click(function(){
		$.ajax({
			url: 'init/createTable',
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				//alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
	});
    $("#hisDayDataBtn").show();
	$("#hisCwDataBtn").hide();
	$("#hisMxDataBtn").hide();
    $("#showDataType").change(function(){
    	var dataType = $(this).val();
    	if(dataType=='day'){
    		$("#hisDayDataBtn").show();
    		$("#hisCwDataBtn").hide();
    		$("#hisMxDataBtn").hide();
    	}else if(dataType=='cw'){
    		$("#hisDayDataBtn").hide();
    		$("#hisCwDataBtn").show();
    		$("#hisMxDataBtn").hide();
    	}else if(dataType=='mx'){
    		$("#hisDayDataBtn").hide();
    		$("#hisCwDataBtn").hide();
    		$("#hisMxDataBtn").show();
    	}
    });
	
    var searchFormHeight = $("#primaryDataSearchForm").height();
    var contentHeight = $("#mainContent").height();
    var contentWidth = $("#mainContent").width();
    gridWidth = contentWidth-5;
    gridHeight = contentHeight-searchFormHeight-10;
    $("#jqGrid_div").height(gridHeight);
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
	<form id="primaryDataSearchForm" action="primaryData/fillPrimaryData" method="post" class="breadcrumb form-search">
		<div>
			<label>数据类型：</label>
			<select id="showDataType" style="width:100px">
			<option value="day">日数据</option>
			<option value="cw">财务数据</option>
			<option value="mx">成交明细数据</option>
			</select>
			<label>股票：</label><input id="gpCode" name="gpCode" type="hidden"style="width:100px"/><input id="gpName" name="gpName" type="text"style="width:100px"/>
			<label>日期：</label><input id="dateFrom" name="dateFrom" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>结束日期：</label><input id="dateTo" name="dateTo" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<button id="reloadDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<div id="hisDayDataBtn" class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					导入数据
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="importTodayData">导入当日数据</a></li>
					<li><a id="importHisDayData">导入日期数据</a></li>
					<!-- <li><a id="importTenDayData">导入日期10天前数据</a></li>
					<li><a id="importSeasonData" href="javaScript:">导入日期季度数据</a></li>
					<li><a id="createTable" href="javaScript:">建表</a></li> -->
				</ul>
			</div>
			<div id="hisCwDataBtn" class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					导入财务数据
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="importCwData" href="javaScript:">导入财务数据</a></li>
					<li><a id="importHisFhData" href="javaScript:">导入历史分红数据</a></li>
				</ul>
			</div>
			<button id="hisMxDataBtn" type="button" class="btn btn-primary" >
					导入日期明细数据
			</button>
		</div>
	</form>
	<div id="jqGrid_div" style="margin:2px;">
    <table id="jqGrid"></table>
    <div id="jqGridPager"></div>
	</div>
	<script type="text/javascript"> 
    
        $(document).ready(function () {
            $("#jqGrid").jqGrid({
                url: 'primaryData/primaryDataGridList?gpCode=${gpCode}&period=${period}',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'period', label: '日期', width: 75 },
                    { name: 'code', label: '编码', width: 100 },
                    { name: 'name', label: '名称', width: 100 },
                    { name: 'changepercent', label: '涨幅',align:'right', formatter:'number',width: 100 },
                    { name: 'settlement', label: '昨收价',align:'right', formatter:'number', width: 100 },
                    { name: 'open', label: '开盘价',align:'right', formatter:'number', width: 80 },
                    { name: 'close', label: '收盘价',align:'right', formatter:'number', width: 80 },
                    { name: 'high', label: '最高价',align:'right', formatter:'number', width: 80 },
                    { name: 'low', label: '最低价',align:'right', formatter:'number', width: 80 },
                    { name: 'volume', label: '成交量(万)',align:'right', formatter:'number', width: 130 },
                    { name: 'amount', label: '成交额(万)',align:'right', formatter:'number', width: 130 },
                    { name: 'turnoverrate', label: '换手率',align:'right', formatter:'number', width: 130 },
                    { name: 'fiveminute', label: '5分钟涨幅',align:'right', formatter:'number', width: 130 },
                    { name: 'lb', label: '量比',align:'right', formatter:'number', width: 130 },
                    { name: 'pe', label: '市盈率',align:'right', formatter:'number', width: 130 },
                    { name: 'mcap', label: '流通市值',align:'right', formatter:'number', width: 130 },
                    { name: 'mfsum', label: '每股收益',align:'right', formatter:'number', width: 130 },
                    { name: 'mfratio2', label: '净利润',align:'right', formatter:'number', width: 130 }
                ],
				page: 1,
				rownumbers :true,
                autowidth:false,
                height: '100%',
                rowNum: 20,
                pager: "#jqGridPager",
                ondblClickRow:function(rowid,iRow,iCol, e){
                	var rowData = $(this).jqGrid('getRowData',rowid);
                	var code = rowData.code;
                	var name = rowData.name;
                	$('.modal-body','#modalDialog').html("");
                	$('.modal-body','#modalDialog').load("chart/kChart?code="+code+"&name="+name);
                	$('#modalDialog').modal('show');
                },
                gridComplete:function(){
                	//alert(gridHeight);
                	$(this).setGridWidth(gridWidth);
                	$(this).setGridHeight(gridHeight-85);
                	var rowIds = $(this).getDataIDs();
               		var ret = $(this).jqGrid('getRowData');
                	var rowNum = rowIds.length;
                	for (i=0;i<rowNum;i++){
                		var id = rowIds[i];
	     		    	var data = ret[i];
	     		    	if(data.changepercent>0){
	     		    		$(this).setCell(id,'changepercent','',{color:'red','font-weight':700});
	     		    	}else if(data.changepercent<0){
	     		    		$(this).setCell(id,'changepercent','',{color:'green','font-weight':700});
	     		    	}
	     		    	$(this).setCell(id,'volume',data.volume/10000);
	     		    	$(this).setCell(id,'amount',data.volume/10000);
	     		    	//data.volume=data.volume/100/10000;
	     		    	//data.amount=data.amount/10000/10000;
                	}
                }
            });
        });

    </script>
	</div>
	</div>
</body>
</html>