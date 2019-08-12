package com.dabai.markdownq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.dabai.markdownq.R;
import com.dabai.markdownq.utils.DabaiUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedActivity extends AppCompatActivity {

    TextInputLayout til2;
    Button seedbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til2 = findViewById(R.id.til2);
        seedbutton = findViewById(R.id.send_feed);
        initTheme();
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




    public void seed_feedtext(View view) {

        String text = til2.getEditText().getText().toString();

        try {
            if (!text.equals("")) {
                seedbutton.setText("正在发送......");
                feedback(getString(R.string.app_name), text);
            } else {
                til2.getEditText().setError("这里还空着哦!");
            }
        } catch (Exception e) {
            Toast.makeText(FeedActivity.this, "哦~~被玩坏了!", Toast.LENGTH_SHORT).show();
        }
    }


    public void feedback(final String title, final String text) {
        new Thread(new Runnable() {

            private int qucode;

            @Override
            public void run() {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss EEE", Locale.CHINA);
                    String titlem = "[来自:" + Build.MODEL + "的反馈]";
                    String textm = "  \n**[时间：" + sdf.format(new Date()) + "]** **[版本：" + new DabaiUtils().getVersionName(getApplicationContext()) + "]**";

                    URL url = new URL("https://sc.ftqq.com/SCU35649Tec88ecad70ac8f2375a6c5a6e323c8425be9602402c5b.send?text=" + title + titlem + "&desp=" + text + textm);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    final int code = urlConnection.getResponseCode();
                    qucode = code;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) {
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                finish();
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(getApplicationContext(), "应该是没有网络吧", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }).start();
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

}
