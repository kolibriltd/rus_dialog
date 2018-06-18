package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.jsonHelper.ContentBook;

import java.util.ArrayList;
import java.util.List;

public class OtvetAdapter extends ArrayAdapter<ContentBook> {
    List<ContentBook> contentBookList;
    Context context;

    public OtvetAdapter (Context context) {
        super(context, 0);
        contentBookList = new ArrayList<>();
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ContentBook item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_otvet_list, null);
            ViewHolder holder = new ViewHolder();
            holder.txtOtvet = (TextView) view.findViewById(R.id.txtOtvet);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.txtOtvet.setText(item.msg);

        return view;
    }

    class ViewHolder {
        TextView txtOtvet;
    }
}
