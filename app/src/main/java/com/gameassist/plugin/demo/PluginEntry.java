package com.gameassist.plugin.demo;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.gameassist.plugin.Plugin;

public class PluginEntry extends Plugin {
    PluginManagerView pluginManagerView;
    public static PluginEntry instance;

    @Override
    public boolean OnPluginCreate() {
        System.loadLibrary("demo");
        try {
            NativeUtils.nativeInit(targetApplication.getPackageManager().getPackageInfo(targetApplication.getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        instance = this;
        targetApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                pluginManagerView = new PluginManagerView(activity);
                pluginManagerView.onActivityCreate(activity, bundle);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (pluginManagerView != null) {
                    pluginManagerView.onActivityResumed(activity);
                } else {
                    pluginManagerView = new PluginManagerView(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (pluginManagerView != null) {
                    pluginManagerView.onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (pluginManagerView != null) {
                    pluginManagerView.onActivityDestroy(activity);
                }
            }
        });
        return false;
    }

    @Override
    public void OnPlguinDestroy() {

    }

    @Override
    public View OnPluginUIShow() {
        return null;
    }

    @Override
    public void OnPluginUIHide() {

    }

    @Override
    public boolean pluginAutoHide() {
        return true;
    }
}
