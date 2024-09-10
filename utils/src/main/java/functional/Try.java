package functional;

import java.util.Optional;
import java.util.function.Function;

public class Try<S> {
	private S success;
	private Exception fail;

	public Try(S success) {
		this.success = success;
	}

	public Try(Exception fail) {
		this.fail = fail;
	}

	public Optional<S> getSuccess() {
		return Optional.ofNullable(success);
	}

	public boolean isSuccess() {
		return success != null;
	}

	public Optional<Exception> getFail() {
		return Optional.ofNullable(fail);
	}

	public boolean isFail() {
		return fail != null;
	}

	@FunctionalInterface
	public interface ThrowingSupplier<S> {
		S get() throws Exception;
	}

	public static <S> Try<S> of(ThrowingSupplier<S> supplier) {
		try {
			return new Try<>(supplier.get());
		} catch (Exception e) {
			return new Try<>(e);
		}
	}

	public <R> Try<R> onSuccess(ThrowingFunction<S, R> successFunction) {
		if (isSuccess()) {
			try {
				return new Try<>(successFunction.apply(success));
			} catch (Exception e) {
				return new Try<>(e);
			}
		} else {
			return new Try<>(fail);
		}
	}

	public <T> Try<T> flatMap(Function<S, Try<T>> mapper) {
		if (isSuccess()) {
			return mapper.apply(success);
		} else {
			return new Try<>(fail);
		}
	}

	public <T> Try<T> map(ThrowingFunction<S, T> mapper) {
		if (isSuccess()) {
			try {
				return new Try<>(mapper.apply(success));
			} catch (Exception e) {
				return new Try<>(e);
			}
		} else {
			return new Try<>(fail);
		}
	}

}
