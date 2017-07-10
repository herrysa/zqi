package com.zqi.strategy.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.zqi.frame.util.DecimalUtil;
import com.zqi.primaryData.fileDataBase.FileDataBase;
import com.zqi.strategy.HQFinder;
import com.zqi.strategy.QuantContext;
import com.zqi.unit.DateUtil;
import com.zqi.unit.UUIDGenerator;

public class Account {
	
	private String accountCode;
	private String snapCode ;
	private String quantCode;
	private String quantName;
	private StringBuilder jdgTemp;
	private StringBuilder profitTemp;
	private StringBuilder markProfitTemp;
	
	private HQFinder hqFinder;
	private QuantContext quantContext;
	
	private double capital_base = 100000000d;
	private double cash = 0d;
	private String current_date;
	private Date now;
	private String start;
	private String end;
	private String benchmark;
	private String benchmarkName;
	
	//private boolean allSignal = false;
	private double commission = 0.001;
	//private boolean needBalance = false;
	//private boolean needPersistence = true;
	
	private Map<String,List<AccountTrans>> transaction;
	private Map<String,List<AccountPosition>> positionMap;
	private Map<String,Double> cashMap;
	private List<AccountTrans> transactionList;
	private List<AccountPosition> positionList;
	private Map<String,AccountPosition> position;//持仓
	
	private Double positionValue ;
	//private Map<String, BigDecimal> signalMap;
	private Map<String, String> wholeIndexMap;
	//private List<Map<String, Object>> blotter;
	//private List<Map<String, Object>> capital;
	//private List<Map<String, Object>> assets;
	//private int interrupt = 0;
	//private BigDecimal current_yield_rate;
	private List<Double> yieldRates;
	private List<Double> markYieldRates;
	private List<String> xData;
	
	//balance变量
	private Double markBase ;
	private Double mark_yield_rate = 0d;
	private Double yield_rate = 0d;
	//private double cashTemp ;
	//private double capTemp ;
	//private List<AccountPosition> positionListTemp ;
	//private Map<String, AccountTrans> transactionTemp;
	
	//private HQDataBox dataBox;
	//private Map<String,Object> markData;
	
	public Account(){
		init();
	}
	
	public Account(HQFinder hqFinder){
		this.hqFinder = hqFinder;
		init();
	}
	
	public Account(Long capital,HQFinder hqFinder){
		capital_base = capital;
		this.hqFinder = hqFinder;
		init();
	}
	
	public void init(){
		this.snapCode = DateUtil.getSnapCode();
		this.cash = this.capital_base;
		this.xData = new ArrayList<String>();
		this.transaction = new HashMap<String,List<AccountTrans>>();
		this.position = new HashMap<String, AccountPosition>();
		this.wholeIndexMap = new HashMap<String, String>();
		this.yieldRates = new ArrayList<Double>();
		this.markYieldRates = new ArrayList<Double>();
		this.positionMap = new HashMap<String, List<AccountPosition>>();
		this.cashMap = new HashMap<String, Double>();
		
		this.transactionList = new ArrayList<AccountTrans>();
		this.positionList = new ArrayList<AccountPosition>();
		
		this.quantContext = new QuantContext();
		
		this.markBase = null;
		//cashTemp = this.capital_base;
		//capTemp = 0d;
		//positionListTemp = new ArrayList<AccountPosition>();
		//transactionTemp = new HashMap<String, AccountTrans>();
		
		this.jdgTemp = new StringBuilder();
		this.profitTemp = new StringBuilder();
		this.markProfitTemp = new StringBuilder();
		FileDataBase jgdDb = new FileDataBase("temp/jgd");
		jgdDb.deleteDataBase();
	}
	
	public void prepare(){
		if(!StringUtils.isEmpty(accountCode)){
			Map<String, Object> account = this.hqFinder.getZqiDao().findFirst("select * from ac_account where accountCode='"+accountCode+"'");
			BigDecimal balance = (BigDecimal)account.get("balance");
			if(balance!=null){
				this.cash = balance.doubleValue();
			}
		}
		/*if(needPersistence){
		}*/
	}
	
