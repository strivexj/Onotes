package com.example.onotes.ui.onboarding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.onotes.R;


public class OnboardingFragment extends Fragment {

    private AppCompatTextView sectionLabel;
    private AppCompatTextView sectionIntro;
    private ImageView sectionImg;

    private int page;

    // The fragment argument representing
    // the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    public OnboardingFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OnboardingFragment newInstance(int sectionNumber) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        initViews(view);

        switch (page) {
            case 0:
                sectionImg.setBackgroundResource(R.drawable.icon);
                sectionLabel.setText(R.string.onboarding_section_1);
                sectionIntro.setText(R.string.onboarding_intro_1);
                break;
            case 1:
                sectionImg.setBackgroundResource(R.drawable.back);
                sectionLabel.setText(R.string.onboarding_section_2);
                sectionIntro.setText(R.string.onboarding_intro_2);
                break;
            case 2:
                sectionImg.setBackgroundResource(R.drawable.robot);
                sectionLabel.setText(R.string.onboarding_section_3);
                sectionIntro.setText(R.string.onboarding_intro_3);
                break;
            default:
                break;
        }

        return view;
    }

    private void initViews(View view) {
        sectionLabel = (AppCompatTextView) view.findViewById(R.id.section_label);
        sectionIntro = (AppCompatTextView) view.findViewById(R.id.section_intro);
        sectionImg = (ImageView) view.findViewById(R.id.section_img);
    }

}
