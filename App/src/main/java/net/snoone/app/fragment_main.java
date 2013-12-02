package net.snoone.app;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by hl on 2013/11/26.
 */
public class fragment_main extends Fragment{

    TextView msgText;
    TextView talkShow;
    RefreshMessage refreshMessage;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("schat1", "fragment_main onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        msgText = (TextView)rootView.findViewById(R.id.msg);
        talkShow = (TextView)rootView.findViewById(R.id.talk_show);
        Log.i("schat1", "fragment_main onCreateView()");
/*        //建立接收訊息連線
        ConnectionConfiguration config = new ConnectionConfiguration("192.168.1.31", 5222);
        config.setReconnectionAllowed(true);//允許自動連接
        config.setSendPresence(true);
        XMPPConnection connection = new XMPPConnection(config);
        connection.DEBUG_ENABLED = true;
        try {
            connection.connect();
            connection.login("test1", "test1");
            Log.i("schat1", connection.getUser());
            ChatManager chatManager = connection.getChatManager();
            Chat newChat = chatManager.createChat("t003@of1", new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    if (message.getBody() != null){
                        Log.i("schat1", "Received from:" + message.getFrom() + " Message:" + message.getBody());
                        //msgText = (TextView)getView().findViewById(R.id.msg);
                    }
                }
            });
        }catch (XMPPException ex){
            Log.e("schat1", ex.toString());
        }*/
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("schat1", "fragment_main onStart()");

        refreshMessage = new RefreshMessage();
        refreshMessage.start();

        //手動發送訊息
        final View btnSend = getView().findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new onClickListener());
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public Handler mHandler = new Handler(){
        @Override
    public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            talkShow.setText(talkShow.getText() + "\n" + msg.getData().getString("message", ""));
        }
    };

    public class RefreshMessage extends Thread{
        @Override
        public void run(){
            super.run();
            //建立接收訊息連線
            ConnectionConfiguration config = new ConnectionConfiguration("192.168.1.31", 5222);
            config.setReconnectionAllowed(true);//允許自動連接
            config.setSendPresence(true);
            XMPPConnection connection = new XMPPConnection(config);
            connection.DEBUG_ENABLED = true;
            try {
                connection.connect();
                connection.login("test1", "test1");
                Log.i("schat1", connection.getUser());
                ChatManager chatManager = connection.getChatManager();
                Chat newChat = chatManager.createChat("t003@of1", new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody() != null){
                            Log.i("schat1", "Received from:" + message.getFrom() + " Message:" + message.getBody());
                            Bundle msgBundle = new Bundle();
                            msgBundle.putString("message", "Received from:" + message.getFrom() + " Message:" + message.getBody());
                            android.os.Message msg = new android.os.Message();
                            msg.setData(msgBundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }catch (XMPPException ex){
                Log.e("schat1", ex.toString());
            }
        }
    }
}
