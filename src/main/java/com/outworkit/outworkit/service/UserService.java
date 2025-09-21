package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.User;
import com.outworkit.outworkit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<User> getUsers() {
        try {
            log.debug("Getting users");
            List<User> users = userRepository.findAll();
            log.info("Users retrieved");
            return users;
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            throw e;
        }
    }

    public User getUser(Long id) {
        log.debug("Looking for user with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("User not found with ID: %d", id)
                    );
                });
    }

    public Optional<User> findUserById(Long userId) {
        log.debug("Searching for user with ID: {}", userId);

        if (userId == null) {
            return Optional.empty();
        }

        return userRepository.findById(userId);
    }

    @Transactional
    public User saveOrUpdate(User user) {
        log.debug("Saving/updating user: {}", user);

        validateUser(user);

        if (user.getId() != null) {
            if (!userRepository.existsById(user.getId())) {
                throw new ResourceNotFoundException(
                        String.format("Can't update user not found with ID: %d", user.getId())
                );
            }
            log.info("Updating existing user ID: {}", user.getId());
        } else {
            log.info("Creating a new user: {}", user.getUserName());
        }

        User savedUser = userRepository.save(user);
        log.info("User saved successfully ID: {}", savedUser.getId());

        return savedUser;
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("User not found with ID: %d", id));
        }
        userRepository.deleteById(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new BadRequestException("User can't be null");
        }

        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new BadRequestException("User name is required");
        }

        if (user.getUserName().trim().length() > 255) {
            throw new BadRequestException("User name can't exceed 255 characters");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (user.getEmail().trim().length() > 255) {
            throw new BadRequestException("Email can't exceed 255 characters");
        }

        log.debug("User validation performed successfully.");
    }
}