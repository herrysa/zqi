account = {
	capital_base : 1000000,
	cash : 0,
	p_cash : 0,
	current_date : null,
	transaction : [],
	position : [],
	blotter : [],
	xData : [],
	strategy : null,
	mcap : [],
	init : function(){
		this.cash = this.capital_base;
	},
	changePosition:function(p){
		position.push(p);
	},
	changeMcap:function(){
		var mcap = cash + p_cash;
		var nowCap = {period:this.current_date,money:changeCash};
		mcap
	},
	getpcash:function(){
		
	},
	order :function(code,amount,price,otype){
		var amountStr = amount.toString();
		if(amountStr.indeOf(".")!=-1){
			amount = amountStr.split(".")[0];
		}
		var amount = Number(amount);
		var orderType ='buy';
		if(amount<0){
			orderType = 'sell';
			amount = -amount;
		}
		var changeCash = amount.mul(price);
		if(orderType=='buy'){
			var remainCash = cash - changeCash;
			if(remainCash<0){
				
			}else{
				var trans = {period:this.current_date,code:code,type:orderType,money:changeCash};
				this.transaction.push(trans);
				this.cash -= changeCash;
				var p = {code:code,amount:amount,pCash:changeCash};
				changePosition(p);
				
				
			}
		}else{
			var trans = {period:this.current_date,code:code,type:orderType,money:changeCash};
			transaction.push(trans);
			cash += changeCash;
		}
	},
	run : function(){
		if(xData){
			for(var i in xData){
				var period = xData[i];
				this.strategy(period);
				changeMcap();
			}
		}
	}
};