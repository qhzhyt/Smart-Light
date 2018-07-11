package com.example.administrator.lightcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.lightcontroller.util.RequestManager;
import com.example.administrator.lightcontroller.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class IndexActivity extends AppCompatActivity {

    private String serverIp;
    private int serverPort;
    private TextView tvServer;
    private CameraManager manager;
    private Camera camera = null;
    private Camera.Parameters parameters = null;


    private Socket socket;
    private InputStreamReader reader;
    private boolean flag = true;
    private LinearLayout container;

    private String macAddr;


    private Thread connectThread;

    private int luminance = 0;

    //Android UI
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setColorAndLight(R.color.lightslategray, 51);
                    break;
                case 2:
                    setColorAndLight(R.color.saddlebrown, 102);
                    break;
                case 3:
                    setColorAndLight(R.color.darkred, 153);
                    break;
                case 4:
                    setColorAndLight(R.color.mediumvioletred, 205);
                    break;
                case 5:
                    setColorAndLight(R.color.crimson, 255);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //tvServer = findViewById(R.id.container);

        container = findViewById(R.id.container);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        getApplicationContext();
        //WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        macAddr = Utils.getMacAddress(getApplicationContext());//wm.getConnectionInfo().getMacAddress();

        connectThread = new Thread() {
            @Override
            public void run() {
                postLightInfo();
            }
        };
    }


    private void postLightInfo() {

        HashMap<String, String> form = new HashMap<>();

        form.put("lightPhoneId", macAddr);


        while (true) {

            //form.put("lastActiveTime",""+System.currentTimeMillis());

            //Camera.Parameters cameraParameters = camera.getParameters();

            //String state=cameraParameters.getFlashMode();

            //if (state.equals(Camera.Parameters.FLASH_MODE_ON))
            //    form.put("state","true");
            //else
            //    form.put("state","false");

            //form.put("luminance",""+luminance);

            RequestManager requestManager = RequestManager.getInstance(IndexActivity.this);
            requestManager.requestAsyn("SingleLightBeanServlet", RequestManager.TYPE_GET, form, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        //String tLevel=jsonObject.getString("")
                        setTitle(serverIp + ":" + serverPort + "(已连接)");
                        int level = jsonObject.getInt("luminance");

                        Message msg = new Message();
                        msg.what = level;
                        handler.sendMessage(msg);

                        boolean state=jsonObject.getBoolean("state");

                        closeORopen(state);


                    } catch (JSONException e) {

                        e.printStackTrace();
                        setTitle(serverIp + ":" + serverPort + "(未连接)");

                    }


                }

                @Override
                public void onReqFailed(String errorMsg) {
                    setTitle(serverIp + ":" + serverPort + "(未连接)");
                }

            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }


    private void setColorAndLight(int colorId, int level) {
        // 设备屏幕背景色
        container.setBackgroundColor(getResources().getColor(colorId));
        //设置屏幕亮度
        setLight(IndexActivity.this, level);

    }

    /**
     * 设备屏幕背景色
     *
     * @param colorId
     */
    private void setColor(int colorId) {
        Resources res = getResources();
        Drawable drawable = res.getDrawable(colorId);
        this.getWindow().setBackgroundDrawable(drawable);
    }


    /**
     * 设置屏幕亮度
     *
     * @param context
     * @param level
     */
    private void setLight(Activity context, int level) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(level) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    private void closeORopen(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //android6.0调用的手电筒接口
            try {
                manager.setTorchMode("0", isChecked);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            //低于6.0系统的手电筒
            if (isChecked) {
                camera = Camera.open();
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);
                camera.startPreview();
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * Android4.0之后的新特性
     * 网络连接部分必须要放在子线程中不能再主线程中执行
     */

    /*private void initScoket() {
        try {
            socket = new Socket(serverIp, serverPort);
            new Thread() {
                @Override
                public void run() {
                    try {
                        reader = new InputStreamReader(socket.getInputStream());
                        char[] datas = new char[1024];
                        int length = reader.read(datas);

                        while (length != -1 && flag) {
                            if (datas[0]==0xff){
                                length = reader.read(datas);
                                continue;
                            }
                            String readStr = new String(datas, 0, length);
                            if (readStr.equals("10")) {
                                closeORopen(true);
                            } else if (readStr.equals("11")) {
                                closeORopen(false);
                            }

                            int leval = Integer.parseInt(readStr);
                            Message msg = new Message();
                            msg.what = leval;
                            handler.sendMessage(msg);
                            //setColorAndLight(R.color.lightslategray,51);


                            //System.out.println(readStr);
                            Log.e("CMD", readStr);
                            length = reader.read(datas);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    private void initData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] temps = sp.getString("server_host", "192.168.43.1:8888").split(":");

        serverIp = temps[0];

        if (temps.length > 1) {
            serverPort = Integer.parseInt(temps[1]);
        }

        RequestManager.BASE_URL = "http://" + serverIp + ":" + serverPort;

        //tvServer.setText(serverIp+":"+serverPort);
        setTitle(serverIp + ":" + serverPort + "(未连接)");

        //connectThread.stop();


    }

    /**
     * 重写activity 中创建菜单的选项
     *
     * @return 返回真假决定是否显示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //通过inflater对象将自己写的资源文件转换成menu对象
        //参数1代表需要创建的菜单，参数2代表将菜单设置到对应的menu上
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * 重写activity 中菜单选中事件
     *
     * @return 返回真假，对实现效果没有影响。
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_connect:
                Toast.makeText(this, "connecting......", Toast.LENGTH_SHORT).show();
                connectThread.start();
                setTitle(serverIp + ":" + serverPort + "(正在连接)");
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_disconnect:

                break;
        }

        return false;
    }

}
