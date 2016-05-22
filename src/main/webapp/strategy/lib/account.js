account = {
	capital_base : 1000000,
	cash : 0,
	current_date : null,
	transaction : [],
	position : [],
	blotter : [],
	xData : [],
	code : {},
	capital : [],
	assets : [],
	interrupt : 0,
	current_yield_rate : null,
	yield_rate : [],
	init : function(){
		this.cash = this.capital_base;
	},
	changePosition:function(p){
		var positioned = false,positionChanged =false;
		for(var pIndex in this.position){
			var c_p = this.position[pIndex];
			if(c_p.code==p.code){
				c_p.amount += p.amount;
				c_p.cap = c_p.amount.mul(p.price);
				positioned =true;
				positionChanged = true;
				break;
			}
		}
		if(!positioned){
			var amount = p.amount;
			if(amount>0){
				var cap = p.amount.mul(p.price);
				p.cap = cap;
				this.position.push(p);
				positionChanged = true;
			}
		}
		if(positionChanged){
			var changeCash = p.amount.mul(p.price);
			var trans = {period:this.current_date,code:p.code,amount:p.amount,price:p.price,cap:changeCash};
			this.transaction.push(trans);
			this.cash -= changeCash;
		}
		
	},
	getPosition:function(code){
		var amount = 0;
		for(var pIndex in this.position){
			var c_p = this.position[pIndex];
			if(c_p.code==code){
				amount = c_p.amount;
				break;
			}
		}
		return amount;
	},
	balance:function(){
		var sum = this.cash;
		sum = Number(sum);
		for(var pIndex in this.position){
			var c_p = this.position[pIndex];
			var price = this.code.data[this.current_date].close;
			var cap = c_p.amount.mul(price);
			sum = sum.add(cap);
		}
		this.assets.push(sum);
		var yield_rate = sum.sub(this.capital_base).div(this.capital_base).mul(100);
		this.current_yield_rate = yield_rate;
		this.yield_rate.push(yield_rate);
	},
	order :function(code,amount,price,otype){
		var amountStr = amount.toString();
		if(amountStr.indexOf(".")!=-1){
			amount = amountStr.split(".")[0];
		}
		var amount = Number(amount);
		if(amount==0){
			return ;
		}
		var changeCash = amount.mul(price);
		var remainCash = this.cash - changeCash;
		if(remainCash>=0){
			var p = {code:code,amount:amount,price:price};
			this.changePosition(p);
		}else{
			
		}
	},
	run : function(){
		if(this.xData){
			for(var i in this.xData){
				if(this.interrupt>0){
					this.interrupt --;
					continue;
				}
				var period = this.xData[i];
				this.current_date = period;
				this.strategy(period,i);
				this.balance();
				if(!this.code.closeHigh){
					var codeData = this.code.data;
					if(codeData){
						var d = codeData[period];
						this.code.closeHigh = d.close; 
					}
				}else{
					var codeData = this.code.data;
					if(codeData){
						var d = codeData[period];
						if(this.code.closeHigh < d.close){
							this.code.closeHigh = d.close; 
						}							
					}
				}
			}
		}
	}
};