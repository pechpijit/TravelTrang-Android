package com.khiancode.traveltrang.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.SelectTravelActivity;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.CategoryTravelModel;

import java.util.ArrayList;


public class AdapterTravelSelect extends RecyclerView.Adapter<AdapterTravelSelect.VersionViewHolder> {
    ArrayList<CategoryTravelModel> model;
    Context context;
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();

    public AdapterTravelSelect(Context applicationContext, ArrayList<CategoryTravelModel> model) {
        this.context = applicationContext;
        this.model = model;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_travel_select, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
        versionViewHolder.bind(i);
        versionViewHolder.setIsRecyclable(false);
        versionViewHolder.title.setText(model.get(i).getName());
        Glide.with(context)
                .load(BaseActivity.BASE_URL_PICTURE + "/images_resize/travel/" + model.get(i).getImage())
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
        CheckBox checkBox;
        CardView cardlist_item;
        public VersionViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            title = itemView.findViewById(R.id.title);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        void bind(int position) {
            if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);
                ((SelectTravelActivity) context).activitie[position] = 0;
            }
            else {
                checkBox.setChecked(true);
                ((SelectTravelActivity) context).activitie[position] = model.get(position).getId();
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                ((SelectTravelActivity) context).activitie[adapterPosition] = model.get(adapterPosition).getId();
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                ((SelectTravelActivity) context).activitie[adapterPosition] = 0;
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }
        }
    }

}
