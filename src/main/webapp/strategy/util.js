function accDiv(arg1,arg2){  
	var t1=0,t2=0,r1,r2;  
	try{t1=arg1.toString().split(".")[1].length}catch(e){}  
	try{t2=arg2.toString().split(".")[1].length}catch(e){}  
	with(Math){  
		r1=Number(arg1.toString().replace(".",""));  
		r2=Number(arg2.toString().replace(".",""));  
		var rs = (r1/r2)*pow(10,t2-t1);
		return accRound(rs,3); 
	}  
}
function accRound(v,e){
	var t=1;
	for(;e>0;t*=10,e--);
	for(;e<0;t/=10,e++);
	return Math.round(v*t)/t;
}
Number.prototype.div=function(arg){  
	return accDiv(this,arg);  
};
function strTOJson(str){
	return eval("("+str+")");
}
function json2str(o) {
	var arr = [];
 	var fmt = function(s) {
	if (typeof s == 'object' && s != null) return json2str(s);
 		return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
 	}
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
	
}