package com.dss.model.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dss.model.logica.Ingredientes.Ingrediente;

public class IngredienteDAO {

    public List<Ingrediente> listarTodos() {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT nome, quantidade_em_stock, nivel_minimo, unidade FROM ingrediente";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Ingrediente(
                    rs.getString("nome"),
                    rs.getDouble("quantidade_em_stock"),
                    rs.getDouble("nivel_minimo"),
                    rs.getString("unidade")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingredientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizarStock(String nome, double novaQuantidade) {
        String sql = "UPDATE ingrediente SET quantidade_em_stock = ? WHERE nome = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, novaQuantidade);
            ps.setString(2, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar stock de " + nome + ": " + e.getMessage(), e);
        }
    }

    public boolean alterarQuantidade(String nome, double novaQuantidade) {
        String sql = "UPDATE ingrediente SET quantidade_em_stock = ? WHERE nome = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, novaQuantidade);
            ps.setString(2, nome);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar quantidade de " + nome + ": " + e.getMessage(), e);
        }
    }
}
