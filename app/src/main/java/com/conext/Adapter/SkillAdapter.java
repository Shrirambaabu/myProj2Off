package com.conext.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conext.R;
import com.conext.model.Skill;
import com.conext.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shriram on 6/22/2017.
 */

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillHolder> {

    private List<Skill> skillList;
    private Context context;
    private int itemResource;
    private ArrayList<String> usersSkillList = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    ItemClickListener itemClickListener;
    int pos;

    public SkillAdapter(Context context, int itemResource, List<Skill> skillList) {
        this.skillList = skillList;
        this.context = context;
        this.itemResource = itemResource;

        for (int i = 0; i < skillList.size(); i++) {

            selectedItems.put(i, true);

            if (usersSkillList.size() > 0 && usersSkillList != null) {
                if (!usersSkillList.contains(skillList.get(i).getTitle())) {
                    usersSkillList.add(skillList.get(i).getTitle());
                }
            } else {
                usersSkillList.add(skillList.get(i).getTitle());
            }
        }
    }


    public SkillAdapter(Context context, int itemResource, List<Skill> skillList, ItemClickListener itemClickListener) {
        this.skillList = skillList;
        this.context = context;
        this.itemResource = itemResource;
        this.itemClickListener = itemClickListener;

        for (int i = 0; i < skillList.size(); i++) {

            selectedItems.put(i, true);

            if (usersSkillList.size() > 0 && usersSkillList != null) {
                if (!usersSkillList.contains(skillList.get(i).getTitle())) {
                    usersSkillList.add(skillList.get(i).getTitle());
                }
            } else {
                usersSkillList.add(skillList.get(i).getTitle());
            }
        }
    }

    public List<String> getSelectedItems() {
        return usersSkillList;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public SkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
        return new SkillHolder(v);
    }

    @Override
    public void onBindViewHolder(final SkillHolder holder, final int position) {

        pos = position;

        final Skill skill = skillList.get(pos);
        holder.skillName.setText(skill.getTitle());

        holder.skillName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When selecting an item, save it
                selectedItems.put(position, true);
                notifyDataSetChanged();

                if (usersSkillList.size() > 0 && usersSkillList != null) {
                    if (!usersSkillList.contains(skillList.get(position).getTitle())) {
                        usersSkillList.add(skillList.get(position).getTitle());
                    }
                } else {
                    usersSkillList.add(skillList.get(position).getTitle());
                }
                itemClickListener.onItemClick(view, (int) skillList.get(position).getSkillId());
            }
        });

        if (selectedItems.get(position)) {
            holder.myBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.skill_highlight));
            holder.skillName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.myBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_card_selected));
            holder.skillName.setTextColor(ContextCompat.getColor(context, R.color.am));
        }

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When selecting an item, save it
                selectedItems.delete(position);
                notifyDataSetChanged();

                if (usersSkillList.size() > 0 && usersSkillList != null) {
                    if (usersSkillList.contains(skillList.get(position).getTitle())) {
                        usersSkillList.remove(skillList.get(position).getTitle());
                        itemClickListener.onItemClick(v, (int) skillList.get(position).getSkillId());
                    }
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return this.skillList.size();
    }

    class SkillHolder extends RecyclerView.ViewHolder {

        private TextView skillName;
        private ImageView img;
        private RelativeLayout myBackground;

        SkillHolder(View itemView) {
            super(itemView);
            // Set up the UI widgets of the holder
            this.img = (ImageView) itemView.findViewById(R.id.deselect_skill);
            this.skillName = (TextView) itemView.findViewById(R.id.skill_name);
            this.myBackground = (RelativeLayout) itemView.findViewById(R.id.background);

            //itemView.setOnClickListener(this);

        }/*

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, (int) skillList.get(getAdapterPosition()).getSkillId());
        }
*/
    }
}