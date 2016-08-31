
code = '000001';
start = '2016-01-01';
end = '2016-09-14';

codeData = Data.getAllGPData('changepercent','{code:"000001",start:"2016-01-01",end:"2016-09-01"}');
xData = '';
result = {};
out = {upNum:'accu'};
wholeOut = {};
//init
var uNum = 0, dNum = 0 , sum = 0;
for(var x in xData){
	var period = xData[x];
	var data = codeData[period];
	var changepercent = data.changepercent;
	if(changepercent >0){
		uNum++;
	}else if(changepercent <0){
		dNum++;
	}
	sum++;
}
result['upNum'] = {uNum:uNum,dNum:dNum,sum:sum};
result = json2str(result);