package com.dss.model.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dss.model.logica.funcionarios.FuncaoFuncionario;
import com.dss.model.logica.funcionarios.Funcionario;

public class FuncionarioDAO {

    public Optional<Funcionario> obterPorId(int id) {
        String sql = "SELECT id, nome, funcao FROM funcionario WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Funcionario(
                    rs.getInt("id"), rs.getString("nome"),
                    FuncaoFuncionario.valueOf(rs.getString("funcao"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar funcionário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, funcao FROM funcionario ORDER BY nome";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(new Funcionario(
                rs.getInt("id"), rs.getString("nome"),
                FuncaoFuncionario.valueOf(rs.getString("funcao"))));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar funcionários: " + e.getMessage(), e);
        }
        return lista;
    }
}
