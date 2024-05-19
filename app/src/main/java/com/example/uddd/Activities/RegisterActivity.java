package com.example.uddd.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uddd.Domains.CommentDomain;
import com.example.uddd.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    ImageButton backButton, showButton1,showButton2;
    EditText nameBar,usernameBar,passwordBar,confirmBar;
    Button singupButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordBar.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    passwordBar.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else
                    passwordBar.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        showButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmBar.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    confirmBar.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else
                    confirmBar.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameBar.getText().toString();
                String username = usernameBar.getText().toString();
                String password = passwordBar.getText().toString();

                if(!checkValidName(name)||!checkValidUsername(username)||!checkValidPassword())
                    return;

                //Handle register
            }
        });
    }
    void showError(String error)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Error")
                .setMessage(error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    boolean checkValidName(String name)
    {
        if(name.isEmpty())
        {
            showError("Name is empty.");
            return false;
        }
        //Name does not contain number and special character
        String regex = "^[a-zA-Z]+$";
        if(!name.matches(regex))
        {
            showError("Invalid name. Name doesn't contain number and special character. Please try again.");
            return false;
        }
        return true;
    }

    boolean checkValidUsername(String username)
    {
        if(username.isEmpty())
        {
            showError("Username is empty.");
            return false;
        }
        //Username does not contain space
        String regex = "^[^\\s]+$";
        if(!username.matches(regex))
        {
            showError("Invalid username. Username doesn't contain space. Please try again.");
            return false;
        }
        return true;
    }
    boolean checkValidPassword()
    {
        String password = passwordBar.getText().toString();
        String confirm = confirmBar.getText().toString();
        if(password.isEmpty())
        {
            showError("Password is empty.");
            return false;
        }
        if(confirm.isEmpty())
        {
            showError("Confirm password is empty.");
            return false;
        }
        if(!password.equals(confirm))
        {
            showError("Password and confirmation of password aren't the same. Please check your password.");
            return false;
        }
        return true;
    }
    public void initView()
    {
        backButton = findViewById(R.id.btn_back);
        nameBar = findViewById(R.id.name_bar);
        usernameBar = findViewById(R.id.user_name_bar);
        passwordBar = findViewById(R.id.password_bar);
        confirmBar = findViewById(R.id.confirm_password_bar);
        singupButton = findViewById(R.id.btn_signup);
        showButton1 = findViewById(R.id.btn_show1);
        showButton2 = findViewById(R.id.btn_show2);
    }
}