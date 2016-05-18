//name:5日均线买卖策略
code = '002142';

start = '2014-01-01';
end = '2015-01-01';
capital_base = 1000000;
refresh_rate = 1;
benchmark = '0000300';
freq = 'd';

xData = null;
codeData = Data.getGPData(code,'close,@avg({close:[5]})',start,end);
benchmarkData = Data.getGPData(benchmark,'close',start,end);

result = {};
//init

indicator = new Array();
if(!xData){
	xData = getPeriodArr(benchmarkData);
}
var otype = 'market';
for(var i in xData){
	var period = xData[i];
	var data = codeData[period];
	var markData = benchmarkData[period];
	var close = data.close;
	var avg5 = data.close5;
	var markClose = markData.close;
	if(close>avg5){
		order(code,amount,price,otype);
	}
	if(close<avg5){
		order();
	}
	indicator.push(zrsi);
}