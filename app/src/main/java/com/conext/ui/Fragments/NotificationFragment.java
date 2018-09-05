package com.conext.ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.NotificationUpcoming;
import com.conext.model.OtherNotification;
import com.conext.model.Skill;
import com.conext.model.db.USER_EVENT;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.conext.AppManager.getAppContext;


public class NotificationFragment extends Fragment {

    private LinearLayoutManager mLayoutManager;
    ArrayList<OtherNotification> otherNotificationArrayList;
    OtherNotification otherNotification;
    RecyclerAdapterOtherNotification otherNotificationAdapter = null;

    ArrayList<NotificationUpcoming> notificationUpcomingArrayList;
    NotificationUpcoming notificationUpcoming;
    RecyclerAdapterUpcomingNotification upcomingAdapter = null;
    RecyclerView recyclerViewUp, recyclerViewOther;

    private String dateStarUpcomeEvent;
    private String monthStartUpcomeEvent;
    private String dateTimeMyEvent;
    SQLiteDBHelper sqLiteDBHelper;

    ArrayList<Skill> skillArrayList = null;
    ArrayList<Long> eventKeyArrayList = null;
    ArrayList<USER_EVENT> myEventsArrayList;
    private TextView up_comming, otherNotifica, nothingText;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notification");

        up_comming = (TextView) view.findViewById(R.id.up_comming);
        otherNotifica = (TextView) view.findViewById(R.id.notifi);
        nothingText = (TextView) view.findViewById(R.id.nothing_text);
        myEventsArrayList = new ArrayList<>();
        eventKeyArrayList = new ArrayList<>();
        skillArrayList = new ArrayList<>();

        displayUpcomingNotification(view);

