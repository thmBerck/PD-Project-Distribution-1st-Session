package Marketplace;

import java.io.Serializable;

/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Either<T> implements Serializable {
    private final T value;
    private final String error;
    private final boolean isSuccess;

    public Either(T value, String error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
    public T getValue() {
        if(!isSuccess) {
            throw new IllegalStateException("You cannot get a value from a failed result.");
        }
        return value;
    }
    public String getError() {
        if(isSuccess) {
            throw new IllegalStateException("You cannot get an error message from a successful result.");
        }
        return error;
    }
}
