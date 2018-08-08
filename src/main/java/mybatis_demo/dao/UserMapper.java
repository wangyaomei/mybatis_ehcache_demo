package mybatis_demo.dao;

import mybatis_demo.entity.User;

public interface UserMapper {
	User selectUserByID(int id);
}