	public void beforeTrading(){
		List<AccountTrans> dayTrans = new ArrayList<AccountTrans>();
		this.transaction.put(this.current_date,dayTrans);
		
		Set<String> codes = this.position.keySet();
		Set<String> codeSet = new HashSet<String>();
		codeSet.addAll(codes);
		for(String code : codeSet){
			AccountPosition accountPosition = this.position.get(code);
			if(accountPosition.isShouldSell()){
				Map<String, Object> data = hqFinder.getRHQData(code, this.current_date);
				if(data!=null){
					BigDecimal open = (BigDecimal)data.get("open");
					boolean suc = this.order(this.current_date, code, -1, open.doubleValue(), "");
					if(!suc){
						BigDecimal close = (BigDecimal)data.get("close");
						suc = this.order(this.current_date, code, -1, close.doubleValue(), "");
					}
				}
			}
		}
		//getPositionValue();
	}
	
	public void afterTrading(){
		balance();
		/*if(needBalance){
		}*/
	}
	
	public void updatePosition(List<Map<String, Object>> dataList){
		for(Map<String, Object> daydata : dataList){
			String code = daydata.get("code").toString();
			AccountPosition posi = this.position.get(code);
			if(posi==null){
				continue;
			}
			String posiKey = posi.getCode();
			double amount = posi.getAmount();
			Double close = (Double)daydata.get("close");
			if(close==null||close.doubleValue()==0){
				close = (Double)daydata.get("settlement");
			}
			String isFh = daydata.get("isFh").toString();
			if("1".equals(isFh)){
				BigDecimal sg = hqFinder.getSgData(posiKey, current_date);
				BigDecimal zz = hqFinder.getZzData(posiKey, current_date);
				double s = 0;
				if(sg!=null){
					s += sg.doubleValue();
				}
				if(zz!=null){
					s += zz.doubleValue();
				}
				s = DecimalUtil.scale(s);
				if(s>0){
					//double amount = posi.getAmount();
					double fhRate = DecimalUtil.scale(1+s/10);
					posi.setFhRate(fhRate);
					amount = amount*fhRate;
					posi.setAmount(amount);
					if("601567".equals(code)){
						System.out.println();
					}
					//System.out.println("fffff"+code+":"+current_date);
				}
			}
			posi.setClose(close.doubleValue());
		}
	}
	
	/**计算买入费用
	 * @param cap
	 * @param code
	 * @return
	 */
	private double getBuyCost(double cap , String code){
		double c = DecimalUtil.scale(cap*commission);//佣金
		double g = 0d;		//过户费
		if(code.startsWith("6")){
			g = DecimalUtil.scale(cap*0.00002d);
		}
		return DecimalUtil.scale(c+g);
	}
	
	/**计算卖出费用
	 * @param cap
	 * @param code
	 * @return
	 */
	private double getSellCost(double cap , String code){
		cap = -cap;
		double y = DecimalUtil.scale(cap*0.001);//印花税
		double c = DecimalUtil.scale(cap*commission);//佣金
		double g = 0d;		//过户费
		if(code.startsWith("6")){
			g = DecimalUtil.scale(cap*0.00002d);
		}
		return DecimalUtil.scale(y+c+g);
	}
	
