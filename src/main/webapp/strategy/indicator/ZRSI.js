
start = '2016-05-01';
end = '2016-05-11';
code = '';
contrast_code = '0000001';
benchmark = '0000001';

capital_base = 100000;
freq = 'd';

out = 'zrsi';

codeData = Data.getGPData(code,'close,@avg(5,close)',start,end);
contrastData = Data.getGPData(contrast_code,'close',start,end);
result = {};
//init

indicator = new Array();
for(var period in codeData){
	var data = codeData[period];
	var contrastData = contrastData[period];
	var close = data.close;
	var contrastClose = contrastData.close;
	var zrsi = Number(Number(close)*100).div(contrastClose);
	indicator.push(zrsi);
}
result.zrsi = json2str(result);



