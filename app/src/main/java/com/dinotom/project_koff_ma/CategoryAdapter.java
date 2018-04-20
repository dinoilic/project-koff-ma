package com.dinotom.project_koff_ma;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;

import org.w3c.dom.Text;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>  {

    private Context mCtx;
    private List<Result> categoryList;

    public CategoryAdapter(Context mCtx, List<Result> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.category_card, null);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Result category = categoryList.get(position);

        holder.textViewTitle.setText(category.getName());
        holder.imageViewCategory.setImageResource(R.drawable.car);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageViewCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            imageViewCategory = itemView.findViewById(R.id.category_image);
            textViewTitle = itemView.findViewById(R.id.category_name);
        }
    }

}
