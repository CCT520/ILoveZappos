package com.example.myapplication;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.example.myapplication.databinding.ListItemBinding;
import java.util.*;
import java.util.List;

/**
 * Created by chenchutian on 2017/1/29.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> list;
    private ListItemBinding binding;
    private int layoutId;
    private int variableId;

    public ListAdapter(Context context, ArrayList list, int layoutId, int variableId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        binding = null;
        if (convertView == null){
            binding = DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,parent,false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(variableId,list.get(position));
        return binding.getRoot();
    }

}
