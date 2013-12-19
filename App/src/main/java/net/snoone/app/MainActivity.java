package net.snoone.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

public class MainActivity extends ActionBarActivity {

    //openfire server 連線參數
    public static ConnectionConfiguration config;
    public static XMPPConnection connection;
    public static SharedPreferences prefs;

    // 偏好資訊(登入帳密、主機資訊)
    public static String ACCOUNT_NAME;
    public static String ACCOUNT_PASSWORD;
    public static String HOST_ADDRESS;
    public static String HOST_PORT;

    //登入成功旗標
    public static boolean loginFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            prefs = getSharedPreferences("schat1Prefs", 0);// Load  Preferences
            loadPrefs();
        }catch (Exception e){
            Log.e("schat1", "getSharedPreferences 失敗!"+e.toString());
        }
        String conntResult = myConnect();//連線嘗試
        if(loginFlag==true){
            //若連線成功跳主聊天夜
            getSupportFragmentManager().beginTransaction().add(R.id.container, new fragment_main(), "fragment_main").commit();
        }else {
            //若連線失敗跳設定頁修正
            getSupportFragmentManager().beginTransaction().add(R.id.container, new class_setting(), "class_setting_null").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //頁面選單
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(loginFlag==false){
            return false;//如果登入不成功不要允許切換其他頁面，可能會導致程式崩潰跳出
        }
        int id = item.getItemId();
        String title = String.valueOf(item.getTitle());
        Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
        if (title.equals("偏好設定")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_setting(), "class_setting");
            ft.addToBackStack(null);
            ft.commit();
        }else if(title.equals("廣播發送")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_broadcast(), "class_broadcast");
            ft.addToBackStack(null);
            ft.commit();
        }else if(title.equals("搜尋用戶")){
            Log.i("schat1", title);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new class_searchusers(), "class_searchusers");
            ft.addToBackStack(null);
            ft.commit();
        }else if (title.equals("使用說明")){
            //尚未使用
            Log.i("schat1", title);
        }else {
            //Log.i("schat1", title);
            Log.i("schat1", "Menu Item ID:" + id);
        }
        return super.onOptionsItemSelected(item);
    }

    //讀取SharedPreferences
    public static void loadPrefs(){
        try {
            ACCOUNT_NAME = prefs.getString("ACCOUNT_NAME", "");//帳號名稱
            ACCOUNT_PASSWORD = prefs.getString("ACCOUNT_PASSWORD","");//帳號密碼
            HOST_ADDRESS = prefs.getString("HOST_ADDRESS", "");//主機位址
            HOST_PORT = prefs.getString("HOST_PORT", "5222");//主機埠號
            Log.i("schat1", "SharedPreferences:"+prefs.getAll().toString());
        }catch (Exception e){
            Log.i("schat1", "SharedPreferences載入錯誤:" + e.toString());
        }
    }

    //連線
    public static String myConnect(){
        Log.i("schat1", "myConnect Start");
        try {
            //pm_configure(ProviderManager.getInstance());
            MainActivity.config = new ConnectionConfiguration(MainActivity.HOST_ADDRESS, Integer.parseInt(MainActivity.HOST_PORT));
            MainActivity.config.setReconnectionAllowed(true);//允許自動連接
            MainActivity.config.setSendPresence(true);
            MainActivity.connection = new XMPPConnection(MainActivity.config);
            MainActivity.connection.DEBUG_ENABLED = true;
        }catch (Exception e){
            MainActivity.loginFlag = false;
            return e.toString();
        }
        //重新連線看看
        try {
            MainActivity.connection.disconnect();//先斷(所有頁面連接均不斷，集中於此處斷接)
            MainActivity.connection.connect();//開始連接
            MainActivity.connection.login(MainActivity.ACCOUNT_NAME, MainActivity.ACCOUNT_PASSWORD);//登入帳號
            pm_configure(ProviderManager.getInstance());//一定要執行否則搜尋會導致程式錯誤跳開，原因尚待深入調查
            MainActivity.loginFlag = true;
            Log.i("schat1", "目前登入帳戶:"+connection.getUser().toString());
            return "true";
        } catch (XMPPException e) {
            Log.e("schat1", "帳號登入失敗:"+e.toString());
            MainActivity.loginFlag = false;
            return e.toString();
        }
    }

    //這段式網路抄來的，尚未完全了解。其目的在修正asmack IOProvider修正
    //connection connect 前先執行，主要會影響搜尋功能正確性
    public static void pm_configure(ProviderManager pm)
    {
        Log.i("schat1", "configure(ProviderManager pm)....");
        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try
        {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        }
        catch (ClassNotFoundException e)
        {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
        // Version
        try
        {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        }
        catch (ClassNotFoundException e)
        {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }

}
