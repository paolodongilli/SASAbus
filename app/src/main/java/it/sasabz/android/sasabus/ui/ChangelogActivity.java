package it.sasabz.android.sasabus.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.Changelog;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.DeviceUtils;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.ChangelogAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the changelogs of all the app versions which have been released either as production
 * or beta. The changelogs are saved in {@link it.sasabz.android.sasabus.R.string#changelog} in {@code english},
 * {@code german} and {@code italian}.
 *
 * @see it.sasabz.android.sasabus.util.Changelog
 * @author Alex Lardschneider
 */
public class ChangelogActivity extends AppCompatActivity {

    private static final String TAG = "ChangelogActivity";

    private List<Changelog> mItems = new ArrayList<>();
    private ChangelogAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_changelog);

        AnalyticsHelper.sendScreenView(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mItems = new ArrayList<>();
        mAdapter = new ChangelogAdapter(mItems);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.changelog_recycler);
        recyclerView.setHasFixedSize(true);

        if (DeviceUtils.isTablet(this)) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        recyclerView.setAdapter(mAdapter);

        parseData();
    }

    /**
     * Loads the changelog entries from the changelog json files located
     * in /assets.
     */
    private void parseData() {
        try {
            String content = getString(R.string.changelog_content);

            JSONArray array = new JSONArray(content);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                JSONArray changes = object.getJSONArray("CHANGES");
                StringBuilder sb = new StringBuilder();

                for (int j = 0; j < changes.length(); j++) {
                    sb.append("• ").append(changes.getString(j)).append('\n');
                }

                sb.setLength(sb.length() - 1);

                mItems.add(new Changelog(getString(R.string.changelog_version) + ' ' +
                        object.getString("VERSION_NAME"), sb.toString()));
            }

            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Utils.handleException(e);
        }
    }
}