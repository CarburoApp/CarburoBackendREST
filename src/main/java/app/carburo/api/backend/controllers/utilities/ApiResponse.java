package app.carburo.api.backend.controllers.utilities;

public record ApiResponse<T>(
		boolean success,
		T data,
		ApiErrorResponse error,
		String timestamp
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null, now());
	}

	public static <T> ApiResponse<T> error(String code, String message) {
		return new ApiResponse<>(false, null, new ApiErrorResponse(code, message), now());
	}

	private static String now() {
		return java.time.Instant.now().toString();
	}
}