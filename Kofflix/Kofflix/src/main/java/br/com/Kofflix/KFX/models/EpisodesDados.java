package br.com.Kofflix.KFX.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EpisodesDados(
        @JsonAlias ("Title") String Title,
        @JsonAlias ("Episode") String numberEpisode,
        @JsonAlias ("Released") String ReleaseDate,
        @JsonAlias ("imdbRating") String Rating)
         {
}
