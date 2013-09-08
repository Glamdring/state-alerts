package bg.statealerts.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Profile(TestProfile.PROFILE_NAME)
public @interface TestProfile {
	public static final String PROFILE_NAME = "test-profile";

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Profile('!' + TestProfile.PROFILE_NAME)
	@interface Disabled {

	}
}
