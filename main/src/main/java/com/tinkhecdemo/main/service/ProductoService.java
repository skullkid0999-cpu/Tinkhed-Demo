package com.tinkhecdemo.main.service;

import com.tinkhecdemo.main.model.Producto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    private final List<Producto> productos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ProductoService() {
        // Productos de ejemplo para el restaurante
        productos.add(new Producto(idCounter.getAndIncrement(), "Hamburguesa Clásica", "Hamburguesa con carne, lechuga, tomate y queso", 
                new BigDecimal("12.50"), "Comida", 25));
        productos.add(new Producto(idCounter.getAndIncrement(), "Pizza Margherita", "Pizza con salsa de tomate, mozzarella y albahaca", 
                new BigDecimal("15.00"), "Comida", 10));
        productos.add(new Producto(idCounter.getAndIncrement(), "Ensalada César", "Ensalada con pollo, crutones y aderezo César", 
                new BigDecimal("8.50"), "Ensaladas", 15));
        productos.add(new Producto(idCounter.getAndIncrement(), "Coca Cola", "Bebida gaseosa 500ml", 
                new BigDecimal("2.50"), "Bebidas", 50));
        productos.add(new Producto(idCounter.getAndIncrement(), "Cerveza", "Cerveza rubia 500ml", 
                new BigDecimal("3.00"), "Bebidas", 30));
        productos.add(new Producto(idCounter.getAndIncrement(), "Tiramisú", "Postre italiano tradicional", 
                new BigDecimal("6.00"), "Postres", 8));
        productos.add(new Producto(idCounter.getAndIncrement(), "Papas Fritas", "Papas fritas crujientes", 
                new BigDecimal("4.50"), "Acompañamientos", 20));
        productos.add(new Producto(idCounter.getAndIncrement(), "Café", "Café americano caliente", 
                new BigDecimal("2.00"), "Bebidas", 40));
    }

    public List<Producto> obtenerTodos() {
        return new ArrayList<>(productos);
    }

    public List<Producto> obtenerActivos() {
        return productos.stream()
                .filter(Producto::isActivo)
                .collect(Collectors.toList());
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productos.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    public List<String> obtenerCategorias() {
        return productos.stream()
                .map(Producto::getCategoria)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Producto crear(Producto producto) {
        producto.setId(idCounter.getAndIncrement());
        producto.setFechaCreacion(java.time.LocalDateTime.now());
        productos.add(producto);
        return producto;
    }

    public Optional<Producto> actualizar(Long id, Producto productoActualizado) {
        return obtenerPorId(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setStock(productoActualizado.getStock());
            producto.setStockMinimo(productoActualizado.getStockMinimo());
            producto.setActivo(productoActualizado.isActivo());
            return producto;
        });
    }

    public boolean eliminar(Long id) {
        return productos.removeIf(p -> p.getId().equals(id));
    }

    public boolean actualizarStock(Long id, int nuevoStock) {
        return obtenerPorId(id).map(producto -> {
            producto.setStock(nuevoStock);
            return true;
        }).orElse(false);
    }

    public boolean reducirStock(Long id, int cantidad) {
        return obtenerPorId(id).map(producto -> {
            if (producto.getStock() >= cantidad) {
                producto.setStock(producto.getStock() - cantidad);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public boolean aumentarStock(Long id, int cantidad) {
        return obtenerPorId(id).map(producto -> {
            producto.setStock(producto.getStock() + cantidad);
            return true;
        }).orElse(false);
    }

    public List<Producto> obtenerProductosConStockBajo() {
        return productos.stream()
                .filter(Producto::necesitaReposicion)
                .collect(Collectors.toList());
    }

    public long contarProductos() {
        return productos.size();
    }

    public long contarProductosActivos() {
        return productos.stream().filter(Producto::isActivo).count();
    }

    public long contarProductosConStockBajo() {
        return obtenerProductosConStockBajo().size();
    }

    public BigDecimal calcularValorTotalInventario() {
        return productos.stream()
                .filter(Producto::isActivo)
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}