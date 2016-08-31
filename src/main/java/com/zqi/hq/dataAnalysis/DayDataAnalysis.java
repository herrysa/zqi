package com.zqi.hq.dataAnalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
		List<Map<String, Object>> waveTrans = new ArrayList<Map<String,Object>>();
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
						//waveList.add(waveMap);
						waveTrans.add(waveMap);
						periodBegin = lastPeriod;
						//waveBegin = lastClose;
						waveHigh = high;
						waveLow =low;
						direct = 1;
						//i += 2;
						waveNum = 1;
						lastDayData = dayData;
						waveTrans.add(waveMap);
						if(waveTrans.size()==3){
							//List<Map<String, Object>> waveTransTemp = center1Analysis(waveTrans);
							//waveList.addAll(waveTransTemp);
						}
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
	
/*	public List<Map<String, Object>> center1Analysis(Map<String, WaveShape> waveFactory){
		for(int i=0;i<10;i++){
			WaveShape waveShape = waveFactory.get(i);
			List<Map<String, Object>> wavequeue = waveShape.getWaveQueue();
			if(wavequeue!=null&&wavequeue.size()==3){//可以升级，wave或center数等于3
				//1.判断中枢及两边(l1)
				List<Map<String, Object>> centerList = centerAnalysis(wavequeue);
				//2.和同级比较
				
			}
		}
	}*/
	public List<Map<String, Object>> centerAnalysis(List<Map<String, Object>> wavequeue){
		Map<String, Object> waveA = wavequeue.get(0);
		Map<String, Object> waveB = wavequeue.get(1);
		Map<String, Object> waveC = wavequeue.get(2);
		
		String code = waveA.get("code").toString();
		String name = waveA.get("name").toString();
		
		String directA = waveA.get("direct").toString();
		String directB = waveB.get("direct").toString();
		String directC = waveC.get("direct").toString();
		
		BigDecimal waveHighA = (BigDecimal)waveA.get("waveHigh");
		BigDecimal waveHighB = (BigDecimal)waveB.get("waveHigh");
		BigDecimal waveHighC = (BigDecimal)waveC.get("waveHigh");
		
		BigDecimal waveLowA = (BigDecimal)waveA.get("waveLow");
		BigDecimal waveLowB = (BigDecimal)waveB.get("waveLow");
		BigDecimal waveLowC = (BigDecimal)waveC.get("waveLow");
		
		Map<String, Object> center = new HashMap<String, Object>();
		List<Map<String, Object>> waveTransTemp = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> centerTemp = new ArrayList<Map<String,Object>>();
		
		if("1".equals(directA)){
			BigDecimal backPercentB_A = waveHighB.subtract(waveLowB).divide(waveHighA.subtract(waveLowA),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal backPercentC_B = waveHighC.subtract(waveLowC).divide(waveHighB.subtract(waveLowB),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
			int compareB_A_1 = backPercentB_A.compareTo(new BigDecimal(1));
			if(compareB_A_1==1){
				//反转情况
				int compareC_B_1 = backPercentC_B.compareTo(new BigDecimal(1));
				if(compareC_B_1==1){
					//第三波收回一波高点，生成A_B中枢,单边为3波
					BigDecimal high = (BigDecimal)waveB.get("high");
					BigDecimal low = (BigDecimal)waveB.get("low");
					BigDecimal high2 = (BigDecimal)waveC.get("high");
					BigDecimal low2 = low;
					Map<String, Object> centerMap = new HashMap<String, Object>();
					centerMap.put("code", code);
					centerMap.put("name", name);
					centerMap.put("periodBegin", waveB.get("periodBegin"));
					centerMap.put("periodEnd", waveB.get("periodEnd"));
					centerMap.put("high", high);
					centerMap.put("low", low);
					centerMap.put("high2", high2);
					centerMap.put("low2", low2);
					centerMap.put("level", 1);
					BigDecimal zf = high.subtract(low).divide(low,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					centerMap.put("zf", zf);
					BigDecimal zf2 = high2.subtract(low2).divide(low2,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					centerMap.put("zf2", zf2);
					centerMap.put("num", waveB.get("num"));
					
					waveA.put("level", "0");
					waveB.put("level", "0");
					waveC.put("level", "1");
					
					centerTemp.add(centerMap);
					centerTemp.add(waveC);

					waveTransTemp.add(waveC);
					
					wavequeue = waveTransTemp;
					return centerTemp;
				}else{
					int compareCg_Ad = waveHighC.compareTo(waveLowA);
					if(compareCg_Ad==1){
						//形成一个A_B中枢，没有单边
					}else{
						//生成二波单边,单边left：1
					}
				}
			}else{
				//形成A_B中枢
				BigDecimal backPercentC_A = waveHighC.subtract(waveLowC).divide(waveHighA.subtract(waveLowA),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
				int compareC_A_1 = backPercentC_A.compareTo(new BigDecimal(1));
				if(compareC_A_1==1){
					//三波长大于一波长 ，三波为单边
				}else{
					//否则一波单边
				}
				int compareB_A_03 = backPercentB_A.compareTo(new BigDecimal(0.66));
				if(compareB_A_03==1){
					//弱中枢
				}else{
					int compareB_A_02 = backPercentB_A.compareTo(new BigDecimal(0.5));
					if(compareB_A_02==1){
						//一般中枢
					}else{
						//弱中枢
					}
				}
			}
		}
		return null;
	}
	
	public void centerAnalysis(){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			String code = gp.get("code").toString();
			List<Map<String, Object>> waveList = zqiDao.findAll("select * from i_gpwave where code='"+code+"' order by periodBegin asc");
			//Map<String, Object> center = null;
			int i = 0 ;
			for(;i<waveList.size();i=i+2){
				Map<String, Object> waveA = waveList.get(i);
				Map<String, Object> waveB = waveList.get(i+1);
				Map<String, Object> waveC = waveList.get(i+2);
				
				String directA = waveA.get("direct").toString();
				String directB = waveB.get("direct").toString();
				String directC = waveC.get("direct").toString();
				
				BigDecimal waveHighA = (BigDecimal)waveA.get("waveHigh");
				BigDecimal waveHighB = (BigDecimal)waveB.get("waveHigh");
				BigDecimal waveHighC = (BigDecimal)waveC.get("waveHigh");
				
				BigDecimal waveLowA = (BigDecimal)waveA.get("waveLow");
				BigDecimal waveLowB = (BigDecimal)waveB.get("waveLow");
				BigDecimal waveLowC = (BigDecimal)waveC.get("waveLow");
				
				Map<String, Object> center = new HashMap<String, Object>();
				
				
				if("1".equals(directA)){
					BigDecimal backPercentB_A = waveHighB.subtract(waveLowB).divide(waveHighA.subtract(waveLowA),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal backPercentC_B = waveHighC.subtract(waveLowC).divide(waveHighB.subtract(waveLowB),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
					int compareB_A_1 = backPercentB_A.compareTo(new BigDecimal(1));
					if(compareB_A_1==1){
						//反转情况
						int compareC_B_1 = backPercentC_B.compareTo(new BigDecimal(1));
						if(compareC_B_1==1){
							//第三波收回一波高点，生成A_B中枢,单边为3波
							center.put("centerBegin", "");
							center.put("centerEnd", "");
							center.put("code", "");
							center.put("name", "");
							center.put("centerHigh", "");
							center.put("centerLow", "");
							center.put("centerHigh2", "");
							center.put("centerLow2", "");
							center.put("centerNum", "");
							center.put("centerZf", "");
							center.put("periodEdge", "");
							center.put("waveEdge", "");
						}else{
							int compareCg_Ad = waveHighC.compareTo(waveLowA);
							if(compareCg_Ad==1){
								//形成一个A_B中枢，没有单边
							}else{
								//生成二波单边,单边left：1
							}
						}
					}else{
						//形成A_B中枢
						BigDecimal backPercentC_A = waveHighC.subtract(waveLowC).divide(waveHighA.subtract(waveLowA),10,BigDecimal.ROUND_HALF_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
						int compareC_A_1 = backPercentC_A.compareTo(new BigDecimal(1));
						if(compareC_A_1==1){
							//三波长大于一波长 ，三波为单边
						}else{
							//否则一波单边
						}
						int compareB_A_03 = backPercentB_A.compareTo(new BigDecimal(0.66));
						if(compareB_A_03==1){
							//弱中枢
						}else{
							int compareB_A_02 = backPercentB_A.compareTo(new BigDecimal(0.5));
							if(compareB_A_02==1){
								//一般中枢
							}else{
								//弱中枢
							}
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
