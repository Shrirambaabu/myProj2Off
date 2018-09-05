package com.conext.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conext.Adapter.RecyclerAdapterProfileEvents;
import com.conext.Adapter.RecyclerAdapterProfileInfo;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Skill;
import com.conext.model.db.USER_EVENT;

import java.util.ArrayList;

import static com.conext.AppManager.getAppContext;
import static com.conext.utils.Utility.isColorDark;

public class OtherProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView eventsAttendedImageView, coffeeConnectImageView, lunchMeetImageView, alumniImageView, mentorshipImageView;
    private TextView eventsAttendedTextView, coffeeConnectTextView, lunchMeetTextView, alumniTextView, mentorshipTextView;
    private RecyclerView eventsRecyclerView, mentorOtherRecycelerView, menteeOtherRecyclerView, otherInterestRecyclerView;
    CollapsingToolbarLayout collapsingToolbar;
    TextView subtitleTextView, otherInterestText;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    View otherView;
    private FloatingActionButton fab_other;
    boolean flag = false;
    private LinearLayout mentorCardView, menteeCardView;
    ArrayList<Skill> mentorSkillArrayList = null;
    ArrayList<Skill> menteeSkillArrayList = null;
    RecyclerAdapterProfileInfo skillMentorOtherAdapter = null;

    RecyclerAdapterProfileInfo otherMenteeAdapter = null;

    SQLiteDBHelper sqLiteDBHelper;

    ImageView headerImageView;
    ArrayList<Skill> skillOtherArrayList = null;
    RecyclerAdapterProfileInfo skillOtherAdapter = null;

    ArrayList<USER_EVENT> otherProfileEventsArrayList;
    RecyclerAdapterProfileEvents otherProfileEventsAdapter = null;
    private ImageView eventImage, eventDark, eventMore;
    private TextView eventTitle, eventType, eventDate, eventsAttend, eventMonth;
    String id;

    private Drawable navDrawable, overflowIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        id = String.valueOf(getIntent().getExtras().getInt("id"));
        if (id.equals("0")) {
            id = getIntent().getExtras().getString("id");
        }
        sqLiteDBHelper = new SQLiteDBHelper(this);

        mentorCardView = (LinearLayout) findViewById(R.id.mentor_card_other_profile);
        menteeCardView = (LinearLayout) findViewById(R.id.mentee_card_other_profile);
        otherInterestText = (TextView) findViewById(R.id.other_interest_text_other_profile);
        eventsAttend = (TextView) findViewById(R.id.events_attended_text_view);

        setupToolbar();

        setupCollapsingToolbar();

        fab();
        //addressing view
        addressingView();

        displayDetails();
        //attaching listeners
        listeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
         /* case R.id.other_profile_favorite:
                Toast.makeText(getAppContext(), "You have clicked Favorites", Toast.LENGTH_LONG).show();
                break;*/
            case R.id.other_profile_share:
                Toast.makeText(getAppContext(), "You have clicked Share", Toast.LENGTH_LONG).show();
                break;
            case R.id.other_profile_invite:
                Toast.makeText(getAppContext(), "You have clicked Invite", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void fab() {
        fab_other = (FloatingActionButton) this.findViewById(R.id.fab_other);
        fab_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    fab_other.setImageResource(R.drawable.ic_star_border_white_24dp);
                    flag = false;
                } else {
                    fab_other.setImageResource(R.drawable.ic_star_white_24dp);
                    flag = true;
                }
            }
        });
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


    private void setupCollapsingToolbar() {

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.other_collapsing_toolbar);
        headerImageView = (ImageView) findViewById(R.id.header_image);

        headerImageView.setImageBitmap(sqLiteDBHelper.getUserImage(id));

        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle(sqLiteDBHelper.getName(id));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedappbar);

        subtitleTextView = (TextView) findViewById(R.id.other_subtitle);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(OtherProfileActivity.this, R.color.colorPrimaryDark));
                    subtitleTextView.setVisibility(View.GONE);

                    Drawable drawableOverflow = toolbar.getOverflowIcon();
                    if (drawableOverflow != null) {
                        drawableOverflow.mutate();
                        drawableOverflow.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    }
                    Drawable drawable = toolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.mutate();
                        drawable.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    }

                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(OtherProfileActivity.this, android.R.color.transparent));
                    subtitleTextView.setText(sqLiteDBHelper.getCompanyName(id));
                    subtitleTextView.setVisibility(View.VISIBLE);

                    Drawable drawableOverflow = toolbar.getOverflowIcon();
                    if (drawableOverflow != null) {
                        drawableOverflow.mutate();
                        drawableOverflow.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    }
                    Drawable drawable = toolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.mutate();
                        drawable.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    }
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
                            overflowIcon.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                        navDrawable = toolbar.getNavigationIcon();
                        if (navDrawable != null) {
                            navDrawable.mutate();
                            navDrawable.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        }

                    } else {
                        subtitleTextView.setTextColor(Color.BLACK);
                        collapsingToolbar.setExpandedTitleColor(Color.BLACK);

                        overflowIcon = toolbar.getOverflowIcon();
                        if (overflowIcon != null) {
                            overflowIcon.mutate();
                            overflowIcon.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                        navDrawable = toolbar.getNavigationIcon();
                        if (navDrawable != null) {
                            navDrawable.mutate();
                            navDrawable.setColorFilter(ContextCompat.getColor(OtherProfileActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }

                    }
                }
            }
        });
       /*     }
        });*/

    }

    private void displayDetails() {

        mentorSkillArrayList = sqLiteDBHelper.getUserMentorSkill(id);

        if (mentorSkillArrayList.isEmpty()) {
            mentorCardView.setVisibility(View.GONE);
        } else {
            skillMentorOtherAdapter = new RecyclerAdapterProfileInfo(getAppContext(), mentorSkillArrayList, R.layout.other_profile_mentor);
        }

        RecyclerView.LayoutManager mentorLayoutManager = new LinearLayoutManager(getAppContext());
        mentorOtherRecycelerView.setLayoutManager(mentorLayoutManager);
        mentorOtherRecycelerView.setHasFixedSize(true);
        mentorOtherRecycelerView.setAdapter(skillMentorOtherAdapter);

        menteeSkillArrayList = sqLiteDBHelper.getUserMenteeSkill(id);

        if (menteeSkillArrayList.isEmpty()) {
            menteeCardView.setVisibility(View.GONE);
        } else {
            otherMenteeAdapter = new RecyclerAdapterProfileInfo(getAppContext(), menteeSkillArrayList, R.layout.other_profile_mentee);
        }

        RecyclerView.LayoutManager menteeLayoutManager = new LinearLayoutManager(getAppContext());
        menteeOtherRecyclerView.setLayoutManager(menteeLayoutManager);
        menteeOtherRecyclerView.setHasFixedSize(true);
        menteeOtherRecyclerView.setAdapter(otherMenteeAdapter);
        skillOtherArrayList = sqLiteDBHelper.getUserOtherSkill(id);

        if (skillOtherArrayList.isEmpty()) {
            otherInterestText.setVisibility(View.GONE);
            otherView = (View) findViewById(R.id.other_view);
            otherView.setVisibility(View.GONE);
        } else {
            skillOtherAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillOtherArrayList, R.layout.other_profile_other_interest);
        }


        RecyclerView.LayoutManager otherInterestLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        otherInterestRecyclerView.setLayoutManager(otherInterestLayoutManager);
        otherInterestRecyclerView.setHasFixedSize(true);
        otherInterestRecyclerView.setAdapter(skillOtherAdapter);

        otherProfileEventsArrayList = new ArrayList<>();

        otherProfileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(id);

        otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);


        RecyclerView.LayoutManager eventManager = new LinearLayoutManager(OtherProfileActivity.this);
        eventsRecyclerView.setLayoutManager(eventManager);
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setAdapter(otherProfileEventsAdapter);

        int attendEvent = sqLiteDBHelper.getMyAttendedEvents(id).size();
        if (attendEvent == 0) {
            eventsAttend.setVisibility(View.GONE);
        }
        String eventFinalCount;
        if (attendEvent < 10) {
            eventFinalCount = "" + 0 + attendEvent;
        } else {
            eventFinalCount = "" + attendEvent;
        }
        eventsAttendedTextView.setText(eventFinalCount);

        int coffeeConnectCount = sqLiteDBHelper.getParticularMyAttendedEvents(id, "4").size();

        String coffeeConnect;
        if (coffeeConnectCount < 10) {
            coffeeConnect = "" + 0 + coffeeConnectCount;
        } else {
            coffeeConnect = "" + coffeeConnectCount;
        }
        coffeeConnectTextView.setText(coffeeConnect);

        int lunchMeetCount = sqLiteDBHelper.getParticularMyAttendedEvents(id, "3").size();

        String lunchMeet;
        if (lunchMeetCount < 10) {
            lunchMeet = "" + 0 + lunchMeetCount;
        } else {
            lunchMeet = "" + lunchMeetCount;
        }
        lunchMeetTextView.setText(lunchMeet);

        int alumniCount = sqLiteDBHelper.getParticularMyAttendedEvents(id, "2").size();

        String alumniConnect;
        if (alumniCount < 10) {
            alumniConnect = "" + 0 + alumniCount;
        } else {
            alumniConnect = "" + alumniCount;
        }
        alumniTextView.setText(alumniConnect);

        int mentorShipCount = sqLiteDBHelper.getParticularMyAttendedEvents(id, "1").size();

        String mentorShip;
        if (mentorShipCount < 10) {
            mentorShip = "" + 0 + mentorShipCount;
        } else {
            mentorShip = "" + mentorShipCount;
        }
        mentorshipTextView.setText(mentorShip);

    }


    private void listeners() {

        eventsAttendedImageView.setOnClickListener(this);
        coffeeConnectImageView.setOnClickListener(this);
        lunchMeetImageView.setOnClickListener(this);
        alumniImageView.setOnClickListener(this);
        mentorshipImageView.setOnClickListener(this);

    }

    private void addressingView() {

        eventsAttendedImageView = (ImageView) findViewById(R.id.event_attend_prof_other);
        coffeeConnectImageView = (ImageView) findViewById(R.id.event_coffee_prof_other);
        lunchMeetImageView = (ImageView) findViewById(R.id.event_lunch_prof_other);
        alumniImageView = (ImageView) findViewById(R.id.event_alumini_prof_other);
        mentorshipImageView = (ImageView) findViewById(R.id.event_mentor_prof_other);

        eventsAttendedTextView = (TextView) findViewById(R.id.event_prof_attended_other);

        coffeeConnectTextView = (TextView) findViewById(R.id.event_coffee_attended_other);
        lunchMeetTextView = (TextView) findViewById(R.id.event_lunch_attended_other);
        alumniTextView = (TextView) findViewById(R.id.event_alumni_meet_other);
        mentorshipTextView = (TextView) findViewById(R.id.event_prof_ship_other);

        eventsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_profile_other);
        mentorOtherRecycelerView = (RecyclerView) findViewById(R.id.recycler_other_mentor);
        menteeOtherRecyclerView = (RecyclerView) findViewById(R.id.recycler_other_mentee);
        otherInterestRecyclerView = (RecyclerView) findViewById(R.id.recycler_other_other);

        eventImage = (ImageView) findViewById(R.id.bac);
        eventDark = (ImageView) findViewById(R.id.black_image);
        eventMore = (ImageView) findViewById(R.id.event_more);
        eventTitle = (TextView) findViewById(R.id.event_name);
        eventType = (TextView) findViewById(R.id.event_type);
        eventDate = (TextView) findViewById(R.id.event_date);
        eventMonth = (TextView) findViewById(R.id.event_month);
        eventsAttend = (TextView) findViewById(R.id.events_attended_text_view);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.event_attend_prof_other:

                eventsAttendedImageView.setImageResource(R.drawable.event_attended);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);


                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(id);

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);


                break;
            case R.id.event_coffee_prof_other:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.event_attended);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);


                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(id, "4");

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);

                break;
            case R.id.event_lunch_prof_other:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.event_attended);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(id, "3");

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);


                break;
            case R.id.event_alumini_prof_other:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.event_attended);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(id, "2");

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);


                break;
            case R.id.event_mentor_prof_other:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.event_attended);

                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(id, "1");

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);

                break;
            default:

                eventsAttendedImageView.setImageResource(R.drawable.event_attended);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                otherProfileEventsArrayList = new ArrayList<>();

                otherProfileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(id);

                if (otherProfileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                } else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                otherProfileEventsAdapter = new RecyclerAdapterProfileEvents(OtherProfileActivity.this, otherProfileEventsArrayList);

                eventsRecyclerView.setAdapter(otherProfileEventsAdapter);

        }

    }

}