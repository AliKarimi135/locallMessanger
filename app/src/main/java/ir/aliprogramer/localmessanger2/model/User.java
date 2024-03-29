package ir.aliprogramer.localmessanger2.model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.InetAddress;

import ir.aliprogramer.localmessanger2.ClientClass;
import ir.aliprogramer.localmessanger2.MessageServer;

public class User {
    private static final String TAG2 = "UserClass";
    public static void sendMessage(String msg, InetAddress serverAddress){
        Log.d(TAG2,"User "+"Sending message: "+msg+" to: " +serverAddress.toString());
        ClientClass sender = new ClientClass(serverAddress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg);
            Log.d(TAG2,"MSG0:"+msg);
        }
        else {
            sender.execute(msg);
            Log.d(TAG2,"MSG1:"+msg);
        }
    }

    public static void receiveMessage(Context context){
        Log.d(TAG2,"User "+"Starting messageserver");
        MessageServer server = new MessageServer(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        else
            server.execute((Void[])null);
    }
}