	/**买入订单处理
	 * @param accountTrans
	 * @return
	 */
	private boolean buy(AccountTrans accountTrans){
		String period = accountTrans.getPeriod();
		String code = accountTrans.getCode();
		double amount = accountTrans.getAmount();
		double price = accountTrans.getPrice();
		double realAmount = 0 ;
		//double positionValue = getPositionValue();
		String date = null;
		if(period.length()>10){
			date = period.split(" ")[0];
		}else{
			date = period ; 
		}
		
		if(price==0){
			return false;
		}
		//positionValue为上一日结束后市值
		double needMoney = DecimalUtil.scale((positionValue+this.cash) * amount, 2, BigDecimal.ROUND_DOWN);
		price += 0.01;
		if(needMoney>this.cash){
			return false;
		}else{
			realAmount = DecimalUtil.scale(needMoney/price, 0, BigDecimal.ROUND_DOWN);
		}
		if(realAmount==0){
			return false;
		}
		
		Map<String, Object> daydata = hqFinder.getRHQData(code, date);
		if(daydata==null){
			return false;
		}
		//停牌
		BigDecimal close = (BigDecimal)daydata.get("close");
		if(close.compareTo(new BigDecimal(0))==0){
			return false;
		}
		//涨停
		BigDecimal changepercent = (BigDecimal)daydata.get("changepercent");
		if(changepercent.compareTo(new BigDecimal(10))>=0){
			return false;
		}else{
			BigDecimal settlement = (BigDecimal)daydata.get("settlement");
			double s = DecimalUtil.scale(settlement.doubleValue()*1.1);
			if(close.doubleValue()>=s){
				return false;
			}
		}
		double cap =  DecimalUtil.scale(realAmount*price); //交易金额
		accountTrans.setCap(cap);
		accountTrans.setAmount(realAmount);
		double cost = getBuyCost(cap, code);
		accountTrans.setCost(cost);
		//处理持仓
		this.cash = DecimalUtil.scale(this.cash - cap - cost);
		//
		AccountPosition accountPosition = position.get(code);
		if(accountPosition==null){//开仓
			accountPosition = new AccountPosition();
			//accountPosition.setPeriod(period);
			accountPosition.setCode(code);
			accountPosition.setAmount(realAmount);
			accountPosition.setAvgPrice(price);
			//accountPosition.setYield(0d);
			//accountPosition.setNowCap(cap);
			accountPosition.setMoney(cap);
			accountPosition.setCost(cost);
			accountPosition.setCash(this.cash);
			this.position.put(code,accountPosition);
			
			accountTrans.setRemainder(realAmount);
			
		}else{//加仓
			double posiAmount = accountPosition.getAmount();
			posiAmount = DecimalUtil.scale(posiAmount+realAmount, 0, BigDecimal.ROUND_DOWN);
			accountPosition.setAmount(posiAmount);
			double posiMoney = accountPosition.getMoney();
			accountPosition.setMoney(DecimalUtil.scale(cap+posiMoney));
			double posiCost = accountPosition.getCost();
			accountPosition.setCost(DecimalUtil.scale(cost+posiCost));
			accountPosition.setCash(this.cash);
			
			accountTrans.setRemainder(posiAmount);
		}
		accountPosition.setClose(price);
		Map<String, Object> transMap = new HashMap<String, Object>();
		transMap.put("tradeCode",accountTrans.getTradeCode());
		transMap.put("amount",realAmount);
		accountPosition.addTrans(transMap);
		accountTrans.setWcap(DecimalUtil.scale(positionValue + cap));
		accountTrans.setCash(this.cash);
		accountTrans.setTradeCode2("0");
		List<AccountTrans> dayTrans = this.transaction.get(date);
		dayTrans.add(accountTrans);
		return true;
	}
	
