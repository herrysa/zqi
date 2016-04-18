<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
</head>
<script>
var hqGridDefine = {
		key:"hq_gridtable",
		main:{
			Build : '',
			Load :'',
		},
		event:{
			//单元格数据修改
			"EditChanged":function( id,p1, p2, p3, p4){
				var grid = eval("("+id+")");
				/* var v = grid.func("GetCellData",p1+"\r\n"+p2);
				var gzIdTemp =  grid.func("GetCellData",p1+"\r\n gzId");
				var rowIndex = jQuery.inArray(gzIdTemp, gzIds);
				if(rowIndex > -1){
					gzContents[rowIndex][p2] = v;
					gzContents[rowIndex]["isEdit"] = '1';
					var changedData = gzContentCalculate(rowIndex,p1);
					//console.log(JSON.stringify(changedData));
					var nextRow = parseInt(p1)+1;
					grid.func("SetRowCellData", p1+'\r\n '+JSON.stringify(changedData));
					grid.func("SelectCell", nextRow+'\r\n '+p2);
				} */
			},
				//单元格双击
				"DblClicked":function( id,p1, p2, p3, p4){
					var grid = eval("("+id+")");
					/* var colEditAble = grid.func("GetColEditAble",p2);
					if(colEditAble == 1){//编辑列不弹出卡片
						return;
					}
					var dialogHeight = 600;
					var cols = grid.func("GetCols","");//获取列数
					var rows = grid.func("getRows","");//获取行数
					var dialogRows = 0;
					if(cols > 0&&rows>0){
						for(var colIndex = 0;colIndex < cols;colIndex++){
							var isAbsHide = grid.func("GetColProp",colIndex+" \r\n isAbsHide");//绝对隐藏
							var isHide = grid.func("GetColProp",colIndex+" \r\n isHide");//隐藏
							if(isAbsHide == 1||isHide == 1){
								continue;
							}
							dialogRows ++;
						}
					}
					if(dialogRows > 0){
						if(dialogRows/2 == 0){
							dialogHeight = 70 + dialogRows/2*30;
						}else{
							dialogRows++;
							dialogHeight = 70 + dialogRows/2*30;
						}
					}
					if(dialogHeight > 600){
						dialogHeight = 600;
					}
					var winTitle='<s:text name="gzContentEdit.title"/>';
				 	var url = "editGzContent?navTabId=gzContent_gridtable&id="+p1;
				 	url = encodeURI(url);
				 	$.pdialog.open(url,'inheritGzContent',winTitle, {ifr:true,hasSupcan:"gzContent_gridtable",mask:true,resizable:true,maxable:true,width : 700,height : dialogHeight}); */
				},
				"MenuBeforePopup":function( id,p1, p2, p3, p4){//鼠标右键菜单即将弹出
					var grid = eval("("+id+")");
					/* var menuTemp = "id=gridRefresh;text=刷新;";
					grid.func("AddMenu", menuTemp); */
				},
				"MenuClicked":function( id,p1, p2, p3, p4){//鼠标右键菜单自定义功能被选中
					var grid = eval("("+id+")");
					/* if("gridRefresh" == p1){
						grid.func("setProp", "sort \r\n orgCode,deptCode,personCode");
					} */
				}
		},callback:{
			onComplete:function(id){
				var grid = eval("("+id+")");
//					grid.func("InsertCol", "0\r\nname=checked;isCheckboxOnly=true");
				/* grid.func("EnableMenu","print,copy,addSort,selectCol,export,separate,showRuler,enter \r\n false");//打印预览,复制,加入多重排序,自定义显示隐藏列,转换输出,分屏冻结,显示/隐藏左标尺,回车键行为	
				var gzCustomLayout = jQuery("#gzContent_gzCustomLayout").val();
				if(gzCustomLayout){
					grid.func("setCustom", gzCustomLayout);
				}
				grid.func("GrayWindow",'1 \r\n 255');//遮罩/还原的动作
				gzContentWarning();
				grid.func("GrayWindow","0");//遮罩/还原的动作 */
			}
		}
	};
