package org.donorcalendar.security;

/***
 * A component which implements this interface should exist before this module can be used.
 */
public interface PasswordEncoder {
	String encode(CharSequence rawPassword);

	boolean matches(CharSequence rawPassword, String encodedPassword);
}
