package com.example.cis350finalprojecthomeactivity;

public class PostParser {

    private String raw;

    public PostParser(String in) {
        this.raw = in;
    }

    public Post parseNextPost() {
        return null;
    }

    public static void main(String[] args) {

        String posts = "{\"posts\":[{\"_id\":\"5e9f19a91c9d440000df353c\",\"sender\":\"Joe Bob\",\"type\":\"Recipient\",\"category\":\"Clothing\",\"zip\":19104,\"tags\":\"tag\",\"description\":\"looking for clothing to give to some people!\"},{\"_id\":\"5e9f2f93f0963d62e78b6e0b\",\"sender\":\"Jack McKnight\",\"type\":\"donor\",\"category\":\"Clothing\",\"zip\":19010,\"tags\":\"green\",\"description\":\"I am giving away all of my clothes because I am nice like that\",\"__v\":0},{\"_id\":\"5e9f40ffb52c80638149ab9f\",\"sender\":\"Jack McKnight\",\"type\":\"donor\",\"category\":\"Food\",\"zip\":19104,\"tags\":\"yellow\",\"description\":\"I got beans\",\"__v\":0},{\"_id\":\"5e9f424c13fce9641c96b93f\",\"sender\":\"Jack McKnight\",\"type\":\"donor\",\"category\":\"Food\",\"zip\":19108,\"tags\":\"yellow\",\"description\":\"I got soup\",\"__v\":0},{\"_id\":\"5e9f6dd7f2e43d077c5fe9a3\",\"sender\":\"Dagmawi Dereje\",\"type\":\"recipient\",\"category\":\"clothes\",\"zip\":20850,\"tags\":\"\",\"description\":\"got hella fly clothes\",\"__v\":0}]}";
        PostParser p = new PostParser(posts);
    }

}
