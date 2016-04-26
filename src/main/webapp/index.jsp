<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/links.jsp"%>
<%@ include file="/common/GuriddoLinks.jsp"%>
<%@ include file="/common/bootStrapLinks.jsp"%>
<%@ include file="/common/bsgridLinks.jsp"%>
<%@ include file="/common/bootstrap-tableLinks.jsp"%>
<%@ include file="/common/zqiLinks.jsp"%>
<html>
<head>
   <title>量化投资系统</title>
   <script type="text/javascript"	src="./scripts/layouts/complex.js"></script>
   <link rel="stylesheet" type="text/css" 	href="./scripts/layouts/complex.css" />
   <script type="text/javascript">
   	$(document).ready( function() {
		// create the OUTER LAYOUT
		outerLayout = $("body").layout( layoutSettings_Outer );
		outerLayout.addToggleBtn( "#tbarToggleNorth", "north" );
		outerLayout.addOpenBtn( "#tbarOpenSouth", "south" );
		outerLayout.addCloseBtn( "#tbarCloseSouth", "south" );
		outerLayout.addPinBtn( "#tbarPinWest", "west" );
		outerLayout.addPinBtn( "#tbarPinEast", "east" );
		
		// save selector strings to vars so we don't have to repeat it
		// must prefix paneClass with "body > " to target ONLY the outerLayout panes
		var westSelector = "body > .ui-layout-west"; // outer-west pane
		var eastSelector = "body > .ui-layout-east"; // outer-east pane

		 // CREATE SPANs for pin-buttons - using a generic class as identifiers
		$("<span></span>").addClass("pin-button").prependTo( westSelector );
		$("<span></span>").addClass("pin-button").prependTo( eastSelector );
		// BIND events to pin-buttons to make them functional
		outerLayout.addPinBtn( westSelector +" .pin-button", "west");
		outerLayout.addPinBtn( eastSelector +" .pin-button", "east" );

		 // CREATE SPANs for close-buttons - using unique IDs as identifiers
		$("<span></span>").attr("id", "west-closer" ).prependTo( westSelector );
		$("<span></span>").attr("id", "east-closer").prependTo( eastSelector );
		// BIND layout events to close-buttons to make them functional
		outerLayout.addCloseBtn("#west-closer", "west");
		outerLayout.addCloseBtn("#east-closer", "east");


		/* Create the INNER LAYOUT - nested inside the 'center pane' of the outer layout
		 * Inner Layout is create by createInnerLayout() function - on demand
		 *
			innerLayout = $("div.pane-center").layout( layoutSettings_Inner );
		 *
		 */


		// DEMO HELPER: prevent hyperlinks from reloading page when a 'base.href' is set
		$("a").each(function () {
			var path = document.location.href;
			if (path.substr(path.length-1)=="#") path = path.substr(0,path.length-1);
			if (this.href.substr(this.href.length-1) == "#") this.href = path +"#";
		});

   	});
   	var layoutSettings_Outer = {
   			name: "outerLayout" // NO FUNCTIONAL USE, but could be used by custom code to 'identify' a layout
   			// options.defaults apply to ALL PANES - but overridden by pane-specific settings
   		,	defaults: {
   				size:					"auto"
   			,	minSize:				50
   			,	paneClass:				"pane" 		// default = 'ui-layout-pane'
   			,	resizerClass:			"resizer"	// default = 'ui-layout-resizer'
   			,	togglerClass:			"toggler"	// default = 'ui-layout-toggler'
   			,	buttonClass:			"button"	// default = 'ui-layout-button'
   			,	contentSelector:		".content"	// inner div to auto-size so only it scrolls, not the entire pane!
   			,	contentIgnoreSelector:	"span"		// 'paneSelector' for content to 'ignore' when measuring room for content
   			,	togglerLength_open:		35			// WIDTH of toggler on north/south edges - HEIGHT on east/west edges
   			,	togglerLength_closed:	35			// "100%" OR -1 = full height
   			,	hideTogglerOnSlide:		true		// hide the toggler when pane is 'slid open'
   			,	togglerTip_open:		"Close This Pane"
   			,	togglerTip_closed:		"Open This Pane"
   			,	resizerTip:				"Resize This Pane"
   			//	effect defaults - overridden on some panes
   			,	fxName:					"slide"		// none, slide, drop, scale
   			,	fxSpeed_open:			750
   			,	fxSpeed_close:			1500
   			,	fxSettings_open:		{ easing: "easeInQuint" }
   			,	fxSettings_close:		{ easing: "easeOutQuint" }
   		}
   		,	north: {
   				spacing_open:			1			// cosmetic spacing
   			,	togglerLength_open:		0			// HIDE the toggler button
   			,	togglerLength_closed:	-1			// "100%" OR -1 = full width of pane
   			,	resizable: 				false
   			,	slidable:				false
   			//	override default effect
   			,	fxName:					"none"
   			}
   		,	south: {
   				maxSize:				200
   			,	spacing_closed:			0			// HIDE resizer & toggler when 'closed'
   			,	slidable:				false		// REFERENCE - cannot slide if spacing_closed = 0
   			,	initClosed:				true
   			//	CALLBACK TESTING...
   			,	onhide_start:			function () { return confirm("START South pane hide \n\n onhide_start callback \n\n Allow pane to hide?"); }
   			,	onhide_end:				function () { alert("END South pane hide \n\n onhide_end callback"); }
   			,	onshow_start:			function () { return confirm("START South pane show \n\n onshow_start callback \n\n Allow pane to show?"); }
   			,	onshow_end:				function () { alert("END South pane show \n\n onshow_end callback"); }
   			,	onopen_start:			function () { return confirm("START South pane open \n\n onopen_start callback \n\n Allow pane to open?"); }
   			,	onopen_end:				function () { alert("END South pane open \n\n onopen_end callback"); }
   			,	onclose_start:			function () { return confirm("START South pane close \n\n onclose_start callback \n\n Allow pane to close?"); }
   			,	onclose_end:			function () { alert("END South pane close \n\n onclose_end callback"); }
   			//,	onresize_start:			function () { return confirm("START South pane resize \n\n onresize_start callback \n\n Allow pane to be resized?)"); }
   			,	onresize_end:			function () { alert("END South pane resize \n\n onresize_end callback \n\n NOTE: onresize_start event was skipped."); }
   			}
   		,	west: {
   				size:					200
   			,	spacing_closed:			21			// wider space when closed
   			,	togglerLength_closed:	21			// make toggler 'square' - 21x21
   			,	togglerAlign_closed:	"top"		// align to top of resizer
   			,	togglerLength_open:		0			// NONE - using custom togglers INSIDE west-pane
   			,	togglerTip_open:		"Close West Pane"
   			,	togglerTip_closed:		"Open West Pane"
   			,	resizerTip_open:		"Resize West Pane"
   			,	slideTrigger_open:		"click" 	// default
   			,	initClosed:				false
   			//	add 'bounce' option to default 'slide' effect
   			,	fxSettings_open:		{ easing: "easeOutBounce" }
   			}
   		,	east: {
   				size:					250
   			,	spacing_closed:			21			// wider space when closed
   			,	togglerLength_closed:	21			// make toggler 'square' - 21x21
   			,	togglerAlign_closed:	"top"		// align to top of resizer
   			,	togglerLength_open:		0 			// NONE - using custom togglers INSIDE east-pane
   			,	togglerTip_open:		"Close East Pane"
   			,	togglerTip_closed:		"Open East Pane"
   			,	resizerTip_open:		"Resize East Pane"
   			,	slideTrigger_open:		"mouseover"
   			,	initClosed:				true
   			//	override default effect, speed, and settings
   			,	fxName:					"drop"
   			,	fxSpeed:				"normal"
   			,	fxSettings:				{ easing: "" } // nullify default easing
   			}
   		,	center: {
   				paneSelector:			"#mainContent" 			// sample: use an ID to select pane instead of a class
   			,	minWidth:				200
   			,	minHeight:				200
   			}
   		};
	function menuClick(url){
		//alert(url);
		$("#mainContent_center").load(url);
	}
   </script>
