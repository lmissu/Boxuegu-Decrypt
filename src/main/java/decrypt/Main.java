package decrypt;

import decrypt.config.ConfigLoader;
import decrypt.service.VideoProcessor;

/**
 * 主程序入口
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // 初始化配置
            ConfigLoader config = createConfig(args);
            
            // 创建视频处理器并执行
            VideoProcessor processor = new VideoProcessor(config);
            processor.processVideos();
            
            System.out.println("所有视频处理完成！");
            
        } catch (Exception e) {
            System.err.println("程序执行失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * 创建配置对象，支持命令行参数覆盖默认配置
     */
    private static ConfigLoader createConfig(String[] args) {
        if (args.length >= 2) {
            return new ConfigLoader(args[0], args[1]);
        } else if (args.length == 1) {
            return new ConfigLoader(args[0], null);
        } else {
            return new ConfigLoader(); // 使用默认配置
        }
    }
}
