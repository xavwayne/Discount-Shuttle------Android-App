package com.example.jiyushi1.dis.activity.customer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.util.HashMap;
import java.util.Map;

public class CustomerNavigation extends Activity
        implements NavigationDrawerFragmentCustomer.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentCustomer mNavigationDrawerFragment;
    private  Bundle bd;

    private Handler handler;
    private ProgressDialog progressDialog;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_navigation);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==3){
                    String accountInfo = (String)msg.obj;
                    bd.putString("accountInfo", accountInfo);
                    getIntent().putExtras(bd);
                    progressDialog.dismiss();

                    Fragment obj = new CustomerAccount();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,obj)
                            .commit();
                }
            }
        };

        mNavigationDrawerFragment = (NavigationDrawerFragmentCustomer)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment obj = null;
        FragmentManager fragmentManager;
        switch (position){
            case 0:
                obj = new CustomerMainPage();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container,obj)
                        .commit();
                break;
            case 1:
                bd = getIntent().getExtras();
                String username = bd.getString("username");
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", username);
                progressDialog = ProgressDialog.show(CustomerNavigation.this, "Connecting", "Connecting to Server");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8", 6);
                util.start();
                break;
            case 2:
                obj = new AboutUs();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container,obj)
                        .commit();
                break;
            case 3:
                obj = new Help();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container,obj)
                        .commit();
                break;
            case 4:
                obj = new CustomerPassword();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container,obj)
                        .commit();
                break;
        }


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_customer_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_customer_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_customer_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_customer_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_customer_section5);
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.customer_navigation, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_customer_navigation, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((CustomerNavigation) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    private class HttpConnectDB extends HttpDBServer {
        int number;
        HttpConnectDB(Map<String, String> params, String encode,int requestType){
            super(params,encode,requestType);

        }
        @Override
        public void run() {

                super.run();
                Message message = Message.obtain();
                message.what = 3;
                message.obj = super.getResult();
                handler.sendMessage(message);

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to LOG OFF?");
        builder.setTitle("Hey Dude:");
        builder.setPositiveButton("Sure",
                new android.content.DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        CustomerNavigation.this.finish();
                    }
                });

        builder.setNegativeButton("Cancel",
                new android.content.DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
