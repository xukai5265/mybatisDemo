# mybatisDemo3
## Spring集成MyBatis升级版
1. 去掉MyBatisCfg.xml配置文件

在没有Spring的环境下我们单纯使用MyBatis ORM框架时，我们是通过MyBatisCfg.xml完成sqlSessionFactory的构建工作，
如果使用Spring则这部分配置的内容可以完全由Spring容器替代，具体实现如下：
```
    <!--创建一个sql会话工厂bean，指定数据源 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 1指定数据源 -->
        <property name="dataSource" ref="jdbcDataSource" />
        <!-- 2类型别名包，默认引入com.zhangguo.Spring61.entities下的所有类 -->
        <property name="typeAliasesPackage" value="com.zhangguo.Spring61.entities"></property>
        <!-- 3指定sql映射xml文件的路径 -->
        <property name="mapperLocations"
            value="classpath:com/zhangguo/Spring61/mapping/*Mapper.xml"></property>
    </bean>
```
**mapperLocations：**它表示我们的Mapper文件存放的位置，当我们的Mapper文件跟对应的Mapper接口处于同一位置的时候可以不用指定该属性的值。

**configLocation：**用于指定Mybatis的配置文件位置。如果指定了该属性，那么会以该配置文件的内容作为配置信息构建对应的SqlSessionFactoryBuilder，但是后续属性指定的内容会覆盖该配置文件里面指定的对应内容。

**typeAliasesPackage：**它一般对应我们的实体类所在的包，这个时候会自动取对应包中不包括包名的简单类名作为包括包名的别名。多个package之间可以用逗号或者分号等来进行分隔。

**typeAliases：**数组类型，用来指定别名的。指定了这个属性后，Mybatis会把这个类型的短名称作为这个类型的别名，前提是该类上没有标注@Alias注解，否则将使用该注解对应的值作为此种类型的别名。
```
<property name="typeAliases">
    <array>
        <value>com.tiantian.mybatis.model.Blog</value>
        <value>com.tiantian.mybatis.model.Comment</value>
    </array>
</property>
```
我们修改了applicationContext.xml中的配置，通过容器完成了一个sqlSessionFactory Bean的创建， 1指定了数据源，2指定类型别名包这样在sql映射文件中使用类型时可以省去全名称，3指定了所有要加载的sql映射xml文件，如果有多个目录，则可以使用如下的形式：
```
        <property name="mapperLocations">
            <list>
                <value>classpath:com/...目录.../*_mapper.xml</value>
                <value>classpath:com/...目录.../*_resultmap.xml</value>
            </list>
        </property>
```
如果需要设置更多的属性则可以参考类型org.mybatis.spring.SqlSessionFactoryBean，如果不使用Spring，也不使用MyBatis配置文件我们照样可以获得一个sqlSessionFactory对象完成对MyBatis ORM框架的使用，因为可以直接实例化一个SqlSessionFactoryBean对象，只是此时该工作被Spring容器替代，按这个思路可以在SqlSessionFactoryBean类中找到更多的属性设置在applicationContext.xml配置中，部分源代码如下所示：
```
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {

  private static final Log LOGGER = LogFactory.getLog(SqlSessionFactoryBean.class);

  private Resource configLocation;

  private Configuration configuration;

  private Resource[] mapperLocations;

  private DataSource dataSource;

  private TransactionFactory transactionFactory;

  private Properties configurationProperties;

  private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

  private SqlSessionFactory sqlSessionFactory;

  //EnvironmentAware requires spring 3.1
  private String environment = SqlSessionFactoryBean.class.getSimpleName();

  private boolean failFast;

  private Interceptor[] plugins;

  private TypeHandler<?>[] typeHandlers;

  private String typeHandlersPackage;

  private Class<?>[] typeAliases;

  private String typeAliasesPackage;

  private Class<?> typeAliasesSuperType;

  //issue #19. No default provider.
  private DatabaseIdProvider databaseIdProvider;

  private Class<? extends VFS> vfs;

  private Cache cache;

  private ObjectFactory objectFactory;

  private ObjectWrapperFactory objectWrapperFactory;
}
```
如果习惯两者结合使用，当然还是可以指定MyBatis配置文件的，增加属性：<property name="configLocation" value="classpath:MyBatisCfg.xml"></property>

