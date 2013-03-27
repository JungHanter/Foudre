package com.soma.psproto;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 10001;
	private static final int REQUEST_CONNECT_DEVICE = 10002; //secure
	
	private Button btnSetBT, btnConnect, btnDisconn, btnSerialTest, btnPasso, btnGPS;
	private TextView mTitle;
	
	private String deviceAddress = null;
	
	private BluetoothAdapter mBTAdapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        btnSetBT = (Button)findViewById(R.id.btn_setbt);
        btnConnect = (Button)findViewById(R.id.btn_connectbt);
        btnDisconn = (Button)findViewById(R.id.btn_disconntbt);
        btnSerialTest = (Button)findViewById(R.id.btn_serialtest);
        btnPasso = (Button)findViewById(R.id.btn_passometer);
        btnGPS = (Button)findViewById(R.id.btn_gps);
        
        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name_full);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        mTitle.setText("Disconnected");
        
        //로컬 블루투쓰 하드웨어 어댑터를 받아옴
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBTAdapter == null) {
		    // device does not support Bluetooth
			new AlertDialog.Builder(this).setMessage("UNSUPPORT BLUETOOTH").setTitle("WARING!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.this.finish();
					} }).show();
			return;
		}
		
		btnSerialTest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mBTAdapter.isEnabled()) {
					if (deviceAddress != null) {
						Intent i = new Intent(MainActivity.this, SerialTestActivity.class);
						i.putExtra("ADDRESS", deviceAddress);
						startActivity(i);
			            return;
			        } else {
			        	Toast.makeText(MainActivity.this, "Please Connect to Divice", Toast.LENGTH_SHORT).show();
			        }
				} else {
					Toast.makeText(MainActivity.this, "Please Turn on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnPasso.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mBTAdapter.isEnabled()) {
					if (deviceAddress != null) {
						Intent i = new Intent(MainActivity.this, PassometerActivity.class);
						i.putExtra("ADDRESS", deviceAddress);
						startActivity(i);
			            return;
			        } else {
			        	Toast.makeText(MainActivity.this, "Please Connect to Divice", Toast.LENGTH_SHORT).show();
			        }
				} else {
					Toast.makeText(MainActivity.this, "Please Turn on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnGPS.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mBTAdapter.isEnabled()) {
					if (deviceAddress != null) {
						Intent i = new Intent(MainActivity.this, GPSActivity.class);
						i.putExtra("ADDRESS", deviceAddress);
						startActivity(i);
			            return;
			        } else {
			        	Toast.makeText(MainActivity.this, "Please Connect to Divice", Toast.LENGTH_SHORT).show();
			        }
				} else {
					Toast.makeText(MainActivity.this, "Please Turn on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
		btnConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE);
			}
		});
		
		btnDisconn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deviceAddress = null;
				mTitle.setText("Disconnected");
			}
		});
        
        btnSetBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*//블루투쓰 연결 확인
				if (!mBTAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				} else {
					new AlertDialog.Builder(MainActivity.this).setMessage("ALREADY CONNECTED")
    				.setPositiveButton("OK", null).show();
				}*/
				
				//블루투스 세팅 액티비티
				/*Intent btIntent = new Intent(Intent.ACTION_MAIN);
				btIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings"));
				startActivity(btIntent);*/
				startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)); //같은코드
			}
		});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode) {
    	case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	// get device Address
                deviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                mTitle.setText("Connected");
                Toast.makeText(this, "Connect with " + deviceAddress, Toast.LENGTH_SHORT).show();
            } else {
            	Toast.makeText(this, "Connect Fail", Toast.LENGTH_SHORT).show();
            }
            break;
            
    	/*case REQUEST_ENABLE_BT: //블루투쓰 연결 확인
    		if(resultCode == RESULT_OK) {
    			new AlertDialog.Builder(this).setMessage("BT CONNECT SUCCESSED")
    				.setPositiveButton("OK", null).show();
    		} else {
   			new AlertDialog.Builder(this).setMessage("BT CONNECT FAILURE")
				.setPositiveButton("OK", null).show();
    		}
    		break;*/
    	}
    }
    
    @Override
    public synchronized void onResume() {
        super.onResume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        deviceAddress = null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
