package decrypt.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 配置加载器
 */
public class ConfigLoader {
    private String pcmPath;
    private String outputPath;
    
    public ConfigLoader() {
        this.pcmPath = AppConfig.DEFAULT_PCM_PATH;
        this.outputPath = AppConfig.DEFAULT_OUTPUT_PATH;
    }
    
    public ConfigLoader(String pcmPath, String outputPath) {
        this.pcmPath = validatePath(pcmPath, "PCM路径");
        this.outputPath = validatePath(outputPath, "输出路径");
    }
    
    private String validatePath(String path, String pathType) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException(pathType + "不能为空");
        }
        
        File file = new File(path);
        if (!file.exists()) {
            // 尝试创建目录
            if (!file.mkdirs()) {
                throw new IllegalArgumentException(pathType + "不存在且无法创建: " + path);
            }
        }
        return path;
    }
    
    public String getPcmPath() {
        return pcmPath;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setPcmPath(String pcmPath) {
        this.pcmPath = validatePath(pcmPath, "PCM路径");
    }
    
    public void setOutputPath(String outputPath) {
        this.outputPath = validatePath(outputPath, "输出路径");
    }
}