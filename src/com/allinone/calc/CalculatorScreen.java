package com.allinone.calc;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class CalculatorScreen implements Screen {

    private static final String[][] BASIC_KEYS = {
            {"C", "⌫", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"0", ".", "±", "="}
    };

    private static final String[][] SCI_KEYS = {
            {"sin", "cos", "tan", "DEG", "C"},
            {"ln", "log", "√", "^", "⌫"},
            {"π", "e", "x!", "(", ")"},
            {"7", "8", "9", "÷", "×"},
            {"4", "5", "6", "−", "+"},
            {"1", "2", "3", "%", "±"},
            {"0", ".", "="}
    };

    private static final String[] DEL_SUFFIX = {"sin(", "cos(", "tan(", "log(", "ln(", "√(", "^2", "!"};

    private final boolean sci;
    private final Context ctx;
    private final View root;
    private final StringBuilder buf = new StringBuilder();
    private final TextView exprView;
    private final TextView previewView;
    private final TextView historyView;
    private TextView degKey;
    private boolean degrees = true;

    public CalculatorScreen(Context c, boolean scientific) {
        this.sci = scientific;
        this.ctx = c;

        LinearLayout col = Ui.column(c, 0);
        int p = Ui.dp(c, 14);
        col.setPadding(p, Ui.dp(c, 8), p, p);

        LinearLayout card = Ui.column(c, 0);
        card.setBackground(Ui.roundBg(Ui.SURFACE, Ui.dp(c, 20), Ui.dp(c, 1), Ui.STROKE));
        int cp = Ui.dp(c, 16);
        card.setPadding(cp, Ui.dp(c, 14), cp, Ui.dp(c, 14));

        historyView = Ui.text(c, "", 12, Ui.MUTED, false);
        historyView.setGravity(Gravity.END);
        historyView.setSingleLine(true);

        LinearLayout topRow = Ui.row(c);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView historyButton = Ui.text(c, "HISTORY", 10, Ui.ACCENT, true);
        historyButton.setLetterSpacing(0.08f);
        historyButton.setPadding(Ui.dp(c, 9), Ui.dp(c, 5), Ui.dp(c, 9), Ui.dp(c, 5));
        historyButton.setBackground(Ui.roundBg(0xFF14332C, Ui.dp(c, 8)));
        historyButton.setClickable(true);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistory();
            }
        });
        topRow.addView(historyButton);
        topRow.addView(historyView, new LinearLayout.LayoutParams(0, -2, 1f));
        card.addView(topRow, new LinearLayout.LayoutParams(-1, -2));

        exprView = Ui.text(c, "0", sci ? 28 : 36, Ui.TEXT, false);
        exprView.setGravity(Gravity.END);
        exprView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        exprView.setMaxLines(2);
        card.addView(exprView, new LinearLayout.LayoutParams(-1, -2));

        previewView = Ui.text(c, " ", 15, Ui.ACCENT, true);
        previewView.setGravity(Gravity.END);
        previewView.setSingleLine(true);
        card.addView(previewView, new LinearLayout.LayoutParams(-1, -2));

        col.addView(card, new LinearLayout.LayoutParams(-1, -2));
        col.addView(buildKeypad(c), new LinearLayout.LayoutParams(-1, 0, 1f));

        this.root = col;
    }

    @Override
    public View view() {
        return root;
    }

    private View buildKeypad(Context c) {
        String[][] keys = sci ? SCI_KEYS : BASIC_KEYS;
        LinearLayout pad = Ui.column(c, 0);
        pad.setPadding(0, Ui.dp(c, 8), 0, 0);

        for (String[] rowDef : keys) {
            LinearLayout row = Ui.row(c);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, 0, 1f);
            rowParams.topMargin = Ui.dp(c, 5);
            pad.addView(row, rowParams);

            for (String label : rowDef) {
                float weight = 1f;
                if (rowDef.length == 3) {
                    if (label.equals("0") || label.equals("=")) {
                        weight = 2f;
                    }
                }
                TextView key = Ui.key(c, label, keyBg(label), keyFg(label), keySize(label));
                if (label.equals("DEG")) {
                    degKey = key;
                }
                LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(0, -1, weight);
                int m = Ui.dp(c, 3);
                kp.setMargins(m, 0, m, 0);
                row.addView(key, kp);

                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handle(label);
                    }
                });
            }
        }
        return pad;
    }

    private float keySize(String label) {
        if (label.length() == 1 && Character.isDigit(label.charAt(0))) {
            return sci ? 21 : 23;
        }
        if (label.equals("=") || label.equals("C") || label.equals("⌫")
                || label.equals(".") || label.equals("±") || label.equals("%")) {
            return sci ? 20 : 22;
        }
        if (label.equals("DEG") || label.equals("RAD")) {
            return 12;
        }
        if (label.length() > 1) {
            return 14;
        }
        return 21;
    }

    private int keyBg(String label) {
        if (isDigit(label)) {
            return Ui.SURFACE2;
        }
        switch (label) {
            case "=":
                return Ui.ACCENT;
            case "C":
                return 0xFF3A2126;
            case "⌫":
                return 0xFF31262A;
            case "DEG":
            case "RAD":
                return 0xFF13332C;
            case "(":
            case ")":
                return Ui.SURFACE;
            default:
                break;
        }
        if (label.length() > 1) {
            return Ui.SURFACE;
        }
        return Ui.OP_BG;
    }

    private int keyFg(String label) {
        switch (label) {
            case "=":  return Ui.ACCENT_D;
            case "C":  return 0xFFFF9AA2;
            case "⌫":  return 0xFFFFB3B8;
            case "DEG":
            case "RAD": return Ui.ACCENT;
            default: break;
        }
        if (isDigit(label) || label.equals(".")) {
            return Ui.TEXT;
        }
        if (label.equals("(") || label.equals(")")) {
            return Ui.MUTED;
        }
        return Ui.ACCENT;
    }

    private boolean isDigit(String s) {
        return s.length() == 1 && s.charAt(0) >= '0' && s.charAt(0) <= '9';
    }

    private void handle(String key) {
        switch (key) {
            case "C":
                buf.setLength(0);
                historyView.setText("");
                previewView.setText(" ");
                break;
            case "⌫":
                deleteLast();
                break;
            case "=":
                evaluate();
                return;
            case "DEG":
            case "RAD":
                degrees = !degrees;
                degKey.setText(degrees ? "DEG" : "RAD");
                break;
            case "±":
                toggleSign();
                break;
            case "√":
                buf.append("√(");
                break;
            case "x²":
                buf.append("^2");
                break;
            case "x!":
                buf.append("!");
                break;
            case "sin":
            case "cos":
            case "tan":
            case "ln":
            case "log":
                buf.append(key).append('(');
                break;
            case "^":
                buf.append('^');
                break;
            default:
                buf.append(key);
                break;
        }
        refresh();
    }

    private void deleteLast() {
        if (buf.length() == 0) {
            return;
        }
        String s = buf.toString();
        for (String suffix : DEL_SUFFIX) {
            if (s.endsWith(suffix)) {
                buf.setLength(buf.length() - suffix.length());
                return;
            }
        }
        if (s.endsWith("^2")) {
            buf.setLength(buf.length() - 2);
            return;
        }
        buf.setLength(buf.length() - 1);
    }

    private void toggleSign() {
        String s = buf.toString();
        if (s.isEmpty()) {
            buf.append('-');
            return;
        }
        int i = s.length() - 1;
        while (i >= 0 && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
            i--;
        }
        if (i >= 0 && s.charAt(i) == '-') {
            buf.deleteCharAt(i);
        } else {
            buf.insert(i + 1, '-');
        }
    }

    private static String balanced(String src) {
        int open = 0;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (c == '(') {
                open++;
            } else if (c == ')') {
                open--;
            }
        }
        if (open <= 0) {
            return src;
        }
        StringBuilder b = new StringBuilder(src);
        while (open-- > 0) {
            b.append(')');
        }
        return b.toString();
    }

    private void refresh() {
        String src = buf.length() == 0 ? "0" : buf.toString();
        exprView.setText(src);
        if (buf.length() == 0) {
            previewView.setText(" ");
            previewView.setTextColor(Ui.ACCENT);
            return;
        }
        try {
            double v = Expr.eval(balanced(src), degrees);
            if (Ui.valid(v)) {
                previewView.setText("= " + Ui.fmt(ctx, v));
                previewView.setTextColor(Ui.ACCENT);
            } else {
                previewView.setText(" ");
            }
        } catch (Exception e) {
            previewView.setText(" ");
        }
    }

    private void evaluate() {
        if (buf.length() == 0) {
            return;
        }
        String src = balanced(buf.toString());
        try {
            double v = Expr.eval(src, degrees);
            if (!Ui.valid(v)) {
                throw new ArithmeticException("bad result");
            }
            String out = Ui.fmt(ctx, v);
            historyView.setText(src + " =");
            HistoryStore.add(ctx, src, out);
            buf.setLength(0);
            buf.append(out);
            exprView.setText(out);
            previewView.setText(" ");
            previewView.setTextColor(Ui.ACCENT);
        } catch (Exception e) {
            previewView.setText("Invalid expression");
            previewView.setTextColor(Ui.DANGER);
        }
    }

    private void showHistory() {
        final List<String[]> items = HistoryStore.load(ctx);

        final AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setTitle("History")
                .setNegativeButton("Close", null)
                .create();

        LinearLayout list = Ui.column(ctx, 0);
        int pad = Ui.dp(ctx, 16);
        list.setPadding(pad, Ui.dp(ctx, 6), pad, Ui.dp(ctx, 6));

        if (items.isEmpty()) {
            TextView empty = Ui.text(ctx, "No calculations yet.", 14, Ui.MUTED, false);
            empty.setPadding(0, Ui.dp(ctx, 18), 0, Ui.dp(ctx, 18));
            list.addView(empty);
        } else {
            for (final String[] item : items) {
                LinearLayout row = Ui.column(ctx, 0);
                row.setPadding(Ui.dp(ctx, 8), Ui.dp(ctx, 10), Ui.dp(ctx, 8), Ui.dp(ctx, 10));
                row.setClickable(true);
                row.setBackground(Ui.ripple(Ui.SURFACE, Ui.dp(ctx, 10)));

                TextView expression = Ui.text(ctx, item[0], 13, Ui.MUTED, false);
                expression.setSingleLine(true);
                TextView value = Ui.text(ctx, "= " + item[1], 17, Ui.TEXT, true);
                value.setSingleLine(true);

                row.addView(expression);
                row.addView(value);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buf.setLength(0);
                        buf.append(item[1]);
                        refresh();
                        dialog.dismiss();
                    }
                });
                list.addView(row);
                list.addView(Ui.divider(ctx));
            }
        }

        ScrollView scroller = new ScrollView(ctx);
        scroller.addView(list, new ScrollView.LayoutParams(-1, -2));
        dialog.setView(scroller);

        if (!items.isEmpty()) {
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Clear all",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface d, int which) {
                            HistoryStore.clear(ctx);
                            historyView.setText("");
                        }
                    });
        }
        dialog.show();
    }
}
