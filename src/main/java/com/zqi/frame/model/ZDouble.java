package com.zqi.frame.model;

import com.zqi.frame.util.DecimalUtil;

public class ZDouble {

	private double d;
	
	public ZDouble(double d){
		this.d = d;
	}
	
	public ZDouble add(double a){
		this.d = DecimalUtil.scale(this.d+a);
		return this;
	}
	
	public ZDouble subtrac(double s){
		this.d = DecimalUtil.scale(this.d-s);
		return this;
	}
	
	public ZDouble multi(double m){
		this.d = DecimalUtil.scale(this.d*m);
		return this;
	}
	
	public ZDouble divide(double d){
		this.d = DecimalUtil.scale(this.d/d);
		return this;
	}
	
	public double doubleValue(){
		return d;
	}
}
