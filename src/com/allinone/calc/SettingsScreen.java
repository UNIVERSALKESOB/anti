package com.allinone.calc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsScreen implements Screen {

    private final Context ctx;
    private final View root;

    public SettingsScreen(Context c) {
        this.ctx = c;

        LinearLayout col = Ui.column(c, 0);
        int p = Ui.dp(c, 14);
        col.setPadding(p, Ui.dp(c, 8), p, p);

        // Display settings
        LinearLayout disp = Ui.card(c, "Display");
        disp.addView(Ui.cap(c, "Decimal places"));
        final String[] decLabels = {"Auto", "0", "1", "2", "3", "4", "5", "6", "7", "8"};
        final Spinner decSpin = Ui.spinner(c, decLabels);
        int curDec = SettingsPrefs.getDecimalPlaces(c);
        int decSel = 0;
        if (curDec >= 0 && curDec <= 8) decSel = curDec + 1;
        decSpin.setSelection(decSel);
        decSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int v = (position == 0) ? -1 : (position - 1);
                SettingsPrefs.setDecimalPlaces(ctx, v);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        disp.addView(decSpin, Ui.lp(-1, -2, 0, 6, 0, 0, c));

        disp.addView(Ui.spacer(c, 10));
        LinearLayout rowComma = rowToggle(c, "Thousand separators", "Use commas (e.g. 1,000)", SettingsPrefs.isCommaGroup(c));
        final Switch swComma = (Switch) rowComma.getTag();
        swComma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsPrefs.setCommaGroup(ctx, isChecked);
            }
        });
        disp.addView(rowComma, new LinearLayout.LayoutParams(-1, -2));
        col.addView(disp, new LinearLayout.LayoutParams(-1, -2));

        // Behavior
        LinearLayout beh = Ui.card(c, "Behavior");
        LinearLayout rowVib = rowToggle(c, "Vibration feedback", "Reserved (saved for future use)", SettingsPrefs.isVibrate(c));
        final Switch swVib = (Switch) rowVib.getTag();
        swVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsPrefs.setVibrate(ctx, isChecked);
            }
        });
        beh.addView(rowVib, new LinearLayout.LayoutParams(-1, -2));
        col.addView(beh, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // Data
        LinearLayout data = Ui.card(c, "Data");
        TextView clearBtn = Ui.key(c, "Clear Calculator History", Ui.SURFACE2, Ui.TEXT, 14);
        LinearLayout.LayoutParams clearLp = new LinearLayout.LayoutParams(-1, Ui.dp(c, 46));
        clearBtn.setGravity(Gravity.CENTER);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClearHistory();
            }
        });
        data.addView(clearBtn, clearLp);
        col.addView(data, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // Reset
        LinearLayout resetCard = Ui.card(c, "Reset");
        TextView resetBtn = Ui.key(c, "Reset All Settings", 0xFF3A2126, 0xFFFF9AA2, 14);
        resetBtn.setGravity(Gravity.CENTER);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReset();
            }
        });
        resetCard.addView(resetBtn, new LinearLayout.LayoutParams(-1, Ui.dp(c, 46)));
        col.addView(resetCard, Ui.lp(-1, -2, 0, 12, 0, 0, c));

        // Info
        TextView info = Ui.text(c, "Version " + versionName(c), 12, Ui.MUTED, false);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, Ui.dp(c, 18), 0, Ui.dp(c, 8));
        col.addView(info, new LinearLayout.LayoutParams(-1, -2));

        root = Ui.scroll(c, col);
    }

    private LinearLayout rowToggle(Context c, String title, String subtitle, boolean checked) {
        LinearLayout row = Ui.row(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout textCol = Ui.column(c, 0);
        textCol.addView(Ui.text(c, title, 14, Ui.TEXT, false));
        TextView sub = Ui.text(c, subtitle, 12, Ui.MUTED, false);
        sub.setPadding(0, Ui.dp(c, 2), 0, 0);
        textCol.addView(sub);
        row.addView(textCol, new LinearLayout.LayoutParams(0, -2, 1f));
        Switch sw = new Switch(c);
        sw.setChecked(checked);
        row.addView(sw, new LinearLayout.LayoutParams(-2, -2));
        row.setTag(sw);
        return row;
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(ctx)
                .setTitle("Clear history?")
                .setMessage("This will remove all saved calculator history.")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HistoryStore.clear(ctx);
                        Toast.makeText(ctx, "History cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmReset() {
        new AlertDialog.Builder(ctx)
                .setTitle("Reset settings?")
                .setMessage("This will reset all app settings to default.")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettingsPrefs.resetAll(ctx);
                        Toast.makeText(ctx, "Settings reset. Reopen settings to see defaults.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String versionName(Context c) {
        try {
            PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return info.versionName == null ? "1.0" : info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    @Override
    public View view() {
        return root;
    }
}
