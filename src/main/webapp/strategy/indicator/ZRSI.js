
start = '';
end = '';
code = '';
contrast_code = '0000001';
benchmark = '0000001';

capital_base = 100000;
freq = 'd';

out = 'zrsi';

xData = null;
codeData = Data.getGPData(code,'close,@avg({close:[5,10]})',start,end);
contrastData = Data.getGPData(contrast_code,'close',start,end);
result = {};
//init

indicator = new Array();
if(!xData){
	xData = getPeriodArr(codeData);
}
for(var i in xData){
	var period = xData[i];
	var data = codeData[period];
	var contrData = contrastData[period];
	var close = data.close;
	var contrastClose = contrData.close;
	var zrsi = Number(Number(close)*100).div(contrastClose);
	indicator.push(zrsi);
}

result.zrsi = indicator;
result = json2str(result);


