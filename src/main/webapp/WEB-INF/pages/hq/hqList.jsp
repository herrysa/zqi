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
supcanGridMap.put("hq_gridtable",hqDefine);
$(function() {
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
 
});

</script>
</head>
<body>
	
	<div id="demo_grid1"></div>
</body>
</html>