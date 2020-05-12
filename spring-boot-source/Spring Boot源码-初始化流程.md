# Spring Boot源码-框架初始化

## 初始化器实战

### 准备工作

- 在Spring Boot工程中创建三个初始化类，如下:

  当实现了Spring Boot的初始化器，会在容器初始化之前调用initializer方法。

  ```java
  @Order(10)
  public class MyInitializer1 implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  
      @Override
      public void initialize(ConfigurableApplicationContext applicationContext) {
          System.out.println("MyInitializer1.....................");
          
           //可以在容器初始化之前注入一些环境变量，或者做一些其他事0.0
          ConfigurableEnvironment environment = applicationContext.getEnvironment();
          Map<String, Object> map = new HashMap<>();
          map.put("key", "value");
          MapPropertySource mapPropertySource = new MapPropertySource("myMapProperties", map);
          environment.getPropertySources().addLast(mapPropertySource);
          System.out.println("MyInitializer1.....................");
      }
  }
  @Order(5)
  public class MyInitializer2 implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  
      @Override
      public void initialize(ConfigurableApplicationContext applicationContext) {
          System.out.println("MyInitializer2.....................");
      }
  }
  @Order(20)
  public class MyInitializer3 implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  
      @Override
      public void initialize(ConfigurableApplicationContext applicationContext) {
          System.out.println("MyInitializer3.....................");
      }
  }
  ```

  

### 三种方式配置初始化器

#### 通过spi机制配置spring.factories

- 在resource/META-INFO/spring.factories文件中配置

  ```propert
  org.springframework.context.ApplicationContextInitializer=spring_sources.initialzed.MyInitializer
  ```

#### 手动添加初始化器

- 手动创建SpringApplication，并添加initializer

  ```java
      public static void main(String[] args) {
          /*
           * 可以使用如下方式启动spring
           * 传入当前class，spring boot在创建容器之后会将此类注入容器
           */
          SpringApplication application = new SpringApplication(SpringSourceTestApp.class);
          //手动添加初始化器
          application.addInitializers(new MyInitializer2());
          application.run(args);
      }
  ```

#### yml中配置初始化器

- 配置文件配置初始化器

  ```yml
  context:
    initializer:
    #    使用该方式配置order注解会失效，实际使用的order是0
      classes: spring_sources.initialzed.MyInitializer3
  ```

### 验证

- 验证环境变量是否加入spring，根据Spring预留的扩展接口Aware（后面源码会详细分析）创建Context工具类

  ```java
  @Component
  public class MySpring implements ApplicationContextAware {
      @Getter
      private ApplicationContext context;
  
      @Override
      public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
          this.context = applicationContext;
      }
  }
  ```

- 启动类中获取自定义的环境变量

  ```java
  @SpringBootApplication
  public class SpringSourceTestApp implements ApplicationRunner {
      @Autowired
      private MySpring mySpring;
  
      public static void main(String[] args) {
          /*
           * 可以使用如下方式启动spring
           * 传入当前class，spring boot在创建容器之后会将此类注入容器
           */
          SpringApplication application = new SpringApplication(SpringSourceTestApp.class);
          application.addInitializers(new MyInitializer2());
          application.run(args);
      }
  
      @Override
      public void run(ApplicationArguments args) throws Exception {
          MutablePropertySources propertySources = ((ConfigurableApplicationContext) mySpring.getContext()).getEnvironment().getPropertySources();
  
          System.out.println("propertySources "+propertySources);
  
          System.out.println(mySpring.getContext().getEnvironment().getProperty("key"));
  
      }
  }
  ```

  实现了ApplicationRunner接口，会在spring boot启动完成之后调用run方法（源码也会详细分析）