	/**卖出订单处理
	 * @param accountTrans
	 * @return
	 */
	private boolean sell(AccountTrans accountTrans){
		String period = accountTrans.getPeriod();
		String code = accountTrans.getCode();
		double amount = accountTrans.getAmount();
		double price = accountTrans.getPrice();
		double realAmount = 0 ;
		AccountPosition accountPosition = position.get(code);
		if(accountPosition==null){
			return false;
		}
		if("601567".equals(code)){
			System.out.println();
		}
		accountPosition.setShouldSell(true);
		String date = null;
		if(period.length()>=10){
			date = period.split(" ")[0];
		}else{
			date = period ; 
		}
		Map<String, Object> daydata = hqFinder.getRHQData(code, date);
		if(daydata==null){
			return false;
		}
		//停牌
		BigDecimal close = (BigDecimal)daydata.get("close");
		if(close.compareTo(new BigDecimal(0))==0){
			return false;
		}
		//跌停
		BigDecimal changepercent = (BigDecimal)daydata.get("changepercent");
		if(changepercent.compareTo(new BigDecimal(-10))<=0){
			return false;
		}else{
			BigDecimal settlement = (BigDecimal)daydata.get("settlement");
			double s = DecimalUtil.scale(settlement.doubleValue()*-1.1);
			if(close.doubleValue()<=s){
				return false;
			}
		}
		if(amount<-1){//小于-1 错误数据
			return false;
		}
		if(price==0){
			return false;
		}
		price -= 0.01;
		//实际卖出量
		while(!accountPosition.isEmpty()){
			Map<String, Object> transMap = accountPosition.popTrans();
			Double buyAmount = (Double)transMap.get("amount");
			Double posiAmount = buyAmount*accountPosition.getFhRate();
			realAmount = DecimalUtil.scale(posiAmount * amount, 0, BigDecimal.ROUND_DOWN);
			if(realAmount==0){
				return false;
			}
			double cap = DecimalUtil.scale(realAmount*price);//交易金额
			double cost = getSellCost(cap, code);
			this.cash = DecimalUtil.scale(this.cash - cap - cost);
			accountTrans.setAmount(realAmount);
			accountTrans.setCap(cap);
			accountTrans.setCost(cost);
			posiAmount = DecimalUtil.scale(posiAmount+realAmount, 0, BigDecimal.ROUND_DOWN);//修改持仓量
			if(posiAmount==0){
				this.position.remove(code);
				accountTrans.setRemainder(0);
			}else{
				accountPosition.setAmount(posiAmount);
				double posiMoney = accountPosition.getMoney();
				accountPosition.setMoney(DecimalUtil.scale(posiMoney+cap));
				double posiCost = accountPosition.getCost();
				accountPosition.setCost(DecimalUtil.scale(posiCost+cost));
				accountPosition.setCash(this.cash);
				accountPosition.setClose(price);
				
				accountTrans.setRemainder(posiAmount);
			}
			//transMap.put("tradeCode",accountTrans.getTradeCode());
			//transMap.put("amount",realAmount);
			//accountPosition.addTrans(transMap);
			accountTrans.setWcap(DecimalUtil.scale(positionValue + cap));
			accountTrans.setCash(this.cash);
			String tradeCode2 = transMap.get("tradeCode").toString();
			AccountTrans accountTransTemp = accountTrans.clone();
			accountTransTemp.setTradeCode(UUIDGenerator.getInstance().getNextValue());
			accountTransTemp.setTradeCode2(tradeCode2);
			List<AccountTrans> dayTrans = this.transaction.get(date);
			dayTrans.add(accountTransTemp);
		}
		return true;
	}
	
	/**根据调仓修改持仓 
	 * @param accountTrans
	 */
	public boolean changePosition(AccountTrans accountTrans){
		String period = accountTrans.getPeriod();
		double pAmount = accountTrans.getAmount();
		boolean sucess = false;
		String tradeCode = UUIDGenerator.getInstance().getNextValue();
		accountTrans.setTradeCode(tradeCode);
		if(pAmount>0){
			sucess = buy(accountTrans);
		}else if(pAmount==0){
			return false;
		}else{
			sucess = sell(accountTrans);
		}
		if(!sucess){
			return sucess;
		}
		String date = null;
		if(period.length()>10){
			date = period.split(" ")[0];
		}else{
			date = period;
		}
		List<AccountPosition> positionList = new ArrayList<AccountPosition>();
		Set<String> codes = this.position.keySet();
		for( String code : codes){
			AccountPosition posi = this.position.get(code);
			positionList.add(posi.clone());
		}
		positionMap.put(date, positionList);
		cashMap.put(date, this.cash);
		return sucess;
	}
	
	
	/**
	 * 计算持仓市值
	 */
	public void getPositionValue() {
		positionValue = 0d;
		Set<String> codes = position.keySet();
		if(codes.size()<5000){
			for(String code : codes){
				AccountPosition posi = position.get(code);
				//String posiKey = posi.getCode();
				double amount = posi.getAmount();
				double close = posi.getClose();
				//Map<String, Object> daydata = hqFinder.getRHQData(posiKey, current_date);
				double nowCap = DecimalUtil.scale(close*amount);
				positionValue += nowCap;
				/*if(daydata==null){
					
				}else{
					BigDecimal close = (BigDecimal)daydata.get("close");
					if(close!=null){
						if(close.compareTo(new BigDecimal(0))==0){
							close = (BigDecimal)daydata.get("settlement");
						}
						double nowCap = DecimalUtil.scale(close.doubleValue()*amount);
						positionValue += nowCap;
					}
				}*/
			}
		}else{
			StringBuilder codeBuilder = new StringBuilder();
			for(String code : codes){
				codeBuilder.append("'"+code+"',");
			}
			String codeStr = "("+codeBuilder.substring(0, codeBuilder.length()-1)+")";
			String year = current_date.split("-")[0];
			List<Map<String, Object>> dataList = hqFinder.getGpHq(year, codeStr, current_date,new HashMap<String, String>());
			for(Map<String, Object> data : dataList){
				String code = data.get("code").toString();
				AccountPosition posi = position.get(code);
				String posiKey = posi.getCode();
				double amount = posi.getAmount();
				String isFh = data.get("isFh").toString();
				if("1".equals(isFh)){
					BigDecimal sg = hqFinder.getSgData(posiKey, current_date);
					BigDecimal zz = hqFinder.getZzData(posiKey, current_date);
					double s = 0;
					if(sg!=null){
						s += sg.doubleValue();
					}
					if(zz!=null){
						s += zz.doubleValue();
					}
					s = DecimalUtil.scale(s);
					if(s>0){
						//double amount = posi.getAmount();
						double fhRate = DecimalUtil.scale(1+s/10);
						posi.setFhRate(fhRate);
						amount = amount*fhRate;
						posi.setAmount(amount);
						//System.out.println("fffff"+code+":"+current_date);
					}
				}
				BigDecimal close = (BigDecimal)data.get("close");
				if(close!=null){
					if(close.compareTo(new BigDecimal(0))==0){
						close = (BigDecimal)data.get("settlement");
					}
					double nowCap = DecimalUtil.scale(close.doubleValue()*amount);
					positionValue += nowCap;
				}
			}
		}
		
		positionValue = DecimalUtil.scale(positionValue);
	}
	
