package com.capstone.service;

import com.capstone.domain.User;
import com.capstone.domain.Role;
import com.capstone.domain.UserRole;
import com.capstone.repository.UserRepository;
import com.capstone.repository.RoleRepository;
import com.capstone.repository.UserRoleRepository;
import com.capstone.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * User Service Test - Tests the Application Layer
 * 
 * This test demonstrates testing of the service layer and contributes
 * to the required 50% code coverage for Stage 5.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;
    private UserRole testUserRole;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ROLE_CUSTOMER");
        testRole.setDescription("Customer role");

        // Create test user role
        testUserRole = new UserRole();
        testUserRole.setId(1L);
        testUserRole.setRole(testRole);

        // Create test user
        testUser = new User();
        testUser.setId(1L);

        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhone("+1234567890");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setIsActive(true);
        testUser.setEmailVerified(false);
        testUser.setUserRoles(new HashSet<>(Arrays.asList(testUserRole)));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
        assertEquals(testUser.getEmail(), actualUsers.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> actualUser = userService.getUserById(userId);

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(testUser.getUsername(), actualUser.get().getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> actualUser = userService.getUserById(userId);

        // Assert
        assertFalse(actualUser.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByEmail() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> actualUser = userService.getUserByEmail(email);

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(testUser.getEmail(), actualUser.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }



    @Test
    void testCreateUser() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPasswordHash("password123");
        newUser.setFirstName("New");
        newUser.setLastName("User");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User actualUser = userService.createUser(newUser);

        // Assert
        assertNotNull(actualUser);
        assertEquals("new@example.com", actualUser.getEmail());
        verify(userRepository, times(1)).existsByEmail(newUser.getEmail());
        verify(roleRepository, times(1)).findByName("ROLE_CUSTOMER");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testCreateUserWithExistingEmail() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("existing@example.com");
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(newUser);
        });
        verify(userRepository, times(1)).existsByEmail(newUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    void testUpdateUser() {
        // Arrange
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User actualUser = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(actualUser);
        assertEquals("Updated", actualUser.getFirstName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        // Arrange
        Long userId = 999L;
        User updatedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.getIsActive());
    }

    @Test
    void testUserExistsByEmail() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean exists = userService.userExistsByEmail(email);

        // Assert
        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail(email);
    }



    @Test
    void testGetUsersByRole() {
        // Arrange
        String roleName = "ROLE_CUSTOMER";
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findByUserRolesRoleName(roleName)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getUsersByRole(roleName);

        // Assert
        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
        assertEquals(testUser.getEmail(), actualUsers.get(0).getEmail());
        verify(userRepository, times(1)).findByUserRolesRoleName(roleName);
    }

    @Test
    void testActivateUser() {
        // Arrange
        Long userId = 1L;
        testUser.setIsActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User actualUser = userService.activateUser(userId);

        // Assert
        assertTrue(actualUser.getIsActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeactivateUser() {
        // Arrange
        Long userId = 1L;
        testUser.setIsActive(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User actualUser = userService.deactivateUser(userId);

        // Assert
        assertFalse(actualUser.getIsActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }
}
