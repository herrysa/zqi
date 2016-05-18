//name:5日均线买卖策略
code = '002142';

start = '2014-01-01';
end = '2015-01-01';
refresh_rate = 1;
benchmark = '0000300';

codeData = Data.getGPData(code,'close,@avg({close:[5]})',start,end);
benchmarkData = Data.getGPData(benchmark,'close',start,end);

account.capital_base = 1000000;
account.xData = getPeriodArr(benchmarkData);;
account.freq = 'd';
result = {};
//init

account.init();
var strategy = function(period){
	var otype = 'market';
	var data = codeData[period];
	var close = data.close;
	var avg5 = data.close5;
	if(close>avg5){
		this.order(code,this.cash/close,close,otype);
	}else if(close<avg5){
		this.order(code,-(this.cash/close),close,otype);
	}
}
account.strategy;
account.run();