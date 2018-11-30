package yoggaebi.yoseb.yoseptalk;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import yoggaebi.yoseb.yoseptalk.fragment.ProfileFragment;

public class SettingActivity1 extends AppCompatActivity {
    private TextView stateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting1);

        stateMessage = (TextView)findViewById(R.id.SettingActivity1_TextView_stateMessage);

        getFragmentManager().beginTransaction().replace(R.id.SettingActivity1_linearlayout_profileview,new ProfileFragment()).commit();

    }

    public void signout(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    public void clickStateMessage(View view) {
        showDialog(view.getContext());
    }

    void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment,null);
        final EditText editText = (EditText)view.findViewById(R.id.commentDialog_edittext);
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getUid();

                stringObjectMap.put("comment",editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity1.this, ChatRoomActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(),R.anim.fromleft,R.anim.toright);
        startActivity(intent,activityOptions.toBundle());
        super.onBackPressed();
        this.finish();
//        overridePendingTransition(R.anim.fromleft,R.anim.toright);
    }
}