- 启动spring boot，查看控制台输出

  ```java
    .   ____          _            __ _ _
   /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
  ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
   \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
    '  |____| .__|_| |_|_| |_\__, | / / / /
   =========|_|==============|___/=/_/_/_/
   :: Spring Boot ::        (v2.2.4.RELEASE)
  
  MyInitializer3.....................
  MyInitializer2.....................
  MyInitializer1.....................
  propertySources [MapPropertySource {name='server.ports'}, ConfigurationPropertySourcesPropertySource {name='configurationProperties'}, PropertiesPropertySource {name='systemProperties'}, OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}, RandomValuePropertySource {name='random'}, OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.yml]'}, MapPropertySource {name='myMapProperties'}]
  value
  ```

  可以发现获取到的为我们加入的mapproperties，且属性也能获取到，但是发现并未按设定的顺序输出（用order排序了），而是第三个初始化器最先输出，即最先调用，后面查看源码会发现缘由。

## 工厂加载机制

### SpringFactoriesLoader

- 实现spi机制的核心类，框架内部通用的工厂加载机制
- 从classpath下多个jar包特定位置读取文件并初始化
- 文件内容必须为kv形式，即properties类型
- key必须为抽象类/接口的全限定名，value是实现类的全限定名，多实现用，分隔

### 工厂加载机制源码跟踪

- 进入run方法

  ```java
  public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
      return run(new Class<?>[] { primarySource }, args);
  }
  
  public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
      return new SpringApplication(primarySources).run(args);
  }
  ```

- 接着进入SpringApplication构造器

  ```java
  public SpringApplication(Class<?>... primarySources) {
      this(null, primarySources);
  }
  
  public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
      this.resourceLoader = resourceLoader;
      Assert.notNull(primarySources, "PrimarySources must not be null");
      this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
      this.webApplicationType = WebApplicationType.deduceFromClasspath();
      //通过配置，设置初始化器initializer
      setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
      setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
      this.mainApplicationClass = deduceMainApplicationClass();
  }
  
  ```

- 进入关键方法getSpringFactoriesInstances

  ```java
  private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
      return getSpringFactoriesInstances(type, new Class<?>[] {});
  }
  
  private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
      //获取classloader
      ClassLoader classLoader = getClassLoader();
      // Use names and ensure unique to protect against duplicates
      //加载配置的初始化器
      Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
      //通过反射创建出所有对象
      List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
      //对初始化器根据order排序
      AnnotationAwareOrderComparator.sort(instances);
      //返回所有初始化器
      return instances;
  }
  ```

  此时的type是ApplicationContextInitializer，

     - 进入SpringFactoriesLoader.loadFactoryNames(type, classLoader)方法

       ```java
       public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
           //此时的name为 org.springframework.context.ApplicationContextInitializer
           String factoryTypeName = factoryType.getName();
           //关键，获取key为factoryTypeName类型的初始化器，若没有则返回空list
           return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
       }
       ```

     - 进入loadSpringFactories(classLoader)方法

       ```java
       private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
           	//从缓存中查找当前loader对应的初始化器，若有则返回（第一次是是没有的，该方法会被调用多次，获取不同类型的配置，如initializer，listener等）
       		MultiValueMap<String, String> result = cache.get(classLoader);
       		if (result != null) {
       			return result;
       		}
       
       		try {
                   /**
                   *	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
                   * 因此可以看出是利用classloader加载classpath路径下的，META-INF/spring.factories文件
                   * 
                   */
       			Enumeration<URL> urls = (classLoader != null ?
       					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
       					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
       			result = new LinkedMultiValueMap<>();
                   //遍历每个clas下的每个spring.factories
       			while (urls.hasMoreElements()) {
       				URL url = urls.nextElement();
       				UrlResource resource = new UrlResource(url);
                       //将文件解析为properties（即k-v形式）
       				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                       //遍历每个key-value键值
       				for (Map.Entry<?, ?> entry : properties.entrySet()) {
       					String factoryTypeName = ((String) entry.getKey()).trim();
                           //使用工具类，将当前key对应的value用英文逗号（","）切分为数组，并依次遍历
       					for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
                               //添加到结果集，并使用factoryTypeName作为key，此时的factoryTypeName为org.springframework.context.ApplicationContextInitializer
       						result.add(factoryTypeName, factoryImplementationName.trim());
       					}
       				}
       			}
                   //放入缓存，下次再调用则直接返回
       			cache.put(classLoader, result);
       			return result;
       		}
       		catch (IOException ex) {
       			throw new IllegalArgumentException("Unable to load factories from location [" +
       					FACTORIES_RESOURCE_LOCATION + "]", ex);
       		}
       	}
       ```

      -  

