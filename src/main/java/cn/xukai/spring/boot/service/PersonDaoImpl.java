package cn.xukai.spring.boot.service;

import cn.xukai.spring.boot.entity.Person;
import cn.xukai.spring.boot.entity.PersonMapper;
import cn.xukai.spring.boot.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by xukai on 2017/2/3.
 */
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
