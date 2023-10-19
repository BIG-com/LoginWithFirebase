package com.jgs.loginwithfirebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailAuthActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button btn_auth_email_resend;
    private Button btn_auth_email_complete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Button btn_auth_email_resend = (Button) findViewById(R.id.btn_auth_email_resend);
        Button btn_auth_email_complete = (Button) findViewById(R.id.btn_auth_email_complete);


        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(EmailAuthActivity.this, "인증 이메일이 발송 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        btn_auth_email_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Email resent.");
                        Toast.makeText(EmailAuthActivity.this, "인증 이메일이 재발송 되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btn_auth_email_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(EmailAuthActivity.this, "이메일 인증이 되었습니다", Toast.LENGTH_SHORT).show();
                    //Intent EmailToMain = new Intent(EmailAuthActivity.this, MainActivity.class);
                    //startActivity(EmailToMain);
                    //finish();
                }
                else {
                    Toast.makeText(EmailAuthActivity.this, "재전송 버튼 눌러주세요", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}