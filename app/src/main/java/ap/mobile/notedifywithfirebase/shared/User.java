package ap.mobile.notedifywithfirebase.shared;

public class User {
    private String name;
    private int profilePhotoResId;

    public User(String name, int profilePhotoResId) {
        this.name = name;
        this.profilePhotoResId = profilePhotoResId;
    }

    public String getName() {
        return name;
    }

    public int getProfilePhotoResId() {
        return profilePhotoResId;
    }
}