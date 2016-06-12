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
    $("#reloadBkDataGrid").click(function(){
    	var bkType = jQuery("#bkType").val();
    	var gpCode = jQuery("#bkGpCode").val();
    	var bkName = jQuery("#bkName").val();
    	var url = "bkData/bkDataGridList?filter_EQS_bkType="+bkType+"&filter_EQS_code="+gpCode+"&filter_EQS_bkName="+bkName;
    	jQuery('#bk_grid').jqGrid('setGridParam', {
			url : url
		}).trigger("reloadGrid");
    });
    
    $("#importBkData").click(function(){
		$.ajax({
			url: 'bkData/importBkData',
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
			<label>股票类别：</label><select id="bkType">
			<option value=""></option>
			<option value="gainianbankuai">概念板块</option>
			<option value="diyu">地域板块</option>
			<option value="bkshy">行业板块</option>
			<option value="zhishu_000001">上证</option>
			<option value="hs300">沪深300</option>
			<option value="zxqy">中小板</option>
			<option value="cyb">创业板</option>
			</select>
			<label>股票：</label><input id="bkGpCode" name="bkGpCode" type="hidden"style="width:100px"/><input id="bkGpName" name="bkGpName" type="text"style="width:100px"/>
			<label>板块：</label><input id="bkName" name="bkName" type="text"style="width:100px"/>
			<button id="reloadBkDataGrid" type="button" class="btn btn-primary" >
					查询
			</button>
			<button id="importBkData" type="button" class="btn btn-primary" >
				更新板块数据
			</button>
		</div>
	</form>
	<div id="bk_grid_div" style="margin:2px;">
    <table id="bk_grid"></table>
    <div id="bk_grid_pager"></div>
	</div>
	<script type="text/javascript"> 
    
        $(document).ready(function () {
            $("#bk_grid").jqGrid({
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
                pager: "#bk_grid_pager",
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