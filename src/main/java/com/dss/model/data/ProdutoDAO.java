package com.dss.model.data;

import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;
import com.dss.model.logica.produtos.ProdutoPreparado;
import com.dss.model.logica.produtos.ProdutoPronto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAO {

    public ProdutoDAO() {
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT codigo, nome, preco, tipo, tempo_estimado_preparacao FROM produto ORDER BY codigo";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = "PREPARADO".equals(rs.getString("tipo"))
                    ? new ProdutoPreparado(rs.getString("codigo"), rs.getString("nome"), rs.getDouble("preco"), rs.getInt("tempo_estimado_preparacao"))
                    : new ProdutoPronto(rs.getString("codigo"), rs.getString("nome"), rs.getDouble("preco"));
                try (PreparedStatement psPers = DatabaseConnection.getConnection().prepareStatement(
                        "SELECT id, descricao, categoria FROM personalizacao WHERE produto_codigo = ?")) {
                    psPers.setString(1, produto.getCodigo());
                    try (ResultSet rsPers = psPers.executeQuery()) {
                        while (rsPers.next())
                            produto.adicionarPersonalizacao(new Personalizacao(
                                rsPers.getInt("id"), rsPers.getString("descricao"), rsPers.getString("categoria")));
                    }
                }
                try (PreparedStatement psFicha = DatabaseConnection.getConnection().prepareStatement(
                        "SELECT ingrediente_nome, quantidade FROM ficha_ingrediente WHERE produto_codigo = ?")) {
                    psFicha.setString(1, produto.getCodigo());
                    try (ResultSet rsFicha = psFicha.executeQuery()) {
                        while (rsFicha.next())
                            produto.adicionarIngrediente(rsFicha.getString("ingrediente_nome"), rsFicha.getDouble("quantidade"));
                    }
                }
                lista.add(produto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Produto> obterPorCodigo(String codigo) {
        String sql = "SELECT codigo, nome, preco, tipo, tempo_estimado_preparacao FROM produto WHERE codigo = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto produto = "PREPARADO".equals(rs.getString("tipo"))
                        ? new ProdutoPreparado(rs.getString("codigo"), rs.getString("nome"), rs.getDouble("preco"), rs.getInt("tempo_estimado_preparacao"))
                        : new ProdutoPronto(rs.getString("codigo"), rs.getString("nome"), rs.getDouble("preco"));
                    try (PreparedStatement psPers = DatabaseConnection.getConnection().prepareStatement(
                            "SELECT id, descricao, categoria FROM personalizacao WHERE produto_codigo = ?")) {
                        psPers.setString(1, produto.getCodigo());
                        try (ResultSet rsPers = psPers.executeQuery()) {
                            while (rsPers.next())
                                produto.adicionarPersonalizacao(new Personalizacao(
                                    rsPers.getInt("id"), rsPers.getString("descricao"), rsPers.getString("categoria")));
                        }
                    }
                    try (PreparedStatement psFicha = DatabaseConnection.getConnection().prepareStatement(
                            "SELECT ingrediente_nome, quantidade FROM ficha_ingrediente WHERE produto_codigo = ?")) {
                        psFicha.setString(1, produto.getCodigo());
                        try (ResultSet rsFicha = psFicha.executeQuery()) {
                            while (rsFicha.next())
                                produto.adicionarIngrediente(rsFicha.getString("ingrediente_nome"), rsFicha.getDouble("quantidade"));
                        }
                    }
                    return Optional.of(produto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter produto " + codigo + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
