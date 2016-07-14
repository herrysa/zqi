//name:日k线统计策略
code = '002142';

start = '2015-01-01';
end = '2016-07-14';

codeData = Data.getAllGPData('close,@avg({close:[5,10,20,60]})','{start:"2015-01-01",end:"2016-07-14"}');

result = {};
out = {upNum:'line'};
//init
var uNum = 0, dNum = 0;
for(var code in codeData){
	var codeData = codeData[code];
	var beforeChangepercent = -99;
	for(var period in codeData){
		var data = codeData[period];
		var changepercent = data.changepercent;
		if(beforeChangepercent = -99){
			
		}
	}
}
result['upNum'] = [uNum,dNum];
result = json2str(result);