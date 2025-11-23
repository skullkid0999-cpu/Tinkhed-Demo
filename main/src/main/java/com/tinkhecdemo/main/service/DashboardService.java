package com.tinkhecdemo.main.service;

import com.tinkhecdemo.main.model.Pedido;
import com.tinkhecdemo.main.model.Producto;
import com.tinkhecdemo.main.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    public Map<String, Object> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();

        // Estadísticas de usuarios
        estadisticas.put("totalUsuarios", usuarioService.contarUsuarios());
        estadisticas.put("usuariosActivos", usuarioService.contarUsuariosActivos());

        // Estadísticas de productos
        estadisticas.put("totalProductos", productoService.contarProductos());
        estadisticas.put("productosActivos", productoService.contarProductosActivos());
        estadisticas.put("productosStockBajo", productoService.contarProductosConStockBajo());
        estadisticas.put("valorTotalInventario", productoService.calcularValorTotalInventario());

        // Estadísticas de pedidos
        estadisticas.put("totalPedidos", pedidoService.contarPedidos());
        estadisticas.put("pedidosPendientes", pedidoService.contarPedidosPorEstado(Pedido.Estado.PENDIENTE.name()));
        estadisticas.put("pedidosEnPreparacion",
                pedidoService.contarPedidosPorEstado(Pedido.Estado.EN_PREPARACION.name()));
        estadisticas.put("pedidosListos", pedidoService.contarPedidosPorEstado(Pedido.Estado.LISTO.name()));
        estadisticas.put("pedidosEntregados", pedidoService.contarPedidosPorEstado(Pedido.Estado.ENTREGADO.name()));
        estadisticas.put("pedidosCancelados", pedidoService.contarPedidosPorEstado(Pedido.Estado.CANCELADO.name()));

        // Ventas
        estadisticas.put("ventasDelDia", pedidoService.calcularVentasDelDia());

        return estadisticas;
    }

    public Map<String, Object> obtenerResumenVentas() {
        Map<String, Object> resumen = new HashMap<>();

        // Ventas de hoy
        BigDecimal ventasHoy = pedidoService.calcularVentasDelDia();
        resumen.put("ventasHoy", ventasHoy);

        // Pedidos de hoy
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        long pedidosHoy = pedidoService.obtenerTodos().stream()
                .filter(p -> p.getFechaCreacion().isAfter(inicioDia) && p.getFechaCreacion().isBefore(finDia))
                .count();
        resumen.put("pedidosHoy", pedidosHoy);

        // Ticket promedio
        if (pedidosHoy > 0) {
            BigDecimal ticketPromedio = ventasHoy.divide(BigDecimal.valueOf(pedidosHoy), 2, BigDecimal.ROUND_HALF_UP);
            resumen.put("ticketPromedio", ticketPromedio);
        } else {
            resumen.put("ticketPromedio", BigDecimal.ZERO);
        }

        return resumen;
    }

    public Map<String, Integer> obtenerDistribucionPedidosPorEstado() {
        Map<String, Integer> distribucion = new HashMap<>();

        distribucion.put("Pendientes", (int) pedidoService.contarPedidosPorEstado(Pedido.Estado.PENDIENTE.name()));
        distribucion.put("En Preparación",
                (int) pedidoService.contarPedidosPorEstado(Pedido.Estado.EN_PREPARACION.name()));
        distribucion.put("Listos", (int) pedidoService.contarPedidosPorEstado(Pedido.Estado.LISTO.name()));
        distribucion.put("Entregados", (int) pedidoService.contarPedidosPorEstado(Pedido.Estado.ENTREGADO.name()));
        distribucion.put("Cancelados", (int) pedidoService.contarPedidosPorEstado(Pedido.Estado.CANCELADO.name()));

        return distribucion;
    }

    public List<Producto> obtenerProductosConStockBajo() {
        return productoService.obtenerProductosConStockBajo();
    }

    public List<Pedido> obtenerPedidosRecientes(int limite) {
        return pedidoService.obtenerPedidosRecientes(limite);
    }

    public List<String> obtenerCategoriasProductos() {
        return productoService.obtenerCategorias();
    }

    public Map<String, Long> obtenerProductosPorCategoria() {
        Map<String, Long> productosPorCategoria = new HashMap<>();

        for (String categoria : productoService.obtenerCategorias()) {
            long cantidad = productoService.obtenerPorCategoria(categoria).size();
            productosPorCategoria.put(categoria, cantidad);
        }

        return productosPorCategoria;
    }
}