- 获取到上一步返回的所有在spring.factories配置的实现类之后，进入createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);方法

     - 此时的type为ApplicationContextInitializer，names为配置的所有实现类

       ```JAVA
       	private <T> List<T> createSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes,
       			ClassLoader classLoader, Object[] args, Set<String> names) {
       		List<T> instances = new ArrayList<>(names.size());
       		for (String name : names) {
       			try {
       				Class<?> instanceClass = ClassUtils.forName(name, classLoader);
       				Assert.isAssignable(type, instanceClass);
       				Constructor<?> constructor = instanceClass.getDeclaredConstructor(parameterTypes);
       				T instance = (T) BeanUtils.instantiateClass(constructor, args);
       				instances.add(instance);
       			}
       			catch (Throwable ex) {
       				throw new IllegalArgumentException("Cannot instantiate " + type + " : " + name, ex);
       			}
       		}
       		return instances;
       	}
       ```

      -  上面代码较为简单，使用反射创建ApplicationContextInitializer所有实现类的实例，并返回

- 接着进入 AnnotationAwareOrderComparator.sort(instances);排序方法，将所有的initializer根据order排序（可以是@Order注解也可以是继承了Ordered类）

  

- 最后调用set方法，将所有的初始化器实例赋值个SpringApplication

  ```java
  private List<ApplicationContextInitializer<?>> initializers;
  
  public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
      this.initializers = new ArrayList<>(initializers);
  }
  ```

- 上面的loadSpringFactories(classLoader)方法会在Spring Boot启动中多次调用，用来加载不同类型的配置

### loadFactories流程图

