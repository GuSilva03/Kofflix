package br.com.Kofflix.KFX.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeriesDados(@JsonAlias ({"Title", "Titulo"}) String Title,
        @JsonAlias("Season") String Season,
        @JsonAlias("Episodes") List <EpisodesDados> Episodes,
        @JsonAlias ("Year") String Year,
        @JsonAlias ("totalSeasons") Integer totalSeasons,
        @JsonAlias ("imdbRating") String rating,
        @JsonAlias ("Genre") String genero,
        @JsonAlias ("Actors")String atores,
        @JsonAlias ("Poster") String poster,
        @JsonAlias ("Plot") String sinopse){
}

