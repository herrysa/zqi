<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta charset="utf-8">
</head>
<script>
var indicatorContainer = {};
$(function() {
});
function changeIndicator(indi){
	var code = "${code}";
	$.ajax({
        url: 'chart/indicator?indi='+indi+'&code='+code,
        type: 'post',
        dataType: 'json',
        async:false,
        error: function(data){
        alertMsg.error("系统错误！");
        },
        success: function(data){
            var indiOption = data.indiOption;
            //alert(indiOption);
            if(indiOption){
            	$("#kChartContent").append('<div id="'+indi+'_chart" style="height:200px"></div>');
            	indicatorContainer[indi] = echarts.init(document.getElementById(indi+'_chart'));
            	indicatorContainer[indi].setOption(eval("("+indiOption+")")); 
            }
            
        }
    });
}
</script>
</head>
<body>
	<div class="page">
		<div id="kChartContent" class="pageContent">
			<div id="k_chart" style="height:400px"></div>
			<script>
			var kChart = echarts.init(document.getElementById('k_chart')); 
			var kOption = eval('(${kOption})');
			kChart.setOption(kOption);
			kChart.on('dataZoom', function (params) {
			    //console.log(params.start);
			    var start = params.start;
			    var end = params.end;
			    for(var indi in indicatorContainer){
			    	var indiChart = indicatorContainer[indi];
			    	indiChart.dispatchAction({
				    	type: 'dataZoom',
				    	start:start,
				    	end:end
				    });
			    }
			    
			});
			</script>
			<div style="margin-left:5px;margin-top:5px">
				<span id="ZRSI_span" style="padding:1px;font-size:14px;border: 1px solid red; "><a style="text-decoration : none;color:blue" href="javaScript:changeIndicator('ZRSI')">ZRSI</a></span>
				<span id="PSY_span" style="margin:-5px;padding:1px;font-size:14px;border: 1px solid red; "><a style="text-decoration : none;color:blue" href="javaScript:changeIndicator('PSY')">PSY</a></span>
			</div>
		</div>
	</div>
</body>
</html>