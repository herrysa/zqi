
start = '';
end = '';
code = '';
contrast_code = '0000001';
benchmark = '0000001';

capital_base = 100000;
freq = 'd';

out = 'ZRSI';

xData = null;
codeData = Data.getGPData(code,'close',start,end);
contrastData = Data.getGPData(contrast_code,'close',start,end);
result = {};
//init

result.ZRSI = indicator.ZRSI;
result = json2str(result);


