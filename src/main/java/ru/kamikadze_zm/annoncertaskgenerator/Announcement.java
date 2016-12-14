package ru.kamikadze_zm.annoncertaskgenerator;

public class Announcement {

    private final String movieName;
    private String announcement;

    public Announcement(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieFullName() {
        return movieName;
    }
    
    public String getMovieName() {
        int i = movieName.lastIndexOf("\\");
        if (i != -1) {
            return movieName.substring(i + 1);
        } else {
            return movieName;
        }
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

}
