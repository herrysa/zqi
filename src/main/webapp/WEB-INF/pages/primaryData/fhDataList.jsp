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
			<label>日期：</label><input id="fhDateFrom" name="fhDateFrom" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<button id="reloadFhDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<button type="button" class="btn btn-primary" 
				data-toggle="dropdown">
				更新分红数据
				<span class="caret"></span>
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
                    { name: 'sg', label: '送股比例(10送X)',align:'right', formatter:'number', width: 60 },
                    { name: 'zz', label: '转增股比例(10转增X)',align:'right', formatter:'number',width: 60 },
                    { name: 'fh', label: '税前红利(元)',align:'right', formatter:'number', width: 60 },
                    { name: 'djDate', label: '股权登记日',align:'left', width: 60 },
                    { name: 'cqDate', label: '除权除息日',align:'left', width: 60 }
                ],
                sortname: 'period',
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