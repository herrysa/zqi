
code = '';
freq = 'd';

N = 12;
M = 6;
out = 'PSY,PSYMA';

xData = null;
codeData = Data.getGPData(code,'settlement,close',null,null);
result = {};
result.PSY = indicator.PSY;
result.PSYMA = lib.avg;
//init

result = json2str(result);


