package org.api.library.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.api.library.jwt.JwtUtils;
import org.api.library.models.ChangePasswordRequest;
import org.api.library.models.Jwt;
import org.api.library.models.MessageResponse;
import org.api.library.models.Role;
import org.api.library.models.SignupRequest;
import org.api.library.models.User;
import org.api.library.repository.RoleRepository;
import org.api.library.repository.UserRepository;
import org.api.library.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody User user) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new Jwt(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

			strRoles.forEach(role -> {
								Role enrollRole = roleRepository.findByDescription(role)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(enrollRole);

							
			});
		

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PostMapping("/changepassword")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changepasswordreguest) {
		
			
		if (!(userRepository.existsByUsername(changepasswordreguest.getUsername()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("User is Not Present!"));
		}else {
			User user = userRepository.findByUsername(changepasswordreguest.getUsername()).get();
			String dbPassword=user.getPassword();
			if (encoder.matches(changepasswordreguest.getCurrentPassword(), dbPassword)) {
				user.setPassword(encoder.encode(changepasswordreguest.getNewPassword())); 
				userRepository.save(user);
				return ResponseEntity.ok(new MessageResponse("User Password Updated successfully!"));
			} else {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Entered Current Password is incorrect!"));
			}

		
		}
		
	}
}
