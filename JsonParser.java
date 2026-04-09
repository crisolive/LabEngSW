package br.com.locadora.filmeomdb;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.locadora.ator.Ator;
import br.com.locadora.filme.Filme;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonParser {
	
	public static List<FilmeOmdb> extracaoFilmeOmdb (String json) throws JsonMappingException, JsonProcessingException {
		List <FilmeOmdb> filmes = new ArrayList<FilmeOmdb>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		JsonNode searchArray = rootNode.path("Search");

		for (JsonNode node : searchArray) {
			FilmeOmdb filmeOmdb = new FilmeOmdb();
			 // Extraindo partes específicas
			filmeOmdb.setTitulo(node.path("Title").asText());
			filmeOmdb.setAno(node.path("Year").asText());
			filmeOmdb.setImdbID(node.path("imdbID").asText());
			filmeOmdb.setPoster(node.path("Poster").asText());
			filmes.add(filmeOmdb);
		}
		return filmes;
	}
	public static Filme extracaoFilme (String json) throws JsonMappingException, JsonProcessingException {
		DateTimeFormatter formatterPT = new DateTimeFormatterBuilder()
	            .appendPattern("dd MMM yyyy")
	            .toFormatter(new Locale("pt", "BR"));
	    
	    DateTimeFormatter formatterEN = new DateTimeFormatterBuilder()
	            .appendPattern("dd MMM yyyy")
	            .toFormatter(Locale.ENGLISH);
	    
	    DateTimeFormatter formatterDefault = DateTimeFormatter.ISO_LOCAL_DATE;
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		
		Filme filme = new Filme();
		// Extraindo filme inteiro
		filme.setTitulo(rootNode.path("Title").asText());
		filme.setAno(rootNode.path("Year").asText());
		filme.setClassificacao(rootNode.path("Rated").asText());
		String dataLancamentoStr = rootNode.path("Released").asText().trim(); 
		 if (!dataLancamentoStr.isEmpty() && !dataLancamentoStr.equalsIgnoreCase("N/A")) {
		        try {
		            // Tentamos primeiro com o locale em português
		            LocalDate dataLancamento = LocalDate.parse(dataLancamentoStr, formatterPT);
		            filme.setDataLancamento(dataLancamento);
		        } catch (DateTimeParseException e1) {
		            try {
		                // Se falhar, tentamos com locale em inglês
		                LocalDate dataLancamento = LocalDate.parse(dataLancamentoStr, formatterEN);
		                filme.setDataLancamento(dataLancamento);
		            } catch (DateTimeParseException e2) {
		                try {
		                    // Se ainda falhar, tentamos o formato ISO
		                    LocalDate dataLancamento = LocalDate.parse(dataLancamentoStr, formatterDefault);
		                    filme.setDataLancamento(dataLancamento);
		                } catch (DateTimeParseException e3) {
		                    System.err.println("Não foi possível parsear a data: " + dataLancamentoStr);
		                    // Você pode optar por definir uma data padrão ou deixar null
		                    filme.setDataLancamento(null);
		                }
		            }
		        }
		    } else {
		        filme.setDataLancamento(null);
		    }
	    //desmembrando os atores
	    String atoresString = rootNode.path("Actors").asText();
	   
	    String [] vetatores = atoresString.split(",");
	    List<Ator> atores = new ArrayList<Ator>();
	    for (String i: vetatores) {
	    	Ator ator = new Ator(i.trim());
	    	atores.add(ator);
	    }
	    filme.setAtores(atores);
		filme.setDuracao(rootNode.path("Runtime").asText());
		filme.setGenero(rootNode.path("Genre").asText());
		filme.setNomeDiretor(rootNode.path("Director").asText());
		filme.setResumo(rootNode.path("Plot").asText());
		filme.setPais(rootNode.path("Country").asText());
		filme.setLingua(rootNode.path("Language").asText());
		filme.setPoster(rootNode.path("Poster").asText());
		filme.setImdbId(rootNode.path("imdbID").asText());
		return filme;
	}
}