        displayOtherNotification(view);
        if (myEventsArrayList.isEmpty() && otherNotificationArrayList.isEmpty()) {
            nothingText.setVisibility(View.VISIBLE);
        }
        return view;
    }


    private void displayUpcomingNotification(View view) {
        notificationUpcomingArrayList = new ArrayList<>();
        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());

        eventKeyArrayList = sqLiteDBHelper.getMyUpComingEventsKey(skillArrayList, Prefs.getUserKey());

        myEventsArrayList = sqLiteDBHelper.getMyUpCommingEvents(Prefs.getUserKey(), eventKeyArrayList);

        // myEventsArrayList = sqLiteDBHelper.getMyEvents(Prefs.getUserKey());

        Log.e("tag", myEventsArrayList.toString());

        if (myEventsArrayList.isEmpty())
            up_comming.setVisibility(View.GONE);

        upcomingAdapter = new RecyclerAdapterUpcomingNotification(getAppContext(), myEventsArrayList);

        recyclerViewUp = (RecyclerView) view.findViewById(R.id.upcome_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewUp.setLayoutManager(mLayoutManager);
        recyclerViewUp.setHasFixedSize(true);
        recyclerViewUp.setAdapter(upcomingAdapter);
    }

    private void displayOtherNotification(View view) {
        otherNotificationArrayList = new ArrayList<>();

        otherNotificationArrayList = sqLiteDBHelper.getMenteeMentorRequest();

        if (otherNotificationArrayList.isEmpty())
            otherNotifica.setVisibility(View.GONE);
        otherNotificationAdapter = new RecyclerAdapterOtherNotification(getAppContext(), otherNotificationArrayList);

        recyclerViewOther = (RecyclerView) view.findViewById(R.id.other_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerViewOther.setLayoutManager(mLayoutManager);
        recyclerViewOther.setHasFixedSize(true);
        recyclerViewOther.setAdapter(otherNotificationAdapter);
    }


    class RecyclerAdapterOtherNotification extends RecyclerView.Adapter<RecyclerAdapterOtherNotification.MyViewHolder> {
        private List<OtherNotification> otherNotificationList;
        private Context context;
        private LayoutInflater inflater;
        int posi;

        public RecyclerAdapterOtherNotification(Context context, List<OtherNotification> otherNotificationList) {

            this.context = context;
            this.otherNotificationList = otherNotificationList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View rootView = inflater.inflate(R.layout.card_view_other_notification, parent, false);
            return new RecyclerAdapterOtherNotification.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final boolean[] visible = {false};
            posi = position;
            final OtherNotification otherNotification = otherNotificationList.get(position);

            holder.notifyName.setText(otherNotification.getNotifyName());
            holder.notifyTime.setText(otherNotification.getNotifyTime());

            Spanned result;

            if (otherNotification.getMentorMentee().equals("Mentee")) {
                String sourceString = "Wants you to be a Mentee in <b>" + otherNotification.getEventType() + "</b> Event";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    result = Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    result = Html.fromHtml(sourceString);
                }
                holder.wantsMentor.setText(result);
            } else if (otherNotification.getMentorMentee().equals("Mentor")) {
                String sourceString = "Wants you to Mentor him for <b>" + otherNotification.getEventType() + "</b> ";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    result = Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    result = Html.fromHtml(sourceString);
                }
                holder.wantsMentor.setText(result);
            } else {
                String sourceString = "Wants you to be a Participant for <b>" + otherNotification.getEventType() + "</b> Event";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    result = Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    result = Html.fromHtml(sourceString);
                }
                holder.wantsMentor.setText(result);
            }

            holder.contactNotify.setBorderColor(ContextCompat.getColor(context, R.color.black));
            holder.contactNotify.setImageBitmap(otherNotification.getImg());

            holder.defaultOption.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!visible[0]) {
                        holder.longPressOption.setVisibility(View.VISIBLE);
                        visible[0] = true;
                    } else {
                        holder.longPressOption.setVisibility(View.GONE);
                        visible[0] = false;
                    }
                    return true;
                }
            });

            holder.acceptNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDBHelper.updateParticipantStatus(otherNotification.getEventId(), Prefs.getUserKey(), "1");
                    otherNotificationList.remove(position);
                    notifyDataSetChanged();
                    if (otherNotificationArrayList.isEmpty()) {
                        otherNotifica.setVisibility(View.GONE);
                       }
                    if (myEventsArrayList.isEmpty() && otherNotificationArrayList.isEmpty()) {
                        nothingText.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.declineNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDBHelper.updateParticipantStatus(otherNotification.getEventId(), Prefs.getUserKey(), "2");
                    otherNotificationList.remove(position);
                    notifyDataSetChanged();
                    if (otherNotificationArrayList.isEmpty()) {
                        otherNotifica.setVisibility(View.GONE);
                       }
                    if (myEventsArrayList.isEmpty() && otherNotificationArrayList.isEmpty()) {
                        nothingText.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.wantsMentor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("tag", "click" + otherNotification.getEventId());

                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", otherNotification.getEventId());
                    eventDetailsFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment_container, eventDetailsFragment, "tag")
                            .addToBackStack("tag").commit();
                }
            });

        }

        @Override
        public int getItemCount() {
            return otherNotificationList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView notifyName, notifyTime, wantsMentor, declineNotify, acceptNotify;
            private ImageView clearNotify, doneNotify;
            private RelativeLayout longPressOption, defaultOption;
            // private PorterShapeImageView contactNotify;
            private HexagonMaskView contactNotify;

            public MyViewHolder(View itemView) {
                super(itemView);

                notifyName = (TextView) itemView.findViewById(R.id.notify_name);
                notifyTime = (TextView) itemView.findViewById(R.id.notify_time);
                wantsMentor = (TextView) itemView.findViewById(R.id.wants_mentor);
                //   notifyLanguage = (TextView) itemView.findViewById(R.id.notify_language);
                declineNotify = (TextView) itemView.findViewById(R.id.decline_notify);
                acceptNotify = (TextView) itemView.findViewById(R.id.accept_notify);

                contactNotify = (HexagonMaskView) itemView.findViewById(R.id.contact_notify);
                clearNotify = (ImageView) itemView.findViewById(R.id.clear_notify);
                doneNotify = (ImageView) itemView.findViewById(R.id.done_notify);
                longPressOption = (RelativeLayout) itemView.findViewById(R.id.longPress);
                defaultOption = (RelativeLayout) itemView.findViewById(R.id.notifyNormal);

            }
        }
    }

    class RecyclerAdapterUpcomingNotification extends RecyclerView.Adapter<RecyclerAdapterUpcomingNotification.MyViewHolder> {
        private List<USER_EVENT> notificationUpcomingList;
        private Context context;
        private LayoutInflater inflater;

        public RecyclerAdapterUpcomingNotification(Context context, List<USER_EVENT> notificationUpcomingList) {

            this.context = context;
            this.notificationUpcomingList = notificationUpcomingList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = inflater.inflate(R.layout.card_view_notification_upcoming, parent, false);
            return new RecyclerAdapterUpcomingNotification.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final USER_EVENT notificationUpcoming = notificationUpcomingList.get(position);

            holder.meetNotify.setText(notificationUpcoming.getEventTitle());
            String dateStart = notificationUpcoming.getEventStartTs();
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            try {
                Date dateStartEvent = parseFormat.parse(dateStart);
                dateStarUpcomeEvent = dateFormat.format(dateStartEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String monthStart = notificationUpcoming.getEventStartTs();
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM");
            try {
                Date monthStartEvent = parseFormat.parse(monthStart);
                monthStartUpcomeEvent = dateFormat2.format(monthStartEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.dateNotify.setText(dateStarUpcomeEvent);
            holder.monthNotify.setText(monthStartUpcomeEvent);
            SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMM, hh:mm aa");
            try {
                Date dateTimeStart = parseFormat.parse(dateStart);
                dateTimeMyEvent = dateFormatStart.format(dateTimeStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.timeNotify.setText(dateTimeMyEvent + " onwards");

            holder.imageNotify.setImageBitmap(Utility.getPhoto(notificationUpcoming.getImageKey()));

            String friendsCount;
            if (sqLiteDBHelper.getNumberOfPeopleGoing(notificationUpcoming.getEventKey(), Prefs.getUserKey()).equals("0"))
                friendsCount = sqLiteDBHelper.getNumberOfPeopleGoing(notificationUpcoming.getEventKey(), Prefs.getUserKey()) + " friend are going";
            else
                friendsCount = sqLiteDBHelper.getNumberOfPeopleGoing(notificationUpcoming.getEventKey(), Prefs.getUserKey()) + " friends are going";

            holder.friendsNotify.setText(friendsCount);

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Log.e("tag", "click" + notificationUpcomingList.get(position).getEventKey());

                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", notificationUpcomingList.get(position).getEventKey());
                    eventDetailsFragment.setArguments(bundle);
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment_container, eventDetailsFragment, "tag")
                            .addToBackStack("tag").commit();
                }
            });


        }

        @Override
        public int getItemCount() {
            return notificationUpcomingList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView dateNotify, monthNotify, meetNotify, timeNotify, friendsNotify;
            private ImageView imageNotify, imageMoreNotify;
            private ItemClickListener itemClickListener;

            public MyViewHolder(View itemView) {
                super(itemView);
                dateNotify = (TextView) itemView.findViewById(R.id.notify_upcome_date_one);
                monthNotify = (TextView) itemView.findViewById(R.id.notify_upcome_month_one);
                meetNotify = (TextView) itemView.findViewById(R.id.notify_meet);
                timeNotify = (TextView) itemView.findViewById(R.id.onwards_notify);
                friendsNotify = (TextView) itemView.findViewById(R.id.friends_notify);

                imageNotify = (ImageView) itemView.findViewById(R.id.img_notify_upcome);
                imageMoreNotify = (ImageView) itemView.findViewById(R.id.notify_more);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                this.itemClickListener.onItemClick(v, getLayoutPosition());
            }

            void setItemClickListener(ItemClickListener ic) {
                this.itemClickListener = ic;
            }
        }
    }
}
