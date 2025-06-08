package br.com.Kofflix.KFX.principal;

import br.com.Kofflix.KFX.models.Episode;
import br.com.Kofflix.KFX.models.EpisodesDados;
import br.com.Kofflix.KFX.models.SeasonsDados;
import br.com.Kofflix.KFX.models.SeriesDados;
import br.com.Kofflix.KFX.service.ConsumoApi;
import br.com.Kofflix.KFX.service.ConvertDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scann = new Scanner(System.in);
    private ConvertDados newConvert = new ConvertDados();
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=2eb803e3";
    private ConsumoApi consumo = new ConsumoApi();

    public void showMenu() {
        int opcao;
        do {
            System.out.println("\n==== MENU ====");
            System.out.println("1 - Buscar série ou filme");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scann.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida.");
                opcao = -1;
                continue;
            }
            switch (opcao) {
                case 1:
                    buscarSerie();
                    break;
                case 0:
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        scann.close();
    }
    private void buscarSerie() {
        System.out.println("Digite o nome do filme ou série: ");
        var titleSearch = scann.nextLine();

        var json = consumo.returnDados(URL + titleSearch.replace(" ", "+") + API_KEY);
        SeriesDados seriesData = newConvert.resultDados(json, SeriesDados.class);

        if (seriesData == null || seriesData.totalSeasons() == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        List<SeasonsDados> seasonsList = new ArrayList<>();
        for (int i = 1; i <= seriesData.totalSeasons(); i++) {
            String seasonJson = consumo.returnDados(URL + titleSearch.replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonsDados seasonData = newConvert.resultDados(seasonJson, SeasonsDados.class);
            if (seasonData != null) {
                seasonsList.add(seasonData);
            }
        }

        List<EpisodesDados> episodes = seasonsList.stream()
                .flatMap(s -> s.Episodes().stream())
                .collect(Collectors.toList());

        List<Episode> episodeList = seasonsList.stream()
                .flatMap(s -> s.Episodes().stream()
                        .map(e -> new Episode(Integer.parseInt(s.Season()), e)))
                .collect(Collectors.toList());

        int option;
        do {
            System.out.println("\n=== MENU DA SÉRIE ===");
            System.out.println("1 - Ver todos os episódios por temporada");
            System.out.println("2 - Ver Top 5 episódios com melhor avaliação");
            System.out.println("3 - Buscar episódios por ano");
            System.out.println("4 - Buscar episódio por nome");
            System.out.println("5 - Ver estatísticas da série");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            try {
                option = Integer.parseInt(scann.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida.");
                option = -1;
                continue;
            }

            switch (option) {
                case 1:
                    for (SeasonsDados season : seasonsList) {
                        System.out.println("\nTemporada " + season.Season() + ":");
                        for (EpisodesDados episode : season.Episodes()) {
                            System.out.println("- " + episode.Title());
                        }
                    }
                    break;
                case 2:
                    System.out.println("\nTop 5 episódios com melhor avaliação:");
                    episodes.stream()
                            .filter(e -> !e.Rating().equalsIgnoreCase("N/A"))
                            .sorted(Comparator.comparing(EpisodesDados::Rating).reversed())
                            .limit(5)
                            .forEach(e -> System.out.println(
                                    "Título: " + e.Title() +
                                            " | Temporada: " + e.Season() +
                                            " | Episódio: " + e.numberEpisode() +
                                            " | Avaliação: " + e.Rating()
                            ));
                    break;
                case 3:
                    mostrarEpisodiosPorAno(episodeList);
                    break;
                case 4:
                    buscarEpisodioPorNome(episodeList);
                    break;
                case 5:
                    mostrarEstatisticas(episodeList);
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (option != 0);
    }

    private void mostrarEpisodiosPorAno(List<Episode> episodesList) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.println("Digite o ano desejado (ou digite 'sair' para cancelar): ");
            var input = scann.nextLine().trim();

            if (input.equalsIgnoreCase("sair")) {
                System.out.println("Busca por ano cancelada.");
                break;
            }

            int yearInt;
            try {
                yearInt = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ano inválido. Tente novamente.");
                continue;
            }

            LocalDate dateStart = LocalDate.of(yearInt, 1, 1);
            LocalDate dateEnd = LocalDate.of(yearInt, 12, 31);
            List<Episode> filteredEpisodes = episodesList.stream()
                    .filter(e -> e.getReleaseDate() != null &&
                            !e.getReleaseDate().isBefore(dateStart) &&
                            !e.getReleaseDate().isAfter(dateEnd))
                    .collect(Collectors.toList());

            if (filteredEpisodes.isEmpty()) {
                System.out.println("Nenhum episódio encontrado no ano de " + yearInt + ".");
            } else {
                System.out.println("\nEpisódios lançados em " + yearInt + ":");
                filteredEpisodes.forEach(e -> System.out.println(
                        "Temporada: " + e.getSeason() +
                                " | Episódio: " + e.getTitle() +
                                " | Lançamento: " + e.getReleaseDate().format(formatador)
                ));
                break;
            }
        }
    }

    private void buscarEpisodioPorNome(List<Episode> episodesList) {
        System.out.println("\nDigite o nome (ou trecho) do episódio: ");
        var excerptTitle = scann.nextLine().toLowerCase();

        List<Episode> matchedEpisodes = episodesList.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(excerptTitle))
                .collect(Collectors.toList());

        if (matchedEpisodes.isEmpty()) {
            System.out.println("Nenhum episódio encontrado com esse nome.");
            return;
        }

        System.out.println("\nEpisódios encontrados:");
        for (int i = 0; i < matchedEpisodes.size(); i++) {
            Episode ep = matchedEpisodes.get(i);
            System.out.printf("%d - Temporada %s, Episódio %s: %s\n",
                    i + 1, ep.getSeason(), ep.getEpisode(), ep.getTitle());
        }

        while (true) {
            System.out.print("\nDigite o número do episódio para ver detalhes (ou 0 para voltar): ");
            String input = scann.nextLine();
            int selected;

            try {
                selected = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
                continue;
            }

            if (selected == 0) {
                System.out.println("Voltando ao menu da série...");
                break;
            }

            if (selected < 1 || selected > matchedEpisodes.size()) {
                System.out.println("Número fora do intervalo.");
                continue;
            }

            Episode ep = matchedEpisodes.get(selected - 1);
            System.out.println("\nDetalhes do episódio:");
            System.out.println("Título: " + ep.getTitle());
            System.out.println("Temporada: " + ep.getSeason());
            System.out.println("Número do episódio: " + ep.getEpisode());
            System.out.println("Avaliação: " + ep.getRating());

            if (ep.getReleaseDate() != null) {
                System.out.println("Data de lançamento: " +
                        ep.getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                System.out.println("Data de lançamento: N/A");
            }

            break;
        }
    }
    private void mostrarEstatisticas(List<Episode> episodesList) {
            Map<Integer, Double> ratingForSeason = episodesList.stream()
                    .filter(e -> e.getRating() > 0.0)
                    .collect(Collectors.groupingBy(
                            Episode::getSeason,
                            Collectors.averagingDouble(Episode::getRating)
                    ));
            System.out.println("\nMédia de avaliações por temporada:");
            ratingForSeason.forEach((season, avg) ->
                    System.out.println("Temporada " + season + ": " + String.format("%.2f", avg)));

            DoubleSummaryStatistics est = episodesList.stream()
                    .filter(e -> e.getRating() > 0.0)
                    .collect(Collectors.summarizingDouble(Episode::getRating));
            System.out.println("\nEstatísticas Gerais:");
            System.out.println("Média: " + String.format("%.2f", est.getAverage()));
            System.out.println("Melhor Avaliação: " + est.getMax());
            System.out.println("Pior Avaliação: " + est.getMin());
            System.out.println("Total de Episódios Avaliados: " + est.getCount());
    }
}