package com.khiancode.traveltrang.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.CommentModel;
import com.khiancode.traveltrang.model.GroupCommentModel;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;


public class AdapterComment extends RecyclerView.Adapter<AdapterComment.VersionViewHolder> {
    ArrayList<GroupCommentModel> model;
    Context context;
    int custId;
    OnItemClickListener clickListener;

    public AdapterComment(Context applicationContext, ArrayList<GroupCommentModel> model, int custId) {
        this.context = applicationContext;
        this.model = model;
        this.custId = custId;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comment, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder viewHolder, final int i) {

        viewHolder.title.setText(model.get(i).getCustomer().getName());
        if (model.get(i).getComment().getDetail().length() > 30) {
            viewHolder.detail.setVisibility(View.VISIBLE);
            viewHolder.detail.setText(model.get(i).getComment().getDetail().substring(0, 30) + " เพิ่มเติม...");

            viewHolder.ex_detailohter.setVisibility(View.INVISIBLE);
            viewHolder.ex_detailohter.setText(model.get(i).getComment().getDetail());
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.ex_detailohter.toggle();
                    if (viewHolder.ex_detailohter.isExpanded()) {
                        viewHolder.ex_detailohter.collapse();
                        viewHolder.detail.setVisibility(View.VISIBLE);
                        viewHolder.ex_detailohter.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.ex_detailohter.expand();
                        viewHolder.detail.setVisibility(View.INVISIBLE);
                        viewHolder.ex_detailohter.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            viewHolder.detail.setVisibility(View.VISIBLE);
            viewHolder.detail.setText(model.get(i).getComment().getDetail());
            viewHolder.ex_detailohter.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profile;
        TextView title, detail;
        ExpandableTextView ex_detailohter;
        LinearLayout layout;
        public VersionViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.cardlist_item);
            profile = itemView.findViewById(R.id.profile);
            title = itemView.findViewById(R.id.title);
            detail = itemView.findViewById(R.id.detail);
            ex_detailohter = itemView.findViewById(R.id.ex_detailohter);

            setExp(ex_detailohter);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    private void setExp(ExpandableTextView exp) {
        exp.setAnimationDuration(1000L);
        exp.setPadding(10,10,10,10);
        exp.setInterpolator(new OvershootInterpolator());
        exp.setExpandInterpolator(new OvershootInterpolator());
        exp.setCollapseInterpolator(new OvershootInterpolator());
        exp.addOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(@NonNull final ExpandableTextView view)
            {

            }

            @Override
            public void onCollapse(@NonNull final ExpandableTextView view)
            {

            }
        });
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
