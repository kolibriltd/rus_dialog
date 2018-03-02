package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.activity.InfoBookActivity;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.HttpConnectClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 02.06.17.
 */
public class BooksAdapter extends ArrayAdapter<Books> {
    List<Books> booksList;
    Context context;
    boolean guestFlag = false;


    public BooksAdapter (Context context) {
        super(context, 0);
        booksList = new ArrayList<>();
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Books item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_list_book, null);
            ViewHolder holder = new ViewHolder();
            holder.txtName = (TextView) view.findViewById(R.id.txtName);
            holder.txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
            holder.imageViewCover = (ImageView) view.findViewById(R.id.imageViewCover);
            holder.imgStic = (ImageView) view.findViewById(R.id.imgStic);
            holder.imgMoneyIc = (ImageView) view.findViewById(R.id.imgMoneyIc);
            holder.relItemListBook = (RelativeLayout) view.findViewById(R.id.relItemListBook);
            holder.txtEye = (TextView) view.findViewById(R.id.txtEye);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.relItemListBook.setVisibility(View.VISIBLE);
        holder.imgMoneyIc.setVisibility(View.GONE);
        holder.txtName.setText(item.name);
        holder.txtAuthor.setText(item.author);
        holder.txtEye.setText(item.isViewCount + "");

        if (item.type_id == 3) {
            holder.imgMoneyIc.setVisibility(View.VISIBLE);
        }

        if (item.pathCoverFileStorage != null) {
            holder.imageViewCover.setImageURI(Uri.parse(item.pathCoverFileStorage));
        } else if (item.pathCoverFile != null) {
            String url_img = HttpConnectClass.URL_IMAGE + item.pathCoverFile;

            Glide.with(context)
                    .load(url_img)
                    .into(holder.imageViewCover);
        }
        if (item.new_istori_int == null) {
            holder.imgStic.setVisibility(View.GONE);
        }
        else {
            if (item.new_istori_int == 1) {
                holder.imgStic.setVisibility(View.VISIBLE);
            } else {
                holder.imgStic.setVisibility(View.GONE);
            }
        }


//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (guestFlag) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Ошибка")
//                            .setMessage("Вы не залогинелись")
//                            .setCancelable(false)
//                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.cancel();
//                                }
//                            });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//                else {
//                    Intent intent = new Intent(context, InfoBookActivity.class);
//                    intent.putExtra("id_book", item.id_book);
//                    context.startActivity(intent);
//                }
//            }
//        });

        return view;
    }

    class ViewHolder {
        TextView txtAuthor;
        TextView txtName, txtEye;
        ImageView imageViewCover, imgStic, imgMoneyIc;
        RelativeLayout relItemListBook;
    }
}
