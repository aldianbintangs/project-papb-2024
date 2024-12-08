package ap.mobile.notedifywithfirebase.shared;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import ap.mobile.notedifywithfirebase.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> userList;
    private final int[] colors = {
            R.color.light_yellow,
            R.color.light_pink,
            R.color.light_green,
            R.color.light_blue,
            R.color.light_orange,
            R.color.light_red,
            R.color.light_gray,
            R.color.light_purple,
            R.color.light_teal,
            R.color.light_brown
    };

    private final OnItemClickListener onItemClickListener;

    public UserAdapter(List<User> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeUser(String userName) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getName().equals(userName)) {
                userList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.ivUserPhoto.setImageResource(user.getProfilePhotoResId());

        int randomColor = colors[new Random().nextInt(colors.length)];
        holder.constraintLayout.setBackgroundResource(randomColor);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(user.getName());
            }
        });
    }

    public void clearUser(String userName) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getName().equals(userName)) {
                userList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto;
        TextView tvName;
        ConstraintLayout constraintLayout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            constraintLayout = itemView.findViewById(R.id.clUserContainer);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String userName);
    }
}
