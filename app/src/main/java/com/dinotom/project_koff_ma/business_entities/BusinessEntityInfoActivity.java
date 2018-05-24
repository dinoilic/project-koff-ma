package com.dinotom.project_koff_ma.business_entities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.APIClient;
import com.dinotom.project_koff_ma.APIInterface;
import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.UserPk;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntityDetails;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRating;
import com.dinotom.project_koff_ma.pojo.business_entities.PostCommentAndRating;
import com.dinotom.project_koff_ma.pojo.business_entities.UserCommentAndRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;
import ru.noties.markwon.Markwon;

public class BusinessEntityInfoActivity extends AppCompatActivity implements ICommentsAndRatingsView
{

    APIInterface apiInterface;
    private static final String TAG = BusinessEntityInfoActivity.class.getSimpleName();

    RecyclerView recyclerView;

    CommentsAndRatingsAdapter commentsAndRatingsAdapter;
    CommentsAndRatingsPresenter commentsAndRatingsPresenter;

    Paginate paginate;

    int entityPk;
    int commentAndRatingPk = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessentity_info);

        Intent intent = getIntent();
        entityPk = intent.getIntExtra("ENTITY_PK", -1);
        final double entityAvgScore = intent.getDoubleExtra("ENTITY_AVG_SCORE", -1);
        final String entityIsWorking = intent.getStringExtra("ENTITY_WORKING");

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<BusinessEntityDetails> detailsCall = apiInterface.getBusinessEntityDetails(entityPk);
        detailsCall.enqueue(new Callback<BusinessEntityDetails>()
        {
            @Override
            public void onResponse(Call<BusinessEntityDetails> call, Response<BusinessEntityDetails> response)
            {
                BusinessEntityDetails details = response.body(); // Category pojo is fetched

                TextView tv = findViewById(R.id.entityName);
                tv.setText(details.getName());

                TextView tvAddress = findViewById(R.id.entityAddress);
                tvAddress.setText(details.getAddress());

                RatingBar rtBusinessEntity = findViewById(R.id.bedetails_avgrating);
                rtBusinessEntity.setRating((float) entityAvgScore);

                TextView tvBusinessEntityWorking = findViewById(R.id.bedetails_isworking);
                tvBusinessEntityWorking.setText(entityIsWorking);

                if(entityIsWorking.equals(KoffGlobal.getAppContext().getResources().getString(R.string.businessentity_open))) {
                    tvBusinessEntityWorking.setTextColor(getResources().getColor(R.color.openColor));
                } else if (entityIsWorking.equals(KoffGlobal.getAppContext().getResources().getString(R.string.businessentity_closed))) {
                    tvBusinessEntityWorking.setTextColor(getResources().getColor(R.color.closedColor));
                }

                List<String> emails = details.geteMail();
                ConstraintLayout cLayout = findViewById(R.id.beEmailsLayout);
                ArrayList<Integer> listEmails = new ArrayList<Integer>(
                        Arrays.asList(R.id.beFirstEmail, R.id.beSecondEmail, R.id.beThirdEmail)
                );

                for (Integer listEmail : listEmails) {
                    TextView tvEmail = findViewById(listEmail);
                    try {
                        tvEmail.setText(emails.get(listEmails.indexOf(listEmail)));
                    }
                    catch (Exception e) {
                        cLayout.removeView(tvEmail);
                    }
                }

                List<String> webs = details.getWebSite();
                ConstraintLayout cwLayout = findViewById(R.id.beWebsitesLayout);
                ArrayList<Integer> listWebs = new ArrayList<Integer>(
                        Arrays.asList(R.id.beFirstWeb, R.id.beSecondWeb, R.id.beThirdWeb)
                );

                for (Integer listWeb : listWebs) {
                    TextView tvWeb = findViewById(listWeb);
                    try {
                        tvWeb.setText(webs.get(listWebs.indexOf(listWeb)));
                    }
                    catch (Exception e) {
                        cwLayout.removeView(tvWeb);
                    }
                }

                TextView tvDescription = findViewById(R.id.entityDescription);
                String description = response.body().getDescription() != null ? response.body().getDescription() : "";
                Markwon.setMarkdown(tvDescription, description);
            }

            @Override
            public void onFailure(Call<BusinessEntityDetails> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Business Entity Details fetching unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        setupUserCommentAndRatingCall(entityPk);
        setupCommentsCall();

        final Button ratingSubmitButton = findViewById(R.id.rating_submit_button);
        ratingSubmitButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Float rating = ((RatingBar)findViewById(R.id.business_user_rating)).getRating();
                if(commentAndRatingPk == -1)
                    postCommentAndRatingCall(entityPk, rating.intValue(), "");
                else
                    updateCommentAndRatingCall(commentAndRatingPk, rating.intValue(), "");
            }
        });
        ratingSubmitButton.setVisibility(View.GONE);

        final Button ratingDeleteButton = findViewById(R.id.rating_delete_button);
        ratingDeleteButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deleteCommentAndRatingCall();
            }
        });
    }

    private void setupCommentsCall()
    {
        Call<UserPk> userCall = apiInterface.getUserPk();

        userCall.enqueue(new Callback<UserPk>()
        {
            @Override
            public void onResponse(Call<UserPk> call, Response<UserPk> response)
            {
                Log.d(TAG, response.body().getUserPk());
                setupComments(response.body());
            }

            @Override
            public void onFailure(Call<UserPk> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "User Pk fetching unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupComments(final UserPk userPk)
    {
        recyclerView = findViewById(R.id.rv_commentsandratings_in_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        commentsAndRatingsAdapter = new CommentsAndRatingsAdapter(KoffGlobal.getAppContext(), userPk.getUserPk());
        recyclerView.setAdapter(commentsAndRatingsAdapter);

        commentsAndRatingsPresenter = new CommentsAndRatingsPresenter(this, entityPk);

        paginate = new PaginateBuilder()
                .with(recyclerView)
                .setOnLoadMoreListener(commentsAndRatingsPresenter)
                .setLoadingTriggerThreshold(5) // malo se igrati s ovime
                .build();
    }

    private void setupUserCommentAndRatingCall(final Integer businessEntityPk)
    {
        Call<UserCommentAndRating> userCommentAndRatingCall = apiInterface.getUserRatingAndComment(businessEntityPk);

        userCommentAndRatingCall.enqueue(new Callback<UserCommentAndRating>()
        {
            @Override
            public void onResponse(Call<UserCommentAndRating> call, Response<UserCommentAndRating> response)
            {
                Log.d(TAG, String.format("setup Comment call: %s %d", response.body().getUserComment(), response.body().getUserRating()));
                setupUserCommentAndRating(response.body());
            }

            @Override
            public void onFailure(Call<UserCommentAndRating> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "User Comment and Rating fetching unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUserCommentAndRating(final UserCommentAndRating userCommentAndRating)
    {
        Button ratingDeleteButton = findViewById(R.id.rating_delete_button);
        final RatingBar userRatingBar = findViewById(R.id.business_user_rating);

        Button commentButton = findViewById(R.id.comment_button);
        View commentBottomLine = findViewById(R.id.commentBottomLine);

        if(userCommentAndRating.getUserRating() == -1)
        {
            ratingDeleteButton.setVisibility(View.GONE);
            userRatingBar.setRating(0);
        }
        else
            userRatingBar.setRating(userCommentAndRating.getUserRating());

        if(!userCommentAndRating.getUserComment().isEmpty())
        {
            commentButton.setVisibility(View.GONE);
            commentBottomLine.setVisibility(View.GONE);
        }

        commentAndRatingPk = userCommentAndRating.getPk();

        final Button ratingSubmitButton = findViewById(R.id.rating_submit_button);
        final float currentRating = userRatingBar.getRating();
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            float defaultRating = currentRating;

            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b)
            {
                if(v != defaultRating)
                    ratingSubmitButton.setVisibility(View.VISIBLE);
                else
                    ratingSubmitButton.setVisibility(View.GONE);
            }
        });
    }

    private void postCommentAndRatingCall(Integer entityPk, Integer rating, String comment)
    {
        Log.d(TAG, String.format("postCommentAndRatingCall: %d %d %s", entityPk, rating, comment));
        Call<PostCommentAndRating> postCommentAndRatingCall = apiInterface.postCommentAndRating(entityPk, rating, comment);

        postCommentAndRatingCall.enqueue(new Callback<PostCommentAndRating>()
        {
            @Override
            public void onResponse(Call<PostCommentAndRating> call, Response<PostCommentAndRating> response)
            {
                Log.d(TAG, String.format("Response in postCommendAndRatingCall: %d", response.code()));
                recreate();
            }

            @Override
            public void onFailure(Call<PostCommentAndRating> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "User Comment and Rating posting unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCommentAndRatingCall(Integer pk, Integer rating, String comment)
    {
        Call<ResponseBody> updateCommentAndRatingCall;

        if(rating == -1)
            updateCommentAndRatingCall = apiInterface.updateComment(pk, comment);
        else if(comment.isEmpty())
            updateCommentAndRatingCall = apiInterface.updateRating(pk, rating);
        else
            updateCommentAndRatingCall = apiInterface.updateCommentAndRating(pk, rating, comment);

        updateCommentAndRatingCall.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.d(TAG, String.format("Response in updateCommentAndRatingCall: %d", response.code()));
                recreate();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "User Comment and/or Rating updating unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCommentAndRatingCall()
    {
        Call<ResponseBody> deleteCommentAndRatingCall = apiInterface.deleteCommentAndRating(commentAndRatingPk);

        deleteCommentAndRatingCall.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.d(TAG, String.format("Response in deleteCommendAndRatingCall: %d", response.code()));
                recreate();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "User Comment and Rating deleting unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() { super.onStart(); }

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
