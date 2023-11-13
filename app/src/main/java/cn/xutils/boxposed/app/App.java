package cn.xutils.boxposed.app;

import android.app.Application;

import cn.xutils.boxposed.api.PatchUtils;

public class App extends Application {

    static {
        PatchUtils.init();
    }
}
