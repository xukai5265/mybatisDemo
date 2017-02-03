package cn.xukai.spring.boot;
import static org.junit.Assert.*;
import cn.xukai.spring.boot.entity.Person;
import cn.xukai.spring.boot.entity.PersonMapper;
import cn.xukai.spring.boot.service.PersonDaoImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by xukai on 2017/2/3.
 */
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
