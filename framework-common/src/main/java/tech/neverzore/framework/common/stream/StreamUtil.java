package tech.neverzore.framework.common.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhouzb
 * @date 2019/6/2
 */
public class StreamUtil {
    public static ByteArrayOutputStream cloneInputStreamData(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        } catch (IOException e) {
            throw e;
        }

        return baos;
    }
}
