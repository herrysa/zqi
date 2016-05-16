
code = '';
freq = 'd';

N = 12;
M = 6;
out = 'PSY,PSYMA';

xData = null;
codeData = Data.getGPData(code,'settlement,close',null,null);
result = {};
//init

result.PSY = indicator.PSY;
result.PSYMA = lib.avg;
result = json2str(result);


