package edu.uci.ics.fabflixmobile;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends ActionBarActivity {
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle bundle = getIntent().getExtras();
        Toast.makeText(this, bundle.get("message") + ".", Toast.LENGTH_LONG).show();

        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        mListView = (ListView) findViewById(R.id.list_view);

        String[] listItems = new String[10];
// 3
        for(int i = 0; i < 10; i++){
            listItems[i] = Integer.toString(i);
        }
// 4
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);
    }
}
