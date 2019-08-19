package com.dabai.markdownq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EditText1 extends AppCompatEditText {


    public EditText1(Context context) {
        super(context);
    }

    public EditText1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditText1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectHandleDisabled();
                    }
                }, 50); // 延迟50ms，等room显示handle完后，再隐藏
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    private void setSelectHandleDisabled() {
        try {
            Field mEditor = EditText.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            Object object = mEditor.get(this);
            Class mClass = Class.forName("android.widget.Editor");
            // 选中时handle
            Method selectionController = mClass.getDeclaredMethod("getSelectionController");
            selectionController.setAccessible(true);
            Object invokeSelect = selectionController.invoke(object);
            Method hideSelect = invokeSelect.getClass().getDeclaredMethod("hide");
            hideSelect.invoke(invokeSelect);
            // 插入时handle
            Method insertionController = mClass.getDeclaredMethod("getInsertionController");
            insertionController.setAccessible(true);
            Object invokeInsert = insertionController.invoke(object);
            Method hideInsert = invokeInsert.getClass().getDeclaredMethod("hide");
            hideInsert.invoke(invokeInsert);
        } catch (Exception e) {

        }

    }


}
