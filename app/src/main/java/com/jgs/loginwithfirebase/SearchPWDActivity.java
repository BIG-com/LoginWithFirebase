package com.jgs.loginwithfirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class SearchPWDActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText pwdFindCheckEmail;
    private TextView pwdResetEmail;
    private String emailAddress;
    private Button pwdFindBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pwdactivity);

        firebaseAuth = FirebaseAuth.getInstance();

        pwdFindCheckEmail = (EditText) findViewById(R.id.pwdFindCheckEmail);
        pwdResetEmail = (TextView) findViewById(R.id.pwdResetEmail);

        pwdFindBtn = (Button) findViewById(R.id.pwdFindBtn);

        //비밀번호 찾기 버튼 클릭시 이벤트
        pwdFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // EditText에 쓴 이메일을 emailAdress 매개변수로 가져오기
                emailAddress = pwdFindCheckEmail.getText().toString().trim();
                // 이메일 공백 예외처리
                if (emailAddress != null){
                    // 파이어베이스 비밀번호 변경 이메일 전송 코드
                    firebaseAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //이메일 확인하라는 TextView Visible 설정
                                        pwdResetEmail.setVisibility(View.VISIBLE);
                                        Log.d(TAG, "Email sent.");
                                    }
                                    else {
                                        Toast.makeText(SearchPWDActivity.this, "인터넷 확인 혹은 이메일을 확인해주세요",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SearchPWDActivity.this, "이메일을 적어주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}