# mybatisDemo4
## Spring集成MyBatis升级第四版使用tk.mapper
## 目的： Mapper接口默认实现了一些增删改查的方法，可以提高开发效率。
改动：
1. pom.xml添加mapper依赖
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
2. 创建Dog实体类
```
@Table(name = "dog")
public class Dog {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
```
3. 创建DogMapper接口继承Mapper接口
```
@Component
public interface DogMapper extends Mapper<Dog>{
}
```
4. 修改ApplicationContext.xml 扫描mapper的方式
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
5. 测试
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
