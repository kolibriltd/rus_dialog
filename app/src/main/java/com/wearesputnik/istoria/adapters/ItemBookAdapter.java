package com.wearesputnik.istoria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.activity.YoutubeActivity;
import com.wearesputnik.istoria.helpers.Config;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
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
    private DisplayImageOptions options;

    public ItemBookAdapter (Context context, String nameA, String nameB) {
        super(context, 0);
        textInfoList = new ArrayList<>();
        this.context = context;
        this.nameA = nameA;
        this.nameB = nameB;
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
        final TextInfo item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_text_book, null);
            ViewHolder holder = new ViewHolder();
            holder.relPeopleA = (RelativeLayout) view.findViewById(R.id.relPeopleA);
            holder.txtNamePeopleA = (TextView) view.findViewById(R.id.txtNamePeopleA);
            holder.txtPeopleA = (TextView) view.findViewById(R.id.txtPeopleA);

            holder.relPeopleB = (RelativeLayout) view.findViewById(R.id.relPeopleB);
            holder.txtPeopleB = (TextView) view.findViewById(R.id.txtPeopleB);
            holder.txtNamePeopleB = (TextView) view.findViewById(R.id.txtNamePeopleB);

            holder.txtDescAB = (TextView) view.findViewById(R.id.txtDescAB);
            holder.relDescAB = (RelativeLayout) view.findViewById(R.id.relDescAB);

            holder.relEnd = (RelativeLayout) view.findViewById(R.id.relEnd);

            holder.relEmty = (RelativeLayout) view.findViewById(R.id.relEmty);

            holder.relImageA = (RelativeLayout) view.findViewById(R.id.relImageA);
            holder.txtImageNameA = (TextView) view.findViewById(R.id.txtImageNameA);
            holder.imgPeopleA = (ImageView) view.findViewById(R.id.imgPeopleA);

            holder.relImageB = (RelativeLayout) view.findViewById(R.id.relImageB);
            holder.txtImageNameB = (TextView) view.findViewById(R.id.txtImageNameB);
            holder.imgPeopleB = (ImageView) view.findViewById(R.id.imgPeopleB);

            holder.relVideoB = (RelativeLayout) view.findViewById(R.id.relVideoB);
            holder.youtube_view = (YouTubeThumbnailView) view.findViewById(R.id.youtube_thumbnail);
            holder.txtVideoNameB = (TextView) view.findViewById(R.id.txtVideoNameB);

            holder.relVideoA = (RelativeLayout) view.findViewById(R.id.relVideoA);
            holder.youtube_view_a = (YouTubeThumbnailView) view.findViewById(R.id.youtube_thumbnail_a);
            holder.txtVideoNameA = (TextView) view.findViewById(R.id.txtVideoNameA);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.relImageA.setVisibility(View.GONE);
        holder.relImageB.setVisibility(View.GONE);
        holder.relPeopleA.setVisibility(View.GONE);
        holder.relPeopleB.setVisibility(View.GONE);
        holder.relVideoB.setVisibility(View.GONE);
        holder.relVideoA.setVisibility(View.GONE);
        holder.relDescAB.setVisibility(View.GONE);
        holder.relEmty.setVisibility(View.GONE);
        holder.relEnd.setVisibility(View.GONE);

        if (item.peopleA != null) {
            holder.relPeopleA.setVisibility(View.VISIBLE);

            holder.txtPeopleA.setText(item.peopleA);
            holder.txtNamePeopleA.setText(this.nameA);
            if (!item.flags) {
                StartAnimation(holder.relPeopleA);
            }
        }
        if (item.imgPeopleA != null) {
            holder.relImageA.setVisibility(View.VISIBLE);

            holder.imgPeopleA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_photo_book);
                    ImageView imgPhotoBook = (ImageView) dialog.findViewById(R.id.imgPhotoBook);

                    ImageLoaderView(HttpConnectClass.URL_IMAGE + item.imgPeopleA, imgPhotoBook);

                    imgPhotoBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            ImageLoaderView(HttpConnectClass.URL_IMAGE + item.imgPeopleA, holder.imgPeopleA);
            holder.txtImageNameA.setText(this.nameA);
            if (!item.flags) {
                StartAnimation(holder.relImageA);
            }
        }
        if (item.videoPeopleA != null) {
            holder.relVideoA.setVisibility(View.VISIBLE);

            final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    ///holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };

            holder.youtube_view_a.initialize(Config.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    youTubeThumbnailLoader.setVideo(item.videoPeopleA);
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });

            holder.youtube_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, YoutubeActivity.class);
                    intent.putExtra("video", item.videoPeopleA);
                    context.startActivity(intent);
                }
            });

            holder.txtVideoNameA.setText(this.nameA);
            if (!item.flags) {
                StartAnimation(holder.relImageA);
            }
        }
        if (item.peopleB != null) {
            holder.relPeopleB.setVisibility(View.VISIBLE);

            holder.txtPeopleB.setText(item.peopleB);
            holder.txtNamePeopleB.setText(this.nameB);
            if (!item.flags) {
                StartAnimation(holder.relPeopleB);
            }
        }
        if (item.imgPeopleB != null) {
            holder.relImageB.setVisibility(View.VISIBLE);

            holder.relImageB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_photo_book);
                    ImageView imgPhotoBook = (ImageView) dialog.findViewById(R.id.imgPhotoBook);

                    ImageLoaderView(HttpConnectClass.URL_IMAGE + item.imgPeopleB, imgPhotoBook);

                    imgPhotoBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            ImageLoaderView(HttpConnectClass.URL_IMAGE + item.imgPeopleB, holder.imgPeopleB);
            holder.txtImageNameB.setText(this.nameB);
            if (!item.flags) {
                StartAnimation(holder.relImageB);
            }
        }
        if (item.videoPeopleB != null) {
            holder.relVideoB.setVisibility(View.VISIBLE);

            final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    ///holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };

            holder.youtube_view.initialize(Config.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    youTubeThumbnailLoader.setVideo(item.videoPeopleB);
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });

            holder.youtube_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, YoutubeActivity.class);
                    intent.putExtra("video", item.videoPeopleB);
                    context.startActivity(intent);
                }
            });

            holder.txtVideoNameB.setText(this.nameB);
            if (!item.flags) {
                StartAnimation(holder.relImageB);
            }
        }
        if (item.context != null) {
            holder.relDescAB.setVisibility(View.VISIBLE);

            holder.txtDescAB.setText(item.context);
            if (!item.flags) {
                StartAnimation(holder.relDescAB);
            }
        }
        if (item.callPeopleB != null) {
            holder.relDescAB.setVisibility(View.VISIBLE);

            holder.txtDescAB.setText(this.nameB + " сбросил вызов");
            if (!item.flags) {
                StartAnimation(holder.relDescAB);
            }
        }
        if (item.metka != null) {
            holder.relEnd.setVisibility(View.VISIBLE);

            if (!item.flags) {
                StartAnimation(holder.relEnd);
            }
        }
        if (item.emptyFlag) {
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
        TextView txtPeopleA, txtNamePeopleA, txtImageNameA, txtVideoNameA;
        TextView txtPeopleB, txtNamePeopleB, txtImageNameB, txtVideoNameB;
        TextView txtDescAB;
        ImageView imgPeopleB, imgPeopleA;
        RelativeLayout relPeopleA,relPeopleB, relDescAB, relEnd, relEmty, relImageB, relImageA, relVideoB, relVideoA;
        YouTubeThumbnailView youtube_view, youtube_view_a;
    }

    public void ImageLoaderView(String url_img, ImageView imageView) {
        ImageLoader.getInstance()
                .displayImage(url_img, imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
//        Picasso.with(context)
//            .load(url_img)
//            .error(R.mipmap.null_foto)
//            .into(imageView);
    }
}
