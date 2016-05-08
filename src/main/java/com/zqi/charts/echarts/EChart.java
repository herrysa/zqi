package com.zqi.charts.echarts;



public class EChart {

	public String backgroundColor;	//全图默认背景  默认为无，透明
	public String[] color;			//数值系列的颜色列表
	public Boolean renderAsImage;	//非IE8-支持渲染为图片
	public Boolean calculable;		//是否启用拖拽重计算特性，默认关闭
	public String calculableColor;
	public String calculableHolderColor;
	public String nameConnector;
	public String valueConnector;
	public Boolean animation;		//是否开启动画，默认开启
	public Boolean addDataAnimation;
	public Integer animationThreshold;
	public Integer animationDuration;
	public Integer animationDurationUpdate;
	public String animationEasing;
	
	
	public Timeline timeline;		//时间轴，每个图表最多仅有一个时间轴控件
	public Title title;				//标题，每个图表最多仅有一个标题控件
	public Toolbox toolbox;			//工具箱，每个图表最多仅有一个工具箱
	public Tooltip tooltip;			//提示框，鼠标悬浮交互时的信息提示
	public Legend legend;			//图例，每个图表最多仅有一个图例，混搭图表共享
	public DataRange dataRange;		//值域选择,值域范围
	public DataZoom dataZoom;		//数据区域缩放,数据展现范围选择
	public RoamController roamController;		//漫游缩放组件,搭配地图使用
	public Grid grid;				//直角坐标系内绘图网格
	public XAxis xAxis;				//直角坐标系中横轴数组
	public YAxis yAxis;				//直角坐标系中纵轴数组
	public Series series;			//驱动图表生成的数据内容
	
	public String getChart(){
		String chart = "";
		if(backgroundColor!=null){
			chart += "backgroundColor:"+ChartTools.getStr(backgroundColor)+",";
		}
		if(color!=null){
			chart += "color:"+ChartTools.getArrayStr(color)+",";
		}
		if(renderAsImage!=null){
			chart += "renderAsImage:"+ChartTools.getBooleanStr(renderAsImage)+",";
		}
		return chart;
	}
	
}
