package mybatis_demo;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import mybatis_demo.dao.UserMapper;
import mybatis_demo.dao.UserMapper2;
import mybatis_demo.entity.User;

public class MybatisTest {
	private static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	@Before
	public void init() throws IOException {
		reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

	}
	/*一级缓存，范围：SqlSeesion关闭之前
	    一级缓存区域是根据SqlSession为单位划分的。
	   每次查询会先从缓存区域找，如果找不到从数据库查询，查询到数据将数据写入缓存。
	  Mybatis内部存储缓存使用一个HashMap，key为hashCode+sqlId+Sql语句。value为从查询出来映射生成的java对象
     sqlSession执行insert、update、delete等操作commit提交后会清空缓存区域。
     	查询条件不一样是重新查询，既缓存没用
	 */

	@Test
	public void getUserById() throws IOException {
		// 第四步，创建SqlSession对象
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 第五步，使用SqlSession对象执行查询，得到User对象
		// 第一个参数：执行查询的StatementId
		User user = sqlSession.selectOne("mybatis.selectUserByID", 1);
		User user1 = sqlSession.selectOne("mybatis.selectUserByID", 1);
		// 第六步，打印结果
		System.out.println(user);
		System.out.println(user1);
		// 第七步，释放资源，每一个sqlSession就是一个连接
		sqlSession.close();
	}
	@Test
	public void getUserById2() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		User user1 = userMapper.selectUserByID(1);
		System.out.println(user1);
		User user2 = userMapper.selectUserByID(2);
		System.out.println(user2);
		sqlSession.close();
	}
	/*
	 * 二级缓存:二级缓存区域是根据mapper的namespace划分的
	 * sqlSession执行insert、update、delete等操作commit提交后会清空缓存区域。
	 * 6.3.2	开启二级缓存：
在核心配置文件SqlMapConfig.xml中加入
<setting name="cacheEnabled" value="true"/>

要在你的Mapper映射文件中添加一行：  <cache /> ，表示此mapper开启二级缓存。

实现序列化
	二级缓存需要查询结果映射的pojo对象实现java.io.Serializable接口实现序列化和反序列化操作，注意如果存在父类、成员pojo都需要实现序列化接口。
	public class Orders implements Serializable
	public class User implements Serializable

	mybatis默认的二级缓存，无法实现分布式缓存，所以需要整合其他缓存框架，实现分布式缓存，如ehcache、memcache、redis等，

	这里演示整合ehcache
	 * 
	 */
	@Test
	public void getUserById3() {
		//获取session1
		SqlSession session1 = sqlSessionFactory.openSession();
		UserMapper2 userMapper = session1.getMapper(UserMapper2.class);
		//使用session1执行第一次查询
		User user1 = userMapper.selectUserByID(1);
		System.out.println(user1);
		//关闭session1
		session1.close();

		//获取session2
		SqlSession session2 = sqlSessionFactory.openSession();
		UserMapper2 userMapper2 = session2.getMapper(UserMapper2.class);
		//使用session2执行第二次查询，由于开启了二级缓存这里从缓存中获取数据不再向数据库发出sql
		User user2 = userMapper2.selectUserByID(1);
		System.out.println(user2);
		//关闭session2
		session2.close();

		//获取session3,换一个namespace，自己会查一次
		SqlSession session3 = sqlSessionFactory.openSession();
		UserMapper userMapper3 = session3.getMapper(UserMapper.class);
		//使用session2执行第二次查询，由于开启了二级缓存这里从缓存中获取数据不再向数据库发出sql
		User user3 = userMapper3.selectUserByID(1);
		System.out.println(user3);
		//关闭session2
		session3.close();

	}

	@Test
	public void getUserById4() throws IOException {
		// 第四步，创建SqlSession对象
		SqlSession sqlSession1 = sqlSessionFactory.openSession();
		// 第五步，使用SqlSession对象执行查询，得到User对象
		// 第一个参数：执行查询的StatementId
		User user1 = sqlSession1.selectOne("mybatis.selectUserByID", 1);
		// 第六步，打印结果
		System.out.println(user1);
		// 第七步，释放资源，每一个sqlSession就是一个连接
		sqlSession1.close();

		// 第四步，创建SqlSession对象
		SqlSession sqlSession2 = sqlSessionFactory.openSession();
		// 第五步，使用SqlSession对象执行查询，得到User对象
		// 第一个参数：执行查询的StatementId
		User user2 = sqlSession2.selectOne("mybatis.selectUserByID", 1);
		// 第六步，打印结果
		System.out.println(user2);
		// 第七步，释放资源，每一个sqlSession就是一个连接
		sqlSession2.close();
	}

	@Test
	public void testSelectList() {
		SqlSession sqlSession1 = sqlSessionFactory.openSession();
		String sql = "select * from user";
		String sql2 = "select * from user where id = 2";

		//虽然mybatis.selectList 的返回值类型为map，但是当mybatis.selectList"调用selectList方法是可以正常使用
		List<Map<String, Object>> list = sqlSession1.selectList("mybatis.selectList", sql);

		Map<String, Object> map = sqlSession1.selectOne("mybatis.selectList", sql2);
		System.out.println(map);

		System.out.println(list);

		sqlSession1.close();
		
		SqlSession sqlSession2 = sqlSessionFactory.openSession();
		List<Map<String, Object>> list1 = sqlSession2.selectList("mybatis.selectList", sql);

		Map<String, Object> map1 = sqlSession2.selectOne("mybatis.selectList", sql2);

		sqlSession2.close();
	}

	/*
	 * 
	 * 
	 * 
	 */









}
