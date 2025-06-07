package br.com.Kofflix.KFX;

import br.com.Kofflix.KFX.models.EpisodesDados;
import br.com.Kofflix.KFX.models.SeriesDados;
import br.com.Kofflix.KFX.service.ConsumoApi;
import br.com.Kofflix.KFX.service.ConvertDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KofflixApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(KofflixApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.returnDados("http://www.omdbapi.com/?t=The+Clone+Wars&apikey=2eb803e3");
		System.out.println(json);

		ConvertDados newConvert = new ConvertDados();
		SeriesDados dads = newConvert.resultDados(json, SeriesDados.class);
		System.out.println(dads);
		json = consumoApi.returnDados("https://www.omdbapi.com/?t=The+Clone+Wars&season=6&episode=3&apikey=2eb803e3");
		EpisodesDados dadsEpisodes = newConvert.resultDados(json, EpisodesDados.class);
		System.out.println(dadsEpisodes);
	}
}
