package com.conext.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conext.Adapter.ViewPagerAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.ui.Fragments.Alumni;
import com.conext.ui.Fragments.Events;
import com.conext.ui.Fragments.Info;
import com.conext.utils.Prefs;

import static com.conext.AppManager.getAppContext;
import static com.conext.utils.Utility.isColorDark;

/**
 * Created by Ashith VL on 6/9/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    boolean flag = true;
    ImageView headerImageView;
    Toolbar toolbar;
    TextView subtitleTextView;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    String id;
    SQLiteDBHelper sqLiteDBHelper;

    private boolean fabStatus = false;
    private FrameLayout fraToolFloat;
    private FloatingActionButton fabSetting;
    private FloatingActionButton fabSub1;
    private FloatingActionButton fabSub2;
    private FloatingActionButton fabSub3;
    private FloatingActionButton fabSub4;

    private LinearLayout linFabSetting;
    private LinearLayout linFab1;
    private LinearLayout linFab2;
    private LinearLayout linFab3;
    private LinearLayout linFab4;


    private Drawable navDrawable, overflowIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        sqLiteDBHelper = new SQLiteDBHelper(this);

        setupToolbar();

        fab();

        setupViewPager();

        setupCollapsingToolbar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {

            case R.id.profile_mentoship:
                Intent intent4 = new Intent(ProfileActivity.this, CreateEventActivity.class);
                bundle.putString("value", "0");
                intent4.putExtras(bundle);
                startActivity(intent4);
                break;
            case R.id.profile_alumni:
                Intent intent3 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "1");
                intent3.putExtras(bundle);
                startActivity(intent3);
                break;
            case R.id.profile_lunch:
                Intent intent2 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "2");
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.profile_coffee:
                Intent intent1 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "3");
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void fab() {

        fraToolFloat = (FrameLayout) this.findViewById(R.id.fraToolFloat);
        fabSetting = (FloatingActionButton) this.findViewById(R.id.fabSetting);
        fabSub1 = (FloatingActionButton) this.findViewById(R.id.fabSub1);
        fabSub2 = (FloatingActionButton) this.findViewById(R.id.fabSub2);
        fabSub3 = (FloatingActionButton) this.findViewById(R.id.fabSub3);
        fabSub4 = (FloatingActionButton) this.findViewById(R.id.fabSub4);

        linFab1 = (LinearLayout) this.findViewById(R.id.linFab1);
        linFab2 = (LinearLayout) this.findViewById(R.id.linFab2);
        linFab3 = (LinearLayout) this.findViewById(R.id.linFab3);
        linFab4 = (LinearLayout) this.findViewById(R.id.linFab4);
        linFabSetting = (LinearLayout) this.findViewById(R.id.linFabSetting);

        fabSub1.setOnClickListener(this);
        fabSub2.setOnClickListener(this);
        fabSub3.setOnClickListener(this);
        fabSub4.setOnClickListener(this);

        /* when fab Setting (fab main) clicked */
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabStatus) {
                    if (flag) {
                        fabSetting.setImageResource(R.drawable.prof_plus);
                        fabSetting.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.colorPrimary));
                        flag = false;
                        hideFab();
                    }
                } else {
                    fabSetting.setImageResource(R.drawable.ic_clear_skill);
                    fabSetting.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.bg_card));
                    flag = true;
                    showFab();
                }
            }
        });

        hideFab();
    }

    @Override
    public void onResume() {
        super.onResume();
        fabSetting.setImageResource(R.drawable.prof_plus);
        fabSetting.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.colorPrimary));
        hideFab();

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {

            case R.id.fabSub1:
                Intent intent1 = new Intent(ProfileActivity.this, CreateEventActivity.class);
                bundle.putString("value", "0");
                intent1.putExtras(bundle);
                startActivity(intent1);
               /* tabLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                collapsingToolbar.setVisibility(View.GONE);*/
                break;
            case R.id.fabSub2:
                Intent intent2 = new Intent(ProfileActivity.this, CreateEventActivity.class);
                bundle.putString("value", "1");
                intent2.putExtras(bundle);
                startActivity(intent2);
              /*  tabLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                collapsingToolbar.setVisibility(View.GONE);*/
                break;
            case R.id.fabSub3:
                Intent intent3 = new Intent(ProfileActivity.this, CreateEventActivity.class);
                bundle.putString("value", "2");
                intent3.putExtras(bundle);
                startActivity(intent3);
               /* tabLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                collapsingToolbar.setVisibility(View.GONE);*/
                break;
            case R.id.fabSub4:
                Intent intent4 = new Intent(ProfileActivity.this, CreateEventActivity.class);
                bundle.putString("value", "3");
                intent4.putExtras(bundle);
                startActivity(intent4);
             /*   tabLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                collapsingToolbar.setVisibility(View.GONE);*/
                break;
        }
    }

    private void hideFab() {
        linFab1.setVisibility(View.INVISIBLE);
        linFab2.setVisibility(View.INVISIBLE);
        linFab3.setVisibility(View.INVISIBLE);
        linFab4.setVisibility(View.INVISIBLE);
        fraToolFloat.setBackgroundColor(Color.argb(0, 255, 255, 255));
        fabStatus = false;
    }

    private void showFab() {
        linFab1.setVisibility(View.VISIBLE);
        linFab2.setVisibility(View.VISIBLE);
        linFab3.setVisibility(View.VISIBLE);
        linFab3.setVisibility(View.VISIBLE);
        linFab4.setVisibility(View.VISIBLE);
        fraToolFloat.setBackgroundColor(Color.argb(200, 0, 0, 0));
        fabStatus = true;
    }

    private void setupCollapsingToolbar() {

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        headerImageView = (ImageView) findViewById(R.id.header_image);
        headerImageView.setImageBitmap(sqLiteDBHelper.getUserImage(Prefs.getUserKey()));
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle(sqLiteDBHelper.getName(Prefs.getUserKey()));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedappbar);

        subtitleTextView = (TextView) findViewById(R.id.textViewJob);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
               /* float percentage = ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                //fraToolFloat.setAlpha(percentage);
                fraToolFloat.animate().translationY(verticalOffset).start();
*/
                fraToolFloat.post(new Runnable() {
                    @Override
                    public void run() {
                        fraToolFloat.animate().translationY(verticalOffset).start();
                    }
                });

                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorPrimary));
                    subtitleTextView.setVisibility(View.GONE);
                    fraToolFloat.setVisibility(View.GONE);

                    Drawable drawableOverflow = toolbar.getOverflowIcon();
                    if (drawableOverflow != null) {
                        drawableOverflow.mutate();
                        drawableOverflow.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    }
                    Drawable drawable = toolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.mutate();
                        drawable.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    }
                    //fam.setVisibility(View.GONE);
                } else {

                    toolbar.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent));
                    subtitleTextView.setText(sqLiteDBHelper.getCompanyName(Prefs.getUserKey()));
                    subtitleTextView.setVisibility(View.VISIBLE);
                    fraToolFloat.setVisibility(View.VISIBLE);

                    Drawable drawableOverflow = toolbar.getOverflowIcon();
                    if (drawableOverflow != null) {
                        drawableOverflow.mutate();
                        drawableOverflow.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    }
                    Drawable drawable = toolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.mutate();
                        drawable.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    }
                    // fam.setVisibility(View.VISIBLE);
                }

            }
        });


        /*headerImageView.post(new Runnable() {
            @Override
            public void run() {*/
        Palette.from(((BitmapDrawable) headerImageView.getDrawable()).getBitmap()).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
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
                            overflowIcon.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                        navDrawable = toolbar.getNavigationIcon();
                        if (navDrawable != null) {
                            navDrawable.mutate();
                            navDrawable.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                    } else {
                        collapsingToolbar.setExpandedTitleColor(Color.BLACK);

                        overflowIcon = toolbar.getOverflowIcon();
                        if (overflowIcon != null) {
                            overflowIcon.mutate();
                            overflowIcon.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                        navDrawable = toolbar.getNavigationIcon();
                        if (navDrawable != null) {
                            navDrawable.mutate();
                            navDrawable.setColorFilter(ContextCompat.getColor(ProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                    }
                }
            }
        });
         /*   }
        });*/

    }


    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupViewPager() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new Info(), "INFO");

        adapter.addFrag(new Alumni(), "ALUMNI");

        adapter.addFrag(new Events(), "EVENTS");

        viewPager.setAdapter(adapter);
    }


}
