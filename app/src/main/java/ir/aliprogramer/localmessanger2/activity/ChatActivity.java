package ir.aliprogramer.localmessanger2.activity;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ir.aliprogramer.localmessanger2.R;
import ir.aliprogramer.localmessanger2.adapter.ChatAdapter;
import ir.aliprogramer.localmessanger2.model.ChatMessage;

import static ir.aliprogramer.localmessanger2.WiFiDirectBroadcastReceiver.EXTRA_MESSAGE;
import static ir.aliprogramer.localmessanger2.WiFiDirectBroadcastReceiver.disconnect;
import static ir.aliprogramer.localmessanger2.model.User.receiveMessage;
import static ir.aliprogramer.localmessanger2.model.User.sendMessage;

public class ChatActivity extends AppCompatActivity {
    public static ChatAdapter chatAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false, restoredFlag = false;
    public static String macTableName = "dmac";

    Animation sendBtnAnim;

    public static TextView otherDeviceName;
    public static String myNameString ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.transition_anim_1,R.anim.transition_anim);
        setContentView(R.layout.activity_chat);

        //macTableName = getStringMac();
        otherDeviceName =findViewById(R.id.myUserNameId);
        buttonSend =  findViewById(R.id.send);
        sendBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.send_btn_anim);
        listView =  findViewById(R.id.msgview);
        listView.setStackFromBottom(true);
        chatAdapter = new ChatAdapter(getApplicationContext(), R.layout.right_raw);
        listView.setAdapter(chatAdapter);


        chatText =  findViewById(R.id.msg);



        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatAdapter);


        chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatAdapter.getCount() - 1);
            }
        });

        receiveMessage(this.getApplicationContext());
       // sendConfig(macTableName);

    }

    @Override
    public void onBackPressed() {

        disconnect();

        buttonSend.startAnimation(sendBtnAnim);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }
        },1000);
    }



    private boolean sendChatMessage( ) {
        Intent i = getIntent();
        String serverAddress = i.getStringExtra(EXTRA_MESSAGE);

        try {
            side = true;
            final InetAddress inetAddress = InetAddress.getByName(serverAddress);

            String txt = chatText.getText().toString();
            if (txt.length() != 0) {
                chatAdapter.add(new ChatMessage(side, txt));
                sendMessage(txt, inetAddress);
            }
            chatText.setText("");


        } catch (UnknownHostException e) {

        }
        return true;
    }
/*
    private boolean sendConfig(String msg) {
        Intent i = getIntent();///// S G K /////
        String serverAddress = i.getStringExtra(EXTRA_MESSAGE);

        try {
            side = true;
            final InetAddress inetAddress = InetAddress.getByName(serverAddress);

            String txt = "sgk.macTable=" + msg;
            Log.d("sgk","sgk.macTable="+txt+"sendConfig()--------------");
            sendMessage(txt, inetAddress);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String txt1;

                    txt1 ="sgk.device.myName="+myNameString;

                    Log.d("sgk","sgk.device.myName="+txt1+"sendConfig()--------------");
                    sendMessage(txt1, inetAddress);

                }
            },800);

        } catch (UnknownHostException e) {

        }
        return true;
    }*/
    static public void setOtherDeviceName(String string){
        otherDeviceName.setText(string);
    }

    /*public String getStringMac() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();



        String temp = "SG";
//       ///// S G K /////
        StringTokenizer stringTokenizer = new StringTokenizer(macAddress, ":");
        while (stringTokenizer.hasMoreTokens()) {
            temp += stringTokenizer.nextToken();

        }
        temp += "SG";
        Log.d("sgk", "macToString() temp:" + temp);
        return temp;
    }*/
}
