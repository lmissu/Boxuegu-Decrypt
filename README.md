# Boxuegu-Decrypt 博学谷解密工具

## 📋 项目概述

这是一个用于解密博学谷平台PCM音频文件的Java工具。通过解析特定的XML配置文件和DES加密算法，将加密的PCM文件转换为MP4视频文件。

## 🏗️ 项目结构

```
Boxuegu-Decrypt/
├── src/main/java/decrypt/
│   ├── Main.java                 # 程序入口点
│   ├── config/                   # 配置管理
│   │   ├── AppConfig.java        # 应用常量和默认配置
│   │   └── ConfigLoader.java     # 运行时配置加载器
│   ├── service/                  # 业务逻辑层
│   │   └── VideoProcessor.java   # 视频处理服务
│   ├── model/                    # 数据模型层
│   │   ├── DESutil.java          # DES解密核心工具
│   │   ├── MapParser.java        # XML配置解析器
│   │   └── PCMHeader.java        # PCM文件头解析器
│   └── util/                     # 通用工具类
│       └── ResourceUtil.java     # 资源文件读取工具
├── pom.xml                       # Maven构建配置
└── README.md                     # 项目文档
```

## 🎯 核心特性

### ✨ 优化亮点

1. **清晰的包结构** - 按职责分离为配置、服务、模型、工具四层架构
2. **灵活的配置管理** - 支持命令行参数和默认配置
3. **健壮的错误处理** - 完善的异常捕获和用户友好的错误信息
4. **安全的资源管理** - 自动关闭文件流，避免资源泄漏
5. **严格的输入验证** - 所有外部输入都经过验证和清理
6. **详细的日志输出** - 提供处理进度和错误信息

### 🔧 技术栈

- **语言**: Java 25
- **构建工具**: Maven
- **加密算法**: DES
- **JSON处理**: org.json
- **字符编码**: UTF-8

## 🚀 快速开始

### 环境要求

- JDK 25 或更高版本
- Maven 3.6+

### 构建项目

```bash
# 编译项目
mvn compile

# 打包项目
mvn package

# 运行测试（如果有测试用例）
mvn test
```

### 运行程序

#### 方式一：使用默认配置
```bash
java -cp target/classes decrypt.Main
```
默认PCM路径：`D:\Downloads`
默认输出路径：`D:\video`

#### 方式二：指定路径
```bash
# 指定PCM路径和输出路径
java -cp target/classes decrypt.Main "C:\MyPCM" "C:\MyOutput"

# 只指定PCM路径（使用默认输出路径）
java -cp target/classes decrypt.Main "C:\MyPCM"
```

## 📖 使用说明

### 准备工作

1. **放置PCM文件**
   - 将待解密的`.pcm`文件放入配置的PCM路径中
   - 文件名应与配置文件中的`numName`字段对应

2. **配置文件**
   - `mystorage.xml` - 包含视频ID到密钥的映射
   - `com_bokecc_base_sp.xml` - 包含文件目录结构信息
   - 这两个文件应放在项目的`resources`目录下

### 处理流程

1. **初始化配置** - 加载路径配置和XML配置文件
2. **解析目录结构** - 从XML中提取文件组织结构
3. **递归处理节点** - 区分目录节点和文件节点
4. **执行解密** - 对PCM文件进行DES解密
5. **生成输出** - 创建对应的MP4文件

### 目录结构示例

假设配置文件定义如下结构：
```
output/
├── 第一章/
│   ├── 视频1.mp4
│   └── 视频2.mp4
└── 第二章/
    └── 视频3.mp4
```

## 🔍 核心组件详解

### Main.java
程序入口，负责：
- 解析命令行参数
- 初始化配置加载器
- 启动视频处理服务
- 统一异常处理

### ConfigLoader
配置管理器，功能：
- 管理PCM路径和输出路径
- 验证路径有效性
- 自动创建不存在的目录

