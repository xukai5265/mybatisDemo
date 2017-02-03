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
mapperLocations：它表示我们的Mapper文件存放的位置，当我们的Mapper文件跟对应的Mapper接口处于同一位置的时候可以不用指定该属性的值。

configLocation：用于指定Mybatis的配置文件位置。如果指定了该属性，那么会以该配置文件的内容作为配置信息构建对应的SqlSessionFactoryBuilder，但是后续属性指定的内容会覆盖该配置文件里面指定的对应内容。

typeAliasesPackage：它一般对应我们的实体类所在的包，这个时候会自动取对应包中不包括包名的简单类名作为包括包名的别名。多个package之间可以用逗号或者分号等来进行分隔。

typeAliases：数组类型，用来指定别名的。指定了这个属性后，Mybatis会把这个类型的短名称作为这个类型的别名，前提是该类上没有标注@Alias注解，否则将使用该注解对应的值作为此种类型的别名。
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
