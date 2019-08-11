package com.dabai.markdownq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.markdownq.activity.FeedActivity;
import com.dabai.markdownq.activity.HelpActivity;
import com.dabai.markdownq.utils.DabaiUtils;
import com.dabai.markdownq.utils.FileUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.zzhoujay.markdown.MarkDown;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichType;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.ig.DefaultImageGetter;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import ren.qinc.edit.PerformEdit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View view1, view2;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组
    EditText view_edit;
    TextView view_result_text;
    WebView view_result_web;

    ConstraintLayout cons;

    TextView te2;
    CardView tipscard;
    // 变量

    private PerformEdit mPerformEdit;

    String filepath = "";
    private Context context;

    final String TAG = "dabai";


    private boolean is_save = false;
    private FloatingActionButton fab;
    private MarkdownView mMarkdownView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_file();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                hideInput();
                super.onDrawerOpened(drawerView);

            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();
        /**
         * ...
         */

        init_val();
        init();


    }


    /**
     * 初始化逻辑
     */
    private void init() {

        //检查权限
        checkPermissio();

        //初始化md编译器
        RichText.initCacheDir(this);

        //检测屏幕高度 设置编辑框高度
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight() - 200;
        view_edit.setMinHeight(height);

        //编辑框监听
        view_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable editable) {
                is_save = false;
                f5();
                fab.setVisibility(View.VISIBLE);
            }
        });


        //判断工具栏 有没有打开 恢复他的状态
        if (get_sharedString("tipscard", "显示").equals("显示")) {
            tipscard.setVisibility(View.VISIBLE);
        } else {
            tipscard.setVisibility(View.GONE);
        }


        filepath = get_filepath();

        if (filepath.isEmpty()) {
            setTitle("未命名");
        } else {
            try {
                open_file(filepath);
                Snackbar.make(cons, "工作状态已恢复", Snackbar.LENGTH_SHORT).show();

            } catch (Exception e) {
                Snackbar.make(cons, "工作状态异常，已默认新建", Snackbar.LENGTH_SHORT).show();
                new_file();
            }
        }

    }


    /**
     * 保存文件
     */
    @SuppressLint("RestrictedApi")
    private void save_file() {

        if (get_filepath().isEmpty() || !new File(get_filepath()).exists()) {
            //如果没有打开文件
            new MaterialDialog.Builder(this)
                    .title("另存到文件")
                    .content("文件会默认保存到" + get_sharedString("workdir", "/sdcard/MarkdownQ/"))
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("文件名", null, new MaterialDialog.InputCallback() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            try {
                                filepath = get_sharedString("workdir", "/sdcard/MarkdownQ/") + input + ".md";
                                new FileUtils().writeText(filepath, view_edit.getText().toString(), true);
                                setTitle(new File(filepath).getName());
                                change_filepath(filepath);
                                is_save = true;
                                fab.setVisibility(View.GONE);
                            } catch (IOException e) {
                                Snackbar.make(cons, "保存失败！", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .positiveText("保存")
                    .show();
        } else {
            //打开了呢？？？？？？？？？
            try {
                new FileUtils().writeText(filepath, view_edit.getText().toString(), true);
                Snackbar.make(cons, "保存成功！", Snackbar.LENGTH_SHORT).show();
                change_filepath(filepath);
                is_save = true;
                fab.setVisibility(View.GONE);
            } catch (IOException e) {
                Snackbar.make(cons, "保存失败！", Snackbar.LENGTH_SHORT).show();
                change_filepath(filepath);
                is_save = true;
            }
        }

    }


    /**
     * 检查保存了没
     *
     * @return
     */
    private boolean checktext() {
        if (getTitle().equals("未命名")) {
            return true;
        } else {
            if (is_save) {
                return true;
            }
        }
        return false;
    }


    /**
     * 打开文件
     *
     * @param filepath
     * @throws Exception
     */
    private void open_file(String filepath) throws Exception {
        //恢复工作状态
        setTitle(new File(filepath).getName());
        String edittext = FileUtils.read_file(filepath);
        view_edit.setText(edittext);
        view_edit.setSelection(view_edit.getText().length());
        is_save = true;
        change_filepath(filepath);

    }


    /**
     * 新建文件
     */
    private void new_file() {
        setTitle("未命名");
        filepath = "";
        change_filepath(filepath);
        view_edit.setText("");

    }


    /**
     * 权限检查以及创建工作空间
     */
    private void checkPermissio() {

        //检查代码是否拥有这个权限
        int checkResult = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //if(!=允许),抛出异常
        if (checkResult != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1); // 动态申请读取权限
            }
        } else {
            createWorkDir();
        }
    }


    /**
     * 创建工作目录
     */
    private void createWorkDir() {

        File file = new File(get_sharedString("workdir", "/sdcard/MarkdownQ/"));
        if (!file.exists()) {
            if (file.mkdirs()) {
                Snackbar.make(cons, "创建工作目录成功", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(cons, "创建工作目录失败，程序异常", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void f5() {
        te2.setText("字数：" + view_edit.getText().length());
    }


    /***
     * 改变正在工作的文件路径
     */

    public void change_filepath(String filepath) {
        set_sharedString("filepath", filepath);
    }

    /**
     * 获取正在工作的文件路径
     *
     * @return
     */
    public String get_filepath() {
        return get_sharedString("filepath", "");
    }


    /**
     * 初始化界面
     */
    private void init_val() {


        LayoutInflater inflater = getLayoutInflater();


        viewPager = findViewById(R.id.viewpager);

        view1 = inflater.inflate(R.layout.pages_edit, null);
        view2 = inflater.inflate(R.layout.pages_result, null);

        cons = findViewById(R.id.cons);
        tipscard = view1.findViewById(R.id.tipscard);
        view_edit = view1.findViewById(R.id.editText);
        te2 = view1.findViewById(R.id.textView2);
        mMarkdownView = (MarkdownView)view2.findViewById(R.id.markdownview);


        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);

        mPerformEdit = new PerformEdit(view_edit);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //下面三个回调缺一不可，否则就会编译报错
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    String text = view_edit.getText().toString();
                    if (text.isEmpty()) {
                        //Snackbar.make(cons, "无文本，不转换", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:

                        String text = view_edit.getText().toString();

                        mMarkdownView.addStyleSheet(new Github());
                        mMarkdownView.loadMarkdown(text);


                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:

                        try {

                            hideInput();

                        } catch (Exception e) {
                            // Snackbar.make(cons, "异常：" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        break;
                }
            }
        });

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }


    /**
     * 隐藏输入法
     */
    public void hideInput() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 创建 右上角菜单那
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                //撤销
                mPerformEdit.undo();
                break;
            case R.id.go:
                //重做
                mPerformEdit.redo();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    MaterialDialog file_choose_dialog;
    int longoncount;
    ListView lv;

    private void showFileChooser() {

        final ArrayList<String> data = new ArrayList<>();
        final String dir_path = get_sharedString("workdir", "/sdcard/MarkdownQ/");


        for (String filename : new File(dir_path).list()) {
            if (filename.endsWith(".md") || filename.endsWith(".MD")) {
                data.add(filename);
            }
        }


        View view = getLayoutInflater().inflate(R.layout.dialog_filechoose, null);

        file_choose_dialog = new MaterialDialog.Builder(this)
                .title(new File(dir_path).getName())
                .customView(view, false)
                .positiveText("关闭")
                .show();

        if (data.size() != 0) {
            view.findViewById(R.id.filecte).setVisibility(View.GONE);
        }

        lv = view.findViewById(R.id.lv);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = new File(new File(dir_path), data.get(i)).getAbsolutePath();
                try {
                    open_file(path);
                    file_choose_dialog.dismiss();
                } catch (Exception e) {
                    file_choose_dialog.dismiss();
                    Snackbar.make(cons, "打开失败：" + "找不到文件", Snackbar.LENGTH_LONG).show();
                }
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                longoncount = i;

                PopupMenu popup = new PopupMenu(file_choose_dialog.getContext(), view);
                popup.inflate(R.menu.file_choose_popmenu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String path = new File(new File(dir_path), data.get(longoncount)).getAbsolutePath();
                        file_choose_dialog.dismiss();
                        switch (item.getItemId()) {
                            case R.id.pop_delete:
                                data.remove(longoncount);
                                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, data);
                                lv.setAdapter(adapter);


                                final File file = new File(path);

                                File del_history_dir = new File("/sdcard/.MarkdownQ_del/");
                                final File delfile = new File(del_history_dir, file.getName());

                                if (!del_history_dir.exists()) {
                                    del_history_dir.mkdirs();

                                    boolean a = file.renameTo(delfile);
                                    if (a) {
                                        Snackbar.make(cons, "1个文件已经被移动到回收站", Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                delfile.renameTo(file);
                                            }
                                        }).show();
                                    } else {
                                        Snackbar.make(cons, "归档失败！", Snackbar.LENGTH_LONG).show();
                                    }

                                } else {
                                    boolean a = file.renameTo(delfile);
                                    if (a) {
                                        Snackbar.make(cons, "1个文件已经被移动到回收站", Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                delfile.renameTo(file);
                                            }
                                        }).show();
                                    } else {
                                        Snackbar.make(cons, "归档失败！", Snackbar.LENGTH_LONG).show();
                                    }

                                }

                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.newfile) {
            if (checktext()) {
                new_file();
            } else {
                new MaterialDialog.Builder(this)
                        .title("新建空文档")
                        .content("你有文件未保存，如果继续操作可能会丢失文本！")
                        .positiveText("保存并继续")
                        .neutralText("不在乎")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                // TODO

                                if (get_filepath().isEmpty() || !new File(get_filepath()).exists()) {
                                    //如果没有打开文件
                                    new MaterialDialog.Builder(MainActivity.this)
                                            .title("另存到文件")
                                            .content("文件会默认保存到" + get_sharedString("workdir", "/sdcard/MarkdownQ/"))
                                            .inputType(InputType.TYPE_CLASS_TEXT)
                                            .input("文件名", null, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                                    try {
                                                        filepath = get_sharedString("workdir", "/sdcard/MarkdownQ/") + input + ".md";
                                                        new FileUtils().writeText(filepath, view_edit.getText().toString(), true);
                                                        setTitle(new File(filepath).getName());
                                                        new_file();
                                                    } catch (IOException e) {
                                                        Snackbar.make(cons, "保存失败！", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .positiveText("保存")
                                            .show();
                                } else {
                                    //打开了呢？？？？？？？？？
                                    try {
                                        new FileUtils().writeText(filepath, view_edit.getText().toString(), true);
                                        Snackbar.make(cons, "保存成功！", Snackbar.LENGTH_SHORT).show();

                                        new_file();
                                    } catch (IOException e) {
                                        Snackbar.make(cons, "保存失败！", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                // TODO
                                new_file();
                            }
                        })
                        .show();
            }
        } else if (id == R.id.openfile) {
            try {
                showFileChooser();
            } catch (Exception e) {
                Snackbar.make(cons, "工作空间异常，请重启软件", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.helptext) {
            startActivity(new Intent(this, HelpActivity.class));

        } else if (id == R.id.onofftools) {

            if (tipscard.getVisibility() == View.GONE) {
                tipscard.setVisibility(View.VISIBLE);
                set_sharedString("tipscard", "显示");
            } else {
                tipscard.setVisibility(View.GONE);
                set_sharedString("tipscard", "不显示");
            }

        } else if (id == R.id.sharetext) {

            new MaterialDialog.Builder(this)
                    .title("选择分享方式")
                    .items(new String[]{"分享文本(text)", "分享MD文件(.md)", "分享html文件(.html)", "分享图片(.png)"})
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    new DabaiUtils().sendText(context, view_edit.getText().toString());
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    break;
                            }
                        }
                    })
                    .show();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this, FeedActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void to_imglink(View view) {
        new DabaiUtils().openLink(this, "https://www.coolapk.com/picture/13194328?shareKey=NmY0MmM0OTQ3ZmIxNWQ0ZmU4NDc~&shareUid=1049606&shareFrom=com.coolapk.market_9.4.1");
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

}
