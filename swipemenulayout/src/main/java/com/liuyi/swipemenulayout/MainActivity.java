package com.liuyi.swipemenulayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liuyi.swipemenulayout.view.SwipeBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<SwipeBean> swipeBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            swipeBeans.add(new SwipeBean("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试"));
        }
        final RVAdapter adapter = new RVAdapter(this, swipeBeans);
        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = v.getId();
                if (id == R.id.text) {
                    Toast.makeText(MainActivity.this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                } else if (id == R.id.tv1) {/*删除*/
                    Toast.makeText(MainActivity.this, ((TextView) v).getText().toString() + position, Toast.LENGTH_SHORT).show();
                    adapter.remove(position);
                    //                    SwipeBean item = adapter.getItem(position);
//                    item.text = "aaa";
//                    adapter.notifyItemChanged(position);
                } else if (id == R.id.tv2) {
                    Toast.makeText(MainActivity.this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View v, int position) {
                Toast.makeText(MainActivity.this, position + "position", Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);


    }

    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {
        private Context context;
        private List<SwipeBean> mDatas;

        RVAdapter(Context context, List<SwipeBean> datas) {
            this.context = context;
            this.mDatas = datas;
        }

        public void remove(int pos) {
            ensureDatas();
            mDatas.remove(pos);
            notifyItemRemoved(pos);
        }

        private void ensureDatas() {
            if (mDatas == null) {
                mDatas = new ArrayList<>();
            }
        }

        public SwipeBean getItem(int pos) {
            ensureDatas();
            return mDatas.get(pos);
        }

        public interface OnItemClickListener {
            void onItemClick(View v, int position);

            void onItemLongClick(View v, int position);
        }

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }


        @NonNull
        @Override
        public RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RVViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RVViewHolder holder, int position) {
            holder.text.setText(mDatas.get(position).text + position);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public class RVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            private TextView text;
            private TextView tv1;
            private TextView tv2;

            public RVViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
                tv1 = (TextView) itemView.findViewById(R.id.tv1);
                tv2 = (TextView) itemView.findViewById(R.id.tv2);
                text.setOnLongClickListener(this);
                text.setOnClickListener(this);
                tv1.setOnClickListener(this);
                tv2.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }

            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        }
    }

}
