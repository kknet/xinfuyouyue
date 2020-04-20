package com.xfyyim.cn.ui.me;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xfyyim.cn.R;
import com.xfyyim.cn.bean.BillCashBean;
import com.xfyyim.cn.bean.LikeMeBean;
import com.xfyyim.cn.bean.UserVIPPrivilege;
import com.xfyyim.cn.helper.AvatarHelper;
import com.xfyyim.cn.ui.base.BaseActivity;
import com.xfyyim.cn.util.ArithUtils;
import com.xfyyim.cn.util.DateUtils;
import com.xfyyim.cn.util.glideUtil.GlideImageUtils;
import com.xfyyim.cn.view.MergerStatus;
import com.xfyyim.cn.view.SkinImageView;
import com.xfyyim.cn.view.SkinTextView;
import com.xfyyim.cn.view.SuperSolarizePopupWindow;
import com.xfyyim.cn.view.cjt2325.cameralibrary.util.LogUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class

CheckLikesMeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_title_left)
    SkinImageView ivTitleLeft;
    @BindView(R.id.tv_title_left)
    SkinTextView tvTitleLeft;
    @BindView(R.id.pb_title_center)
    ProgressBar pbTitleCenter;
    @BindView(R.id.tv_title_center)
    SkinTextView tvTitleCenter;
    @BindView(R.id.iv_title_center)
    ImageView ivTitleCenter;
    @BindView(R.id.iv_title_right)
    SkinImageView ivTitleRight;
    @BindView(R.id.iv_title_right_right)
    SkinImageView ivTitleRightRight;
    @BindView(R.id.tv_title_right)
    SkinTextView tvTitleRight;
    @BindView(R.id.mergerStatus)
    MergerStatus mergerStatus;
    private SmartRefreshLayout mRefreshLayout;
    private CheckLikesMeAdapter checkLikesMeAdapter;
    private Unbinder unbinder;
    private List<LikeMeBean>   likeMeBeanList=new ArrayList<LikeMeBean>();
    private boolean more=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_likes_me);
        unbinder=ButterKnife.bind(this);
        initView();
        initActionBar();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        ivTitleLeft.setVisibility(View.VISIBLE);
        ivTitleLeft.setOnClickListener(this);
        tvTitleCenter.setText("谁喜欢我");
       /* ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setImageDrawable(getResources().getDrawable(R.drawable.me_edit_pen));*/

    }
    private void initView() {

        mRefreshLayout = findViewById(R.id.refreshLayout);


        RecyclerView rclBill = (RecyclerView) findViewById(R.id.rclCheck);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rclBill.setLayoutManager(linearLayoutManager);
        checkLikesMeAdapter = new CheckLikesMeAdapter(this);
         checkLikesMeAdapter.likeMeBeanList = likeMeBeanList;
        rclBill.setAdapter(checkLikesMeAdapter);
        checkLikesMeAdapter.notifyDataSetChanged();

        findViewById(R.id.ivSolarize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //爆光
                swithSuperSolarize(1);
            }
        });

      /*  mRefreshLayout.setOnRefreshListener(rl -> {
           //刷新
            LogUtil.e("*******************刷新1******************");

        });*/


        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                more=true;

                pageIndex++;
                getWhoLikeMe();

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                more=false;
                pageIndex++;
                getWhoLikeMe();

            }
        });

        getWhoLikeMe();
    }

    /**
     * 打开超级爆光页面
     */
    private void OpenSolarize() {


    }
    private int pageIndex=0;
    private  void  getWhoLikeMe(){
     Map<String, String> params = new HashMap<>();
      params.put("access_token", coreManager.getSelfStatus().accessToken);
      params.put("userId", coreManager.getSelf().getUserId());
      params.put("pageIndex", pageIndex+"");
      params.put("pageSize", 10+"");

            HttpUtils.post().url(coreManager.getConfig().USER_WHO_LIKE_ME)
                    .params(params)
                    .build()
                    .execute(new ListCallback<LikeMeBean>(LikeMeBean.class) {
                        @Override
                        public void onResponse(ArrayResult<LikeMeBean> result) {
                            if (Result.checkSuccess(getApplicationContext(), result)) {
                               ;
                                LogUtil.e("likeMeBeanList = " +likeMeBeanList.size());
                                likeMeBeanList.addAll(result.getData());
                                checkLikesMeAdapter.likeMeBeanList.addAll(result.getData());
                                tvTitleCenter.setText("谁喜欢我人("+likeMeBeanList.size()+")");
                                checkLikesMeAdapter.notifyDataSetChanged();
                                if(more){
                                    mRefreshLayout.finishLoadMore();
                                }else {
                                    mRefreshLayout.finishRefresh();
                                }
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }
                    });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public class CheckLikesMeAdapter extends RecyclerView.Adapter<CheckLikesMeAdapter.ViewHolder> {
        private LayoutInflater mInflater;
        private Context context;
        private List<LikeMeBean>   likeMeBeanList=new ArrayList<LikeMeBean>();

        public CheckLikesMeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            ;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivlike;
            TextView tvLikeName;

            public ViewHolder(View view) {
                super(view);
                ivlike = (ImageView) view.findViewById(R.id.ivlike);
                tvLikeName = (TextView) view.findViewById(R.id.tvLikeName);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_check_likes_adapter, null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (likeMeBeanList != null && likeMeBeanList.size() > 0) {
                final LikeMeBean likeMeBean = likeMeBeanList.get(position);
                holder.tvLikeName.setText("" + likeMeBean.getNickname() + "");

                String url = AvatarHelper.getAvatarUrl(likeMeBean.getUserId()+","+likeMeBean.getAge(), false);

             //   GlideImageUtils.setImageView(context, url, holder.ivlike);
                GlideImageUtils.setImageDrawableCirCle(mContext, url,  holder.ivlike);


            }
        }

        @Override
        public int getItemCount() {
            return likeMeBeanList.size();
        }
    }
    private void swithSuperSolarize(int switchType){
        SuperSolarizePopupWindow RegretsUnLikePopupWindow=new SuperSolarizePopupWindow(this,switchType);
        RegretsUnLikePopupWindow.setBtnOnClice(new SuperSolarizePopupWindow.BtnOnClick() {
            @Override
            public void btnOnClick(int sumbitType) {


            }
        });
    }
}
