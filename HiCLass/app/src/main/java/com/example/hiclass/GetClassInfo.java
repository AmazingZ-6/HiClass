package com.example.hiclass;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;

import com.example.hiclass.schedule.ScheduleMain;

public class GetClassInfo extends AppCompatActivity {

    private final static String SEND_IP = "101.43.18.202";  //发送IP
    private final static int SEND_PORT = 12345;               //发送端口号
//    private final static int RECEIVE_PORT = 10006;            //接收端口号

    private boolean listenStatus = true;  //接收线程的循环标识
    private byte[] receiveInfo;     //接收报文信息
    //    private ArrayList<String> info = new ArrayList<String>();
    private String info;
    private ArrayList<String> ClassItem = new ArrayList<String>();
    private byte[] buf;
    private String userName;
    private String logInfo;

    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress serverAddr;
    private final SendHandler sendHandler = new SendHandler();
    private final ReceiveHandler receiveHandler = new ReceiveHandler();
    private String isReLogin;


    private TextView tvMessage;
    private Button btnSendUDP;
    private EditText UserNameGet;
    private EditText PassWordGet;
    private Intent nextIntent;

    public GetClassInfo() {
    }


    class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            StringBuilder a = new StringBuilder();
//            int i;
//            for (i = 0; i < ClassItem.size(); i++) {
//                a.append(ClassItem.get(i));
//                if (i % 2 == 0) {
//                    a.append(":");
//                }
//                if (i % 2 == 1) {
//                    a.append(",");
//                }
//            }
//            Toast.makeText(GetClassInfo.this, info.get(1), Toast.LENGTH_SHORT).show();
        }


    }

    class SendHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(GetClassInfo.this, "加载课表中...", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_getinfo);
        btnSendUDP = (Button) findViewById(R.id.login);
//        tvMessage = (TextView) findViewById(R.id.add);

        btnSendUDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮则发送UDP报文
                new UdpSendThread().start();

            }
        });

//        //进入Activity时开启接收报文线程
//        new UdpReceiveThread().start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止接收线程，关闭套接字连接
//        listenStatus = false;
//        receiveSocket.close();
    }

    /*
     *   UDP数据发送线程
     * */
    public class UdpSendThread extends Thread {
        @Override
        public void run() {
            try {
                UserNameGet = (EditText) findViewById(R.id.username);
                userName = UserNameGet.getText().toString();
                logInfo = userName;
                sendHandler.sendEmptyMessage(1);
                sendSocket = new DatagramSocket();   // 创建DatagramSocket对象，使用随机端口
                serverAddr = InetAddress.getByName(SEND_IP);
                String sendInfo = logInfo;
                buf = sendInfo.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, SEND_PORT);
                sendSocket.send(outPacket);

                byte[] inBuf = new byte[32768];
                DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                sendSocket.receive(inPacket);
                receiveInfo = inPacket.getData();
                String infoTemp = new String(receiveInfo);
//                info.add(i,infoTemp);
                info = infoTemp;
                sendSocket.close();

                receiveHandler.sendEmptyMessage(1);
                Pattern p = Pattern.compile("(?<=')[\\u4e00-\\u9fa5_0-9a-zA-Z].*?(?=')");
                Matcher m = p.matcher(infoTemp);
                int a = 0;
                while (m.find()) {
                    ClassItem.add(a++, m.group());
                }
                if (!info.isEmpty()) {
                    Intent intent = getIntent();
                    String loginFlag = intent.getStringExtra("isReLogin");
                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", userName);
                    editor.apply();
//                    String data = getResources().getString(R.string.username);
//                    data = String.format(data, userName);
                    nextIntent = new Intent(GetClassInfo.this, ScheduleMain.class);
                    nextIntent.putExtra("class_info", ClassItem.toString());
                    if (loginFlag != null && loginFlag.equals("true")) {
                        nextIntent.putExtra("isReLogin", "true");
                    }
                    startActivity(nextIntent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *   UDP数据接收线程
     * */
//    public class UdpReceiveThread extends Thread {
//        @Override
//        public void run() {
//            try {
//
//                receiveSocket = new DatagramSocket(RECEIVE_PORT);
//                serverAddr = InetAddress.getByName(SEND_IP);
//
//                while (listenStatus) {
//                    byte[] inBuf = new byte[1024];
//                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
//                    receiveSocket.receive(inPacket);
//
//                    if (!inPacket.getAddress().equals(serverAddr)) {
//                        throw new IOException("未知名的报文");
//                    }
//
//                    receiveInfo = inPacket.getData();
//                    info = new String(receiveInfo);
//                    Pattern p = Pattern.compile("(?<=')[\\u4e00-\\u9fa5_a-zA-Z].*?(?=')");
//                    Matcher m = p.matcher(info);
//                    int a = 0;
//                    while(m.find()) {
//                        ClassItem.add(a++, m.group());
//                    }
//                    receiveHandler.sendEmptyMessage(1);
//                    if(!info.isEmpty()){
//                        returnIntent = new Intent();
//                        returnIntent.putExtra("class_info",ClassItem.toString());
//                        setResult(Activity.RESULT_OK,returnIntent);
//                        finish();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


}