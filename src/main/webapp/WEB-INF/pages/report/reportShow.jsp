<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java"   pageEncoding="UTF-8"%>
<script type="text/javascript">
var reportDefine = {
		key:"${random}_report_gridtable",
		main:{
			SetSource : '${ctx}/report/datasource.xml',
			Build : '${ctx}/report/blank.xml',
			Load :''
		},
		event:{
			"Load":function( id,p1, p2, p3, p4){
			},
			"Opened":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				grid.func("AddUserFunctions", "${ctx}/report/func.xml");
			}
		},
		callback:{
			
		}
	}; 
	
    supcanGridMap['report_gridtable_${random}']=reportDefine; 
 	jQuery(document).ready(function(){
 		//reportDefine.main.Build = initreportColModel();
 		//alert(reportDefine.main.Build);
 		insertReportToDiv("${random}_report_gridtable_container","report_gridtable_${random}","","100%");
 	});
 	function sourcepayinSum(checkperiod1,checkperiod2,deptId,chargeType){
 		//return checkperiod1;
 		var sum;
 		var sql = "select sum(amount) from v_sourcepayin where checkPeriod BETWEEN '"+checkperiod1+"' and '"+checkperiod2+"' and kdDeptId='"+deptId+"'";
 		$.ajax({
            url: 'getBySql?sql='+sql,
            type: 'post',
            dataType: 'json',
            async:false,
            error: function(data){
            alertMsg.error("系统错误！");
            },
            success: function(data){
            	console.log(data.sqlResult);
            	sum = data.sqlResult;
            }
        });
 		
 		return sum;
 	}
 </script>
 <div class="page">
	<div class="pageContent">
		<div id="${random}_report_gridtable_div"  layoutH=50 style="margin:0px;background-color: #DFF1FE;" buttonBar="width:500;height:300">
			<div id="${random}_report_gridtable_container" layoutH=50 style="margin-left:15px;margin-right:15px;margin-buttom:15px"></div>
		</div>
	</div> 
 </div>