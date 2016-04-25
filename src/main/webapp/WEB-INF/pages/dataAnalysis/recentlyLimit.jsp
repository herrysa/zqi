<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	
}); 

</script>
</head>
<body>
<div class="page">
	<div class="pageContent">
	<div style="margin-left:20px">
    <table id="jqGrid"></table>
    <div id="jqGridPager"></div>
	</div>
	<script type="text/javascript"> 
    
        $(document).ready(function () {
            $("#jqGrid").jqGrid({
                url: 'marketTemper/recentlyLimitUpGridList',
                mtype: "GET",
                datatype: "json",
                colModel: [
                    { label: 'period', name: 'period', key: true, width: 75 },
                    { label: 'code', name: 'code', width: 150 },
                    { label: 'name', name: 'name', width: 150 },
                    { label: 'increasePercent', name: 'increasePercent', width: 150 }
                ],
				page: 1,
                width: 780,
                height: 250,
                rowNum: 20,
                regional : 'cn',
				scrollPopUp:true,
				scrollLeftOffset: "83%",
				viewrecords: true,
                scroll: 1, // set the scroll property to 1 to enable paging with scrollbar - virtual loading of records
                emptyrecords: 'Scroll to bottom to retrieve new page', // the message will be displayed at the bottom 
                pager: "#jqGridPager"
            });
        });

    </script>
	</div>
</div>
</body>
</html>