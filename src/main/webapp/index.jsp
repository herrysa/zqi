<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/links.jsp"%>
<%@ include file="/common/GuriddoLinks.jsp"%>
<%@ include file="/common/bootStrapLinks.jsp"%>
<%@ include file="/common/zqiLinks.jsp"%>
<html>
<head>
<script type="text/javascript">
   	$(document).ready( function() {
		var documentWidth = $(document).width();
		var documentHeight = $(document).height();
		var containerWidth = documentWidth;
		var contentWidth = containerWidth - 150 -5;
		var contentHeight = documentHeight - 105;
		$("#container").width(containerWidth);
		$("#mainContent").width(contentWidth);
		$("#content").height(contentHeight);
   		
   	});
   	function menuClick(url){
			//alert(url);
			$("#mainContent").load(url);
	}
</script>
<style type='text/css'>
		
		#head{
			height:50px;
			margin-bottom:5px;
		}
		#openClose{
			float:left;
			background-color:#CCC;
			width:5px;
			height:100%
		}
		
		#mainContent{
			margin:0;
			background-color: white;
			float:left;
			width:500px;
			height:100%;
			font-weight: normal;
			font-size: 12px;
			font-family: 微软雅黑
		}
		#mainContent label{
			font-weight: normal;
			font-size: 12px;
			font-family: 微软雅黑
		}
		#foot{
			height:50px;
		}
		
		.page{
			height:100%
		}
		.pageContent{
			height:100%
		}
		
		.form-search{
			margin-bottom :0
		}
		.modal-full {
			width: 100%;
			height:100%;
			margin:0
		}
		#modalDialog .modal-content{
			height:100%;
			margin:0
		}
    </style>
</head>
<body>
	<div id="container">
		<div id="head" class='navbar navbar-default'>
		  <div class='nav-collapse'>
		    <ul class="nav navbar-nav">
		      <li class="active"><a href="#">Home</a></li>
		      <li><a href="#">Page One</a></li>
		      <li><a href="#">Page Two</a></li>
		    </ul>
		  </div>
		</div>
		<div id='content' class='row-fluid' style="height:400px">
			<div class='sidebar'>
				<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
				  <div class="panel panel-default">
				    <div class="panel-heading" role="tab" id="headingOne">
				        <a class="accordion-head" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
							原始数据
				        </a>
				    </div>
				    <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
				      <div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li><a href="javaScript:menuClick('init/initpage')">初始化</a></li>
							<li><a href="javaScript:menuClick('primaryData/primaryDataList')">原始数据</a></li>
							<li><a href="javaScript:menuClick('chart/kChart?code=600847')">图形</a></li>
							<li><a href="javaScript:menuClick('hq/findHQ')">行情</a></li>
						</ul>
				      </div>
				    </div>
				  </div>
				</div>
				<div class="panel-group" id="reportMenu" role="tablist" aria-multiselectable="true">
				  <div class="panel panel-default">
				    <div class="panel-heading" role="tab" id="reportMenu-head">
				        <a class="accordion-head" role="button" data-toggle="collapse" data-parent="#accordion" href="#reportMenu-content" aria-expanded="false" aria-controls="reportMenu-content">
							报表系统
				        </a>
				    </div>
				    <div id="reportMenu-content" class="panel-collapse collapse" role="tabpanel" aria-labelledby="reportMenu-head">
				      <div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li><a href="javaScript:menuClick('report/reportMain')">报表定义</a></li>
							<li><a href="#">Java面向对象</a></li>
							<li><a href="#">Java核心API</a></li>
							<li><a href="#">JavaEE</a></li>
						</ul>
				      </div>
				    </div>
				  </div>
				</div>
			</div>
			<div id="openClose" class="close">&nbsp;</div>
			<div id="mainContent" class='mainContent'>
				
			</div>
		</div>	
		<div id="foot">
			foot
		</div>
	</div>
	<div class="modal fade" id="modalDialog" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-full" role="document">
			<div class="modal-content">
				<div class="modal-header">
				  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				  
				</div>
				<div class="modal-body">
					1111111111
				</div>
      <!-- <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Send message</button>
      </div> -->
    </div>
  </div>
</div>
</body>
</html>