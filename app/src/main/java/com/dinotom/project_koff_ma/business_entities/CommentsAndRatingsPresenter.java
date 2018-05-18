package com.dinotom.project_koff_ma.business_entities;

import android.util.Log;

import com.dinotom.project_koff_ma.APIClient;
import com.dinotom.project_koff_ma.APIInterface;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRatingPage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener;

public class CommentsAndRatingsPresenter implements OnLoadMoreListener
{
    private static final String TAG = CommentsAndRatingsPresenter.class.getSimpleName();

    private ICommentsAndRatingsView view;
    private APIInterface apiInterface;

    private Integer CommentAndRatingPk;
    private Integer currentPage;

    public CommentsAndRatingsPresenter(ICommentsAndRatingsView view, Integer CommentAndRatingPk)
    {
        this.view = view;
        this.currentPage = 1;
        this.CommentAndRatingPk = CommentAndRatingPk;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    private void getCommentsAndRatings()
    {
        Log.d(TAG, "getCommentsAndRatings - currentPage: " + currentPage);
        view.showPaginateError(false);
        view.showPaginateLoading(true);

        Call<CommentAndRatingPage> commentAndRatingPageCall = apiInterface.getRatingsAndComments(
                CommentAndRatingPk,
                currentPage
        );
        commentAndRatingPageCall.enqueue(new Callback<CommentAndRatingPage>()
        {
            @Override
            public void onResponse(Call<CommentAndRatingPage> call, Response<CommentAndRatingPage> response)
            {
                CommentAndRatingPage commentAndRatingPage = response.body();
                if (commentAndRatingPage != null) {
                    currentPage += 1;
                    view.addItems(commentAndRatingPage.getResults());
                    if (view.getItemsNum() >= commentAndRatingPage.getCount())
                        view.setPaginateNoMoreData(true);
                }
                view.showPaginateLoading(false);
            }

            @Override
            public void onFailure(Call<CommentAndRatingPage> call, Throwable t)
            {
                view.showPaginateLoading(false);
                view.showPaginateError(true);
            }
        });
    }

    @Override
    public void onLoadMore()
    {
        Log.d(TAG, "onLoadMore");
        getCommentsAndRatings();
    }
}
