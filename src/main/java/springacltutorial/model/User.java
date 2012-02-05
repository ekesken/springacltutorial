package springacltutorial.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class User {

	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

	public User(String login) {
		this.login = login;
	}

	private String login;

	public String getLogin() {
		return login;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
}
