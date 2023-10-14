package org.lsposed.lsplant;

public class LSPTest {

    static {
        System.loadLibrary("core");
    }

    public boolean field;

    public LSPTest() {
        field = false;
    }

    public native static boolean initHooker();

    public static boolean staticMethod() {
        return false;
    }

    public String normalMethod(String a, int b, long c) {
        return a + b + c;
    }

    public String manyParametersMethod(String a, boolean b, byte c, short d, int e, long f, float g, double h, Integer i, Long j) {
        return a + b + c + d + e + f + g + h + i + j;
    }


    public static class NeedInitialize {
        static int x;

        static {
            x = 0;
        }

        static boolean staticMethod() {
            try {
                return x != 0;
            } catch (Throwable e) {
                return false;
            }
        }

        static boolean callStaticMethod() {
            try {
                return staticMethod();
            } catch (Throwable e) {
                return false;
            }
        }
    }

    public interface ForProxy {
        String abstractMethod(String a, boolean b, byte c, short d, int e, long f, float g, double h, Integer i, Long j);
    }
}
