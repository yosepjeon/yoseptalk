package yoggaebi.yoseb.yoseptalk;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 9001;
    private String email = "";
    private String password = "";

    private EditText inputEmail, inputPassword, inputID;
    private FirebaseAuth auth;  //firebase의 로그인/회원가입을 위한 authentication(권한) 변수
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset, btnGoogleLogin;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ChatRoomActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnGoogleLogin = (Button) findViewById(R.id.btn_google_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //auth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_google_login).setOnClickListener(this);

        btnGoogleLogin.setVisibility(View.VISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password) //입력한 email과 password를 firebaseAuth를 이용하여 Goole유저인지 권한을 체크하여 로그인한다.
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(LoginActivity.this, "로그인 실패. 아이디 및 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();

                            /*
                            if (password.length() < 6) {    //password는 6자리 이상으로 해야 한다.
                                inputPassword.setError("비밀번호는 6자리 이상으로 입력하세요.");
                            } else {
                                Toast.makeText(LoginActivity.this, "로그인 실패!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            */
                        } else {
                            Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

//        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, GoogleLoginActivity.class));
//            }
//        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }
//
//    .setAvailableProviders(Collections.singletonList(
//            new AuthUI.IdpConfig.EmailBuilder().build()))
    private void startSignIn() {
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setLogo(R.mipmap.ic_launcher)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_google_login:
                System.out.println("startSignIn");
                startSignIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign in succeeded
                Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                //updateUI(null);
            }
        }
    }
}
