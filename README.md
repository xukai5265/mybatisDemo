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