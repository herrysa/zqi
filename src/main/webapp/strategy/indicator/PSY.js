code = '';
freq = 'd';

N = 12;
out = 'PSY';

xData = null;
codeData = Data.getGPData(code,'settlement,close',null,null);
result = {};
//init

indicator = new Array();
if(!xData){
	xData = getPeriodArr(codeData);
}
for(var i in xData){
	if (i < N) {
		indicator.push('-');
        continue;
    }
	var A = 0;
	for (var j = 0; j < N; j++) {
		var period = xData[i - j];
		var data = codeData[period];
		var settlement = data.settlement;
		var close = data.close;
		if(close>settlement){
			A++;
		}
    }
	var psy = Number(Number(A)*100).div(N);
	indicator.push(psy);
}
result.PSY = indicator;
result = json2str(result);


