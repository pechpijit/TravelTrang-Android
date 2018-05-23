package com.khiancode.traveltrang.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.DetailTravelActivity;
import com.khiancode.traveltrang.EmailSignInActivity;
import com.khiancode.traveltrang.PlaceActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.adapter.AdapterCategory;
import com.khiancode.traveltrang.adapter.AdapterComment;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.GroupCommentModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class CommentFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterComment adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int ID;

    TextView noData;

    public CommentFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CommentFragment(int id) {
        this.ID = id;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((DetailTravelActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        recyclerView = view.findViewById(R.id.dummyfrag_scrollableview);
        noData = view.findViewById(R.id.noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        ((DetailTravelActivity)getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

        getData();

        return view;
    }

    private void getData() {
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL+"user/comment-travel/"+ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                noData.setVisibility(View.INVISIBLE);
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
//                ((DetailTravelActivity) getActivity()).dialogResultError();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void ResultNull(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
//                ((DetailTravelActivity) getActivity()).dialogResultNull();
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<GroupCommentModel>>() {}.getType();
        Collection<GroupCommentModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<GroupCommentModel> posts = new ArrayList<GroupCommentModel>(enums);

        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);

        adapter = new AdapterComment(getActivity(), posts,sp.getInt("id",0));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ((DetailTravelActivity)getActivity()).fab.getVisibility() == View.VISIBLE) {
                    ((DetailTravelActivity)getActivity()).fab.hide();
                } else if (dy < 0 && ((DetailTravelActivity)getActivity()).fab.getVisibility() != View.VISIBLE) {
                    ((DetailTravelActivity)getActivity()).fab.show();
                }
            }
        });

        adapter.SetOnItemClickListener(new AdapterComment.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                int ID =posts.get(position).get();
//                Intent intent = new Intent(getActivity(), PlaceActivity.class);
//                intent.putExtra("id", ID);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    private void dialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog_comment,null);
        builder.setView(view);

        final EditText comment = view.findViewById(R.id.input_comment);

        builder.setTitle("คอมเมนต์");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DetailTravelActivity)getActivity()).showProgressDialog(BaseActivity.LOAD);
                postComment(comment.getText().toString());
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void postComment(String detail) {
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);

        RequestBody requestBody = new FormBody.Builder()
                .add("detail", detail.trim())
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .add("travelId", String.valueOf(ID))
                .build();

        ApiClient.POST post = new ApiClient.POST(getActivity());
        post.setURL(BaseActivity.BASE_URL+"user/comment-create");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                getData();
            }

            @Override
            public void ResultError(String data) {
                ((DetailTravelActivity)getActivity()).hideProgressDialog();
                ((DetailTravelActivity)getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                ((DetailTravelActivity)getActivity()).hideProgressDialog();
                ((DetailTravelActivity)getActivity()).dialogResultNull();
            }
        });
    }

}
