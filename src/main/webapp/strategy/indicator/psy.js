function accDiv(arg1,arg2){  var t1=0,t2=0,r1,r2;  try{t1=arg1.toString().split(".")[1].length;}catch(e){};try{t2=arg2.toString().split(".")[1].length;}catch(e){};with(Math){  r1=Number(arg1.toString().replace(".",""));  r2=Number(arg2.toString().replace(".",""));  var rs = (r1/r2)*pow(10,t2-t1);return accRound(rs,3); }  };function accRound(v,e){var t=1;for(;e>0;t*=10,e--);for(;e<0;t/=10,e++);return Math.round(v*t)/t;};Number.prototype.div=function(arg){  return accDiv(this,arg);  };
function strTOJson(str){return eval("("+str+")");};
function json2str(o){var arr = []; 
var fmt = function(s) {
	if (typeof s == 'object' && s != null) 
		return json2str(s); 
	return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s; 
}; if(o instanceof Array)
{for (var i in o){arr.push(fmt(o[i]));}return '[' + arr.join(',') + ']';}else{for (var i in o){arr.push("'" + i + "':" + fmt(o[i]));}return '{' + arr.join(',') + '}';}};
result={};contrast_code='0000001';benchmark='0000001';start='2016-05-01';freq='d';capital_base=100000;codeData={"2016-05-09":{"close":25.63,"period":"2016-05-09"},"2016-05-06":{"close":28.48,"period":"2016-05-06"},"2016-05-04":{"close":26.7,"period":"2016-05-04"},"2016-05-05":{"close":26.75,"period":"2016-05-05"},"2016-05-03":{"close":0,"period":"2016-05-03"},"2016-05-11":{"close":24.17,"period":"2016-05-11","close5":"31.234"},"2016-05-10":{"close":24.44,"period":"2016-05-10","close5":"26.400"}};contrastData={"2016-05-09":{"close":2832.113,"period":"2016-05-09"},"2016-05-06":{"close":2913.248,"period":"2016-05-06"},"2016-05-04":{"close":2991.272,"period":"2016-05-04"},"2016-05-05":{"close":2997.842,"period":"2016-05-05"},"2016-05-03":{"close":2992.643,"period":"2016-05-03"},"2016-05-11":{"close":2837.04,"period":"2016-05-11"},"2016-05-10":{"close":2832.59,"period":"2016-05-10"}};code=600745;end='2016-05-11';out='zrsi';
indicator = new Array();for(var period in codeData){	var data = codeData[period];	
var contrastData = contrastData[period];	
var close = data.close;
var contrastClose = contrastData.close;
var zrsi = Number(Number(close)*100).div(contrastClose);	indicator.push(zrsi);}result.zrsi = json2str(result);