package com.antispam.phone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antispam.phone.R;
import com.antispam.phone.data.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;

public class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.WhitelistHolder> {
    private List<WhitelistEntry> entries = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public WhitelistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.whitelist_item, parent, false);
        return new WhitelistHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WhitelistHolder holder, int position) {
        WhitelistEntry currentEntry = entries.get(position);
        holder.textViewName.setText(currentEntry.getName());
        holder.textViewPhoneNumber.setText(currentEntry.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<WhitelistEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    class WhitelistHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewPhoneNumber;
        private ImageView imageDelete;

        public WhitelistHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPhoneNumber = itemView.findViewById(R.id.text_view_phone_number);
            imageDelete = itemView.findViewById(R.id.image_delete);

            imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(entries.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(WhitelistEntry entry);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}