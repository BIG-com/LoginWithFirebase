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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseFirestore firebaseDB;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;
    private Button btn_search_id;
    private Button btn_search_pwd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);

        btn_search_id = (Button) findViewById(R.id.btn_search_id);
        btn_search_pwd = (Button) findViewById(R.id.btn_search_pwd);



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

        // 아이디 찾기 버튼 클릭시 이벤트
        btn_search_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainToSearchID = new Intent(MainActivity.this, SearchIDActivity.class);
                startActivity(MainToSearchID);
            }
        });

        //-------------------------------------------------------------------------------------------------

        // 아이디 찾기 버튼 클릭시 이벤트
        btn_search_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainToSearchPWD = new Intent(MainActivity.this, SearchPWDActivity.class);
                startActivity(MainToSearchPWD);
            }
        });





        //-------------------------------------------------------------------------------------------------

        //최근에 로그인 했는지 확인해서 자동 로그인

        // NO 1. 버벅임 없이 자동 로그인


        /*FirebaseUser user = firebaseAuth.getCurrentUser();
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
        위의 특이한 사례에 대처가능함
        */

        // 결론 -> NO 2 를 사용하는데 있어 애니메이션을 추가 시켜서 로딩창을 제공함
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && firebaseAuth.getCurrentUser().isEmailVerified()==true) { // 자동으로 로그인 된 케이스
                    //휴대폰의 저장된 토큰이 데이터베이스에 토큰이 지금도 존재하는지 확인
                    user.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()){

                                        //Send token to your backend via HTTPS
                                        //String idToken = task.getResult().getToken();

                                        //유저의 이메일을 토스트
                                        //Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MainActivity.this, LoginSucessActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
                else {// 자동으로 로그인 안된 케이스

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
                        if (task.isSuccessful() == true && firebaseAuth.getCurrentUser().isEmailVerified()==true) {
                            // 로그인 성공
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        }
                        else if (task.isSuccessful() == true && firebaseAuth.getCurrentUser().isEmailVerified()==false) {
                            // 로그인 실패 (이메일 인증 X)
                            Toast.makeText(MainActivity.this, "이메일 인증이 되지 않았습니다", Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful() == false){
                            // 로그인 실패
                            Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }else { }
                    }
                });

    }


    //-------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() { // 앱이 시작될 때 Listener 설정
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onStop() { //앱이 종료될 때 Listener 해제
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}