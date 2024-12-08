package ap.mobile.notedifywithfirebase.search;

public class Notes {
    private String id;
    private String title;
    private String content;
    private String category;
    private String date;
    private long timestamp;

    public Notes() {}

    public Notes(String id, String title, String content, String category, String date, long timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
