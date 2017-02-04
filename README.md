# mybatisDemo4
## Spring集成MyBatis升级第四版使用tk.mapper
## 目的： 
Mapper接口默认实现了一些增删改查的方法，省去编写实体与表的映射关系的xml配置文件，可以提高开发效率。
##  改动： 
**1.pom.xml添加mapper依赖**
```
    <dependency>
      <groupId>tk.mybatis</groupId>
      <artifactId>mapper</artifactId>
      <version>3.3.7</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>4.2.5.RELEASE</version>
      <scope>test</scope>
    </dependency>
```
**2.创建Dog实体类**  
```
@Table(name = "dog")
public class Dog {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
```
**3.创建DogMapper接口继承Mapper接口**
```
@Component
public interface DogMapper extends Mapper<Dog>{
}
```
**4.修改ApplicationContext.xml 扫描mapper的方式**
```
修改前：
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <!-- 指定sql会话工厂，在上面配置过的 -->
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
    <!-- 指定基础包，即自动扫描com.zhangguo.Spring61.mapping这个包以及它的子包下的所有映射接口类 -->
    <property name="basePackage" value="cn.xukai.spring.boot.entity"></property>
</bean>
修改后：
<bean class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
    <!-- 指定sql会话工厂，在上面配置过的 -->
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
    <!-- 指定基础包，即自动扫描com.zhangguo.Spring61.mapping这个包以及它的子包下的所有映射接口类 -->
    <property name="basePackage" value="cn.xukai.spring.boot.entity"></property>
</bean>
```
**5.测试**
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/ApplicationContext.xml"})
public class DogMapperTest {

    @Autowired
    private DogMapper dogMapper;
    @Test
    public void testMapper(){
        Dog dog = new Dog();
        dog.setId(1);
        dog.setName("wangwang");
        dogMapper.insert(dog);
    }
}
```
### 数据源与连接池
通过连接池可以增加数据访问的性能，因为访问数据库时建立连接与释放连接是耗时操作，JDBC默认不带连接池技术，但MyBatis是内置连接池功能的，还有一些第三方知名的连接池技术如：DBCP、C3P0、Druid（德鲁伊）。
**1、DBCP**  
DBCP 是 Apache 软件基金组织下的开源连接池实现，要使用DBCP数据源，需要应用程序应在系统中增加如下两个 jar 文件：Commons-dbcp.jar：连接池的实现、Commons-pool.jar：连接池实现的依赖库，常用属性如下：
```
#连接设置
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/jdbcstudy
username=root
password=XDP

#<!-- 初始化连接 -->
initialSize=10
#最大连接数量
maxActive=50
#<!-- 最大空闲连接 -->
maxIdle=20
#<!-- 最小空闲连接 -->
minIdle=5
#<!-- 超时等待时间以毫秒为单位 6000毫秒/1000等于60秒 -->
maxWait=60000
#JDBC驱动建立连接时附带的连接属性属性的格式必须为这样：[属性名=property;] 
#注意："user" 与 "password" 两个属性会被明确地传递，因此这里不需要包含他们。
connectionProperties=useUnicode=true;characterEncoding=UTF8
#指定由连接池所创建的连接的自动提交（auto-commit）状态。
defaultAutoCommit=true
#driver default 指定由连接池所创建的连接的只读（read-only）状态。
#如果没有设置该值，则“setReadOnly”方法将不被调用。（某些驱动并不支持只读模式，如：Informix）
defaultReadOnly=
#driver default 指定由连接池所创建的连接的事务级别（TransactionIsolation）。
#可用值为下列之一：（详情可见javadoc。）NONE,READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
defaultTransactionIsolation=READ_UNCOMMITTED
```
在与Spring整合时设置：
```
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql://localhost:3306/hlp?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull"></property>
        <property name="username" value="root"></property>
        <property name="password" value="1234"></property>
        <property name="maxActive" value="100"></property>
        <property name="maxIdle" value="30"></property>
        <property name="maxWait" value="500"></property>
        <property name="defaultAutoCommit" value="true"></property>
    </bean>
```
 使用上面的代码替换原数据源的创建
 **2.C3P0**  
 C3P0是一个开源的JDBC连接池，它实现了数据源和JNDI绑定，支持JDBC3规范和JDBC2的标准扩展。目前使用它的开源项目有Hibernate，Spring等。C3P0数据源在项目开发中使用得比较多。dbcp没有自动回收空闲连接的功能，而c3p0有自动回收空闲连接功能。

在pom.xml中添加依赖：
```
  <!--c3p0 连接池 -->
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>
```
 在与Spring整合时修改applicationContext.xml，设置如下：
 ```
s <!--定义一个jdbc数据源，创建一个驱动管理数据源的bean -->
    <bean id="jdbcDataSource"
        class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
        <property name="driverClass" value="${jdbc.driver}" />
        <property name="jdbcUrl" value="${jdbc.url}" />
        <property name="user" value="${jdbc.uid}" />
        <property name="password" value="${jdbc.pwd}" />
        <property name="acquireIncrement" value="5"></property>
        <property name="initialPoolSize" value="10"></property>
        <property name="minPoolSize" value="5"></property>
        <property name="maxPoolSize" value="20"></property>
    </bean>
