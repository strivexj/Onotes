package com.example.onotes.ui.onboarding;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.login.ForgetPasswordActivity;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.utils.ActivityCollector;

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

        final SharedPreferences sp = App.getContext().getSharedPreferences("app", MODE_PRIVATE);
        if (sp.getBoolean("is_first_lanuch", true)) {
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
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putBoolean("is_first_lanuch", false);
                    ed.apply();
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
