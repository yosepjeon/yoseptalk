package yoggaebi.yoseb.yoseptalk;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import yoggaebi.yoseb.yoseptalk.fragment.ChatFragment;
import yoggaebi.yoseb.yoseptalk.fragment.PeopleFragment;
import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class ChatRoomActivity extends AppCompatActivity{
    private FirebaseAuth auth;  //firebase의 로그인/회원가입을 위한 authentication(권한) 변수

    //
    private Spinner affiliated_company_spinner;
    private Spinner department_spinner;
    private ImageButton find_spinner_button;

    //
    private ImageButton viewPeopleListButton;
    private ImageButton viewChatRoomListButton;
    private ImageButton viewCommunityListButton;
    private ImageButton settingButton1;

    //
    private ImageButton createRoomButton;
    private ImageButton deleteRoomButton;
    private ImageButton enterRoomButton;
    private ImageButton settingButton2;

    //
    private ListView peopleList;
    private ListView chatRoomList;
    private ListView communityList;

    private LinearLayout linearLayout_basicList;
    private LinearLayout linearLayout_chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(ChatRoomActivity.this, LoginActivity.class));
            onPause();
        }



        linearLayout_basicList = (LinearLayout) findViewById(R.id.ChatRoomActivity_LinearLayout_BasicList);
        linearLayout_chatList = (LinearLayout) findViewById(R.id.ChatRoomActivity_LinearLayout_RoomList);

        affiliated_company_spinner = (Spinner) findViewById(R.id.ChatRoomActivity_Spinner_affiliated_company_spinner);
        department_spinner = (Spinner) findViewById(R.id.ChatRoomActivity_Spinner_department_spinner);
        find_spinner_button = (ImageButton) findViewById(R.id.ChatRoomActivity_Button_userButton);

        viewPeopleListButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_viewPeopleListButton);
        viewChatRoomListButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_viewChatRoomListButton);
        viewCommunityListButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_viewCommunityList);
        settingButton1 = (ImageButton) findViewById(R.id.ChatRoomActivity_Button_settingButton1);

        createRoomButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_createRoomButton);
        deleteRoomButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_deleteRoomButton);
        enterRoomButton = (ImageButton) findViewById(R.id.ChatRoomActivity_ImageButton_enterRoomButton);
        settingButton2 = (ImageButton) findViewById(R.id.ChatRoomActivity_Button_settingButton2);

//        peopleList = (ListView) findViewById(R.id.ChatRoomActivity_ListView_peopleList);
//        chatRoomList = (ListView) findViewById(R.id.ChatRoomActivity_ListView_chatRoomList);
//        communityList = (ListView) findViewById(R.id.ChatRoomActivity_ListView_communityList);


        //View 설정
        affiliated_company_spinner.setVisibility(View.VISIBLE);
        department_spinner.setVisibility(View.VISIBLE);
        find_spinner_button.setVisibility(View.VISIBLE);

//        viewPeopleListButton.setVisibility(View.VISIBLE);
//        viewChatRoomListButton.setVisibility(View.VISIBLE);
//        viewCommunityListButton.setVisibility(View.VISIBLE);
//        settingButton1.setVisibility(View.VISIBLE);
//
//        createRoomButton.setVisibility(View.VISIBLE);
//        deleteRoomButton.setVisibility(View.VISIBLE);
//        enterRoomButton.setVisibility(View.VISIBLE);
//        settingButton2.setVisibility(View.VISIBLE);
        linearLayout_basicList.setVisibility(View.VISIBLE);
        linearLayout_chatList.setVisibility(View.GONE);

        getFragmentManager().beginTransaction().replace(R.id.ChatRoomActivity_Layout_viewList1,new PeopleFragment()).commit();

        passPushTokenServer();
    }

    public void findUserList(View view) {

    }

    public void pressViewPeopleList(View view) {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);

        affiliated_company_spinner.setVisibility(View.VISIBLE);
        department_spinner.setVisibility(View.VISIBLE);
        find_spinner_button.setVisibility(View.VISIBLE);

        linearLayout_chatList.setVisibility(View.GONE);
        linearLayout_basicList.setVisibility(View.VISIBLE);
        linearLayout_basicList.setAnimation(animation);

        getFragmentManager().beginTransaction().replace(R.id.ChatRoomActivity_Layout_viewList1,new PeopleFragment()).commit();

    }

    public void pressViewChatRoomList(View view) {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);

        affiliated_company_spinner.setVisibility(View.INVISIBLE);
        department_spinner.setVisibility(View.INVISIBLE);
        find_spinner_button.setVisibility(View.INVISIBLE);

        linearLayout_chatList.setVisibility(View.GONE);
        linearLayout_basicList.setVisibility(View.VISIBLE);
        linearLayout_basicList.setAnimation(animation);

        getFragmentManager().beginTransaction().replace(R.id.ChatRoomActivity_Layout_viewList1,new ChatFragment()).commit();
    }

    public void pressViewCommunityList(View view) {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);
        affiliated_company_spinner.setVisibility(View.INVISIBLE);
        department_spinner.setVisibility(View.INVISIBLE);
        find_spinner_button.setVisibility(View.INVISIBLE);

        linearLayout_chatList.setVisibility(View.VISIBLE);
        linearLayout_chatList.setAnimation(animation);
        linearLayout_basicList.setVisibility(View.GONE);

    }

    public void pressBackButton(View view) {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);

        affiliated_company_spinner.setVisibility(View.VISIBLE);
        department_spinner.setVisibility(View.VISIBLE);
        find_spinner_button.setVisibility(View.VISIBLE);
        linearLayout_chatList.setVisibility(View.GONE);
        linearLayout_basicList.setVisibility(View.VISIBLE);
        linearLayout_basicList.setAnimation(animation);
    }

    public void pressSettingButton1(View view) {
        Intent intent = new Intent(this, SettingActivity1.class);

        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
        startActivity(intent,activityOptions.toBundle());
        onPause();
        //auth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(ChatRoomActivity.this, LoginActivity.class));
            onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            startActivity(new Intent(ChatRoomActivity.this, LoginActivity.class));
            onPause();
        }
        */
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void passPushTokenServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        //setValue는 기존의 데이터를 없애고 덮어쓰는 방식이므로 숙지
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }
}
