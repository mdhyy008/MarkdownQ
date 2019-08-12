package com.dabai.markdownq.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.markdownq.MainActivity;
import com.dabai.markdownq.R;
import com.dabai.markdownq.utils.DabaiUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

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
            final Preference textsize = getPreferenceManager().findPreference("textsize");

            workdir.setSummary(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
            textsize.setSummary(get_sharedString("textsize", "18") + "sp");
            ver.setSummary(new DabaiUtils().getVersionName(getContext()));

        }

        Float size;

        MaterialDialog file_choose_dialog;
        ListView lv;



        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            switch (preference.getKey()) {
                case "deldir":

                    huanyuan();

                    break;
                case "scr_some":
                    SwitchPreference sp = (SwitchPreference) findPreference("scr_some");
                    if (sp.isChecked()) {
                        set_sharedString("scr_some","同步滚动");
                    } else {
                        set_sharedString("scr_some","不同步滚动");
                    }
                    break;
                case "textsize":

                    View view222 = getLayoutInflater().inflate(R.layout.dialog_change_textsize, null);

                    AlertDialog ad = new AlertDialog.Builder(getContext())
                            .setTitle("字体大小调整")
                            .setView(view222)
                            .setNeutralButton("恢复默认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    set_sharedString("textsize", "" + 18);
                                    final Preference textsize = getPreferenceManager().findPreference("textsize");
                                    textsize.setSummary(get_sharedString("textsize", "18") + "sp");

                                }
                            })
                            .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    set_sharedString("textsize", "" + size.intValue());
                                    final Preference textsize = getPreferenceManager().findPreference("textsize");
                                    textsize.setSummary(get_sharedString("textsize", "18") + "sp");

                                }
                            })
                            .show();

                    SeekBar seekBar = view222.findViewById(R.id.seekBar);
                    final TextView textView = view222.findViewById(R.id.textView555);
                    textView.setTextSize(Float.parseFloat(get_sharedString("textsize", "18")));
                    textView.setText(get_sharedString("textsize", "18"));
                    seekBar.setProgress(Integer.parseInt(get_sharedString("textsize", "18")) - 15);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            size = (float) (i + 15);
                            textView.setTextSize(size);
                            textView.setText(size.intValue() + "");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });


                    break;
                case "about":
                    startActivity(new Intent(getContext(), AboutActivity.class));
                    break;
                case "github":
                    startActivity(new Intent(getContext(), GithubActivity.class));
                    break;
                case "workdir":
                    change_workdir();

                    break;
            }


            return super.onPreferenceTreeClick(preference);
        }

        private void huanyuan() {

            File del_history_dir11 = new File("/sdcard/.MarkdownQ_del/");
            if (!del_history_dir11.exists()){
                del_history_dir11.mkdirs();
            }

            final ArrayList<String> data = new ArrayList<>();
            final String dir_path = "/sdcard/.MarkdownQ_del/";
            for (String filename : new File(dir_path).list()) {
                if (filename.endsWith(".md") || filename.endsWith(".MD")) {
                    data.add(filename);
                }
            }
            View view = getLayoutInflater().inflate(R.layout.dialog_filechoose, null);

            file_choose_dialog = new MaterialDialog.Builder(getContext())
                    .title("回收站")
                    .customView(view, false)
                    .positiveText("关闭")
                    .neutralText("清空回收站")
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new MaterialDialog.Builder(getContext())
                                    .title("警告")
                                    .content("按下确认键，回收站内所有文件将被删除，不可找回！")
                                    .positiveText("确认")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            new DabaiUtils().deleteDir(new File("/sdcard/.MarkdownQ_del/"));
                                        }
                                    })
                                    .neutralText("算了")
                                    .show();

                        }
                    })
                    .show();
            if (data.size() != 0) {
                view.findViewById(R.id.filecte).setVisibility(View.GONE);
            }
            lv = view.findViewById(R.id.lv);
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, data);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String path = new File(new File(dir_path), data.get(i)).getAbsolutePath();

                    data.remove(i);
                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, data);
                    lv.setAdapter(adapter);

                    final File file = new File(path);
                    File del_history_dir = new File(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
                    final File delfile = new File(del_history_dir, file.getName());

                    if (!del_history_dir.exists()) {
                        del_history_dir.mkdirs();
                        file.renameTo(delfile);
                    } else {
                       file.renameTo(delfile);
                    }
                }
            });
        }

        private void change_workdir() {
            new MaterialDialog.Builder(getContext())
                    .title("工作空间更改")
                    .content("请输入合法路径，不然不能修改")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            File file = new File(input.toString());
                            if (file.isDirectory() && file.exists()) {
                                set_sharedString("workdir", file.getAbsolutePath() + "/");
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
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (file.isDirectory() && file.exists()) {
                                set_sharedString("workdir", file.getAbsolutePath() + "/");
                                final Preference workdir = getPreferenceManager().findPreference("workdir");
                                workdir.setSummary(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
                                Toast.makeText(getContext(), "已恢复默认", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "异常", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
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