### VideoProcessor
业务处理核心，职责：
- 协调各个组件的工作流程
- 解析XML配置文件
- 递归处理目录结构
- 调用DES解密功能

### DESutil
DES解密引擎：
- 读取PCM文件头信息
- 提取视频ID和加密段信息
- 执行DES解密算法
- 合并解密数据和原始数据

### MapParser
XML配置解析器：
- 解析`<string name="key">value</string>`格式
- 构建键值对映射表
- 提供高效的键值查询

### PCMHeader
PCM文件头解析器：
- 读取文件头元数据
- 解析视频ID、文件大小、加密段信息
- 提供数据完整性验证

### ResourceUtil
资源管理工具：
- 安全读取资源文件
- 自动处理字符编码
- 确保资源正确关闭

## ⚙️ 配置说明

### AppConfig 常量

| 常量名 | 默认值 | 说明 |
|--------|--------|------|
| `DEFAULT_PCM_PATH` | `D:\Downloads` | 默认PCM文件目录 |
| `DEFAULT_OUTPUT_PATH` | `D:\video` | 默认输出目录 |
| `MYSTORAGE_XML_PATH` | `/mystorage.xml` | 密钥映射文件路径 |
| `COM_BOKECC_BASE_SP_XML_PATH` | `/com_bokecc_base_sp.xml` | 目录结构文件路径 |
| `BUFFER_SIZE` | `8192` | IO缓冲区大小 |

### 命令行参数

| 参数位置 | 说明 | 示例 |
|----------|------|------|
| args[0] | PCM文件路径 | `"C:\\pcm"` |
| args[1] | 输出文件路径 | `"C:\\output"` |

## 🛡️ 错误处理

### 常见错误及解决方案

1. **文件不存在**
   - 错误信息：`输入文件不存在: xxx`
   - 解决：检查PCM文件路径和文件名是否正确

2. **密钥未找到**
   - 错误信息：`未找到视频ID对应的密钥: xxx`
   - 解决：检查`mystorage.xml`是否包含对应的视频ID

3. **权限不足**
   - 错误信息：`无法读取输入文件` 或 `无法创建输出目录`
   - 解决：以管理员权限运行或修改文件权限

4. **DES密钥无效**
   - 错误信息：`DES密钥长度不足8字节`
   - 解决：检查XML配置文件中的密钥格式

## 🔧 扩展开发

### 添加新的配置源

1. 在`config`包中创建新的配置加载器
2. 实现配置验证逻辑
3. 在`Main`类中集成新的配置源

### 支持新的加密算法

1. 在`model`包中创建新的解密工具类
2. 实现统一的接口规范
3. 修改`VideoProcessor`以支持新的解密器

### 添加日志框架

当前使用`System.out/err`，可轻松替换为：
- SLF4J + Logback
- java.util.logging
- Log4j2

## 📝 开发规范

### 代码风格
- 遵循Java编码规范
- 使用有意义的命名
- 添加必要的注释和文档
- 保持方法单一职责

### 异常处理
- 不吞掉异常，提供有意义的错误信息
- 使用具体的异常类型而非通用的Exception
- 在适当的层级处理异常

### 资源管理
- 使用try-with-resources确保资源释放
- 及时关闭文件流和网络连接
- 避免在循环中创建大量临时对象

## 🐛 故障排除

### 编译错误

1. **Java版本不匹配**
   ```bash
   # 检查Java版本
   java -version
   
   # 确保使用JDK 25
   ```

2. **依赖缺失**
   ```bash
   # 清理并重新下载依赖
   mvn clean compile
   ```

### 运行时错误

1. **内存不足**
   - 增加JVM堆内存：`-Xmx2g`
   
2. **文件路径问题**
   - Windows路径使用双反斜杠：`C:\\path`
   - 或使用正斜杠：`C:/path`

## 📄 许可证

本项目仅用于学习和研究目的。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 📞 联系方式

如有问题或建议，请联系项目维护者。