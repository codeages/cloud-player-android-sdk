package com.edusoho.playerdemo.listener;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.cloud.core.entity.ResourceDefinition;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.util.Utils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DefinitionListAdapter extends RecyclerView.Adapter<DefinitionListAdapter.ViewHolder> {

    private List<ResourceDefinition> mResourceDefinitions;
    private Context                  mContext;
    private OnItemClickListener      mOnItemClickListener;

    public DefinitionListAdapter(Context context, List<ResourceDefinition> definitions) {
        this.mContext = context;
        this.mResourceDefinitions = definitions;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setTitle(Utils.getDefinitionStringName(mContext, mResourceDefinitions.get(position).name()));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder.getAdapterPosition()));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(createTitleView());
    }

    private TextView createTitleView() {
        TextView textView = new TextView(mContext);
        textView.setTextColor(mContext.getResources().getColor(R.color.textIcons));
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.caption);
        textView.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.definition_btn_w));
        textView.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.definition_btn_h));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, padding);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.white_shape_rectangle_bg);
        return textView;
    }

    @Override
    public int getItemCount() {
        return mResourceDefinitions.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mTitleView = (TextView) view;
        }

        public void setTitle(String title) {
            mTitleView.setText(title);
        }
    }
}
