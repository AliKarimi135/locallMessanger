package ir.aliprogramer.localmessanger2.activity;


import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import ir.aliprogramer.localmessanger2.R;
import ir.aliprogramer.localmessanger2.WiFiDirectBroadcastReceiver;
import ir.aliprogramer.localmessanger2.model.Ingredient;

public class MainActivity extends AppCompatActivity {


    static WifiP2pManager managerObj;
    Boolean exit = false;
    static WifiP2pManager.Channel channelObj;
    WiFiDirectBroadcastReceiver receiverObj;
    IntentFilter filterObj;

    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    List<Ingredient> ingredientsList = new ArrayList<>();
    final List<WifiP2pDevice> peers = new ArrayList<>();
    public static String DMAC, DNAME;
    public static ArrayAdapter<Ingredient> adapter;
    public static EditText myName;



    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
                ingredientsList.clear();


                for (WifiP2pDevice d1 : peers)
                {
                    ingredientsList.add(new Ingredient(d1.deviceName, d1.deviceAddress));
                }

            }

            if (peers.size() == 0) {
                Log.d("MainActivity", "No peers found");
                Toast.makeText(getApplicationContext(),"دستگاهی پیدا نشد.",Toast.LENGTH_LONG).show();
                // no peers found
            }


        }
    };

    @Override
    public void onBackPressed() {

        if (exit) {
            Toast.makeText(this, "خداحافظ", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "جهت خروج از برنامه یکبار دیگر دکمه back  را بزنید.", Toast.LENGTH_SHORT).show();
            exit = true;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.transition_anim_r,R.anim.transition_anim_1_r);
        setContentView(R.layout.activity_main);


        //TO TURN ON THE WIFI

        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            },1500);
        }

        swipeRefreshLayout =findViewById(R.id.slayout);
        lv =  findViewById(R.id.FndListId);
        myName=findViewById(R.id.mtNameId);
        TextView ok=findViewById(R.id.okId);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discover();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        adapter = new ArrayAdapter<Ingredient>(MainActivity.this, android.R.layout.simple_list_item_1, ingredientsList);

                        lv.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 4 * 1000);

            }
        });



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String devAddress = ingredientsList.get(i).mac;
                WifiP2pConfig configDevice = new WifiP2pConfig();
                configDevice.deviceAddress = devAddress;

                DMAC = devAddress;

                DNAME = ingredientsList.get(i).name;


                managerObj.connect(channelObj, configDevice, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("connect", "Connection initiated successfully");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("connect", "Connection failed: " + reason);
                    }
                });

            }
        });


        managerObj = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channelObj = managerObj.initialize(this, getMainLooper(), null);
        receiverObj = new WiFiDirectBroadcastReceiver(managerObj, channelObj, this, peerListListener, this.getApplicationContext());

        filterObj = new IntentFilter();
        filterObj.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discover();


        adapter = new ArrayAdapter<Ingredient>(this, android.R.layout.simple_list_item_1, ingredientsList);
        lv.setAdapter(adapter);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"نام شما ذخیره شد",Toast.LENGTH_SHORT).show();
                ChatActivity.myNameString=myName.getText().toString();
            }
        });


    }

    void discover() {
        managerObj.discoverPeers(channelObj, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("discover", "discover onSuccess called");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("discover", "discover onSuccess called");
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiverObj, filterObj);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverObj);
    }

   /* public String macToString(String macAdd) {
        Log.d("sgk", "macToString() macAdd:" + macAdd);
        String temp = "SG";///// S G K /////
        Log.d("sgk",macAdd);
        StringTokenizer stringTokenizer = new StringTokenizer(macAdd,":");
        while(stringTokenizer.hasMoreTokens()){
            temp+=stringTokenizer.nextToken();

        }

        Log.d("sgk", "macToString() temp:" + temp);
        return temp;
    }*/


}
