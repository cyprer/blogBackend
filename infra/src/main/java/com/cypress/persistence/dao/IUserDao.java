package com.cypress.persistence.dao;

import com.cypress.persistence.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IUserDao {
    UserPo findByPhone(String phone);
    UserPo findByUsername(String username);
    List<UserPo> findAllByUsername(String username);
    UserPo selectById(Long id);
    void insert(UserPo userPo);
    void updateById(UserPo userPo);
}
