package com.dabai.markdownq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.bitmap.BitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.dabai.markdownq.activity.FeedActivity;
import com.dabai.markdownq.activity.HelpActivity;
import com.dabai.markdownq.activity.SettingsActivity;
import com.dabai.markdownq.utils.DabaiUtils;
import com.dabai.markdownq.utils.FileUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.wildma.pictureselector.PictureSelector;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import okhttp3.Call;
import ren.qinc.edit.PerformEdit;

/**
 * //  ┏┓　　　┏┓
 * //┏┛┻━━━┛┻┓━━━━━━━
 * //┃　　　　　　　   ┃
 * //┃　　　━　　　   ┃
 * //┃　┳┛　  ┗┳　  ┃
 * //┃　　　　　　　 ┃
 * //┃　　　┻　　　 ┃
 * //┃　　　　　　 ┃
 * //┗━┓　　　  ┏━┛
 * //    ┃　　　┃   神兽保佑
 * //    ┃　　　┃   代码无BUG！
 * //    ┃　　　┗━━━┓
 * //    ┃　　　　　┣┓
 * //    ┃　　　　 ┏┛
 * //    ┗┓┓┏━┳┓┏┛
 * //      ┃┫┫　┃┫┫
 * //      ┗┻┛　┗┻┛
 **/


