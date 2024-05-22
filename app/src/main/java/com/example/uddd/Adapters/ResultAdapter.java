package com.example.uddd.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.uddd.API.RetrofitClient;
import com.example.uddd.Activities.DetailActivity;
import com.example.uddd.Activities.FavouriteFragment;
import com.example.uddd.Activities.MainActivity;
import com.example.uddd.Domains.PopularDomain;
import com.example.uddd.Models.User;
import com.example.uddd.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    ArrayList<PopularDomain> items;
    public ResultAdapter(ArrayList<PopularDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_result, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());
        holder.locationTxt.setText(items.get(position).getAddress());
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String roundedValue = df.format(items.get(position).getAvgStar());
        holder.scoreTxt.setText(roundedValue);
        holder.descriptionTxt.setText(items.get(position).getDescription());
        holder.totalComment.setText("("+items.get(position).getTotalComment()+")");
        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    User user= MainActivity.getUser();
                    if(user == null) return;
                    holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.likeButton.getContext(), R.drawable.love_20px), null);
                    Call<Void> call = RetrofitClient.getInstance().getAPI().addFavour(user.getUserID(),items.get(position).getLocationID());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            FavouriteFragment.initFavouriteLocation();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(),"Fail to connect server.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else
                    holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.likeButton.getContext(), R.drawable.favorite_20px), null);
            }
        });

        int drawableResId = holder.itemView.getResources().getIdentifier(items.get(position).getPhoto(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResId)
                .transform(new CenterCrop(), new GranularRoundedCorners(30,30, 30, 30))
                .into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("object", items.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, locationTxt, scoreTxt, descriptionTxt, totalComment;
        ImageView pic;
        ToggleButton likeButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
            descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
            totalComment = itemView.findViewById(R.id.numOfValuateTxt);
            pic = itemView.findViewById(R.id.picImg);
            likeButton = itemView.findViewById(R.id.btn_like);
        }
    }
}