```
**3.Druid**  
Druid首先是一个数据库连接池，但它不仅仅是一个数据库连接池，它还包含一个ProxyDriver，一系列内置的JDBC组件库，一个SQL Parser。阿里巴巴是一个重度使用关系数据库的公司，我们在生产环境中大量的使用Druid，通过长期在极高负载的生产环境中实际使用、修改和完善，让Druid逐步发展成最好的数据库连接池。Druid在监控、可扩展性、稳定性和性能方面都有明显的优势。

在与Spring整合时设置：
```
<!-- 配置数据源，使用的是alibaba的Druid(德鲁伊)数据源 -->
    <bean name="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
        <property name="url" value="${jdbc_url}" />
        <property name="username" value="${jdbc_username}" />
        <property name="password" value="${jdbc_password}" />
        <!-- 初始化连接大小 -->
        <property name="initialSize" value="0" />
        <!-- 连接池最大使用连接数量 -->
        <property name="maxActive" value="20" />
        <!-- 连接池最大空闲 -->
        <property name="maxIdle" value="20" />
        <!-- 连接池最小空闲 -->
        <property name="minIdle" value="0" />
        <!-- 获取连接最大等待时间 -->
        <property name="maxWait" value="60000" />
        <!-- 
        <property name="poolPreparedStatements" value="true" /> 
        <property name="maxPoolPreparedStatementPerConnectionSize" value="33" /> 
        -->
        <property name="validationQuery" value="${validationQuery}" />
        <property name="testOnBorrow" value="false" />
        <property name="testOnReturn" value="false" />
        <property name="testWhileIdle" value="true" />
        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000" />
        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="25200000" />
        <!-- 打开removeAbandoned功能 -->
        <property name="removeAbandoned" value="true" />
        <!-- 1800秒，也就是30分钟 -->
        <property name="removeAbandonedTimeout" value="1800" />
        <!-- 关闭abanded连接时输出错误日志 -->
        <property name="logAbandoned" value="true" />
        <!-- 监控数据库 -->
        <!-- <property name="filters" value="stat" /> -->
        <property name="filters" value="mergeStat" />
    </bean>
```
 使用上面的代码替换原数据源的创建。

 maven依赖方式：
 ```
<dependency>
     <groupId>com.alibaba</groupId>
     <artifactId>druid</artifactId>
     <version>1.0.20</version>
</dependency>
 ```
 ###使用SqlSession  
 
 Mybatis-Spring为我们提供了一个实现了SqlSession接口的SqlSessionTemplate类，它是线程安全的，可以被多个Dao同时使用。同时它还跟Spring的事务进行了关联，确保当前被使用的SqlSession是一个已经和Spring的事务进行绑定了的。而且它还可以自己管理Session的提交和关闭。当使用了Spring的事务管理机制后，SqlSession还可以跟着Spring的事务一起提交和回滚。修改applicationContext.xml文件，增加一个bean。

SqlSessionTemplate 是 MyBatis-Spring 的核心。 这个类负责管理 MyBatis 的 SqlSession, 调用 MyBatis 的 SQL 方法, 翻译异常。 SqlSessionTemplate 是线程安全的, 可以被多个 DAO 所共享使用。
当调用 SQL 方法时, 包含从映射器 getMapper()方法返回的方法, SqlSessionTemplate 将会保证使用的 SqlSession 是和当前 Spring 的事务相关的。此外,它管理 session 的生命 周期,包含必要的关闭,提交或回滚操作。
SqlSessionTemplate 实现了 SqlSession 接口,这就是说,在代码中无需对 MyBatis 的 SqlSession 进行替换。 SqlSessionTemplate 通常是被用来替代默认的 MyBatis 实现的 DefaultSqlSession , 因为模板可以参与到 Spring 的事务中并且被多个注入的映射器类所使 用时也是线程安全的。相同应用程序中两个类之间的转换可能会引起数据一致性的问题。
SqlSessionTemplate 对象可以使用 SqlSessionFactory 作为构造方法的参数来创建。
```
<!-- 创建一个sqlSession对象 -->
    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <constructor-arg index="0" ref="sqlSessionFactory" />
    </bean>
    
    <!--创建一个BTDImpl对象 -->
    <bean id="bTDImpl" class="com.zhangguo.Spring61.dao.BTDImpl">
        <property name="sqlSession" ref="sqlSession"></property>
    </bean>
```
 新增一个BTDImpl类，实现接口BookTypeDAO，具体如下：
 ```
 public class BTDImpl implements BookTypeDAO {
    private SqlSession sqlSession;

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<BookType> getAllBookTypes() {
        return sqlSession.selectList("com.zhangguo.Spring61.mapping.BookTypeDAO.getAllBookTypes");
    }

}
 ```
 使用Spring的依赖注入在DAO中直接使用SqlSessionTemplate来访问数据库了。
 ```
 public class TestMyBatisSpring02 {
    @Test
    public void test01() {
        //初始化容器
        ApplicationContext ctx=new ClassPathXmlApplicationContext("ApplicationContext.xml");
        //获得bean
        BTDImpl bTDImpl=ctx.getBean("bTDImpl",BTDImpl.class);
        //访问数据库
        List<BookType> booktypes=bTDImpl.getAllBookTypes();
        for (BookType bookType : booktypes) {
            System.out.println(bookType);
        }
        assertNotNull(booktypes);
    }
}
 ```
 
 也可以通过自动装配实现，使配置更加简单，在配置文件中可以删除“创建一个BTDImpl对象”这一段，在类BTDImpl中增加注解，代码如下：
 ```
 @Repository
public class BTDImpl implements BookTypeDAO {
    @Resource
    private SqlSession sqlSession;

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<BookType> getAllBookTypes() {
        return sqlSession.selectList("com.zhangguo.Spring61.mapping.BookTypeDAO.getAllBookTypes");
    }

}
 ```
 测试方法获得bean要修改成：BTDImpl bTDImpl=ctx.getBean(BTDImpl.class);，运行结果同上。
