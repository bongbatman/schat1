package net.snoone.app;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;


/**
 * Created by hl on 2013/11/26.
 */
public class onClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        View v = (View) view.getParent();
        TextView msgText = (TextView) v.findViewById(R.id.msg);
        //msgText.append("傳送了\n");
        EditText sendBody = (EditText)v.findViewById(R.id.edit_message_body);//使用者輸入-訊息文本
        EditText sendTarget = (EditText)v.findViewById(R.id.edit_message_target);//使用者輸入-發送對象
        String bodyStr =  sendBody.getText().toString();//傳送訊息內容
        String targetStr =  sendTarget.getText().toString();//傳送對象
        if(targetStr.length()==0){
            sendTarget.setError("請輸入發送目標帳號");
            return;
        }
        // 連線主機資料
        String HOST = "192.168.1.31";
        int PORT = 5222;
        String SERVICE = "jabber";
        ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
        XMPPConnection connection = new XMPPConnection(config);
        try {
            connection.connect();
            connection.login("test1", "test1");//使用登入帳號
            Message message = new Message(targetStr,Message.Type.chat);
            message.setBody(bodyStr);
            connection.sendPacket(message);
            msgText.append("傳送成功!\n");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
