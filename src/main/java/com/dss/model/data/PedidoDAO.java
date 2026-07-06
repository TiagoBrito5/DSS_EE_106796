package com.dss.model.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dss.model.logica.pedidos.EstadoPedido;
import com.dss.model.logica.pedidos.EstadoPreparacao;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;
import com.dss.model.logica.pedidos.ItemPedido;
import com.dss.model.logica.pedidos.Pedido;

public class PedidoDAO {

    private final ProdutoDAO produtoDAO;

    public PedidoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void guardar(Pedido pedido) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pedido (data_hora, estado) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, Timestamp.valueOf(pedido.getDataHora()));
                ps.setString(2, pedido.getEstado().name());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) pedido.setId(rs.getInt(1));
                }
            }

            try (PreparedStatement psItem = conn.prepareStatement(
                     "INSERT INTO item_pedido (pedido_id, produto_codigo, quantidade, estado_preparacao, preco_unitario) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psPers = conn.prepareStatement(
                     "INSERT INTO item_personalizacao (item_id, personalizacao_id) VALUES (?, ?)")) {

                for (ItemPedido item : pedido.getItens()) {
                    psItem.setInt(1, pedido.getId());
                    psItem.setString(2, item.getProduto().getCodigo());
                    psItem.setInt(3, item.getQuantidade());
                    psItem.setString(4, item.getEstadoPreparacao().name());
                    psItem.setDouble(5, item.getPrecoUnitario());
                    psItem.executeUpdate();
                    try (ResultSet rs = psItem.getGeneratedKeys()) {
                        if (rs.next()) item.setId(rs.getInt(1));
                    }

                    for (Personalizacao p : item.getPersonalizacoesSelecionadas()) {
                        psPers.setInt(1, item.getId());
                        psPers.setInt(2, p.getId());
                        psPers.addBatch();
                    }
                }
                psPers.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { }}
            throw new RuntimeException("Erro ao guardar pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException ex) { }}
        }
    }

    public Optional<Pedido> obterPorId(int id) {
        String sql = "SELECT id, data_hora, estado FROM pedido WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = new Pedido(rs.getInt("id"),
                        rs.getTimestamp("data_hora").toLocalDateTime(),
                        EstadoPedido.valueOf(rs.getString("estado")));
                    String sqlItens = "SELECT id, produto_codigo, quantidade, estado_preparacao, preco_unitario FROM item_pedido WHERE pedido_id = ?";
                    try (PreparedStatement psItens = DatabaseConnection.getConnection().prepareStatement(sqlItens)) {
                        psItens.setInt(1, pedido.getId());
                        try (ResultSet rsItens = psItens.executeQuery()) {
                            while (rsItens.next()) {
                                int itemId           = rsItens.getInt("id");
                                String codigo        = rsItens.getString("produto_codigo");
                                int quantidade       = rsItens.getInt("quantidade");
                                EstadoPreparacao ep  = EstadoPreparacao.valueOf(rsItens.getString("estado_preparacao"));
                                double precoUnitario = rsItens.getDouble("preco_unitario");
                                Produto produto = produtoDAO.obterPorCodigo(codigo)
                                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + codigo));
                                List<Personalizacao> selecionadas = new ArrayList<>();
                                String sqlPers = "SELECT personalizacao_id FROM item_personalizacao WHERE item_id = ?";
                                try (PreparedStatement psPers = DatabaseConnection.getConnection().prepareStatement(sqlPers)) {
                                    psPers.setInt(1, itemId);
                                    try (ResultSet rsPers = psPers.executeQuery()) {
                                        while (rsPers.next()) {
                                            int pid = rsPers.getInt("personalizacao_id");
                                            produto.getPersonalizacoesDisponiveis().stream()
                                                .filter(p -> p.getId() == pid)
                                                .findFirst()
                                                .ifPresent(selecionadas::add);
                                        }
                                    }
                                }
                                ItemPedido item = new ItemPedido(produto, quantidade, selecionadas, ep, precoUnitario);
                                item.setId(itemId);
                                pedido.adicionarItem(item);
                            }
                        }
                    }
                    return Optional.of(pedido);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter pedido #" + id + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Pedido> listarTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, data_hora, estado FROM pedido ORDER BY id";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pedido pedido = new Pedido(rs.getInt("id"),
                    rs.getTimestamp("data_hora").toLocalDateTime(),
                    EstadoPedido.valueOf(rs.getString("estado")));
                String sqlItens = "SELECT id, produto_codigo, quantidade, estado_preparacao, preco_unitario FROM item_pedido WHERE pedido_id = ?";
                try (PreparedStatement psItens = DatabaseConnection.getConnection().prepareStatement(sqlItens)) {
                    psItens.setInt(1, pedido.getId());
                    try (ResultSet rsItens = psItens.executeQuery()) {
                        while (rsItens.next()) {
                            int itemId           = rsItens.getInt("id");
                            String codigo        = rsItens.getString("produto_codigo");
                            int quantidade       = rsItens.getInt("quantidade");
                            EstadoPreparacao ep  = EstadoPreparacao.valueOf(rsItens.getString("estado_preparacao"));
                            double precoUnitario = rsItens.getDouble("preco_unitario");
                            Produto produto = produtoDAO.obterPorCodigo(codigo)
                                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + codigo));
                            List<Personalizacao> selecionadas = new ArrayList<>();
                            String sqlPers = "SELECT personalizacao_id FROM item_personalizacao WHERE item_id = ?";
                            try (PreparedStatement psPers = DatabaseConnection.getConnection().prepareStatement(sqlPers)) {
                                psPers.setInt(1, itemId);
                                try (ResultSet rsPers = psPers.executeQuery()) {
                                    while (rsPers.next()) {
                                        int pid = rsPers.getInt("personalizacao_id");
                                        produto.getPersonalizacoesDisponiveis().stream()
                                            .filter(p -> p.getId() == pid)
                                            .findFirst()
                                            .ifPresent(selecionadas::add);
                                    }
                                }
                            }
                            ItemPedido item = new ItemPedido(produto, quantidade, selecionadas, ep, precoUnitario);
                            item.setId(itemId);
                            pedido.adicionarItem(item);
                        }
                    }
                }
                lista.add(pedido);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizar(Pedido pedido) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pedido SET estado = ? WHERE id = ?")) {
                ps.setString(1, pedido.getEstado().name());
                ps.setInt(2, pedido.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE item_pedido SET estado_preparacao = ? WHERE id = ?")) {
                for (ItemPedido item : pedido.getItens()) {
                    ps.setString(1, item.getEstadoPreparacao().name());
                    ps.setInt(2, item.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { }}
            throw new RuntimeException("Erro ao actualizar pedido #" + pedido.getId() + ": " + e.getMessage(), e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException ex) { }}
        }
    }

    public long contarEntreguesNoIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM pedido WHERE estado = 'ENTREGUE' AND data_hora BETWEEN ? AND ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar pedidos: " + e.getMessage(), e);
        }
    }

    public double somarFaturacaoNoIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COALESCE(SUM(p.preco * i.quantidade), 0) " +
                     "FROM pedido pd " +
                     "JOIN item_pedido i ON i.pedido_id = pd.id " +
                     "JOIN produto p ON p.codigo = i.produto_codigo " +
                     "WHERE pd.estado = 'ENTREGUE' AND pd.data_hora BETWEEN ? AND ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar faturação: " + e.getMessage(), e);
        }
    }

    public Map<String, Long> produtosMaisVendidosNoIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT p.nome, SUM(i.quantidade) AS total " +
                     "FROM pedido pd " +
                     "JOIN item_pedido i ON i.pedido_id = pd.id " +
                     "JOIN produto p ON p.codigo = i.produto_codigo " +
                     "WHERE pd.estado = 'ENTREGUE' AND pd.data_hora BETWEEN ? AND ? " +
                     "GROUP BY p.nome ORDER BY total DESC";
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.put(rs.getString("nome"), rs.getLong("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar produtos mais vendidos: " + e.getMessage(), e);
        }
        return resultado;
    }

}
