package org.api.library.controllers;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.api.library.exception.DataNotFoundException;
import org.api.library.models.Role;
import org.api.library.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/common")
public class CommonController {
	
	@Autowired
	RoleRepository roleRepository;

	@GetMapping(value="/getRoles", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARY')")
	public  Set<String> getRoles()  {
		
		try {
			 SortedSet<String> roleDescription = new TreeSet<String>(); 
			
			List<Role> roles=roleRepository.findAll();
			roles.forEach(role -> {
				roleDescription.add(role.getDescription());
			
			});

		return roleDescription;
		}
		catch (RuntimeException e) {
			throw new DataNotFoundException("Roles not found.");
		}
		
	}
	
}
