package com.zqi.strategy.model;

import com.zqi.frame.model.BaseObject;

public class HQRData extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6772784789967634297L;
	private String code;
	private String period;
	private String name;
	private Double settlement;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;
	private Double amount;
	private Double changeprice;
	private Double changepercent;
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getSettlement() {
		return settlement;
	}
	public void setSettlement(Double settlement) {
		this.settlement = settlement;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getChangeprice() {
		return changeprice;
	}
	public void setChangeprice(Double changeprice) {
		this.changeprice = changeprice;
	}
	public Double getChangepercent() {
		return changepercent;
	}
	public void setChangepercent(Double changepercent) {
		this.changepercent = changepercent;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((changepercent == null) ? 0 : changepercent.hashCode());
		result = prime * result
				+ ((changeprice == null) ? 0 : changeprice.hashCode());
		result = prime * result + ((close == null) ? 0 : close.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result
				+ ((settlement == null) ? 0 : settlement.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HQRData other = (HQRData) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (changepercent == null) {
			if (other.changepercent != null)
				return false;
		} else if (!changepercent.equals(other.changepercent))
			return false;
		if (changeprice == null) {
			if (other.changeprice != null)
				return false;
		} else if (!changeprice.equals(other.changeprice))
			return false;
		if (close == null) {
			if (other.close != null)
				return false;
		} else if (!close.equals(other.close))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (settlement == null) {
			if (other.settlement != null)
				return false;
		} else if (!settlement.equals(other.settlement))
			return false;
		if (volume == null) {
			if (other.volume != null)
				return false;
		} else if (!volume.equals(other.volume))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "HQRData [code=" + code + ", period=" + period + ", name="
				+ name + ", settlement=" + settlement + ", open=" + open
				+ ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + ", amount=" + amount
				+ ", changeprice=" + changeprice + ", changepercent="
				+ changepercent + "]";
	}
	
	
	
	
}
