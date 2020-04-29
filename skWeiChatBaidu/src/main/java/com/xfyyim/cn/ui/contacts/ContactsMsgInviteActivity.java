package com.xfyyim.cn.ui.contacts;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.xfyyim.cn.MyApplication;
import com.xfyyim.cn.R;
import com.xfyyim.cn.Reporter;
import com.xfyyim.cn.bean.Contact;
import com.xfyyim.cn.bean.Contacts;
import com.xfyyim.cn.db.dao.ContactDao;
import com.xfyyim.cn.helper.AvatarHelper;
import com.xfyyim.cn.helper.DialogHelper;
import com.xfyyim.cn.sortlist.BaseComparator;
import com.xfyyim.cn.sortlist.BaseSortModel;
import com.xfyyim.cn.sortlist.SideBar;
import com.xfyyim.cn.sortlist.SortHelper;
import com.xfyyim.cn.ui.base.BaseActivity;
import com.xfyyim.cn.util.AsyncUtils;
import com.xfyyim.cn.util.Constants;
import com.xfyyim.cn.util.ContactsUtil;
import com.xfyyim.cn.util.PermissionUtil;
import com.xfyyim.cn.util.PreferenceUtils;
import com.xfyyim.cn.util.ToastUtil;
import com.xfyyim.cn.util.ViewHolder;
import com.xfyyim.cn.view.PullToRefreshSlideListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ContactsMsgInviteActivity extends BaseActivity {
    private SideBar mSideBar;
    private TextView mTextDialog;
    private PullToRefreshSlideListView mListView;
    private ContactsAdapter mContactsAdapter;
    private List<Contacts> mContactList;
    private List<BaseSortModel<Contacts>> mSortContactList;
    private BaseComparator<Contacts> mBaseComparator;
    private String mLoginUserId;
    private Map<String, Contacts> mBatchAddContacts = new HashMap<>();

    private Map<String, Contacts> phoneContacts;

    private int mobilePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_msg_invite);

        mLoginUserId = coreManager.getSelf().getUserId();
        mContactList = new ArrayList<>();
        mSortContactList = new ArrayList<>();
        mBaseComparator = new BaseComparator<>();
        mContactsAdapter = new ContactsAdapter();

        mobilePrefix = PreferenceUtils.getInt(MyApplication.getContext(), Constants.AREA_CODE_KEY, 86);

        initActionBar();
        boolean isReadContacts = PermissionUtil.checkSelfPermissions(this, new String[]{Manifest.permission.READ_CONTACTS});
        if (!isReadContacts) {
            DialogHelper.tip(this, getString(R.string.please_open_address_per));
            return;
        }

        initView();
        dataLayering();
        initEvent();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.phone_contact));

    }

    public void initView() {
        mListView = (PullToRefreshSlideListView) findViewById(R.id.pull_refresh_list);
        mListView.getRefreshableView().setAdapter(mContactsAdapter);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);

        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mTextDialog = (TextView) findViewById(R.id.text_dialog);
        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mContactsAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.getRefreshableView().setSelection(position);
                }
            }
        });

    }

    private void dataLayering() {
        phoneContacts = ContactsUtil.getPhoneContacts(this);

        List<Contact> allContacts = ContactDao.getInstance().getAllContacts(mLoginUserId);
        // 现在数据库内在创建联系人的时候已经去重了，按理说这里已经不需要处理了，但是一些老用户联系人表内已经生成了一些重复的数据，所以这里在去下重
        Set<Contact> set = new TreeSet<>(new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getToUserId().compareTo(o2.getToUserId());
            }
        });
        set.addAll(allContacts);
        allContacts = new ArrayList<>(set);

        // 移除已注册IM的联系人，显示未注册IM的联系人
        for (int i = 0; i < allContacts.size(); i++) {
            phoneContacts.remove(allContacts.get(i).getToTelephone());
        }

        Collection<Contacts> values = phoneContacts.values();
        mContactList = new ArrayList<>(values);

        DialogHelper.showDefaulteMessageProgressDialog(this);
        try {
            AsyncUtils.doAsync(this, e -> {
                Reporter.post("加载数据失败，", e);
                AsyncUtils.runOnUiThread(this, ctx -> {
                    DialogHelper.dismissProgressDialog();
                    ToastUtil.showToast(ctx, R.string.data_exception);
                });
            }, c -> {
                Map<String, Integer> existMap = new HashMap<>();
                List<BaseSortModel<Contacts>> sortedList = SortHelper.toSortedModelList(mContactList, existMap, Contacts::getName);
                c.uiThread(r -> {
                    DialogHelper.dismissProgressDialog();
                    mSideBar.setExistMap(existMap);
                    mSortContactList = sortedList;
                    mContactsAdapter.setData(sortedList);
                });
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initEvent() {

        TextView shareAppTv = findViewById(R.id.share_app_tv);

        mListView.getRefreshableView().setOnItemClickListener((parent, view, position, id) -> {
            position = (int) id;
            Contacts contact = mSortContactList.get(position).getBean();
            String tel =contact.getTelephone().substring(String.valueOf(mobilePrefix).length());
            ToastUtil.showToast(this,contact.getTelephone()+"表白"+"     "+tel);
        });

    }



    class ContactsAdapter extends BaseAdapter implements SectionIndexer {
        List<BaseSortModel<Contacts>> mSortContactList;

        public ContactsAdapter() {
            mSortContactList = new ArrayList<>();
        }

        public void setData(List<BaseSortModel<Contacts>> sortContactList) {
            mSortContactList = sortContactList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSortContactList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSortContactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_contacts_msg_invite, parent, false);
            }
            TextView categoryTitleTv = ViewHolder.get(convertView, R.id.catagory_title);
            View view_bg_friend = ViewHolder.get(convertView, R.id.view_bg_friend);
            ImageView avatarImg = ViewHolder.get(convertView, R.id.avatar_img);
            TextView contactNameTv = ViewHolder.get(convertView, R.id.contact_name_tv);
            TextView userNameTv = ViewHolder.get(convertView, R.id.user_name_tv);

            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                categoryTitleTv.setVisibility(View.VISIBLE);
                categoryTitleTv.setText(mSortContactList.get(position).getFirstLetter());
                view_bg_friend.setVisibility(View.GONE);
            } else {
                categoryTitleTv.setVisibility(View.GONE);
                view_bg_friend.setVisibility(View.VISIBLE);
            }

            final Contacts contact = mSortContactList.get(position).getBean();
            if (contact != null) {
                AvatarHelper.getInstance().displayAddressAvatar(contact.getName(), avatarImg);
                contactNameTv.setText(contact.getName());
                // 因为存储的时候默认拼上了区号，这里将区号截掉显示
                String tel = contact.getTelephone().substring(String.valueOf(mobilePrefix).length());
                userNameTv.setText(tel);
            }

            return convertView;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = mSortContactList.get(i).getFirstLetter();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return mSortContactList.get(position).getFirstLetter().charAt(0);
        }
    }
}
