package decrypt.model;

import decrypt.config.AppConfig;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * PCM文件头解析器
 */
public class PCMHeader {
    public String videoId;
    public long start;
    public long end;
    public long sourceFileSize;
    public long[] encryptLengths;
    
    private static final int HEADER_SKIP_BYTES = 7;
    private static final int UNKNOWN_FIELD_SKIP_OFFSET = 4;
    private static final int RESERVED_SKIP_BYTES = 4;
    
    public PCMHeader(RandomAccessFile raf) throws IOException {
        validateRandomAccessFile(raf);
        parseHeader(raf);
    }
    
    /**
     * 验证RandomAccessFile是否有效
     */
    private void validateRandomAccessFile(RandomAccessFile raf) throws IOException {
        if (raf == null) {
            throw new IllegalArgumentException("RandomAccessFile不能为null");
        }
        if (raf.length() == 0) {
            throw new IOException("PCM文件为空");
        }
    }
    
    /**
     * 解析PCM文件头
     */
    private void parseHeader(RandomAccessFile raf) throws IOException {
        // 跳过文件头前7个字节
        skipBytesWithValidation(raf, HEADER_SKIP_BYTES, "文件头标识");

        // 读取视频ID
        int videoIdLength = readInt(raf);
        this.videoId = readString(raf, videoIdLength);
        
        if (videoId.isEmpty()) {
            throw new IOException("视频ID为空");
        }

        // 跳过未知字段
        int unknownFieldLength = readInt(raf);
        skipBytesWithValidation(raf, unknownFieldLength + UNKNOWN_FIELD_SKIP_OFFSET, "未知字段");

        // 读取关键位置信息
        this.start = readLong(raf);
        this.end = readLong(raf);
        this.sourceFileSize = readLong(raf);
        this.end = this.sourceFileSize; // 重置end为文件大小

        // 跳过保留字段
        skipBytesWithValidation(raf, RESERVED_SKIP_BYTES, "保留字段");

        // 读取加密段信息
        int segmentCount = readInt(raf);
        if (segmentCount <= 0) {
            throw new IOException("加密段数量无效: " + segmentCount);
        }
        
        this.encryptLengths = new long[segmentCount];
        for (int i = 0; i < segmentCount; i++) {
            this.encryptLengths[i] = readLong(raf);
            
            // 验证段长度合理性
            if (this.encryptLengths[i] < 0) {
                System.err.println("警告: 第" + i + "段长度为负数: " + this.encryptLengths[i]);
                this.encryptLengths[i] = 0;
            }
        }
        
        // 跳过尾部数据
        int tailLength = readInt(raf);
        skipBytesWithValidation(raf, tailLength, "文件尾");
    }
    
    /**
     * 带验证的跳过字节方法
     */
    private void skipBytesWithValidation(RandomAccessFile raf, int bytesToSkip, String fieldName) throws IOException {
        if (bytesToSkip < 0) {
            throw new IOException(fieldName + "跳过字节数无效: " + bytesToSkip);
        }
        
        long currentPosition = raf.getFilePointer();
        long fileSize = raf.length();
        
        if (currentPosition + bytesToSkip > fileSize) {
            throw new IOException(String.format("%s: 需要跳过%d字节，但剩余只有%d字节 (位置:%d/%d)", 
                fieldName, bytesToSkip, fileSize - currentPosition, currentPosition, fileSize));
        }
        
        raf.skipBytes(bytesToSkip);
    }

    private String readString(RandomAccessFile raf, int length) throws IOException {
        if (length < 0) {
            throw new IOException("字符串长度不能为负数: " + length);
        }
        if (length == 0) {
            return "";
        }
        
        byte[] bytes = new byte[length];
        raf.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private int readInt(RandomAccessFile raf) throws IOException {
        return readNumber(raf, 4).getInt();
    }

    private long readLong(RandomAccessFile raf) throws IOException {
        return readNumber(raf, 8).getLong();
    }

    private ByteBuffer readNumber(RandomAccessFile raf, int size) throws IOException {
        if (size <= 0) {
            throw new IOException("读取数字字节数无效: " + size);
        }
        
        byte[] bytes = new byte[size];
        raf.readFully(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
    }

    public byte[] readBytes(RandomAccessFile raf, int length) throws IOException {
        if (length < 0) {
            throw new IOException("读取字节数不能为负数: " + length);
        }
        if (length == 0) {
            return new byte[0];
        }
        
        byte[] bytes = new byte[length];
        raf.readFully(bytes);
        return bytes;
    }
    
    /**
     * 获取文件头信息摘要
     */
    public String getHeaderSummary() {
        return String.format("PCMHeader{videoId='%s', fileSize=%d, segments=%d, segmentSizes=%s}",
            videoId, sourceFileSize, encryptLengths.length, Arrays.toString(encryptLengths));
    }
    
    /**
     * 验证文件头数据的完整性
     */
    public boolean isValid() {
        return videoId != null && !videoId.isEmpty() && 
               sourceFileSize > 0 && 
               encryptLengths != null && 
               encryptLengths.length > 0;
    }
}