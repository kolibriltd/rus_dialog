package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.cuustomImage.HexagonButtom2ImageView;
import com.wearesputnik.istoria.cuustomImage.HexagonButtom4Img;
import com.wearesputnik.istoria.cuustomImage.HexagonImageView;
import com.wearesputnik.istoria.cuustomImage.HexagonTop2ImageView;
import com.wearesputnik.istoria.cuustomImage.HexagonTop4Img;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.helpers.MapProgressInfo;
import com.wearesputnik.istoria.helpers.ProgressInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 23.10.17.
 */

public class MapProgressAdapter extends ArrayAdapter<ProgressInfo> {
    List<ProgressInfo> progressInfoList;
    Context context;

    public MapProgressAdapter(Context context) {
        super(context, 0);
        progressInfoList = new ArrayList<>();
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ProgressInfo item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_map_progress, null);
            ViewHolder holder = new ViewHolder();
            holder.relLayoutOne = (RelativeLayout) view.findViewById(R.id.relLayoutOne);
            holder.ImageMap = (HexagonImageView) view.findViewById(R.id.ImageMap);
            holder.relLineOneImg1 = (RelativeLayout) view.findViewById(R.id.relLineOneImg1);
            holder.relLineOneImg2 = (RelativeLayout) view.findViewById(R.id.relLineOneImg2);

            holder.relLayoutTwo = (RelativeLayout) view.findViewById(R.id.relLayoutTwo);
            holder.imgMap2_1 = (HexagonTop2ImageView) view.findViewById(R.id.imgMap2_1);
            holder.imgMap2_2 = (HexagonButtom2ImageView) view.findViewById(R.id.imgMap2_2);
            holder.relLineTwoImg1 = (RelativeLayout) view.findViewById(R.id.relLineTwoImg1);
            holder.relLineTwoImg2 = (RelativeLayout) view.findViewById(R.id.relLineTwoImg2);
            holder.relLineBottom2_1 = (RelativeLayout) view.findViewById(R.id.relLineBottom2_1);
            holder.relLineBottom2_2 = (RelativeLayout) view.findViewById(R.id.relLineBottom2_2);
            holder.relLineBottom2_3 = (RelativeLayout) view.findViewById(R.id.relLineBottom2_3);
            holder.relLineBottom2_4 = (RelativeLayout) view.findViewById(R.id.relLineBottom2_4);

            holder.relLayoutThree = (RelativeLayout) view.findViewById(R.id.relLayoutThree);
            holder.hexagonButtom4Left = (HexagonButtom4Img) view.findViewById(R.id.hexagonButtom4Left);
            holder.hexagonTop4Left = (HexagonTop4Img) view.findViewById(R.id.hexagonTop4Left);
            holder.hexagonTop4Right = (HexagonTop2ImageView) view.findViewById(R.id.hexagonTop4Right);
            holder.hexagonButtom4Right = (HexagonButtom2ImageView) view.findViewById(R.id.hexagonButtom4Right);
            holder.relLineTop4_1 = (RelativeLayout) view.findViewById(R.id.relLineTop4_1);
            holder.relLineTop4_2 = (RelativeLayout) view.findViewById(R.id.relLineTop4_2);
            holder.relLineTop4_3 = (RelativeLayout) view.findViewById(R.id.relLineTop4_3);
            holder.relLineTop4_4 = (RelativeLayout) view.findViewById(R.id.relLineTop4_4);
            holder.relLineTwo4_1 = (RelativeLayout) view.findViewById(R.id.relLineTwo4_1);
            holder.relLineTwo4_2 = (RelativeLayout) view.findViewById(R.id.relLineTwo4_2);

            holder.lineBottomTwo = (RelativeLayout) view.findViewById(R.id.lineBottomTwo);
            holder.relLineTwo = (RelativeLayout) view.findViewById(R.id.relLineTwo);

