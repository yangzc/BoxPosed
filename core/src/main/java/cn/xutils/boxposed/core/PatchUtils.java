package cn.xutils.boxposed.core;

import static de.robv.android.xposed.XposedHelpers.findField;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;

import cn.xutils.boxposed.core.utils.FileUtils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PatchUtils {

    private static final String TAG = "PatchUtils";
    private static final String PLUGIN_APP = "cn.xutils.plugin";

    public static void init() {
        Context context = createAppContext();
        try {
            try {
                // replace signature
                String signature = FileUtils.getText(context.getAssets().open("signature"));
                killPmm(context.getPackageName(), signature);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            PackageInfo wwPackageInfo = context.getPackageManager().getPackageInfo("com.tencent.wework", PackageManager.GET_SIGNATURES);
//            String wwSignature = Base64.encodeToString(wwPackageInfo.signatures[0].toByteArray(), Base64.DEFAULT);
//            Log.v(TAG, "ww signature: " + wwSignature);
//            ApplicationInfo applicationInfo = wwPackageInfo.applicationInfo;
//            Log.v(TAG, "applicationInfo: " + applicationInfo.sourceDir);
            Context wxContext = context.createPackageContext(PLUGIN_APP,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            try(InputStream is = wxContext.getAssets().open("init_entry")) {
                FileUtils.copy(is, bas);
            } catch (IOException e) {
            }
            String entryClass = new String(bas.toByteArray(), Charset.defaultCharset());
            if (!TextUtils.isEmpty(entryClass)) {
                XC_LoadPackage.LoadPackageParam param = new XC_LoadPackage.LoadPackageParam(null);
                param.packageName = context.getPackageName();
                param.processName = getCurProcessName(context);
                param.classLoader = PatchUtils.class.getClassLoader();
                param.appInfo = context.getApplicationInfo();
                param.isFirstApplication = isMainProcess(context);

                Class<IXposedHookLoadPackage> applicationCls = (Class<IXposedHookLoadPackage>)
                        wxContext.getClassLoader().loadClass(entryClass);
                XposedHelpers.callStaticMethod(applicationCls, "handleLoadPackage", param);
            }
//            Log.v(TAG, "appClz: " + wxContext.getClassLoader());
//            Class applicationCls = wxContext.getClassLoader()
//                    .loadClass("com.tencent.wework.launch.WwApplication");
//            Log.v(TAG, "appInst: " + applicationCls.newInstance());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Context createAppContext() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);

            Object activityThreadObj = currentActivityThreadMethod.invoke(null);

            Field boundApplicationField = activityThreadClass.getDeclaredField("mBoundApplication");
            boundApplicationField.setAccessible(true);
            Object mBoundApplication = boundApplicationField.get(activityThreadObj);   // AppBindData

            Field infoField = mBoundApplication.getClass().getDeclaredField("info");   // info
            infoField.setAccessible(true);
            Object loadedApkObj = infoField.get(mBoundApplication);  // LoadedApk

            Class contextImplClass = Class.forName("android.app.ContextImpl");
            Method createAppContextMethod = contextImplClass.getDeclaredMethod("createAppContext", activityThreadClass, loadedApkObj.getClass());
            createAppContextMethod.setAccessible(true);

            Object context = createAppContextMethod.invoke(null, activityThreadObj, loadedApkObj);

            if (context instanceof Context) {
                return (Context) context;
            }

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String sCurProcessName = null;

    public static String getCurProcessName(Context context) {
        String procName = sCurProcessName;
        if (procName != null && !procName.isEmpty()) {
            return procName;
        }
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    Log.d("Process", "processName = " + appProcess.processName);
                    sCurProcessName = appProcess.processName;
                    return sCurProcessName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sCurProcessName = getCurProcessNameFromProc();
        return sCurProcessName;
    }

    private static String getCurProcessNameFromProc() {
        BufferedReader cmdlineReader = null;
        try {
            cmdlineReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(
                            "/proc/" + android.os.Process.myPid() + "/cmdline"),
                    "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = cmdlineReader.read()) > 0) {
                processName.append((char) c);
            }
            Log.d("Process", "get processName = " + processName.toString());
            return processName.toString();
        } catch (Throwable e) {
            // ignore
        } finally {
            if (cmdlineReader != null) {
                try {
                    cmdlineReader.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return null;
    }

    public static boolean isMainProcess(Context context) {
        String processName = getCurProcessName(context);
        if (processName != null && processName.contains(":")) {
            return false;
        }
        return (processName != null && processName.equals(context.getPackageName()));
    }

    private static void killPmm(String packageName, String signatureData) {
        try {
            //正确的签名
            Signature fakeSignature = new Signature(Base64.decode(signatureData,Base64.DEFAULT));
            //1. 获取原包装 , 在 IPackageManager.Stub.Proxy 中 实际 获取签名就是这个
            Parcelable.Creator<PackageInfo> originalCreator = PackageInfo.CREATOR;
            //2.咋们创建一个我们自己的
            Parcelable.Creator<PackageInfo> creator = new Parcelable.Creator<PackageInfo>() {
                @Override
                public PackageInfo createFromParcel(Parcel source) {
                    //3.从原包装创建 packageInfo
                    PackageInfo packageInfo = originalCreator.createFromParcel(source);
                    Log.v(TAG, "packageInfo::" + packageInfo.packageName);
                    if (packageInfo.packageName.equals(packageName)) {
                        if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                            //4.把当前改过的签名 修改成以前 正确的签名
                            packageInfo.signatures[0] = fakeSignature;
                        }
                    }
                    //4.对新api 的兼容
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (packageInfo.signingInfo != null) {
                            Signature[] signaturesArray = packageInfo.signingInfo.getApkContentsSigners();
                            if (signaturesArray != null && signaturesArray.length > 0) {
                                signaturesArray[0] = fakeSignature;
                            }
                        }
                    }
                    return packageInfo;
                }

                @Override
                public PackageInfo[] newArray(int size) {
                    return originalCreator.newArray(size);
                }
            };
            try {
                //5.将原来的 CREATOR 替换成我们的
                findField(PackageInfo.class, "CREATOR").set(null, creator);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                Map<?, ?> mCreators = (Map<?, ?>) findField(Parcel.class, "mCreators").get(null);
                Map<?, ?> sPairedCreators = (Map<?, ?>) findField(Parcel.class, "sPairedCreators").get(null);

                //清除调用条件
                mCreators.clear();
                sPairedCreators.clear();
            } catch (Throwable ignored) {
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
