package com.tinkhecdemo.main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Long id;
    private String numeroPedido;
    private Long usuarioId;
    private String clienteNombre;
    private String clienteTelefono;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<PedidoItem> items;
    private String observaciones;

    public enum Estado {
        PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO
    }

    public Pedido() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = Estado.PENDIENTE.name();
        this.total = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    public Pedido(Long id, String numeroPedido, Long usuarioId, String clienteNombre) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.usuarioId = usuarioId;
        this.clienteNombre = clienteNombre;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = Estado.PENDIENTE.name();
        this.total = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<PedidoItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<PedidoItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void agregarItem(PedidoItem item) {
        getItems().add(item);
        calcularTotal();
    }

    public void removerItem(PedidoItem item) {
        getItems().remove(item);
        calcularTotal();
    }

    public void calcularTotal() {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.total = items.stream()
                .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estaPendiente() {
        return Estado.PENDIENTE.name().equals(this.estado);
    }

    public boolean estaCancelado() {
        return Estado.CANCELADO.name().equals(this.estado);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                '}';
    }
}