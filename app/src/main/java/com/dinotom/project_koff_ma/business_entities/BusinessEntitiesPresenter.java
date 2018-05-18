package com.dinotom.project_koff_ma.business_entities;

import android.util.Log;
import android.widget.Toast;

import com.dinotom.project_koff_ma.APIClient;
import com.dinotom.project_koff_ma.APIInterface;
import com.dinotom.project_koff_ma.CategoryAdapter;
import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntityPage;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener;

public class BusinessEntitiesPresenter implements OnLoadMoreListener
{
    private static final String TAG = BusinessEntitiesPresenter.class.getSimpleName();

    private IBusinessEntitiesView view;
    private APIInterface apiInterface;

    private Integer SubcategoryPk;
    private Integer currentPage;

    public BusinessEntitiesPresenter(IBusinessEntitiesView view, Integer SubcategoryPk)
    {
        this.view = view;
        this.currentPage = 1;
        this.SubcategoryPk = SubcategoryPk;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    private void getBusinessEntities()
    {
        Log.d(TAG, "getBusinessEntities - currentPage: " + currentPage);
        view.showPaginateError(false);
        view.showPaginateLoading(true);

        Call<BusinessEntityPage> businessEntityPageCall = apiInterface.getBusinessEntities(
                SubcategoryPk,
                BusinessEntitiesUtilities.getLocation(),
                BusinessEntitiesUtilities.getRadius().doubleValue(),
                BusinessEntitiesUtilities.getIsWorking(),
                BusinessEntitiesUtilities.getSortMode(),
                currentPage
        );
        businessEntityPageCall.enqueue(new Callback<BusinessEntityPage>()
        {
            @Override
            public void onResponse(Call<BusinessEntityPage> call, Response<BusinessEntityPage> response)
            {
                BusinessEntityPage businessEntityPage = response.body();
                if (businessEntityPage != null) {
                    currentPage += 1;
                    view.addItems(businessEntityPage.getResults());
                    if (view.getItemsNum() >= businessEntityPage.getCount())
                        view.setPaginateNoMoreData(true);
                }
                view.showPaginateLoading(false);
            }

            @Override
            public void onFailure(Call<BusinessEntityPage> call, Throwable t)
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
        getBusinessEntities();
    }
}
