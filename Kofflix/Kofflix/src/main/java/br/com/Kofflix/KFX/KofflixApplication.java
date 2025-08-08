package br.com.Kofflix.KFX;



import br.com.Kofflix.KFX.models.SeriesDados;
import br.com.Kofflix.KFX.principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KofflixApplication implements CommandLineRunner {

    @Autowired
    private Principal principal;

    public static void main(String[] args) {
        SpringApplication.run(KofflixApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        SeriesDados seriesDados = principal.getSerie();
        if (seriesDados != null){
            principal.showMenuSerie(seriesDados, principal.getSeasonList(), principal.getEpisodeList());
        }
    }
}