</head>
<body>

<div class="ui-layout-west">

	<div class="header" style="height:20px"></div>

	<div class="content" style="padding:1px">
		<!-- <div class="navbar navbar-duomi navbar-static-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/Admin/index.html" id="logo">配置管理系统（流量包月）
                </a>
            </div>
        </div>
    </div> -->
    <div class="container-fluid" style="padding:0px">
        <div class="row" style="margin:0px">
            <div class="col-md-2" style="width:100%;padding:0px">
                <ul id="main-nav" class="nav nav-tabs nav-stacked" style="">
                    <!-- <li class="active">
                        <a href="#">
                            <i class="glyphicon glyphicon-th-large"></i>
                            首页         
                        </a>
                    </li> -->
                    <li>
                        <a href="#systemSetting" class="nav-header collapsed" data-toggle="collapse">
                            <i class="glyphicon glyphicon-cog"></i>
                            系统管理
                               <span class="pull-right glyphicon glyphicon-chevron-down"></span>
                        </a>
                        <ul id="systemSetting" class="nav nav-list collapse secondmenu" style="height: 0px;">
                            <li><a href="#"><i class="glyphicon glyphicon-user"></i>用户管理</a></li>
                            <li><a href="#"><i class="glyphicon glyphicon-th-list"></i>菜单管理</a></li>
                            <li><a href="#"><i class="glyphicon glyphicon-asterisk"></i>角色管理</a></li>
                            <li><a href="#"><i class="glyphicon glyphicon-edit"></i>修改密码</a></li>
                            <li><a href="#"><i class="glyphicon glyphicon-eye-open"></i>日志查看</a></li>
                        </ul>
                    </li>
                    <li>
                        <a href="#primaryData" class="nav-header collapsed" data-toggle="collapse">
                            <i class="glyphicon glyphicon-credit-card"></i>
                            物料管理        
                        </a>
                        <ul id="primaryData" class="nav nav-list collapse secondmenu" style="height: 0px;">
                        	<li><a href="javaScript:menuClick('primaryData/primaryDataList?gpCode=sh600000')"><i class="glyphicon glyphicon-user"></i>原始数据</a></li>
                            <li><a href="javaScript:menuClick('hq/findHQ')"><i class="glyphicon glyphicon-th-list"></i>行情</a></li>
                        </ul>
                    </li>
 
                    <li>
                        <a href="#dataAnalysis" class="nav-header collapsed" data-toggle="collapse">
                            <i class="glyphicon glyphicon-globe"></i>
             数据分析
                            <!-- <span class="label label-warning pull-right">5</span> -->
                        </a>
                        <ul id="dataAnalysis" class="nav nav-list collapse secondmenu" style="height: 0px;">
                        	<li><a href="javaScript:menuClick('marketTemper/main')"><i class="glyphicon glyphicon-user"></i>市场温度</a></li>
                        </ul>
                    </li>
 
                    <li>
                        <a href="./charts.html">
                            <i class="glyphicon glyphicon-calendar"></i>
                            图表统计
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="glyphicon glyphicon-fire"></i>
                            关于系统
                        </a>
                    </li>
 
                </ul>
            </div>
            <!-- <div class="col-md-10">
                主窗口
            </div> -->
        </div>
    </div>
	</div>

