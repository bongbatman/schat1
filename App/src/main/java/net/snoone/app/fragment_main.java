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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by hl on 2013/11/26.
 */
public class fragment_main extends Fragment{

    TextView msgText;
    TextView talkShow;
    RefreshMessage refreshMessage;
    AutoCompleteTextView autoCompleteTextView;

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
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("schat1", "fragment_main onStart()");

        autoCompleteTextView = (AutoCompleteTextView)getView().findViewById(R.id.autoCompleteTextView);

        //聊天對象變更
        autoCompleteTextView.addTextChangedListener(new chatUserChange());

        //手動發送訊息
        final View btnSend = getView().findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new onClickListener());

        // 聊天連線
        refreshMessage = new RefreshMessage();
        refreshMessage.start();

        RosterAutoComplete();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.i("schat1", "fragment_main onDetach()");
    }

/*========================= Class ====================================*/
    // Thread
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            talkShow.setText(talkShow.getText() + msg.getData().getString("message", ""));
        }
    };
    public class RefreshMessage extends Thread{
        @Override
        public void run(){
            super.run();
            if(MainActivity.loginFlag==false){
                Toast.makeText(getActivity(), "Login fail:", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i("schat1", MainActivity.connection.getUser());
            ChatManager chatManager = MainActivity.connection.getChatManager(); // Create Chat manager
            String chatTarget = autoCompleteTextView.getText().toString(); //取得欲建立聊天的對象
            if(chatTarget.equals(null) || chatTarget.equals("")){
                chatTarget = "test1@of1";
            }
            Log.i("schat1", "目前聊天對象:"+chatTarget);
            Chat newChat = chatManager.createChat(chatTarget, new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    if (message.getBody() != null){
                        Log.i("schat1", "Received from:" + message.getFrom() + " Message:" + message.getBody());
                        Bundle msgBundle = new Bundle();
                        msgBundle.putString("message", message.getFrom() + "==>" + message.getTo() + ": " + message.getBody() + "\n");
                        android.os.Message msg = new android.os.Message();
                        msg.setData(msgBundle);
                        mHandler.sendMessage(msg);
                    }
                }
            });
        }
    }

    // button click event
    public class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(MainActivity.loginFlag==false){
                Toast.makeText(getActivity(), "Login fail:", Toast.LENGTH_LONG).show();
                return;
            }
            View v = (View) view.getParent();
            TextView msgText = (TextView) v.findViewById(R.id.msg);
            //msgText.append("傳送了\n");
            EditText sendBody = (EditText)v.findViewById(R.id.edit_message_body);//使用者輸入-訊息文本
            String bodyStr =  sendBody.getText().toString();//傳送訊息內容
            String targetStr =  autoCompleteTextView.getText().toString();//sendTarget.getText().toString();//傳送對象
            if(targetStr.equals("") || targetStr.equals(null)){
                msgText.setText(msgText.getText()+"請輸入傳送對象!\n");
                return;
            }
            Log.i("schat1", targetStr);
            Message message = new Message(targetStr,Message.Type.chat);
            message.setBody(bodyStr);
            MainActivity.connection.sendPacket(message);
            talkShow.setText(talkShow.getText() + MainActivity.connection.getUser() + "==>" + targetStr + ": " + bodyStr + "\n");
            //connection.disconnect();
            msgText.append("傳送成功!\n");
        }
    }

    public class chatUserChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Log.i("schat1", "聊天對象文字-beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Log.i("schat1", "聊天對象文字-onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i("schat1", "聊天對象文字-afterTextChanged");
            //建立與新對象的聊天連線
            refreshMessage = new RefreshMessage();
            refreshMessage.start();
        }
    }

/*========================= Func ====================================*/

    // 取得好友，列入自動文字清單
    public void RosterAutoComplete(){
        if (MainActivity.loginFlag==false){
            return;//沒有連線成功
        }
        ArrayList<String> rosterList = new ArrayList<String>();
        //List member
        Roster roster = MainActivity.connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for(RosterEntry entry:entries){
            rosterList.add(entry.getUser());
        }
        Log.i("schat1", rosterList.toString());
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, rosterList);
        autoCompleteTextView.setAdapter(usersAdapter);
        //autoCompleteTextView.setThreshold(1);
    }


}
