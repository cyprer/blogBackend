package com.cypress.user.service;

import com.cypress.request.UpdateUserInfoRequest;
import com.cypress.response.Response;
import com.cypress.user.model.entity.User;
import com.cypress.user.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UserDomainService 单元测试
 * 测试领域层的用户信息更新逻辑
 */
@ExtendWith(MockitoExtension.class)
public class UserDomainServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserDomainService userDomainService;

    private User testUser;
    private UpdateUserInfoRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setAge(25);
        testUser.setGender(1);
        testUser.setAvatarUrl("http://example.com/avatar.jpg");
        testUser.setBio("测试用户简介");
        testUser.setSignature("测试签名");
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());

        updateRequest = new UpdateUserInfoRequest();
    }

    @Test
    void testUpdateUserInfo_SuccessWithoutUserIdChange() {
        System.out.println("测试：普通字段更新（userId不变）");

        // 准备测试数据 - 只更新普通字段
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setUsername("newusername");
        updateRequest.setAge(26);

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByUsername("newusername")).thenReturn(null);
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(null);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user; // 返回更新后的用户
        });

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("200", response.getCode());
        assertEquals("更新用户信息成功", response.getInfo());
        assertNotNull(response.getData());

        User updatedUser = response.getData();
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("newusername", updatedUser.getUsername());
        assertEquals(26, updatedUser.getAge());
        assertEquals(1L, updatedUser.getUserId()); // userId不变

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository).findByUsername("newusername");
        verify(userRepository).findByEmail("newemail@example.com");
        verify(userRepository).update(any(User.class));

        System.out.println("✅ 普通字段更新测试通过");
    }

    @Test
    void testUpdateUserInfo_SuccessWithUserIdChange() {
        System.out.println("测试：userId更新场景");

        // 准备测试数据 - 更新userId
        updateRequest.setUserId(2L);
        updateRequest.setEmail("newemail@example.com");

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByUserId(2L)).thenReturn(null); // 新userId不存在
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(null);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("200", response.getCode());
        assertEquals("更新用户信息成功", response.getInfo());

        User updatedUser = response.getData();
        assertEquals(2L, updatedUser.getUserId()); // userId已改变
        assertEquals("newemail@example.com", updatedUser.getEmail());

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository).findByUserId(2L); // 验证新userId是否已存在
        verify(userRepository).update(any(User.class));

        System.out.println("✅ userId更新测试通过");
    }

    @Test
    void testUpdateUserInfo_UserNotFound() {
        System.out.println("测试：用户不存在");

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(null);

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("400", response.getCode());
        assertEquals("用户不存在", response.getInfo());
        assertNull(response.getData());

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository, never()).update(any(User.class));

        System.out.println("✅ 用户不存在测试通过");
    }

    @Test
    void testUpdateUserInfo_UserIdAlreadyExists() {
        System.out.println("测试：userId已存在");

        // 准备测试数据 - 尝试更新到已存在的userId
        updateRequest.setUserId(2L);

        User existingUser = new User();
        existingUser.setUserId(2L);

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByUserId(2L)).thenReturn(existingUser);

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("400", response.getCode());
        assertEquals("用户ID已存在", response.getInfo());
        assertNull(response.getData());

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository).findByUserId(2L);
        verify(userRepository, never()).update(any(User.class));

        System.out.println("✅ userId已存在测试通过");
    }

    @Test
    void testUpdateUserInfo_UsernameAlreadyExists() {
        System.out.println("测试：用户名已存在");

        // 准备测试数据 - 尝试更新到已存在的用户名
        updateRequest.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setUserId(2L);
        existingUser.setUsername("existinguser");

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByUsername("existinguser")).thenReturn(existingUser);

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("400", response.getCode());
        assertEquals("用户名已存在", response.getInfo());
        assertNull(response.getData());

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository).findByUsername("existinguser");
        verify(userRepository, never()).update(any(User.class));

        System.out.println("✅ 用户名已存在测试通过");
    }

    @Test
    void testUpdateUserInfo_EmailAlreadyExists() {
        System.out.println("测试：邮箱已存在");

        // 准备测试数据 - 尝试更新到已存在的邮箱
        updateRequest.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setUserId(2L);
        existingUser.setEmail("existing@example.com");

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByEmail("existing@example.com")).thenReturn(existingUser);

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("400", response.getCode());
        assertEquals("邮箱已存在", response.getInfo());
        assertNull(response.getData());

        // 验证调用次数
        verify(userRepository).findByUserId(1L);
        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).update(any(User.class));

        System.out.println("✅ 邮箱已存在测试通过");
    }

    @Test
    void testUpdateUserInfo_PartialUpdate_ShouldOnlyUpdateNonNullFields() {
        System.out.println("测试：部分更新（只更新非空字段）");

        // 准备测试数据 - 只更新部分字段
        updateRequest.setEmail("partial@example.com");
        // 其他字段为null

        // Mock repository调用
        when(userRepository.findByUserId(1L)).thenReturn(testUser);
        when(userRepository.findByEmail("partial@example.com")).thenReturn(null);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        // 执行测试
        Response<User> response = userDomainService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertEquals("200", response.getCode());
        assertNotNull(response.getData());

        User updatedUser = response.getData();
        assertEquals("partial@example.com", updatedUser.getEmail());
        assertEquals("testuser", updatedUser.getUsername()); // 原用户名保持不变
        assertEquals(25, updatedUser.getAge()); // 原年龄保持不变

        verify(userRepository).update(any(User.class));

        System.out.println("✅ 部分更新测试通过");
    }
}