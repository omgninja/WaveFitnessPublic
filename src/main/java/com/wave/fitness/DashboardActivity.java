package com.wave.fitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.maps.model.Dash;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import studios.codelight.smartloginlibrary.SmartLoginConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.id.list;

public class DashboardActivity extends AppCompatActivity implements Animation.AnimationListener {

    Drawer menu;
    SharedPreferences prefs = null;

    Animation animFadein;

    SpotifyCore core;

    String userFirstName = "John";
    String userLastName = "Blogs";
    String userEmail = "johnblogs@gmail.com";
    String profileID = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        core = ((SpotifyCore)getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.standard);

        animFadein.setAnimationListener(this);

        createUserInfo();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(userFirstName + userLastName).withEmail(userEmail).withIcon(getResources().getDrawable(R.drawable.temp_profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem dashboard = new PrimaryDrawerItem().withIdentifier(1).withName("Dashboard");
        SecondaryDrawerItem run = new SecondaryDrawerItem().withIdentifier(2).withName("Start A Run");
        SecondaryDrawerItem music = new SecondaryDrawerItem().withIdentifier(3).withName("Music");
        SecondaryDrawerItem past = new SecondaryDrawerItem().withIdentifier(4).withName("Previous Runs");
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(5).withName("Settings");
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(6).withName("Logout");

         menu = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        dashboard,run,music,past, new DividerDrawerItem(),settings,logout
                )
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener(){
                             @Override
                             public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                 startActivity(new Intent(DashboardActivity.this, MusicPlayerActivity.class));
                                 return true;
                             }
                        }
                )
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        menu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        SimpleDateFormat parseFormat = new SimpleDateFormat("EEEE");
        Date date =new Date();
        String dashboardDate = parseFormat.format(date);

        Random prefix = new Random();
        String[] prefixs = new String[] { "Happy ", "It's ",
                "Don't you just love ", "Let's get you through ", "Welcome to "};

        String prefixString = "Happy ";

        int INDEXn = prefix.nextInt(prefixs.length);
        for (int i2 = 0; i2 < INDEXn; i2++) {
            prefixString = (String) (prefixs[INDEXn]);
        }

        Random endSent = new Random();
        String[] endSentence = new String[] {"!", "!", "?", ".", "."};

        String endSentString = "!";

        for (int i2 = 0; i2 < INDEXn; i2++) {
            endSentString = (String) (endSentence[INDEXn]);
        }

        TextView curDate = (TextView)findViewById(R.id.dashDate);
        curDate.setText(prefixString + dashboardDate + ", Josh" + endSentString);
    }

    protected void createUserInfo() {
        userFirstName = core.firstName;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {

            Intent firstTime = new Intent(DashboardActivity.this, startupActivity.class);
            DashboardActivity.this.startActivity(firstTime);
        }
    }

    public void onRunButtonClicked(View view) {
        startActivity(new Intent(DashboardActivity.this, AuthActivity.class));
    }

    @Override
    public void onBackPressed() {
        if(menu.isDrawerOpen()){
            menu.closeDrawer();
        }
        else{
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade in animation
        if (animation == animFadein) {
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

}
