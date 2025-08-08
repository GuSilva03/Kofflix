package br.com.Kofflix.KFX.service;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;

public class ConsultaIA {
    private static final String API_KEY = "&apikey=" + System.getenv("API_IA_KEY");
    private static final String URL = "https://api.cohere.ai/v1/chat";

    public static String obterTraducao(String texto) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        JsonObject requisicao = new JsonObject();
        requisicao.addProperty("model", "command-r-plus");
        requisicao.addProperty("message", "Traduza para o português o texto: " + texto);
        requisicao.addProperty("temperature", 0.7);
        requisicao.addProperty("max_tokens", 1000);

        RequestBody body = RequestBody.create(
                gson.toJson(requisicao),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Erro: " + response.code() + " - " + response.message();
            }

            JsonObject respostaJson = gson.fromJson(response.body().string(), JsonObject.class);
            return respostaJson.has("text") ? respostaJson.get("text").getAsString() : "Sem resposta.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao chamar a API da Cohere: " + e.getMessage();
        }
    }
}
