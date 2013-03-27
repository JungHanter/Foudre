package com.soma.psproto;

import java.util.Calendar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class GPSActivity extends Activity {
	private String deviceAddress = null;
	
	private static final String RQ_GETRTDATA  = "RQ:GRTGPS]";
    private static final String RQ_STOPRTDATA = "RQ:SRTGPS]";
    private static final String RQ_DISCONNECT = "RQ:DISCON]";
    
    private static final String RQ_STARTWALK  = "RQ:STWALK]";
    private static final String RQ_STOPWALK   = "RQ:SPWALK]";
    
    private static final String RS_GETRTDATA  = "RS:GRTGPS";
    private static final String RS_STARTWALK  = "RS:STWALK";
    private static final String RS_STOPWALK   = "RS:SPWALK";
    private static final String RS_GETWALKDONE = "RS:GWALKD";
    
    private int mReadState = RDST_NONE;
    private int mReadOldState = mReadState;
    
    private static final int RDST_NONE = 0;
    private static final int RDST_GETRTDATA = 1;
    private static final int RDST_GETWALKDATA = 2;
    
    private boolean bReadWalkFirst = true;
    private int walkMindNum = 0;
    
    private Calendar mCalendar;
    
 // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private Button btnRT, btnWalk;
    private boolean bShowRT = false;
    private boolean bWalking = false;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // Local Bluetooth adapter
    private BluetoothAdapter mBTAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_gps);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		deviceAddress = getIntent().getStringExtra("ADDRESS");
		
		// Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText("Connect GPS");
        mTitle = (TextView) findViewById(R.id.title_right_text);
        
        mCalendar = Calendar.getInstance();
		
	}
	
	@Override
    public void onStart() {
        super.onStart();
        
        if (mChatService == null) setupChat();
	}
	
	@Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
	
	private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.gps_chat_list);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the send button with a listener that for click events
        btnRT = (Button) findViewById(R.id.gps_btn_rt);
        btnRT.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(bShowRT) {	//show -> hide
                	bShowRT = false;
                	btnRT.setText("Show RT data");
                	sendMessage(RQ_STOPRTDATA);
                } else {	//hide -> show
                	bShowRT = true;
                	btnRT.setText("Hide RT data");
                	sendMessage(RQ_GETRTDATA);
                }
            }
        });
        
        btnWalk = (Button) findViewById(R.id.gps_btn_walk);
        btnWalk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(bWalking) {	//walking -> not
                	bWalking = false;
                	btnWalk.setText("Start walk");
                	sendMessage(RQ_STOPWALK);
                } else {	//not -> walking
                	bWalking = true;
                	btnWalk.setText("Stop walk");
                	sendMessage(RQ_STARTWALK);
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        
        BluetoothDevice device = mBTAdapter.getRemoteDevice(deviceAddress);
        // Attempt to connect to the device
        mChatService.connect(device, true);
    }
	
	private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "Please connect a device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
	
	
	StringBuffer mReadStringBuffer = new StringBuffer();
	MyTime nowTime = new MyTime(0, 0);
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothChatService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case BluetoothChatService.MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
//                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
//                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case BluetoothChatService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                
                
                while(true) {
                	//]가 있으면
                	if(readMessage.contains("]")) {
	                	int pos = readMessage.indexOf(']');
	                	
	                	//]까지 출력하고
	                	mReadStringBuffer.append(readMessage.substring(0, pos));
	                	String response = mReadStringBuffer.toString();
	                	
	                	if(mReadState == RDST_GETWALKDATA) {
	                		if(bReadWalkFirst) {
	                			bReadWalkFirst = false;
	                			try {
	                				walkMindNum = Integer.parseInt(response);
	                			}catch(Exception e) {
	                				Log.d("parseerror", "string="+response);
	                				try {
	                					walkMindNum = Integer.parseInt(response.substring(0, 1));
	                				} catch(Exception e2) {
	                					try {
	                						walkMindNum = Integer.parseInt(response.substring(1, 2));
	                					} catch(Exception e3) {
	                						walkMindNum=0;
	                						bReadWalkFirst = true;
	    		                			mReadState = mReadOldState;
	                					}
	                				}
	                			}
	                			Log.d("gps", "walkNum="+walkMindNum);
	                			
	                			Calendar c = Calendar.getInstance();
	                			nowTime.h = c.get(Calendar.HOUR);
	                			nowTime.m = c.get(Calendar.MINUTE);
	                			Log.d("nowTime", ""+nowTime.h+":"+nowTime.m);
	                		} else {
		                		if(response.equals(RS_GETWALKDONE)) {
		                			walkMindNum = 0;
		                			bReadWalkFirst = true;
		                			mReadState = mReadOldState;
		                			Log.d("gps", "get data end");
		                		} else {
		                			try {
		                				String[] loc = response.split("/");
		                				
		                				int lati = Integer.parseInt(loc[0]);
		                				int longi = Integer.parseInt(loc[1]);

			                			MyTime time = new MyTime(nowTime);
		            					time.subMinute(walkMindNum);
		            					walkMindNum--;
			                			mConversationArrayAdapter.add("" + time.h + "시 " +
			                					time.m + "분 : " + "lat/long=" + 
			                					lati + "/" + longi);
		                			} catch(Exception e) {
		                				Log.d("parseerror", "string="+response);
		                				e.printStackTrace();
		                			}
		                		}
	                		}
	                	} else {
	                		if(response.equals(RS_GETRTDATA)) {
		                		mReadOldState = mReadState;
		                		mReadState = RDST_GETRTDATA;
		                	} else if(response.equals(RS_STOPWALK)) {
		                		//stopwalk버튼 눌러서 리스폰스 오면 그때부터 walk data 수산
		                		mReadOldState = mReadState;
		                		mReadState = RDST_GETWALKDATA;
		                	}
	                		
	                		mConversationArrayAdapter.add(mReadStringBuffer.toString());
	                		
	                	}
	                	
	                	mReadStringBuffer.delete(0, mReadStringBuffer.length());
	                	
	                	//]이후는 다시 readMessage로
	                	readMessage = readMessage.substring(pos+1, readMessage.length());
	                	
	                } else {
	                	// 더이상 ]가 없으면 버퍼에 전부 추가
	                	mReadStringBuffer.append(readMessage);
	                	break;
	                }
                }
                
//                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case BluetoothChatService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(BluetoothChatService.DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothChatService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothChatService.TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
	
	
	class GPSPosPair {
		double latitude, longitude;
		
		GPSPosPair(double lat, double lon) {
			latitude = lat;
			longitude = lon;
		}
		
		int getLat1E6() {
			return (int)(latitude*1000000);
		}
		
		int getLong1E6() {
			return (int)(longitude*1000000);
		}
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) {
        	sendMessage(RQ_STOPRTDATA);
        	sendMessage(RQ_STOPWALK);
        	mChatService.stop();
        }
    }
	
	class MyTime {
    	int h;
    	int m;
    	
    	MyTime(int h, int m) {
    		this.h = h;
    		this.m = m;
    	}
    	
    	MyTime(MyTime time) {
    		h = time.h;
    		m = time.m;
    	}
    	
    	void subMinute(int m2) {
    		m = m - m2;
    		if(m<0) {
    			m += 60;
    			h--;
    			if(h==-1) {
    				h=11;
    			}
    		}
    	}
    }
}
