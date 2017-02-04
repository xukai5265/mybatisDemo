package cn.xukai.spring.boot;

import cn.xukai.spring.boot.entity.animal.Dog;
import cn.xukai.spring.boot.entity.animal.DogMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xukai on 2017/2/4.
 */
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
