package br.com.Kofflix.KFX.models;



import java.time.LocalDate;
import java.util.List;

public class Episode {
    private String title;
    private String Episode;
    private LocalDate ReleaseDate;
    private double Rating;
    private int Season;

    public Episode(Integer numberSeason, EpisodesDados dadosEpisode) {
        this.Season = numberSeason;
        this.Episode = dadosEpisode.numberEpisode();
        this.title = dadosEpisode.Title();
        try {
            if (!dadosEpisode.ReleaseDate().equalsIgnoreCase("N/A"))
            this.ReleaseDate = LocalDate.parse(dadosEpisode.ReleaseDate());
        }catch (Exception e){
            this.ReleaseDate = null;
        }
        try {
            this.Rating = Double.valueOf(dadosEpisode.Rating());
        }catch (NumberFormatException e){
            this.Rating = 0;
        }

    }

    public Integer getSeason() {
        return Season;
    }
    public void setSeason(Integer season) {

    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getEpisode() {
        return Episode;
    }

    @Override
    public String toString() {
        return
                "Episode='" + Episode + '\'' +
                ", title='" + title + '\'' +
                ", ReleaseDate=" + ReleaseDate +
                ", Rating=" + Rating +
                ", Season=" + Season ;
    }
    public LocalDate getReleaseDate() {
        return ReleaseDate;
    }

}
