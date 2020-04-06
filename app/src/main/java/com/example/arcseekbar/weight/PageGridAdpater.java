package com.example.arcseekbar.weight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arcseekbar.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PageGridAdpater extends RecyclerView.Adapter<PageGridAdpater.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<String> datas = new ArrayList<>();

    public PageGridAdpater(Context mContext, List<String> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pagegrid_itemview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String str = datas.get(position);
        holder.textView.setText(str);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        Toast.makeText(mContext,"" + position ,Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
        }
    }
}
