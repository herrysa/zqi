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
    $("#reloadDataGrid").click(function(){
    	var gpCode = $("#gpName").val();
    	var period = $("#fillDate").val();
    	var url = "primaryData/primaryDataGridList?gpCode="+gpCode+"&period="+period;
    	jQuery('#jqGrid').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
	$("#importSeasonData").click(function(){
		var fillDate = $("#fillDate").val();
		$.ajax({
			url: 'primaryData/fillPrimaryData?fillDate='+fillDate,
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
			<label>股票：</label><input id="gpName" name="gpName" type="text"style="width:100px"/>
			<label>日期：</label><input id="fillDate" name="fillDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<%-- <label>结束日期：</label><input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>&nbsp;&nbsp; --%>
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
					<li><a id="importTenDayData">导入日期10天前数据</a></li>
					<li><a id="importSeasonData" href="javaScript:">导入日期季度数据</a></li>
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
                    { label: 'period', name: 'period', key: true, width: 75 },
                    { label: 'code', name: 'code', width: 150 }
                ],
				page: 1,
                width: 780,
                height: 250,
                rowNum: 20,
                pager: "#jqGridPager"
            });
        });

    </script>
	</div>
	</div>
</body>
</html>