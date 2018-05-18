package com.dinotom.project_koff_ma.business_entities;

import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRating;

import java.util.List;

import ru.alexbykov.nopaginate.callback.PaginateView;

public interface ICommentsAndRatingsView extends PaginateView
{
    void addItems(List<CommentAndRating> items);
    public int getItemsNum();
}