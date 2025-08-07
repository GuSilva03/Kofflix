package br.com.Kofflix.KFX.repository;

import br.com.Kofflix.KFX.models.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTitle(String title);
}
