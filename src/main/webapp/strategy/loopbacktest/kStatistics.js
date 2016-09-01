
code = '603288';
start = '2016-01-01';
end = '2016-09-14';

codeData = Data.getAllGPData('changepercent','{code:"603288",start:"2016-01-01",end:"2016-09-01"}');
xData = '';
result = {};
out = {upNum:'line'};
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
result['upNum'] = [1,2,2];
result = json2str(result);