</div>

<div class="ui-layout-east">

	<div class="header">Outer - East</div>

	<div class="subhead">I'm a subheader</div>

	<div class="content">
		<h3><b>Inner Layout</b></h3>
		<ul id="createInner">
			<li><a href="#" onClick="createInnerLayout(); return false;">CREATE Inner Layout</a></li>
		</ul>
		<ul id="innerCommands" style="display: none;">
			<li><a href="#" onClick="innerLayout.toggle('north')">原始数据</a></li>
			<li><a href="#" onClick="innerLayout.toggle('south')">Toggle South</a></li>
			<li><a href="#" onClick="innerLayout.toggle('west')"> Toggle West</a></li>
			<li><a href="#" onClick="innerLayout.toggle('east')"> Toggle East</a></li>
			<li><a href="#" onClick="innerLayout.hide('north')">Hide North</a></li>
			<li><a href="#" onClick="innerLayout.hide('south')">Hide South</a></li>
			<li><a href="#" onClick="innerLayout.hide('west')"> Hide West</a></li>
			<li><a href="#" onClick="innerLayout.hide('east')"> Hide East</a></li>
			<li><a href="#" onClick="innerLayout.show('east')"> Show East</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('north', 50); innerLayout.open('north')">   Resize North=50</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('north', 300); innerLayout.open('north')">  Resize North=300</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('north', 10000); innerLayout.open('north')">Resize North=10000</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('south', 50); innerLayout.open('south')">   Resize South=50</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('south', 300); innerLayout.open('south')">  Resize South=300</a></li>
			<li><a href="#" onClick="innerLayout.sizePane('south', 10000); innerLayout.open('south')">Resize South=10000</a></li>
			<li><a href="#" onClick="innerLayout.panes.north.css('backgroundColor','#FCC')">North Color = Red</a></li>
			<li><a href="#" onClick="innerLayout.panes.north.css('backgroundColor','#CFC')">North Color = Green</a></li>
			<li><a href="#" onClick="innerLayout.panes.north.css('backgroundColor','')">    North Color = Default</a></li>
			<li><a href="#" onClick="alert('innerLayout.name = \''+innerLayout.options.name+'\'')">Show Layout Name</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'defaults')">Show Options.Defaults</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'north')">   Show Options.North</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'south')">   Show Options.South</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'west')">    Show Options.West</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'east')">    Show Options.East</a></li>
			<li><a href="#" onClick="showOptions(innerLayout,'center')">  Show Options.Center</a></li>
			<li><a href="#" onClick="showState(innerLayout,'container')"> Show State.Container</a></li>
			<li><a href="#" onClick="showState(innerLayout,'north')">     Show State.North</a></li>
			<li><a href="#" onClick="showState(innerLayout,'south')">     Show State.South</a></li>
			<li><a href="#" onClick="showState(innerLayout,'west')">      Show State.West</a></li>
			<li><a href="#" onClick="showState(innerLayout,'east')">      Show State.East</a></li>
			<li><a href="#" onClick="showState(innerLayout,'center')">    Show State.Center</a></li>
		</ul>
	</div>

	<div class="footer">I'm a footer</div>
	<div class="footer">I'm another footer</div>
	<div class="footer">Unlimited headers &amp; footers</div>

