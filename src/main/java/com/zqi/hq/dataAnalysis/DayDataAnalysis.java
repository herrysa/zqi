package com.zqi.hq.dataAnalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;

@Component("dayDataAnalysis")
public class DayDataAnalysis {

	private ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}
	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	public void dayAnalysis(){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			toWaveAnalysis(gp);
		}
	}
	
	public void toWaveAnalysis(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String name = gp.get("name").toString();
		
		//Map maxMap = new HashMap<String, BigDecimal>();
		List<Map<String, Object>> waveList = new ArrayList<Map<String,Object>>();
		Map<String, Object> lastDayData = null;
		//BigDecimal waveZf = new BigDecimal(0);
		//boolean horizWave = false;
		String periodBegin = null;
		//BigDecimal waveBegin = null;
		//String waveHighPeriod = null;
		BigDecimal waveHigh = null;
		//String waveLowPeriod = null;
		BigDecimal waveLow = null;
		//String waveMaxVolPeriod = null;
		//BigDecimal waveMaxVol = null;
		//String waveMinVolPeriod = null;
		//BigDecimal waveMinVol = null;	//
		//BigDecimal waveAvgVol = null;	//平均量能
		//String waveMaxZfPeriod = null;
		int waveNum = 1;
		
		//Map<String, Object> waveMapA = null;
		//Map<String, Object> waveMapB = null;
		
		int direct = 2;
		Map<String, Object> waveStatus = zqiDao.findFirst("select * from i_gpwave_status where code='"+code+"'");
		String dataSql = "select * from daytable_all where code='"+code+"'";
		String status_period = "";
		if(!waveStatus.isEmpty()){
			status_period = waveStatus.get("period").toString();
			dataSql += " and period>='"+status_period+"'";
			periodBegin = waveStatus.get("periodBegin").toString();
			direct = Integer.parseInt(waveStatus.get("direct").toString());
			waveNum = Integer.parseInt(waveStatus.get("waveNum").toString());
			waveHigh = (BigDecimal)waveStatus.get("waveHigh");
			waveLow = (BigDecimal)waveStatus.get("waveLow");
		}
		dataSql += " order by period asc";
		List<Map<String, Object>> dayDataList = zqiDao.findAll(dataSql);
		int i = 0;
		if(!waveStatus.isEmpty()){
			lastDayData = dayDataList.get(0);
			i=1;
		}
		for(;i<dayDataList.size();i++){
			Map<String, Object> dayData = dayDataList.get(i);
			BigDecimal close = (BigDecimal)dayData.get("close");
			BigDecimal high = (BigDecimal)dayData.get("high");
			BigDecimal low = (BigDecimal)dayData.get("low");
			BigDecimal amount = (BigDecimal)dayData.get("amount");
			String period = dayData.get("period").toString();
			System.out.println("处理的日期为:"+period+" 最高价格为："+high+" 最低价格为："+low);
			if(lastDayData==null){
				lastDayData = dayData;
				periodBegin = period;
				//waveBegin = close;
				waveHigh = high;
				waveLow = low;
				//waveHigh = amount;
			}else{
				BigDecimal lastClose = (BigDecimal)lastDayData.get("close");
				if(lastClose.compareTo(new BigDecimal(0))==0){
					continue;
				}
				String lastPeriod = lastDayData.get("period").toString();
				System.out.println("上一日日期为:"+lastPeriod+" 价格为："+lastClose);
				//int directTemp = close.compareTo(lastClose);
				int directTemp = getDirect(direct,dayData,lastDayData);
				System.out.println("当日价格方向："+directTemp+" 波段方向："+direct);
				if(directTemp==1){
					if(direct==-1){
						boolean isEnd = true;
						/*if(waveNum<3){
							Map<String, Object> dayDataTemp = mergeKData(i,dayDataList);
							directTemp = getDirect(direct,dayDataTemp,lastDayData);
							if(directTemp==-1){
								isEnd = false;
							}
						}*/
						if(isEnd){
						Map<String, Object> waveMap = new HashMap<String, Object>();
						waveMap.put("code", code);
						waveMap.put("name", name);
						waveMap.put("periodBegin", periodBegin);
						waveMap.put("periodEnd", lastPeriod);
						//waveMap.put("waveBegin", waveBegin);
						//waveMap.put("waveEnd", lastClose);
						waveMap.put("waveHigh", waveHigh);
						waveMap.put("waveLow", waveLow);
						waveMap.put("direct", direct);
						BigDecimal zf = waveHigh.subtract(waveLow).divide(waveHigh,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
						waveMap.put("waveNum", waveNum);
						waveMap.put("zf", zf);
						waveList.add(waveMap);
						periodBegin = lastPeriod;
						//waveBegin = lastClose;
						waveHigh = high;
						waveLow =low;
						direct = 1;
						//i += 2;
						waveNum = 1;
						lastDayData = dayData;
						}
					}else if(direct==1){
						waveNum ++;
						lastDayData = dayData;
						if(high.compareTo(waveHigh)>0){
							waveHigh = high;
						}
						if(low.compareTo(waveLow)<0){
							waveLow = low;
						}
						if(i==dayDataList.size()-1){
							zqiDao.excute("delete from i_gpwave_status where code='"+code+"'");
							waveStatus = new HashMap<String, Object>();
							waveStatus.put("code", code);
							waveStatus.put("name", name);
							waveStatus.put("periodBegin", periodBegin);
							waveStatus.put("period", period);
							waveStatus.put("direct", direct);
							waveStatus.put("waveNum", waveNum);
							waveStatus.put("waveHigh", waveHigh);
							waveStatus.put("waveLow", waveLow);
							BigDecimal zf = waveHigh.subtract(waveLow).divide(waveHigh,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveStatus.put("zf", zf);
							zqiDao.add(waveStatus, "i_gpwave_status");
						}
					}else{
						waveNum ++;
						lastDayData = dayData;
						direct = directTemp;
					}
				}else if(directTemp==-1){
					if(direct==1){
						boolean isEnd = true;
						/*if(waveNum<3){
							Map<String, Object> dayDataTemp = mergeKData(i,dayDataList);
							directTemp = getDirect(direct,dayDataTemp,lastDayData);
							if(directTemp==1){
								isEnd = false;
							}
						}*/
						if(isEnd){
						Map<String, Object> waveMap = new HashMap<String, Object>();
						waveMap.put("code", code);
						waveMap.put("name", name);
						waveMap.put("periodBegin", periodBegin);
						waveMap.put("periodEnd", lastPeriod);
						//waveMap.put("waveBegin", waveBegin);
						//waveMap.put("waveEnd", lastClose);
						waveMap.put("waveHigh", waveHigh);
						waveMap.put("waveLow", waveLow);
						waveMap.put("direct", direct);
						BigDecimal zf = waveHigh.subtract(waveLow).divide(waveLow,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
						waveMap.put("waveNum", waveNum);
						waveMap.put("zf", zf);
						waveList.add(waveMap);
						periodBegin = lastPeriod;
						//waveBegin = lastClose;
						waveHigh = high;
						waveLow =low;
						direct = -1;
						//i += 2;
						waveNum = 1;
						lastDayData = dayData;
						}
					}else if(direct==-1){
						waveNum ++;
						lastDayData = dayData;
						if(high.compareTo(waveHigh)>0){
							waveHigh = high;
						}
						if(low.compareTo(waveLow)<0){
							waveLow = low;
						}
						if(i==dayDataList.size()-1){
							zqiDao.excute("delete from i_gpwave_status where code='"+code+"'");
							waveStatus = new HashMap<String, Object>();
							waveStatus.put("code", code);
							waveStatus.put("name", name);
							waveStatus.put("periodBegin", periodBegin);
							waveStatus.put("period", period);
							waveStatus.put("direct", direct);
							waveStatus.put("waveNum", waveNum);
							waveStatus.put("waveHigh", waveHigh);
							waveStatus.put("waveLow", waveLow);
							BigDecimal zf = waveHigh.subtract(waveLow).divide(waveHigh,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveStatus.put("zf", zf);
							zqiDao.add(waveStatus, "i_gpwave_status");
						}
					}else{
						waveNum ++;
						lastDayData = dayData;
						direct = directTemp;
					}
				}else{
					waveNum ++;
					lastDayData = dayData;
					direct = directTemp;
				}
 				/*if(directTemp>0){
					if(direct==-1||direct==0){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.subtract(waveBegin).divide(waveBegin,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = 1;
							//i += 2;
							waveNum = 1;
							lastDayData = dayData;
<<<<<<< HEAD
							if(waveNum<3){
								if(waveMapA==null){
									waveMapA = waveMap;
								}else if(waveMapB==null){
									waveMapB = waveMap;
								}else{
									//合并这三个小于3的波段
								}
							}else{
								if(waveMapA==null){
									waveList.add(waveMap);
								}else if(waveMapB==null){
									//只有一段
									waveList.add(waveMap);
								}else{
									
									//合并这2小一大
									waveList.add(waveMap);
								}
							}
							
=======
						}else{
							waveNum ++;
							lastDayData = dayData;
>>>>>>> d0bd8c85fa7e44f1be59e69e551818bb532b5681
						}
					}else if(direct==1){
						waveNum ++;
						if(high.compareTo(waveHigh)>0){
							waveHigh = high;
						}
						if(low.compareTo(waveLow)<0){
							waveLow = low;
						}
						lastDayData = dayData;
						
					}else{
						waveNum ++;
						direct = 1;
					}
				}else if(directTemp==0){
					if(direct==-1||direct==1){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.subtract(waveBegin).divide(waveBegin,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							if(waveNum<3){
								waveMap.put("state", "9");
								lastDayData = dayData;
							}else{
								waveMap.put("state", "0");
							}
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = 0;
							//i += 2;
							waveNum = 1;
							lastDayData = dayData;
						}else{
							waveNum ++;
							lastDayData = dayData;
						}
					}else if(direct==0){
						waveNum ++;
						lastDayData = dayData;
					}else{
						waveNum ++;
						direct = 0;
					}
				}else if(directTemp<0){
					if(direct==1||direct==0){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.subtract(waveBegin).divide(waveBegin,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							if(waveNum<3){
								waveMap.put("state", "9");
								lastDayData = dayData;
							}else{
								waveMap.put("state", "0");
							}
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = -1;
							//i += 2;
							waveNum = 1;
							lastDayData = dayData;
						}else{
							waveNum ++;
							lastDayData = dayData;
						}
					}else if(direct==-1){
						waveNum ++;
						lastDayData = dayData;
					}else{
						waveNum ++;
						direct = -1;
					}
				}*/
			}
		}
		zqiDao.addList(waveList, "i_gpwave");
		System.out.println();
	}
	
	private int getDirect(int d,Map<String, Object> dayData,Map<String, Object> lastDayData){
		
		int direct = 0;
		
		BigDecimal high = (BigDecimal)dayData.get("high");
		BigDecimal low = (BigDecimal)dayData.get("low");
		
		BigDecimal high2 = (BigDecimal)lastDayData.get("high");
		BigDecimal low2 = (BigDecimal)lastDayData.get("low");
		
		int h = high.compareTo(high2);
		int l = low.compareTo(low2);
		if(h==1){
			if(l==1){
				direct = 1;
			}else if(l==-1){
				if(d==1){
					dayData.put("low",low2);
				}else if(d==-1){
					dayData.put("high",high2);
				}
				direct = d;
			}else{
				if(d==-1){
					dayData.put("high",high2);
				}
				direct = d;
			}
		}else if(h==-1){
			if(l==1){
				if(d==1){
					dayData.put("high",high2);
				}else if(d==-1){
					dayData.put("low",low2);
				}
				direct = d;
			}else if(l==-1){
				direct = -1;
			}else{
				if(d==1){
					dayData.put("high",high2);
				}
				direct = d;
			}
		}else{
			if(l==1){
				if(d==-1){
					dayData.put("low",low2);
				}
			}else if(l==-1){
				if(d==1){
					dayData.put("low",low2);
				}
			}
			direct = d;
		}
		
		return direct;
	}
	
	private Map<String, Object> mergeKData(int i,List<Map<String, Object>> dayDataList){
		
		Map<String, Object> cloneData = new HashMap<String, Object>();
		Map<String, Object> currentData = dayDataList.get(i);
		Map<String, Object> nextData = dayDataList.get(i);
		cloneData.putAll(currentData);
		
		BigDecimal high = (BigDecimal)cloneData.get("high");
		BigDecimal low = (BigDecimal)cloneData.get("low");
		
		BigDecimal high2 = (BigDecimal)nextData.get("high");
		BigDecimal low2 = (BigDecimal)nextData.get("low");
		
		int h = high.compareTo(high2);
		int l = low.compareTo(low2);
		
		if(h==-1){
			cloneData.put("high", high2);
			
		}
		if(l==1){
			cloneData.put("low", low2);
		}
		return cloneData;
	}
		
	private boolean isEnd(int direct,int i,BigDecimal lastClose,List<Map<String, Object>> dayDataList){
		boolean waveEnd = false;
		int transCount = 0;
		
		//Map<String, Object> dayData1 = dayDataList.get(i+1);
		//Map<String, Object> dayData2 = dayDataList.get(i+2);
		if(i+3>dayDataList.size()-1){
			return waveEnd;
		}
		Map<String, Object> dayData3 = dayDataList.get(i+3);
		
		//BigDecimal close1 = (BigDecimal)dayData1.get("close");
		//BigDecimal close2 = (BigDecimal)dayData2.get("close");
		BigDecimal close3 = (BigDecimal)dayData3.get("close");
		int directTemp = close3.compareTo(lastClose);
		if(direct==1){
			if(directTemp==-1||directTemp==0){
				waveEnd = true;
			}
		}else if(direct==-1){
			if(directTemp==1||directTemp==0){
				waveEnd = true;
			}
		}else{
			if(directTemp==-1||directTemp==1){
				waveEnd = true;
			}
		}
		
		return waveEnd;
	}
	
	
	public void dayWaveShapeAnalysis(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String name = gp.get("name").toString();
		List<Map<String, Object>> dayWaveDataList = zqiDao.findAll("select * from i_gpwave where code='"+code+"' order by periodBegin asc");
		
		String waveShape = "";
		String periodBegin = "";
		String periodEnd = "";
		BigDecimal waveHigh = null;
		BigDecimal waveLow = null;
		BigDecimal lastWaveHigh = null;
		BigDecimal lastWaveLow = null;
		boolean nearFloor = false;
		boolean nearCeil = false;
		int shapeNum = 0;
		for(int i=0;i<dayWaveDataList.size();i++){
			Map<String, Object> dayWaveData = dayWaveDataList.get(i);
			String directStr = dayWaveData.get("direct").toString();
			int direct = Integer.parseInt(directStr);
			String waveNumStr = dayWaveData.get("waveNum").toString();
			int waveNum = Integer.parseInt(waveNumStr);
			BigDecimal waveBegin = (BigDecimal)dayWaveData.get("waveBegin");
			BigDecimal waveEnd = (BigDecimal)dayWaveData.get("waveEnd");
			BigDecimal zf = (BigDecimal)dayWaveData.get("zf");
			String wavePeriodBegin = dayWaveData.get("periodBegin").toString();
			String wavePeriodEnd = dayWaveData.get("wavePeriodEnd").toString();
			
			
			if(direct==1){
				if("".equals(waveShape)){
					if(waveNum<3){
						waveShape = "震荡";
					}else{
						if(zf.compareTo(new BigDecimal(10))>0){
							waveShape = "上涨";
						}else{
							waveShape = "震荡";
						}
					}
					waveHigh = waveBegin;
					waveLow = waveEnd;
					lastWaveHigh = waveBegin;
					lastWaveLow = waveEnd;
					periodBegin = wavePeriodBegin;
					shapeNum += waveNum;
				}else{
					if("上涨".equals(waveShape)){
						int higherLastWaveHigh = waveEnd.compareTo(lastWaveHigh);
						if(higherLastWaveHigh>=0){
							lastWaveHigh = waveEnd;
							lastWaveLow = waveBegin;
							waveHigh = waveEnd;
						}else{
							//波段结束，形态为震荡
							nearFloor = true;
							lastWaveHigh = waveEnd;
							lastWaveLow = waveBegin;
						}
					}
				}
			}else if(direct==-1){
				if("".equals(waveShape)){
					if(waveNum<3){
						waveShape = "震荡";
					}else{
						if(zf.compareTo(new BigDecimal(-10))<0){
							waveShape = "下跌";
						}else{
							waveShape = "震荡";
						}
					}
					waveHigh = waveEnd;
					waveLow = waveBegin;
					lastWaveHigh = waveEnd;
					lastWaveLow = waveBegin;
					periodBegin = wavePeriodBegin;
					shapeNum += waveNum;
				}else{
					if("上涨".equals(waveShape)){
						int lowerLastWaveLow = waveEnd.compareTo(lastWaveLow);
						if(lowerLastWaveLow<=0){
							//波段结束，形态为下跌
							lastWaveHigh = waveBegin;
							lastWaveLow = waveEnd;
						}else{
							nearFloor = true;
							lastWaveHigh = waveBegin;
							lastWaveLow = waveEnd;
						}
					}else if("下跌".equals(waveShape)){
						int lowerLastWaveLow = waveEnd.compareTo(lastWaveLow);
						
						
					}else if("震荡".equals(waveShape)){
						int lowerLastWaveLow = waveEnd.compareTo(waveLow);
						if(lowerLastWaveLow<0){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", wavePeriodEnd);
							waveMap.put("waveHigh", waveHigh);
							waveMap.put("waveLow", waveLow);
							waveMap.put("waveShape", waveShape);

							waveShape = "下跌";
						}
						
					}
				}
			}else if(direct==0){
				if(waveNum<3){
					waveShape = "横盘";
				}else{
					waveShape = "横盘";
				}
			}
		}
	}
	
	public void centerAnalysis(){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			String code = gp.get("code").toString();
			List<Map<String, Object>> waveList = zqiDao.findAll("select * from i_gpwave where code='"+code+"' order by periodBegin asc");
			int i = 0 ;
			for(;i<waveList.size();i=i+2){
				Map<String, Object> wave1 = waveList.get(i);
				Map<String, Object> wave2 = waveList.get(i+1);
				Map<String, Object> wave3 = waveList.get(i+2);
				
				String direct1 = wave1.get("direct").toString();
				String direct2 = wave2.get("direct").toString();
				String direct3 = wave3.get("direct").toString();
				
				BigDecimal waveHigh1 = (BigDecimal)wave1.get("waveHigh");
				BigDecimal waveHigh2 = (BigDecimal)wave2.get("waveHigh");
				BigDecimal waveHigh3 = (BigDecimal)wave3.get("waveHigh");
				
				BigDecimal waveLow1 = (BigDecimal)wave1.get("waveLow");
				BigDecimal waveLow2 = (BigDecimal)wave2.get("waveLow");
				BigDecimal waveLow3 = (BigDecimal)wave3.get("waveLow");
				
				
				if("1".equals(direct1)){
					BigDecimal backPercent1 = waveHigh2.subtract(waveLow2).divide(waveHigh1.subtract(waveLow1),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal backPercent2 = waveHigh3.subtract(waveLow3).divide(waveHigh2.subtract(waveLow2),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
					int compare10 = backPercent1.compareTo(new BigDecimal(1));
					if(compare10==1){
						//反转情况，第三波收回一波高点，生成中枢，否则生成二波单边和一个中枢
					}else{
						//形成中枢，三波低点低于一波，三波为单边，否则一波单边
						int compare3 = backPercent1.compareTo(new BigDecimal(0.3));
						if(compare3==1){
							
						}else{
							int compare5 = backPercent1.compareTo(new BigDecimal(0.5));
						}
					}
				}
			}
		}
	}
	
	public void waveClass(){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		Map<String, Integer> waveNumFirst = new HashMap<String, Integer>();
		Map<String, Integer> waveNumSecond = new HashMap<String, Integer>();
		for(Map<String, Object> gp : gpDicList){
			String code = gp.get("code").toString();
			Map<String, Object> lastWave = null;
			List<Map<String, Object>> waveList = zqiDao.findAll("select * from i_gpwave where code='"+code+"' order by periodBegin asc");
			for(Map<String, Object> wave : waveList){
				if(lastWave==null){
					lastWave = wave;
				}else{
					String lastWaveNum = lastWave.get("waveNum").toString();
					String nowWaveNum = wave.get("waveNum").toString();
					Integer num = waveNumFirst.get(lastWaveNum);
					Integer num2 = waveNumSecond.get(lastWaveNum+"_"+nowWaveNum);
					if(num==null){
						num = 0;
					}else{
						num++;
					}
					if(num2==null){
						num2 = 0;
					}else{
						num2++;
					}
					waveNumFirst.put(lastWaveNum,num);
					waveNumSecond.put(lastWaveNum+"_"+nowWaveNum,num2);
				}
			}
		}
		Set<String> keySet = waveNumSecond.keySet();
		TreeSet<String> keyTreeSet = new TreeSet<String>(keySet);
		for(String key :keyTreeSet){
			String key1 = key.split("_")[0];
			Integer firstWave = waveNumFirst.get(key1);
			Integer secWave = waveNumSecond.get(key);
			BigDecimal fistNum = new BigDecimal(firstWave);
			BigDecimal secNum = new BigDecimal(secWave);
			secNum = secNum.divide(fistNum,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			System.out.println(key+":"+secNum);
		}
	}
}
