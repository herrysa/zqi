<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java"   pageEncoding="UTF-8"%>
<script type="text/javascript">
var reportDefine = {
		key:"${random}_report_gridtable",
		main:{
			Build : '${ctx}/report/blank.xml',
			SetSource : '${ctx}/report/datasource.xml',
			Load :''
		},
		event:{
			"Load":function( id,p1, p2, p3, p4){
			},
			"Opened":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				grid.func("AddUserFunctions", "${ctx}/report/func.xml");
			},
			"Toolbar":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				if(p1=="104"){
					var reportXml = grid.func("GetFileXML", "");
					$.ajax({
			            url: 'saveReportXml?code='+reportDefineCode+'&reportXml='+reportXml,
			            type: 'post',
			            dataType: 'json',
			            async:false,
			            error: function(data){
			            alertMsg.error("系统错误！");
			            },
			            success: function(data){
			            	formCallBack(data);
			            }
			        });
					grid.func("CancelEvent", "");
				}
			}
		},
		callback:{
			onComplete : function(id){
				var grid = eval("("+id+")");
				console.log(111);
				//var url = "${ctx}/report/getDataSourceBySql?sql=select * from daytable_all where";
				
				grid.func("NewDS", "rdata \r\n rdata");
				var url = "select * from daytable_all where 1=1 and period='2016-05-20' and close<>0 and ROUND(settlement*0.1,2)=changeprice";
				url += " order by period desc,code asc";
				grid.func("SetParas", "rdata \r\n sql="+url+"");
			}
		}
	}; 
	
    supcanGridMap['report_gridtable_${random}']=reportDefine; 
 	jQuery(document).ready(function(){
 		//reportDefine.main.Build = initreportColModel();
 		//alert(reportDefine.main.Build);
 		insertReportToDiv("${random}_report_gridtable_container","report_gridtable_${random}","","100%");
 		jQuery("#aaa").click(function(){
 			var grid = eval("(report_gridtable_${random})");
			//var url = "${ctx}/report/getDataSourceBySql?sql=select * from daytable_all where";
			var url = "sql=select * from daytable_all where 1=1 and settlement*0.1=changeprice";
			url += "order by period desc,code asc limit 0,100";
			alert();
			grid.func("SetParas", "RHIS \r\n sql="+url);
 		});
 	});
 	function findRData(period,code,col){
 		//return checkperiod1;
 		var sum;
 		var sql = "select "+col+" from daytable_all where period='"+period+"' and code='"+code+"'";
 		$.ajax({
            url: 'report/getDataBySql?sql='+sql,
            type: 'post',
            dataType: 'json',
            async:false,
            error: function(data){
            alertMsg.error("系统错误！");
            },
            success: function(data){
            	if(data.rs){
            		var rs = data.rs;
            		sum = data.rs[col];
            	}
            }
        });
 		
 		return sum;
 	}
 </script>
 <div class="page">
	<div class="pageContent">
		<div id="${random}_report_gridtable_div"  layoutH=50 style="height:100%;margin:0px;background-color: #DFF1FE;">
			<div id="${random}_report_gridtable_container" layoutH=50 style="height:100%;margin-left:2px;margin-right:2px;margin-buttom:2px"></div>
		</div>
	</div> 
 </div>