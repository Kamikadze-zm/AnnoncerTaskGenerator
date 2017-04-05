package ru.kamikadze_zm.announcertaskgenerator;

import java.util.Objects;

public class Announcement implements Comparable<Announcement> {

    private final String movieName;
    private String announcement;
    private boolean upperCase;

    public Announcement(String movieName, boolean upperCase) {
        this.movieName = movieName;
        this.announcement = movieName.substring(
                movieName.lastIndexOf("\\") + 1,
                movieName.lastIndexOf("."));
        this.upperCase = upperCase;
    }
    
    public Announcement(String movieName, String announcement, boolean upperCase) {
        this.movieName = movieName;
        this.announcement = announcement;
        this.upperCase = upperCase;
    }

    public String getMovieFullName() {
        return movieName;
    }
    
    public String getMoviePath() {
        int i = movieName.lastIndexOf("\\");
        if (i != -1) {
            return movieName.substring(0, i + 1);
        } else {
            return "";
        }
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

    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
    
    public String toTaskString() {
        String s = movieName + " " + Main.separator;
        if (upperCase) {
            s += announcement.toUpperCase();
        } else {
            s += announcement;
        }
        return s;
    }

    @Override
    public int compareTo(Announcement o) {
        return getMovieName().compareToIgnoreCase(o.getMovieName());
    }

@Override
        public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.movieName);
        return hash;
    }

    @Override
        public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Announcement other = (Announcement) obj;
        if (!Objects.equals(this.movieName, other.movieName)) {
            return false;
        }
        return true;
    }
}