            holder.relLine2_1 = (RelativeLayout) view.findViewById(R.id.relLine2_1);
            holder.relLine2_1_left = (RelativeLayout) view.findViewById(R.id.relLine2_1_left);
            holder.relLine2_2 = (RelativeLayout) view.findViewById(R.id.relLine2_2);
            holder.relLine2_2_right = (RelativeLayout) view.findViewById(R.id.relLine2_2_right);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.relLayoutOne.setVisibility(View.GONE);
        holder.relLayoutTwo.setVisibility(View.GONE);
        holder.relLayoutThree.setVisibility(View.GONE);

        if (item.progressInfoList.size() == 1) {///One Image View
            holder.relLayoutOne.setVisibility(View.VISIBLE);
            String url_img = HttpConnectClass.URL_IMAGE + item.progressInfoList.get(0).ImageMap;
            if (!item.progressInfoList.get(0).isActiveImage) {
                holder.ImageMap.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(0).ImageMap != null && !item.progressInfoList.get(0).ImageMap.trim().equals("")) {
                Glide.with(context)
                    .load(url_img)
                    .into(holder.ImageMap);
            }
            if (item.isActiveLineOne) {
                holder.relLineOneImg1.setBackgroundResource(R.drawable.bg_share_line);
                holder.relLine2_1.setBackgroundResource(R.drawable.bg_share_line);
            }
            if (item.isActiveLineTwo) {
                holder.relLineOneImg2.setBackgroundResource(R.drawable.bg_share_line);
                holder.relLine2_2.setBackgroundResource(R.drawable.bg_share_line);
            }
            holder.relLine2_1_left.setVisibility(View.GONE);
            holder.relLine2_2_right.setVisibility(View.GONE);
        }
        if (item.progressInfoList.size() == 2) {///Two Image View
            holder.relLayoutTwo.setVisibility(View.VISIBLE);

            if (item.isActiveLineOne) {
                holder.relLineTwoImg1.setBackgroundResource(R.drawable.bg_share_line);
            }
            if (item.isActiveLineTwo) {
                holder.relLineTwoImg2.setBackgroundResource(R.drawable.bg_share_line);
            }

            String url_img = HttpConnectClass.URL_IMAGE + item.progressInfoList.get(0).ImageMap;
            if (!item.progressInfoList.get(0).isActiveImage) {
                holder.imgMap2_1.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(0).ImageMap != null && !item.progressInfoList.get(0).ImageMap.trim().equals("")) {
                Glide.with(context)
                    .load(url_img)
                    .into(holder.imgMap2_1);
            }
            if (!item.progressInfoList.get(1).isActiveImage) {
                holder.imgMap2_2.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(1).ImageMap != null && !item.progressInfoList.get(1).ImageMap.trim().equals("")) {
                Glide.with(context)
                    .load(HttpConnectClass.URL_IMAGE + item.progressInfoList.get(1).ImageMap)
                    .into(holder.imgMap2_2);
            }
            if (item.metkaEnd) {
                holder.relLineTwo.setVisibility(View.GONE);
                holder.lineBottomTwo.setVisibility(View.GONE);
            }
            else {
                if (item.isButtomActiveLineOne) {
                    holder.relLineBottom2_1.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLine2_1_left.setBackgroundResource(R.drawable.bg_share_line);
                }
                if (item.isButtomActiveLineTwo) {
                    holder.relLineBottom2_2.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLine2_1.setBackgroundResource(R.drawable.bg_share_line);
                }
                if (item.isButtomActiveLineThree) {
                    holder.relLineBottom2_3.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLine2_2.setBackgroundResource(R.drawable.bg_share_line);
                }
                if (item.isButtomActiveLineFour) {
                    holder.relLineBottom2_4.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLine2_2_right.setBackgroundResource(R.drawable.bg_share_line);
                }
            }
        }
        if (item.progressInfoList.size() == 4) {///Four Image View
            holder.relLayoutThree.setVisibility(View.VISIBLE);

            if (item.isActiveLineOne) {
                holder.relLineTop4_1.setBackgroundResource(R.drawable.bg_share_line);
            }
            if (item.isActiveLineTwo) {
                holder.relLineTop4_2.setBackgroundResource(R.drawable.bg_share_line);
            }
            if (item.isActiveLineThree) {
                holder.relLineTop4_3.setBackgroundResource(R.drawable.bg_share_line);
            }
            if (item.isActiveLineFour) {
                holder.relLineTop4_4.setBackgroundResource(R.drawable.bg_share_line);
            }

            if (!item.progressInfoList.get(0).isActiveImage) {
                holder.hexagonTop4Left.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(0).ImageMap != null && !item.progressInfoList.get(0).ImageMap.trim().equals("")) {
                Glide.with(context)
                        .load(HttpConnectClass.URL_IMAGE + item.progressInfoList.get(0).ImageMap)
                        .into(holder.hexagonTop4Left);
            }
            if (!item.progressInfoList.get(1).isActiveImage) {
                holder.hexagonButtom4Left.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(1).ImageMap != null && !item.progressInfoList.get(1).ImageMap.trim().equals("")) {
                Glide.with(context)
                        .load(HttpConnectClass.URL_IMAGE + item.progressInfoList.get(1).ImageMap)
                        .into(holder.hexagonButtom4Left);
            }
            if (!item.progressInfoList.get(2).isActiveImage) {
                holder.hexagonTop4Right.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(2).ImageMap != null && !item.progressInfoList.get(2).ImageMap.trim().equals("")) {
                Glide.with(context)
                        .load(HttpConnectClass.URL_IMAGE + item.progressInfoList.get(2).ImageMap)
                        .into(holder.hexagonTop4Right);
            }
            if (!item.progressInfoList.get(3).isActiveImage) {
                holder.hexagonButtom4Right.setBorderColor(Color.GRAY);
            }
            if (item.progressInfoList.get(3).ImageMap != null && !item.progressInfoList.get(3).ImageMap.trim().equals("")) {
                Glide.with(context)
                        .load(HttpConnectClass.URL_IMAGE + item.progressInfoList.get(3).ImageMap)
                        .into(holder.hexagonButtom4Right);
            }

            if (item.metkaEnd) {
                holder.relLineTwo.setVisibility(View.GONE);
                holder.relLineTwo4_1.setVisibility(View.GONE);
                holder.relLineTwo4_2.setVisibility(View.GONE);
            }
            else {
                if (item.isButtomActiveLineOne) {
                    holder.relLine2_1.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLineTwo4_1.setBackgroundResource(R.drawable.bg_share_line);
                }
                if (item.isButtomActiveLineTwo) {
                    holder.relLine2_2.setBackgroundResource(R.drawable.bg_share_line);
                    holder.relLineTwo4_2.setBackgroundResource(R.drawable.bg_share_line);
                }
                holder.relLine2_1_left.setVisibility(View.GONE);
                holder.relLine2_2_right.setVisibility(View.GONE);
            }
        }
        return view;
    }

    class ViewHolder {
        RelativeLayout relLayoutOne, relLayoutTwo, relLayoutThree;
        RelativeLayout lineBottomTwo, relLineTwo;
        HexagonImageView ImageMap;
        HexagonTop2ImageView imgMap2_1, hexagonTop4Right;
        HexagonButtom2ImageView imgMap2_2, hexagonButtom4Right;
        HexagonButtom4Img hexagonButtom4Left;
        HexagonTop4Img hexagonTop4Left;
        RelativeLayout relLineTop4_1, relLineTop4_2, relLineTop4_3, relLineTop4_4, relLineTwo4_1, relLineTwo4_2;
        RelativeLayout relLineBottom2_1, relLineBottom2_2, relLineBottom2_3, relLineBottom2_4;
        RelativeLayout relLine2_1, relLine2_1_left, relLine2_2, relLine2_2_right, relLineOneImg1, relLineOneImg2, relLineTwoImg1, relLineTwoImg2;
    }
}
