// PSY（N）=A÷N×100
code = '';
freq = 'd';

num = 5;
col = 'close';

out = 'avg';

xData = null;
codeData = Data.getGPData(code,'settlement,close',null,null);
result = {};
//init
avgArray = new Array();
if(!xData){
	xData = getPeriodArr(codeData);
}
for(var i in xData){
	if (i < num) {
		avgArray.push('-');
        continue;
    }
	var sum = 0;
	for (var j = 0; j < num; j++) {
		var period = xData[i - j];
		var data = codeData[period];
		var close = data.close;
		sum = Number(sum).add(Number(close));
    }
	var avg = Number(sum).div(num);
	avgArray.push(avg);
}

result.avg = avgArray;
result = json2str(result);
println(result);
