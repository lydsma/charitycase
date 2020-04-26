package com.example.cis350finalprojecthomeactivity;

import java.util.Set;
import java.util.TreeSet;

class Post {

    Category category;
    String zipCode;
    boolean seekingDonations;
    Set<String> likes;
    Set<String> tags;
    boolean empty;

    // represents empty post, can't have a final static instance bc it's an inner class
    public Post() {
        empty = true;
    }

    public Post(Category cat, String zip, boolean type, Set<String> tgs) {
        empty = false;
        this.category = cat;
        this.zipCode = zip;
        this.seekingDonations = type;
        this.likes = new TreeSet<String>();
        this.tags = tgs;
    }

    public Post(String cat, String zip, boolean type, Set<String> tgs) {
        Category catEnum = Category.NULL;
        if (cat.toLowerCase().equals("education")) {
            catEnum = Category.EDUCATION;
        } else if (cat.toLowerCase().equals("clothing")) {
            catEnum = Category.CLOTHING;
        } else if (cat.toLowerCase().equals("food")) {
            catEnum = Category.FOOD;
        }

        empty = false;
        this.category = catEnum;
        this.zipCode = zip;
        this.seekingDonations = type;
        this.likes = new TreeSet<String>();
        this.tags = tgs;
    }

    public String tagsString() {
        if (this.tags == null || this.tags.size() == 0) {
            return "";
        } else {
            String out = "";
            for (String s : this.tags) {
                out += "#" + s + " ";
            }

            return out.substring(0, out.length() - 1);
        }
    }

    public Set<String> tags() {
        if (this.tags == null) {
            return new TreeSet<String>();
        } else {
            return this.tags;
        }
    }

    public void likePost(String user) {
        likes.add(user);
    }

    public void unlikePost(String user) { likes.remove(user); }

    public int numPins() { return likes.size(); }

    public enum Category {
        FOOD, EDUCATION, CLOTHING, NULL
    }

}