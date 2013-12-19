package net.snoone.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;

import org.jivesoftware.smackx.ReportedData;

import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by hl on 2013/12/5.
 */
public class class_searchusers extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("frag1", "on Create Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("frag1", "on Create View Fragment");
        return inflater.inflate(R.layout.layout_searchusers, container, false);
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
        SearchView searchView = (SearchView)getView().findViewById(R.id.view_search_users);
        searchView.setOnQueryTextListener(new QuerySearch());
    }

    // Class - Search OnQueryTextListener
    public class QuerySearch implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String s) {
            try {
                //Log.i("schat1", entries.toString());
                Log.i("schat1", "QuerySearch....");
                ListView listView = (ListView)getView().findViewById(R.id.listView1);
                ArrayList<String> listSource = new ArrayList<String>();
                ProviderManager.getInstance().addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
                Log.i("schat1", "ProviderManager....");
                UserSearchManager userSearchManager = new UserSearchManager(MainActivity.connection);
                Log.i("schat1", "UserSearchManager....");
                Form searchForm = userSearchManager.getSearchForm("search." + MainActivity.connection.getServiceName());
                Log.i("schat1", "getSearchForm....");
                //Log.i("schat1", searchForm.getTitle());
                Form answerForm = searchForm.createAnswerForm();
                answerForm.setAnswer("search", s);
                answerForm.setAnswer("Username", true);
                answerForm.setAnswer("Name", true);
                answerForm.setAnswer("Email", true);
                ReportedData reportedData = userSearchManager.getSearchResults(answerForm , "search." + MainActivity.connection.getServiceName());
                Iterator<ReportedData.Row> rows = reportedData.getRows();
                while (rows.hasNext()){
                    ReportedData.Row row =rows.next();
                    Iterator<String> jids = row.getValues("Name");
                    while (jids.hasNext()){
                        //Log.i("schat1", jids.next());
                        listSource.add(jids.next());
                    }
                }
                listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, listSource));
                listView.setTextFilterEnabled(true);
                Log.i("schat1", listSource.toString());
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    }
}
