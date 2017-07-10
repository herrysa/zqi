
code = '000001';
start = '2016-01-01';
end = '2016-09-14';

codeData = Data.getGPData('{code:000001,col:"changepercent",start:"2016-01-01",end:"2016-09-01"}');
xData = '';
result = {};
out = {upNum:'json'};
wholeOut = {upNum:{type:'table',accu:true,cols:'uNum,dNum,oNum,sum'}};
//init
var uNum = 0, dNum = 0 , oNum = 0 , sum = 0;
for(var x in xData){
	var period = xData[x];
	var datas = codeData[period];
	for(var d in datas){
		var data = datas[d];
		var changepercent = data.changepercent;
		if(changepercent >0){
			uNum++;
		}else if(changepercent <0){
			dNum++;
		}else{
			oNum++;
		}
		sum++;
	}
}
result['upNum'] = {uNum:uNum,dNum:dNum,oNum:oNum,sum:sum};
result = json2str(result);