package yoggaebi.yoseb.yoseptalk.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import yoggaebi.yoseb.yoseptalk.R;
import yoggaebi.yoseb.yoseptalk.chat.GroupMessageActivity;
import yoggaebi.yoseb.yoseptalk.chat.MessageActivity;
import yoggaebi.yoseb.yoseptalk.model.ChatModel;
import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class ChatFragment extends Fragment {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatFragmentRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;
    }


    class ChatFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private List<String> keys = new ArrayList<>();
        private List<String> destinationUsers = new ArrayList<>();

        public ChatFragmentRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        chatModels.add(item.getValue(ChatModel.class));
                        keys.add(item.getKey());
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chatroom,viewGroup,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

            final CustomViewHolder customViewHolder = (CustomViewHolder)viewHolder;
            String destinationUid = null;

            //채팅방에 있는 유저를 체크
            for(String user : chatModels.get(i).users.keySet()) {
                if(!user.equals(uid)) {
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.getImageUrl())
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);

                    customViewHolder.textView_title.setText(userModel.getUserName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //메세지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(i).comments);
            if(commentMap.keySet().toArray().length > 0) {
                String lastMessageKey = (String) commentMap.keySet().toArray()[0]; //단체 채팅방에서 해당 코드에 대해서 에러가 발생하는데 이유는 단체채팅방에 메시지를 처음 만들면 메시지가 없는데 null값을 읽어들이려 하니까 에러 발생
                customViewHolder.textView_last_message.setText(chatModels.get(i).comments.get(lastMessageKey).message);

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                long unixTime = (long) chatModels.get(i).comments.get(lastMessageKey).timestamp;

                Date date = new Date(unixTime);
                customViewHolder.textView_last_time.setText(simpleDateFormat.format(date));
            }
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                Intent intent = null;
                @Override
                public void onClick(View view) {
                    if(chatModels.get(i).users.size() > 2) {
                        intent = new Intent(view.getContext(), GroupMessageActivity.class);
                        intent.putExtra("destinationRoom",keys.get(i));
                    }else {
                        intent = new Intent(view.getContext(), MessageActivity.class);
                        intent.putExtra("destinationUid", destinationUsers.get(i));
                    }

                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);

                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_message;
            public TextView textView_last_message;
            public TextView textView_last_time;

            public CustomViewHolder(View view) {
                super(view);

                imageView = (ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title = (TextView) view.findViewById(R.id.chatitem_textview_title);
                textView_message = (TextView) view.findViewById(R.id.chatitem_textview_lastmessage);
                textView_last_message = (TextView) view.findViewById(R.id.chatitem_textview_lastmessage);
                textView_last_time = (TextView) view.findViewById(R.id.chatitem_textview_lasttime);
            }
        }
    }
}
