package yoggaebi.yoseb.yoseptalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class  LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword,inputID;
    private FirebaseAuth auth;  //firebase의 로그인/회원가입을 위한 authentication(권한) 변수
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
