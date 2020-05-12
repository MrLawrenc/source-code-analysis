





# Spring Boot源码-Bean的解析

## IOC思想

## bean的两种配置方式

### xml配置

- 有参构造
- 无参构造
- 静态工厂
- 实力工厂

### 注解

#### @Component注解

#### 配置类中使用@Bean

#### 实现FactoryBean

- 定义bean

  ```java
  public class Animal {
      @Getter
      protected String name;
  }
  ```

  ```java
  public class Dog extends Animal {
      public Dog() {
          this.name = "小狗";
      }
  }
  ```

  ```java
  public class Cat extends Animal {
      public Cat() {
          this.name="猫咪";
      }
  }
  ```

  ```java
  public class Bird extends Animal {
      public Bird() {
          this.name = "小鸟";
      }
  }
  ```

- 使用BeanFactory的方式注入Cat

  ```java
  @Component
  public class FactoryBeanTest implements FactoryBean<Animal> {
      @Override
      public Cat getObject() throws Exception {
          return new Cat();
      }
  
      @Override
      public Class<?> getObjectType() {
          return Cat.class;
      }
  }
  ```

#### 实现BeanDefinitionRegistryPostProcessor

- 使用BeanDefinitionRegistryPostProcessor的方式注入Dog

  ```java
  @Component
  public class BeanDefinitionRegistryPostProcessorTest implements BeanDefinitionRegistryPostProcessor {
      @Override
      public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
          RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
          rootBeanDefinition.setBeanClass(Dog.class);
          beanDefinitionRegistry.registerBeanDefinition("dog", rootBeanDefinition);
      }
  
      @Override
      public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
          System.out.println("我是dog 的 postProcessor");
      }
  }
  ```

#### 实现ImportBeanDefinitionRegistrar

- 使用ImportBeanDefinitionRegistrar的方式注入bird

  ```java
  public class ImportBeanDefinitionRegistrarTest implements ImportBeanDefinitionRegistrar {
  
      @Override
      public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
          RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
          rootBeanDefinition.setBeanClass(Bird.class);
          registry.registerBeanDefinition("bird", rootBeanDefinition);
      }
  }
  ```

- 启动类加上注解@Import(value = ImportBeanDefinitionRegistrarTest.class)

####  测试后三种方式的bean注入

- 启动类

  ```java
  @SpringBootApplication
  @Import(value = ImportBeanDefinitionRegistrarTest.class)
  public class TestGetBean implements ApplicationRunner {
  
      /**
       * 由于工厂bean是特殊bean，使用
       * <pre>
       *     @Autowired
       *     @Qualifier("factoryBeanTest")
       *     private Animal cat;
       * </pre>
       * 是注入不了的
       */
      @Autowired
      @Qualifier("factoryBeanTest")
      private Animal cat;
      @Autowired()
      @Qualifier("dog")
      private Animal dog;
      @Autowired()
      @Qualifier("bird")
      private Animal bird;
  
  
      /**
       * 对比
       * <pre>
       *     @Autowired
       *     @Qualifier("factoryBeanTest")
       *     private Animal cat;
       * </pre>
       * 保留疑问，后面会详细分析{@linkplain org.springframework.beans.factory.FactoryBean}
       */
      @Autowired
      @Qualifier("&factoryBeanTest")
      private FactoryBeanTest factoryBeanTest;
  
      @Autowired
      private AnnotationConfigApplicationContext context;
  
      public static void main(String[] args) {
          SpringApplication.run(TestGetBean.class, args);
      }
  
      @Override
      public void run(ApplicationArguments args) throws Exception {
          System.out.println(factoryBeanTest);
          System.out.println(cat.getName() + "  " + dog.getName() + "  " + bird.getName());
      }
  }
  ```

- 输出

  ```java
  .....
  2020-05-11 22:15:41.759  INFO 9384 --- [           main] c.s.springbootsource.bean.TestGetBean    : No active profile set, falling back to default profiles: default
  我是dog 的 postProcessor
  2020-05-11 22:15:42.092  INFO 9384 --- [           main] c.s.springbootsource.bean.TestGetBean    : Started TestGetBean in 0.547 seconds (JVM running for 1.101)
  com.swust.springbootsource.bean.annotation.FactoryBeanTest@7726e185
  猫咪  小狗  小鸟
  ```

- 注意，由于Cat是使用工厂bean实例化的，因此，在注入Cat的时候采用如下方式注入是为空的

  ```java
  //为空
  @Autowired
  @Qualifier("cat")
  private Animal cat;
  
  //这种方式也可以
  @Autowired
  private Cat cat;
  
  ```

- 在使用名字获取bean的时候，Cat实例的名字为工厂类名首字母小写，若要获取工厂bean则需要加&符号。但是通过类型获取则和普通bean一样，后面源码会详细分析FactoryBean

## 核心方法refreshContext解析

### 概览

- Spring Boot的refreshContext方法到Spring核心的refresh方法

  - refreshContext方法位于SpringApplication的核心run方法内
  - org.springframework.boot.SpringApplication#refreshContext到org.springframework.boot.SpringApplication#refresh
  - org.springframework.boot.SpringApplication#refresh到org.springframework.context.support.AbstractApplicationContext#refresh

