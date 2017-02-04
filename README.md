# mybatisDemo  
mybatis maven 配置
本文参照： http://www.cnblogs.com/best/archive/2016/07/04/5638827.html#_lab2_1_5
## 使用MyBatis完成MySQL数据库访问
**1.添加依赖**
要完成使用MyBatis访问MySQL数据库，需要添加一些依赖包，包含MyBatis3，连接驱动，JUnit，Log4j2等。可以去共享资源库中搜索，第一个网站地址是：http://mvnrepository.com/， 这里以搜索连接驱动为示例，搜索后的结果有5.xx版许多，也有6.xx版，但不建议使用6.xx版，因为MyBatis3不支持。
```
pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>cn.xukai.spring.boot</groupId>
  <artifactId>springbootDemo</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>springbootDemo</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jdk.version>1.8</jdk.version>
    <JAVA_HOME>C:\Program Files\Java\jdk1.8.0_91</JAVA_HOME>
  </properties>

  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.40</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-core</artifactId>
      <version>2.6.1</version>
    </dependency>
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.4.1</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.10</version>
    </dependency>
  </dependencies>

  <build>
    <sourceDirectory>src/main/java</sourceDirectory>
    <resources>
      <resource>
        <directory>src/main/resources</directory>
        <includes>
          <include>**/*.xml</include>
          <include>**/*.properties</include>
        </includes>
        <filtering>true</filtering>
      </resource>
      <resource>
        <directory>src/main/java</directory>
        <includes>
          <include>**/*.xml</include>
          <include>**/*.properties</include>
        </includes>
        <filtering>true</filtering>
      </resource>
    </resources>

    <plugins>
      <!-- Set JDK Compiler Level -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>${jdk.version}</source>
          <target>${jdk.version}</target>
          <encoding>UTF-8</encoding>
          <compilerArguments>
            <bootclasspath>${JAVA_HOME}/jre/lib/rt.jar</bootclasspath>
          </compilerArguments>
        </configuration>
      </plugin>

      <!--   要将源码放上去，需要加入这个插件    -->
      <plugin>
        <artifactId>maven-source-plugin</artifactId>
        <version>2.1.2</version>
        <configuration>
          <attach>true</attach>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>jar</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```
**2.准备数据**
```
CREATE TABLE `person` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;

```
**3.创建java Bean **
```
public class Person {
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
```
**4.创建实体与表的映射文件**
这里用接口+XML的形式完成，BookType数据访问接口如下：
```
public interface PersonMapper{
    List<Person> getAllPerson();
}

PersonMapper.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--命名空间应该是对应接口的包名+类名 -->
<mapper namespace="cn.xukai.spring.boot.entity.PersonMapper">
    <!--id应该是接口中的方法，结果类型如没有配置别名则应该使用全名称 -->
    <select id="getAllPerson" resultType="cn.xukai.spring.boot.entity.Person">
        select id,name from person
    </select>
</mapper>
```
**5.创建MyBatisCfg.xml文件**
MyBatisCfg.xml文件用于配置MyBatis的运行环境，内容如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 指定数据库连接信息的位置 -->
    <properties resource="db.properties"></properties>
    <!--类型别名，默认引入com.zhangguo.Spring61.entities下的所有类 -->
    <typeAliases>
        <package name="cn.xukai.spring.boot.entity"/>
    </typeAliases>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC" />
            <dataSource type="POOLED">
                <property name="driver" value="${driver}" />
                <property name="url" value="${url}" />
                <property name="username" value="${username}" />
                <property name="password" value="${password}" />
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <!--引入映射文件 -->
        <mapper resource="cn/xukai/spring/boot/entity/PersonMapper.xml" />
    </mappers>
</configuration>
```
因为配置中依赖了db.properties文件，该文件用于指定数据库的连接信息，内容如下：
```
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/test
username=root
password=root
```
**6.实现数据访问功能**
为了更加方便的复用MyBatis实现数据访问不需要频繁的创建SQLSessionFactory和SQLSession对象，封装一个MyBatisUtil工具类如下：
```
package cn.xukai.spring.boot.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * Created by xukai on 2017/2/3.
 */
public abstract class MyBatisUtil {
    //GC不理static
    private static SqlSessionFactory factory=null;
    public static SqlSessionFactory getSqlSessionFactory(){
        if(factory==null){
            // 获得环境配置文件流
            InputStream config = MyBatisUtil.class.getClassLoader().getResourceAsStream("MyBatisCfg.xml");
            // 创建sql会话工厂
            factory = new SqlSessionFactoryBuilder().build(config);
        }
        return factory;
    }

    //获得会话
    public static SqlSession getSession(){
        return getSqlSessionFactory().openSession(true);
    }

    /**
     * 获得得sql会话
     * @param isAutoCommit 是否自动提交，如果为false则需要sqlSession.commit();rollback();
     * @return sql会话
     */
    public static SqlSession getSession(boolean isAutoCommit){
        return getSqlSessionFactory().openSession(isAutoCommit);
    }
}

```
**7.Mapper实现类 **  
```
public class PersonDaoImpl implements PersonMapper{

    @Override
    public List<Person> getAllPerson() {
        //获得会话对象
        SqlSession session= MyBatisUtil.getSession();
        try {
            //通过MyBatis实现接口BookTypeDAO，返回实例
            PersonMapper bookTypeDAO=session.getMapper(PersonMapper.class);
            return bookTypeDAO.getAllPerson();
        } finally {
            session.close();
        }
    }
}

```
**8.测试 **
```
public class TestBookTypeDAOImpl {
    static PersonMapper bookTypeDao;
    @BeforeClass
    public static void beforeClass()
    {
        bookTypeDao=new PersonDaoImpl();
    }

    @Test
    public void testGetAllBookTypes() {
        List<Person> booktypes=bookTypeDao.getAllPerson();
        for (Person bookType : booktypes) {
            System.out.println(bookType);
        }
        assertNotNull(booktypes);
    }
}
```
**9.整合log4j2 **
面的测试虽然通过，但是有一个错误提示“ERROR StatusLogger No log4j2 configuration file found. Using default configuration: logging only errors to the console.”，大意是：日志记录器没有找到log4j2的配置文件。在源码的根目录下创建一个log4j2.xml配置文件，文件内容如下所示：
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="off" monitorInterval="1800">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>
    </Appenders>

    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```
