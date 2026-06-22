package com.example.trabalhopratico1;

import java.io.Serializable;

public class Chamado implements Serializable {
    private int id;
    private String titulo;
    private String local;
    private String tipo;
    private String data;
    private String descricao;
    private String status;
    private String solucao;
    private String caminhoImagem;

    public Chamado(String titulo, String local, String tipo, String descricao, String data) {
        this.titulo = titulo;
        this.local = local;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public String getTitulo() { return titulo; }
    public String getLocal() { return local; }
    public String getTipo() { return tipo; }
    public String getData() { return data; }
    public String getStatus() { return status; }
    public String getSolucao() { return solucao; }
    public String getCaminhoImagem() { return caminhoImagem; }

    public void setStatus(String status) { this.status = status; }
    public void setSolucao(String solucao) { this.solucao = solucao; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }
}
