package com.example.frota.api;

public record DadosListagemOmdb(
	
	String titulo,
	String imdbID
	) {
	public DadosListagemOmdb (CaminhaoOmdb filmeOmdb) {
	    this(filmeOmdb.getImdbID(), 
	    		filmeOmdb.getTitulo()
	    	); 
	}
}
