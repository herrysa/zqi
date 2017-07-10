package com.zqi.strategy.account;

public class AccountTrans implements Cloneable{

	private String tradeCode;
	private String code;
	private String period;
	private double amount;
	private double remainder;
	private double price;
	private double cap;
	private double cost;

	private double cash;
	private double pl;
	
	private String optType;

	private String tradeCode2;
	private Long dateTime;

	private String speriod;
	private double samount;
	private double sprice;
	private double scap;
	private double scost;
	private double wcap;

	private double scash;
	
	
	public String getTradeCode() {
		return tradeCode;
	}
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getRemainder() {
		return remainder;
	}
	public void setRemainder(double remainder) {
		this.remainder = remainder;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getCap() {
		return cap;
	}
	public void setCap(double cap) {
		this.cap = cap;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getPl() {
		return pl;
	}
	public void setPl(double pl) {
		this.pl = pl;
	}
	
	public String getSperiod() {
		return speriod;
	}
	public void setSperiod(String speriod) {
		this.speriod = speriod;
	}
	public double getSamount() {
		return samount;
	}
	public void setSamount(double samount) {
		this.samount = samount;
	}
	public double getSprice() {
		return sprice;
	}
	public void setSprice(double sprice) {
		this.sprice = sprice;
	}
	public double getScost() {
		return scost;
	}
	public void setScost(double scost) {
		this.scost = scost;
	}
	public double getScap() {
		return scap;
	}
	public void setScap(double scap) {
		this.scap = scap;
	}
	public double getWcap() {
		return wcap;
	}
	public void setWcap(double wcap) {
		this.wcap = wcap;
	}
	public double getScash() {
		return scash;
	}
	public void setScash(double scash) {
		this.scash = scash;
	}
	
	public String getTradeCode2() {
		return tradeCode2;
	}
	public void setTradeCode2(String tradeCode2) {
		this.tradeCode2 = tradeCode2;
	}
	
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	
	public Long getDateTime() {
		return dateTime;
	}
	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}
	
	@Override
	public AccountTrans clone() {
		AccountTrans o = null;
		try {
			o = (AccountTrans) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
