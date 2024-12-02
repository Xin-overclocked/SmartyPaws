package com.example.smartypaws;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<FlashcardSet> flashcardSetList;
    private OnFlashcardSetClickListener listener;

    public interface OnFlashcardSetClickListener {
        void onFlashcardSetClick(FlashcardSet flashcardSet);
    }

    public FlashcardAdapter(List<FlashcardSet> flashcardSetList, OnFlashcardSetClickListener listener) {
        this.flashcardSetList = flashcardSetList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        FlashcardSet flashcardSet = flashcardSetList.get(position);
        holder.titleTextView.setText(flashcardSet.getTitle());
        holder.descriptionTextView.setText(flashcardSet.getDescription());
        holder.bind(flashcardSet, listener);
    }

    @Override
    public int getItemCount() {
        return flashcardSetList.size();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        void bind(final FlashcardSet flashcardSet, final OnFlashcardSetClickListener listener) {
            titleTextView.setText(flashcardSet.getTitle());
            descriptionTextView.setText(flashcardSet.getDescription());
            itemView.setOnClickListener(v -> listener.onFlashcardSetClick(flashcardSet));
        }
    }
}