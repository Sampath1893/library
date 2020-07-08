package org.api.library.repository;

import java.util.List;
import java.util.Optional;

import org.api.library.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByDescription(String name);
  List<Role> findAll();
}
