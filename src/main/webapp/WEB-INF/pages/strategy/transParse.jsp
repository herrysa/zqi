<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java"   pageEncoding="UTF-8"%>
<script type="text/javascript">
var reportDefine = {
		key:"${random}_report_gridtable",
		main:{
			Build : '${ctx}/report/getReportXml?code=transReport',
			Load :''
		},
		event:{
			"Load":function( id,p1, p2, p3, p4){
			},
			"Opened":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				grid.func("AddUserFunctions", "${ctx}/report/getReportFunctionXml");
				grid.func("SetBatchFunctionURL","report/batchFunc \r\n functions=10000;timeout=9999 \r\n user=normal");
				grid.func("SetAutoCalc","0");
				grid.func("SubscribeEvent", "Clicked");
			},
			"Toolbar":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				if(p1=="104"){
					var reportXml = grid.func("GetFileXML", "");
					$.ajax({
			            url: 'report/saveReportXml',
			            type: 'post',
			            dataType: 'json',
			            data :{code:'transReport',reportXml:reportXml},
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
			},
			"Clicked":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				console.log(p1+":"+p2+":"+p3+":"+ p4);
				if(p3&&p3.indexOf("report:")!=-1){
					var reportCode = p3.replace("report:","");
					grid.func("appendWorksheet", "${ctx}/report/getReportXml?code="+reportCode+" \r\n sheetnum=0; isWithFormula=false"); 
					//grid.func("appendWorksheet", "${ctx}/report/getReportXml?code=0000001statistic \r\n sheetnum=0; isWithFormula=false"); 
				}
			} 
		},
		callback:{
			onComplete : function(id){
				var grid = eval("("+id+")");
				
			}
		}
	}; 
	
    supcanGridMap['report_gridtable_${random}']=reportDefine; 
    
    var periodList ;
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
 		/* $.ajax({
            url: 'report/findPeriodList',
            type: 'post',
            dataType: 'json',
            async:false,
            error: function(data){
            alertMsg.error("系统错误！");
            },
            success: function(data){
            	if(data.periodList){
            		periodList = data.periodList;
            	}
            }
        }); */
 	});
 	
 	function BatchControll(){ 
 		this.gridId = null;
 		this.time = 0;
 		this.pretreatment=0;
 		this.cellLength=0;
 		this.cellNum=0;
 		this.over=0;
 		this.rs = {};  
 	};
 	BatchControll.prototype.start=function(){
 		this.doAjax();
 	};
 	BatchControll.prototype.doAjax=function(){
		$.ajax({
            url: 'report/getListDataBySql',
            type: 'post',
            dataType: 'json',
            data :{sql:this.sql},
            async:false,
            error: function(data){
            alertMsg.error("系统错误！");
            },
            success: function(data){
            	console.log(this.pretreatment);
            	var grid = eval("("+gridId+")");
            	var rsList = data.rs;
            	if(rsList){
            		for(var i in rsList){
            			var rs = rsList[i];
            			this.rs[rs.k] = rs.v;
            		}
            		this.over = 1;
            	}
            }
        });
 		}
 	var batchFunction = new BatchControll();
 	batchFunction.pretreatment = 1;	
 	function batchFindRData(key,period,col,where){
 		//return checkperiod1;
 		var sum;
 		var sql = "select code k,"+col+" v from daytable_all where period='"+period+"'";
 		if(where){
 			sql += " "+where;
 		}
 		batchFunction.sql = sql;
 		batchFunction.start();
 		while(true){
 			if(batchFunction.over==1){
 				var value = batchFunction.rs[key];
 				if(value){
 					return value;
 				}else{
 					return "";
 				}
 			}
 		} 
 	}
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
 	
 	function findPeriod(day){
 		var period;
 		period = periodList[day].period;
 		return period;
 	}
 	
 	function getDesXml(xml){
 		var desXml = "";
 		if(xml){
	 		var colArr = xml.split(",");
 			for(var colIndex in colArr){
 				var col = colIndex.split(":");
 				var colXml = "";
 				var colCode = col[0];
 				var colName = col[1];
 				var coltype = "";
 				if(col.length>2){
 					coltype = col[2];
 				}else{
 					coltype = "string";
 				}
 				colXml += '<col name="'+col+'" datatype="'+coltype+'">'+colName+'</col>';
 				desXml += colXml;
 			}
 			desXml = '<?xml version="1.0" encoding="UTF-8"?"><cols>'+desXml+'</cols>';
 		}
 		return desXml;
 	}
 	
 	/* function zfValue(v1,v2,v3){
 		var period;
 		period = periodList[day].period;
 		return period;
 	} */
 </script>
 <div class="page">
	<div class="pageContent">
		<div id="${random}_report_gridtable_div"  layoutH=50 style="height:100%;margin:0px;background-color: #DFF1FE;">
			<div id="${random}_report_gridtable_container" layoutH=50 style="height:100%;margin-left:2px;margin-right:2px;margin-buttom:2px"></div>
		</div>
	</div> 
 </div>