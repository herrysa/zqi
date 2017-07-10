//name:5日均线买卖策略
code = '002142';

start = '2015-01-01';
end = '2016-07-01';
refresh_rate = 1;
benchmark = '0000300';

position = '';
runNum = 0;
xData = '';
codeData = Data.getGPData('{col:"close,@avg({close:[5,10,20,60]})",start:"2015-01-01",end:"2016-09-01"}');
benchmarkData = Data.getGPData('{code:"0000300",col:"close",start:"2015-01-01",end:"2016-09-01"}');

account = 1;
result = {};
out = {order:'table'};
wholeOut = {xData:'x',trans:'trans','收益率':'yield','沪深300':'myield'};
//init

var benchmark_base ,codeBase;
var benchmark_yield_rate = new Array();
var codeMA5 = new Array();
var orderArr = new Array();
var warning = false;
var warnDays = 0;
if(runNum%5==0){
	for(var p in position){
		var posi = position[p];
		var code = posi.code;
		for(var x in xData){
			var period = xData[x];
			var datas = codeData[period];
			for(var i=0;i<datas.length;i++){
				var dcode = data.code;
				var dclose = data.close;
				if(code==dcode){
					orderArr.push({code:code,amount:-1,price:dclose,otype:1});
				}
			}
		}
	}
	for(var x in xData){
		var period = xData[x];
		var datas = codeData[period];
		for(var i=0,i<5;i++){
			var num = Math.random();
			num = Math.ceil(num * datas.length);
			var data = datas[i];
			var code = data.code;
			var close = data.close;
			orderArr.push({code:code,amount:0.2,price:close,otype:1});
		}
	}
}
/*var strategy = function(period,i){
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
};*/

result.order = orderArr;
result = json2str(result);