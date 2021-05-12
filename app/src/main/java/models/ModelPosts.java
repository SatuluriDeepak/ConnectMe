package models;

public class ModelPosts {
    String Tittle,Description,Uid;

    public String getTittle() {
        return Tittle;
    }

    public void setTittle(String tittle) {
        Tittle = tittle;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public ModelPosts(String uid, String tittle, String description) {
        this.Description = description;
        this.Uid = uid;
        this.Tittle = tittle;

    }
}
