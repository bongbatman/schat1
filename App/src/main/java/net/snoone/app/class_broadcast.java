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

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hl on 2013/12/2.
 */
public class class_broadcast  extends Fragment{

    TextView msgText;//系統訊息顯示用
    ListView listView;// 朋友群組顯示列表
    CheckedTextView chkItem;// 客製化清單項目s

    ArrayList<String> friendsGroup;//朋友群組(名稱字串)
    Collection<RosterGroup> rosterGroups;//朋友群組(RosterGroup)
    List<Boolean> SelectedItems;

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

        msgText = (TextView)getView().findViewById(R.id.txt_msg);//定義訊息文字顯示頁面物件

        final View btn_broadcast_send = getView().findViewById(R.id.btn_broad_send);
        btn_broadcast_send.setOnClickListener(new broadcast());
        // 使用者好友群組列表
        Roster roster = MainActivity.connection.getRoster();
        rosterGroups = roster.getGroups();// 取得朋友群組
        friendsGroup = new ArrayList<String>();
        // 逐一取得群組名稱
        for(RosterGroup rosterGroup:rosterGroups){
            //Log.i("schat1", rosterGroup.getName());
            friendsGroup.add(rosterGroup.getName());
        }
        // ArrayList轉String Array
        String[] mStringArray = new String[friendsGroup.size()];
        mStringArray = friendsGroup.toArray(mStringArray);
        listView = (ListView)getView().findViewById(R.id.listView1);//定義清單頁面物件
        listView.setAdapter(new itemAdapter(getActivity(), mStringArray));//配接資料來源
        //SelectedItems = new ArrayList<Boolean>();
        listView.setOnItemClickListener(new listViewItemClick());// 任命ListView按下觸發處理專員
    }

/*======================== Class ===============================*/

    // Click event
    // Sending Broadcast button click event (發送廣播訊息事件)
    public class broadcast implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.i("schat1", "BroadCast Click!!");
            View v = (View) view.getParent();

            //msgText.append("傳送了\n");
            EditText sendBody = (EditText)v.findViewById(R.id.etxt_broadcast);//使用者輸入-訊息文本
            String bodyStr =  sendBody.getText().toString();//傳送訊息內容
            // 沒有任何群組不做
            Log.i("schat1", String.valueOf(SelectedItems.size()));
            if (SelectedItems.size()==0 || SelectedItems.equals(null)){
                Log.i("schat1", "沒有發送對象!");
                msgText.setText(msgText.getText() + "沒有發送對象!");
                return;
            }
            //我的好友
            ArrayList<RosterGroup> rosterGroupList = new ArrayList<RosterGroup>(rosterGroups);// 為了方便存取起見，把Collection轉為ArrayList
            for(int v1=0; v1<SelectedItems.size(); v1++){
                if(SelectedItems.get(v1).equals(true)){
                    Collection<RosterEntry> rosterEntries = rosterGroupList.get(v1).getEntries();
                    for (RosterEntry rosterEntry:rosterEntries){
                        Message message = new Message();
                        message.setTo(rosterEntry.getUser());
                        message.setSubject("重要通知");
                        message.setBody(bodyStr);
                        message.setType(Message.Type.headline);
                        MainActivity.connection.sendPacket(message);
                        msgText.append("傳送訊息給"+rosterEntry.getUser()+"\n");
                    }
                }
            }
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
            //清單項目陣列勾選與否結果儲存陣列物件
            //使用者按下發送訊息時，須依賴此陣列中結果，以便判斷那些群組中的朋友，列為發送對象。
            SelectedItems = new ArrayList<Boolean>();
            for (int v1=0; v1<data.length ; v1++){
                SelectedItems.add(v1, true);//預設每個項目都是勾選的
            }
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

    // ListView Item Click 事件
    public class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            chkItem = (CheckedTextView)view.findViewById(R.id.check1);
            chkItem.setChecked(!chkItem.isChecked());
            //變更選取陣列項目內容
            SelectedItems.set(i, chkItem.isChecked());
            Log.i("schat1", "你點選了"+(i+1));
            Log.i("schat1", SelectedItems.toString());
        }
    }

}

