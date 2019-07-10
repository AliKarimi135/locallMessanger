package ir.aliprogramer.localmessanger2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import ir.aliprogramer.localmessanger2.activity.ChatActivity;
import ir.aliprogramer.localmessanger2.activity.MainActivity;

public class ServerTask extends AsyncTask<Void,Void,String> {
    public static final String EXTRA_MESSAGE = "MESSAGE";

    public static final String DMAC = "dMac";
    public static final String DNAME = "dname";
    private static final String TAG2 = "ServerTask";
    Context context;
    ServerTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();//Waits till client connects
            Log.d(TAG2,"ServerTask "+"Client has connected");
            DataInputStream DIS = new DataInputStream(client.getInputStream());
            String msg_received = DIS.readUTF();
            client.close();
            serverSocket.close();
            Log.d(TAG2,"ServerTask"+" Server has received the message: "+ msg_received);
            return msg_received;
        } catch (BindException e2){
            Log.d(TAG2,"BindException");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG2,"ServerTask"+" Server failed",e);
            try {
                Thread.sleep(50);
            }catch (Exception e1){

            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EXTRA_MESSAGE,result);
        i.putExtra(DMAC, MainActivity.DMAC);
        i.putExtra(DNAME,MainActivity.DNAME);

        Log.d(TAG2,"DNAME:"+MainActivity.DNAME+"  DMAC:"+MainActivity.DMAC+"  EXTRA_MESSAGE"+result);
        context.startActivity(i);
    }
}