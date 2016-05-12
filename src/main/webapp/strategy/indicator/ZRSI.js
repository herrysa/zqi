
start = '2016-05-01';
end = '2016-05-11';
code = '';
contrast_code = '0000001';
benchmark = '0000001';

capital_base = 100000;
freq = 'd';

codeData = func_getGPData(code,'close',start,end);
contrastData = func_getGPData(contrast_code,'close',start,end);
result = new Array();
//init

for(var period in codeData){
	var data = codeData[period];
	var contrastData = contrastData[period];
	var close = data.close;
	var contrastClose = contrastData.close;
	var zrsi = Number(Number(close)*100).div(contrastClose);
	result.push(zrsi);
}
result = json2str(result);



