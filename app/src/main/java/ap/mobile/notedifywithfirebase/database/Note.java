package ap.mobile.notedifywithfirebase.database;

import java.util.List;

public class Note {
    private String id;
    private String title;
    private String content;
    private long timestamp;
    private String category;
    private boolean isPlaceholder;
    private List<String> sharedTo;
    private String imageUrl;

    public Note() {
        // Empty constructor needed by Firebase
    }

    public Note(String title, String content, String category, boolean isPlaceholder, List<String> sharedTo) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
        this.isPlaceholder = isPlaceholder;
        this.sharedTo = sharedTo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean getIsPlaceholder() { return isPlaceholder; }
    public void setIsPlaceholder(boolean isPlaceholder) { this.isPlaceholder = isPlaceholder; }
    public List<String> getSharedTo() { return sharedTo; }
    public void setSharedTo(List<String> sharedTo) { this.sharedTo = sharedTo; }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}