	/**获取持仓
	 * @return
	 */
	public List<AccountPosition> getPosition() {
		Set<String> codes = position.keySet();
		for(String code : codes){
			AccountPosition posi = position.get(code);
			String posiKey = posi.getCode();
			Map<String, Object> daydata = hqFinder.getRHQData(posiKey, current_date);
			if(daydata==null){

			}else{
				BigDecimal close = (BigDecimal)daydata.get("close");
				if(close!=null){
					if(close.compareTo(new BigDecimal(0))==0){
						close = (BigDecimal)daydata.get("settlement");
					}
					posi.setClose(close.doubleValue());
					//posi.getYield();
				}
			}
		}
		List<AccountPosition> positions = new ArrayList<AccountPosition>();
		positions.addAll(position.values());
		//Tools.sort(positions, "yield", false);
		return positions;
	}
	
	public AccountPosition getPosition(String code) {
		return position.get(code);
	}

	
	public void balance(){
		String period = this.current_date;
		Map<String, Object> markData = hqFinder.getRHQData(benchmark, period);//dataBox.getRHQData(period);
		if(markData==null){
			return ;
		}
		
		BigDecimal markClose = (BigDecimal)markData.get("close");
		if(markClose==null){
			return ;
		}
		xData.add(period);
		
		//Map<String, Object> trans = null;
		/*if(dayTrans!=null){
			for(AccountTrans transTemp : dayTrans){
				//trans = transTemp;
				String code = transTemp.getCode();
				AccountTrans trans = transactionTemp.get(code);
				if(trans==null){
					transactionTemp.put(code, transTemp);
				}else{
					String speriod = transTemp.getPeriod();
					double samount = transTemp.getAmount();
					if(samount>0){
						
					}else{
						
					}
					double sprice = transTemp.getPrice();
					double scash = transTemp.getCash();
					double scap = transTemp.getCap();
					double scost = transTemp.getCost();
					trans.setSperiod(speriod);
					trans.setSamount(samount);
					trans.setSprice(sprice);
					trans.setScash(scash);
					trans.setScap(scap);
					trans.setScost(scost);
					double price = trans.getPrice();
					try {
						double pl = DecimalUtil.percent(-(sprice*samount-scost), price*trans.getAmount()+trans.getCost());
						//BigDecimal pl = sprice.subtract(price).divide(price,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
						trans.setPl(pl);
					} catch (Exception e) {
						e.printStackTrace();
					}
					transactionList.add(trans);
					transactionTemp.remove(code);
				}
			}
		}*/
		//Double cash = cashMap.get(period);
		
		//List<AccountPosition> positionList = positionMap.get(period);
		/*if(cash==null){
			cash = cashTemp;
		}else{
			cashTemp = cash;
		}*/
		/*if(positionList==null){
			positionList = positionListTemp;
		}else{
			positionListTemp = positionList;
		}*/
		/*for(AccountPosition posi : positionList){
			String posiKey = posi.getCode();
			double amount = posi.getAmount();
			Map<String, Object> daydata = hqFinder.getRHQData(posiKey, period);
			if(daydata==null){
			}else{
			//TODO
			BigDecimal close = (BigDecimal)daydata.get("close");
			if(close==null){
				close = new BigDecimal(posi.getBuyPrice());
			}
			if(close.compareTo(new BigDecimal(0))==0){
				close = (BigDecimal)daydata.get("settlement");
			}
			
		
			//amount = amount.multiply(close);
			cap += amount*close.doubleValue();
			AccountPosition posiTemp = new AccountPosition();
			posiTemp = posi.clone();
			posiTemp.setPeriod(period);
			posiTemp.setCash(cash);
			posiTemp.setClose(close.doubleValue());
			//posiTemp.setNowCap(cap);
			this.positionList.add(posiTemp);
			}
		}*/
		/*if(positionList.isEmpty()){
			AccountPosition posiTemp = new AccountPosition();
			posiTemp.setPeriod(period);
			posiTemp.setCash(cash);
			this.positionList.add(posiTemp);
		}*/
		//capTemp = cap;
		getPositionValue();
		double totalCap = this.positionValue + this.cash;
		if(markBase==null){
			markBase = markClose.doubleValue();
			markYieldRates.add(mark_yield_rate);
			yieldRates.add(yield_rate);
			
			if(StringUtils.isEmpty(accountCode)){
				String accountSql = "insert into ac_account (accountCode,snapCode,baseCapital,startPeriod,endPeriod,benchmark,benchmarkName,markBase,quantCode) values ('"+quantName+snapCode+"','"+snapCode+"','"+capital_base+"','"+start+"','"+end+"','"+benchmark+"','"+benchmarkName+"','"+markBase+"','"+quantCode+"')";
				hqFinder.getZqiDao().excute(accountSql);
				accountCode = quantName+snapCode;
			}else{
				snapCode = accountCode.substring(accountCode.length()-14,accountCode.length());
			}
			
			String profit = accountCode+"	"+period+"	"+this.cash+"	"+this.positionValue+"	"+yield_rate+"	"+this.position.size()+"\n";
			String markProfit = this.benchmark+snapCode+"	"+period+"	"+markClose.doubleValue()+"	"+mark_yield_rate+"\n";
			profitTemp.append(profit);
			markProfitTemp.append(markProfit);
		}else{
			mark_yield_rate = DecimalUtil.percent(markClose.doubleValue(), markBase);
			//BigDecimal mark_yield_rate = markClose.subtract(markBase).divide(markBase,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			markYieldRates.add(mark_yield_rate);
			yield_rate = DecimalUtil.percent(totalCap, capital_base);
			//BigDecimal yield_rate = cash.subtract(capital_base).divide(capital_base,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			yieldRates.add(yield_rate);
			
			String profit = accountCode+"	"+period+"	"+this.cash+"	"+this.positionValue+"	"+yield_rate+"	"+this.position.size()+"\n";
			String markProfit = this.benchmark+snapCode+"	"+period+"	"+markClose.doubleValue()+"	"+mark_yield_rate+"\n";
			profitTemp.append(profit);
			markProfitTemp.append(markProfit);
		}
		
		List<AccountTrans> dayTrans = transaction.get(period);
		//FileDataBase jgdDb = new FileDataBase("temp/jgd");
		//jgdDb.deleteDataBase();
		if(dayTrans!=null){
			//List<String> sqlList = new ArrayList<String>();
			//String cols = "tradeCode,tradeCode2,businessType,period,code,price,amount,remainder,money,cash,cost,transCode";
			for(AccountTrans accountTrans : dayTrans){
				Long dateTime = accountTrans.getDateTime();
				//String sql = null;
				//String cols = "(tradeCode,businessType,period,code,price,amount,remainder,money,cash,cost,transCode)";
				String values = null;
				String tradeCode = accountTrans.getTradeCode();
				String tradeCode2 = accountTrans.getTradeCode2();
				String code = accountTrans.getCode();
				String tperiod = accountTrans.getPeriod();
				double price = accountTrans.getPrice();
				double amount = accountTrans.getAmount();
				double remainder = accountTrans.getRemainder();
				double cap = accountTrans.getCap();
				double cost = accountTrans.getCost();
				double cash = accountTrans.getCash();
				values = tradeCode+"	"+tradeCode2+"	"+dateTime+"	0	"+tperiod+"	"+code+"	"+price+"	"+amount+"	"+remainder+"	"+cap+"	"+cash+"	"+cost+"	"+accountCode+"\n";
				
				jdgTemp.append(values);
				
				//sql = "insert into i_jgd "+cols+" values "+values;
				//sqlList.add(sql);
			}
			//String[] sqls = new String[sqlList.size()];
			//sqls = sqlList.toArray(sqls);
			//this.hqFinder.getZqiDao().bathUpdate(sqls);
			//String loadDataSql = "load data infile '"+jgdDb.getFilePath("jgd.txt")+"' into table i_gpFh("+cols+",txt"+");";
			//this.hqFinder.getZqiDao().excute(loadDataSql);
			//transactionList.addAll(dayTrans);
		}
	}
	
