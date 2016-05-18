
code = '';
freq = 'd';

N = 12;
M = 6;
out = 'PSY,PSYMA';

xData = null;
codeData = Data.getGPData(code,'settlement,close',null,null);
result = {};
PSY = indicator.PSY;
PSYMA = new Array();
//init
for(var i in PSY){
	if (i < M) {
		PSYMA.push('-');
        continue;
    }
	var sum = 0;
	for (var j = 0; j < M; j++) {
		var data = PSY[i - j];
		sum = Number(sum).add(Number(data));
    }
	var avg = Number(sum).div(M);
	PSYMA.push(avg);
}
result.PSY = PSY;
result.PSYMA = PSYMA;
result = json2str(result);


