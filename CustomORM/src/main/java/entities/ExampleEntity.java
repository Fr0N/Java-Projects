package entities;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

@Entity(name = "example_entity")
public class ExampleEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    private String fullName;

    public ExampleEntity(){}

    public ExampleEntity(String fullName) {
        this.fullName = fullName;
    }
}
