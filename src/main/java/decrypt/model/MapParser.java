package decrypt.model;

import decrypt.config.AppConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML配置解析器
 */
public class MapParser {
    private final Map<String, String> data;
    private final Pattern pattern;
    
    /**
     * 构造方法，传入整个XML字符串并解析
     * @param xmlContent XML格式的字符串
     */
    public MapParser(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("XML内容不能为空");
        }
        
        this.pattern = Pattern.compile(AppConfig.STRING_PATTERN);
        this.data = parseXml(xmlContent);
    }
    
    /**
     * 私有构造函数，用于创建空解析器
     */
    private MapParser() {
        this.pattern = Pattern.compile(AppConfig.STRING_PATTERN);
        this.data = Collections.emptyMap();
    }

    /**
     * 解析XML字符串，提取键值对
     * @param xmlContent XML格式的字符串
     * @return 解析后的键值对映射
     */
    private Map<String, String> parseXml(String xmlContent) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = pattern.matcher(xmlContent);
        
        // 查找所有匹配项
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            if (key != null && !key.trim().isEmpty() && 
                value != null && !value.trim().isEmpty()) {
                result.put(key.trim(), value.trim());
            }
        }
        
        if (result.isEmpty()) {
            System.err.println("警告: 未在XML中找到任何有效的键值对");
        } else {
            System.out.println("成功解析 " + result.size() + " 个配置项");
        }
        
        return Collections.unmodifiableMap(result);
    }

    /**
     * 根据键获取对应的值
     * @param key 要查找的键
     * @return 对应的值，如果键不存在则返回null
     */
    public String getValue(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return data.get(key.trim());
    }
    
    /**
     * 检查是否包含指定的键
     * @param key 要检查的键
     * @return 如果包含该键返回true，否则返回false
     */
    public boolean containsKey(String key) {
        return key != null && data.containsKey(key.trim());
    }
    
    /**
     * 获取所有配置项的数量
     * @return 配置项数量
     */
    public int size() {
        return data.size();
    }
    
    /**
     * 创建空的MapParser实例
     * @return 空的MapParser实例
     */
    public static MapParser empty() {
        return new MapParser();
    }
}