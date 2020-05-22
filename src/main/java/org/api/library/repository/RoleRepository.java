package org.api.library.repository;

import java.util.Optional;

import org.api.library.models.ERole;
import org.api.library.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
