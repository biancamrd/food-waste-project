
package com.example.licenta.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.R;
import com.example.licenta.classes.Recipe;
import com.example.licenta.activities.RecipeActivity;
import com.example.licenta.activities.ShowSavedRecipeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapterSearchResult extends RecyclerView.Adapter<RecyclerViewAdapterSearchResult.MyViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private int activityType;

    public RecyclerViewAdapterSearchResult(Context mContext, List<Recipe> mData, int activityType) {
        this.context = mContext;
        this.recipes = mData;
        this.activityType = activityType;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.cardview_item_search_result, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvRecipeTitle.setText(recipes.get(position).getTitle());
        if (recipes.get(position).getThumbnail().isEmpty()) {
            holder.imageView.setImageResource(R.drawable.nopicture);
        } else{
            Picasso.get().load(recipes.get(position).getThumbnail()).into(holder.imageView);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityType == 1){
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", recipes.get(position).getId());
                    intent.putExtra("title", recipes.get(position).getTitle());
                    intent.putExtra("img", recipes.get(position).getThumbnail());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if (activityType == 2) {
                    Intent intent = new Intent(context, ShowSavedRecipeActivity.class);
                    intent.putExtra("id", recipes.get(position).getRecipeId());
                    intent.putExtra("title", recipes.get(position).getTitle());
                    intent.putExtra("img", recipes.get(position).getThumbnail());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecipeTitle;
        ImageView imageView;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.search_result_recipe_title);
            imageView = itemView.findViewById(R.id.search_result_recipe_img);
            cardView = itemView.findViewById(R.id.search_result_cardview);
        }
    }
}
