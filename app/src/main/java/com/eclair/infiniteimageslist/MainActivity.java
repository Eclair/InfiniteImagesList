package com.eclair.infiniteimageslist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.eclair.infiniteimageslist.adapters.InfiniteImageListAdapter;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listView = (ListView)this.findViewById(R.id.listView);

        InfiniteImageListAdapter adapter = new InfiniteImageListAdapter(this);
        this.listView.setAdapter(adapter);
        this.listView.setOnScrollListener(adapter);
    }
}
