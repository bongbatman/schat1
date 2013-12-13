package net.snoone.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hl on 2013/12/13.
 */
public class class_setting extends Fragment {

    EditText txtAccount;
    EditText txtPassword;
    EditText txtHostAddress;
    EditText txtHostPort;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.i("frag1", "on Attach Fragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("frag1", "on Create Fragment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("frag1", "on Create View Fragment");
        return inflater.inflate(R.layout.layout_setting, container, false);
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
        final Button btnSavePrefs = (Button)getView().findViewById(R.id.btnSavePrefs);
        btnSavePrefs.setOnClickListener(new btnSavePrefsClick());

        txtAccount = (EditText)getView().findViewById(R.id.edtAccount);
        txtPassword = (EditText)getView().findViewById(R.id.edtPassword);
        txtHostAddress = (EditText)getView().findViewById(R.id.edtHostAddress);
        txtHostPort = (EditText)getView().findViewById(R.id.edtHostPort);

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("frag1", "on Resume Fragment");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i("frag1", "on Pause Fragment");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.i("frag1", "on Save Instance State Fragment");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("frag1", "on Stop Fragment");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.i("frag1", "on Destroy View Fragment");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("frag1", "on Destroy Fragment");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.i("frag1", "on Detach Fragment");
    }

/*========================= Class ====================================*/

    //儲存偏好設定
    public class btnSavePrefsClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor prefsEdit = MainActivity.prefs.edit();//起始編輯偏好
            //寫入使用者編輯的偏好
            prefsEdit.putString("ACCOUNT_NAME", txtAccount.getText().toString());
            prefsEdit.commit();
            Log.i("schat1", "寫入偏好完畢!!");
        }
    }

/*========================= Func ====================================*/


}


