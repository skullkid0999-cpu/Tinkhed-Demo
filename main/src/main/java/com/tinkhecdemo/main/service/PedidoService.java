package com.tinkhecdemo.main.service;

import com.tinkhecdemo.main.model.Pedido;
import com.tinkhecdemo.main.model.PedidoItem;
import com.tinkhecdemo.main.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final AtomicLong itemIdCounter = new AtomicLong(1);

    @Autowired
    private ProductoService productoService;

    public PedidoService() {
        // Pedidos de ejemplo
        Pedido pedidoEjemplo = new Pedido(idCounter.getAndIncrement(), "PED-001", 2L, "Cliente Ejemplo");
        pedidoEjemplo.setClienteTelefono("555-0123");
        pedidoEjemplo.setEstado(Pedido.Estado.PENDIENTE.name());

        // Agregar items de ejemplo
        PedidoItem item1 = new PedidoItem(itemIdCounter.getAndIncrement(), pedidoEjemplo.getId(), 1L,
                "Hamburguesa Clásica", 2, new java.math.BigDecimal("12.50"));
        PedidoItem item2 = new PedidoItem(itemIdCounter.getAndIncrement(), pedidoEjemplo.getId(), 4L, "Coca Cola", 2,
                new java.math.BigDecimal("2.50"));

        pedidoEjemplo.agregarItem(item1);
        pedidoEjemplo.agregarItem(item2);
        pedidoEjemplo.calcularTotal();

        pedidos.add(pedidoEjemplo);
    }

    public List<Pedido> obtenerTodos() {
        return new ArrayList<>(pedidos);
    }

    public List<Pedido> obtenerPorEstado(String estado) {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    public List<Pedido> obtenerPorUsuario(Long usuarioId) {
        return pedidos.stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Optional<Pedido> obtenerPorNumero(String numeroPedido) {
        return pedidos.stream()
                .filter(p -> p.getNumeroPedido().equals(numeroPedido))
                .findFirst();
    }

    public Pedido crear(Pedido pedido) {
        pedido.setId(idCounter.getAndIncrement());
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setFechaActualizacion(LocalDateTime.now());

        // Asignar nombres de productos y calcular subtotales si es necesario
        for (PedidoItem item : pedido.getItems()) {
            productoService.obtenerPorId(item.getProductoId()).ifPresent(producto -> {
                item.setProductoNombre(producto.getNombre());
            });
        }

        // Calcular total y reducir stock
        pedido.calcularTotal();
        reducirStockDeProductos(pedido);

        pedidos.add(pedido);
        return pedido;
    }

    public Optional<Pedido> actualizar(Long id, Pedido pedidoActualizado) {
        return obtenerPorId(id).map(pedido -> {
            pedido.setClienteNombre(pedidoActualizado.getClienteNombre());
            pedido.setClienteTelefono(pedidoActualizado.getClienteTelefono());
            pedido.setEstado(pedidoActualizado.getEstado());
            pedido.setObservaciones(pedidoActualizado.getObservaciones());

            // Actualizar items si se proporcionan
            if (pedidoActualizado.getItems() != null) {
                pedido.setItems(new ArrayList<>(pedidoActualizado.getItems()));
                pedido.calcularTotal();
            }

            return pedido;
        });
    }

    public boolean eliminar(Long id) {
        return pedidos.removeIf(p -> p.getId().equals(id));
    }

    public boolean cambiarEstado(Long id, String nuevoEstado) {
        return obtenerPorId(id).map(pedido -> {
            pedido.setEstado(nuevoEstado);
            return true;
        }).orElse(false);
    }

    public boolean agregarItem(Long pedidoId, PedidoItem item) {
        return obtenerPorId(pedidoId).map(pedido -> {
            item.setId(itemIdCounter.getAndIncrement());
            item.setPedidoId(pedidoId);
            pedido.agregarItem(item);
            return true;
        }).orElse(false);
    }

    public boolean removerItem(Long pedidoId, Long itemId) {
        return obtenerPorId(pedidoId).map(pedido -> {
            pedido.getItems().removeIf(item -> item.getId().equals(itemId));
            pedido.calcularTotal();
            return true;
        }).orElse(false);
    }

    public boolean actualizarItem(Long pedidoId, Long itemId, PedidoItem itemActualizado) {
        return obtenerPorId(pedidoId).map(pedido -> {
            return pedido.getItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .map(item -> {
                        item.setCantidad(itemActualizado.getCantidad());
                        item.setPrecio(itemActualizado.getPrecio());
                        item.setObservaciones(itemActualizado.getObservaciones());
                        pedido.calcularTotal();
                        return true;
                    }).orElse(false);
        }).orElse(false);
    }

    private String generarNumeroPedido() {
        return "PED-" + String.format("%03d", idCounter.get());
    }

    private void reducirStockDeProductos(Pedido pedido) {
        for (PedidoItem item : pedido.getItems()) {
            productoService.reducirStock(item.getProductoId(), item.getCantidad());
        }
    }

    public long contarPedidos() {
        return pedidos.size();
    }

    public long contarPedidosPorEstado(String estado) {
        return obtenerPorEstado(estado).size();
    }

    public java.math.BigDecimal calcularVentasDelDia() {
        LocalDateTime inicioDia = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        return pedidos.stream()
                .filter(p -> p.getFechaCreacion().isAfter(inicioDia) && p.getFechaCreacion().isBefore(finDia))
                .filter(p -> !p.getEstado().equals(Pedido.Estado.CANCELADO.name()))
                .map(Pedido::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public List<Pedido> obtenerPedidosRecientes(int limite) {
        return pedidos.stream()
                .sorted((p1, p2) -> p2.getFechaCreacion().compareTo(p1.getFechaCreacion()))
                .limit(limite)
                .collect(Collectors.toList());
    }
}