	String wholeIndexTxt = "";
	public String getWholeIndexTxt() {
		return wholeIndexTxt;
	}

	public void setWholeIndexTxt(String wholeIndexTxt) {
		this.wholeIndexTxt = wholeIndexTxt;
	}

	public void balanceAfter(){
		//年华收益率
		//double a = (cashTemp + capTemp)/capital_base;
		//double b = 250d/xData.size();
		//double yearYield = (Math.pow(a,b)-1)*100;
		//yearYield = DecimalUtil.scale(yearYield, 3);
		//BigDecimal yearYield = new BigDecimal(Math.pow(cashTemp.add(capTemp).divide(capital_base).doubleValue(), new BigDecimal(250).divide(new BigDecimal(xData.size()),10,BigDecimal.ROUND_HALF_DOWN).doubleValue())-1).multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_HALF_UP);
		//wholeIndexMap.put("年化收益率", ""+yearYield);
		//wholeIndexTxt += "年化收益率:"+yearYield+"&nbsp&nbsp";
		
		double transNum = transactionList.size();
		wholeIndexTxt += "调仓次数:"+transNum+"&nbsp&nbsp";
		double sucNum = 0;
		double costSum = 0;
		for(AccountTrans trans : transactionList){
			double pl = trans.getPl();
			if(pl>0){
				sucNum++;
			}
			costSum += trans.getCost();
		}
		//wholeIndexTxt += "调仓正收益次数:"+sucNum+"&nbsp&nbsp";
		/*double sp = 0;
		if(transNum!=0){
			sp = sucNum/transNum*100d;
		}*/
		//BigDecimal sucPercent = new BigDecimal(sp).setScale(2, BigDecimal.ROUND_HALF_UP);
		//wholeIndexTxt += "调仓正收益率:"+sucPercent+"%&nbsp&nbsp";
		wholeIndexTxt += "交易费合计:"+DecimalUtil.scale(costSum)+"&nbsp&nbsp";
		
		FileDataBase jgdDb = new FileDataBase("temp/jgd");
		jgdDb.deleteDataBase();
		jgdDb.writeStr("jgd", jdgTemp.toString(), 0);
		String cols = "tradeCode,tradeCode2,dateTime,businessType,period,code,price,amount,remainder,money,cash,cost,accountCode";
		String loadDataSql = "load data infile '"+jgdDb.getFilePath("jgd")+"' into table ac_jgd("+cols+");";
		this.hqFinder.getZqiDao().excute(loadDataSql);
		
		FileDataBase profitDb = new FileDataBase("temp/profit");
		profitDb.deleteDataBase();
		profitDb.writeStr("profit", profitTemp.toString(), 0);
		String profitCols = "accountCode,period,cash,cap,profit,psize";
		String loadProfitDataSql = "load data infile '"+profitDb.getFilePath("profit")+"' into table ac_profit("+profitCols+");";
		this.hqFinder.getZqiDao().excute(loadProfitDataSql);
		
		FileDataBase markProfitDb = new FileDataBase("temp/markProfit");
		markProfitDb.deleteDataBase();
		markProfitDb.writeStr("markProfit", markProfitTemp.toString(), 0);
		String markProfitCols = "accountCode,period,cash,profit";
		String loadmarkProfitDataSql = "load data infile '"+markProfitDb.getFilePath("markProfit")+"' into table ac_profit("+markProfitCols+");";
		this.hqFinder.getZqiDao().excute(loadmarkProfitDataSql);
		
		this.hqFinder.getZqiDao().excute("update ac_account set balance='"+(this.positionValue + this.cash)+"' where accountCode='"+accountCode+"'");
		
	}
	