supcanGridMap["hq_gridtable"] = hqGridDefine;
$(function() {
	var colModelDatas = [
{name:'code',index:'code',align:'center',text : '代码',width:80,isHide:false,editable:false,dataType:'string'},
{name:'name',index:'name',align:'center',text : '名称',width:80,isHide:false,editable:false,dataType:'string'},
{name:'increasePercent',index:'increasePercent',align:'right',text : '涨幅',width:80,isHide:false,editable:false,dataType:'double',displayMask:'textColor=if(data>0, red, if(data=0,black,green))'},
{name:'close',index:'close',align:'right',text : '现价',width:80,isHide:false,editable:false,dataType:'double'},
{name:'high',index:'high',align:'right',text : '最高价',width:80,isHide:false,editable:false,dataType:'double'},
{name:'low',index:'low',width:'80px',align:'right',text : '最低价',isHide:false,editable:false,dataType:'double'},
{name:'volume',index:'volume',align:'right',text : '成交量',width:80,isHide:false,editable:false,dataType:'double'},
{name:'turnover',index:'turnover',width:'80px',align:'right',text : '成交额',isHide:false,editable:false,dataType:'double'}
		];  
	var hqGrid = jQuery.extend(true, {}, supCanTreeListGrid);
	hqGrid.Cols = colModelDatas;
	hqGridDefine.main.Build = JSON.stringify(hqGrid);
	
	jQuery.ajax({
		url: "${ctx}/hq/findHQGridList",
		//data: {curPeriod:curPeriod,curIssueNumber:curIssueNumber,gzTypeId:gzTypeId,curPeriodStatus:curPeriodStatus,lastPeriod:lastPeriod},
		type: 'post',
		dataType: 'json',
		async:true,
		error: function(data){
		},
		success: function(data){
			var page_data = data.page_data;
			var hqGridData = {};
			hqGridData.Record = page_data;
			hqGridDefine.main.Load = JSON.stringify(hqGridData);
			insertTreeListToDiv("hq_gridtable_div","hq_gridtable","","100%");
		}
	});
    /* $("#demo_grid1").bs_grid({
 
        ajaxFetchDataURL: "${ctx}/hq/findHQGridList",
        row_primary_key: "period",
 
        columns: [
            {field: "period", header: "日期"},
            {field: "code", header: "编码"},
            {field: "name", header: "名称"},
            {field: "increasePercent", header: "涨幅"},
            {field: "open", header: "开盘价"},
            {field: "close", header: "收盘价"},
            {field: "high", header: "最高价"},
            {field: "low", header: "最低价"},
            {field: "volume", header: "成交量"},
            {field: "turnover", header: "成交额"}
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
    }); */
	jQuery("#searchGpHq").click(function(){
		var gpCode = jQuery("#searchGpHqTxt").val();
		var filterStr = "1==1 ";
		filterStr += " and code='" +gpCode+"'";
		hq_gridtable.func("Filter", filterStr);
		
	});
	
	jQuery("#hqRk").click(function(){
		$.ajax({
			url: '${ctx}/hq/hqRk',
			type: 'post',
			dataType: 'json',
			async:false,
			error: function(data){
				alertMsg.error("系统错误！");
			},
			success: function(data){
				alert(data);
			}
		});
		
	});
});

</script>
</head>
<body>
	<div class="row">
         <div class="col-lg-6">
            <div class="input-group">
               <span class="input-group-btn">
                  <button class="btn btn-default" type="button" id="searchGpHq">
                     Go!
                  </button>
               </span>
               <input type="text" class="form-control" id="searchGpHqTxt">
            </div><!-- /input-group -->
         </div>
    </div>
	<div class="btn-toolbar" role="toolbar" style="margin:2px">
	  <div class="btn-group" style="margin-left:0px">
	  <button type="button" class="btn btn-primary" id="hqRk">入库</button>
	  <button type="button" class="btn btn-default">按钮 2</button>
	  <button type="button" class="btn btn-default">按钮 3</button>
	 </div>
	  <div class="btn-group">
	  <button type="button" class="btn btn-default">按钮 4</button>
	  <button type="button" class="btn btn-default">按钮 5</button>
	  <button type="button" class="btn btn-default">按钮 6</button>
	  </div>
	  <div class="btn-group">
	  <button type="button" class="btn btn-default">按钮 7</button>
	  <button type="button" class="btn btn-default">按钮 8</button>
	  <button type="button" class="btn btn-default">按钮 9</button>
	  </div>
	</div>
	<div id="demo_grid1"></div>
	<div id="hq_gridtable_div" style="height:98%"></div>
</body>
</html>