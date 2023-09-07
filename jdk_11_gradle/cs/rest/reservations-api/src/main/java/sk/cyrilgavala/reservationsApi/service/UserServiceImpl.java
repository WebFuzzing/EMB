package sk.cyrilgavala.reservationsApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.reservationsApi.mapper.UserMapper;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.repository.UserRepository;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;
	private final UserMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public UserResponse getByUsername(String username) {
		return mapper.modelToResponse(repository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return repository.existsByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}

	@Override
	public UserResponse saveUser(User user) {
		return mapper.modelToResponse(repository.insert(user));
	}

}
