# Spring Boot源码-框架整体启动流程

## 框架初始化

- 配置资源加载器
- 配置primarySources
- 应用环境检测
- 配置系统初始化器
- 配置应用监听器】
- 配置main方法所在的类

## 框架启动

- 计时器开始计时
- Headless模式赋值
- 发送ApplicationStartingEvent事件
- 配置环境env模块
- 发送ApplicationEnvironmentPrepareEvent事件
- 打印banner
- 创建应用上下文context对象
- 初始化失败分析器
- 关联spring boot组件与应用上下文对象
- 发送ApplicationContextInitialzedEvent事件
- 加载source到context
- 发送ApplicationPrepareEvent事件
- 刷新上下文（refresh）
- 计时器停止计时
- 发送ApplicationStartedEvent事件
- 调用框架的启动扩展类（如，实现类ApplicationRunner接口）
- 发送ApplicationReadyEvent事件

## 自动化装配流程

- 收集配置文件中的配置工厂类
- 加载组件工厂
- 注册组件内自定义的bean

### 启动流程图

![Spring Boot启动流程](https://lmy25.wang/upload/2020/05/Spring%20Boot%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B-c49d0773acf141b9aa1bf6d73a1d1960.jpg)

