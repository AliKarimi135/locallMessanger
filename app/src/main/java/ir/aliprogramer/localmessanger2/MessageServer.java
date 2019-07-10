package ir.aliprogramer.localmessanger2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import ir.aliprogramer.localmessanger2.activity.ChatActivity;

import ir.aliprogramer.localmessanger2.model.ChatMessage;

import static ir.aliprogramer.localmessanger2.activity.ChatActivity.chatAdapter;

public class MessageServer extends ServerTask {
    private static final String TAG2 = "MessageServer";
    public MessageServer(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(Void... params){

        String msg = super.doInBackground();

        Log.d(TAG2,"MessageServer "+"MessageServer started");
        Log.d(TAG2,"MessageServer "+"MessageServer received: " + msg);
        return msg;
    }

    @Override
    protected void onPostExecute(String result){
        Log.d(TAG2,"MessageServer "+"MessageServer received message: "+result);
        if(result!=null)
            if(result.length()==29 && result.contains("sgk.macTable=")){
                String tablename=result.substring(13,29);

                ChatActivity.macTableName=tablename;
            }else if(result.contains("sgk.device.myName="))
            {

                String otherDevice=result.substring(18,result.length());
                //Toast.makeText(context,otherDevice,Toast.LENGTH_SHORT).show();
                ChatActivity.setOtherDeviceName(otherDevice);
                Log.d("sgk","sgk.device.myName="+otherDevice+"=----post");
            }
            else {
        chatArrayAdapter.add(new ChatMessage(false, result));
			}

        MessageServer server = new MessageServer(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        else
            server.execute((Void[])null);
    }
}