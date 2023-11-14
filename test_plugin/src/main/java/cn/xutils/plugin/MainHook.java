package cn.xutils.plugin;

import android.util.Log;

import cn.xutils.boxposed.api.IXposedHookLoadPackage;
import cn.xutils.boxposed.api.XC_MethodHook;
import cn.xutils.boxposed.api.XposedHelpers;
import cn.xutils.boxposed.api.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "MainHook";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.v(TAG, "handleLoadPackage... " + lpparam.packageName);
        Class like = XposedHelpers.findClass("com.tencent.wework.launch.WwApplicationLike", lpparam.classLoader);
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
