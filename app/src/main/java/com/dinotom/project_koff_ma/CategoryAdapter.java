package com.dinotom.project_koff_ma;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static com.dinotom.project_koff_ma.MainActivity.MAIN_CATEGORY_CHILDREN;
import static com.dinotom.project_koff_ma.MainActivity.MAIN_CATEGORY_NAME;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
{


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
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        final Result category = categoryList.get(position);

        holder.textViewTitle.setText(category.getName());
        Picasso.get().load(category.getImage()).into(holder.imageViewCategory);

        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mCtx, SubcategoryActivity.class);
                intent.putExtra(MAIN_CATEGORY_NAME, category.getName());
                intent.putExtra(MAIN_CATEGORY_CHILDREN, category.getChildren());
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView textViewTitle;
        ImageView imageViewCategory;

        public CategoryViewHolder(final View itemView) {
            super(itemView);
            view = itemView;

            imageViewCategory = itemView.findViewById(R.id.category_image);
            textViewTitle = itemView.findViewById(R.id.category_name);
        }
    }

}
