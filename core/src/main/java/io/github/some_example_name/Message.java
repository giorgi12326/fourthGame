package io.github.some_example_name;

import java.io.Serializable;

public class Message implements Serializable {
    String title;
    Object payload;
    public Message(String title, Object payload) {
        this.title = title;
        this.payload = payload;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{" +
            "title='" + title + '\'' +
            ", payload=" + payload +
            '}';
    }
}
