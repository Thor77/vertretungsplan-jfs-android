package de.itgdah.vertretungsplan;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.itgdah.vertretungsplan.data.VertretungsplanContract;
import de.itgdah.vertretungsplan.web.FetchVertretungsplanTask;


public class MainActivity extends Activity {

    public String[] mTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mTitles = getResources().getStringArray(R.array.drawer_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity);
        mDrawerList = (ListView) findViewById(R.id.main_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getFragmentManager().beginTransaction()
                .replace(R.id.main_contentframe, new VertretungsplanFragment())
                .commit();
        mContext = getApplicationContext();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class VertretungsplanFragment extends Fragment {


        private Cursor mCursor;

        public VertretungsplanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            Toolbar toolbar=(Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setTitle(getResources().getStringArray(R.array.drawer_titles)[0]);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_fragment_vertretungsplan, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.vertretungsplan_listview);
            String[] mVertretungsplanListColumns = {
                    VertretungsplanContract.Vertretungen.COLUMN_PERIOD,
                    VertretungsplanContract.Vertretungen.COLUMN_CLASS,
                    VertretungsplanContract.Vertretungen.COLUMN_SUBJECT,
                    VertretungsplanContract.Vertretungen.COLUMN_COMMENT,
                    VertretungsplanContract.Vertretungen._ID
            };

            int[] mVertretungsplanListItems = {
                    R.id.textView, R.id.textView2, R.id.textView3,
                    R.id.textView5
            };

            String mSelectionClause = null;
            String[] mSelectionArgs = null;
            mCursor = getActivity().getContentResolver().query(
                    VertretungsplanContract.Vertretungen.CONTENT_URI,
                    mVertretungsplanListColumns,
                    mSelectionClause,
                    mSelectionArgs,
                    null
            );
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.main_fragment_vertretungsplan_listitem,
                    mCursor,
                    mVertretungsplanListColumns, // column names
                    mVertretungsplanListItems, // view ids
                    0);
            new FetchVertretungsplanTask(adapter, mContext).execute();
            listView.setAdapter(adapter);
            return rootView;
        }
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new VertretungsplanFragment();
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_contentframe, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);

        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
