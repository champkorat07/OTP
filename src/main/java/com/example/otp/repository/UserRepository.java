package com.example.otp.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.otp.entity.Users;

@Repository
public interface UserRepository extends CrudRepository<Users, Long> {
	public Optional<Users> findByUsername(String username);
}