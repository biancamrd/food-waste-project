package com.example.licenta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.R;
import com.example.licenta.classes.RecipeHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecipeHistoryAdapter extends RecyclerView.Adapter<RecipeHistoryAdapter.ViewHolder> {
    private List<RecipeHistory> recipeHistoryList;
    private OnItemClickListener itemClickListener;

    public RecipeHistoryAdapter(List<RecipeHistory> recipeHistoryList) {
        this.recipeHistoryList = recipeHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeHistory recipeHistory = recipeHistoryList.get(position);

        holder.tvRecipeName.setText(recipeHistory.getName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE yyyy/MM/dd", Locale.getDefault());
        String formattedDate = dateFormat.format(recipeHistory.getDate());
        holder.tvRecipeDate.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(String.valueOf(recipeHistory.getApiId()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipeHistoryList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String apiId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        TextView tvRecipeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvRecipeDate = itemView.findViewById(R.id.tv_recipe_date);
        }
    }
}