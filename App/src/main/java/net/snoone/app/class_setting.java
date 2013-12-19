package net.snoone.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;

/**
 * Created by hl on 2013/12/13.
 */
public class class_setting extends Fragment {

    EditText txtAccount;
    EditText txtPassword;
    EditText txtHostAddress;
    EditText txtHostPort;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("schat1", "class_setting on Create Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("schat1", "class_setting on Create View Fragment");
        return inflater.inflate(R.layout.layout_setting, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("schat1", "class_setting on Start Fragment");

        //儲存按鈕事件
        final Button btnSavePrefs = (Button)getView().findViewById(R.id.btnSavePrefs);
        btnSavePrefs.setOnClickListener(new btnSavePrefsClick());
        //回主頁按鈕事件
        final  Button btnBackHome = (Button)getView().findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(new btnBackHomeClick());

        //編輯文字塊物件實體
        txtAccount = (EditText)getView().findViewById(R.id.edtAccount);
        txtPassword = (EditText)getView().findViewById(R.id.edtPassword);
        txtHostAddress = (EditText)getView().findViewById(R.id.edtHostAddress);
        txtHostPort = (EditText)getView().findViewById(R.id.edtHostPort);

        // 載入預設偏好(預設偏好在Main Activity onCreate 階段即讀入公開變數)
        // 將變數內容顯示對應編輯文字塊即可
        txtAccount.setText(MainActivity.ACCOUNT_NAME);
        txtPassword.setText(MainActivity.ACCOUNT_PASSWORD);
        txtHostAddress.setText(MainActivity.HOST_ADDRESS);
        txtHostPort.setText(MainActivity.HOST_PORT);
    }

/*========================= Class ====================================*/

    //儲存偏好設定
    public class btnSavePrefsClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor prefsEdit = MainActivity.prefs.edit();//起始編輯偏好
            //寫入使用者編輯的偏好
            prefsEdit.putString("ACCOUNT_NAME", txtAccount.getText().toString()); // 寫入偏好
            prefsEdit.putString("ACCOUNT_PASSWORD", txtPassword.getText().toString());
            prefsEdit.putString("HOST_ADDRESS", txtHostAddress.getText().toString());
            prefsEdit.putString("HOST_PORT", txtHostPort.getText().toString());
            prefsEdit.commit();
            Log.i("schat1", "寫入偏好完畢!!");
            MainActivity.prefs = getActivity().getSharedPreferences("schat1Prefs", 0);//重新讀取偏好不需重啟程式
            MainActivity.loadPrefs();
            MainActivity.myConnect();//重新連線看看
        }
    }

    //返回首頁事件處理
    public class btnBackHomeClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            MainActivity.prefs = getActivity().getSharedPreferences("schat1Prefs", 0);//重新讀取偏好
            MainActivity.loadPrefs();
            MainActivity.myConnect();
            if(MainActivity.loginFlag==false){
                return;
            }
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            try {
                ft.replace(R.id.container, getFragmentManager().findFragmentByTag("fragment_main"));
                ft.addToBackStack(null);
                ft.commit();
            }catch (Exception e){
                ft.replace(R.id.container, new fragment_main());
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }
}


