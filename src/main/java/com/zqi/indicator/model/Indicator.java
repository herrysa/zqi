package com.zqi.indicator.model;

import com.zqi.frame.model.BaseObject;

public class Indicator  extends BaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8135827926849676748L;
	private String code;
	private String formula;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
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
		Indicator other = (Indicator) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Indicator [code=" + code + ", formula=" + formula + "]";
	}
	
	

}
