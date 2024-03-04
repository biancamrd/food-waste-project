package com.example.licenta.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.R;
import com.example.licenta.classes.Ingredient;
import com.example.licenta.activities.StorageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients;
    private Context context;
    private List<Ingredient> allIngredients;


    public IngredientAdapter(StorageActivity storageActivity, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = storageActivity;
        this.allIngredients = new ArrayList<>(ingredients);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        CheckBox ingredientCheckBox = view.findViewById(R.id.ingredientCheckBox);
        IngredientViewHolder viewHolder = new IngredientViewHolder(view);
        ingredientCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Ingredient ingredient = ingredients.get(adapterPosition);
                ingredient.setSelected(isChecked);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientNameTextView.setText(ingredient.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String expirationDateStr = dateFormat.format(ingredient.getExpirationDate());
        holder.ingredientExpirationDateTextView.setText(expirationDateStr);
        holder.deleteImageButton.setTag(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIngredientDetailsPopup(ingredient);
            }
        });

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.deleteImageButton.getTag();
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private void showIngredientDetailsPopup(Ingredient ingredient) {

        View popupView = LayoutInflater.from(context).inflate(R.layout.ingredient_details_popup, null);

        TextView nameTextView = popupView.findViewById(R.id.ingredientNameTextView);
        TextView quantityTextView = popupView.findViewById(R.id.ingredientQuantityTextView);
        TextView unitTextView = popupView.findViewById(R.id.ingredientUnitTextView);
        TextView expirationDateTextView = popupView.findViewById(R.id.ingredientExpirationDateTextView);

        nameTextView.setText(ingredient.getName());
        quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
        unitTextView.setText(ingredient.getUnit());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String expirationDateStr = dateFormat.format(ingredient.getExpirationDate());
        expirationDateTextView.setText(expirationDateStr);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public List<Ingredient> getSelectedIngredients() {
        List<Ingredient> selectedIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isSelected()) {
                selectedIngredients.add(ingredient);
            }
        }
        return selectedIngredients;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientNameTextView;
        TextView ingredientExpirationDateTextView;
        ImageButton deleteImageButton;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
            ingredientExpirationDateTextView = itemView.findViewById(R.id.ingredientExpirationDate);
            deleteImageButton = itemView.findViewById(R.id.deleteIngredientButton);
        }
    }

    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    public void filterIngredients(List<Ingredient> filteredIngredients) {
        ingredients.clear();
        if (filteredIngredients.isEmpty()) {
            ingredients.addAll(allIngredients);
        } else {
            ingredients.addAll(filteredIngredients);
        }
        notifyDataSetChanged();
    }

}

