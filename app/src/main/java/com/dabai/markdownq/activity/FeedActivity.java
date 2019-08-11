package com.dabai.markdownq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
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

        til2 = findViewById(R.id.til2);
        seedbutton = findViewById(R.id.send_feed);

    }


    public void seed_feedtext(View view) {

        String text = til2.getEditText().getText().toString();


        try {
            if (!text.equals("")) {
                seedbutton.setText("正在发送......");
                feedback(getString(R.string.app_name), text);
            }else {
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
                    String textm = "  \n**[时间：" +sdf.format(new Date())+ "]** **[版本：" + new DabaiUtils().getVersionName(getApplicationContext()) + "]**";

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
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


}
