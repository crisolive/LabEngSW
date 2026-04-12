package com.example.frota.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
//transformei em Service para utilizar as variaveis que 
//estao na properties 
public class OmdbService {
	@Value ("${api.omdb.url}")
	private String apiUrl;
	
	@Value ("${api.omdb.key}")
	private String apiKey;
	
	private final RestTemplate restTemplate = new RestTemplate();

    public  String buscarFilmePorTitulo(String titulo) {
    	String url = String.format("%s?apikey=%s&s=%s", apiUrl, apiKey, titulo);
    	//System.out.println("Montando URL = "+url);
        return restTemplate.getForObject(url, String.class); // Retorna JSON como String
    }

    public  String buscarFilmePorId(String id) {
    	String url = String.format("%s?apikey=%s&i=%s", apiUrl, apiKey, id);
        return restTemplate.getForObject(url, String.class); // Retorna JSON como String
    }
}
