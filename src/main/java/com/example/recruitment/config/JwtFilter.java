package com.example.recruitment.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.recruitment.repository.UserRepository;
import com.example.recruitment.service.JwtService;
import com.example.recruitment.entity.User; // <-- make sure this matches your repository's User type
import com.example.recruitment.entity.User.Role; // <-- optional: import Role enum if you need it

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);

			try {
				Long userId = jwtService.validateAndGetUserId(token);
				if (userId != null) {
					userRepository.findById(userId).ifPresent(user -> {

						Role userType = user.getUserType();
						String roleName = (userType != null) ? userType.name() : "USER";
						List<SimpleGrantedAuthority> authorities = List
								.of(new SimpleGrantedAuthority("ROLE_" + roleName));

						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								user, null, authorities);

						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authentication);
						
						request.setAttribute("authenticatedUser", user);

						System.out.println("Authenticated user: " + user.getEmail());
					});
				}
			} catch (Exception e) {
				System.err.println("JWT validation error: " + e.getMessage());
			}
		}

		filterChain.doFilter(request, response);
	}
}
