package com.allinone.calc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

/** Small helper layer for building the whole UI programmatically. */
public final class Ui {

    public static final int BG       = 0xFF0D1117;
    public static final int SURFACE  = 0xFF161D26;
    public static final int SURFACE2 = 0xFF1E2732;
    public static final int STROKE   = 0xFF2A3644;
    public static final int TEXT     = 0xFFF2F6FA;
    public static final int MUTED    = 0xFF8A97A8;
    public static final int ACCENT   = 0xFF2DD4A7;
    public static final int ACCENT_D = 0xFF062A22;
    public static final int WARN     = 0xFFF3B544;
    public static final int DANGER   = 0xFFFF6B6B;
    public static final int OP_BG    = 0xFF1B2733;

    private Ui() {
    }

    public static int dp(Context c, float v) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics()));
    }

    public static TextView text(Context c, CharSequence s, float sp, int color, boolean bold) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) {
            t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        }
        return t;
    }

    public static GradientDrawable roundBg(int color, float radius) {
        return roundBg(color, radius, 0, 0);
    }

    public static GradientDrawable roundBg(int color, float radius, int stroke, int strokeColor) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        if (stroke > 0) {
            g.setStroke(stroke, strokeColor);
        }
        return g;
    }

    public static RippleDrawable ripple(int color, float radius) {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(Color.WHITE);
        mask.setCornerRadius(radius);
        return new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), roundBg(color, radius), mask);
    }

    public static LinearLayout column(Context c, int padDp) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        if (padDp > 0) {
            int p = dp(c, padDp);
            l.setPadding(p, p, p, p);
        }
        return l;
    }

    public static LinearLayout row(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        return l;
    }

    public static ScrollView scroll(Context c, View child) {
        ScrollView s = new ScrollView(c);
        s.setFillViewport(true);
        s.setClipToPadding(false);
        s.setVerticalScrollBarEnabled(false);
        s.addView(child, new ScrollView.LayoutParams(-1, -2));
        return s;
    }

    public static TextView key(Context c, String label, int bg, int fg, float sp) {
        TextView k = new TextView(c);
        k.setText(label);
        k.setTextSize(sp);
        k.setTextColor(fg);
        k.setGravity(Gravity.CENTER);
        k.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        k.setBackground(ripple(bg, dp(c, 16)));
        k.setClickable(true);
        k.setFocusable(true);
        return k;
    }

    public static EditText field(Context c, String hint) {
        EditText e = new EditText(c);
        e.setHint(hint);
        e.setTextSize(16);
        e.setTextColor(TEXT);
        e.setHintTextColor(MUTED);
        int p = dp(c, 12);
        e.setPadding(p, p, p, p);
        e.setBackground(roundBg(SURFACE2, dp(c, 12)));
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setSingleLine(true);
        return e;
    }

    public static void watch(EditText e, Runnable r) {
        e.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            public void onTextChanged(CharSequence s, int a, int b, int c) {
                r.run();
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static TextView cap(Context c, String s) {
        TextView t = text(c, s.toUpperCase(Locale.US), 11, MUTED, true);
        t.setLetterSpacing(0.08f);
        return t;
    }

    public static LinearLayout.LayoutParams lp(int w, int h, int l, int t, int r, int b, Context c) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(dp(c, l), dp(c, t), dp(c, r), dp(c, b));
        return p;
    }

    public static EditText addField(Context c, LinearLayout parent, String label, String hint, String def) {
        parent.addView(cap(c, label), lp(-1, -2, 0, 10, 0, 0, c));
        EditText e = field(c, hint);
        if (def != null) {
            e.setText(def);
        }
        parent.addView(e, lp(-1, -2, 0, 6, 0, 0, c));
        return e;
    }

    public static TextView addOutput(Context c, LinearLayout parent, String label) {
        LinearLayout r = row(c);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(text(c, label, 13, MUTED, false), new LinearLayout.LayoutParams(0, -2, 1f));
        TextView v = text(c, "—", 16, TEXT, true);
        v.setGravity(Gravity.END);
        r.addView(v, new LinearLayout.LayoutParams(-2, -2));
        parent.addView(r, lp(-1, -2, 0, 12, 0, 0, c));
        return v;
    }

    public static LinearLayout card(Context c, String title) {
        LinearLayout card = column(c, 0);
        card.setBackground(roundBg(SURFACE, dp(c, 18), dp(c, 1), STROKE));
        int p = dp(c, 16);
        card.setPadding(p, p, p, p);
        if (title != null) {
            card.addView(text(c, title, 15, TEXT, true));
            card.addView(spacer(c, 10));
        }
        return card;
    }

    public static View spacer(Context c, int h) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(c, h)));
        return v;
    }

    public static View divider(Context c) {
        View v = new View(c);
        v.setBackgroundColor(STROKE);
        v.setLayoutParams(new LinearLayout.LayoutParams(-1, Math.max(1, dp(c, 1))));
        return v;
    }

    public static ArrayAdapter<String> adapter(final Context c, String[] items) {
        return new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView t = (TextView) super.getView(position, convertView, parent);
                t.setTextColor(TEXT);
                t.setTextSize(15);
                t.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                t.setPadding(dp(c, 12), dp(c, 10), dp(c, 12), dp(c, 10));
                return t;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView t = (TextView) super.getDropDownView(position, convertView, parent);
                t.setTextColor(TEXT);
                t.setTextSize(15);
                t.setBackgroundColor(SURFACE2);
                t.setPadding(dp(c, 14), dp(c, 12), dp(c, 14), dp(c, 12));
                return t;
            }
        };
    }

    public static Spinner spinner(Context c, String[] items) {
        Spinner s = new Spinner(c);
        s.setAdapter(adapter(c, items));
        s.setBackground(roundBg(SURFACE2, dp(c, 12)));
        s.setPopupBackgroundDrawable(roundBg(SURFACE, dp(c, 12)));
        return s;
    }

    /** Formats a double for display without ugly floating point tails. */
    public static String fmt(double d) {
        return fmt(null, d);
    }

    public static String fmt(Context c, double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return "Error";
        }
        if (Math.abs(d) < 1e-12) {
            d = 0;
        }
        int dec = (c != null) ? SettingsPrefs.getDecimalPlaces(c) : -1;
        boolean comma = (c == null) || SettingsPrefs.isCommaGroup(c);
        if (dec >= 0) {
            java.math.BigDecimal bd = java.math.BigDecimal.valueOf(d)
                    .setScale(dec, java.math.RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            String s = bd.toPlainString();
            if (comma && s.indexOf('E') < 0) {
                s = addCommas(s);
            }
            return s;
        }
        if (d == Math.rint(d) && Math.abs(d) < 1e15) {
            String s = String.valueOf((long) d);
            if (comma) s = addCommas(s);
            return s;
        }
        double abs = Math.abs(d);
        if (abs >= 1e15 || abs < 1e-7) {
            String s = String.format(Locale.US, "%.6E", d);
            int e = s.indexOf('E');
            if (e > 0) {
                String mant = s.substring(0, e);
                String exp = s.substring(e);
                if (mant.contains(".")) {
                    mant = mant.replaceAll("0+$", "");
                    if (mant.endsWith(".")) {
                        mant = mant.substring(0, mant.length() - 1);
                    }
                }
                return mant + exp;
            }
            return s;
        }
        java.math.BigDecimal bd = java.math.BigDecimal.valueOf(d)
                .setScale(8, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros();
        String s = bd.toPlainString();
        if (comma) s = addCommas(s);
        return s;
    }

    private static String addCommas(String num) {
        if (num == null) return "0";
        int dot = num.indexOf('.');
        String intPart = dot >= 0 ? num.substring(0, dot) : num;
        String fracPart = dot >= 0 ? num.substring(dot) : "";
        boolean neg = intPart.startsWith("-");
        if (neg) intPart = intPart.substring(1);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = intPart.length() - 1; i >= 0; i--) {
            sb.append(intPart.charAt(i));
            count++;
            if (count == 3 && i > 0) { sb.append(','); count = 0; }
        }
        String out = sb.reverse().toString();
        if (neg) out = "-" + out;
        return out + fracPart;
    }

    public static String money(double d) {
        return money(null, d);
    }

    public static String money(Context c, double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return "—";
        }
        int dec = (c != null) ? SettingsPrefs.getDecimalPlaces(c) : 2;
        if (dec < 0) dec = 2;
        boolean comma = (c == null) || SettingsPrefs.isCommaGroup(c);
        String s = String.format(Locale.US, "%." + dec + "f", d);
        if (comma) s = addCommas(s);
        return s;
    }

    public static double parse(String s) {
        if (s == null || s.trim().isEmpty()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public static boolean valid(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }
}
