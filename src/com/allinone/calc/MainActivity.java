package com.allinone.calc;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String[] TABS = {
            "Basic", "Scientific", "Units", "Currency", "BMI", "Loan", "Age", "Percent", "Tip", "Discount", "Settings", "About"
    };

    private final Screen[] cache = new Screen[TABS.length];
    private final TextView[] chips = new TextView[TABS.length];
    private FrameLayout host;
    private int current = 0;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            current = state.getInt("tab", 0);
        }

        LinearLayout root = Ui.column(this, 0);
        root.setBackgroundColor(Ui.BG);

        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int top;
                int bottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    Insets bars = insets.getInsets(
                            WindowInsets.Type.systemBars() | WindowInsets.Type.ime());
                    top = bars.top;
                    bottom = bars.bottom;
                } else {
                    top = insets.getSystemWindowInsetTop();
                    bottom = insets.getSystemWindowInsetBottom();
                }
                v.setPadding(0, top, 0, bottom);
                return insets;
            }
        });

        root.addView(buildHeader());
        root.addView(buildTabs());

        View line = new View(this);
        line.setBackgroundColor(Ui.STROKE);
        root.addView(line, new LinearLayout.LayoutParams(-1, Math.max(1, Ui.dp(this, 1))));

        host = new FrameLayout(this);
        root.addView(host, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
        select(Math.max(0, Math.min(TABS.length - 1, current)), false);
    }

    private View buildHeader() {
        LinearLayout box = Ui.column(this, 0);
        int l = Ui.dp(this, 18);
        box.setPadding(l, Ui.dp(this, 12), l, Ui.dp(this, 10));

        TextView title = Ui.text(this, "All-in-One Calculator", 22, Ui.TEXT, true);
        title.setLetterSpacing(-0.01f);
        box.addView(title);

        TextView sub = Ui.text(this, "10 tools + settings · offline friendly", 12, Ui.MUTED, false);
        sub.setPadding(0, Ui.dp(this, 2), 0, 0);
        box.addView(sub);
        return box;
    }

    private View buildTabs() {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setClipToPadding(false);

        LinearLayout strip = Ui.row(this);
        int pad = Ui.dp(this, 12);
        strip.setPadding(pad, 0, pad, Ui.dp(this, 10));

        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            TextView chip = Ui.text(this, TABS[i], 14, Ui.MUTED, true);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(Ui.dp(this, 15), Ui.dp(this, 8), Ui.dp(this, 15), Ui.dp(this, 8));
            chip.setClickable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(index, true);
                }
            });
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2);
            p.rightMargin = Ui.dp(this, 8);
            strip.addView(chip, p);
            chips[i] = chip;
        }

        scroll.addView(strip, new HorizontalScrollView.LayoutParams(-2, -2));
        return scroll;
    }

    private void select(int index, boolean animate) {
        current = index;
        host.removeAllViews();
        host.addView(screen(index).view(), new FrameLayout.LayoutParams(-1, -1));

        for (int i = 0; i < chips.length; i++) {
            boolean on = i == index;
            chips[i].setTextColor(on ? Ui.ACCENT_D : Ui.MUTED);
            chips[i].setBackground(on
                    ? Ui.roundBg(Ui.ACCENT, Ui.dp(this, 20))
                    : Ui.roundBg(Ui.SURFACE2, Ui.dp(this, 20)));
        }
        if (animate) {
            host.setAlpha(0f);
            host.animate().alpha(1f).setDuration(140).start();
        }
    }

    private Screen screen(int i) {
        if (cache[i] == null) {
            switch (i) {
                case 0: cache[i] = new CalculatorScreen(this, false); break;
                case 1: cache[i] = new CalculatorScreen(this, true); break;
                case 2: cache[i] = new ConverterScreen(this); break;
                case 3: cache[i] = new CurrencyScreen(this); break;
                case 4: cache[i] = new BmiScreen(this); break;
                case 5: cache[i] = new LoanScreen(this); break;
                case 6: cache[i] = new AgeScreen(this); break;
                case 7: cache[i] = new PercentScreen(this); break;
                case 8: cache[i] = new TipScreen(this); break;
                case 9: cache[i] = new DiscountScreen(this); break;
                case 10: cache[i] = new SettingsScreen(this); break;
            default: cache[i] = new AboutScreen(this); break;
            }
        }
        return cache[i];
    }

    @Override
    public void onBackPressed() {
        if (current != 0) {
            select(0, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("tab", current);
    }
}
