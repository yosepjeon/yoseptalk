package yoggaebi.yoseb.yoseptalk;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private ImageView profile;
    private Uri imageUri = null;

    private FirebaseAuth auth;
    private String text="";
    public static int key;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static String email;
    private static String password;
    private static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot();

        auth = FirebaseAuth.getInstance();

        profile = (ImageView) findViewById(R.id.SignupActivity_ImageView_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);

            }
        });

        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        inputEmail = (EditText) findViewById(R.id.SignupActivity_EditText_email);
        inputPassword = (EditText) findViewById(R.id.SignupActivity_EditText_password);
        inputName = (EditText) findViewById(R.id.SignupActivity_EditText_name);
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
                password = inputPassword.getText().toString().trim();
                name = inputName.getText().toString().trim();

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
                            final String uid = task.getResult().getUser().getUid();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(inputName.getText().toString()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);//회원가입할때 프로필을 담아줌 push사용 용도

                            if(imageUri == null) {
                                imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                        "://" + getResources().getResourcePackageName(R.drawable.ic_perm_identity_black_36dp)
                                        + '/' + getResources().getResourceTypeName(R.drawable.ic_perm_identity_black_36dp)
                                        + '/' + getResources().getResourceEntryName(R.drawable.ic_perm_identity_black_36dp));
                            }

                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    String imageUrl = task.getResult().getDownloadUrl().toString();

                                    UserModel userModel = new UserModel();
                                    userModel.setUserName(name);
                                    userModel.setImageUrl(imageUrl);
                                    userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                            Toast.makeText(SignupActivity.this, name + "님" + " 환영합니다!",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, ChatRoomActivity.class);
                                            startActivity(intent);

                                            SignupActivity.this.finish();
                                        }
                                    });
                                }
                            });
//                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                    String imageUrl = task.getResult().getDownloadUrl().toString();
//
//                                    UserModel userModel = new UserModel();
//                                    userModel.setUserName(name);
//                                    userModel.setImageUrl(imageUrl);
//                                    userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
//                                }
//                            });
//                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri);
//                            String imageUrl = FirebaseStorage.getInstance().getReference().child(uid).getDownloadUrl().toString();
//                            UserModel userModel = new UserModel();
//                            userModel.setUserName(name);
//                            userModel.setImageUrl(imageUrl);
//                            userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData()); // 가운데 뷰를 바꿈
            imageUri = data.getData(); // 이미지 경로 원본
        }
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
