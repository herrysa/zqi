//name:羊驼策略

step = 1;
refresh_rate = 1;
fq = 1;
benchmark = '0000300';

xData = '';
codeData = Data.getGPData({hasData:'0',gpNum:"-1"});

accountContext = {total:0,suc:0,fail:0};

result = {};
out = {sig:'table',accountContext:'json'};
wholeOut = {avgSignal:'table'};
//init
start2 = '2016-01-01';
end2 = '2016-09-01';
codeData2 = Data.getGPData({code:xData,col:"code,close_p,@avg({close_p:[5,10,20,60]})",start:start2,end:end2});
//local

for(var p in codeData2){
		var data = codeData2[p];
		var pcode = data.code;
		var close = data.close_p;
		var avg5 = data.close_p5;
		var avg10 = data.close_p10;
		var pContext = accountContext[pcode];
		if(!pContext){
			accountContext[pcode] = {};
			accountContext[pcode].day = 0;
		}else{
			var b = accountContext[pcode].b;
			if(b == 1){
				accountContext[pcode].day++;
				var initClose = accountContext[pcode].close;
				var yield = Number(close).sub(initClose).div(initClose).mul(100);
				if(yield>=20){
					if(!accountContext.suc){
						accountContext.suc = 0;
					}else{
						accountContext.suc++;
					}
					if(!accountContext.total){
						accountContext.total = 0;
					}else{
						accountContext.total++;
					}
					accountContext[pcode].b = 0;
				}else if(yield<=-20){
					if(!accountContext.fail){
						accountContext.fail = 0;
					}else{
						accountContext.suc++;
					}
					if(!accountContext.total){
						accountContext.total = 0;
					}else{
						accountContext.total++;
					}
					accountContext[pcode].b = 0;
				}
			}
		}
		if(avg5>avg10){
			var b = accountContext[pcode].b;
			if(!b){
				accountContext[pcode].b = 1;
				accountContext[pcode].close = close;
			}
		}
	
}

result.accountContext = accountContext;
result.avgSignal = {total:accountContext.total,suc:accountContext.suc,fail:accountContext.fail};
result = json2str(result);