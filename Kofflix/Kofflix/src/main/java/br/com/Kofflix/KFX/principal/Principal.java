package br.com.Kofflix.KFX.principal;

import br.com.Kofflix.KFX.models.Episode;
import br.com.Kofflix.KFX.models.EpisodesDados;
import br.com.Kofflix.KFX.models.SeasonsDados;
import br.com.Kofflix.KFX.models.SeriesDados;
import br.com.Kofflix.KFX.service.ConsumoApi;
import br.com.Kofflix.KFX.service.ConvertDados;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner scann = new Scanner(System.in);
    private ConvertDados newConvert = new ConvertDados();
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=2eb803e3";
    private ConsumoApi consumo = new ConsumoApi();

    public void showMenu() {

        System.out.println("Digite o nome do filme ou série: ");
        var titleSearch = scann.nextLine();
        var json = consumo.returnDados(URL + titleSearch.replace(" ", "+") + API_KEY);
        System.out.println(json);

        SeriesDados dadsSeries = newConvert.resultDados(json, SeriesDados.class);
        System.out.println(dadsSeries);

        EpisodesDados dadsEpisodes = newConvert.resultDados(json, EpisodesDados.class);

        List<SeasonsDados> Seasons = new ArrayList<>();

        for (int i = 1; i <= dadsSeries.totalSeasons(); i++) {
            json = consumo.returnDados(URL + titleSearch.replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonsDados dadsSeason = newConvert.resultDados(json, SeasonsDados.class);
            Seasons.add(dadsSeason);
            System.out.println(dadsSeason);
        }
        Seasons.forEach(System.out::println);
        Seasons.forEach(t -> t.Episodes().forEach(e -> System.out.println(e.Title())));

        List<EpisodesDados> episodes = Seasons.stream().flatMap(t -> t.Episodes().stream())
                .collect(Collectors.toList());
        episodes.stream().filter(e -> !e.Rating().equalsIgnoreCase("N/A")).sorted(Comparator.comparing(EpisodesDados::Rating).
                reversed()).limit(5).forEach(System.out::println);

        List<Episode> episodesList = Seasons.stream()
                .flatMap(t -> t.Episodes().stream()
                        .map(d -> new Episode(Integer.parseInt(t.Season()), d)))
                .collect(Collectors.toList());
        episodesList.forEach(System.out::println);

        System.out.println("Deseja ver os episódios de qual ano?");
        var year = scann.nextLine();

        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido.");
            return;
        }
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateSearch = LocalDate.of(yearInt, 1, 1);
        episodesList.stream()
                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(dateSearch))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getSeason() +
                                " Episode: " + e.getTitle() +
                                " Data de Lançamento: " + e.getReleaseDate().format(formatador)
                ));

        scann.close();
    }
}