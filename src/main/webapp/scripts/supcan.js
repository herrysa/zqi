var supcanGridMap = {};
function OnReady( id ){
	var supcanGrid = supcanGridMap[id];
	if(supcanGrid){
		var main = supcanGrid.main;
		if(main){
			for(var f in main){
				if(main[f]){
					eval("("+id+")").func(f,main[f]);
				}else{
					eval("("+id+")").func(f,"");
				}
			}
		}
		var callback = supcanGrid.callback;
		if(callback){
			if(callback.onComplete&&typeof(callback.onComplete)=='function'){
				callback.onComplete(id);
			}
		}
		/*var supcanGridDefine = supcanGrid.define;
		var supcanGridData = supcanGrid.data;
		eval("("+id+")").func("Build",JSON.stringify(supcanGridDefine));
		eval("("+id+")").func("Load",JSON.stringify(supcanGridData));*/
	}
}
function OnEvent( id, Event, p1, p2, p3, p4){
	var supcanGrid = supcanGridMap[id];
	if(supcanGrid){
		var eventMap = supcanGrid.event;
		if(eventMap){
			var e = eventMap[Event];
			if(e&&typeof(e)=='function'){
				e(id,p1, p2, p3, p4);
			}
		}
	}
}
/*硕正字符串格式化*/
function parseBuildString(buildStr){
	if(buildStr){
		buildStr = buildStr.replace(/Col_([0-9]*)_Col/ig, "Col");
		buildStr = buildStr.replace(/group_([0-9]*)_group/ig, "group");
	}
	return buildStr;
}
//硕正treeList控件字体
var supCanFont = [//bold 0不加粗；1加粗   underline 0没有下滑线；1有下划线
                  {faceName:"微软雅黑",bold:"0",underline:"0"},
                  {faceName:"微软雅黑",bold:"1",underline:"0"},
                  {faceName:"微软雅黑",bold:"0",underline:"1"},
                  {faceName:"微软雅黑",bold:"1",underline:"1"},
                  {faceName:"宋体",bold:"0",underline:"0"},
                  {faceName:"宋体",bold:"1",underline:"0"},
                  {faceName:"宋体",bold:"0",underline:"1"},
                  {faceName:"宋体",bold:"1",underline:"1"},
                  {faceName:"方正舒体",bold:"0",underline:"0"},
                  {faceName:"方正姚体",bold:"0",underline:"0"},
                  {faceName:"仿宋体",bold:"0",underline:"0"},
                  {faceName:"黑体",bold:"0",underline:"0"},
                  {faceName:"华文彩云",bold:"0",underline:"0"},
                  {faceName:"华文仿宋",bold:"0",underline:"0"},
                  {faceName:"华文琥珀",bold:"0",underline:"0"},
                  {faceName:"华文楷体",bold:"0",underline:"0"},
                  {faceName:"华文隶书",bold:"0",underline:"0"},
                  {faceName:"华文宋体",bold:"0",underline:"0"},
                  {faceName:"华文细黑",bold:"0",underline:"0"},
                  {faceName:"华文新魏",bold:"0",underline:"0"},
                  {faceName:"华文行楷",bold:"0",underline:"0"},
                  {faceName:"华文中宋",bold:"0",underline:"0"},
                  {faceName:"楷体",bold:"0",underline:"0"},
                  {faceName:"隶书",bold:"0",underline:"0"},
                  {faceName:"幼圆",bold:"0",underline:"0"}
              ];

