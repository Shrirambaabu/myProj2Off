package com.conext.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.conext.R;
import com.conext.model.Skill;

import java.util.List;

/**
 * Created by Shriram on 6/26/2017.
 */

public class RecyclerAdapterProfileInfo extends RecyclerView.Adapter<RecyclerAdapterProfileInfo.MyViewHolder> {

    private List<Skill> profileInfoList;
    private Context context;
    private LayoutInflater inflater;
    private int res;
    private int pos;

    public RecyclerAdapterProfileInfo(Context context, List<Skill> profileInfoList, int res) {

        this.context = context;
        this.profileInfoList = profileInfoList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.res = res;

    }


    @Override
    public RecyclerAdapterProfileInfo.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(res, parent, false);
        return new RecyclerAdapterProfileInfo.MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        pos = position;
        Skill profileInfo = profileInfoList.get(position);
        if (profileInfo.getTitle() != null)
            holder.infoProfile.setText(profileInfo.getTitle());
    }


    @Override
    public int getItemCount() {
        if (profileInfoList == null)
            return 0;
        else
            return profileInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView infoProfile;
        private ImageView infoProfileImage;


        MyViewHolder(View itemView) {
            super(itemView);

            infoProfile = (TextView) itemView.findViewById(R.id.info_details);

            infoProfileImage = (ImageView) itemView.findViewById(R.id.info_image);

        }
    }
}