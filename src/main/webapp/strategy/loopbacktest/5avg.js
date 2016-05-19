//name:5日均线买卖策略
code = '002142';

start = '2015-01-01';
end = '2016-07-01';
refresh_rate = 1;
benchmark = '0000300';

codeData = Data.getGPData(code,'close,@avg({close:[5,10,20,60]})',start,end);
benchmarkData = Data.getGPData(benchmark,'close',start,end);

account.capital_base = 1000000;
account.xData = getPeriodArr(benchmarkData);;
account.freq = 'd';
result = {};
out = {xData:'line',trans:'table','宁波银行MA5':'line','收益率':'line','沪深300':'line'};
//init

account.init();
var benchmark_base ,codeBase;
var benchmark_yield_rate = new Array();
var codeMA5 = new Array();
var warning = false;
var warnDays = 0;
var strategy = function(period,i){
	var otype = 'market';
	var data = codeData[period];
	var close ;
	var avg5 ,avg10,avg20,avg60;
	if(data){
		close = data.close;
		avg5 = data.close5;
		avg10 = data.close10;
		avg20 = data.close20;
		avg60 = data.close60;
		if(avg60){
			if(close<avg60){
				this.order(code,this.cash/close,close,otype);
			}
			
						
			if(!codeBase){
				codeBase = avg5;
				codeMA5.push(0);
			}else{
				codeMA5.push(Number(avg5).sub(codeBase).div(codeBase).mul(100));
			}
		}
	}
	
	var markData = benchmarkData[period];
	var mclose = markData.close;
	if(i==0){
		benchmark_base = mclose;
	}else{
		benchmark_yield_rate.push(Number(mclose).sub(benchmark_base).div(benchmark_base).mul(100));
	}
};
account.code.data = codeData;
account.strategy = strategy;
account.run();

result.xData = account.xData;
result.trans = account.transaction;
result.宁波银行MA5 = codeMA5;
result.收益率 = account.yield_rate;
result['沪深300'] = benchmark_yield_rate;
result = json2str(result);