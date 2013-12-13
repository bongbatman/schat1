package net.snoone.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class MainActivity extends ActionBarActivity {

    public static ConnectionConfiguration config;
    public static XMPPConnection connection;
    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new fragment_main())
                    .commit();
        }

        // Preferences
        prefs = getSharedPreferences("schat1Prefs", 0);
        Log.i("schat1", "SharedPreferences:"+prefs.getAll().toString());

        // 連線主機
        config = new ConnectionConfiguration("192.168.1.31", 5222, "hl");
        config.setReconnectionAllowed(true);//允許自動連接
        config.setSendPresence(true);
        connection = new XMPPConnection(config);
        connection.DEBUG_ENABLED = true;
        try {
            connection.connect();//開啟連接
            connection.login("test1", "test1");//登入帳號
        } catch (XMPPException e) {
            Toast.makeText(this, "帳號登入失敗:"+e.toString(), Toast.LENGTH_LONG).show();
            Log.e("schat1", e.toString());
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String title = String.valueOf(item.getTitle());
        //Toast.makeText(MainActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
        if (title.equals("偏好設定")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_setting(), "Setting Fragment");
            ft.addToBackStack(null);
            ft.commit();
        }else if(title.equals("廣播發送")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_broadcast(), "BroadCast Fragment");
            ft.addToBackStack(null);
            ft.commit();
        }else if(title.equals("搜尋用戶")){
            Log.i("schat1", title);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_searchusers(), "SearchUsers Fragment");
            ft.addToBackStack(null);
            ft.commit();
        }else if (title.equals("使用說明")){
            Log.i("schat1", title);
        }else {
            //Log.i("schat1", title);
            Log.i("schat1", "Menu Item ID:" + id);
        }
/*
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void changeText(String str){
        TextView talkShow = (TextView)this.findViewById(R.id.talk_show);
        talkShow.setText(talkShow.getText() + str);
    }

}
