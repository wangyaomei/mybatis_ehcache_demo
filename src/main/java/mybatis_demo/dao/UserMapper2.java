package mybatis_demo.dao;

import mybatis_demo.entity.User;

public interface UserMapper2 {
	User selectUserByID(int id);
}
