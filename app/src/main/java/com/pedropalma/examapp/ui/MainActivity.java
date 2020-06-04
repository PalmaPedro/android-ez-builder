package com.pedropalma.examapp.ui;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.pedropalma.examapp.R;
import com.pedropalma.examapp.auth.SignupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // method to handle button click
    public void goToSignup(View view){
        Intent intent=new Intent(this, SignupActivity.class);
        startActivity(intent);

    }
}