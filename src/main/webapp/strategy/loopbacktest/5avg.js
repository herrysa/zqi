//name:5日均线买卖策略
code = '000019';

start = '2015-01-01';
end = '2016-07-01';
refresh_rate = 1;
fq = 1;
benchmark = '0000300';

xData = '';
codeData = Data.getGPData('{code:"600307",col:"code,close_p,@avg({close_p:[5,10,20,60]})",start:"2015-01-01",end:"2016-09-01",ex_suspended:"0"}');
benchmarkData = Data.getGPData('{code:"600307",col:"close",start:"2015-01-01",end:"2016-09-01"}');
position = '';
accountContext = '';

account = 1;
result = {};
out = {order:'table',accountContext:'json'};
wholeOut = {xData:'x',trans:'trans',posi:'posi','收益率':'yield','沪深300':'myield'};
//init

var benchmark_base ,codeBase;
var benchmark_yield_rate = new Array();
var codeMA5 = new Array();
var orderArr = new Array();
var warning = false;
var warnDays = 0;
var codePosition = {};
for(var p in position){
	var data = position[p];
	var code = data.code;
	codePosition[code] = data;
}
for(var x in xData){
	var period = xData[x];
	var data = codeData[period];
	var code = data.code;
	if(!accountContext){
		accountContext = {};
	}
	var codeParam = accountContext[code];
	var buyFlag = 1;
	var sellFlag = 0;
	if(codeParam){
		buyFlag = codeParam.buyFlag;
		sellFlag = codeParam.sellFlag;
	}
	var opted = 0;
	if(data){
		close = data.close_p;
		avg5 = data.close_p5;
		avg10 = data.close_p10;
		avg20 = data.close_p20;
		avg60 = data.close_p60;
		if(avg10){
			var ps = codePosition[code];
			if(ps){
				var yield = ps.yield;
				if(yield>20){
					if(sellFlag==1){
						orderArr.push({code:code,amount:-1,price:close,otype:1});
					}
				}
			}
			
			if(avg5<avg10){
				if(sellFlag==1){
					if(ps){
						orderArr.push({code:code,amount:-1,price:close,otype:1});
					}
					buyFlag = 1;
					sellFlag = 0;
				}
			}else{
				if(avg5>=avg10){
					if(buyFlag==1){
						orderArr.push({code:code,amount:1,price:close,otype:1});
						buyFlag = 0;
						sellFlag = 1;
					}
				}
			}
		}
		
	}
	accountContext[code] = {buyFlag:buyFlag,sellFlag:sellFlag};
}

result.accountContext = accountContext;
result.order = orderArr;
result = json2str(result);