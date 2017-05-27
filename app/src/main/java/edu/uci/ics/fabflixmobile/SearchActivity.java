package edu.uci.ics.fabflixmobile;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private final int MOVIE_PER_PAGE = 5;

    private ListView mListView;
    private EditText mEditText;
    private Button btn_prev;
    private Button btn_next;
    private TextView currPageView;
    private SearchTask mSearchTask = null;
    private String errorMessage;
    private String searchInput;
    private int pageCount;
    private int leftover;
    private int currPage = 0;
    private ArrayList<String> movieList;
    private final String url = "http://54.200.163.127:8080/fabflix/Wyd70lJX0W/A_Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListView = (ListView) findViewById(R.id.list_view);
        currPageView = (TextView) findViewById(R.id.page_text);
        btn_prev = (Button)findViewById(R.id.prev);
        btn_next = (Button)findViewById(R.id.next);
        btn_prev.setEnabled(false);
        btn_next.setEnabled(false);
        mEditText = (EditText) findViewById(R.id.search_edit_text);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
//                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    searchMovie();
                    return true;
                }
                return false;
            }
        });
    }

    private void searchMovie (){
        movieList = new ArrayList<>();
        searchInput = mEditText.getText().toString();
        if (!searchInput.equals("")) {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(SearchActivity.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            mSearchTask = new SearchTask();
            mSearchTask.execute(url);
        }
    }

    // toggle button's availability
    private void toggleButtons()
    {
        if (currPage == 1 && currPage == pageCount) {
            btn_prev.setEnabled(false);
            btn_next.setEnabled(false);
        }
        else {
            if (currPage == pageCount) {
                btn_prev.setEnabled(true);
                btn_next.setEnabled(false);
            } else if (currPage == 1) {
                btn_next.setEnabled(true);
                btn_prev.setEnabled(false);
            } else {
                btn_prev.setEnabled(true);
                btn_next.setEnabled(true);
            }
        }
    }

    /**
     * Method for loading data in listview
     * @param pageNum page number (starts from 1)
     */
    private void loadList(int pageNum)
    {
        ArrayList<String> thisPageMovies = new ArrayList<>();
        currPageView.setText(Integer.toString(pageNum) + "/" + Integer.toString(pageCount));

        int offset = (pageNum - 1) * MOVIE_PER_PAGE; //page numebr starts from 1
        for(int  i = offset; i < offset + MOVIE_PER_PAGE; i++)
        {
            if(i < movieList.size())
                thisPageMovies.add(movieList.get(i));
            else
                break;
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thisPageMovies);
        mListView.setAdapter(adapter);
    }

    public void onClickSearch (View view) {
        searchMovie();
    }

    public void onClickPrev (View view) {
        currPage--;
        loadList(currPage);
        toggleButtons();
    }

    public void onClickNext (View view) {
        currPage++;
        loadList(currPage);
        toggleButtons();
    }

    private class SearchTask extends AsyncTask<String, Integer, Boolean> {
        private Exception exception;

        @Override
        protected  Boolean doInBackground(String... URLs) {
            try {
                ArrayList<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("query", searchInput));
                String result = MyHTTPRequest.executeHttpPost(URLs[0], postParameters);
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
            leftover = movieList.size() % MOVIE_PER_PAGE;
            leftover = (leftover == 0) ? 0 : 1;
            pageCount = movieList.size() / MOVIE_PER_PAGE + leftover;
            btn_prev.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);
            currPageView.setVisibility(View.VISIBLE);
            currPage = 1;
            toggleButtons();
            loadList(1); // load first page after search
            if (exception != null) {
                errorMessage = "Error: " + this.exception.toString();
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
