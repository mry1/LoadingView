package com.liuyi.adimageview;

import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_ad;
    private AdViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_ad = findViewById(R.id.rv_ad);
        ArrayList<AdBean> datas = new ArrayList<>();
        Drawable dd = getResources().getDrawable(R.drawable.ad_image);
        for (int i = 0; i < 16; i++) {
            AdBean adBean = new AdBean("如何置顶公众号", "点击右侧置顶；点击右侧置顶；点击右侧置顶；点击右侧置顶；点击右侧置顶；", dd, false);
            datas.add(adBean);
        }
        datas.set(6, new AdBean(" ", " ", dd, true));
        mAdapter = new AdViewAdapter(this, datas);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        rv_ad.setLayoutManager(mLinearLayoutManager);
        rv_ad.setAdapter(mAdapter);
        rv_ad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int fPos = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (fPos >= 5) {
                    View view = mLinearLayoutManager.findViewByPosition(6);
                    if (view != null) {
                        AdImageView adImageView = view.findViewById(R.id.iv_ad);
                        adImageView.setDy(getDistance() - mLinearLayoutManager.findViewByPosition(6).getTop(), view.getHeight());
                    }
                }

            }
        });
    }

    /**
     * 已滑动的距离
     */
    private int getDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv_ad.getLayoutManager();
        View firstVisibItem = rv_ad.getChildAt(0);
        int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
        int itemHeight = firstVisibItem.getHeight();
        int firstItemBottom = layoutManager.getDecoratedBottom(firstVisibItem);
        return (firstItemPosition + 1) * itemHeight - firstItemBottom;
    }
}
