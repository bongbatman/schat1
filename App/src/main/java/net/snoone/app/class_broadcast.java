package net.snoone.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
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

    ListView listView;
    ArrayList<String> friendsGroup;

    ConnectionConfiguration config;
    XMPPConnection connection;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("frag1", "on Create Fragment");

        // 連線主機
        config = new ConnectionConfiguration("192.168.1.31", 5222, "hl");
        connection = new XMPPConnection(config);
        connection.DEBUG_ENABLED = false;
        try {
            connection.connect();//開啟連接
            connection.login("test1", "test1");//登入帳號
        } catch (XMPPException e) {
            e.printStackTrace();
        }


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
        // 使用者好友群組列表
        Roster roster = connection.getRoster();
        Collection<RosterGroup> rosterGroups = roster.getGroups();
        friendsGroup = new ArrayList<String>();
        for(RosterGroup rosterGroup:rosterGroups){
            //Log.i("schat1", rosterGroup.getName());
            friendsGroup.add(rosterGroup.getName());
        }
        String[] mStringArray = new String[friendsGroup.size()];
        mStringArray = friendsGroup.toArray(mStringArray);
        listView = (ListView)getView().findViewById(R.id.listView1);
        listView.setAdapter(new itemAdapter(getActivity(), mStringArray));
        listView.setOnItemClickListener(new listViewItemClick());

    }

/*======================== Func ===============================*/


/*======================== Class ===============================*/

    // Click event
    //發送廣播訊息事件
    public class broadcast implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.i("schat1", "BroadCast Click!!");
            View v = (View) view.getParent();
            TextView msgText = (TextView) v.findViewById(R.id.txt_msg);
            //msgText.append("傳送了\n");
            EditText sendBody = (EditText)v.findViewById(R.id.etxt_broadcast);//使用者輸入-訊息文本
            String bodyStr =  sendBody.getText().toString();//傳送訊息內容
            //我的好友
            Roster roster = connection.getRoster();
            Collection<RosterEntry> entries = roster.getEntries();
            Log.i("schat1", entries.toString());
            for (RosterEntry entry:entries){
                Log.i("schat1", entry.toString());
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

    // 配接 客制項目給 ListView
    public class itemAdapter extends BaseAdapter{

        Context context;
        String[] data;
        private LayoutInflater inflater = null;

        public itemAdapter(Context context, String[] data){
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi = view;
            if(vi==null){
                vi = inflater.inflate(R.layout.layout_item, null);
            }
            CheckedTextView checkedTextView = (CheckedTextView)vi.findViewById(R.id.check1);
            checkedTextView.setText(data[i]);
            return vi;
        }
    }

    // ListView Click 事件
    public class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CheckedTextView chkItem = (CheckedTextView)view.findViewById(R.id.check1);
            chkItem.setChecked(!chkItem.isChecked());
            Log.i("schat1", "你點選了"+(i+1));

        }
    }

}

