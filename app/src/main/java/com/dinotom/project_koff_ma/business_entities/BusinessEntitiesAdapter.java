package com.dinotom.project_koff_ma.business_entities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BusinessEntitiesAdapter extends RecyclerView.Adapter<BusinessEntitiesAdapter.ViewHolder>
{
    private List<BusinessEntity> businessEntities;
    private Context mCtx;

    BusinessEntitiesAdapter(Context mCtx)
    {
        this.mCtx = mCtx;
        businessEntities = new ArrayList<>();
    }

    BusinessEntitiesAdapter(Context mCtx, List<BusinessEntity> newBusinessEntities)
    {
        this.mCtx = mCtx;
        businessEntities = newBusinessEntities;
    }

    @Override
    public BusinessEntitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.businessentity_card, parent, false);

        return new BusinessEntitiesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BusinessEntitiesAdapter.ViewHolder holder, final int position)
    {
        holder.businessEntityName.setText(businessEntities.get(position).getName());
        holder.businessEntityAddress.setText(businessEntities.get(position).getAddress());

        String distance = String.format("%.1f km", businessEntities.get(position).getDistance() / 1000.0);

        holder.businessEntityDistanceTo.setText(distance);
        Double avgRating = businessEntities.get(position).getAvgRating();
        if(avgRating != null)
            holder.businessEntityAvgRating.setRating(avgRating.floatValue());
        else
            holder.businessEntityAvgRating.setRating(0);

        List<DayWorkingHours> workingHours = businessEntities.get(position).getWorkingHours();
        try {
            if(!workingHours.isEmpty() && BusinessEntitiesUtilities.isWorkingNow(workingHours))
            {
                String open = KoffGlobal.getAppContext().getResources().getString(R.string.businessentity_open);
                holder.businessEntityIsWorking.setTextColor(KoffGlobal.getAppContext().getResources().getColor(R.color.openColor));
                holder.businessEntityIsWorking.setText(open);
            }
            else
            {
                String closed = KoffGlobal.getAppContext().getResources().getString(R.string.businessentity_closed);
                holder.businessEntityIsWorking.setTextColor(KoffGlobal.getAppContext().getResources().getColor(R.color.closedColor));
                holder.businessEntityIsWorking.setText(closed);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, BusinessEntityInfoActivity.class);
                intent.putExtra("ENTITY_PK", businessEntities.get(position).getPk());
                intent.putExtra("ENTITY_AVG_SCORE", businessEntities.get(position).getAvgRating());
                intent.putExtra("ENTITY_WORKING", holder.businessEntityIsWorking.getText());
                mCtx.startActivity(intent);
            }
        });
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
        private TextView businessEntityAddress;
        private TextView businessEntityIsWorking;
        private TextView businessEntityDistanceTo;
        private RatingBar businessEntityAvgRating;

        public ViewHolder(final View itemView) {
            super(itemView);

            businessEntityName = itemView.findViewById(R.id.businessentity_name);
            businessEntityAddress = itemView.findViewById(R.id.businessentity_address);
            businessEntityIsWorking = itemView.findViewById(R.id.businessentity_isworking);
            businessEntityDistanceTo = itemView.findViewById(R.id.businessentity_distance);
            businessEntityAvgRating = itemView.findViewById(R.id.businessentity_avg_rating);
        }
    }
}
