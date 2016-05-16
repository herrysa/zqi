function accAdd(arg1,arg2){  
	var r1,r2,m;  
	try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0};
	try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0};
	m=Math.pow(10,Math.max(r1,r2));  
	return(arg1*m+arg2*m)/m;  
}
Number.prototype.add=function(arg){  
	return accAdd(arg,this);  
};
function accSub(arg1,arg2){  
	var r1,r2,m,n;  
	try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0};
	try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0};
	m=Math.pow(10,Math.max(r1,r2));  
	n=(r1>=r2)?r1:r2;  
	return ((arg2*m-arg1*m)/m).toFixed(n);  
}  
Number.prototype.sub=function(arg){  
	return accSub(arg,this);  
};
function accMul(arg1,arg2)  
{  
	var m=0,s1=arg1.toString(),s2=arg2.toString();  
	try{m+=s1.split(".")[1].length}catch(e){};
	try{m+=s2.split(".")[1].length}catch(e){};
	return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);  
}  
Number.prototype.mul=function(arg){  
	return accMul(arg,this);  
};  
function accDiv(arg1,arg2){  
	var t1=0,t2=0,r1,r2;  
	try{t1=arg1.toString().split(".")[1].length}catch(e){};
	try{t2=arg2.toString().split(".")[1].length}catch(e){};
	with(Math){  
		r1=Number(arg1.toString().replace(".",""));  
		r2=Number(arg2.toString().replace(".",""));  
		var rs = (r1/r2)*pow(10,t2-t1);
		return accRound(rs,3); 
	}  
};
function accRound(v,e){
	var t=1;
	for(;e>0;t*=10,e--);
	for(;e<0;t/=10,e++);
	return Math.round(v*t)/t;
};
Number.prototype.div=function(arg){  
	return accDiv(this,arg);  
};
function strTOJson(str){
	return eval("("+str+")");
};
function json2str(o){
	var arr = [];
 	var fmt = function(s) {
 		if (typeof s == 'object' && s != null) return json2str(s);
 		return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
 	};
 	if(o instanceof Array){
		for (var i in o){
			arr.push(fmt(o[i]));
		}
		return '[' + arr.join(',') + ']';
	}else{
		for (var i in o){
			arr.push("'" + i + "':" + fmt(o[i]));
		}
		return '{' + arr.join(',') + '}';
	}
};
function getPeriodArr(dataList){
	var periodArr = new Array();
	for(var data in dataList){
		if(data.indexOf("-")==-1){
			var row = dataList[data];
			var period = row.period;
			if(period){
				periodArr.push(period);
			}
		}else{
			periodArr.push(data);
		}
	}
	periodArr.sort();
	return periodArr;
}