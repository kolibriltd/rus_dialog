package com.wearesputnik.istoria.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.activity.InfoBookActivity;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.models.BookModel;

import java.io.ByteArrayOutputStream;
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

    public BooksAdapter (Context context, boolean guestFlag) {
        super(context, 0);
        booksList = new ArrayList<>();
        this.guestFlag = guestFlag;
        this.context = context;
        options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.icon_app)
            .showImageForEmptyUri(R.mipmap.icon_app)
            .showImageOnFail(R.mipmap.icon_app)
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
            holder.txtEve = (TextView) view.findViewById(R.id.txtEve);
            holder.imageViewCover = (ImageView) view.findViewById(R.id.imageViewCover);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.txtName.setText(item.name);
        holder.txtAuthor.setText(item.author);
        holder.txtEve.setText(item.isViewCount + "");

        if (item.pathCoverFileStorage != null) {
            holder.imageViewCover.setImageURI(Uri.parse(item.pathCoverFileStorage));
        } else if (item.pathCoverFile != null) {
            String url_img = HttpConnectClass.URL_IMAGE + item.pathCoverFile;
            ImageLoader.getInstance()
                .displayImage(url_img, holder.imageViewCover, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (item.pathCoverFileStorage == null) {
                            BookModel bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", item.id_book).executeSingle();
                            bookModelOne.PathCoverFileStorage = getImageUri(context, loadedImage).toString();
                            if (bookModelOne.PathCoverFileStorage != null || !bookModelOne.PathCoverFileStorage.trim().equals("null")) {
                                item.pathCoverFileStorage = bookModelOne.PathCoverFileStorage;
                                bookModelOne.save();
                            }
                        }
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoBookActivity.class);
                intent.putExtra("id_book", item.id_book);
                context.startActivity(intent);
            }
        });

        return view;
    }

    class ViewHolder {
        TextView txtAuthor;
        TextView txtName;
        TextView txtEve;
        ImageView imageViewCover;
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            /*System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal);
            string localPath = System.IO.Path.Combine(documentsDirectory, ImageName);*/
            String patch = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Istoria", null);
            return Uri.parse(patch);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
