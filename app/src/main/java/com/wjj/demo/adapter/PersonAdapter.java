package com.wjj.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Person;
import com.wjj.demo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jiajiewang on 16/7/4.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MainViewHolder> {
    private Context context;
    private List<Person> list;

    public PersonAdapter(Context context, List<Person> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.Address.setText("地址 " + list.get(position).getAddress());
        holder.id.setText("id " + list.get(position).getId().toString());
        holder.Age.setText("年纪 " + list.get(position).getAge());
        holder.Name.setText("姓名 " + list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id)
        TextView id;
        @BindView(R.id.Name)
        TextView Name;
        @BindView(R.id.Age)
        TextView Age;
        @BindView(R.id.Address)
        TextView Address;


        public MainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
