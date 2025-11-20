package com.cypress.persistence.dao;

import com.cypress.persistence.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IUserDao {
    UserPo findByPhone(String phone);
    UserPo findByUsername(String username);
    List<UserPo> findAllByUsername(String username);
    UserPo selectByUserId(Long id);
    UserPo findByEmail(String email);
    void insert(UserPo userPo);
    void updateByUserId(UserPo userPo);
}
