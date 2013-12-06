package net.snoone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hl on 2013/12/2.
 */
public class class_broadcast  extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("frag1", "on Create Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("frag1", "on Create View Fragment");
        return inflater.inflate(R.layout.layout_broadcast, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.i("frag1", "on Activity Created Fragment");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("frag1", "on Start Fragment");

        final View btn_broadcast_send = getView().findViewById(R.id.btn_broad_send);
        btn_broadcast_send.setOnClickListener(new broadcast());
    }

    // Click event
    public class broadcast implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.i("schat1", "BroadCast Click!!");

            View v = (View) view.getParent();
            TextView msgText = (TextView) v.findViewById(R.id.txt_msg);
            //msgText.append("傳送了\n");
            EditText sendBody = (EditText)v.findViewById(R.id.etxt_broadcast);//使用者輸入-訊息文本
            String bodyStr =  sendBody.getText().toString();//傳送訊息內容
            // 連線主機資料
            String HOST = "192.168.1.31";
            int PORT = 5222;
            ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
            XMPPConnection connection = new XMPPConnection(config);
            connection.DEBUG_ENABLED = false;
            AccountManager accountManager;
            try {
                connection.connect();//開啟連接
                accountManager = connection.getAccountManager();//獲取帳戶管理
                connection.login("test1", "test1");//登入帳號

                //列印所有成員
                Roster roster = connection.getRoster();
                Collection<RosterEntry> entries = roster.getEntries();
                Log.i("schat1", entries.toString());
                for (RosterEntry entry:entries){
                    Log.i("schat1", entry.toString());
                }

            } catch (XMPPException e) {
                e.printStackTrace();
            }
            Message message = new Message();
            message.setTo("t001@of1");
            message.setSubject("重要通知");
            message.setBody(bodyStr);
            message.setType(Message.Type.headline);
            connection.sendPacket(message);
            connection.disconnect();
            msgText.append("傳送成功!\n");
        }
    }
}

