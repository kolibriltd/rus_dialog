package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.activity.GuestActivity;
import com.wearesputnik.istoria.activity.InfoBookActivity;
import com.wearesputnik.istoria.activity.ListBookActivity;
import com.wearesputnik.istoria.activity.SingupActivity;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.models.BookModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 02.06.17.
 */
public class BooksAdapter extends ArrayAdapter<Books> {
    List<Books> booksList;
    Context context;
    private DisplayImageOptions options;
    boolean guestFlag;
    Bitmap m_currentBitmap;

    public BooksAdapter (Context context, boolean guestFlag) {
        super(context, 0);
        booksList = new ArrayList<>();
        this.guestFlag = guestFlag;
        this.context = context;
        options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.null_foto)
            .showImageForEmptyUri(R.mipmap.null_foto)
            .showImageOnFail(R.mipmap.null_foto)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
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
            holder.txtRaiting = (TextView) view.findViewById(R.id.txtRaiting);
            holder.imageViewCover = (ImageView) view.findViewById(R.id.imageViewCover);
            holder.imgStic = (ImageView) view.findViewById(R.id.imgStic);
            holder.relItemListBook = (RelativeLayout) view.findViewById(R.id.relItemListBook);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.relItemListBook.setVisibility(View.VISIBLE);
        holder.txtName.setText(item.name);
        holder.txtAuthor.setText(item.author);
        holder.txtRaiting.setText(item.raiting);

        if (item.pathCoverFileStorage != null) {
            holder.imageViewCover.setImageURI(Uri.parse(item.pathCoverFileStorage));
        } else if (item.pathCoverFile != null) {
            String url_img = HttpConnectClass.URL_IMAGE + item.pathCoverFile;

            Glide.with(context)
                    .load(url_img)
                    .into(holder.imageViewCover);
        }

        if (item.new_istori_int == 1) {
            holder.imgStic.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgStic.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoBookActivity.class);
                intent.putExtra("id_book", item.id_book);
                intent.putExtra("guestFlag", guestFlag);
                context.startActivity(intent);
            }
        });


        return view;
    }

    class ViewHolder {
        TextView txtAuthor;
        TextView txtName;
        TextView txtRaiting;
        ImageView imageViewCover, imgStic;
        RelativeLayout relItemListBook;
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            int widthBit = 0;
            int heightBit = 0;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int widthScrin = size.x;

            int bitWidth = bitmap.getWidth();
            int bitHeight = bitmap.getHeight();
            if (widthScrin < bitWidth) {
                widthBit = widthScrin;
                int widthBitProc = ((bitWidth - widthBit)*100)/bitWidth;
                heightBit = (bitHeight*(100-widthBitProc))/100;
            }
            else {
                widthBit = bitWidth;
                heightBit = bitHeight;
            }

            Bitmap resized = Bitmap.createScaledBitmap(bitmap, widthBit, heightBit, true);

            String patch = MediaStore.Images.Media.insertImage(context.getContentResolver(), resized, "Istoria", null);
            return Uri.parse(patch);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
