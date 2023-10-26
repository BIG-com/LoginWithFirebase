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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private Button buttonJoin;
    private FirebaseFirestore firebaseDB;
    private Map<String, Object> UsersInfo;

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
        editTextName = (EditText) findViewById(R.id.editText_name);

        buttonJoin = (Button) findViewById(R.id.btn_join);

        //-------------------------------------------------------------------------------------------------

        //가입 버튼 클릭시 공백 확인
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // 이름이 공백이 아닌 경우
                    if (editTextName.getText().toString() != null) {
                        createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextName.getText().toString());

                        //--------------------------------------------------------------------------------------------------
                        // 사용자의 정보 데이터 베이스에 저장
                        UsersInfo.put("Email", editTextEmail.getText().toString().trim());
                        UsersInfo.put("Password", editTextPassword.getText().toString().trim());
                        UsersInfo.put("Name", editTextName.getText().toString().trim());
                        UsersInfo.put("Age", 28);
                        UsersInfo.put("Sex", "남자");
                        UsersInfo.put("SubscriptionStatus", true);

                        // 경로 ( UsersData / firebaseAuth.getUid() / Email,Password,Name,Age,Sex,SubscriptionStatus
                        firebaseDB.collection("UsersData").document(firebaseAuth.getUid())
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
                    }
                    //--------------------------------------------------------------------------------------------------

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
    private void createUser(String email, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent JoinToEmail = new Intent(JoinActivity.this, FirstLoginActivity.class);
                            startActivity(JoinToEmail);
                        } else {
                            // 계정이 중복된 경우
                            Toast.makeText(JoinActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}