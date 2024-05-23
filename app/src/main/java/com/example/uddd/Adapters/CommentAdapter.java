package com.example.uddd.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uddd.API.RetrofitClient;
import com.example.uddd.Activities.DetailActivity;
import com.example.uddd.Activities.MainActivity;
import com.example.uddd.Activities.ViewProfileActivity;
import com.example.uddd.Domains.CommentDomain;
import com.example.uddd.Models.User;
import com.example.uddd.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<CommentDomain> items;
    public CommentAdapter(ArrayList<CommentDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_comment, parent, false);
        return new ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Call<User> call = RetrofitClient.getInstance().getAPI().getUserByID(items.get(position).getUserID());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                 User commentUser = response.body();

                 if(commentUser != null)
                 {
                     holder.username.setText(commentUser.getName());
                     Uri avatarUri = Uri.parse(commentUser.getAvatar());
                     Glide.with(holder.itemView.getContext())
                             .load(avatarUri)
                             .into(holder.avatar);
                     holder.avatar.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent intent = new Intent(holder.itemView.getContext(), ViewProfileActivity.class);
                             intent.putExtra("user",commentUser);
                             holder.itemView.getContext().startActivity(intent);
                         }
                     });
                 }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(),"Fail to connect to server from comment adapter.",Toast.LENGTH_LONG ).show();
            }
        });

        holder.commentTxt.setText(items.get(position).getContent());
        holder.dateTxt.setText(items.get(position).getDate());
        holder.num_like.setText("("+items.get(position).getLikes()+")");
        holder.num_dislike.setText("("+items.get(position).getDislikes()+")");
        holder.score_bar.setRating(items.get(position).getNumStar());
        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Call<Void> call = RetrofitClient.getInstance().getAPI().likeComment(items.get(position).getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            items.get(position).setLikes(items.get(position).getLikes()+1);
                            holder.num_like.setText("("+String.valueOf(items.get(position).getLikes())+")");
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(),"Fail to connect to server from comment adapter.",Toast.LENGTH_LONG ).show();
                        }
                    });
                }
                else
                {
                    Call<Void> call = RetrofitClient.getInstance().getAPI().undoLike(items.get(position).getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            items.get(position).setLikes(items.get(position).getLikes()-1);
                            holder.num_like.setText("("+String.valueOf(items.get(position).getLikes())+")");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(),"Fail to connect to server from comment adapter.",Toast.LENGTH_LONG ).show();
                        }
                    });

                }
            }
        });
        holder.dislikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Call<Void> call = RetrofitClient.getInstance().getAPI().dislikeComment(items.get(position).getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            items.get(position).setDislikes(items.get(position).getDislikes()+1);
                            holder.num_dislike.setText("("+String.valueOf(items.get(position).getDislikes())+")");
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(),"Fail to connect to server from comment adapter.",Toast.LENGTH_LONG ).show();
                        }
                    });
                }
                else
                {
                    Call<Void> call = RetrofitClient.getInstance().getAPI().undoDislike(items.get(position).getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            items.get(position).setDislikes(items.get(position).getDislikes()-1);
                            holder.num_dislike.setText("("+String.valueOf(items.get(position).getDislikes())+")");
                            DetailActivity.updateComment();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(),"Fail to connect to server from comment adapter.",Toast.LENGTH_LONG ).show();
                        }
                    });

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(CommentDomain item)
    {
        items.add(item);
        notifyItemInserted(items.size()-1);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, commentTxt, dateTxt ,num_like, num_dislike;
        RatingBar score_bar;
        ImageView avatar;
        ToggleButton likeButton,dislikeButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            commentTxt = itemView.findViewById(R.id.commentTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            num_like = itemView.findViewById(R.id.num_like);
            num_dislike  = itemView.findViewById(R.id.num_dislike);
            score_bar = itemView.findViewById(R.id.score_bar);
            avatar = itemView.findViewById(R.id.avatar);
            likeButton = itemView.findViewById(R.id.btn_agree);
            dislikeButton = itemView.findViewById(R.id.btn_dissagree);
        }
    }
}
