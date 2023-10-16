package com.jgs.loginwithfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);

        //-------------------------------------------------------------------------------------------------

        //회원가입 버튼 클릭시 이벤트
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity 연결
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        //-------------------------------------------------------------------------------------------------


        //로그인 버튼 클릭시 이벤트
        buttonLogIn = (Button) findViewById(R.id.btn_login);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //-------------------------------------------------------------------------------------------------

        //최근에 로그인 했는지 확인해서 자동 로그인

        // NO 1. 버벅임 없이 자동 로그인
        /*
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is signed in
            Intent intent = new Intent(MainActivity.this, LoginSucessActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            // no user is signed in
        }*/


        // NO 2. 버벅임 있는데 자동로그인
        /*
        getCurrentUser가 null이 아닌 FirebaseUser를 반환하지만 기본 토큰이 유효하지 않은 경우가 있습니다.
        예를 들어 사용자가 다른 기기에서 삭제되었는데 로컬 토큰을 새로고침하지 않은 경우가 여기에 해당합니다.
        이 경우 유효한 사용자 getCurrentUser를 가져올 수 있지만, 인증 리소스에 대한 후속 호출이 실패합니다.
        위의 특이한 사례에 대처가능함*/
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, LoginSucessActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                }
            }
        };
    }

    //-------------------------------------------------------------------------------------------------

    //파이어베이스 정보와 입력한 값이랑 같으면 로그인
    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //-------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}