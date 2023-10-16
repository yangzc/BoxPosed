package cn.xutils.boxposed.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FileUtils {

    public static String readFile(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            copy(fis, baos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), Charset.defaultCharset());
    }

    public static String getText(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            copy(is, baos);
        } catch (IOException e) {
        }
        return new String(baos.toByteArray(), Charset.defaultCharset());
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf, 0, 1024)) != -1) {
            os.write(buf, 0, len);
        }
    }
}
