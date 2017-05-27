package edu.uci.ics.fabflixmobile;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;

import edu.uci.ics.fabflixmobile.login.MyHTTPRequest;

public class SearchActivity extends ActionBarActivity {
    private ListView mListView;
    private EditText mEditText;
    private SearchTask mSearchTask = null;
    private String errorMessage;
    private String searchInput;
    private ArrayList<String> movieList;
    private final String url = "http://54.200.163.127:8080/fabflix/Wyd70lJX0W/A_Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEditText = (EditText) findViewById(R.id.search_edit_text);
        mListView = (ListView) findViewById(R.id.list_view);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    movieList = new ArrayList<>();
                    searchInput = mEditText.getText().toString();
                    mSearchTask = new SearchTask();
                    mSearchTask.execute(url);
                    return true;
                }
                return false;
            }
        });
    }

    public void onClickSearch (View view) {
        movieList = new ArrayList<>();
        searchInput = mEditText.getText().toString();
        mSearchTask = new SearchTask();
        mSearchTask.execute(url);
    }

    private class SearchTask extends AsyncTask<String, Integer, Boolean> {
        private Exception exception;

        @Override
        protected  Boolean doInBackground(String... URLs) {
            try {
                ArrayList<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("query", searchInput));
                String result = MyHTTPRequest.executeHttpPost(URLs[0], postParameters);
//                String result = MyHTTPRequest.executeHttpGet(URLs[0]);
                JSONArray resultJson = new JSONArray(result);
                if (resultJson.length() == 0){
                    movieList.add("No Result");
                    return true;
                }
                else {
                    for (int i = 0; i < resultJson.length(); i++) {
                        movieList.add(resultJson.getString(i));
                    }
                    return false;
                }
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean empty) {
            mSearchTask = null;
            ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this, android.R.layout.simple_list_item_1, movieList);
            mListView.setAdapter(adapter);

            if (exception != null) {
                errorMessage = "Error: " + this.exception.toString();
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
