package dtos;

import entities.Hobby;

import java.util.ArrayList;
import java.util.List;

public class HobbyDTO {
    private long id;
    private String name;
    private String description;

    public HobbyDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public HobbyDTO(Hobby hobby) {
        if (hobby.getId() != null) {
            this.id = hobby.getId();
        }
        this.name = hobby.getName();
        this.description = hobby.getDescription();
    }

    public static List<HobbyDTO> getDtos(List<Hobby> hobbies) {
        List<HobbyDTO> hobbyDTOS = new ArrayList<>();
        hobbies.forEach(hobby -> hobbyDTOS.add(new HobbyDTO(hobby)));
        return hobbyDTOS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "HobbyDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
