<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<script>
$(function() {
	$("#runQuant").click(function(){
		var param = {};
		param.quantName = $("#quant_name").val();
		param.start = $("#quant_start").val();
		param.end = $("#quant_end").val();
		
		var ps = $("#quant_ps").val();
		var ps_num = new Number(ps);
		if(ps_num!=0){
			param.ps =  ps_num;
		}
		
		var p = $("#quant_profit").val();
		var p_num = new Number(p);
		if(p_num!=0){
			param.profit = p_num;
		}
		
		var l = $("#quant_loss").val();
		var l_num = new Number(l);
		if(l_num!=0){
			param.loss = l_num;
		}
		var paramStr = JSON.stringify(param);
		//$('.modal-body','#modalDialog').html("");
		$('.modal-body','#modalDialog').load("strategy/strategyResult?accountCode="+$("#quant_accountCode").val()+"&code="+$("#quant_code").val()+"&type="+$("#quant_type").val()+"&param="+paramStr);
		$("#strategy_gtidtable").jqGrid("setGridParam", {
			search : true
		}).trigger("reloadGrid", [ {
				page : 1
		}]);
	});
});
</script>
</head>
<body>
	<div class="page">
		<div class="pageContent">
			<form id="strategyform" class="form-horizontal pageForm" role="form" action="strategy/save">
				<div class="form-group">
					<label class="col-sm-2 control-label">历史策略:</label>
					<div class="col-sm-4">
						<select id="quant_accountCode" name="accountCode">
							<option value="">--</option>
							<c:forEach items="${accountCodes}" var="ac">
								<option value="${ac.accountCode }">${ac.accountCode }</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">策略名称:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_name" class="form-control input-sm" value="${paramMap.quantName}" />
					</div>
				</div>
				<div class="form-group">
					<input type="hidden" id="quant_code" class="form-control input-sm" value="${paramMap.code}"/>
					<input type="hidden" id="quant_type" class="form-control input-sm" value="${paramMap.type}"/>
					<label class="col-sm-2 control-label">开始时间:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_start" class="form-control input-sm" value="${paramMap.start}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">结束时间:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_end" class="form-control input-sm" value="${paramMap.end}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">每笔仓位:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_ps" class="form-control input-sm" value="${paramMap.ps}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">止盈率:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_profit" class="form-control input-sm" value="${paramMap.profit}"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">止损率:</label>
					<div class="col-sm-4">
						<input type="text" id="quant_loss" class="form-control input-sm" value="${paramMap.loss}"/>
					</div>
				</div>
				<div class="form-group form-actions">
					<div class="col-sm-2"></div>
					<div class="col-sm-4">
					<input id="runQuant" class="btn btn-primary" type="button" value="运行"/>&nbsp;
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>