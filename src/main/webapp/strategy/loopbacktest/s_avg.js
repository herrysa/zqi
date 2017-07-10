//name:羊驼策略
code = '';

start = '2015-01-01';
end = '2016-09-01';
step = 5;
refresh_rate = 1;
fq = 1;
benchmark = '0000300';

xData = '';
codeData = Data.getGPData({code:benchmark,col:'code,period',start:start,end:end,gpNum:"-1"});
benchmarkData = Data.getGPData({code:benchmark,col:'close',start:start,end:end,gpNum:"-1"});

position = '';
accountContext = '';

account = 1;
result = {};
out = {order:'table',accountContext:'json'};
wholeOut = {xData:'x',trans:'trans',posi:'posi','收益率':'yield','沪深300':'myield'};
//init
codeData2 = Data.getGPData({col:"code,close_p,@avg({close_p:[5,10,20,60]})",start:start,end:end,gpNum:"5",random:"1"});
//local

var benchmark_base ,codeBase;
var benchmark_yield_rate = new Array();
var orderArr = new Array();
var codePosition = {};
var buyFlag = 1;
var sellFlag = 0;
if(accountContext['buyFlag']||accountContext['buyFlag']==0){
	buyFlag = accountContext['buyFlag'];
}
if(accountContext['sellFlag']||accountContext['sellFlag']==0){
	sellFlag = accountContext['sellFlag'];
}
if(buyFlag=='1'){
	for(var p in codeData2){
		var datas = codeData2[p];
		for(var i in datas){
			var data = datas[i];
			var code = data.code;
			var avg5 = data.close_p5;
			var avg10 = data.close_p10;
			close = data.close_p;
			orderArr.push({code:code,amount:0.2,price:close,otype:1});
		}
	}
	buyFlag = 0;
	sellFlag = 1;
}else if(sellFlag=='1'){
	var pi = 0;
	for(var p in position){
		var data = position[p];
		var code = data.code;
		var yield = data.yield;
		var close = data.close;
		if(pi>1||yield>10){
			orderArr.push({code:code,amount:-1,price:close,otype:1});
		}
		pi++;
	}
	sellFlag = 0;
	buyFlag = 1;
}
accountContext['buyFlag'] = buyFlag;
accountContext['sellFlag'] = sellFlag;


result.accountContext = accountContext;
result.order = orderArr;
result = json2str(result);