	/**下单
	 * @param period 下单时间
	 * @param code  
	 * @param amount
	 * @param price
	 * @param otype
	 */
	public boolean order(String period , String code,double amount,double price,String optype){
		if(amount==0){
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		AccountTrans accountTrans = new AccountTrans();
		accountTrans.setDateTime(calendar.getTimeInMillis());
		accountTrans.setCode(code);
		accountTrans.setPeriod(period);
		accountTrans.setAmount(amount);
		accountTrans.setPrice(price);
		accountTrans.setOptType(optype);
		return this.changePosition(accountTrans);
	}
	
	public Map<String, List<AccountTrans>> getTransaction() {
		return transaction;
	}

	public void setTransaction(Map<String, List<AccountTrans>> transaction) {
		this.transaction = transaction;
	}

	public List<Double> getYieldRates() {
		return yieldRates;
	}

	public void setYieldRates(List<Double> yieldRates) {
		this.yieldRates = yieldRates;
	}

	public List<Double> getMarkYieldRates() {
		return markYieldRates;
	}

	public void setMarkYieldRates(List<Double> markYieldRates) {
		this.markYieldRates = markYieldRates;
	}
	
	/*public Map<String, Object> getMarkData() {
		return markData;
	}

	public void setMarkData(Map<String, Object> markData) {
		this.markData = markData;
	}*/
	/*public HQDataBox getDataBox() {
		return dataBox;
	}

	public void setDataBox(HQDataBox dataBox) {
		this.dataBox = dataBox;
	}*/
	
	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}
	
