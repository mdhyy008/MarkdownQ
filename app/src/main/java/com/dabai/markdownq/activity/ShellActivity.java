package com.dabai.markdownq.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.markdownq.MainActivity;
import com.dabai.markdownq.R;
import com.dabai.markdownq.utils.FileUtils;
import com.dabai.markdownq.utils.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShellActivity extends AppCompatActivity {

    private Context context;
    private GridView lv;
    EditText ed;
    private File dir;
    private String[] data;
    private AlertDialog dialog_sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);
        context = getApplicationContext();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initTheme();



        dialog_sh = new AlertDialog.Builder(this)
                .setTitle("脚本执行器")
                .setMessage("")
                .setPositiveButton("关闭弹窗",null)
                .create();



        lv = findViewById(R.id.lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String filename;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 filename = data[i];

                new MaterialDialog.Builder(ShellActivity.this)
                        .title("提示")
                        .content("确定执行 "+filename +" 嘛？")
                        .positiveText("确认")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                
                                File file = new File(dir,filename);
                                do_exec("sh "+file.getAbsolutePath());

                            }
                        })
                        .negativeText("取消")
                        .show();

            }
        });





        ed = findViewById(R.id.ed);


        init();

    }

    private void init() {

        dir = new File(context.getFilesDir(), "shells");
        if (!dir.exists()) {
            dir.mkdir();
        }


         data = dir.list();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);

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

    public void deledit(View view) {

        new MaterialDialog.Builder(this)
                .title("提示")
                .content("确认清空内容吗？")
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ed.setText("");
                    }
                })
                .negativeText("取消")
                .show();

    }

    public void copyedit(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(ed.getText().toString());
        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();

    }

    public void pasteedit(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ed.setText(clipboardManager.getText().toString());
        Toast.makeText(context, "粘贴成功", Toast.LENGTH_SHORT).show();
        ed.setSelection(ed.length());
    }

    public void saveedit(View view) {

        new MaterialDialog.Builder(this)
                .title("文件名")
                .content("请输入文件名")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {


                        File file = new File(dir, input + ".sh");
                        if (file.exists()) {
                            Toast.makeText(context, "保存失败，文件名冲突", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                new FileUtils().writeText(file.getAbsolutePath(), ed.getText().toString(), true);
                                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                                init();
                                ed.setText("");
                            } catch (IOException e) {
                                Toast.makeText(context, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                })
                .positiveText("保存")
                .show();
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }


    String do_exec(String cmd) {

        dialog_sh.setMessage("");
        dialog_sh.show();
        String s = "\n";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                Log.d("dabai", "do_exec: "+line);

                s += line + "\n";
                dialog_sh.setMessage(s);
            }
        } catch (IOException e) {
            dialog_sh.setMessage(""+e);
            Log.d("dabai", "do_exec: "+e);
        }
        return cmd;
    }

}
