package com.conext.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conext.Adapter.ViewPagerAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.AllSkill;
import com.conext.ui.Fragments.AppTagsFragment;
import com.conext.ui.Fragments.InfoFragment;
import com.conext.ui.Fragments.MenteeFragment;
import com.conext.ui.Fragments.MentorFragment;
import com.conext.utils.IFragmentToActivity;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.conext.utils.Utility.animateFab;
import static com.conext.utils.Utility.isColorDark;


public class RegistrationPersonaliseActivity extends AppCompatActivity implements IFragmentToActivity, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Toolbar toolbar;
    TextView subtitleTextView;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    ViewPagerAdapter adapter;
    SQLiteDBHelper sqLiteDBHelper;
    MenuItem editMenuItem, doneMenuItem;
    ArrayList<AllSkill> userTagArrayList = null;
    ArrayList<AllSkill> menteeTagArrayList = null;
    ArrayList<AllSkill> mentorTagArrayList = null;
    boolean editMode;
    Uri uriPath = null;
    long key;
    LocationManager manager;

    private int GALLERY_KITKAT_INTENT_CALLED = 11;

    private String accessToken;
    long locationKey = 0;
    Menu menu;
    int menuOption = 0;

    ImageView imageView;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;

    private String address = null;

    Drawable editDrawable, doneDrawable, overflowIcon;
    public FloatingActionButton floatingActionButtonAddInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_personalise);

        if (!getIntent().getExtras().isEmpty())
            key = getIntent().getExtras().getLong("key");

        floatingActionButtonAddInfo = (FloatingActionButton) findViewById(R.id.fab_add);

        //editMode = Prefs.getEditMode();
        userTagArrayList = new ArrayList<>();
        menteeTagArrayList = new ArrayList<>();
        mentorTagArrayList = new ArrayList<>();

        sqLiteDBHelper = new SQLiteDBHelper(RegistrationPersonaliseActivity.this);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Utility.showGPSSettingsAlert(RegistrationPersonaliseActivity.this);
        }

        buildGoogleApiClient();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


