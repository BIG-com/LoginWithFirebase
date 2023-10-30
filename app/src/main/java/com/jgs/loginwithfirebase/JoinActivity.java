package com.jgs.loginwithfirebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDB;
    private Map<String, Object> UsersInfo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editText_name;
    private EditText editText_age;
    private EditText editText_sex;
    private Button btn_join;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //-------------------------------------------------------------------------------------------------

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseFirestore.getInstance();
        UsersInfo = new HashMap<>();

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editText_name = findViewById(R.id.editText_name);
        editText_age = findViewById(R.id.editText_age);
        editText_sex = findViewById(R.id.editText_sex);

        btn_join = (Button) findViewById(R.id.btn_join);


        //-------------------------------------------------------------------------------------------------

        //가입 버튼 클릭시 공백 확인
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // 이름이 공백이 아닌 경우
                    createUser(
                            editTextEmail.getText().toString().trim(),
                            editTextPassword.getText().toString().trim() );

                    //editText_name.getText().toString().trim()
                    UsersInfo.put("Email", editTextEmail.getText().toString().trim());
                    UsersInfo.put("Password", editTextPassword.getText().toString().trim());
                    UsersInfo.put("Name", editText_name.getText().toString().trim());
                    UsersInfo.put("Age", Integer.parseInt(editText_age.getText().toString().trim()));
                    UsersInfo.put("Sex", editText_sex.getText().toString().trim());
                    UsersInfo.put("SubscriptionStatus", true);

                    // 경로 ( UsersData / firebaseAuth.getUid() / Email,Password,Name,Age,Sex,SubscriptionStatus
                    firebaseDB.collection("UsersData").document(editTextEmail.getText().toString().trim())
                            .set(UsersInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "데이터베이스 저장 완료");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "데이터 베이스 저장 실패");
                                }
                            });

                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(JoinActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //--------------------------------------------------------------------------------------------------

    }

    //-------------------------------------------------------------------------------------------------

    //파이어 베이스 DB에 계정 중복 확인
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // 회원가입 성공시
                            Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent JoinToUserinfo = new Intent(JoinActivity.this, FirstLoginActivity.class);
                            startActivity(JoinToUserinfo);
                        } else {
                            // 계정이 중복된 경우
                            Toast.makeText(JoinActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}