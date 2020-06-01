package org.api.library.repository;

import java.util.Optional;

import org.api.library.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface UserRepository extends MongoRepository<User, String> {
	  Optional<User> findByUsername(String username);

	  Boolean existsByUsername(String username);

	  Boolean existsByEmail(String email);
	  Optional<User> findByUsernameAndPassword(String username,String password);
	}
