package com.zqi.hq.dataAnalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaveShape {

	private List<Map<String, Object>> waveQueue;//都是wave没有center
	
	private Map<String, Object> parentWave;//上级wave
	private WaveGroup lastWaveGroup;//最近一次centerAnalysis的结果
	//private WaveGroup waveGroup;//目前级别整体形态
	
	private int cursor = 0;
	private int level = 0;
	private int direct = 2;
	
	private WaveShape parentShape;
	
	public void pushWaveQueue(Map<String, Object> wave){
		if(waveQueue==null){
			waveQueue = new ArrayList<Map<String,Object>>();
		}
		waveQueue.add(wave);
		dealQueue();
	}
	
	public void dealQueue(){
		int length = waveQueue.size();
		if(length>1){
			Map<String, Object> waveNew = waveQueue.get(length-1);
			Map<String, Object> waveLast = waveQueue.get(length-2);
			String directNew = waveNew.get("direct").toString();
			String directLast = waveLast.get("direct").toString();
			if(directNew.equals(directLast)){//同向合并
				
			}else{
				if(length==3){//可构成中枢
					List<Map<String, Object>> centerList = centerAnalysis();
					//semiWave是否结束，结束生成上一级wave或center
				}
			}
		}
	}
	
	public List<Map<String, Object>> centerAnalysis(){
		Map<String, Object> waveA = waveQueue.get(0);
		Map<String, Object> waveB = waveQueue.get(1);
		Map<String, Object> waveC = waveQueue.get(2);
		
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
					
					WaveGroup wavegroup = new WaveGroup();
					wavegroup.setCenter(centerMap);
					wavegroup.setWaveC(waveC);
					centerTemp.add(centerMap);
					centerTemp.add(waveC);
					

					waveTransTemp.add(waveC);
					
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
	
	
	public void trendAnalysis(WaveGroup wavegroup){
		if(lastWaveGroup==null){
			lastWaveGroup = wavegroup;
		}else{
			Map<String, Object> center1 = lastWaveGroup.getCenter();
			Map<String, Object> center2 = wavegroup.getCenter();
			if(center1==null){
				lastWaveGroup = wavegroup;
			}else{
				BigDecimal high21 = (BigDecimal)center1.get("high2");
				BigDecimal low21 = (BigDecimal)center1.get("low2");
				
				BigDecimal high22 = (BigDecimal)center2.get("high2");
				BigDecimal low22 = (BigDecimal)center2.get("low2");
				
				if(low22.compareTo(high21)==1){
					//离开中枢，向上
					if(parentWave==null){
						parentWave = new HashMap<String, Object>();
						parentWave.put("code",  center1.get("code"));
						parentWave.put("name",  center1.get("name"));
						parentWave.put("periodBegin", center1.get("periodBegin"));
						parentWave.put("periodEnd", center1.get("periodEnd"));
						parentWave.put("high",  center2.get("high2"));
						parentWave.put("low",  center1.get("low2"));
					}else{
						String parentWaveDirect = parentWave.get("direct").toString();
						if("-1".equals(parentWaveDirect)){
							//转折
							if(parentShape==null){
								parentShape = new WaveShape();
							}
							parentShape.pushWaveQueue(parentWave);
						}else{
							//趋势延续
							
						}
					}
					
				}else if(high22.compareTo(low21)==-1){
					//离开中枢，向下
					
				}else{
					BigDecimal centerHigh = null;
					BigDecimal centerLow = null;
					
					BigDecimal centerHigh2 = null;
					BigDecimal centerLow2 = null;
					
					BigDecimal high11 = (BigDecimal)center1.get("high");
					BigDecimal low11 = (BigDecimal)center1.get("low");
					
					BigDecimal high12 = (BigDecimal)center2.get("high");
					BigDecimal low12 = (BigDecimal)center2.get("low");
					
					int h21 = high12.compareTo(high11);
					int l21 = low12.compareTo(low11);
					
					int h2_21 = high22.compareTo(high21);
					int l2_21 = low22.compareTo(low21);
					if(h21==1){
						centerHigh = high12;
						center1.put("high",high12);
					}
					if(l21==-1){
						centerLow = low12;
						center1.put("low",low12);
					}
					if(h2_21==1){
						centerHigh2 = high22;
						center1.put("high2",high22);
					}
					if(l2_21==-1){
						centerLow2 = low22;
						center1.put("low2",low22);
					}
					String numStr = center1.get("num").toString();
					int num = Integer.parseInt(numStr);
					String num2Str = center2.get("num").toString();
					int num2 = Integer.parseInt(num2Str);
					Map<String, Object> waveA = wavegroup.getWaveA();
					String numAStr = waveA.get("num").toString();
					int numA = Integer.parseInt(numAStr);
					center1.put("num",(num+num2+numA));
					BigDecimal zf = centerHigh.subtract(centerLow).divide(centerLow,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					center1.put("zf", zf);
					BigDecimal zf2 = centerHigh2.subtract(centerLow2).divide(centerLow2,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					center1.put("zf2", zf2);
					/*if(h21==1){
						if(l21==1){
							//中枢升高
							
						}else if(l21==0){
							//中枢扩张
							
						}else{
							//中枢扩张
							
						}
					}else if(h21==0){
						if(l21==-1){
							//中枢扩张
							
						}else if(l21==0){
							//中枢延伸
							
						}else{
							//中枢收缩
						}
					}else{
						if(l21==1){
							//中枢收缩
							
						}else if(l21==0){
							//中枢收缩
							
						}else{
							//中枢降低
						}
					}*/
				}
				int direcr1 = lastWaveGroup.getDirect();
				int direcr2 = wavegroup.getDirect();
				if(direcr1==1){
					if(direcr2==1){
						
					}
				}else if(direcr1==-1){
					
				}else if(direcr1==0){
					
				}
			}
		}
	}
	
	public List<Map<String, Object>> getWaveQueue() {
		return waveQueue;
	}
	public void setWaveQueue(List<Map<String, Object>> waveQueue) {
		this.waveQueue = waveQueue;
	}
	public int getCursor() {
		return cursor;
	}
	public void setCursor(int cursor) {
		this.cursor = cursor;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	
	public WaveShape getParentShape() {
		return parentShape;
	}

	public void setParentShape(WaveShape parentShape) {
		this.parentShape = parentShape;
	}
}
