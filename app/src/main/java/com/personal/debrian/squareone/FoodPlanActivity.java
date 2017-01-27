package com.personal.debrian.squareone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import DayFrags.FridayFragment;
import DayFrags.MondayFragment;
import DayFrags.SaturdayFragment;
import DayFrags.SundayFragment;
import DayFrags.ThursdayFragment;
import DayFrags.TuesdayFragment;
import DayFrags.WednesdayFragment;

public class FoodPlanActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        myAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Toast.makeText(getApplicationContext(),"Not Signed In",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FoodPlanActivity.this,Login.class));
                }
            }
        });
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
        }else if(id == R.id.action_signout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FoodPlanActivity.this,Login.class));
            return true;
        }else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0://Sunday Frag
                    SundayFragment sunday = new SundayFragment();
                    return sunday;
                case 1://monday frag
                    MondayFragment monday = new MondayFragment();
                    return monday;
                case 2://tuesday frag
                    TuesdayFragment tuesday = new TuesdayFragment();
                    return tuesday;
                case 3://wednesday frag
                    WednesdayFragment wednesday = new WednesdayFragment();
                    return wednesday;
                case 4://thursday frag
                    ThursdayFragment thursday = new ThursdayFragment();
                    return thursday;
                case 5://friday frag
                    FridayFragment friday = new FridayFragment();
                    return friday;
                case 6://saturday frag
                    SaturdayFragment saturday = new SaturdayFragment();
                    return saturday;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Su";
                case 1:
                    return "M";
                case 2:
                    return "Tu";
                case 3:
                    return "W";
                case 4:
                    return "Th";
                case 5:
                    return "F";
                case 6:
                    return "Sa";
            }
            return null;
        }
    }
}
