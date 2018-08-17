package yoggaebi.yoseb.yoseptalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleLoginActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private String email = "";
    private String password = "";

    private FirebaseAuth auth;//firebase의 로그인/회원가입을 위한 authentication(권한) 변수
    private EditText inputEmail, inputPassword, inputID;
    private ProgressBar progressBar;
    private Button btnLogin, btnBack, btnReset;
    private Intent intent;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(GoogleLoginActivity.this, ChatRoomActivity.class));
            finish();
        }

        setContentView(R.layout.activity_google_login);

        inputEmail = (EditText) findViewById(R.id.google_email);
        inputPassword = (EditText) findViewById(R.id.google_password);
        progressBar = (ProgressBar) findViewById(R.id.google_login_progressBar);
        btnLogin = (Button) findViewById(R.id.btn_google_login);
        btnBack = (Button) findViewById(R.id.btn_back_to_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GoogleLoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoogleLoginActivity.this, LoginActivity.class));
            }
        });
    }
}
