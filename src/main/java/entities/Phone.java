package entities;

import javax.persistence.*;

@Entity
public class Phone {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name ="number")
    private String number;
    @Column(name ="description")
    private  String description;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Phone() {
    }

    public Phone(String number, String description, Person person) {
        this.number = number;
        this.description = description;
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
