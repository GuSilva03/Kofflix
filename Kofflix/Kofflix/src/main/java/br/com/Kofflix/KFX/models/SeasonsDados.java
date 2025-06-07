package br.com.Kofflix.KFX.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeasonsDados(@JsonAlias ("Season") String Season,
                           @JsonAlias ("Episodes") List<EpisodesDados> Episodes,
                           @JsonAlias ("Released") String ReleaseDate,
                           @JsonAlias ("Rating") String Rating,
                           @JsonAlias ("Title") String Title ){
}
