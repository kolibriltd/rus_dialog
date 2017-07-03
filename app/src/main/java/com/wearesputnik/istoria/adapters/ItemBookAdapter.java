package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.helpers.TextInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 05.06.17.
 */
public class ItemBookAdapter extends ArrayAdapter<TextInfo> {
    List<TextInfo> textInfoList;
    Context context;
    String nameA, nameB;

    public ItemBookAdapter (Context context, String nameA, String nameB) {
        super(context, 0);
        textInfoList = new ArrayList<>();
        this.context = context;
        this.nameA = nameA;
        this.nameB = nameB;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final TextInfo item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_text_book, null);
            ViewHolder holder = new ViewHolder();
            holder.txtPeopleA = (TextView) view.findViewById(R.id.txtPeopleA);
            holder.txtNamePeopleA = (TextView) view.findViewById(R.id.txtNamePeopleA);
            holder.txtPeopleB = (TextView) view.findViewById(R.id.txtPeopleB);
            holder.txtNamePeopleB = (TextView) view.findViewById(R.id.txtNamePeopleB);
            holder.txtDescAB = (TextView) view.findViewById(R.id.txtDescAB);
            holder.relPeopleA = (RelativeLayout) view.findViewById(R.id.relPeopleA);
            holder.relPeopleB = (RelativeLayout) view.findViewById(R.id.relPeopleB);
            holder.relDescAB = (RelativeLayout) view.findViewById(R.id.relDescAB);
            holder.relEnd = (RelativeLayout) view.findViewById(R.id.relEnd);
            holder.relEmty = (RelativeLayout) view.findViewById(R.id.relEmty);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (item.peopleA != null) {
            holder.relPeopleA.setVisibility(View.VISIBLE);
            holder.relPeopleB.setVisibility(View.GONE);
            holder.relDescAB.setVisibility(View.GONE);
            holder.relEmty.setVisibility(View.GONE);
            holder.relEnd.setVisibility(View.GONE);
            holder.txtPeopleA.setText(item.peopleA);
            holder.txtNamePeopleA.setText(this.nameA);
            if (!item.flags) {
                StartAnimation(holder.relPeopleA);
            }
        }
        if (item.peopleB != null) {
            holder.relPeopleA.setVisibility(View.GONE);
            holder.relPeopleB.setVisibility(View.VISIBLE);
            holder.relEmty.setVisibility(View.GONE);
            holder.relDescAB.setVisibility(View.GONE);
            holder.relEnd.setVisibility(View.GONE);
            holder.txtPeopleB.setText(item.peopleB);
            holder.txtNamePeopleB.setText(this.nameB);
            if (!item.flags) {
                StartAnimation(holder.relPeopleB);
            }
        }
        if (item.context != null) {
            holder.relPeopleA.setVisibility(View.GONE);
            holder.relPeopleB.setVisibility(View.GONE);
            holder.relEmty.setVisibility(View.GONE);
            holder.relDescAB.setVisibility(View.VISIBLE);
            holder.relEnd.setVisibility(View.GONE);
            holder.txtDescAB.setText(item.context);
            if (!item.flags) {
                StartAnimation(holder.relDescAB);
            }
        }
        if (item.metka != null) {
            holder.relPeopleA.setVisibility(View.GONE);
            holder.relPeopleB.setVisibility(View.GONE);
            holder.relDescAB.setVisibility(View.GONE);
            holder.relEmty.setVisibility(View.GONE);
            holder.relEnd.setVisibility(View.VISIBLE);
            if (!item.flags) {
                StartAnimation(holder.relEnd);
            }
        }
        if (item.emptyFlag) {
            holder.relPeopleA.setVisibility(View.GONE);
            holder.relPeopleB.setVisibility(View.GONE);
            holder.relDescAB.setVisibility(View.GONE);
            holder.relEnd.setVisibility(View.GONE);
            holder.relEmty.setVisibility(View.VISIBLE);
            if (!item.flags) {
                StartAnimation(holder.relEmty);
            }
        }

        item.flags = true;

        return view;
    }

    private void StartAnimation(RelativeLayout layout) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.transition);
        anim.reset();
        layout.clearAnimation();
        layout.startAnimation(anim);

    }

    class ViewHolder {
        TextView txtPeopleA, txtNamePeopleA;
        TextView txtPeopleB, txtNamePeopleB;
        TextView txtDescAB;
        RelativeLayout relPeopleA,relPeopleB, relDescAB, relEnd,relEmty;
    }
}
