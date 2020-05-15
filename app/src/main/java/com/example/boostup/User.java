package com.example.boostup;

public class User {
    private  String Name;
    private  String Email;
    private String Password;
    public User(){
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName()
    {
        return Name;
    }
}
