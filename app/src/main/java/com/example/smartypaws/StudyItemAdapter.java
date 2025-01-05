package com.example.smartypaws;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudyItemAdapter extends RecyclerView.Adapter<StudyItemAdapter.ViewHolder> {
    private List<StudyItem> studyItems;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(StudyItem item);
    }

    public StudyItemAdapter(List<StudyItem> studyItems, OnItemClickListener listener) {
        this.studyItems = studyItems;
        this.listener = listener;
    }

    public void updateData(List<StudyItem> newData) {
        this.studyItems = newData;
        notifyDataSetChanged();
    }

    public List<StudyItem> getItems() {
        return studyItems;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudyItem item = studyItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return studyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        void bind(StudyItem item, OnItemClickListener listener) {
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