2. 映射接口类自动扫描配置
在示例3的applicationContext.xml配置文件中有一段实现BookTypeDAO接口实例的创建工厂，配置如下：
```
    <!-- 创建一个booTypeDAO -->
    <bean id="bookTypeDao" class="org.mybatis.spring.mapper.MapperFactoryBean">
        <!--指定映射文件 -->
        <property name="mapperInterface" value="com.zhangguo.Spring61.mapping.BookTypeDAO"></property>
        <!-- 指定sql会话工厂 -->
        <property name="sqlSessionFactory" ref="sqlSessionFactory"></property>
    </bean>
```
如果有多个表，则需要配置多段信息，麻烦。我们可以通过自动扫描一次完成，配置如下：
```
    <!--自动扫描映射接口-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 指定sql会话工厂，在上面配置过的 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!-- 指定基础包，即自动扫描com.zhangguo.Spring61.mapping这个包以及它的子包下的所有映射接口类 -->
        <property name="basePackage" value="com.zhangguo.Spring61.mapping"></property>
    </bean>
```
需要注意的是这里的sql会话工厂的指定可以使用sqlSessionFactoryBeanName属性指定，也可以使用sqlSessionFactory属性指定，但建议大家使用sqlSessionFactoryBeanName，否则会因为加载的先后顺序问题引起读不到properties文件的内容。其它属性的设置可以查看源码后再定义。

这样MapperScannerConfigurer就会扫描指定basePackage下面的所有接口，并把它们注册为一个个MapperFactoryBean对象。basePackage下面的并不全是我们定义的Mapper接口，为此MapperScannerConfigurer还为我们提供了另外两个可以缩小搜索和注册范围的属性。一个是annotationClass，另一个是markerInterface。
annotationClass：当指定了annotationClass的时候，MapperScannerConfigurer将只注册使用了annotationClass注解标记的接口。
markerInterface：markerInterface是用于指定一个接口的，当指定了markerInterface之后，MapperScannerConfigurer将只注册继承自markerInterface的接口。

sqlSessionFactory： 已废弃 。当使用多个数据源时就需要通过sqlSessionFactory来指定注册MapperFactoryBean的时候需要使用的SqlSessionFactory，因为在没有指定sqlSessionFactory的时候，会以Autowired的方式自动注入一个。换言之当我们只使用一个数据源的时候，即只定义了一个SqlSessionFactory的时候我们就可以不给MapperScannerConfigurer指定SqlSessionFactory。
sqlSessionFactoryBeanName：它的功能跟sqlSessionFactory是一样的，只是它指定的是定义好的SqlSessionFactory对应的bean名称。
sqlSessionTemplate： 已废弃 。它的功能也是相当于sqlSessionFactory的，MapperFactoryBean最终还是使用的SqlSession的getMapper方法取的对应的Mapper对象。当定义有多个SqlSessionTemplate的时候才需要指定它。对于一个MapperFactoryBean来说SqlSessionFactory和SqlSessionTemplate只需要其中一个就可以了，当两者都指定了的时候，SqlSessionFactory会被忽略。
sqlSessionTemplateBeanName：指定需要使用的sqlSessionTemplate对应的bean名称。

3. 引入属性配置文件db.properties
从示例3的配置代码中可以发现数据库连接字符信息同时出现在两个位置，分别是applicationContext.xml与db.properties文件中，如下所示：
```
    <!--定义一个jdbc数据源，创建一个驱动管理数据源的bean -->
    <bean id="jdbcDataSource"
        class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/db2" />
        <property name="username" value="root" />
        <property name="password" value="root" />
    </bean>
```
```
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/db2
username=root
password=root
```
根据我们写代码的原则“Write once only once”，连接字符信息应该只存在一个位置，我们可以采用下面的方法整合：

修改后的db.properties文件内容如下：
```
#jdbc
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/db2?useUnicode=true&characterEncoding=UTF-8
jdbc.uid=root
jdbc.pwd=root

#other ..
other.msg=hello
```
修改后的 applicationContext.xml文件内容如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
    xmlns:aop="http://www.springframework.org/schema/aop" xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.3.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-4.3.xsd">

    <!--属性占位文件引入，可以通过${属性名}获得属性文件中的内容-->
    <context:property-placeholder location="classpath:db.properties"/>

    <!--定义一个jdbc数据源，创建一个驱动管理数据源的bean -->
    <bean id="jdbcDataSource"
        class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${jdbc.driver}" />
        <property name="url" value="${jdbc.url}" />
        <property name="username" value="${jdbc.uid}" />
        <property name="password" value="${jdbc.pwd}" />
    </bean>

    <!--创建一个sql会话工厂bean，指定数据源 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 指定数据源 -->
        <property name="dataSource" ref="jdbcDataSource" />
        <!--类型别名包，默认引入com.zhangguo.Spring61.entities下的所有类 -->
        <property name="typeAliasesPackage" value="com.zhangguo.Spring61.entities"></property>
        <!--指定sql映射xml文件的路径 -->
        <property name="mapperLocations"
            value="classpath:com/zhangguo/Spring61/mapping/*Mapper.xml"></property>
    </bean>

    <!--自动扫描映射接口-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 指定sql会话工厂，在上面配置过的 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!-- 指定基础包，即自动扫描com.zhangguo.Spring61.mapping这个包以及它的子包下的所有映射接口类 -->
        <property name="basePackage" value="com.zhangguo.Spring61.mapping"></property>
    </bean>

    <!--下面的配置暂时未使用 -->
    <context:component-scan base-package="com.zhangguo.Spring61">
    </context:component-scan>
    <aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
</beans>
```