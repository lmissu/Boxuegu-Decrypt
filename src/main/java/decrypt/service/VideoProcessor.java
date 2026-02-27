package decrypt.service;

import decrypt.config.AppConfig;
import decrypt.config.ConfigLoader;
import decrypt.model.MapParser;
import decrypt.model.DESutil;
import decrypt.util.ResourceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

/**
 * 视频处理服务
 */
public class VideoProcessor {
    private final ConfigLoader config;
    
    public VideoProcessor(ConfigLoader config) {
        this.config = config;
    }
    
    public void processVideos() {
        try {
            // 读取配置文件
            String content = ResourceUtil.readResourceAsString(getClass(), AppConfig.MYSTORAGE_XML_PATH);
            MapParser mapParser = new MapParser(content);
            
            String[] fileList = ResourceUtil.readResourceAsLines(getClass(), AppConfig.COM_BOKECC_BASE_SP_XML_PATH);
            
            if (fileList.length < 5) {
                throw new IllegalStateException("配置文件格式不正确");
            }
            
            // 解析JSON数据
            String jsonString = fileList[4].substring(33, fileList[4].length() - 9)
                    .replaceAll("&quot;", "\"");
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray data = jsonObject.getJSONArray("data");
            
            // 处理JSON数据
            processJsonData(data, mapParser, config.getOutputPath(), config.getPcmPath());
            
        } catch (Exception e) {
            throw new RuntimeException("视频处理失败", e);
        }
    }
    
    private void processJsonData(JSONArray data, MapParser mapParser, String basePath, String pcmPath) {
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject jsonObject = data.getJSONObject(i);
                String dirPath = basePath + File.separator + jsonObject.getString("numName");
                
                try {
                    JSONArray children = jsonObject.getJSONArray("children");
                    File dir = new File(dirPath);
                    if (!dir.exists() && !dir.mkdirs()) {
                        System.err.println("创建目录失败: " + dirPath);
                        continue;
                    }
                    
                    if (children != null && !children.isEmpty()) {
                        processJsonData(children, mapParser, dirPath, pcmPath);
                    }
                } catch (Exception e) {
                    // 不是目录，是文件，进行解密
                    String fileName = jsonObject.getString("numName");
                    String inputPath = pcmPath + File.separator + fileName + ".pcm";
                    String outputPath = dirPath + ".mp4";
                    
                    File inputFile = new File(inputPath);
                    if (!inputFile.exists()) {
                        System.err.println("PCM文件不存在: " + inputPath);
                        continue;
                    }
                    
                    DESutil.start(inputPath, outputPath, mapParser);
                }
            } catch (Exception e) {
                System.err.println("处理数据项失败: " + e.getMessage());
            }
        }
    }
}