package com.example.frota.api;

import java.io.Serializable;

public class CaminhaoOmdb implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String modelo;
	private String imdbID;
	
	
	public CaminhaoOmdb(DadosListagemOmdb dados) {
		this.modelo = dados.titulo();
		this.imdbID = dados.imdbID();
	}

	public CaminhaoOmdb() {
		super();
	}

	public String getTitulo() {
		return modelo;
	}

	public void setTitulo(String titulo) {
		this.modelo = titulo;
	}

	public String getImdbID() {
		return imdbID;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}
}
