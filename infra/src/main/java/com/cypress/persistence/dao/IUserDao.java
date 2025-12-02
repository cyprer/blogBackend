package com.cypress.persistence.dao;

import com.cypress.persistence.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUserDao {
    UserPo findByPhone(String phone);
    UserPo findByUsername(String username);
    List<UserPo> findAllByUsername(String username);
    UserPo selectByUserId(Long id);
    UserPo findByEmail(String email);
    UserPo selectById(Long id);
    void insert(UserPo userPo);
    void updateByUserId(UserPo userPo);
    void updateUserIdById(@Param("id") Long id, @Param("newUserId") Long newUserId);
}