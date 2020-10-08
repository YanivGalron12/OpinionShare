package com.example.opinionshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<UserListRow> originalData,tempData;
    CustomFilter cs;

    public UsersListAdapter(Context c, ArrayList<UserListRow> originalData) {
        this.context = c;
        this.originalData = originalData;
        ArrayList<UserListRow> Data = new ArrayList<>();
        Data.addAll(originalData);
        this.tempData = Data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View user_list_display = layoutInflater.inflate(R.layout.user_list_display, null);
        CircleImageView userProfileImageView = user_list_display.findViewById(R.id.UserProfileImageView1);
        TextView userNameTextView = user_list_display.findViewById(R.id.UserNameTextView1);

        String rName = tempData.get(position).getUserName();
        userNameTextView.setText(rName);
        Picasso.get().load(tempData.get(position).getUserProfilePhoto()).into(userProfileImageView);
        return user_list_display;
    }
    @Override
    public Filter getFilter() {
        if (cs == null) {
            cs = new CustomFilter();
        }
        return cs;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return tempData.size();
    }

    @Override
    public Object getItem(int i) {
        return tempData.get(i);
    }

    class CustomFilter extends Filter {
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tempData.clear();
            tempData.addAll((ArrayList<UserListRow>) filterResults.values);
            notifyDataSetChanged();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constrains) {
            FilterResults results = new FilterResults();
            if (constrains != null && constrains.length() > 0) {
                constrains = constrains.toString().toUpperCase();
                ArrayList<UserListRow> filters = new ArrayList<>();

                for (int i = 0; i < originalData.size(); i++) {
                    if (originalData.get(i).getUserName().toUpperCase().contains(constrains)) {
                        UserListRow userListRow = new UserListRow(originalData.get(i).getUserName(),originalData.get(i).getUserID(),originalData.get(i).getUserProfilePhoto());
                        filters.add(userListRow);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = originalData.size();
                results.values = originalData;
            }
            return results;
        }
    }
}
