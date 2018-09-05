package com.conext;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conext.db.SQLiteDBHelper;
import com.conext.model.ProfileAlumni;
import com.conext.ui.CreateEventActivity;
import com.conext.ui.Fragments.EventsFragment;
import com.conext.ui.Fragments.MyMenteesFragment;
import com.conext.ui.Fragments.MyMentorsFragment;
import com.conext.ui.Fragments.NetworkFragment;
import com.conext.ui.Fragments.NotificationFragment;
import com.conext.ui.ProfileActivity;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.Prefs;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView headerText;
    // private PorterShapeImageView porterShapeImageView;
    private HexagonMaskView porterShapeImageView;
    private SQLiteDBHelper sqLiteDBHelper;

    int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqLiteDBHelper = new SQLiteDBHelper(this);

        navigationDrawer();

        navigation();

        Menu menu = navigationView.getMenu();

        MenuItem university_menu = menu.findItem(R.id.university_menu);
        MenuItem education_strategy = menu.findItem(R.id.education_strategy);
        MenuItem menu_year = menu.findItem(R.id.menu_year);

        ArrayList<ProfileAlumni> profileAlumniArrayList = sqLiteDBHelper.getAluminiDetails(Prefs.getUserKey());

        ProfileAlumni profileAlumni = profileAlumniArrayList.get(profileAlumniArrayList.size() - 1);

        university_menu.setTitle(profileAlumni.getAlumniUniversity());
        university_menu.setEnabled(false);
        education_strategy.setTitle(profileAlumni.getAlumniStudy());
        education_strategy.setEnabled(false);
        menu_year.setTitle(profileAlumni.getAlumniYear());
        menu_year.setEnabled(false);

        Drawable drawable = toolbar.getNavigationIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (selectedPosition != 0) {
            displaySelectedScreen(R.id.network_view);
        } else {
            /*new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();*/
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //default
        Drawable defaultDrawable = toolbar.getMenu().findItem(R.id.net_img).getIcon();
        if (defaultDrawable != null) {
            defaultDrawable.mutate();
            defaultDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Drawable networkDrawable;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.net_img:
                fragment = networkFragment();
                item.setChecked(true);
                selectedPosition = 0;
                navigationView.getMenu().getItem(0).setChecked(true);
                networkDrawable = item.getIcon();
                if (networkDrawable != null) {
                    networkDrawable.mutate();
                    networkDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                toolbar.getMenu().findItem(R.id.event_switch).setIcon(R.drawable.event_first);
                toolbar.getMenu().findItem(R.id.notification_tool).setIcon(R.drawable.notify_first);
                break;
            // action with ID action_settings was selected
            case R.id.event_switch:
                eventFragment(0);
                item.setChecked(true);
                selectedPosition = 1;
                navigationView.getMenu().getItem(1).setChecked(true);
                Drawable eventDrawable = item.getIcon();
                if (eventDrawable != null) {
                    eventDrawable.mutate();
                    eventDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                toolbar.getMenu().findItem(R.id.net_img).setIcon(R.drawable.net_first);
                toolbar.getMenu().findItem(R.id.notification_tool).setIcon(R.drawable.notify_first);
                break;
            case R.id.notification_tool:
                fragment = new NotificationFragment();
                item.setChecked(true);
                selectedPosition = 7;
                Drawable notificationDrawable = item.getIcon();
                if (notificationDrawable != null) {
                    notificationDrawable.mutate();
                    notificationDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                toolbar.getMenu().findItem(R.id.net_img).setIcon(R.drawable.net_first);
                toolbar.getMenu().findItem(R.id.event_switch).setIcon(R.drawable.event_first);
                navigationView.getMenu().findItem(R.id.network_view).setChecked(true);
                break;
            default:
                fragment = networkFragment();
                item.setChecked(true);
                selectedPosition = 0;
                navigationView.getMenu().getItem(0).setChecked(true);
                networkDrawable = item.getIcon();
                if (networkDrawable != null) {
                    networkDrawable.mutate();
                    networkDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                toolbar.getMenu().findItem(R.id.event_switch).setIcon(R.drawable.event_first);
                toolbar.getMenu().findItem(R.id.notification_tool).setIcon(R.drawable.notify_first);
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.commit();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("result", "got result 0 ,Don't Remove");
    }


    private void navigation() {
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.network_view);

        View header = navigationView.getHeaderView(0);

        RelativeLayout linearLayoutUserProfile = (RelativeLayout) header.findViewById(R.id.header);
        headerText = (TextView) header.findViewById(R.id.name);
        porterShapeImageView = (HexagonMaskView) header.findViewById(R.id.image_view_profile_pic);
        porterShapeImageView.setRadius(1000f);
        porterShapeImageView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        headerText.setText(sqLiteDBHelper.getUserEmail(Prefs.getUserKey()));
        porterShapeImageView.setImageBitmap(sqLiteDBHelper.getUserImage(Prefs.getUserKey()));

        linearLayoutUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                menuItem.setChecked(true);
                //Closing drawer on item click
                drawerLayout.closeDrawers();

                displaySelectedScreen(menuItem.getItemId());

                return true;

            }
        });
        navigationView.setItemIconTintList(null);

        displaySelectedScreen(R.id.network_view);

        MenuItem myEventsMenuItem = navigationView.getMenu().findItem(R.id.my_event);
        Drawable myEventsIcon = myEventsMenuItem.getIcon();

        if (myEventsIcon != null) {

            myEventsIcon.mutate();
            myEventsIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.event_color), PorterDuff.Mode.SRC_ATOP);

        }
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.network_view:
                fragment = networkFragment();
                selectedPosition = 0;

                navigationView.getMenu().findItem(R.id.network_view).setChecked(true);

                MenuItem nMenuItem1 = toolbar.getMenu().findItem(R.id.net_img);
                MenuItem eMenuItem1 = toolbar.getMenu().findItem(R.id.event_switch);
                MenuItem notiMenuItem1 = toolbar.getMenu().findItem(R.id.notification_tool);
                Drawable nDrawable1;
                if (nMenuItem1 != null && eMenuItem1 != null && notiMenuItem1 != null) {
                    nDrawable1 = nMenuItem1.getIcon();
                    eMenuItem1.setIcon(R.drawable.event_first);
                    notiMenuItem1.setIcon(R.drawable.notify_first);
                    if (nDrawable1 != null) {
                        nDrawable1.mutate();
                        nDrawable1.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                break;
            case R.id.event:
                eventFragment(0);
                selectedPosition = 1;
                MenuItem nMenuItem2 = toolbar.getMenu().findItem(R.id.net_img);
                MenuItem eMenuItem2 = toolbar.getMenu().findItem(R.id.event_switch);
                MenuItem notiMenuItem2 = toolbar.getMenu().findItem(R.id.notification_tool);
                Drawable eDrawable2;
                if (nMenuItem2 != null && eMenuItem2 != null && notiMenuItem2 != null) {
                    eDrawable2 = eMenuItem2.getIcon();
                    nMenuItem2.setIcon(R.drawable.net_first);
                    notiMenuItem2.setIcon(R.drawable.notify_first);
                    if (eDrawable2 != null) {
                        eDrawable2.mutate();
                        eDrawable2.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                break;
            case R.id.my_event:
                selectedPosition = 11;
                eventFragment(1);
                MenuItem nMenuItem21 = toolbar.getMenu().findItem(R.id.net_img);
                MenuItem eMenuItem21 = toolbar.getMenu().findItem(R.id.event_switch);
                MenuItem notiMenuItem21 = toolbar.getMenu().findItem(R.id.notification_tool);
                Drawable eDrawable21;
                if (nMenuItem21 != null && eMenuItem21 != null && notiMenuItem21 != null) {
                    eDrawable21 = eMenuItem21.getIcon();
                    nMenuItem21.setIcon(R.drawable.net_first);
                    notiMenuItem21.setIcon(R.drawable.notify_first);
                    if (eDrawable21 != null) {
                        eDrawable21.mutate();
                        eDrawable21.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    }
                }

                break;
            case R.id.my_mentees:
                fragment = new MyMenteesFragment();

                selectedPosition = 2;
                MenuItem nMenuItem3 = toolbar.getMenu().findItem(R.id.net_img);
                MenuItem eMenuItem3 = toolbar.getMenu().findItem(R.id.event_switch);
                MenuItem notiMenuItem3 = toolbar.getMenu().findItem(R.id.notification_tool);
                if (nMenuItem3 != null && eMenuItem3 != null && notiMenuItem3 != null) {
                    nMenuItem3.setIcon(R.drawable.net_first);
                    eMenuItem3.setIcon(R.drawable.event_first);
                    notiMenuItem3.setIcon(R.drawable.notify_first);
                }
                break;
            case R.id.my_mentors:
                fragment = new MyMentorsFragment();
                selectedPosition = 3;
                MenuItem nMenuItem4 = toolbar.getMenu().findItem(R.id.net_img);
                MenuItem eMenuItem4 = toolbar.getMenu().findItem(R.id.event_switch);
                MenuItem notiMenuItem4 = toolbar.getMenu().findItem(R.id.notification_tool);
                if (nMenuItem4 != null && eMenuItem4 != null && notiMenuItem4 != null) {
                    nMenuItem4.setIcon(R.drawable.net_first);
                    eMenuItem4.setIcon(R.drawable.event_first);
                    notiMenuItem4.setIcon(R.drawable.notify_first);
                }
                break;
            case R.id.create_event:
                //  new SQLiteDBHelper(MainActivity.this).deleteEvents();
                Intent createEventIntent = new Intent(new Intent(MainActivity.this, CreateEventActivity.class));
                createEventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(createEventIntent);
                break;
            case R.id.create_mentorship:
                selectedPosition = 5;
                 /* fragment=new CreateEventActivity();*/
                break;

            default:
                selectedPosition = 0;
                fragment = new NetworkFragment();
                break;
        }

        if (fragment != null) {
            if ((fragment instanceof EventsFragment)) {
                return;
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.commit();
        }

    }

    private void navigationDrawer() {
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


    }

    private void eventFragment(int index) {

        Fragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment, "event");
        ft.commit();
    }

    private Fragment networkFragment() {
        return new NetworkFragment();
    }

}
