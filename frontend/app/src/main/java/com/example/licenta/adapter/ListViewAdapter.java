
package com.example.licenta.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.R;
import com.example.licenta.classes.Recipe;
import com.example.licenta.activities.RecipeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.MyViewHolder> {

    private Context context;
    private List<Recipe> recipes;

    public ListViewAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.cardview_item_recipe,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvRecipeTitle.setText(recipes.get(holder.getAdapterPosition()).getTitle());
        holder.tvAmountOfDishes.setText(Integer.toString(recipes.get(holder.getAdapterPosition()).getAmountOfDishes()));
        holder.tvReadyInMins.setText(Integer.toString(recipes.get(holder.getAdapterPosition()).getReadyInMins()));
        if (recipes.get(holder.getAdapterPosition()).getThumbnail().isEmpty()) {
            holder.view.setImageResource(R.drawable.nopicture);
        } else {
            Picasso.get().load(recipes.get(holder.getAdapterPosition()).getThumbnail()).into(holder.view);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", recipes.get(position).getId());
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

        TextView tvRecipeTitle, tvAmountOfDishes, tvReadyInMins;
        ImageView view;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRecipeTitle = (TextView) itemView.findViewById(R.id.recipe_title_id) ;
            view = (ImageView) itemView.findViewById(R.id.recipe_img_id);
            tvAmountOfDishes = (TextView) itemView.findViewById(R.id.servingTvLeft);
            tvReadyInMins = (TextView) itemView.findViewById(R.id.readyInTvRight);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}
