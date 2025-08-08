package br.com.Kofflix.KFX.principal;

import br.com.Kofflix.KFX.models.*;
import br.com.Kofflix.KFX.repository.SerieRepository;
import br.com.Kofflix.KFX.service.ConsumoApi;
import br.com.Kofflix.KFX.service.ConvertDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {
    private Scanner scann = new Scanner(System.in);
    private ConvertDados newConvert = new ConvertDados();
    private final String URL = "https://www.omdbapi.com/?t=";
    @Value("${api.omdb.key}")
    private String API_KEY;
    private final ConsumoApi consumo = new ConsumoApi();
    private final SerieRepository repository;
    private List<SeasonsDados> seasonList = new ArrayList<>();
    private List<Episode> episodeList = new ArrayList<>();


    @Autowired
    public Principal(SerieRepository repository) {
        this.repository = repository;
    }
    private void listSearchSeries(){
        List<Serie> series = repository.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }
    public SeriesDados getSerie() {
        String titleSearch = getTitle();
        SeriesDados seriesData = getSeriesData(titleSearch);
        if (!validateSerie(seriesData)) {
            return null;
        }
        this.seasonList = getSeasons(titleSearch, seriesData.totalSeasons());
        this.episodeList = convertSeasonToEpisode(seasonList);
        Serie serieConvert = new Serie(seriesData);
        validExistSerie(serieConvert);
        showMenuSerie(seriesData, seasonList, episodeList);
        return seriesData;
    }


    private String getTitle() {
        System.out.println("Digite o nome do filme ou série: ");
        return scann.nextLine();
    }

    private SeriesDados getSeriesData(String title) {
        String json = consumo.returnDados(URL + title.replace(" ", "+") + "&apikey=" + API_KEY);
        return newConvert.resultDados(json, SeriesDados.class);
    }

    private boolean validateSerie(SeriesDados seriesDados) {
        if (seriesDados == null || seriesDados.totalSeasons() == null) {
            System.out.println("Série não encontrada.");
            return false;
        }
        return true;
    }

    private void validExistSerie(Serie serie) {
        Optional<Serie> exists = repository.findByTitle(serie.getTitle());
        if (exists.isEmpty()) {
            repository.save(serie);
        } else {
            System.out.println("Série: '" + serie.getTitle() + "' já cadastrada.");
        }
    }

    private List<SeasonsDados> getSeasons(String title, int totalSeasons) {
        List<SeasonsDados> list = new ArrayList<>();
        for (int i = 1; i <= totalSeasons; i++) {
            String json = consumo.returnDados(URL + title.replace(" ", "+") + "&season=" + i + "&apikey=" + API_KEY);
            System.out.println("JSON Temporada " + i + ": " + json);
            SeasonsDados dados = newConvert.resultDados(json, SeasonsDados.class);
            if (dados != null) {
                list.add(dados);
            }
        }
        return list;
    }

    private List<Episode> convertSeasonToEpisode(List<SeasonsDados> seasons) {
        if (seasons == null) return List.of();

        return seasons.stream()
                .filter(s -> s != null && s.Episodes() != null)
                .flatMap(s -> s.Episodes().stream()
                        .map(e -> new Episode(s.getSeasonNumber(), e)) // conversão EpisodesDados → Episode
                )
                .collect(Collectors.toList());
    }

    private void showEpisodesForYear(List<Episode> episodeList) {
        var formated = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.println("Digite o ano desejado (ou digite 'sair' para cancelar) ");
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
            var dateStart = java.time.LocalDate.of(yearInt, 1, 1);
            var dateEnd = java.time.LocalDate.of(yearInt, 12, 31);
            List<Episode> filtered = episodeList.stream().filter(ep -> ep.getReleaseDate() != null && !ep.getReleaseDate().isBefore(dateStart) && !ep.getReleaseDate().isAfter(dateEnd)).toList();
            if (filtered.isEmpty()) {
                System.out.println("Nenhum episódio encontrado no ano de " + yearInt + ".");
            } else {
                System.out.println("\nEpisódios lançados em " + yearInt + ":");
                filtered.forEach(ep -> System.out.println(
                        "Temporada: " + ep.getSeason() +
                                " | Episódio: " + ep.getTitle() +
                                " | Lançamento: " + ep.getReleaseDate().format(formated)
                ));
                break;
            }
        }

    }
    private void getEpisodeByName(List<Episode> episodeList){
        System.out.println("\nDigite o nome (ou parte da série) do episódio: ");
        String input = scann.nextLine().toLowerCase();

        List<Episode> matched = episodeList.stream().filter(ep -> ep.getTitle().toLowerCase().contains(input)).toList();
        if(matched.isEmpty()){
            System.out.println("Nenhum episódio encontrado com esse nome.");
            return;
        }
        System.out.println("\n Episódios encontrados: ");
        for (int i = 0; i < matched.size(); i++) {
            var ep = matched.get(i);
            System.out.printf("%d - Temporada %d, Episódio %s: %s%n", i + 1, ep.getSeason(), ep.getEpisode(), ep.getTitle());
        }
        while (true){
            System.out.println("\nDigite o número do episódio para ver detalhes (ou 0 para voltar): ");
            String selectedStr = scann.nextLine();

            int selected;
            try{
                selected = Integer.parseInt(selectedStr);
            }catch (NumberFormatException e){
                System.out.println("Entrada inválida. Digite um número.");
                continue;
            }
            if (selected == 0 ){
                System.out.println("Voltando ao menu da série...");
                break;
            }
            if (selected < 1 || selected > matched.size()){
                System.out.println("Número fora do intervalo.");
                continue;
            }
            Episode ep = matched.get(selected - 1);
            System.out.println("\n Detalhes do Episódio: ");
            System.out.println("Título: " + ep.getTitle());
            System.out.println("Temporada: " + ep.getSeason());
            System.out.println("Número do episódio: " + ep.getEpisode());
            System.out.println("Avaliação: " + ep.getRating());
            if(ep.getReleaseDate() != null ) {
                System.out.println("Data de lançamento: " + ep.getReleaseDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }else {
                System.out.println("Data de lançamento: N/A");
            }break;
        }
    }
    private void showStatistics(List<Episode> episodeList){
        var ratingBySeason = episodeList.stream().filter
                (e -> e.getRating() > 0).collect(Collectors.groupingBy(Episode::getSeason,Collectors.averagingDouble(Episode::getRating)));
        var stats = episodeList.stream().filter(e -> e.getRating() > 0).collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("\nEstatísticas Gerais:");
        System.out.println("Média: " + String.format("%.2f", stats.getAverage()));
        System.out.println("Melhor Avaliação: " + stats.getMax());
        System.out.println("Pior Avaliação: " + stats.getMin());
        System.out.println("Total de Episódios Avaliados: " + stats.getCount());
    }

    public void showMenuSerie(SeriesDados seriesDados, List<SeasonsDados> seasonList, List<Episode> episodeList){
        Scanner scann = new Scanner(System.in);
        int option;
        do {
            System.out.println("\n=== MENU DA SÉRIE ===");
            System.out.println("1 - Ver todos os episódios por temporada");
            System.out.println("2 - Ver Top 5 episódios com melhor avaliação");
            System.out.println("3 - Buscar episódios por ano");
            System.out.println("4 - Buscar episódio por nome");
            System.out.println("5 - Ver estatísticas da série");
            System.out.println("6 - Ver séries buscadas");
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
                    for (SeasonsDados season : seasonList) {
                        System.out.println("\nTemporada " + season.Season() + ":");
                        List<EpisodesDados> episodes = season.Episodes();
                        if (episodes != null) {
                            for (EpisodesDados episode : episodes) {
                                System.out.println("- " + episode.Title());
                            }
                        } else {
                            System.out.println("Nenhum episódio encontrado para essa temporada.");
                        }
                    }
                    break;
                case 2:
                    System.out.println("\n Top 5 episódios com melhor avaliação: ");
                    episodeList.stream().filter(e -> e.getRating() > 0)
                            .sorted(Comparator.comparing(Episode::getRating).reversed()).limit(5).forEach(
                                    e -> System.out.println(
                                            "Título: " + e.getTitle() +
                                                    " | Temporada: " + e.getSeason() +
                                                    " | Episódio: " + e.getEpisode() +
                                                    " | Avaliação: " + e.getRating()
                                    ));
                    break;
                case 3:
                    showEpisodesForYear(episodeList);
                    break;
                case 4:
                    getEpisodeByName(episodeList);
                    break;
                case 5:
                    showStatistics(episodeList);
                    break;
                case 6:
                    listSearchSeries();
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
            }
        } while (option != 0);
    }
    public List<SeasonsDados> getSeasonList(){
        return this.seasonList;
    }
    public List<Episode> getEpisodeList(){
        return this.episodeList;
    }


}
