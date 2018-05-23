package com.khiancode.traveltrang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.DIaryModel;

import java.util.ArrayList;


public class AdapterDiaryList extends RecyclerView.Adapter<AdapterDiaryList.VersionViewHolder> {
    ArrayList<DIaryModel> model;
    Context context;
    OnItemClickListener clickListener;

    public AdapterDiaryList(Context applicationContext, ArrayList<DIaryModel> model) {
        this.context = applicationContext;
        this.model = model;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_category, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
        if (model.get(i).getDetail().length() > 30) {
            versionViewHolder.title.setText(model.get(i).getDetail().substring(0,30)+"...");
        } else {
            versionViewHolder.title.setText(model.get(i).getDetail());
        }
        Glide.with(context)
                .load(BaseActivity.BASE_URL_PICTURE + "/images_resize/" + model.get(i).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.nopic)
                .into(versionViewHolder.logo);
    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView logo;
        TextView title;
        public VersionViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
