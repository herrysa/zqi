package com.zqi.hq.dataAnalysis;

import java.util.Map;

public class WaveFactory {

	private Map<String, WaveShape> waveMap;
	
	public void addWave(Map<String, Object> wave){
		String levelStr = wave.get("level").toString();
		int level = Integer.parseInt(levelStr);
		WaveShape shape = waveMap.get(levelStr);
		if(shape==null){
			shape = new WaveShape();
			waveMap.put(levelStr,shape);
		}
		shape.pushWaveQueue(wave);
	}
}