/**
 * //
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                           O\  =  /O
 * //                        ____/`---'\____
 * //                      .'  \\|     |//  `.
 * //                     /  \\|||  :  |||//  \
 * //                    /  _||||| -:- |||||-  \
 * //                    |   | \\\  -  /// |   |
 * //                    | \_|  ''\---/''  |   |
 * //                    \  .-\__  `-`  ___/-. /
 * //                  ___`. .'  /--.--\  `. . __
 * //               ."" '<  `.___\_<|>_/___.'  >'"".
 * //              | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * //              \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 **/


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View view1, view2;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组
    EditText view_edit;
    ConstraintLayout cons;

    ScrollView scr_edit;
    ScrollView scr_res;
    TextView te2, res_textview;
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
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    hideInput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                save_file();
                viewPager.setCurrentItem(1);
                return true;
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
                try {
                    hideInput();
                } catch (Exception e) {

                }
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();

        init_val();
        init();


        /**
         * 引导动画
         */

        IntroView(fab, "1", "这里是保存按钮，每次更改完记得保存哦，不然内容丢失可不负责呦\n单击：立即保存\n长按：立即保存并预览");


    }

    private void IntroView(View v, String id, String text) {

        new MaterialIntroView.Builder(this)
                .enableDotAnimation(false)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .setDelayMillis(200)
                .setTargetPadding(30)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(text)
                .setTarget(v)
                .setUsageId(id) //THIS SHOULD BE UNIQUE ID
                .show();
    }


    /**
     * 初始化逻辑
     */
    @SuppressLint("NewApi")
    private void init() {

        mMarkdownView = view2.findViewById(R.id.markdownview);

        //检查权限
        checkPermissio();

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
        if (get_sharedString("tipscard", "不显示").equals("显示")) {
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

        initTheme();
        f5();

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();

        view_edit.setTextSize(Float.parseFloat(get_sharedString("textsize", "18")));

        if (get_sharedString("scr_some", "同步滚动").equals("同步滚动")) {
            scr_edit.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    scr_res.setScrollY(i1);
                }
            });
            scr_res.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    scr_edit.setScrollY(i1);
                }
            });
        }
    }

    private void initTheme() {

        String theme = get_sharedString("theme", "日");
        if (theme.equals("日")) {

        } else {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setBackgroundColor(Color.parseColor("#607D8B"));

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#607D8B")));
            view_edit.setBackgroundColor(Color.parseColor("#90a4ae"));
            view1.setBackgroundColor(Color.parseColor("#90a4ae"));
            //view2.setBackgroundColor(Color.parseColor("#90a4ae"));

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

        view_edit.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto1-Regular.ttf"));


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
        scr_edit = view1.findViewById(R.id.scr_edit);
        scr_res = view2.findViewById(R.id.scr_res);

        te2 = view1.findViewById(R.id.textView2);


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
                    fab.setVisibility(View.GONE);
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
    //设置返回按钮：不应该退出程序---而是返回桌面
    //复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        mPerformEdit.clearHistory();
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

    private int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }


    private void f5() {
        te2.setText("当前字数：" + view_edit.getText().length() + "   总行数:" + view_edit.getText().toString().split("\n").length);


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
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void c(String assetsFileName, String OutFileName) throws IOException {
        File f = new File(OutFileName);
        if (f.exists())
            f.delete();
        f = new File(OutFileName);
        f.createNewFile();
        InputStream I = getAssets().open(assetsFileName);
        OutputStream O = new FileOutputStream(OutFileName);
        byte[] b = new byte[1024];
        int l = I.read(b);
        while (l > 0) {
            O.write(b, 0, l);
            l = I.read(b);
        }
        O.flush();
        I.close();
        O.close();
    }


    /**
     * 隐藏输入法
     */
    public void hideInput() throws Exception {
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
            case R.id.see:
                try {
                    hideInput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (viewPager.getCurrentItem() == 1){
                    Snackbar.make(cons,"已经在预览模式了",Snackbar.LENGTH_SHORT).show();

                }else {
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.undo:
                //撤销
                mPerformEdit.undo();

                mMarkdownView.loadMarkdown(view_edit.getText().toString());
                break;
            case R.id.go:
                //重做
                mPerformEdit.redo();
                mMarkdownView.loadMarkdown(view_edit.getText().toString());

                break;
            case R.id.tools:


                if (tipscard.getVisibility() == View.GONE) {
                    tipscard.setVisibility(View.VISIBLE);
                    set_sharedString("tipscard", "显示");
                    IntroView(tipscard, "2", "这里是工具栏，可以查看统计和使用快捷按钮");
                } else {
                    tipscard.setVisibility(View.GONE);
                    set_sharedString("tipscard", "不显示");
                }

                viewPager.setCurrentItem(0);
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

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_theme) {

            String theme = get_sharedString("theme", "日");
            Log.d(TAG, "onNavigationItemSelected: " + theme);
            if (theme.equals("日")) {
                set_sharedString("theme", "夜");
                Snackbar.make(cons, "主题更新 - 夜，请重启软件", Snackbar.LENGTH_LONG).setAction("重启", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                    }
                }).show();
            } else {
                set_sharedString("theme", "日");
                Snackbar.make(cons, "主题更新 - 日，请重启软件", Snackbar.LENGTH_LONG).setAction("重启", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                    }
                }).show();
            }

        } else if (id == R.id.onofftools) {


        } else if (id == R.id.sharetext) {

            new MaterialDialog.Builder(this)
                    .title("选择分享方式")
                    .items(new String[]{"分享文本(text)", "分享MD文件(.md)", "分享图片(.png)"})
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    if (view_edit.getText().toString().isEmpty()) {
                                        Snackbar.make(cons, "编辑框为空，不能分享", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        new DabaiUtils().sendText(context, view_edit.getText().toString());
                                    }
                                    break;
                                case 1:

                                    break;
                                case 2:
                                    shareImage();
                                    break;
                            }
                        }
                    })
                    .show();

        } else if (id == R.id.nav_share) {


            View view = getLayoutInflater().inflate(R.layout.dialog_shareapp, null);
            new MaterialDialog.Builder(this)
                    .title("分享软件")
                    .customView(view, true)
                    .show();

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this, FeedActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 分享图片
     */
    private void shareImage() {
        if (view_edit.getText().toString().isEmpty()) {
            Snackbar.make(cons, "编辑框为空，不能分享", Snackbar.LENGTH_SHORT).show();
        } else {
            showProgress("正在处理", "程序正在进行图片处理，马上就好");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text1 = view_edit.getText().toString();
                            mMarkdownView.addStyleSheet(new Github());
                            mMarkdownView.loadMarkdown(text1);
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                    md.dismiss();
                    Bitmap bitmap = getBitmapByView(mMarkdownView);//iv是View
                    shareSingleImage(bitmap);
                }
            }).start();

        }
    }

    MaterialDialog md;

    public void showProgress(String title, String text) {
        md = new MaterialDialog.Builder(this)
                .title(title)
                .cancelable(false)
                .content(text)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .positiveText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        md.dismiss();
                    }
                })
                .show();
    }

    //根据view获取bitmap
    public static Bitmap getBitmapByView(View view) {
        int h = 0;
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //检查sd
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //分享单张图片
    public void shareSingleImage(Bitmap bitmap) {
        //由文件得到uri
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
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


    /**
     * 光标处插入文本
     *
     * @param text
     */
    public void insert_text(String text) {
        int index = view_edit.getSelectionStart();
        Editable editable = view_edit.getText();
        editable.insert(index, text);

    }


    /**
     * 下面是工具栏的小可爱们施展技能的地方
     *
     * @param view
     */

    public void ins_date(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        insert_text(dateFormat.format(new Date()));
    }


    public void ins_title(View view) {
        new MaterialDialog.Builder(this)
                .title("插入大标题")
                .items(new String[]{"h1", "h2", "h3", "h4", "h5", "h6"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        checkKeyWord();
                        switch (which) {
                            case 0:
                                insert_text("# ");
                                break;
                            case 1:
                                insert_text("## ");
                                break;
                            case 2:
                                insert_text("### ");
                                break;
                            case 3:
                                insert_text("#### ");
                                break;
                            case 4:
                                insert_text("##### ");
                                break;
                            case 5:
                                insert_text("###### ");
                                break;
                        }
                    }
                })
                .show();
    }


    public void ins_ul(View view) {
        checkKeyWord();
        insert_text("- ");
    }

    public void ins_ui(View view) {
        try {
            String starttext = view_edit.getText().toString().split("\n")[getCurrentCursorLine(view_edit) - 1];
            if (starttext.contains(".")) {
                insert_text("\n");
            }
            String num = starttext.substring(0, starttext.indexOf("."));
            insert_text((Integer.parseInt(num) + 1) + ". ");
        } catch (Exception e) {
            insert_text("1. ");
        }
    }


    public void ins_link(View view) {

        final View diaview = getLayoutInflater().inflate(R.layout.dialog_inslink, null);
        new AlertDialog.Builder(this)
                .setTitle("插入链接")
                .setView(diaview)
                .setPositiveButton("插入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextInputLayout til1 = diaview.findViewById(R.id.til1);
                        TextInputLayout til2 = diaview.findViewById(R.id.til2);

                        String text = til1.getEditText().getText().toString();
                        String link = til2.getEditText().getText().toString();

                        checkKeyWord();

                        insert_text("[" + text + "](" + link + ")");
                    }
                })
                .show();

    }

    private void checkKeyWord() {
        try {
            String starttext = view_edit.getText().toString().split("\n")[getCurrentCursorLine(view_edit) - 1];


            if (starttext.contains("#") ||
                    starttext.contains("-")
                    || starttext.contains(".")
                    || starttext.contains(">")
                    || starttext.contains("*")
                    || starttext.contains("[")
                    || starttext.contains("(")
                    || starttext.contains("`")
                    || starttext.contains("/")
                    || starttext.contains("_")) {
                insert_text("\n");
            }


        } catch (Exception e) {
            Log.d(TAG, "checkKeyWord: " + e);
        }
    }

    public void ins_codes(View view) {
        checkKeyWord();
        insert_text("```\n\n```\n");
        view_edit.setSelection(view_edit.getSelectionStart() - 5);
    }


    public void ins_bold(View view) {
        checkKeyWord();
        insert_text("****");
        view_edit.setSelection(view_edit.getSelectionStart() - 2);
    }

    public void ins_quote(View view) {
        checkKeyWord();
        insert_text("> ");
    }

    public void ins_fengeline(View view) {
        try {
            String starttext = view_edit.getText().toString().split("\n")[getCurrentCursorLine(view_edit) - 1];
            if (!starttext.isEmpty()) {
                insert_text("\n");
            }
        } catch (Exception e) {
        }
        insert_text("-------\n");

    }

    public void ins_zhong(View view) {
        insert_text("``");
        view_edit.setSelection(view_edit.getSelectionStart() - 1);
    }

    public void ins_delline(View view) {
        insert_text("~~~~");
        view_edit.setSelection(view_edit.getSelectionStart() - 2);
    }

    public void ins_pic(View view) {

        /**
         * 图库选择  可裁剪
         *
         * */
        PictureSelector
                .create(MainActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(false, 200, 200, 1, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                final String picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
                new MaterialDialog.Builder(this)
                        .title("选择链接类型")
                        .items(new String[]{"本地链接", "网络链接"})
                        .cancelable(false)
                        .positiveText("取消")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        File file = new File(picturePath);
                                        checkKeyWord();
                                        insert_text("![" + file.getName() + "](file://" + file.getAbsolutePath() + ")");

                                        break;

                                    case 1:
                                        //上传到图床！！！！！！

                                        upload_pic(picturePath);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        }
    }

    private void upload_pic(String picturePath) {
        showProgress("正在上传图片", "正在把图片上传到图床，请稍等");
        final File picfile = new File(picturePath);

        String url = "https://sm.ms/api/upload";
        String ua = "Mozilla/5.0 (Linux; Android ; M5 Build/MRA58K) MarkdownQ/1.0";

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("User-Agent", ua);

        OkHttpUtils.post()
                .url(url)
                .headers(header)
                .addFile("smfile", picfile.getName(), picfile)
                .addParams("ssl", "true")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        md.dismiss();
                        Snackbar.make(cons, "上传失败"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        JSONObject requestjson = null;
                        try {
                            md.dismiss();
                            requestjson = new JSONObject(s);
                            JSONObject datajson = requestjson.getJSONObject("data");
                            String link = datajson.getString("url");
                            checkKeyWord();
                            insert_text("![" + picfile.getName() + "](" + link.replace("\\","") + ")");

                        } catch (Exception e) {
                            md.dismiss();
                            Snackbar.make(cons, "上传失败:"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void share_downlink(View view) {
        new DabaiUtils().sendText(context, "推荐应用 【MarkdownQ】： https://www.coolapk.com/apk/com.dabai.markdownq  分享自【MarkdownQ】 ");
    }
}
