package com.cypress.persistence.repository;

import com.cypress.constants.Constants;
import com.cypress.enums.VerificationResult;
import com.cypress.persistence.dao.IUserDao;
import com.cypress.persistence.po.UserPo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.cypress.user.model.entity.User;
import com.cypress.user.repository.IUserRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class UserRepository implements IUserRepository {
    @Autowired
    private IUserDao userDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public User findByPhone(String phone) {
        UserPo userPo = userDao.findByPhone(phone);
        return convertToDomain(userPo);
    }

    @Override
    public User findByUsername(String username) {
        UserPo userPo = userDao.findByUsername(username);
        return convertToDomain(userPo);
    }

    @Override
    public List<User> findAllByUsername(String username) {
        List<UserPo> userPos = userDao.findAllByUsername(username);
        return userPos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserPo userPo = convertToPo(user);
        userDao.insert(userPo);
        return user;
    }

    @Override
    public User findByUserId(Long userId) {
        UserPo userPo = userDao.selectByUserId(userId);
        return convertToDomain(userPo);
    }

    @Override
    public User update(User user) {
        UserPo userPo = convertToPo(user);
        userDao.updateByUserId(userPo);
        user = findByUserId(user.getUserId());
        return user;
    }

    @Override
    public void saveCode(String phone, String code) {
        String redisKey = Constants.UserConstants.USER + phone + Constants.RedisConstants.VERIFICATION_CODE_PREFIX;
        long expire = Constants.RedisConstants.VERIFICATION_CODE_EXPIRE;
        stringRedisTemplate.opsForValue().set(redisKey, code, expire, TimeUnit.MILLISECONDS
        );

    }

    @Override
    public VerificationResult validCode(String phone, String code) {
        String redisKey = Constants.UserConstants.USER + phone + Constants.RedisConstants.VERIFICATION_CODE_PREFIX;
        String redisCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisCode == null) {
            return VerificationResult.EXPIRED;  // 验证码过期
        }

        if (redisCode.equals(code)) {
            return VerificationResult.SUCCESS;  // 验证成功
        } else {
            return VerificationResult.INVALID;  // 验证码错误
        }
    }

    @Override
    public User findByEmail(String email) {
        UserPo userPo = userDao.findByEmail(email);
        return convertToDomain(userPo);
    }

    /**
     * 将数据实体转换为领域实体
     * @param userPo 数据实体
     * @return 领域实体
     */
    private User convertToDomain(UserPo userPo) {
        if (userPo == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userPo, user);
        return user;
    }

    /**
     * 将领域实体转换为数据实体
     * @param user 领域实体
     * @return 数据实体
     */
    private UserPo convertToPo(User user) {
        if (user == null) {
            return null;
        }
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(user, userPo);
        return userPo;
    }
}