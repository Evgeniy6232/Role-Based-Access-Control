package com.evgenii.rbac.model;

public record User(String username, String fullname, String email) {

    public User{

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("The username field cannot be empty");
        }

        if (fullname == null || fullname.isBlank()) {
            throw new IllegalArgumentException("The fullname field cannot be empty");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("The email field cannot be empty");
        }


        String usernamePattern = "^[a-zA-Z0-9]{3,20}$";
        if (!username.matches(usernamePattern)) {
            throw new IllegalArgumentException("Username doesn`t matches pattern");
        }

        String emailPattern = "^[\\w-.]+@[\\w-]+\\.[a-z]{2,4}$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("Email doesn`t matches pattern");
        }

    }

    public static User validate(String username, String fullname, String email){
        return new User(username, fullname, email);
    }

    public String format(){
        return String.format("%s (%s) <%s>", username, fullname, email);
    }

    public static void main(String[] args) {

        try {
            User userOne = User.validate("Genna1", "Genna QQ", "tekken1589556@gmail.com");
            System.out.println("Created by user: " + userOne.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            User userTwo = User.validate("", "Genna QQ", "tekken1589556@gmail.com");
            System.out.println("Created by user: " + userTwo.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            User userTree = User.validate("Genna3", "", "tekken1589556@gmail.com");
            System.out.println("Created by user: " + userTree.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            User userFour = User.validate("Genna4", "Genna QQ", "tekken1589556gmail.com");
            System.out.println("Created by user: " + userFour.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
