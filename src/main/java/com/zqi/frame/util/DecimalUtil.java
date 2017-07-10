package com.zqi.frame.util;

import java.math.BigDecimal;

import com.zqi.frame.exception.ZqiException;

public class DecimalUtil {

	public static double scale(double num){
    	return scale(num , 2 , BigDecimal.ROUND_HALF_UP);
    }
	
	public static double scale(double num ,int dec){
    	return scale(num , dec , BigDecimal.ROUND_HALF_UP);
    }
    
    public static double scale(double num , int dec , int round){
    	BigDecimal bigp = new BigDecimal(num);
    	bigp = bigp.setScale(dec, round);
    	return bigp.doubleValue();
    }
    
    public static double divide(double dividend , double divisor ,int dec , int round){
    	double p = dividend/divisor;
    	BigDecimal bigp = new BigDecimal(p);
    	bigp = bigp.setScale(dec, round);
    	return bigp.doubleValue();
    }
    
    public static double divide(double dividend , double divisor ,int dec){
    	return divide(dividend,divisor,dec,BigDecimal.ROUND_HALF_UP);
    }
    
    public static double divide(double dividend , double divisor ){
    	return divide(dividend,divisor,2,BigDecimal.ROUND_HALF_UP);
    }
    
    public static double percent(double dividend , double divisor ){
    	double p = (dividend-divisor)/divisor*100;
    	BigDecimal bigp = new BigDecimal(p);
    	return bigp.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    public static double percentAbs(double dividend , double divisor ){
    	if(divisor==0){
    		throw new ZqiException("除数为0");
    	}
    	double p = (dividend-divisor)/divisor*100;
    	BigDecimal bigp = new BigDecimal(p);
    	return bigp.setScale(2, BigDecimal.ROUND_HALF_UP).abs().doubleValue();
    }
    
    public static int compareAbsPercent(double dividend , double divisor , double percent){
    	if(divisor==0){
    		throw new ZqiException("除数为0");
    	}
    	double p = (dividend-divisor)/divisor*100;
    	BigDecimal bigp = new BigDecimal(p);
    	BigDecimal bigpc = new BigDecimal(percent);
    	return bigp.setScale(2, BigDecimal.ROUND_HALF_UP).abs().compareTo(bigpc);
    }
    
    public static int comparePercent(double dividend , double divisor , double percent){
    	if(divisor==0){
    		throw new ZqiException("除数为0");
    	}
    	double p = (dividend-divisor)/divisor*100;
    	BigDecimal bigp = new BigDecimal(p);
    	BigDecimal bigpc = new BigDecimal(percent);
    	return bigp.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(bigpc);
    }
    
    public static void main(String[] args) {
		System.out.println(DecimalUtil.comparePercent(33-10, 23, 10));
	}
}
