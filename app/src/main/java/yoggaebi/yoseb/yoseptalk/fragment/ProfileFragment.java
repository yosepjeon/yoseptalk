package yoggaebi.yoseb.yoseptalk.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yoggaebi.yoseb.yoseptalk.ProfileChangeActivity;
import yoggaebi.yoseb.yoseptalk.R;
import yoggaebi.yoseb.yoseptalk.SettingActivity1;
import yoggaebi.yoseb.yoseptalk.chat.MessageActivity;
import yoggaebi.yoseb.yoseptalk.model.UserModel;

public class ProfileFragment extends Fragment {
    private UserModel userModel;
    private int people_count = 0;

//    public interface SendToProfileData{
//        void onReceiveData(Object data);
//    }
//
//    private SendToProfileData sendToProfileData;
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        if(getActivity() != null && getActivity() instanceof SendToProfileData) {
//            sendToProfileData = (SendToProfileData) getActivity();
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.profilefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new ProfileFragmentRecyclerViewAdapter());

        return view;
    }


    class ProfileFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> userModels;
        public ProfileFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear(); // 누적(중복)되는 데이터를 없애주는 <공부>
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(userModel.getUid().equals(myUid)){
                            userModels.add(userModel);
                            break;
                        }
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_profile,viewGroup,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Glide.with
                    (viewHolder.itemView.getContext())
                    .load(userModels.get(i).getImageUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)viewHolder).imageView);
            ((CustomViewHolder)viewHolder).textView.setText(userModels.get(i).getUserName());

            //people 목록에서 해당 item layout을 클릭했을때 카톡처럼 해당 사람에대한 창이 뜨도록 하는 클릭 리스너
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), ProfileChangeActivity.class);
//                    intent.putExtra("destinationUid",userModels.get(i).getUid());
//                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
//                    startActivity(intent,activityOptions.toBundle());
                    showDialog(view.getContext());

                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }
    }

    void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment,null);
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.profileitem_imageview);
            textView = (TextView) view.findViewById(R.id.profileitem_textview);
        }
    }
}
