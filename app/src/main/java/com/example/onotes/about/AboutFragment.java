package com.example.onotes.about;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import com.example.onotes.R;

public class AboutFragment extends PreferenceFragmentCompat {

    private Toolbar toolbar;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.about_fragment);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        findPreference("rate").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
                rate();
                return false;
            }
        });


        findPreference("addqqgroup").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
                addqqgroup();
                return false;
            }
        });


        findPreference("follow_me_on_github").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
               followOnGithub();
                return false;
            }
        });

        findPreference("follow_me_on_zhihu").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
               followOnZhihu();
                return false;
            }
        });

        findPreference("feedback").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
               feedback();
                return false;
            }
        });



    }


    public void showRateError() {
        Snackbar.make(toolbar, R.string.no_app_store_found, Snackbar.LENGTH_SHORT).show();
    }


    public void showFeedbackError() {
        Snackbar.make(toolbar, R.string.no_mail_app, Snackbar.LENGTH_SHORT).show();
    }

    public void showBrowserNotFoundError() {
        Snackbar.make(toolbar, R.string.no_browser_found, Snackbar.LENGTH_SHORT).show();
    }

    public void rate() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex){
           showRateError();
        }
    }

    /****************
     * 发起添加群流程。群号：pdf(311357701) 的 key 为： _pTMqAXJrpUUk0t86WRYonnbx-axNgWb
     * 调用 joinQQGroup(_pTMqAXJrpUUk0t86WRYonnbx-axNgWb) 即可发起手Q客户端申请加群 pdf(311357701)
     *
     * @param
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean addqqgroup() {
        String key = "_pTMqAXJrpUUk0t86WRYonnbx-axNgWb";
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivityForResult(intent, 0);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void followOnGithub() {
            try{
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( getActivity().getString(R.string.github_url))));
            } catch (android.content.ActivityNotFoundException ex){
               showBrowserNotFoundError();
            }

    }

    public void followOnZhihu() {
            try{
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( getActivity().getString(R.string.zhihu_url))));
            } catch (android.content.ActivityNotFoundException ex){
                showBrowserNotFoundError();
            }
    }

    public void feedback() {
        try{
            Uri uri = Uri.parse(getActivity().getString(R.string.sendto));
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.mail_topic));
            intent.putExtra(Intent.EXTRA_TEXT,
                    getActivity().getString(R.string.device_model) + Build.MODEL + "\n"
                            + getActivity().getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                            + getActivity().getString(R.string.version));
            getActivity().startActivity(intent);
        }catch (android.content.ActivityNotFoundException ex){
            showFeedbackError();
        }
    }

}
