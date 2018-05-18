package com.dinotom.project_koff_ma.business_entities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRating;

import java.util.List;

import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;


public class CommentsAndRatingsFragment extends Fragment implements ICommentsAndRatingsView
{
    private static final String TAG = CommentsAndRatingsFragment.class.getSimpleName();

    RecyclerView recyclerView;

    CommentsAndRatingsAdapter commentsAndRatingsAdapter;
    CommentsAndRatingsPresenter commentsAndRatingsPresenter;

    Paginate paginate;

    public CommentsAndRatingsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments_and_ratings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        recyclerView = getView().findViewById(R.id.rv_commentsandratings);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        commentsAndRatingsAdapter = new CommentsAndRatingsAdapter(KoffGlobal.getAppContext());
        recyclerView.setAdapter(commentsAndRatingsAdapter);

        /*Intent intent = getIntent();
        Integer subcategoryPk = intent.getIntExtra("SUBCATEGORY_PK", 0);
        String subcategoryName = intent.getStringExtra("SUBCATEGORY_NAME");*/

        commentsAndRatingsPresenter = new CommentsAndRatingsPresenter(this, 50);

        paginate = new PaginateBuilder()
                .with(recyclerView)
                .setOnLoadMoreListener(commentsAndRatingsPresenter)
                .setLoadingTriggerThreshold(5) // malo se igrati s ovime
                .build();
    }

    @Override
    public void addItems(List<CommentAndRating> items) { commentsAndRatingsAdapter.addItems(items); }

    @Override
    public int getItemsNum() { return commentsAndRatingsAdapter.getItemCount(); }

    @Override
    public void showPaginateLoading(boolean isPaginateLoading) { paginate.showLoading(isPaginateLoading); }

    @Override
    public void showPaginateError(boolean isPaginateError) { paginate.showError(isPaginateError); }

    @Override
    public void setPaginateNoMoreData(boolean isNoMoreItems) { paginate.setNoMoreItems(isNoMoreItems); }

    @Override
    public void onDestroy()
    {
        paginate.unbind();
        super.onDestroy();
    }
}
