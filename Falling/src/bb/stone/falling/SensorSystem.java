package bb.stone.falling;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorSystem extends Service {
	// 声明加速度传感器对象  
	private SensorManager sm = null; // 获取SensorManager对象，通过它可以获得距离，加速度等传感器对象  
	// ******************加速度传感器初始化变量*********************************************//  
	float[] Acceleration = new float[3];// 代表3个方向的加速度  
	// 接下来定义的数组是为了对加速度传感器采集的加速度加以计算和转化而定义的，转化的目的是为了使数据看上去更符合我们平时的习惯  
	// 初始化的三个方位角的值  
	// 创建常量，把纳秒转换为秒。
	float[] RotationVector = new float[3];
	float EPSILON=(float) 0.8;
	long tsTemp;
	public void onCreate() {  
		super.onCreate();  

		/** 
		 * 设置加速度传感器 
		 */  
		sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);  
		// 注册加速度传感器
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_GAME);
		// 注册陀螺仪传感器
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_GAME);
		tsTemp=System.currentTimeMillis();

	}  

	public void onStart(Intent intent, int startId) {  
		Log.i("-----------SensorService---------------","服务启动" );   

	}  
	//重写 onDestroy 方法  
	public void onDestroy() {  
		sm.unregisterListener(sensorListener);  
		sm = null;  

		super.onDestroy();  
	}  




	// **************************加速度测试传感器部分*************************  
	// 加速度传感器监听器，当获取的传感器数据发生精度要求范围内的变化时，监听器会调用onSensorChanged函数  
	SensorEventListener sensorListener = new SensorEventListener() {  

		public void onSensorChanged(SensorEvent event) { 


			// 通过getType方法获得当前传回数据的传感器类型
			switch (event.sensor.getType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION: // 处理加速度传感器传回的数据

				Acceleration = event.values;
				// 计算三个方向上的和加速度  
				break;
			case Sensor.TYPE_GYROSCOPE: // 处理光线传感器传回的数据
				// 根据陀螺仪采样数据计算出此次时间间隔的偏移量后，它将与当前旋转向量相乘。
				long tt=event.timestamp/(long)1000000;
				long t=tt-tsTemp;
				if ( t> 50) {
					// 未规格化的旋转向量坐标值，。
					RotationVector= event.values;

					// 计算角速度
					double omegaMagnitude = Math.sqrt(Acceleration[0]*Acceleration[0] + Acceleration[1]*Acceleration[1] + Acceleration[2]*Acceleration[2]);
					// 如果旋转向量偏移值足够大，可以获得坐标值，则规格化旋转向量
					// (也就是说，EPSILON 为计算偏移量的起步值。小于该值的偏移视为误差，不予计算。)

					if (omegaMagnitude > EPSILON) {
						// 为了得到此次取样间隔的旋转偏移量，需要把围绕坐标轴旋转的角速度与时间间隔合并表示。
						// 在转换为旋转矩阵之前，我们要把围绕坐标轴旋转的角度表示为四元组。

						tsTemp = event.timestamp/10000000;
						sendData(tsTemp); 
					}


				}
				// 为了得到旋转后的向量，用户代码应该把我们计算出来的偏移量与当前向量叠加。
				break;
			}


		}

		private void sendData(long tsTemp) {
			Intent i = new Intent();  
			i.setAction("com.dm.sensorReceiver");  
			i.putExtra("timestamp", tsTemp);
			i.putExtra("xA", Acceleration[0]);  
			i.putExtra("yA", Acceleration[1]);  
			i.putExtra("zA", Acceleration[2]);  
			i.putExtra("xD", RotationVector[0]);  
			i.putExtra("yD", RotationVector[1]);  
			i.putExtra("zD", RotationVector[2]);  
			clear();
			sendBroadcast(i);
		}  



		public void onAccuracyChanged(Sensor sensor, int accuracy) {  
		}  

	};  
	private void clear() {
		Acceleration[0]=Acceleration[1]=Acceleration[2]=0;
		RotationVector[0]=RotationVector[1]=RotationVector[2]=0;
	} 




	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
