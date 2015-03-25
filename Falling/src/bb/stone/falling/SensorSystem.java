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
	double gravity[] = new double[3];// 代表3个方向的重力加速度  
	double xAcceleration = 0;// 代表3个方向的真正加速度  
	double yAcceleration = 0;  
	double zAcceleration = 0;  
	double currentAcceleration = 0; // 当前的合加速度  
	double maxAcceleration = 0; // 最大加速度  
	// 接下来定义的数组是为了对加速度传感器采集的加速度加以计算和转化而定义的，转化的目的是为了使数据看上去更符合我们平时的习惯  
	float[] magneticValues = new float[3];  
	float[] accelerationValues = new float[3];  
	float[] values = new float[3];  
	float[] rotate = new float[9];  
	// 初始化的三个方位角的值  
	float Yaw = 0;  
	float Pitch = 0; // values[1]  
	float Roll = 0;  
	// 创建常量，把纳秒转换为秒。
	double NS2S = 1.0f / 1000000000.0f;
	float[] deltaRotationVector = new float[4];
	float timestamp;
	float EPSILON=(float) 0.1;

	public void onCreate() {  
		super.onCreate();  

		/** 
		 * 设置加速度传感器 
		 */  
		sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);  
		// 注册加速度传感器
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST);

		// 注册陀螺仪传感器
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);

		// 注册方向传感器
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_FASTEST);



	}  

	public void onStart(Intent intent, int startId) {  
		Log.i("-----------SensorService---------------","服务启动" );   

	}  
	//重写 onDestroy 方法  
	public void onDestroy() {  
		sm.unregisterListener(sensorListener);  
		currentAcceleration = 0;  
		maxAcceleration = 0;  
		xAcceleration = yAcceleration = zAcceleration = 0;  
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

				xAcceleration = event.values[0];  
				yAcceleration = event.values[1];  
				zAcceleration = event.values[2];  

				// 计算三个方向上的和加速度  
				double G = Math.sqrt(Math.pow(xAcceleration, 2)  
						+ Math.pow(zAcceleration, 2) + Math.pow(yAcceleration, 2));  
				currentAcceleration = G;  



				break;
			case Sensor.TYPE_GYROSCOPE: // 处理光线传感器传回的数据
				// 根据陀螺仪采样数据计算出此次时间间隔的偏移量后，它将与当前旋转向量相乘。
				if (timestamp != 0) {
					double dT = (event.timestamp - timestamp) * NS2S;
					// 未规格化的旋转向量坐标值，。
					float axisX = event.values[0];
					float axisY = event.values[1];
					float axisZ = event.values[2];

					// 计算角速度
					double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

					// 如果旋转向量偏移值足够大，可以获得坐标值，则规格化旋转向量
					// (也就是说，EPSILON 为计算偏移量的起步值。小于该值的偏移视为误差，不予计算。)
					if (omegaMagnitude > EPSILON) {
						axisX /= omegaMagnitude;
						axisY /= omegaMagnitude;
						axisZ /= omegaMagnitude;

						// 为了得到此次取样间隔的旋转偏移量，需要把围绕坐标轴旋转的角速度与时间间隔合并表示。
						// 在转换为旋转矩阵之前，我们要把围绕坐标轴旋转的角度表示为四元组。
						double thetaOverTwo = omegaMagnitude * dT / 2.0f;
						double sinThetaOverTwo = Math.sin(thetaOverTwo);
						double cosThetaOverTwo = Math.cos(thetaOverTwo);
						deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
						deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
						deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
						deltaRotationVector[3] = (float) cosThetaOverTwo;
						long tsTemp = event.timestamp;
						sendData(tsTemp); 
					}

  
				}
				timestamp = event.timestamp;
				// 为了得到旋转后的向量，用户代码应该把我们计算出来的偏移量与当前向量叠加。
				break;
			}


		}

		private void sendData(long tsTemp) {
			Intent i = new Intent();  
			

			i.setAction("com.dm.sensorReceiver");  
			i.putExtra("timestamp", tsTemp);
			i.putExtra("xA", xAcceleration);  
			i.putExtra("yA", yAcceleration);  
			i.putExtra("zA", zAcceleration);  
			i.putExtra("cA", currentAcceleration);  
			if(tsTemp-timestamp<100){
				i.putExtra("xD", deltaRotationVector[0]);  
				i.putExtra("yD", deltaRotationVector[1]);  
				i.putExtra("zD", deltaRotationVector[2]);  
				i.putExtra("cD", deltaRotationVector[3]);
			}
			System.out.println(tsTemp-timestamp+"");
			sendBroadcast(i);
		}  

		public void onAccuracyChanged(Sensor sensor, int accuracy) {  
		}  

	};  

	// ************************方向传感器**************************  
	// 手机方位传感器监听器，当获取的加速度或者磁力传感器数据发生精度要求范围内的变化时，监听器会调用onSensorChanged函数  
	SensorEventListener magneticListener = new SensorEventListener() {  

		public void onSensorChanged(SensorEvent event) {  
			// 如果是加速度传感器的值发生了变化  
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {  
				accelerationValues = event.values;  
			}  

		}  

		public void onAccuracyChanged(Sensor sensor, int accuracy) {  
		}  
	};  




	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
