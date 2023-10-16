package cn.xutils.boxposed.app.hook;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hooker {

    private static final String TAG = "Hooker";

    static {
        Init.init();
    }

    public static void doTestHook() {
        XposedHelpers.findAndHookMethod(Hooker.class, "doSomeThing", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam<?> param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.v(TAG, "beforeHookedMethod...");
                param.setResult("no");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam<?> param) throws Throwable {
                super.afterHookedMethod(param);
                Log.v(TAG, "afterHookedMethod...");
            }
        });

        Hooker hooker = new Hooker();
        String result = hooker.doSomeThing();
        Log.v(TAG, "result = " + result);
    }

    public String doSomeThing() {
        System.out.println("doSomeThing...");
        return "yes";
    }
}
