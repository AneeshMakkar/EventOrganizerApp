package comp3350.plarty.objects;

/**
 * This class provides validation checks for object arguments.
 */
public class ObjectValidator {

    /**
     * Checks that a String object is valid.
     *
     * @param content   the String to validate
     * @param type      the context of the String used in the error message
     */
    public static void stringCheck(String content, String type) {
        if (content == null || content.trim().length() == 0)
            throw new IllegalArgumentException(type + " must not be null, empty or consist only of spaces.");
    }

    /**
     * Checks that an object argument is not null.
     *
     * @param object    the object to validate
     * @param type      the object's type or context, for the error message
     */
    public static void nullCheck(Object object, String type) {
        if (object == null)
            throw new IllegalArgumentException(type + " must not be null.");
    }
}
