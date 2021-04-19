package com.gameassist.plugin.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.gameassist.plugin.mod.demo.R;

public class PluginManagerView extends LinearLayout implements View.OnTouchListener {

    private Button main_play;
    public WindowManager wm;

    private PluginManagerViewHandler handler;
    private static Integer prevX = null, prevY = null;
    private Context selfContext;
    private Context targetContext;
    private boolean resumed = false;
    private boolean failAdd = false;
    public Activity currentActivity;
    public WindowManager.LayoutParams layoutParams;
    private WindowManager sysWindowManager;
    private static final int MSG_SHOW_PLUGINMANAGER = 0;
    public static FrameLayout subFrame;

    private class PluginManagerViewHandler extends Handler {

        PluginManagerViewHandler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //开启悬浮框
                case MSG_SHOW_PLUGINMANAGER:
                    if (currentActivity == null)
                        return;
                    if (!resumed || currentActivity.getWindow().getDecorView().getWidth() == 0) {
                        sendEmptyMessageDelayed(MSG_SHOW_PLUGINMANAGER, 500);
                        return;
                    }
                    try {
                        wm = currentActivity.getWindowManager();
                        if (failAdd) {
                            if (targetContext.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    currentActivity.getPackageManager().getPackageInfo("miui", 0);
                                } catch (Exception ignored) {
                                }
                            } else {
                            }
                            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                            wm = sysWindowManager;
                        }
                        wm.addView(PluginManagerView.this, layoutParams);
                    } catch (Exception ee) {
                        if (!failAdd) {
                            failAdd = true;
                            sendEmptyMessageDelayed(MSG_SHOW_PLUGINMANAGER, 500);
                            return;
                        }
                    }
                    if (prevX != null && prevY != null) {
                        WindowManager.LayoutParams p = (WindowManager.LayoutParams) getLayoutParams();
                        p.x = prevX;
                        p.y = prevY;
                        updateLayout(p);
                    }
                    setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    public PluginManagerView(Context context) {
        super(context);
        KeypadClickListener listener = new KeypadClickListener(this);
        this.selfContext = PluginEntry.instance.getContext();
        this.targetContext = PluginEntry.instance.getTargetApplication();
        sysWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        handler = new PluginManagerViewHandler(Looper.getMainLooper());
        LayoutInflater.from(selfContext).inflate(R.layout.plugin_view, this);
        PluginManagerView.this.setOnTouchListener(this);
        main_play = (Button) findViewById(R.id.main_play);
        subFrame = (FrameLayout) findViewById(R.id.subFrame);

        main_play.setOnClickListener(listener);
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            // layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        }
        updateLayout(layoutParams);

    }

    private int touchX, touchY, startX, startY;
    private boolean isDraged = false;
    private int x, y;
    private int fullWidth, fullHeight;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        touchX = (int) event.getRawX();
        touchY = (int) event.getRawY();
        boolean isHandle = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fullWidth = currentActivity.getWindow().getDecorView().getRight() - currentActivity.getWindow().getDecorView().getLeft();
                fullHeight = currentActivity.getWindow().getDecorView().getBottom() - currentActivity.getWindow().getDecorView().getTop();
                isDraged = false;
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                x = layoutParams.x;
                y = layoutParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDraged) {
                    updateLayout(x, y, touchX - startX, touchY - startY, fullWidth, fullHeight);
                } else if (Math.abs(touchX - startX) >= v.getWidth() / 4 || Math.abs(touchY - startY) >= v.getHeight() / 4) {
                    isDraged = true;
                }
                isHandle = isDraged;
                break;
            case MotionEvent.ACTION_UP:
                isHandle = isDraged;
                isDraged = false;
                break;
        }
        return isHandle;
    }


    public void onActivityCreate(Activity activity, Bundle bundle) {
        currentActivity = activity;
    }

    public void onActivityResumed(Activity activity) {
        resumed = true;
        failAdd = false;
        currentActivity = activity;
        handler.sendEmptyMessageDelayed(MSG_SHOW_PLUGINMANAGER, 1000);
    }

    public void onActivityPaused(Activity activity) {
        resumed = false;
        handler.removeCallbacksAndMessages(null);
        wm = currentActivity.getWindowManager();
        if (layoutParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) {
            wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        try {
            wm.removeView(this);
        } catch (Exception e) {
        }
    }

    public void onActivityDestroy(Activity activity) {
        handler.removeCallbacksAndMessages(null);
    }


    private void updateLayout(int startX, int startY, int offsetX, int offsetY, int width, int height) {
        prevX = layoutParams.x = Math.min(width, Math.max(0, offsetX + startX));
        prevY = layoutParams.y = Math.min(height, Math.max(0, offsetY + startY));
        updateLayout(layoutParams);
    }

    private void updateLayout(WindowManager.LayoutParams lp) {
        setLayoutParams(lp);
        if (currentActivity != null) {
            try {
                WindowManager wm = currentActivity.getWindowManager();
                if (layoutParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) {
                    wm = (WindowManager) currentActivity.getSystemService(Context.WINDOW_SERVICE);
                }
                wm.updateViewLayout(PluginManagerView.this, lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.layoutParams = (WindowManager.LayoutParams) getLayoutParams();
        }
    }
}
