package br.com.Kofflix.KFX.service;

import br.com.Kofflix.KFX.models.SeriesDados;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertDados implements IconvertDados{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T resultDados(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