var supCanTreeListGrid = {
		"Properties": {
			"editAble":"true",
			"Title":"工资编辑表", 
			"HeaderFontIndex":"1",
			"rowHeight":"23px",
			"autoBreakLine":"1",//文字显示的超宽处理
			"curselmode":"rows",//当前行或区域的显示模式
			"headerBackColor":"#dfeffc",//顶部标题条的背景色
			"leftBackColor":"#dfeffc",//左标尺的背景色
			"GridColor":"#a6c9e2",//表格线颜色
			"backColor":"#FFFFFF",//背景色
			"addRowAble":false,//是否允许用户增行操作
			"deleteRowAble":false,//是否允许用户删行操作
			"ListTreeSwitchAble":false,//是否允许在 列表 和 树 之间任意切换
			"isHiLightModified":true,//修改过的单元格, 是否在其左上角显示一个醒目的标记
			"totalBgColor":"#dfeffc",//合计/小计行的背景色
			"totalFontIndex":1,//合计/小计行的字体顺序号
			"curSelBackColor":"#fbec88",//当前行的颜色
			'subtotalFontIndex':"1",
			'subtotalBgColor':"#dfeffc",
			"fadeInStep":"255",//执行 Load( ) 函数时的淡入淡出效果
			"multiLayerAble":false,//是否允许多层表头	
			"sort":"orgCode,deptCode,personCode"//排序列
			},"Fonts": supCanFont,
		  "Cols": []
};
	/*根据数据类型增加edittype*/
	function supCanAddToEditOption(colModelData,row){
		var dataType,spinAsCalendar,totalExpress,totalAlign;
		switch(row.itemType){
		case "0"://数值型
			dataType = "double";
			totalExpress = "@sum";
			totalAlign = "right";
			break;
		case "2"://日期型
			spinAsCalendar = true;
			dataType = "date";
			break;
		case "3"://整型
			dataType = "int";
			totalExpress = "@sum";
			totalAlign = "right";
			break;
		default:
			dataType = "string";
			break;
		}
		if(dataType){
			colModelData.dataType = dataType;
		}
		if(spinAsCalendar){
			colModelData.spin = 'true';
			colModelData.spinAsCalendar = 'true';
		}
		if(totalExpress){
			colModelData.totalExpress = totalExpress;
			colModelData.totalAlign = totalAlign;
		}
		return colModelData;
	}
	/*根据数据类型，设定对齐方式*/
	function supCanParseAlign(type){
		var align = "left";
		switch(type){
			case "0"://数值型
			case "3"://数值型
				align = "right";break;
			case "1"://字符型
			break;
			case "2"://日期型
				align = "center";break;
			default:
				break;
		}
		return align;
	}
	 var supCanPages =//硕正打印纸
     {
         '9': 'A4（21厘米 × 29.7厘米）',
         '1': '信纸（21.59厘米 × 27.94厘米）',
         '2': '小号信纸（21.59厘米 × 27.94厘米）',
         '3': 'Tabloid 11 x 17 in',
         '4': 'Ledger 17 x 11 in',
         '5': '法律专用纸（21.59厘米 × 35.56厘米）',
         '6': 'Statement（13.97厘米 × 21.59厘米）',
         '7': 'Executive（18.41厘米 × 26.67厘米）',
         '8': 'A3（29.7厘米 × 42厘米）',
         '10': 'A4 小号 （21厘米 × 29.7厘米）',
         '11': 'A5 （14.8厘米 × 21厘米）',
         '12': 'B4 (JIS) (25厘米 × 35.4厘米)',
         '13': 'B5 (JIS) (18.2厘米 × 25.7厘米)',
         '14': 'Folio (21.59厘米 × 33.02厘米)',
         '15': 'Quarto (21.5厘米 × 27.5厘米)',
         '16': '10x14 in',
         '17': '11x17 in',
         '18': '便签 (21.59厘米 × 27.94厘米)',
         '19': '信封 #9 (9.84厘米 × 22.54厘米)',
         '20': '信封 #10 (10.47厘米 × 24.13厘米)',
         '21': '信封 #11 (11.43厘米 × 26.35厘米)',
         '22': '信封 #12 (12.06厘米 × 27.94厘米)',
         '23': '信封 #14 (12.7厘米 × 29.21厘米)',
         '24': 'C size sheet',
         '25': 'D size sheet',
         '26': 'E size sheet',
         '27': '信封 DL (11厘米 × 22厘米)',
         '28': '信封 C5 (16.2厘米 × 22.9厘米)162 x 229 mm',
         '29': '信封 C3 (32.4厘米 × 45.8厘米)324 x 458 mm',
         '30': '信封 C4 (22.9厘米 × 32.4厘米)229 x 324 mm',
         '31': '信封 C6 (11.4厘米 × 16.2厘米)114 x 162 mm',
         '32': '信封 C65 (11.4厘米 × 22.9厘米)114 x 229 mm',
         '33': '信封 B4 (25厘米 × 35.3厘米)250 x 353 mm',
         '34': '信封 B5 (17.6厘米 × 25厘米)176 x 250 mm',
         '35': '信封 B6 (17.6厘米 × 12.5厘米)176 x 125 mm',
         '36': '信封 (11厘米 × 23厘米)110 x 230 mm',
         '37': '信封 Monarch (9.84厘米 × 19.05厘米)',
         '38': '6 3/4 信封 (9.2厘米 × 16.51厘米)',
         '39': 'US Std Fanfold 14 7/8 x 11 in',
         '40': '德国标准 Fanfold (21.59厘米 × 30.48厘米)',
         '41': '德国法律专用纸 Fanfold (21.59厘米 × 33.02厘米)',
         '42': 'B4 (ISO) (25厘米 × 35.3厘米)',
         '43': '日式明信片 (10厘米 × 14.8厘米)',
         '44': '9 x 11 in',
         '45': '10 x 11 in',
         '46': '15 x 11 in',
         '47': 'Envelope Invite 220 x 220 mm',
         //'48': 'RESERVED--DO NOT USE',
         //'49': 'RESERVED--DO NOT USE',
         '50': 'Letter Extra 9 \275 x 12 in',
         '51': 'Legal Extra 9 \275 x 15 in',
         '52': 'Tabloid Extra 11.69 x 18 in',
         '53': 'A4 Extra 9.27 x 12.69 in',
         '54': 'Letter Transverse 8 \275 x 11 in',
         '55': 'A4 Transverse 210 x 297 mm',
         '56': 'Letter Extra Transverse 9\275 x 12 in',
         '57': 'SuperA/SuperA/A4 227 x 356 mm',
         '58': 'SuperB/SuperB/A3 305 x 487 mm',
         '59': 'Letter Plus 8.5 x 12.69 in',
         '60': 'A4 Plus 210 x 330 mm',
         '61': 'A5 Transverse 148 x 210 mm',
         '62': 'B5 (JIS) Transverse 182 x 257 mm',
         '63': 'A3 Extra 322 x 445 mm',
         '64': 'A5 Extra 174 x 235 mm',
         '65': 'B5 (ISO) Extra 201 x 276 mm',
         '66': 'A2 420 x 594 mm',
         '67': 'A3 Transverse 297 x 420 mm',
         '68': 'A3 Extra Transverse 322 x 445 mm',
         '69': 'Japanese Double Postcard 200 x 148 mm',
         '70': 'A6 105 x 148 mm',
         '71': 'Japanese Envelope Kaku #2',
         '72': 'Japanese Envelope Kaku #3',
         '73': 'Japanese Envelope Chou #3',
         '74': 'Japanese Envelope Chou #4',
         '75': 'Letter Rotated 11 x 8 1/2 11 in',
         '76': 'A3 Rotated 420 x 297 mm',
         '77': 'A4 Rotated 297 x 210 mm',
         '78': 'A5 Rotated 210 x 148 mm',
         '79': 'B4 (JIS) Rotated 364 x 257 mm',
         '80': 'B5 (JIS) Rotated 257 x 182 mm',
         '81': 'Japanese Postcard Rotated 148 x 100 mm',
         '82': 'Double Japanese Postcard Rotated 148 x 200 mm',
         '83': 'A6 Rotated 148 x 105 mm',
         '84': 'Japanese Envelope Kaku #2 Rotated',
         '85': 'Japanese Envelope Kaku #3 Rotated',
         '86': 'Japanese Envelope Chou #3 Rotated',
         '87': 'Japanese Envelope Chou #4 Rotated',
         '88': 'B6 (JIS) 128 x 182 mm',
         '89': 'B6 (JIS) Rotated 182 x 128 mm',
         '90': '12 x 11 in',
         '91': 'Japanese Envelope You #4',
         '92': 'Japanese Envelope You #4 Rotated',
         '93': 'PRC 16K 146 x 215 mm',
         '94': 'PRC 32K 97 x 151 mm',
         '95': 'PRC 32K(Big) 97 x 151 mm',
         '96': 'PRC Envelope #1 102 x 165 mm',
         '97': 'PRC Envelope #2 102 x 176 mm',
         '98': 'PRC Envelope #3 125 x 176 mm',
         '99': 'PRC Envelope #4 110 x 208 mm',
         '100': 'PRC Envelope #5 110 x 220 mm',
         '101': 'PRC Envelope #6 120 x 230 mm',
         '102': 'PRC Envelope #7 160 x 230 mm',
         '103': 'PRC Envelope #8 120 x 309 mm',
         '104': 'PRC Envelope #9 229 x 324 mm',
         '105': 'PRC Envelope #10 324 x 458 mm',
         '106': 'PRC 16K Rotated',
         '107': 'PRC 32K Rotated',
         '108': 'PRC 32K(Big) Rotated',
         '109': 'PRC Envelope #1 Rotated 165 x 102 mm',
         '110': 'PRC Envelope #2 Rotated 176 x 102 mm',
         '111': 'PRC Envelope #3 Rotated 176 x 125 mm',
         '112': 'PRC Envelope #4 Rotated 208 x 110 mm',
         '113': 'PRC Envelope #5 Rotated 220 x 110 mm',
         '114': 'PRC Envelope #6 Rotated 230 x 120 mm',
         '115': 'PRC Envelope #7 Rotated 230 x 160 mm',
         '116': 'PRC Envelope #8 Rotated 309 x 120 mm',
         '117': 'PRC Envelope #9 Rotated 324 x 229 mm',
         '118': 'PRC Envelope #10 Rotated 458 x 324 mm'
     }