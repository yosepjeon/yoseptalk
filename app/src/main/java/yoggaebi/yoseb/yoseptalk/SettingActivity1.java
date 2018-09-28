package yoggaebi.yoseb.yoseptalk;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yoggaebi.yoseb.yoseptalk.chat.MessageActivity;

public class SettingActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting1);
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
