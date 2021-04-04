package com.gxc.baizi.springbootjwt.serivce;

import com.gxc.baizi.springbootjwt.dao.UserDao;
import com.gxc.baizi.springbootjwt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Transactional
public class UserService {

    @Autowired
    UserDao userDao;

//    @Transactional(propagation = Propagation.SUPPORTS)
    public User login(User user){
        System.out.println(user+"========UserService===========");
        User u = userDao.login(user);
        if (u!=null){
            return u;
        }
        throw new RuntimeException("登录失败");
    }
}
