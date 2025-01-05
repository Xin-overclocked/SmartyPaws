package com.example.smartypaws;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {
    private final List<FlashcardSet> flashcardSets;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public FlashcardAdapter(List<FlashcardSet> flashcardSets) {
        this.flashcardSets = flashcardSets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlashcardSet flashcardSet = flashcardSets.get(position);
        holder.titleTextView.setText(flashcardSet.getTitle());
        holder.descriptionTextView.setText(flashcardSet.getDescription());

        // Set the checkbox state
        holder.checkBox.setChecked(selectedPosition == position);

        holder.checkBox.setOnClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                int previousSelectedPosition = selectedPosition;
                if (selectedPosition == clickedPosition) {
                    // If the clicked item is already selected, unselect it
                    selectedPosition = RecyclerView.NO_POSITION;
                } else {
                    // Otherwise, select the clicked item
                    selectedPosition = clickedPosition;
                }
                // Notify the adapter of the changes
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);

                // Retrieve the FlashcardSet ID
                FlashcardSet selectedFlashcardSet = flashcardSets.get(clickedPosition);
                String flashcardSetId = selectedFlashcardSet.getId(); // Using String ID

                // Perform any action with the ID
                System.out.println("Selected FlashcardSet ID: " + flashcardSetId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashcardSets.size();
    }

    public FlashcardSet getSelectedFlashcardSet() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return flashcardSets.get(selectedPosition);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}

