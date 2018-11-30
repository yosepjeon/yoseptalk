package yoggaebi.yoseb.yoseptalk.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yoggaebi.yoseb.yoseptalk.R;
import yoggaebi.yoseb.yoseptalk.chat.MessageActivity;
import yoggaebi.yoseb.yoseptalk.model.ChatModel;
import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class SelectPeopleActivity extends AppCompatActivity {
    ChatModel chatModel = new ChatModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_people);

        RecyclerView recyclerView = findViewById(R.id.selectPeopleActivity_recyclerview);
        recyclerView.setAdapter(new SelectPeopleFragmentRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button button = findViewById(R.id.selectPeopleActivity_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chatModel.users.put(myUid,true);

                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
            }
        });
    }

    class SelectPeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> userModels;

        public SelectPeopleFragmentRecyclerViewAdapter(){
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear(); // 누적(중복)되는 데이터를 없애주는 <공부>
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(userModel.getUid().equals(myUid)){
                            continue;
                        }

                        userModels.add(userModel);
                    }

                    notifyDataSetChanged(); // 새로고침 반드시 필요!!<공부>
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_people_list_select,viewGroup,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Glide.with
                    (viewHolder.itemView.getContext())
                    .load(userModels.get(i).getImageUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) viewHolder).imageView);
            ((CustomViewHolder) viewHolder).textView.setText(userModels.get(i).getUserName());

            //people 목록에서 해당 item layout을 클릭했을때 카톡처럼 해당 사람에대한 창이 뜨도록 하는 클릭 리스너
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", userModels.get(i).getUid());
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            if (userModels.get(i).getComment() != null) {
                ((CustomViewHolder) viewHolder).textView_comment.setBackgroundResource(R.drawable.leftbubble);
                ((CustomViewHolder) viewHolder).textView_comment.setText(userModels.get(i).getComment());
            }

            ((CustomViewHolder) viewHolder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(b) { // check된 상태
                        chatModel.users.put(userModels.get(i).getUid(),true);
                    }else { // check안된 상태
                        chatModel.users.remove(userModels.get(i));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CheckBox checkBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.peopleitem_imageview);
                textView = (TextView) view.findViewById(R.id.peopleitem_textview);
                textView_comment = (TextView) view.findViewById(R.id.peopleitem_comment);
                checkBox = (CheckBox) view.findViewById(R.id.peopleitem_checkbox);
            }
        }
    }
}
