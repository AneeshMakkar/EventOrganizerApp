package comp3350.plarty.objects;

import android.support.annotation.NonNull;

/**
 * This class represents the Users of Plarty.
 * Users are identified with names (in UI) and IDs (in code).
 */
public class User {
    private final int id;
    private String name;

    public User(String name, int id) {
        ObjectValidator.stringCheck(name, "User name");
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        ObjectValidator.stringCheck(name, "User name");
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public boolean equals(Object object) {
        return object instanceof User && ((User) object).getId() == this.id;
    }

    @NonNull
    public String toString() {
        return "\nName: " + name + "\n\tID: " + id;
    }
}
