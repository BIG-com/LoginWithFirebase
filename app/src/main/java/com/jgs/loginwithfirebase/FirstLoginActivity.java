package com.jgs.loginwithfirebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirstLoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button btn_auth_email_resend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        //--------------------------------------------------------------------------------------------

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);

        btn_auth_email_resend = (Button) findViewById(R.id.btn_auth_email_resend);

        //--------------------------------------------------------------------------------------------

        // 이 창으로 넘어오면 인증 이메일 발송
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(FirstLoginActivity.this, "인증 이메일이 발송 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //--------------------------------------------------------------------------------------------

        // 이메일 재전송 버튼 클릭 시 인증 이메일 발송
        btn_auth_email_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Email resent.");
                        Toast.makeText(FirstLoginActivity.this, "인증 이메일이 재발송 되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //--------------------------------------------------------------------------------------------

        //로그인 버튼 클릭시 이벤트
        buttonLogIn = (Button) findViewById(R.id.btn_login);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    Toast.makeText(FirstLoginActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //--------------------------------------------------------------------------------------------

    }

    //파이어베이스 정보와 입력한 값이랑 같으면 로그인
    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && firebaseAuth.getCurrentUser().isEmailVerified()==true) {
                            // 로그인 성공
                            Toast.makeText(FirstLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            Intent FirstLoginToLoginSuccess = new Intent(FirstLoginActivity.this, LoginSucessActivity.class);
                            FirstLoginToLoginSuccess.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                            startActivity(FirstLoginToLoginSuccess);
                            finish();


                        } else if (firebaseAuth.getCurrentUser().isEmailVerified()==false) {
                            Toast.makeText(FirstLoginActivity.this, "이메일 인증이 되지 않았습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            // 로그인 실패
                            Toast.makeText(FirstLoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //--------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {

        firebaseAuth.getCurrentUser().delete();

        super.onBackPressed();
    }
}