package com.dinotom.project_koff_ma.business_entities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.SubcategoryAdapter;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;

import java.util.ArrayList;
import java.util.List;

public class BusinessEntitiesAdapter extends RecyclerView.Adapter<BusinessEntitiesAdapter.ViewHolder>
{
    private List<BusinessEntity> businessEntities;

    BusinessEntitiesAdapter()
    {
        businessEntities = new ArrayList<>();
    }

    BusinessEntitiesAdapter(List<BusinessEntity> newBusinessEntities)
    {
        businessEntities = newBusinessEntities;
    }

    @Override
    public BusinessEntitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.businessentity_card, null);

        return new BusinessEntitiesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BusinessEntitiesAdapter.ViewHolder holder, final int position)
    {
        holder.businessEntityName.setText(businessEntities.get(position).getName());
    }

    public void addItems(List<BusinessEntity> items)
    {
        this.businessEntities.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return businessEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView businessEntityName;

        public ViewHolder(final View itemView) {
            super(itemView);

            businessEntityName = itemView.findViewById(R.id.businessentity_name);
        }
    }
}
