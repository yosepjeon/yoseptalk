package yoggaebi.yoseb.yoseptalk.chat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yoggaebi.yoseb.yoseptalk.ChatRoomActivity;
import yoggaebi.yoseb.yoseptalk.R;
import yoggaebi.yoseb.yoseptalk.model.ChatModel;
import yoggaebi.yoseb.yoseptalk.model.NotificationModel;
import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class MessageActivity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private int scrollState=0;

    private UserModel userModel;
    int recyclerViewListSize = 0;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int people_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 uid 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 uid
        editText = (EditText) findViewById(R.id.MessageActivity_EditText_editText);
        button = (Button) findViewById(R.id.MessageActivity_EditText_button);

        recyclerView = (RecyclerView) findViewById(R.id.MessageActivity_RecyclerView_chatContents);

        recyclerView.onScrollStateChanged(scrollState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid,true);
                chatModel.users.put(destinationUid,true);

                if(chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkDupChatRoom();
                        }
                    }); //push를 넣어줘야 이름을 임의로 생성하여 채팅방을 생성
                    //checkDupChatRoom() 밑에 이 메서드 하나 추가하면 되긴하지만 onSuccess를 사용해야 더 안전
                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendGCM();
                            editText.setText("");
                        }
                    });
                }
                recyclerView.scrollToPosition(recyclerViewListSize);
            }
        });
        checkDupChatRoom();
    }

    void sendGCM() {
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = userModel.getPushToken();
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyD4iO6smMoGh2GUuYDA_bfOx6XJ5rBLP4Q")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    void checkDupChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2){
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerViewAdapter = new RecyclerViewAdapter();
                        recyclerView.setAdapter(recyclerViewAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setRecyclerViewListSize(){
            recyclerViewListSize = comments.size()-1;
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() { //valueEventListener에 특정 이벤트를 담는다 그다음 backButton으로 가보면
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,Object> readUserMap = new HashMap<>();
                    comments.clear(); //clear가 없으면 데이터가 추가될때마다 서버에서 모든 내용을 다 보내주게 된다. 클리어를 하지 않으면 데이터가 계속 쌓이게 된다.

                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);
                        comment_modify.readUsers.put(uid,true); // 이것을 서버에 알림

                        readUserMap.put(key,comment_modify); //읽은 내용을 가지고 있고

                        //comments.add(item.getValue(ChatModel.Comment.class));

                        comments.add(comment_origin);
                    }

                    if(comments.size() == 0) {
                        //메세지를 갱신시켜주는
                        notifyDataSetChanged();
                        setRecyclerViewListSize();

                        if (!recyclerView.canScrollVertically(1)) {
                            recyclerView.scrollToPosition(comments.size() - 1);
                        }
                    }else {

                        if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                            //메세지를 갱신시켜주는
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                    .updateChildren(readUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyDataSetChanged();
                                    setRecyclerViewListSize();

                                    if (!recyclerView.canScrollVertically(1)) {
                                        recyclerView.scrollToPosition(comments.size() - 1);
                                    }
                                }
                            });
                        } else {
                            notifyDataSetChanged();
                            setRecyclerViewListSize();

                            if (!recyclerView.canScrollVertically(1)) {
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message,viewGroup,false);
            return new MessageViewHoler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            MessageViewHoler messageViewHoler = ((MessageViewHoler) viewHolder);

            if(comments.get(i).uid.equals(uid)) { //메시지가 내가 보낸경우
                messageViewHoler.textView_message.setText(comments.get(i).message);
                messageViewHoler.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHoler.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHoler.textView_message.setTextSize(25);
                messageViewHoler.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(i,messageViewHoler.textView_readCounter_left);
            }else{//상대방이 메시지를 보낸경우
                Glide.with(viewHolder.itemView.getContext()).load(userModel.getImageUrl()).apply(new RequestOptions().circleCrop()).into(messageViewHoler.imageView_profile);
                messageViewHoler.textView_name.setText(userModel.getUserName());
                messageViewHoler.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHoler.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHoler.textView_message.setText(comments.get(i).message);
                messageViewHoler.textView_message.setTextSize(25);
                messageViewHoler.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(i,messageViewHoler.textView_readCounter_right);
            }
            long unixTime = (long) comments.get(i).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHoler.textView_timestamp.setText(time);

            messageViewHoler.textView_message.setText(comments.get(i).message);
        }

        void setReadCounter(final int position, final TextView textView) {
            if(people_count == 0 ) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        people_count = users.size();

                        int count = people_count - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else {
                int count = people_count - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHoler extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public TextView textView_timestamp;

            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;

            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public MessageViewHoler(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView) view.findViewById(R.id.messageItem_textView_name);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textView_timestamp);
                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_readCounter_left = (TextView)view.findViewById(R.id.messageItem_textView_readCounter_left);
                textView_readCounter_right = (TextView)view.findViewById(R.id.messageItem_textView_readCounter_right);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(valueEventListener != null)
            databaseReference.removeEventListener(valueEventListener);//등록했던 이벤트 리스너를 제거함으로써 계속해서 메시지를 읽지 않도록 한다.
        Intent intent = new Intent(MessageActivity.this, ChatRoomActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(),R.anim.fromleft,R.anim.toright);
        startActivity(intent,activityOptions.toBundle());
        super.onBackPressed();
        this.finish();
//        overridePendingTransition(R.anim.fromleft,R.anim.toright);
    }
}
