package com.dinotom.project_koff_ma;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinotom.project_koff_ma.pojo.category.Child;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>
{
    private ArrayList<Child> subcategoryList;

    SubcategoryAdapter(ArrayList<Child> subcategories)
    {
        subcategoryList = subcategories;
    }

    @Override
    public int getItemCount()
    {
        return subcategoryList.size();
    }

    @Override
    public SubcategoryAdapter.SubcategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.subcategory_card, null);

        return new SubcategoryAdapter.SubcategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubcategoryAdapter.SubcategoryViewHolder holder, final int position)
    {
        final Child subcategory = subcategoryList.get(position);

        holder.subcategoryName.setText(subcategory.getName());
        Picasso.get().load(subcategory.getImage()).into(holder.subcategoryImage);
    }

    class SubcategoryViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView subcategoryName;
        ImageView subcategoryImage;

        public SubcategoryViewHolder(final View itemView)
        {
            super(itemView);
            view = itemView;

            subcategoryName = itemView.findViewById(R.id.subcategory_name);
            subcategoryImage = itemView.findViewById(R.id.subcategory_image);
        }
    }
}
