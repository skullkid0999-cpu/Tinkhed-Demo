package com.tinkhecdemo.main.model;

import java.math.BigDecimal;

public class PedidoItem {
    private Long id;
    private Long pedidoId;
    private Long productoId;
    private String productoNombre;
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private String observaciones;

    public PedidoItem() {
    }

    public PedidoItem(Long id, Long pedidoId, Long productoId, String productoNombre, int cantidad, BigDecimal precio) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        if (this.precio != null) {
            this.subtotal = this.precio.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
        if (this.cantidad > 0) {
            this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "PedidoItem{" +
                "id=" + id +
                ", productoNombre='" + productoNombre + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", subtotal=" + subtotal +
                '}';
    }
}