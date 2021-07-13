package com.indianapp.chatapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.indianapp.chatapp.Models.Messages;
import com.indianapp.chatapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter {

    private ArrayList<Messages> messagesArrayList;
    private Context ctx;

    private int ITEM_SEND = 1;
    private int ITEM_RECIVE = 2;

    private long today;
    private long yesterday;

    private String senderUrl;
    private String receiverUrl;
    private String finalDate;

    SimpleDateFormat sfd;
    SimpleDateFormat sf;


    public ChatAdapter(Context ctx, ArrayList<Messages> messagesArrayList, String senderUrl, String receiverUrl) {
        this.ctx = ctx;
        this.messagesArrayList = messagesArrayList;
        this.senderUrl = senderUrl;
        this.receiverUrl = receiverUrl;
        sfd = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault());
        sf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        try {
            today = sfd.parse(sfd.format(new Date())).getTime();
            yesterday = sfd.parse(sfd.format(new Date())).getTime() - 1000 * 60 * 60 * 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getFinalDate(Date d) {
        if (d.getTime() == today) {
            return "Today";
        } else if (d.getTime() == yesterday) {
            return "Yesterday";
        } else {
            return sfd.format(d);
        }
    }

    private void setDateOnUi(int position, TextView holderDate, String dateS) {
        if (position == 0) {
            try {
                holderDate.setVisibility(View.VISIBLE);
                finalDate = getFinalDate(sfd.parse(dateS));
                holderDate.setText(finalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Messages message0 = messagesArrayList.get(position - 1);
            Long timeStamp0 = Long.valueOf(message0.getTimeStamp());
            Date date0 = new Date(timeStamp0);
            String dateS0 = sfd.format(date0);
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sfd.parse(dateS);
                d2 = sfd.parse(dateS0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (d1.getTime() - d2.getTime() > 0) {
                holderDate.setVisibility(View.VISIBLE);
                finalDate = getFinalDate(d1);
                holderDate.setText(finalDate);
            } else {
                holderDate.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.custom_sender_layout_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(ctx).inflate(R.layout.custom_receiver_layout_item, parent, false);
            return new ReciverViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages message = messagesArrayList.get(position);
        Long timeStamp = Long.valueOf(message.getTimeStamp());
        Date date = new Date(timeStamp);
        String dateS = sfd.format(date);
        String timeS = sf.format(date);

        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.txtmessage.setText(message.getMessage());
            viewHolder.time.setText(timeS);
            Glide.with(ctx)
                    .load(senderUrl)
                    .error(R.drawable.blank_profile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(((SenderViewHolder) holder).circleImageView);
            setDateOnUi(position, viewHolder.date, dateS);
            if (message.getMessageStatus().equals("sent")) {
                viewHolder.tickImg.setImageResource(R.drawable.ic_single_tick);
            } else if (message.getMessageStatus().equals("delivered")) {
                viewHolder.tickImg.setImageResource(R.drawable.ic_double_tick_received);
            } else if (message.getMessageStatus().equals("read")) {
                viewHolder.tickImg.setImageResource(R.drawable.ic_resource_double);
            } else {
                viewHolder.tickImg.setImageResource(0);
            }
        } else if (holder.getClass() == ReciverViewHolder.class) {
            ReciverViewHolder viewHolder = (ReciverViewHolder) holder;
            viewHolder.txtmessage.setText(message.getMessage());
            viewHolder.time.setText(timeS);
            Glide.with(ctx)
                    .load(receiverUrl)
                    .error(R.drawable.blank_profile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(((ReciverViewHolder) holder).circleImageView);
            setDateOnUi(position, viewHolder.date, dateS);

        }
    }


    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messagesArrayList.get(position);
        Log.i("debug", String.valueOf(position));

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView txtmessage;
        TextView time;
        TextView date;
        ImageView tickImg;
        CircleImageView circleImageView;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tickImg = itemView.findViewById(R.id.tickImg);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.txtMessages);

        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView txtmessage;
        TextView time;
        TextView date;

        CircleImageView circleImageView;


        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.txtMessages);

        }
    }

}
