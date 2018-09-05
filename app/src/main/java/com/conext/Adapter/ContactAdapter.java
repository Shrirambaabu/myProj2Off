package com.conext.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.conext.R;
import com.conext.model.Contact;
import com.conext.ui.OtherProfileActivity;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.util.List;
import java.util.Objects;

/**
 * Created by Ashith VL on 6/24/2017.
 */


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    public List<Contact> contactList;
    private int mRowIndex = -1;

    private Context context;
    private int itemResource;

    public ContactAdapter(Context context, int itemResource, List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
        this.itemResource = itemResource;
    }

    public void setContactList(List<Contact> dataContactList) {
        if (contactList != dataContactList) {
            contactList = dataContactList;
            notifyDataSetChanged();
        }
    }

    public void setmRowIndex(int index) {
        mRowIndex = index;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
        return new ContactHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.img.setImageBitmap(Utility.getPhoto(contact.getImage()));
        holder.itemView.setTag(position);
/*
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Context context = v.getContext();

                if (!Objects.equals(contactList.get(position).getId(), Prefs.getUserKey())) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setMessage("Are you sure you want to delete this contact?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    contactList.remove(position);
                                    notifyItemRemoved(position);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.equals(contactList.get(position).getId(), Prefs.getUserKey())) {
                    Intent intent = new Intent(context, OtherProfileActivity.class);
                    intent.putExtra("id", contactList.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return this.contactList.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder {

        //  private ImageView img;
        private HexagonMaskView img;

        ContactHolder(View itemView) {
            super(itemView);
            // Set up the UI widgets of the holder
            this.img = (HexagonMaskView) itemView.findViewById(R.id.add_contact_image);
        }

    }


}