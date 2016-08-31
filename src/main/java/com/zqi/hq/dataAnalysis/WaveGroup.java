package com.zqi.hq.dataAnalysis;

import java.util.Map;

public class WaveGroup {

	private Map<String, Object> waveA;
	private Map<String, Object> waveB;
	private Map<String, Object> center;
	private Map<String, Object> waveC;
	
	private int direct;
	
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public Map<String, Object> getWaveA() {
		return waveA;
	}
	public void setWaveA(Map<String, Object> waveA) {
		this.waveA = waveA;
	}
	public Map<String, Object> getWaveB() {
		return waveB;
	}
	public void setWaveB(Map<String, Object> waveB) {
		this.waveB = waveB;
	}
	public Map<String, Object> getCenter() {
		return center;
	}
	public void setCenter(Map<String, Object> center) {
		this.center = center;
	}
	public Map<String, Object> getWaveC() {
		return waveC;
	}
	public void setWaveC(Map<String, Object> waveC) {
		this.waveC = waveC;
	}
	
	
}
