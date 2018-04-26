package com.dinotom.project_koff_ma.business_entities;

import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;

import java.util.List;

import ru.alexbykov.nopaginate.callback.PaginateView;

public interface IBusinessEntitiesView extends PaginateView
{
    void addItems(List<BusinessEntity> items);
    public int getItemsNum();
}