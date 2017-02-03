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