</div>


<div class="ui-layout-north">
	<div class="header">Outer - North</div>
	<div class="content">
		I only have toggler when 'closed' - I cannot be resized - and I do not 'slide open'
	</div>
	<ul class="toolbar">
		<li id="tbarToggleNorth" class="first"><span></span>Toggle NORTH</li>
		<li id="tbarOpenSouth"><span></span>Open SOUTH</li>
		<li id="tbarCloseSouth"><span></span>Close SOUTH</li>
		<li id="tbarPinWest"><span></span>Pin/Unpin WEST</li>
		<li id="tbarPinEast" class="last"><span></span>Pin/Unpin EAST</li>
	</ul>
</div>


<div class="ui-layout-south">
	<div class="header">Outer - South</div>
	<div class="content">
		<p>I only have a resizer/toggler when 'open'</p>
	</div>
</div>


<div id="mainContent" style="padding:0px">
	<!-- DIVs for the INNER LAYOUT -->

	<div class="ui-layout-center" style="height:100%">
		<div id="mainContent_center" class="ui-layout-content" style="height:100%;padding:2px">
		
			
		</div>
	</div>

	<!-- <div class="ui-layout-north"> Inner - North</div>
	<div class="ui-layout-south"> Inner - South</div>
	<div class="ui-layout-west">  Inner - West</div>
	<div class="ui-layout-east">  Inner - East
		<p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p>
		<p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p>
		<p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p>
		<p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p><p>...</p>
	</div> -->

</div>

	

</body>
</html>