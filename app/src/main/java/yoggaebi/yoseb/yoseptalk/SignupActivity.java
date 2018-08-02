package yoggaebi.yoseb.yoseptalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String text="";
    public static int key;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static String email;
    private static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot();

        auth = FirebaseAuth.getInstance();

        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    비밀번호 특수문자 대문자 1개씩 반드시 포함할수 있도록 추가할것 ( 아직 미구현)
                 */
                email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),"이메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 10 && checkSpecialCharacter(password)) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 최소 10자리 이상 입력하십시오.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!checkSpecialCharacter(password)){
                    Toast.makeText(getApplicationContext(),"특수문자가 1개 이상 반드시 포함되어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword( email,password) //이메일과 비밀번호를 받아 회원가입 실행
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful()){ //실패할경우 처리
                            Toast.makeText(SignupActivity.this,"회원가입에 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }else{ //성공했을 경우 처리
                            String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Toast.makeText(SignupActivity.this, name + "님" + " 환영합니다!",Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(SignupActivity.this, ChatRoomActivity.class);
                            //startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

    }

    public boolean checkSpecialCharacter(String password) {
        Pattern p =Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])");
        Matcher m = p.matcher(password);
        if(m.find()){
            return true;
        }else{
            return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
