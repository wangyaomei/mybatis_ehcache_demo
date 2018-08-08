package mybatis_demo;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import mybatis_demo.entity.User;

public class Test {
	private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    static {
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
    
  
    public static void main(String[] args) {
        SqlSession session = sqlSessionFactory.openSession();
        
        SqlSession session1 = sqlSessionFactory.openSession();
        try {
            User user = (User)session.selectOne("mybatis.selectUserByID", 1);
            session.commit();
            System.out.println(user.getUsername());
            
            
            User user1 = (User)session.selectOne("mybatis.selectUserByID", 2);
            session.commit();
            System.out.println(user1.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            session1.close();
        }
    }

}