- Spring核心的refresh方法概览

  ```java
  public void refresh() throws BeansException, IllegalStateException {
          synchronized(this.startupShutdownMonitor) {
              this.prepareRefresh();
              ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
              this.prepareBeanFactory(beanFactory);
  
              try {
                  this.postProcessBeanFactory(beanFactory);
                  this.invokeBeanFactoryPostProcessors(beanFactory);
                  this.registerBeanPostProcessors(beanFactory);
                  this.initMessageSource();
                  this.initApplicationEventMulticaster();
                  this.onRefresh();
                  this.registerListeners();
                  this.finishBeanFactoryInitialization(beanFactory);
                  this.finishRefresh();
              } catch (BeansException var9) {
                  if (this.logger.isWarnEnabled()) {
                      this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                  }
  
                  this.destroyBeans();
                  this.cancelRefresh(var9);
                  throw var9;
              } finally {
                  this.resetCommonCaches();
              }
  
          }
      }
  ```

  ![Spring Boot源码-核心refreshContext方法流程](https://lmy25.wang/upload/2020/05/Spring%20Boot%E6%BA%90%E7%A0%81-%E6%A0%B8%E5%BF%83refreshContext%E6%96%B9%E6%B3%95%E6%B5%81%E7%A8%8B-6617a8353a284c029cc8c0a720ea1275.jpg)

- Spring的refresh方法使用关键字synchronized修饰，不允许并发调用

### prepareRefresh方法

- 进入prepareRefresh方法

  ```java
  	protected void prepareRefresh() {
  		// Switch to active.状态及时间设置
  		this.startupDate = System.currentTimeMillis();
  		this.closed.set(false);
  		this.active.set(true);
  
          //日志（平时使用方式：推荐先判断再使用）
  		if (logger.isDebugEnabled()) {
  			if (logger.isTraceEnabled()) {
  				logger.trace("Refreshing " + this);
  			}
  			else {
  				logger.debug("Refreshing " + getDisplayName());
  			}
  		}
  
  		// Initialize any placeholder property sources in the context environment. 扩展方法。这在org.springframework.context.support.AbstractApplicationContext#initPropertySources中是一个空方法，留给子类实现
  		initPropertySources();
  
  		// Validate that all properties marked as required are resolvable:
  		// see ConfigurablePropertyResolver#setRequiredProperties
          //获取ConfigurableEnvironment对象，并检查是否有“必须”属性，必须属性一般用在提供方。比如我是mybatis-starter，name我可以设置必须有数据库连接池配置，若boot引入了mybatis-starter而没有配置数据库连接信息则会在这儿校验失败
  		getEnvironment().validateRequiredProperties();
  
  		// Store pre-refresh ApplicationListeners... 初始化时为空
  		if (this.earlyApplicationListeners == null) {
              //添加容器中的所有监听器（监听器在源码分析事件部分会详细说明）
  			this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
  		}
  		else {
  			// Reset local application listeners to pre-refresh state.
  			this.applicationListeners.clear();
  			this.applicationListeners.addAll(this.earlyApplicationListeners);
  		}
  
  		// Allow for the collection of early ApplicationEvents,
  		// to be published once the multicaster is available...
          //事件初始化
  		this.earlyApplicationEvents = new LinkedHashSet<>();
  	}
  ```

- 

### obtainFreshBeanFactory

- 进入obtainFreshBeanFactory方法(告知子类刷新容器内部bean工厂了)

  ```java
  	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
  		refreshBeanFactory();
  		return getBeanFactory();
  	}
  ```

- 由于我当前非web环境，所以容器是AnnotationConfigApplicationContext，因此refreshBeanFactory会进入父类org.springframework.context.support.GenericApplicationContext#refreshBeanFactory的方法(若为web环境的其他容器基本一致)

  ```java
  	@Override
  	protected final void refreshBeanFactory() throws IllegalStateException {
          //设置为已刷新过容器
  		if (!this.refreshed.compareAndSet(false, true)) {
  			throw new IllegalStateException(
  					"GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
  		}
  		this.beanFactory.setSerializationId(getId());
  	}
  ```

  

- getBeanFactory();获取当前bean工厂，注解环境的bean工厂为org.springframework.beans.factory.support.DefaultListableBeanFactory

### prepareBeanFactory

- 进入org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory方法（在使用context之前的准备工作）

  ```java
  protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
      // Tell the internal bean factory to use the context's class loader etc.
      beanFactory.setBeanClassLoader(getClassLoader());
      beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
      beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
  
      // Configure the bean factory with context callbacks.
      beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
      beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
      beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
      beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
      beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
      beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
      beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
  
      // BeanFactory interface not registered as resolvable type in a plain factory.
      // MessageSource registered (and found for autowiring) as a bean.
      beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
      beanFactory.registerResolvableDependency(ResourceLoader.class, this);
      beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
      beanFactory.registerResolvableDependency(ApplicationContext.class, this);
  
      // Register early post-processor for detecting inner beans as ApplicationListeners.
      beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
  
      // Detect a LoadTimeWeaver and prepare for weaving, if found.
      if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
          beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
          // Set a temporary ClassLoader for type matching.
          beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
      }
  
      // Register default environment beans.
      if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
          beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
      }
      if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
          beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
      }
      if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
          beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
      }
  }
  ```

  

- 

### postProcessBeanFactory

###invokeBeanFactoryPostProcessors

## bean的实例化流程分析

## 总结

