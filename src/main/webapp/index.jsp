<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/links.jsp"%>
<%@ include file="/common/bootStrapLinks.jsp"%>
<html>
<head>
<style type='text/css'>
      body {
        background-color: #CCC;
      }
    </style>
</head>
<body>
	<div id="main">
		<div id="head" class='navbar navbar-default'>
		  <div class='nav-collapse'>
		    <ul class="nav navbar-nav">
		      <li class="active"><a href="#">Home</a></li>
		      <li><a href="#">Page One</a></li>
		      <li><a href="#">Page Two</a></li>
		    </ul>
		  </div>
		</div>
		<div id='content' class='row-fluid' style="height:600px">
			<div class='sidebar' style="float:left;height:600px">
				<div class="accordion" id="menu-27">
					<div class="accordion-group">
				    <div class="accordion-heading">
				    	<a class="accordion-toggle" data-toggle="collapse" data-parent="#menu-27" data-href="#collapse-28" href="#collapse-28" title=""><i class="icon-chevron-down"></i>&nbsp;个人信息</a>
				    </div>
        			<div id="collapse-28" class="accordion-body collapse in">
						<div class="accordion-inner">
							<ul class="nav nav-list">
								<li class="active"><a data-href=".menu3-29" href="/jeesite/a/sys/user/info" target="mainFrame" jerichotabindex="0"><i class="icon-user icon-white"></i>&nbsp;个人信息</a>
								</li>
								<li><a data-href=".menu3-30" href="/jeesite/a/sys/user/modifyPwd" target="mainFrame"><i class="icon-lock"></i>&nbsp;修改密码</a>
								</li>
							</ul>
						</div>
					</div>
					</div>
					</div>
			</div>
			<div id="openClose" class="close" style="float:left;background-color:red; height:600px">&nbsp;</div>
			<div class='main' style="float:left;height:600px">
				<h2>Main Content Section</h2>
			</div>
		</div>	
		<div class="foot" style="height:50px">
			foot
		</div>
	</div>
</body>
</html>