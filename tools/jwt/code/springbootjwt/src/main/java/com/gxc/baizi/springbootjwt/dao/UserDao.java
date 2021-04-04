package com.gxc.baizi.springbootjwt.dao;

import com.gxc.baizi.springbootjwt.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.MappedTypes;

@Mapper
public interface UserDao {

    User login(User user);
}
