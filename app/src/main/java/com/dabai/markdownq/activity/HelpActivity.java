package com.dabai.markdownq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabai.markdownq.R;
import com.dabai.markdownq.utils.FileUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;

public class HelpActivity extends AppCompatActivity {

    private MarkdownView mMarkdownView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        textView = findViewById(R.id.help_textView);
        String edittext = getFromAssets("help.md");
        textView.setText(edittext);
        mMarkdownView = (MarkdownView) findViewById(R.id.help_markdownview);

        mMarkdownView.addStyleSheet(new Github());
        mMarkdownView.loadMarkdown(edittext);


    }


    public String getFromAssets(String fileName) {
        String Result1 = null;
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            Result1 = "";
            while ((line = bufReader.readLine()) != null)
                Result1 += line+"\n";
            return Result1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result1;
    }
}
