package cn.xutils.plugin;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "MainHook";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.v(TAG, "handleLoadPackage... " + lpparam.packageName);
        Class like = lpparam.classLoader.loadClass("com.tencent.wework.launch.WwApplicationLike");

        Log.v(TAG, "handleLoadPackage... like: " + like);
        XposedHelpers.findAndHookMethod(like,
                "onCreate", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam<?> param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.v(TAG, "WwApplicationLike.onCreate beforeHookedMethod");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam<?> param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.v(TAG, "WwApplicationLike.onCreate afterHookedMethod");
                    }
                }
        );
    }
}