//      accessToken = Prefs.getAccessTokenFromPrefs();

        setupToolbar();

        setupViewPager();

        setupCollapsingToolbar();

        /*imageView.post(new Runnable() {
            @Override
            public void run() {*/
        changeBasedOnImage(imageView);
       /*     }
        });
*/
    }


    public synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        this.menu = menu;
        editMenuItem = menu.findItem(R.id.edit);
        doneMenuItem = menu.findItem(R.id.done);

        editDrawable = editMenuItem.getIcon();
        doneDrawable = doneMenuItem.getIcon();

        if (menuOption == 0) {
            editMenuItem.setVisible(true);
            doneMenuItem.setVisible(false);
        } else if (menuOption == 1) {
            editMenuItem.setVisible(false);
            doneMenuItem.setVisible(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                setVisible(true);
                return true;

            case R.id.done:
                boolean pic = false;
                boolean skill = false;
                boolean info = false;
                if (uriPath != null) {

                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uriPath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        byte[] inputData = Utility.getBytes(iStream);
                        sqLiteDBHelper.updateProfilePicture(inputData, Prefs.getUserKey());
                        pic = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Drawable defaultImage = ContextCompat.getDrawable(RegistrationPersonaliseActivity.this, R.drawable.app_bg_overlay);
                    Bitmap bitmap = ((BitmapDrawable) defaultImage).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapData = stream.toByteArray();
                    sqLiteDBHelper.updateProfilePicture(bitmapData, Prefs.getUserKey());
                    pic = true;

                }

                if (userTagArrayList.size() < 2) {
                    Toast.makeText(RegistrationPersonaliseActivity.this, "Atleast two Skill must be Added", Toast.LENGTH_LONG).show();
                } else {

                    ArrayList<AllSkill> otherAllSkills = new ArrayList<>();

                    //Other Skill starts
                    for (AllSkill allUserSkill : userTagArrayList) {
                        if (!allUserSkill.isMentor() && !allUserSkill.isMentee()) {
                            otherAllSkills.add(allUserSkill);
                        }
                    }

                    if (otherAllSkills.size() > 0) {
                        for (int i = 0; i < otherAllSkills.size(); i++) {
                            long tagKey = sqLiteDBHelper.getTagKey(otherAllSkills.get(i).getTitle());
                            sqLiteDBHelper.addUserSKill(Prefs.getUserKey(), String.valueOf(tagKey), "0", "0");
                        }
                    }

                    //Mentee skills
                    ArrayList<AllSkill> menteeAllSkills = new ArrayList<>();

                    for (AllSkill allUserSkill : menteeTagArrayList) {
                        if (!allUserSkill.isMentor() && allUserSkill.isMentee()) {
                            menteeAllSkills.add(allUserSkill);
                        }
                    }

                    if (menteeAllSkills.size() > 0) {
                        for (int i = 0; i < menteeAllSkills.size(); i++) {
                            long tagKey = sqLiteDBHelper.getTagKey(menteeAllSkills.get(i).getTitle());
                            sqLiteDBHelper.addUserSKill(Prefs.getUserKey(), String.valueOf(tagKey), "1", "0");
                        }
                    }

                    //Mentor Skills
                    ArrayList<AllSkill> mentorAllSkills = new ArrayList<>();

                    for (AllSkill allUserSkill : mentorTagArrayList) {
                        if (allUserSkill.isMentor() && !allUserSkill.isMentee()) {
                            mentorAllSkills.add(allUserSkill);
                        }
                    }

                    if (mentorAllSkills.size() > 0) {
                        for (int i = 0; i < mentorAllSkills.size(); i++) {
                            long tagKey = sqLiteDBHelper.getTagKey(mentorAllSkills.get(i).getTitle());
                            sqLiteDBHelper.addUserSKill(Prefs.getUserKey(), String.valueOf(tagKey), "0", "1");
                        }
                    }
                    skill = true;
                }

                if (sqLiteDBHelper.getAluminiDetails(Prefs.getUserKey()).isEmpty())
                    Toast.makeText(RegistrationPersonaliseActivity.this, "Atleast one Info must be Added", Toast.LENGTH_LONG).show();
                else
                    info = true;

                if (pic && skill && info) {
                    Prefs.setAllUserSkill(new ArrayList<AllSkill>());
                    Prefs.setAllUserMenteeSkill(new ArrayList<AllSkill>());
                    Prefs.setAllUserMentorSkill(new ArrayList<AllSkill>());
                    Prefs.setUserKey("");
                    Prefs.setLoc("");
                    //  Prefs.setEditMode(true);
                    startActivity(new Intent(RegistrationPersonaliseActivity.this, LoginActivity.class));
                }

                setVisible(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_KITKAT_INTENT_CALLED && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriPath = data.getData();
            getContentResolver().takePersistableUriPermission(uriPath, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriPath);

                imageView.setImageBitmap(bitmap);

                /*imageView.post(new Runnable() {
                    @Override
                    public void run() {*/
                changeBasedOnImage(imageView);
                 /*   }
                });*/

                //Prefs.setEditMode(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeBasedOnImage(ImageView imageView) {
        Palette.from(((BitmapDrawable) imageView.getDrawable()).getBitmap()).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getDominantSwatch();

                if (vibrant != null) {
                    if (isColorDark(vibrant.getRgb())) {
                        subtitleTextView.setTextColor(Color.WHITE);
                        collapsingToolbar.setExpandedTitleColor(Color.WHITE);

                        overflowIcon = toolbar.getOverflowIcon();
                        if (overflowIcon != null) {
                            overflowIcon.mutate();
                            overflowIcon.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                        doneDrawable = toolbar.getMenu().findItem(R.id.done).getIcon();
                        if (doneDrawable != null) {
                            doneDrawable.mutate();
                            doneDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                        editDrawable = toolbar.getMenu().findItem(R.id.edit).getIcon();
                        if (editDrawable != null) {
                            editDrawable.mutate();
                            editDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                    } else {
                        subtitleTextView.setTextColor(Color.BLACK);
                        collapsingToolbar.setExpandedTitleColor(Color.BLACK);

                        overflowIcon = toolbar.getOverflowIcon();
                        if (overflowIcon != null) {
                            overflowIcon.mutate();
                            overflowIcon.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                        doneDrawable = toolbar.getMenu().findItem(R.id.done).getIcon();
                        if (doneDrawable != null) {
                            doneDrawable.mutate();
                            doneDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                        editDrawable = toolbar.getMenu().findItem(R.id.edit).getIcon();
                        if (editDrawable != null) {
                            editDrawable.mutate();
                            editDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                    }
                }
            }
        });
    }

    private void setupCollapsingToolbar() {

        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle(sqLiteDBHelper.getName(Prefs.getUserKey()));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedappbar);

        overflowIcon = toolbar.getOverflowIcon();

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.colorPrimary));
                    subtitleTextView.setVisibility(View.GONE);
                    toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
                   /* Drawable editDrawable = toolbar.getLogo(R.id.edit).getIcon();
                    Drawable doneDrawable = menu.findItem(R.id.done).getIcon();*/
                    if (editDrawable != null && doneDrawable != null) {
                        editDrawable.mutate();
                        editDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.language), PorterDuff.Mode.SRC_ATOP);
                        doneDrawable.mutate();
                        doneDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.language), PorterDuff.Mode.SRC_ATOP);
                        overflowIcon.mutate();
                        overflowIcon.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.language), PorterDuff.Mode.SRC_ATOP);
                    }
                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(RegistrationPersonaliseActivity.this, android.R.color.transparent));
                    subtitleTextView.setText(sqLiteDBHelper.getCompanyName(Prefs.getUserKey()));
                    subtitleTextView.setVisibility(View.VISIBLE);
                    /*Drawable editDrawable = menu.findItem(R.id.edit).getIcon();
                    Drawable doneDrawable = menu.findItem(R.id.done).getIcon();*/
                    if (editDrawable != null && doneDrawable != null) {
                        editDrawable.mutate();
                        editDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        doneDrawable.mutate();
                        doneDrawable.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        overflowIcon.mutate();
                        overflowIcon.setColorFilter(ContextCompat.getColor(RegistrationPersonaliseActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    }
                }

            }
        });

    }

    private void setupViewPager() {

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        floatingActionButtonAddInfo.show();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0 || tab.getPosition() == 1)
                    animateFab(tab.getPosition(), floatingActionButtonAddInfo);
                else
                    floatingActionButtonAddInfo.hide();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupToolbar() {

        imageView = (ImageView) findViewById(R.id.header_image);
        subtitleTextView = (TextView) findViewById(R.id.subtitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);

        setSupportActionBar(toolbar);

        imageView.setImageDrawable(ContextCompat.getDrawable(RegistrationPersonaliseActivity.this, R.drawable.app_bg_overlay));
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("location", address);
        InfoFragment infoFragment = new InfoFragment();
        infoFragment.setArguments(bundle);

        adapter.addFrag(infoFragment, "INFO");

        adapter.addFrag(new AppTagsFragment(), "SKILL");

        adapter.addFrag(new MenteeFragment(), "MENTEE");

        adapter.addFrag(new MentorFragment(), "MENTOR");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 3) {
                    invalidateOptionsMenu();
                    menuOption = 1;
                } else {
                    invalidateOptionsMenu();
                    menuOption = 0;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void passSkill(ArrayList<AllSkill> msg) {
        MenteeFragment menteeFragment = (MenteeFragment) adapter.getFragment(2);
        if (menteeFragment != null) {
            menteeFragment.fragmentCommunication(msg);
        } else {
            Log.e("tag", "MenteeFragment is not initialized");
        }

    }

    @Override
    public void passSkillMentor(ArrayList<AllSkill> msg) {

        MentorFragment mentorFragment = (MentorFragment) adapter.getFragment(3);
        if (mentorFragment != null) {
            mentorFragment.fragmentCommunicationMentor(msg);
        } else {
            Log.e("tag", "MentorFragment is not initialized");
        }
    }

    @Override
    public void passAllUserSkill(ArrayList<AllSkill> allSkills) {
        userTagArrayList = allSkills;
        menteeTagArrayList = allSkills;
        mentorTagArrayList = allSkills;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        address = Utility.getAddress(RegistrationPersonaliseActivity.this, location);

        Log.e("tag", "Lat-->" + latitude + "long-->" + longitude + "Address-->" + address);

        Prefs.setLoc(address);

        sqLiteDBHelper.settLocation(Prefs.getUserKey(), String.valueOf(latitude), String.valueOf(longitude));

    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
        if (this.googleApiClient != null) {
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.googleApiClient != null) {
            this.googleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Utility.showGPSSettingsAlert(RegistrationPersonaliseActivity.this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_FINE_LOCATION) {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("tag", "Location permission has now been granted. Showing preview.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}


