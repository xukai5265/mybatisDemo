# mybatisDemo2
使用Spring4.X整合MyBatis3.X初级版
本文参照： http://www.cnblogs.com/best/archive/2016/07/04/5638827.html#_lab2_1_5
在MyBatis的github官网（https://github.com/mybatis/spring）中有一个叫MyBatis Spring Adapter（MyBatis-Spring）的库，
暂且翻译成：MyBatis Spring适配器，它的作用是：原话：
“MyBatis-Spring adapter is an easy-to-use Spring3 bridge for MyBatis sql mapping framework.”，
就是了为更容易的将MyBatis与Spring整合，充分发挥二两结合的优势，它相当于一个桥。

什么是：MyBatis-Spring?
MyBatis-Spring会帮助你将MyBatis代码无缝地整合到Spring中。
使用这个类库中的类,Spring将会加载必要的MyBatis工厂类和session类。
这个类库也提供一个简单的方式来注入MyBatis数据映射器和SqlSession到业务层的bean中。
而且它也会处理事务,翻译MyBatis的异常到Spring的DataAccessException异常(数据访问异常,译者注)中。
最终,它并不会依赖于MyBatis,Spring或MyBatis-Spring来构建应用程序代码。  

**1. 修改pom.xml添加依赖**  

为了将Spring与MyBatis整合完成，需要依赖MyBatis，因为在上面的示例中已依赖完成，这里就不再需要，主要需依赖的是Spring核心，AOP，JDBC，MyBatis-Spring等jar包。具体的依赖结果pom.xml文件如下所示：
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.zhangguo</groupId>
    <artifactId>Spring061</artifactId>
    <version>0.0.1</version>
    <packaging>war</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring.version>4.3.0.RELEASE</spring.version>
    </properties>

    <dependencies>
        <!--mysql数据库驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.38</version>
        </dependency>
        <!--log4j日志包 -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.6.1</version>
        </dependency>
        <!-- mybatis ORM框架 -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.1</version>
        </dependency>
        <!-- JUnit单元测试工具 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.10</version>
        </dependency>
        <!--mybatis-spring适配器 -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.0</version>
        </dependency>
        <!--Spring框架核心库 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!-- aspectJ AOP 织入器 -->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.9</version>
        </dependency>
        <!--Spring java数据库访问包，在本例中主要用于提供数据源 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>
    </dependencies>
</project>
```
**2. 创建Spring上线文初始化配置文件**
该文件取名为ApplicationContext.xml主要原因是“约束优于配置”的理由，使用Web监听器加载Spring时会默认找该名称的文件。在文件中我们可像以前学习Spring一样配置IOC与AOP，只不过这里整合了一些MyBatis内容。文件内容如下：
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

    <!--1定义一个jdbc数据源，创建一个驱动管理数据源的bean -->
    <bean id="jdbcDataSource"
        class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/db2" />
        <property name="username" value="root" />
        <property name="password" value="root" />
    </bean>

    <!--2创建一个sql会话工厂bean，指定数据源-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="jdbcDataSource" /><!-- 指定数据源 -->
        <property name="configLocation" value="classpath:MyBatisCfg.xml"></property> <!-- 指定配置文件 -->
    </bean>

    <!--3创建一个booTypeDAO-->
    <bean id="bookTypeDao" class="org.mybatis.spring.mapper.MapperFactoryBean">
        <!--指定映射文件 -->
        <property name="mapperInterface" value="com.zhangguo.swd.mapping.BookTypeDAO"></property>
        <!-- 指定sql会话工厂-->
        <property name="sqlSessionFactory" ref="sqlSessionFactory"></property>
    </bean>
    <!--下面的配置暂时未使用 -->
    <context:component-scan base-package="com.zhangguo">
    </context:component-scan>
    <aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
</beans>
```
**3. 运行测试**
```
public class TestMyBatisSpring01 {
    @Test
    public void test01() {
        //初始化容器
        ApplicationContext ctx=new ClassPathXmlApplicationContext("ApplicationContext.xml");
        //获得bean
        BookTypeDAO bookTypeDao=ctx.getBean("bookTypeDao",BookTypeDAO.class);
        //访问数据库
        List<BookType> booktypes=bookTypeDao.getAllBookTypes();
        for (BookType bookType : booktypes) {
            System.out.println(bookType);
        }
        assertNotNull(booktypes);
    }
}
```








