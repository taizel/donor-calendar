package org.donorcalendar.security;

import java.util.Base64;

public class FakePasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		byte[] encodedBytes = Base64.getEncoder().encode(rawPassword.toString().getBytes());
		return new String(encodedBytes);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword.getBytes());
		return rawPassword.equals(new String(decodedBytes));
	}
}
