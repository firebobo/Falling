package bb.stone.falling.entry;

public class SensorData {
	long time;
	float xA;
	float yA;
	float zA;
	float xD;
	float yD;
	float zD;

	public SensorData() {
		super();
	}
	public SensorData(long timestramp, float xA, float yA, float zA, float xD,
			float yD, float zD) {
		super();
		this.time = timestramp;
		this.xA = xA;
		this.yA = yA;
		this.zA = zA;
		this.xD = xD;
		this.yD = yD;
		this.zD = zD;
	}
	public long getTime() {
		return time;
	}
	public float getxA() {
		return xA;
	}
	public float getxD() {
		return xD;
	}
	public float getyA() {
		return yA;
	}
	public float getyD() {
		return yD;
	}
	public float getzA() {
		return zA;
	}
	public float getzD() {
		return zD;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public void setxA(float xA) {
		this.xA = xA;
	}
	public void setxD(float xD) {
		this.xD = xD;
	}
	public void setyA(float yA) {
		this.yA = yA;
	}
	public void setyD(float yD) {
		this.yD = yD;
	}
	public void setzA(float zA) {
		this.zA = zA;
	}
	public void setzD(float zD) {
		this.zD = zD;
	}
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(time+":");
		sb.append(xA+" ");
		sb.append(yA+" ");
		sb.append(zA+" ");
		sb.append(xD+" ");
		sb.append(yD+" ");
		sb.append(zD+" ");
		return sb.toString();
	}
}
