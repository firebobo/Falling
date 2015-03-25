package bb.stone.falling;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bb.stone.falling.databases.DBManager;
import bb.stone.falling.entry.SensorData;

public class DocActivity extends Activity {
	String path= "train.txt";
	DBManager manager;
	ArrayList<SensorData> arrayData=new ArrayList<SensorData>();
	ArrayList<SensorData> temp=new ArrayList<SensorData>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		manager=new DBManager(getApplicationContext());

		final TextView text=(TextView) findViewById(R.id.text);
		Button bt=(Button) findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text.setText(manager.size()+"");
			}
		});

		IntentFilter sensorFilter = new IntentFilter();  
		sensorFilter.addAction("com.dm.sensorReceiver");  
		registerReceiver(sensorReceiver, sensorFilter);  


		// 启动注册了传感器监听的 Service  
		Intent i = new Intent(this, SensorSystem.class);  
		startService(i); 
	}

	// 新建并注册广播接收器，用于接收传感器类传递的数据  
	BroadcastReceiver sensorReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) {  
			Bundle bundle = intent.getExtras();// 获得 Bundle  
			long ts = bundle.getLong("timestamp");  
			float xA=bundle.getFloat("xA");
			float yA=bundle.getFloat("yA");
			float zA=bundle.getFloat("zA");
			float cA=bundle.getFloat("cA");
			float xD=bundle.getFloat("xD");
			float yD=bundle.getFloat("yD");
			float zD=bundle.getFloat("zD");
			float cD=bundle.getFloat("cD");
			SensorData data=new SensorData(ts, xA, yA, zA, xD, yD, zD, cD);
			if(arrayData.size()>0){

			}
			arrayData.add(data);
			if(arrayData.size()>100){
				temp.addAll(arrayData);
				arrayData.clear();
				new Thread(new Runnable() {

					@Override
					public void run() {
						manager.add(temp);
						try {
							for(int i=0;i<temp.size();i++){
								copyBigDataToSD(path,temp.get(i).toString());
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
						temp.clear();
					}
				}).start();

			}

		}
	};  

	public void copyBigDataToSD(String path, String data)
			throws IOException {
		File extDir = new File(Environment.getExternalStorageDirectory()+"/Falling/SensorData/");
		if(!extDir.exists()){
			extDir.mkdir();
		}
		String fullFilename =extDir.getAbsolutePath()+ "/"+path;
		ByteArrayInputStream myInput;

		OutputStream myOutput = new FileOutputStream(fullFilename);

		myInput = new ByteArrayInputStream(data.getBytes());

		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}

		myOutput.flush();
		myInput.close();
		myOutput.close();

	}

	public void saveAppend(String filename, String content) throws Exception
	{
		File extDir = Environment.getExternalStorageDirectory();
		String fullFilename =extDir.getAbsolutePath()+ path;
		FileOutputStream outStream = openFileOutput(fullFilename, Context.MODE_APPEND);
		outStream.write(content.getBytes());
		outStream.close();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(sensorReceiver);  
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	public void unLock(){  
		ContentResolver mContentResolver = getContentResolver();  
		//不建议使用  
		//setLockPatternEnabled(android.provider.Settings.System.LOCK_PATTERN_ENABLED,false);  

		//推荐使用  
		setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false,mContentResolver);  
	}  

	private void setLockPatternEnabled(String systemSettingKey, boolean enabled,ContentResolver mContentResolver) {  
		//不建议使用  
		//android.provider.Settings.System.putInt(mContentResolver,systemSettingKey, enabled ? 1 : 0);  

		//推荐使用  
		android.provider.Settings.Secure.putInt(mContentResolver, systemSettingKey,enabled ? 1 : 0);  
	} 

}
