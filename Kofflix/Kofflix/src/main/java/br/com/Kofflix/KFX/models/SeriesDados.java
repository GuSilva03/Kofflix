package br.com.Kofflix.KFX.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeriesDados(@JsonAlias ({"Title", "Titulo"}) String Title,
        @JsonAlias ("Year") String Year,
        @JsonAlias ("totalSeasons") Integer totalSeasons,
        @JsonAlias ("imdbRating") String rating) {
}
