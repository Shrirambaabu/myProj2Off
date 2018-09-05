package com.conext.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.conext.R;
import com.conext.model.ProfileEvents;
import com.conext.model.db.USER_EVENT;
import com.conext.ui.EventDetailsActivity;
import com.conext.ui.Fragments.EventDetailsFragment;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Shriram on 6/26/2017.
 */

public class RecyclerAdapterProfileEvents extends RecyclerView.Adapter<RecyclerAdapterProfileEvents.MyViewHolder> {

    private List<USER_EVENT> profileEventsList;
    private Context context;
    private LayoutInflater inflater;
    private String dateStartEventDetails, dateEndEventDetails, monthStartEventDetails, monthEndEventDetails;
    int pos;

    public RecyclerAdapterProfileEvents(Context context, List<USER_EVENT> profileEventsList) {

        this.context = context;
        this.profileEventsList = profileEventsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public RecyclerAdapterProfileEvents.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.card_events_attended, parent, false);
        return new RecyclerAdapterProfileEvents.MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        pos = position;
        USER_EVENT profileEvents = profileEventsList.get(position);

        holder.profileEventName.setText(profileEvents.getEventTitle());
        holder.profileEventType.setText(profileEvents.getEventTypeKey() + " | " + profileEvents.getTagKey());


        String dateStart = profileEvents.getEventStartTs();
        String datEnd = profileEvents.getEventEndTs();
        Log.e("dateStart", dateStart);
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        try {
            Date dateStartEvent = parseFormat.parse(dateStart);
            dateStartEventDetails = dateFormat.format(dateStartEvent);
            Log.e("startEvent", "" + dateStartEvent);
            Date dateEndEvent = parseFormat.parse(datEnd);
            dateEndEventDetails = dateFormat.format(dateEndEvent);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String monthStart = profileEvents.getEventStartTs();
        String monthEnd = profileEvents.getEventEndTs();

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM");
        try {
            Date monthStartEvent = parseFormat.parse(monthStart);
            Log.e("monthDate", "" + monthStartEvent);
            monthStartEventDetails = dateFormat2.format(monthStartEvent);
            Date monthEndEvent = parseFormat.parse(monthEnd);
            monthEndEventDetails = dateFormat2.format(monthEndEvent);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!dateStartEventDetails.equals(dateEndEventDetails)) {
            holder.profileEventDateStart.setText(dateStartEventDetails + " - " + dateEndEventDetails);
            holder.profileEventMonthStart.setText(monthStartEventDetails + "    " + monthEndEventDetails);
        } else {
            holder.profileEventDateStart.setText(dateStartEventDetails);
            holder.profileEventMonthStart.setText(monthStartEventDetails);
        }

        holder.profileEventImage.setImageBitmap(Utility.getPhoto(profileEvents.getImageKey()));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int posit) {
                Log.e("tag", "click" + profileEventsList.get(position).getEventKey());

                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("id", profileEventsList.get(position).getEventKey());
                (context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileEventsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView profileEventName, profileEventType, profileEventDateStart, profileEventMonthStart;
        private ImageView profileEventImage;
        private ItemClickListener itemClickListener;


        MyViewHolder(View itemView) {
            super(itemView);

            profileEventName=(TextView) itemView.findViewById(R.id.event_name);
            profileEventType=(TextView)itemView.findViewById(R.id.event_type);
            profileEventDateStart=(TextView)itemView.findViewById(R.id.event_date);
            profileEventMonthStart = (TextView) itemView.findViewById(R.id.event_month);
            profileEventImage = (ImageView) itemView.findViewById(R.id.bac);
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