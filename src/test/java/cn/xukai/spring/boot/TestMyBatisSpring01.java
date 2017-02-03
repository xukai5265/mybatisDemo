package cn.xukai.spring.boot;
import cn.xukai.spring.boot.entity.Person;
import cn.xukai.spring.boot.entity.PersonMapper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.assertNotNull;
/**
 * Created by xukai on 2017/2/3.
 */
public class TestMyBatisSpring01 {
    @Test
    public void test01() {
        //初始化容器
        ApplicationContext ctx=new ClassPathXmlApplicationContext("ApplicationContext.xml");
        //获得bean
        PersonMapper bookTypeDao=ctx.getBean(PersonMapper.class);
        //访问数据库
        List<Person> booktypes=bookTypeDao.getAllPerson();
        for (Person bookType : booktypes) {
            System.out.println(bookType);
        }
        assertNotNull(booktypes);
    }
}
