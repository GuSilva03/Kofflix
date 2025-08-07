package br.com.Kofflix.KFX.models;

import br.com.Kofflix.KFX.service.ConsultaIA;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String title;
    private String year;
    private Integer totalSeasons;
    private double rating;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;
    public Serie(){

    }
    @Transient
    private List<Episode> episodes = new ArrayList<>();

    public Serie(SeriesDados seriesDados){
        this.title = seriesDados.Title();
        this.year = seriesDados.Year();
        this.totalSeasons = seriesDados.totalSeasons();
        this.rating = OptionalDouble.of(Double.valueOf(seriesDados.rating())).orElse(0);
        this.genero = Categoria.fromString(seriesDados.genero().split(",")[0].trim());
        this.atores = seriesDados.atores();
        this.poster = seriesDados.poster();
        this.sinopse = ConsultaIA.obterTraducao(seriesDados.sinopse()).trim();
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return
                "genero = " + genero + '\'' +
                "title = '" + title + '\'' +
                ", year = '" + year + '\'' +
                ", totalSeasons = " + totalSeasons +
                ", rating = " + rating +
                ", atores = '" + atores + '\'' +
                ", poster = '" + poster + '\'' +
                ", sinopse = '" + sinopse + '\'';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
