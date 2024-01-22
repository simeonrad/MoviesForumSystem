package repositories;

import models.Tag;

public interface TagRepository {
    void create (Tag tag);
    void update (Tag tag);
    void delete (Tag tag);
    Tag getById(int id);
    Tag getByName(String name);
}
