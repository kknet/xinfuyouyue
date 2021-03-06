package com.xfyyim.cn.fragmentnew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xfyyim.cn.R;
import com.xfyyim.cn.bean.UserVIPPrivilege;
import com.xfyyim.cn.bean.UserVIPPrivilegePrice;
import com.xfyyim.cn.bean.event.EventPaySuccess;
import com.xfyyim.cn.helper.AvatarHelper;
import com.xfyyim.cn.ui.base.EasyFragment;
import com.xfyyim.cn.ui.me.MyNewWalletActivity;
import com.xfyyim.cn.ui.me.payCompleteActivity;
import com.xfyyim.cn.ui.me.redpacket.alipay.AlipayHelper;
import com.xfyyim.cn.util.ArithUtils;
import com.xfyyim.cn.util.DateUtils;
import com.xfyyim.cn.util.EventBusHelper;
import com.xfyyim.cn.util.ToastUtil;
import com.xfyyim.cn.view.CenterPairingLoveDialog;
import com.xfyyim.cn.view.MyVipPaymentPopupWindow;
import com.xfyyim.cn.view.cjt2325.cameralibrary.util.LogUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;

import static com.xfyyim.cn.MyApplication.getContext;


public class MyPrerogativeFragment extends EasyFragment {

    @BindView(R.id.ivUserHead)
    ImageView ivUserHead;
    @BindView(R.id.tvDataTime)
    TextView tvDataTime;
    @BindView(R.id.tvBuyVipPerogative)
    TextView tvBuyVipPerogative;
    private PopupWindow popupWindow;
    private View contentView;
    private List<View> viewList = new ArrayList<>();
    private ViewPager mViewPager;
    private UserVIPPrivilege privilegeBean = new UserVIPPrivilege();
    private UserVIPPrivilegePrice vipPrivilegePriceList = new UserVIPPrivilegePrice();

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_my_prerogative;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initView();
    }

    private void initView() {

        AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), ivUserHead, true);

        findViewById(R.id.tvBuyVipPerogative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserPrivilegeConfigInfo();
            }
        });
        findViewById(R.id.llMyMeber).setOnClickListener(this::onClick);
        findViewById(R.id.rlFiveLike).setOnClickListener(this::onClick);
        findViewById(R.id.llRegret).setOnClickListener(this::onClick);
        findViewById(R.id.llChangePosition).setOnClickListener(this::onClick);
        findViewById(R.id.llLikeTimes).setOnClickListener(this::onClick);
        getUserPrivilegeInfo();
    }


    private void getUserPrivilegeConfigInfo() {
        LogUtil.e("into  getUserPrivilegeConfigInfo");
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("userId", coreManager.getSelf().getUserId());
        HttpUtils.post().url(coreManager.getConfig().USER_PRIVILEGE_CONFIG_INFO)
                .params(params)
                .build()
                .execute(new BaseCallback<UserVIPPrivilegePrice>(UserVIPPrivilegePrice.class) {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(ObjectResult<UserVIPPrivilegePrice> result) {
                        //
                        if (Result.checkSuccess(getActivity(), result)) {
                            vipPrivilegePriceList = result.getData();
                            LogUtil.e("vipPrivilegePriceList = " +vipPrivilegePriceList.toString());
                            if ( vipPrivilegePriceList!=null){
                                BuyMember();
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }

    /**
     * 获取用户特权信息
     */
    private void getUserPrivilegeInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("userId", coreManager.getSelf().getUserId());
        HttpUtils.post().url(coreManager.getConfig().USER_PRIVILEGE_INFO)
                .params(params)
                .build()
                .execute(new BaseCallback<UserVIPPrivilege>(UserVIPPrivilege.class) {
                    @Override
                    public void onResponse(ObjectResult<UserVIPPrivilege> result) {
                        if (Result.checkSuccess(getActivity(), result)) {
                            privilegeBean = result.getData();
                            LogUtil.e("UserVIPPrivilege = " +privilegeBean.toString());
                            if (privilegeBean.getVipLevel().equals("v0") || privilegeBean.getVipLevel().equals("v1") || privilegeBean.getVipLevel().equals("v2") || privilegeBean.getVipLevel().equals("v3")) {
                                tvDataTime.setText("VIP会员(" + DateUtils.newTimedate(privilegeBean.getEndTime()) + "到期）");
                                tvBuyVipPerogative.setText("续费VIP会员");
                            //    AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), ivUserHead, true);
                            } else {
                                tvBuyVipPerogative.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
                                tvDataTime.setText("暂未激活会员");
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMyMeber:
                openPopWindow(v, 0);
                break;
            case R.id.rlFiveLike:
                openPopWindow(v, 1);
                break;
            case R.id.llRegret:
                openPopWindow(v, 2);
                break;
            case R.id.llChangePosition:
                openPopWindow(v, 3);
                break;
            case R.id.llLikeTimes:
                openPopWindow(v, 4);
                break;
        }
    }

    //购买会员
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void BuyMember() {
        //显示VIP购买会员
        MyVipPaymentPopupWindow myVipPaymentPopupWindow = new MyVipPaymentPopupWindow(getActivity(), vipPrivilegePriceList, coreManager.getSelf().getUserId(), coreManager.getSelf().getNickName(),privilegeBean.getVipLevel());

        //谁喜欢我，在线聊天  购买
        //   MyPrivilegePopupWindow  my=new MyPrivilegePopupWindow(getActivity());
        LogUtil.e("BuyMember  BuyMember");
        myVipPaymentPopupWindow.setBtnOnClice(new MyVipPaymentPopupWindow.BtnOnClick() {
            @Override
            public void btnOnClick(String type, int vip) {
                payType(type,vip);
                LogUtil.e("*********************************type = " + type + "-------------vip = " + vip);
            }
        });
    }

    /**
     * 转支付类型返回支付签名数据
     * @param type
     * @param vip
     */
    private void  payType(String type, int vip){
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("payType", type);

        if(vip==1){
            params.put("price",vipPrivilegePriceList.getV0Price()+"");
            params.put("level", vipPrivilegePriceList.getV0());
        }else if(vip==2){
            params.put("price",vipPrivilegePriceList.getV1Price()+"");
            params.put("level", vipPrivilegePriceList.getV1());
        }else if(vip==3){
            params.put("price", vipPrivilegePriceList.getV2Price()+"");
            params.put("level", vipPrivilegePriceList.getV2());
        }else if(vip==4){
            params.put("price", vipPrivilegePriceList.getV3Price()+"");
            params.put("level", vipPrivilegePriceList.getV3());
        }
        params.put("funType", String.valueOf(5));
        params.put("num", String.valueOf(-1));
        params.put("mon", String.valueOf(-1));
        AlipayHelper.rechargePay(getActivity(), coreManager,params);

    }


    public void setOnClick() {
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    public void openPopWindow(View v, int position) {
        //加载弹出框的布局
        contentView = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_myprivilege_layout, null);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //	        this.setWidth(ViewPiexlUtil.dp2px(context,200));
        //设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        mViewPager = (ViewPager) contentView.findViewById(R.id.vp_Privilege);
        //VIP会员权益
        View children_prerogative = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_children_prerogative, null);
        children_prerogative.findViewById(R.id.tvPayVip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(1);
            }
        });

        //每天5个超级喜欢
        View children_prerogative_like = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_children_prerogative_like, null);
        children_prerogative_like.findViewById(R.id.tvGetVip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(1);
            }
        });
        //滑错无限反悔
        View children_online_chat = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_children_slip_error, null);
        TextView  tvConent=children_online_chat.findViewById(R.id.tvConent);
        tvConent.setVisibility(View.VISIBLE);
        tvConent.setText("经常手快滑错了?");

        children_online_chat.findViewById(R.id.tvPaySlip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(3);
            }
        });

        //任意定位
        View children_prerogative_location = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_children_prerogative_location, null);
        children_prerogative_location.findViewById(R.id.tvPayLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(4);
            }
        });
        AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), children_prerogative_location.findViewById(R.id.avatar_img), true);

        //喜欢次数
        View children_prerogative_times = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_children_prerogative_times, null);

        children_prerogative_times.findViewById(R.id.tvPayLikes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(5);
            }
        });


        viewList.add(children_prerogative);
        viewList.add(children_prerogative_like);
        viewList.add(children_online_chat);
        viewList.add(children_prerogative_location);
        viewList.add(children_prerogative_times);


        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getContext());
        viewPageAdapter.setData(viewList);
        mViewPager.setAdapter(viewPageAdapter);
        mViewPager.setCurrentItem(position);
        mViewPager.setOffscreenPageLimit(viewList.size() - 1);

        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        //设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_style_bg));
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        setOnClick();

        //从底部显示
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        int width = (int) getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
      int height = (((int) getContext().getResources().getDisplayMetrics().heightPixels / 10)*7)+50; // 高度
        popupWindow.setWidth(width);
       popupWindow.setHeight(height);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        TextView  tvPayLikes=(TextView)children_prerogative_times.findViewById(R.id.tvPayLikes);
        TextView  tvPayLocation=(TextView)children_prerogative_location.findViewById(R.id.tvPayLocation);
        TextView  tvPaySlip=(TextView) children_online_chat.findViewById(R.id.tvPaySlip);
        TextView  tvGetVip=(TextView) children_prerogative_like.findViewById(R.id.tvGetVip);
        TextView  tvPayVip=(TextView)children_prerogative.findViewById(R.id.tvPayVip);


        //买会员 或者续费
        if (privilegeBean.getVipLevel().equals("v0") || privilegeBean.getVipLevel().equals("v1") || privilegeBean.getVipLevel().equals("v2") || privilegeBean.getVipLevel().equals("v3")) {
            tvPayLikes.setText("续费VIP会员");
            tvPayLocation.setText("续费VIP会员");
            tvPaySlip.setText("续费VIP会员");
            tvGetVip.setText("续费VIP会员");
            tvPayVip.setText("续费VIP会员");
        } else {
            tvPayLikes.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
            tvPayLocation.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
            tvPaySlip.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
            tvGetVip.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
            tvPayVip.setText("￥" + ArithUtils.round1(privilegeBean.getVipPrice()) + "获取VIP会员");
        }
    }

    /**
     * 判断选择的类型
     *
     * @param type
     */
    private void switchType(int type) {
        popupWindow.dismiss();
        if (type == 2) {
            //弹框显示是配对喜欢
         //   showBottomDialog();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               getUserPrivilegeConfigInfo();
            }
        }
    }

    private void showBottomDialog() {
        CenterPairingLoveDialog bottomAnimDialog = new CenterPairingLoveDialog(getActivity(), R.style.CustomDialog);
        bottomAnimDialog.show();
        bottomAnimDialog.setBtnOnClice(new CenterPairingLoveDialog.BtnOnClick() {
            @Override
            public void btnOnClick(String type) {
                LogUtil.e("------------type =" + type);
            }
        });
    }

    public class ViewPageAdapter extends PagerAdapter {
        List<View> viewsList;
        private LayoutInflater inflater;
        Context context;

        public ViewPageAdapter(Context context) {
            this.context = context;
            viewsList = new ArrayList<>();
            inflater = LayoutInflater.from(context);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return viewsList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = viewsList.get(position);
            viewGroup.addView(view);
            return view;
        }

        public void setData(List<View> list) {
            this.viewsList = list;
            if (list == null || list.size() == 0) {
                return;
            }
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
