package com.example.opinionshare;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends ArrayAdapter {

    Context context;
    int resource;

    public ItemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> items) {
        super(context, -1, items);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.items_for_sale_listview,null,true);
        Item item= (Item) getItem(position);
        ImageView imageView=(ImageView)convertView.findViewById(R.id.imageViewItem);
        Picasso.get().load(item.getImage()).into(imageView);
        TextView txtType=(TextView)convertView.findViewById(R.id.txtType);
        txtType.setText(item.getType());
        TextView txtCompany=(TextView)convertView.findViewById(R.id.txtCompany);
        txtCompany.setText(item.getCompany());



        return convertView; 
    }
}
