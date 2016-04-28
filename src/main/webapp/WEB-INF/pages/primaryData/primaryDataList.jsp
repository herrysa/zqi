<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$('#table').bootstrapTable({
    });
	function queryParams() {
	    return {
	        type: 'owner',
	        sort: 'updated',
	        direction: 'desc',
	        per_page: 100,
	        page: 1
	    };
	}
    /* $("#demo_grid1").bs_grid({
 
        ajaxFetchDataURL: "${ctx}/primaryData/primaryDataGridList?gpCode=${gpCode}&period=${period}",
        row_primary_key: "period",
 
        columns: [
            {field: "period", header: "日期"},
            {field: "code", header: "编码"},
            {field: "name", header: "名称"},
            {field: "open", header: "开盘价"},
            {field: "close", header: "收盘价"},
            {field: "high", header: "最高价"}
        ],
 
        sorting: [
            {sortingName: "period", field: "period", order: "descending"},
            {sortingName: "code", field: "code", order: "ascending"},
            {sortingName: "open", field: "open", order: "none"},
            {sortingName: "close", field: "close", order: "none"}
        ],
 
        filterOptions: {
            filters: [
                {
                    filterName: "period", "filterType": "text", field: "period", filterLabel: "日期",
                    excluded_operators: [],
                    filter_interface: [
                        {
                            filter_element: "input",
                            filter_element_attributes: {"type": "text"}
                        }
                    ]
                },
                {
                    filterName: "code", "filterType": "text", field: "code", filterLabel: "编码",
                    excluded_operators: ["equal", "less_or_equal"],
                    filter_interface: [
                        {
                            filter_element: "input",
                            filter_element_attributes: {"type": "text"}
                        }
                    ]
                }
            ]
        }
    });*/
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
	
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
	<form:form id="searchForm" modelAttribute="article" action="primaryData/fillPrimaryData" method="post" class="breadcrumb form-search">
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
	</form:form>
	<div style="margin-left:20px">
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
                    { name: 'period', label: '日期', key: true, width: 75 },
                    { name: 'code', label: '编码', width: 100 },
                    { name: 'name', label: '名称', width: 100 },
                    { name: 'changepercent', label: '涨幅',align:'right', formatter:'number',width: 100 },
                    { name: 'settlement', label: '昨收价',align:'right', formatter:'number', width: 100 },
                    { name: 'open', label: '开盘价',align:'right', formatter:'number', width: 80 },
                    { name: 'close', label: '收盘价',align:'right', formatter:'number', width: 80 },
                    { name: 'high', label: '最高价',align:'right', formatter:'number', width: 80 },
                    { name: 'low', label: '最低价',align:'right', formatter:'number', width: 80 },
                    { name: 'volume', label: '成交量',align:'right', formatter:'number', width: 130 },
                    { name: 'amount', label: '成交额',align:'right', formatter:'number', width: 130 }
                ],
				page: 1,
                autowidth:false,
                height: '100%',
                rowNum: 20,
                pager: "#jqGridPager"
            });
        });

    </script>
	</div>
	</div>
</body>
</html>