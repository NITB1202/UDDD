package com.example.uddd.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uddd.API.RetrofitClient;
import com.example.uddd.Activities.MainActivity;
import com.example.uddd.Domains.SavedDomain;
import com.example.uddd.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {

    ArrayList<SavedDomain> items;

    public SavedAdapter(ArrayList<SavedDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SavedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_saved_location, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAdapter.ViewHolder holder, int position) {
        holder.savedLocationNameTxt.setText(items.get(position).getName());
        holder.savedLocationTxt.setText(items.get(position).getAddress());
        holder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this location?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Call<Void> call = RetrofitClient.getInstance().getAPI().deleteSaved(MainActivity.getUser().getUserID(),items.get(position).getName());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                items.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,items.size());
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.savedLocationTxt.setEnabled(true);
            }
        });

        holder.savedLocationTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to save this location?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hide the keyboard
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            v.setEnabled(false);

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
    }

    public void addItem(SavedDomain item)
    {
        items.add(item);
        notifyItemInserted(items.size()-1);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView savedLocationNameTxt;
        EditText savedLocationTxt;
        ImageButton subtractButton,changeButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedLocationTxt = itemView.findViewById(R.id.savedLocationTxt);
            savedLocationNameTxt = itemView.findViewById(R.id.savedLocationNameTxt);
            changeButton = itemView.findViewById(R.id.btn_change_address);
            subtractButton = itemView.findViewById(R.id.btn_subtract);
        }
    }
}
