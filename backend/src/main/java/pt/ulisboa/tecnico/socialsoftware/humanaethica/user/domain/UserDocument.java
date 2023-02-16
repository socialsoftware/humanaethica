package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;

@Entity
public class UserDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    byte[] content;

    String name;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

