package com.ezly.ezly_android.UI.Post;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 21/11/16.
 */

public class PostImageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_UPLOAD_BUTTON = 1;

    private static final int MAX_IMAGE_ALLOWED = 5;

    private int adapterType;
    private List<EzlyImage> images;

    private PostImageAdapterListener mListener;

    public PostImageListAdapter(List<EzlyImage> images) {
        if(images == null){
            images = new ArrayList<>();
        }
        this.images = images;
    }

    public void setListener(PostImageAdapterListener mListener) {
        this.mListener = mListener;
    }

    public List<EzlyImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<EzlyImage> images) {
        this.images = images;
        notifyDataSetChanged();
    }


    public void addImages(List<EzlyImage> images) {
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void removeImage(int index) {
        this.images.remove(index);
        notifyDataSetChanged();
    }

    public void removeImage(EzlyImage image) {
        this.images.remove(image);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_IMAGE;
        if(position == images.size()){
            viewType = VIEW_TYPE_UPLOAD_BUTTON;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upload_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageViewHolder imageViewHolder = (ImageViewHolder)holder;

        WindowManager wm = (WindowManager) imageViewHolder.imageView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        final int imageSize = (screenWidth - UIHelper.dip2px(imageViewHolder.imageView.getContext(), 60)) / 5;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageViewHolder.imageView.setLayoutParams(lp);

        switch (getItemViewType(position)){
            case VIEW_TYPE_IMAGE:
                setupImage(imageViewHolder, position);
                break;
            case VIEW_TYPE_UPLOAD_BUTTON:
                imageViewHolder.imageView.setImageDrawable(imageViewHolder.imageView.getContext().getResources().getDrawable(R.drawable.btn_upload));
                imageViewHolder.imageView.setBackgroundColor(imageViewHolder.imageView.getContext().getResources().getColor(R.color.black_transparent_50));
                imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener != null){
                            int imageCount = images.size();
                            mListener.onAddImageClicked(MAX_IMAGE_ALLOWED - imageCount);
                        }
                    }
                });
                break;

        }
    }

    private void setupImage(ImageViewHolder imageViewHolder, final int position) {
        imageViewHolder.imageView.setBackgroundColor(imageViewHolder.imageView.getContext().getResources().getColor(R.color.white));
        EzlyImage ezlyImage = images.get(position);
        if(!TextUtils.isEmpty(images.get(position).getUrl())) {
            Picasso.with(imageViewHolder.imageView.getContext())
                    .load(ezlyImage.getUrl())
                    .fit()
                    .into(imageViewHolder.imageView);
        }
        else if(!TextUtils.isEmpty(ezlyImage.getPath())){
            Bitmap myBitmap = BitmapFactory.decodeFile(ezlyImage.getPath());
            imageViewHolder.imageView.setImageBitmap(myBitmap);
        }


        imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onImageClicked(images.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = images.size();
        return count == MAX_IMAGE_ALLOWED ? MAX_IMAGE_ALLOWED : count + 1;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image_view) ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface PostImageAdapterListener{
        void onAddImageClicked(int limit);
        void onImageClicked(EzlyImage image, int position);
    }
}
