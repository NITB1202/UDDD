package com.example.uddd.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uddd.API.RetrofitClient;
import com.example.uddd.Adapters.CommentAdapter;
import com.example.uddd.Domains.CommentDomain;
import com.example.uddd.Domains.PopularDomain;
import com.example.uddd.Models.User;
import com.example.uddd.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private static int locationID = -1;
    private TextView titleTxt, locationTxt,descriptionTxt, scoreTxt, totalComment;
    private PopularDomain item;
    private ImageView picImg;
    private ImageButton backButton, yesButton, noButton;
    private ToggleButton likeButton;
    private static RecyclerView recyclerView;
    private LinearLayout askLayout;
    private ConstraintLayout commentLayout;
    private Button commentButton,cancelButton;
    private User user;
    private EditText comment;
    private RatingBar ratingBar;
    private ImageView avatar;
    private static CommentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        setVariable();
        InitCommentView();

        user = MainActivity.getUser();

        if(user == null)
            Glide.with(this).load(Uri.parse(getString(R.string.basicAvatar))).into(avatar);
        else
            Glide.with(this).load(Uri.parse(user.getAvatar())).into(avatar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(DetailActivity.this, R.drawable.heart_30px), null);
                else
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(DetailActivity.this, R.drawable.heart_black_30px), null);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askLayout.setVisibility(View.GONE);
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askLayout.setVisibility(View.GONE);
                commentLayout.setVisibility(View.VISIBLE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentLayout.setVisibility(View.GONE);
            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null)
                {
                    askLogin();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to post this comment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                Date today = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String formattedDate = dateFormat.format(today);

                                //Hanlde post comment
                                CommentDomain commentPost = new CommentDomain(0,user.getUserID(),locationID,formattedDate,ratingBar.getRating(),comment.getText().toString(),0,0);
                                adapter.addItem(commentPost);
                                commentLayout.setVisibility(View.GONE);

                                //Update to database
                                Call<Void> call = RetrofitClient.getInstance().getAPI().postComment(user.getUserID(),locationID,formattedDate,ratingBar.getRating(),comment.getText().toString());
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(DetailActivity.this,"Post comment successfully.",Toast.LENGTH_LONG).show();

                                        //Update current screen
                                        totalComment.setText("("+String.valueOf(item.getTotalComment()+1)+")");

                                        float point = (item.getAvgStar()*item.getTotalComment()+ratingBar.getRating())/(item.getTotalComment()+1);
                                        DecimalFormat df = new DecimalFormat("#.#");
                                        df.setRoundingMode(RoundingMode.HALF_UP);
                                        String roundedValue = df.format(point);
                                        scoreTxt.setText(roundedValue);

                                        //Update local
                                        MainActivity.UpdateUser();
                                        HomeFragment.intiLocationInfo();
                                        updateComment();
                                    }
                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(DetailActivity.this,"Post comment fail.",Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    public void InitCommentView() {
        recyclerView = findViewById(R.id.view_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (locationID == -1) {
            Toast.makeText(DetailActivity.this, "Can't identify locationID", Toast.LENGTH_LONG).show();
            return;
        }

        updateComment();
    }

    public static void updateComment()
    {
        Call<ArrayList<CommentDomain>> call = RetrofitClient.getInstance().getAPI().getComment(locationID);
        call.enqueue(new Callback<ArrayList<CommentDomain>>() {
            @Override
            public void onResponse(Call<ArrayList<CommentDomain>> call, Response<ArrayList<CommentDomain>> response) {
                ArrayList<CommentDomain> itemList = new ArrayList<>();
                if(response.body()!= null)
                    itemList.addAll(response.body());
                adapter = new CommentAdapter(itemList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<CommentDomain>> call, Throwable t) {
                //Toast.makeText(DetailActivity.this,"Fail to connect to server",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setVariable()
    {
        item = (PopularDomain) getIntent().getSerializableExtra("object");

        locationID = item.getLocationID();
        titleTxt.setText(item.getName());
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String roundedValue = df.format(item.getAvgStar());
        scoreTxt.setText(roundedValue);
        locationTxt.setText(item.getAddress());
        descriptionTxt.setText(item.getDescription());
        totalComment.setText("("+item.getTotalComment()+")");

        int drawableResId = getResources().getIdentifier(item.getPhoto(),"drawable",getPackageName());

        Glide.with(this)
                .load(drawableResId)
                .into(picImg);
    }

    private void initView()
    {
        titleTxt = findViewById(R.id.titleTxt);
        locationTxt = findViewById(R.id.locationTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        totalComment = findViewById(R.id.numOfValuateTxt);
        backButton = findViewById(R.id.btn_back);
        picImg = findViewById(R.id.picImg);
        likeButton = findViewById(R.id.btn_like);
        yesButton = findViewById(R.id.btn_yes);
        noButton = findViewById(R.id.btn_no);
        askLayout = findViewById(R.id.ask_layout);
        commentLayout = findViewById(R.id.comment_layout);
        commentButton = findViewById(R.id.btn_comment);
        cancelButton = findViewById(R.id.btn_cancel);
        comment = findViewById(R.id.comment);
        ratingBar = findViewById(R.id.ratingBar);
        avatar = findViewById(R.id.avatar);
    }
    private void askLogin()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("Error")
                .setMessage("You must login to use this feature.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(DetailActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}