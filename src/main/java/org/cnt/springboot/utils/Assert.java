package org.cnt.springboot.utils;

/**
 * @author lixinjie
 * @since 2019-05-23
 */
public class Assert {
	
	private static final Object[] EMPTY_ARRAY = new Object[0];
	
	public static void check(boolean result, String message) {
		check(result, message, EMPTY_ARRAY);
	}
	
	public static void check(boolean result, String message, Object... args) {
		if (!result) {
			if (args.length > 0) {
				for (Object arg : args) {
					message = message.replaceFirst("\\{\\}", String.valueOf(arg));
				}
			}
			throw new AssertException("the condition '" + message + "' was not satisfied.");
		}
	}
	
	@SuppressWarnings("serial")
	static class AssertException extends RuntimeException {
		
		public AssertException(String message) {
			super(message);
		}
	}

	public static void main(String[] args) {
		Assert.check(1 > 2, "a({}) > b({})", 1, 2);
	}

}
