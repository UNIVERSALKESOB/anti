package com.allinone.calc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutScreen implements Screen {

    private static final String PUBLISHER = "Universal Studio";
    private static final String GITHUB = "https://github.com/UNIVERSALKESOB/AllInOneCalculator";
    private static final String PRIVACY = "https://universalkesob.github.io/AllInOneCalculator/privacy-policy.html";

    private final Context ctx;
    private final View root;

    public AboutScreen(Context c) {
        this.ctx = c;

        LinearLayout col = Ui.column(c, 0);
        int p = Ui.dp(c, 14);
        col.setPadding(p, Ui.dp(c, 8), p, p);

        // identity
        LinearLayout id = Ui.column(c, 0);
        id.setBackground(Ui.roundBg(Ui.SURFACE, Ui.dp(c, 20), Ui.dp(c, 1), Ui.STROKE));
        int ip = Ui.dp(c, 22);
        id.setPadding(ip, ip, ip, ip);
        id.setGravity(Gravity.CENTER);

        ImageView logo = new ImageView(c);
        logo.setImageResource(R.mipmap.ic_launcher);
        id.addView(logo, new LinearLayout.LayoutParams(Ui.dp(c, 88), Ui.dp(c, 88)));

        TextView name = Ui.text(c, "All-in-One Calculator", 20, Ui.TEXT, true);
        name.setGravity(Gravity.CENTER);
        name.setPadding(0, Ui.dp(c, 12), 0, 0);
        id.addView(name, new LinearLayout.LayoutParams(-1, -2));

        TextView version = Ui.text(c, "Version " + versionName(c) + " · 10 tools + settings", 13, Ui.MUTED, false);
        version.setGravity(Gravity.CENTER);
        version.setPadding(0, Ui.dp(c, 4), 0, 0);
        id.addView(version, new LinearLayout.LayoutParams(-1, -2));

        TextView by = Ui.text(c, "Published by " + PUBLISHER, 15, Ui.ACCENT, true);
        by.setGravity(Gravity.CENTER);
        by.setPadding(0, Ui.dp(c, 12), 0, 0);
        id.addView(by, new LinearLayout.LayoutParams(-1, -2));

        col.addView(id, new LinearLayout.LayoutParams(-1, -2));

        // description
        LinearLayout about = Ui.card(c, "About this app");
        TextView body = Ui.text(c,
                "A pocket calculator that does more than arithmetic. Basic and scientific math, "
                        + "unit and currency conversion, BMI, loans, age, percentages, tips and "
                        + "discounts — all in one small app that keeps working without internet.",
                14, Ui.TEXT, false);
        body.setLineSpacing(Ui.dp(c, 4), 1f);
        about.addView(body);
        col.addView(about, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // features
        LinearLayout features = Ui.card(c, "Highlights");
        String[] items = {
                "Basic calculator with history and smart percentages",
                "Scientific mode with trigonometry, logs and DEG/RAD",
                "Unit converter with 60+ units across 8 categories",
                "Currency converter with 28 currencies and offline rates",
                "BMI, loan/EMI, age, percentage, tip and discount tools",
                "No ads, no tracking, no account needed"
        };
        for (String item : items) {
            LinearLayout row = Ui.row(c);
            row.setPadding(0, Ui.dp(c, 4), 0, Ui.dp(c, 4));
            TextView dot = Ui.text(c, "•", 16, Ui.ACCENT, true);
            row.addView(dot);
            TextView text = Ui.text(c, item, 13, Ui.MUTED, false);
            text.setPadding(Ui.dp(c, 8), 0, 0, 0);
            text.setLineSpacing(Ui.dp(c, 2), 1f);
            row.addView(text, new LinearLayout.LayoutParams(0, -2, 1f));
            features.addView(row);
        }
        col.addView(features, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // links
        LinearLayout links = Ui.card(c, "Links");
        links.addView(linkRow(c, "Source code on GitHub", "github.com/UNIVERSALKESOB", GITHUB));
        links.addView(Ui.divider(c));
        links.addView(linkRow(c, "Privacy policy", "No data collected", PRIVACY));
        col.addView(links, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // legal
        TextView legal = Ui.text(c, "© 2026 " + PUBLISHER + ". All rights reserved.", 11, Ui.MUTED, false);
        legal.setGravity(Gravity.CENTER);
        legal.setPadding(0, Ui.dp(c, 18), 0, Ui.dp(c, 8));
        col.addView(legal, new LinearLayout.LayoutParams(-1, -2));

        root = Ui.scroll(c, col);
    }

    @Override
    public View view() {
        return root;
    }

    private View linkRow(Context c, String title, String subtitle, final String url) {
        LinearLayout row = Ui.column(c, 0);
        row.setPadding(Ui.dp(c, 4), Ui.dp(c, 10), Ui.dp(c, 4), Ui.dp(c, 10));
        row.setClickable(true);
        row.setBackground(Ui.ripple(Ui.SURFACE, Ui.dp(c, 10)));
        row.addView(Ui.text(c, title, 14, Ui.TEXT, false));
        TextView sub = Ui.text(c, subtitle, 12, Ui.ACCENT, false);
        sub.setPadding(0, Ui.dp(c, 2), 0, 0);
        row.addView(sub);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(url);
            }
        });
        return row;
    }

    private void open(String url) {
        try {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {
        }
    }

    private static String versionName(Context c) {
        try {
            PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return info.versionName == null ? "1.0" : info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
}
