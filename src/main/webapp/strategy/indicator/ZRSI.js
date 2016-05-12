
start = '2016-05-01';
end = '2016-05-11';
code = '';
benchmark = '0000001';

capital_base = 100000;
freq = 'd';

codeData = {'2015-01-01':{'close':1}};//getGPData(code,['close'],start,end);
benchmarkData = {'2015-01-01':{'close':22}};//getGPData(benchmark,['close'],start,end);
result = new Array();
//init

for(var period in codeData){
	var data = codeData[period];
	var benchmarkdata = benchmarkData[period];
	var close = data.close;
	var benchmarkclose = benchmarkdata.close;
	var zrsi = Number(Number(close)*100).div(benchmarkclose);
	result.push(zrsi);
}
result = json2str(result);



