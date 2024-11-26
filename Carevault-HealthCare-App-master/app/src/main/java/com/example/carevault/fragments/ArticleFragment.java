package com.example.carevault.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carevault.Articles.Covid;
import com.example.carevault.Articles.Diet;
import com.example.carevault.Articles.RelatedArticles;
import com.example.carevault.Articles.Fitness;
import com.example.carevault.Articles.TrendArticle;
import com.example.carevault.R;

public class ArticleFragment extends Fragment {
    Button covid, diet, fitness;
    TextView trend, seeall;
    LinearLayout[] sampleLayouts; // Array để chứa các sample

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        // Khởi tạo các view
        trend = view.findViewById(R.id.trend);
        covid = view.findViewById(R.id.covid);
        seeall = view.findViewById(R.id.seeall);
        diet = view.findViewById(R.id.diet);
        fitness = view.findViewById(R.id.fitness);

        // Khởi tạo mảng sampleLayouts
        sampleLayouts = new LinearLayout[]{
                view.findViewById(R.id.sample1),
                
        };

        // Thiết lập các listener cho các nút chính
        trend.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), TrendArticle.class)));
        covid.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), Covid.class)));
        diet.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), Diet.class)));
        fitness.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), Fitness.class)));
        seeall.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), RelatedArticles.class)));

        // Gán URL cho từng sample
        String[] sampleUrls = new String[]{
                "https://www.thehindu.com/news/national/telangana/state-records-five-new-covid-19-cases/article67759822.ece",
                "https://example.com/article2",
                "https://example.com/article3",
                "https://example.com/article4",
                "https://example.com/article5",
                "https://example.com/article6",
                "https://example.com/article7",
                "https://example.com/article8"
        };

        // Gắn listener cho từng sample
        for (int i = 0; i < sampleLayouts.length; i++) {
            setSampleClickListener(sampleLayouts[i], sampleUrls[i]);
        }

        return view;
    }

    private void setSampleClickListener(LinearLayout sampleLayout, String url) {
        if (sampleLayout != null) { // Kiểm tra nếu sampleLayout tồn tại
            sampleLayout.setOnClickListener(view -> {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
            });
        }
    }
}
