package com.example.frota.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.marca.Marca;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonParser {
	
	public static List<CaminhaoOmdb> extracaoFilmeOmdb (String json) throws JsonMappingException, JsonProcessingException {
		List <CaminhaoOmdb> caminhao = new ArrayList<CaminhaoOmdb>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		JsonNode searchArray = rootNode.path("Search");

		for (JsonNode node : searchArray) {
			CaminhaoOmdb caminhaoOmdb = new CaminhaoOmdb();
			 // Extraindo partes específicas
			caminhaoOmdb.setTitulo(node.path("Title").asText());
			caminhaoOmdb.setImdbID(node.path("imdbID").asText());
			caminhao.add(caminhaoOmdb);
		}
		return caminhao;
	}
	public static Caminhao extracaoFilme (String json) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		
		Caminhao caminhao = new Caminhao();
		// Extraindo nome do filme para colocar no modelo caminhao
		caminhao.setModelo(rootNode.path("Title").asText());
		Marca marca = new Marca();
		marca.setId(4);
		caminhao.setMarca(marca);
		return caminhao;
	}
}

