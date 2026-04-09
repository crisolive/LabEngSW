package br.com.locadora.filmeomdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class OmdbService {
	@Value ("${omdb.api.url}")
	private String omdbApiUrl;
	
	@Value ("${omdb.api.key}")
	private String apiKey;
	
	private final RestTemplate restTemplate = new RestTemplate();

    public  String buscarFilmePorTitulo(String titulo) {
    	String url = String.format("https://www.omdbapi.com"+ "/?apikey="+"99999"+"&s="+ titulo);
    	//String url =  omdbApiUrl + apiKey +"&s="+ titulo;
        return restTemplate.getForObject(url, String.class); // Retorna JSON como String
    }

    public  String buscarFilmePorId(String id) {
    	String url = String.format("https://www.omdbapi.com"+ "/?apikey="+"99999"+"&i="+ id);
        return restTemplate.getForObject(url, String.class); // Retorna JSON como String
    }
}
