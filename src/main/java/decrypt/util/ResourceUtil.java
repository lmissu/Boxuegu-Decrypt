package decrypt.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 资源管理工具类
 */
public class ResourceUtil {
    
    /**
     * 安全读取资源文件内容为字符串
     */
    public static String readResourceAsString(Class<?> clazz, String resourcePath) {
        try (InputStream inputStream = clazz.getResourceAsStream(resourcePath);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException("读取资源文件失败: " + resourcePath, e);
        }
    }
    
    /**
     * 安全读取资源文件内容为字符串数组（按行分割）
     */
    public static String[] readResourceAsLines(Class<?> clazz, String resourcePath) {
        String content = readResourceAsString(clazz, resourcePath);
        return content.split("\\R"); // 按任意换行符分割
    }
}