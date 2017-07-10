<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	
</head>
<script>
$(function() {
	$( "#redisGpName" ).autocomplete({
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
	        $("#redisGpCode").val(ui.item.value);
	        //var gpName = ui.item.label.replace(ui.item.value+",","");
	        setTimeout(function(){
		    	$("#redisGpName").val(ui.item.name);
	        },200);
	      },
	      open: function() {
	        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	      },
	      close: function() {
	        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	      }
	    });
    
    $("#showRedisData").click(function(){
    	var redisGpCode = $("#redisGpCode").val();
    	var redisBeginDate = $("#redisBeginDate").val();
    	var redisEndDate = $("#redisEndDate").val();
		$.ajax({
			url: 'primaryData/showRedisData?gpCode='+redisGpCode+'&beginDate='+redisBeginDate+'&endDate='+redisEndDate,
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				alert("系统错误！");
			},
			success: function(data){
				$("#redisContent_div").html(data.rs);
			}
		});
	});
}); 



</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
	<form id="primaryDataSearchForm" action="primaryData/fillPrimaryData" method="post" class="breadcrumb form-search">
		<div>
			<label>股票：</label><input id="redisGpCode" name="redisGpCode" type="hidden"style="width:100px"/><input id="redisGpName" name="redisGpName" type="text"style="width:100px"/>
			<label>日期：</label><input id="redisBeginDate" name="redisBeginDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>到 日期：</label><input id="redisEndDate" name="redisEndDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
				value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<button id="showRedisData" type="button" class="btn btn-primary" >
					查询
			</button>
		</div>
	</form>
	<div id="redisContent_div" style="margin:2px;">
    
	</div>
	</div>
	</div>
</body>
</html>