package com.dabai.markdownq.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.markdownq.R;
import com.dabai.markdownq.utils.DabaiUtils;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initTheme();


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Preference workdir = getPreferenceManager().findPreference("workdir");
            final Preference ver = getPreferenceManager().findPreference("ver");

            workdir.setSummary(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
            ver.setSummary(new DabaiUtils().getVersionName(getContext()));

        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            switch (preference.getKey()) {

                case "workdir":
                    new MaterialDialog.Builder(getContext())
                            .title("工作空间更改")
                            .content("请输入合法路径，不然不能修改")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("", null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    File file = new File(input.toString());
                                    if (file.isDirectory() && file.exists()) {
                                        set_sharedString("workdir", file.getAbsolutePath()+"/");
                                        final Preference workdir = getPreferenceManager().findPreference("workdir");
                                        workdir.setSummary(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
                                        Toast.makeText(getContext(), "更改成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "路径不合法", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .positiveText("确定")
                            .neutralText("恢复默认")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    File file = new File("/sdcard/MarkdownQ/");
                                    if (!file.exists()){
                                        file.mkdirs();
                                    }
                                    if (file.isDirectory() && file.exists()) {
                                        set_sharedString("workdir", file.getAbsolutePath()+"/");
                                        final Preference workdir = getPreferenceManager().findPreference("workdir");
                                        workdir.setSummary(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
                                        Toast.makeText(getContext(), "已恢复默认", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "异常", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();

                    break;
            }


            return super.onPreferenceTreeClick(preference);
        }

        /**
         * 提交与获取
         *
         * @param key
         * @param value
         */
        public void set_sharedString(String key, String value) {
            SharedPreferences sp = getActivity().getSharedPreferences("data", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }

        public String get_sharedString(String key, String moren) {
            SharedPreferences sp = getActivity().getSharedPreferences("data", 0);
            return sp.getString(key, moren);
        }

    }


    /**
     * 提交与获取
     *
     * @param key
     * @param value
     */
    public void set_sharedString(String key, String value) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get_sharedString(String key, String moren) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        return sp.getString(key, moren);
    }


    private void initTheme() {

        String theme = get_sharedString("theme", "日");
        if (theme.equals("日")) {

        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#607D8B")));
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = this.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#455A64"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                // 处理返回逻辑
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}