![SpringFactoriesLoader load流程](https://lmy25.wang/upload/2020/05/SpringFactoriesLoader%20load%E6%B5%81%E7%A8%8B-45720d67cb26491086e44e8576409fa0.jpg)





## 初始化器解析

### 作用

- 上下文刷新即refresh方法前调用
- 用来编码设置一些属性变量，通常用在web环境中
- 可以通过order接口进行排序

### 初始化器被实例化的时机

#### 配置在spring.factories中的初始化器<a name="md1"></a>

- 通过以上的loadFactories（）方法分析，已经得知，在spring.factories配置的初始化器会在SpringApplication的构造方法中被实例化

#### 手动加入的初始化器

- 首先回顾下手动加入的方法,在启动类中

  ```java
  SpringApplication application = new SpringApplication(SpringSourceTestApp.class);
  application.addInitializers(new MyInitializer2());
  application.run(args);
  ```

- 进入SpringApplication#addInitializers方法

  ```java
  public void addInitializers(ApplicationContextInitializer<?>... initializers) {
      this.initializers.addAll(Arrays.asList(initializers));
  }
  
  private List<ApplicationContextInitializer<?>> initializers;
  ```

  以上可见，这种方式较为粗暴，直接往list里面添加初始化器，随后在run（）调用过程中，会进入以上的loadFactories（）流程，之后会对包括这儿添加的初始化器排序。

#### 通过yml配置的初始化器

- 通过debug，查看方法调用栈的方式寻找yml配置初始化器的初始化时机

- 以之前的为例，在yml配置初始化器MyInitializer3#initialize方法中加入断点，debug启动容器查看调用栈

- 发现是在DelegatingApplicationContextInitializer#applyInitializers中调用的

- 而applyInitializers是在applyInitializerClasses中被调用的

  ```java
  private void applyInitializerClasses(ConfigurableApplicationContext context, List<Class<?>> initializerClasses) {
      Class<?> contextClass = context.getClass();
      List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();
      for (Class<?> initializerClass : initializerClasses) {
          initializers.add(instantiateInitializer(contextClass, initializerClass));
      }
      applyInitializers(context, initializers);
  }
  ```

- 继续查找，发现applyInitializerClasses是在DelegatingApplicationContextInitializer#initialize方法中被调用的，且DelegatingApplicationContextInitializer也实现了ApplicationContextInitializer接口

  ```java
  //类签名
  public class DelegatingApplicationContextInitializer
  		implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered 
      private int order = 0;
      
  @Override
  public void initialize(ConfigurableApplicationContext context) {
      ConfigurableEnvironment environment = context.getEnvironment();
      List<Class<?>> initializerClasses = getInitializerClasses(environment);
      if (!initializerClasses.isEmpty()) {
          applyInitializerClasses(context, initializerClasses);
      }
  }
  ```

- 因此，简单梳理下逻辑，猜测在启动阶段，首先实例化DelegatingApplicationContextInitializer，之后在调用DelegatingApplicationContextInitializer#initialize方法的时候再实例化通过yml定义的初始化器，并且调用其initialize方法

- 接下来验证是否加载了DelegatingApplicationContextInitializer初始化器，全局搜索spring.factories，在spring-bean这个模块的配置中找到了如下spring.factories的配置

  ```properties
  # Application Context Initializers
  org.springframework.context.ApplicationContextInitializer=\
  org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
  org.springframework.boot.context.ContextIdApplicationContextInitializer,\
  org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
  org.springframework.boot.rsocket.context.RSocketPortInfoApplicationContextInitializer,\
  org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
  ```

- 所以，整体流程是通过加载spring.factories的时候实例化了DelegatingApplicationContextInitializer初始化器，随后再调用DelegatingApplicationContextInitializer#initialize方法，进而实例化yml配置的初始化器，并调用其initialize方法。

- 注意的是**DelegatingApplicationContextInitializer的order为0**，这就解释了之前为什么在yml配置的初始化器总是被最先调用了。

### 初始化器的initializer方法被调用的时机

#### 跟踪方法调用栈

- 找到所有初始化器的initialize方法是在SpringApplication#applyInitializers方法中被调用的

  ```java
  protected void applyInitializers(ConfigurableApplicationContext context) {
      //获取所有的初始化器（即添加到SpringApplication成员变量中的），遍历，依次调用prepareContext方法
      for (ApplicationContextInitializer initializer : getInitializers()) {
          Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
                                                                          ApplicationContextInitializer.class);
          Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
          initializer.initialize(context);
      }
  }
  
  //获取所有初始化器
  public Set<ApplicationContextInitializer<?>> getInitializers() {
      return asUnmodifiableOrderedSet(this.initializers);
  }
  ```

- 继续向上查找，发现applyInitializers方法在SpringApplication#prepareContext方法中被调用

  ```java
  private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
                              SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
      context.setEnvironment(environment);
      postProcessApplicationContext(context);
      applyInitializers(context);
      listeners.contextPrepared(context);
      //省略下面代码..........
  }
  ```

- 继续向上查找prepareContext是在我们熟悉的run(String... args) 方法中被调用的

  ```java
  public ConfigurableApplicationContext run(String... args) {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      ConfigurableApplicationContext context = null;
      Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
      configureHeadlessProperty();
      SpringApplicationRunListeners listeners = getRunListeners(args);
      listeners.starting();
      try {
          ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
          ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
          configureIgnoreBeanInfo(environment);
          Banner printedBanner = printBanner(environment);
          context = createApplicationContext();
          exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
                                                           new Class[] { ConfigurableApplicationContext.class }, context);
          
          //调用所有的初始化器
          prepareContext(context, environment, listeners, applicationArguments, printedBanner);
          
          refreshContext(context);
          //省略以下..........
  }
  ```

- 至此，发现所有的初始化器调用事件为在获取到context上下文之后，refreshContext（）方法之前，即prepareContext(context, environment, listeners, applicationArguments, printedBanner);中调用

### 总结

- 系统初始化器有三种实现方式，若为配置在yml中，则默认的order为0

- 推荐使用Spring Boot的spi机制实现，即定义在spring.factories文件中
- 系统初始化器是在SpringApplication#run中的prepareContext（）方法中调用的