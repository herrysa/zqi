<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$( "#fhGpName" ).autocomplete({
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
	                value: item.id,
	                name: item.name
	              }
	            }));
	          }
	        });
	      },
	      minLength: 1,
	      select: function( event, ui ) {
	        $("#fhGpCode").val(ui.item.value);
	        //var gpName = ui.item.label.replace(ui.item.value+",","");
	        setTimeout(function(){
		    	$("#fhGpName").val(ui.item.name);
	        },200);
	      },
	      open: function() {
	        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	      },
	      close: function() {
	        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	      }
	    });
    $("#reloadFhDataGrid").click(function(){
    	var gpCode = $("#fhGpCode").val();
    	var period = $("#fhDateFrom").val();
    	var url = "primaryData/primaryDataGridList?gpCode="+gpCode+"&period="+period;
    	jQuery('#jqGrid').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
    
    $("#importFhData").click(function(){
    	var fhYear = $("#fhYear").val();
		$.ajax({
			url: 'primaryData/importFhData?year='+fhYear,
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
    $("#markFhToRHQ").click(function(){
    	var fhYear = $("#fhYear").val();
		$.ajax({
			url: 'primaryData/markFhToRHQ?year='+fhYear,
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
    $("#perRHQ").click(function(){
    	var rightCol = $("#rightCol").val();
		$.ajax({
			url: 'primaryData/perRHQ?rightCol='+rightCol,
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
    $("#afterRHQ").click(function(){
		$.ajax({
			url: 'primaryData/afterRHQ',
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
    $("#cacheFhData").click(function(){
    	var fhYear = $("#fhYear").val();
		$.ajax({
			url: 'primaryData/cacheFhData?year='+fhYear,
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
				//alertMsg.error("系统错误！");s
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
    gridHeight = contentHeight-searchFormHeight-52;
    $("#jqGrid_div").height(gridHeight);
}); 

</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
	<form id="primaryDataSearchForm" action="primaryData/fillPrimaryData" method="post" class="breadcrumb form-search">
		<div>
			<label>股票：</label><input id="fhGpCode" name="fhGpCode" type="hidden"style="width:100px"/><input id="fhGpName" name="fhGpName" type="text"style="width:100px"/>
			<label>年度：</label><input id="fhYear" name="fhYear" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy',isShowClear:false});"/>
			<button id="reloadFhDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<button id="importFhData" type="button" class="btn btn-primary" >
				更新分红数据
			</button>
			<select id="rightCol">
				<option value="close">close</option>
				<option value="settlement">settlement</option>
				<option value="open">open</option>
				<option value="high">high</option>
				<option value="low">low</option>
			</select>
			<div id="hisDayDataBtn" class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle" 
					data-toggle="dropdown">
					分红处理
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="markFhToRHQ">更新分红状态</a></li>
					<li><a id="perRHQ">前复权数据</a></li>
					<li><a id="afterRHQ">后复权数据</a></li>
				</ul>
			</div>
			<button id="cacheFhData" type="button" class="btn btn-primary" >
				缓存分红数据
			</button>
		</div>
	</form>
	<div id="fh_grid_div" style="margin:2px;">
    <table id="fh_grid"></table>
    <div id="fh_grid_pager"></div>
	</div>
	<script type="text/javascript"> 
    
        $(document).ready(function () {
            $("#fh_grid").jqGrid({
                url: 'primaryData/fhDataGridList?gpCode=${gpCode}&period=${period}',
                mtype: "GET",
                styleUI : 'Bootstrap',
                datatype: "json",
                colModel: [
                    { name: 'ggDate', label: '公告日期', width: 100 },
                    { name: 'code', label: '编码', width: 75 },
                    { name: 'name', label: '名称', width: 100 },
                    { name: 'fhYear', label: '年度', width: 100 },
                    { name: 'sg', label: '送股比例(10送X)',align:'right', formatter:'number', width: 110 },
                    { name: 'zz', label: '转增股比例(10转增X)',align:'right', formatter:'number',width: 110 },
                    { name: 'fh', label: '税前红利(元)',align:'right', formatter:'number', width: 100 },
                    { name: 'djDate', label: '股权登记日',align:'left', width: 100 },
                    { name: 'cqDate', label: '除权除息日',align:'left', width: 100 },
                    { name: 'txt', label: '分红送转',align:'left', width: 100 }
                ],
                sortname: 'ggDate',
            	sortorder: 'desc',
				page: 1,
				rownumbers :true,
                autowidth:false,
                shrinkToFit:false,
                height: '100%',
                rowNum: 20,
                pager: "#fh_grid_pager",
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