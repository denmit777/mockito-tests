package homework15.model.enums;

public enum Topic {

    BUG("Bug was found"),
    TASK("Task was created"),
    SERVER_OFFLINE("Server is offline");

    private final String text;

    Topic(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