	public List<String> getxData() {
		return xData;
	}

	public void setxData(List<String> xData) {
		this.xData = xData;
	}
	public List<AccountTrans> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<AccountTrans> transactionList) {
		this.transactionList = transactionList;
	}
	public String getCurrent_date() {
		return current_date;
	}

	public void setCurrent_date(String current_date) {
		this.current_date = current_date;
	}
	
	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
		this.current_date = DateUtil.convertDateToString(now);
	}
	
	public String getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(String benchmark) {
		this.benchmark = benchmark;
		this.benchmarkName = hqFinder.getGpDic(benchmark).get("name").toString();
	}
	
	public String getBenchmarkName() {
		return benchmarkName;
	}

	public void setBenchmarkName(String benchmarkName) {
		this.benchmarkName = benchmarkName;
	}
	
	public List<AccountPosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<AccountPosition> positionList) {
		this.positionList = positionList;
	}
	
	public Map<String, String> getWholeIndexMap() {
		return wholeIndexMap;
	}

	public void setWholeIndexMap(Map<String, String> wholeIndexMap) {
		this.wholeIndexMap = wholeIndexMap;
	}
	
	/*public boolean isNeedBalance() {
		return needBalance;
	}

	public void setNeedBalance(boolean needBalance) {
		this.needBalance = needBalance;
	}*/
	
	public String getQuantName() {
		return quantName;
	}

	public void setQuantName(String quantName) {
		this.quantName = quantName;
	}
	
	public double getCapital_base() {
		return capital_base;
	}

	public void setCapital_base(double capital_base) {
		this.capital_base = capital_base;
	}
	
	/*public boolean isNeedPersistence() {
		return needPersistence;
	}

	public void setNeedPersistence(boolean needPersistence) {
		this.needPersistence = needPersistence;
	}*/
	
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getQuantCode() {
		return quantCode;
	}

	public void setQuantCode(String quantCode) {
		this.quantCode = quantCode;
	}
	
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	
	public QuantContext getQuantContext() {
		return quantContext;
	}

	public void setQuantContext(QuantContext quantContext) {
		this.quantContext = quantContext;
	}
	
	public static void main(String[] args) {
		//int aa = 267;
		//double sp = Math.pow(0.003 ,0.9);
		double aaa = 0.0d;
		System.out.println(aaa==0);
	}
}
