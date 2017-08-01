package com.example.onotes.ui.onboarding;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.onotes.R;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.service.CityDownloadSerivce;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;

/**
 * 第一次安装APP时启动的引导页。。
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private AppCompatButton buttonFinish;
    private ImageButton buttonPre;
    private ImageButton buttonNext;
    private ImageView[] indicators;

    private int bgColors[];
    private int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查城市是否全部加载成功。否则开启城市下载服务
        if (!SharedPreferenesUtil.isCityadd()) {
            LogUtil.d("shared","cityadd ==false");
            Intent intentService = new Intent(this, CityDownloadSerivce.class);
            startService(intentService);
        }

        //检查APP是否第一次启动。如果是就进入引导页
        if (SharedPreferenesUtil.getIs_first_lanuch()) {

            //使状态栏透明
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            setContentView(R.layout.activity_on_boarding);

            initViews();

            initData();

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int colorUpdate = (Integer) new ArgbEvaluator().evaluate(positionOffset, bgColors[position], bgColors[position == 2 ? position : position + 1]);
                    viewPager.setBackgroundColor(colorUpdate);
                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    updateIndicators(position);
                    viewPager.setBackgroundColor(bgColors[position]);
                    buttonPre.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                    buttonNext.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                    buttonFinish.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenesUtil.setIs_first_lanuch(false);
                    navigateToMainActivity();
                }
            });

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition += 1;
                    viewPager.setCurrentItem(currentPosition, true);
                }
            });

            buttonPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition -= 1;
                    viewPager.setCurrentItem(currentPosition, true);
                }
            });

        } else {

            navigateToMainActivity();

            finish();
        }

    }

    private void initViews() {
        OnboardingPagerAdapter pagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        buttonFinish = (AppCompatButton) findViewById(R.id.buttonFinish);
        buttonNext = (ImageButton) findViewById(R.id.imageButtonNext);
        buttonPre = (ImageButton) findViewById(R.id.imageButtonPre);
        indicators = new ImageView[] {(ImageView) findViewById(R.id.imageViewIndicator0),
                (ImageView) findViewById(R.id.imageViewIndicator1),
                (ImageView) findViewById(R.id.imageViewIndicator2)};
    }

    private void initData() {
        bgColors = new int[]{ContextCompat.getColor(this, R.color.viewpager1),
                ContextCompat.getColor(this, R.color.viewpager2),
                ContextCompat.getColor(this, R.color.viewpager3)};
    }


    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.onboarding_indicator_selected : R.drawable.onboarding_indicator_unselected
            );
        }
    }


    private void navigateToMainActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
