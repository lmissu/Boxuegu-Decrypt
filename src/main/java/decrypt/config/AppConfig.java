package decrypt.config;

/**
 * 应用配置类
 */
public class AppConfig {
    // 默认路径配置
    public static final String DEFAULT_PCM_PATH = "D:\\Downloads";
    public static final String DEFAULT_OUTPUT_PATH = "D:\\video";
    
    // 资源文件路径
    public static final String MYSTORAGE_XML_PATH = "/mystorage.xml";
    public static final String COM_BOKECC_BASE_SP_XML_PATH = "/com_bokecc_base_sp.xml";
    
    // XML解析相关
    public static final String STRING_PATTERN = "<string name=\"([^\"]+)\">([^<]+)</string>";
    
    // 缓冲区大小
    public static final int BUFFER_SIZE = 8192;
}