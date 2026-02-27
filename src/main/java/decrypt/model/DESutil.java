package decrypt.model;

import decrypt.config.AppConfig;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * DES解密工具类
 */
public class DESutil {
    
    /**
     * 启动解密程序入口
     */
    public static void start(String inputPath, String outputPath, MapParser mapParser) throws Exception {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        
        validateInputFile(inputFile);
        validateOutputPath(outputFile);

        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
             FileOutputStream fos = new FileOutputStream(outputFile);
             DataOutputStream dos = new DataOutputStream(fos)) {
            
            PCMHeader header = new PCMHeader(raf);
            String key = getDecryptionKey(mapParser, header.videoId);
            
            long totalDecrypted = decryptSegments(raf, dos, header, key);
            copyRemainingData(raf, dos, header.sourceFileSize - totalDecrypted);

            System.out.println("解密成功: " + outputPath);
            
        } catch (Exception e) {
            throw new Exception("解密失败: " + outputPath + " - " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证输入文件
     */
    private static void validateInputFile(File inputFile) throws Exception {
        if (!inputFile.exists()) {
            throw new Exception("输入文件不存在: " + inputFile.getAbsolutePath());
        }
        if (!inputFile.canRead()) {
            throw new Exception("无法读取输入文件: " + inputFile.getAbsolutePath());
        }
    }
    
    /**
     * 验证输出路径
     */
    private static void validateOutputPath(File outputFile) throws Exception {
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new Exception("无法创建输出目录: " + parentDir.getAbsolutePath());
        }
    }
    
    /**
     * 获取解密密钥
     */
    private static String getDecryptionKey(MapParser mapParser, String videoId) throws Exception {
        if (videoId == null || videoId.trim().isEmpty()) {
            throw new Exception("视频ID为空");
        }
        
        String key = mapParser.getValue(videoId);
        if (key == null || key.trim().isEmpty()) {
            throw new Exception("未找到视频ID对应的密钥: " + videoId);
        }
        
        return key;
    }

    /**
     * 解密所有加密段
     */
    private static long decryptSegments(RandomAccessFile raf, DataOutputStream dos, 
                                      PCMHeader header, String key) throws Exception {
        long totalDecrypted = 0;
        
        for (int i = 0; i < header.encryptLengths.length; i++) {
            long segmentLength = header.encryptLengths[i];
            
            if (segmentLength <= 0) {
                System.err.println("警告: 第" + i + "段长度为非正数: " + segmentLength);
                continue;
            }
            
            byte[] encryptedData = header.readBytes(raf, (int) segmentLength);
            byte[] decryptedData = decrypt(encryptedData, key.getBytes(StandardCharsets.UTF_8));
            
            if (decryptedData != null && decryptedData.length > 0) {
                dos.write(decryptedData);
                dos.flush();
                totalDecrypted += decryptedData.length;
            }
        }
        return totalDecrypted;
    }

    /**
     * DES解密核心方法
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        if (data == null || data.length == 0) {
            return data;
        }
        
        if (key == null || key.length < 8) {
            throw new Exception("DES密钥长度不足8字节");
        }
        
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new Exception("DES解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 复制未加密数据
     */
    private static void copyRemainingData(RandomAccessFile raf, DataOutputStream dos, long remaining)
            throws IOException {
        if (remaining <= 0) return;
        
        byte[] buffer = new byte[AppConfig.BUFFER_SIZE];
        while (remaining > 0) {
            int toRead = (int) Math.min(buffer.length, remaining);
            int read = raf.read(buffer, 0, toRead);
            if (read <= 0) break;
            dos.write(buffer, 0, read);
            remaining -= read;
        }
    }
}
