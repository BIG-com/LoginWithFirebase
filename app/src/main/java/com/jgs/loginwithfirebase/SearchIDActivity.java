package com.jgs.loginwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class SearchIDActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_idactivity);

        firebaseAuth = FirebaseAuth.getInstance();

        currentEmail = (EditText) findViewById(R.id.currentEmail);

        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user != null && firebaseAuth.getCurrentUser().isEmailVerified()==true) {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                currentEmail.setText(user.getEmail().toString().trim());
                            }
                        }
                    });
        }
        else {
            currentEmail.setText("최근 로그한 기록이 없습니다");
        }







    }
}