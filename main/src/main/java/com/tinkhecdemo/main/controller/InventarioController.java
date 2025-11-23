package com.tinkhecdemo.main.controller;

import com.tinkhecdemo.main.model.Producto;
import com.tinkhecdemo.main.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private ProductoService productoService;

    private boolean verificarSesion(HttpSession session) {
        return session.getAttribute("usuarioLogueado") != null;
    }

    @GetMapping
    public String listarProductos(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("productosStockBajo", productoService.obtenerProductosConStockBajo());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "inventario/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "inventario/formulario";
    }

    @PostMapping
    public String crearProducto(@Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            return "inventario/formulario";
        }

        productoService.crear(producto);
        return "redirect:/inventario?success";
    }

    @GetMapping("/{id}")
    public String verProducto(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return productoService.obtenerPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("necesitaReposicion", producto.necesitaReposicion());
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    return "inventario/detalle";
                })
                .orElse("redirect:/inventario?error=notfound");
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return productoService.obtenerPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("categorias", productoService.obtenerCategorias());
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    return "inventario/formulario";
                })
                .orElse("redirect:/inventario?error=notfound");
    }

    @PostMapping("/{id}")
    public String actualizarProducto(@PathVariable Long id,
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            return "inventario/formulario";
        }

        productoService.actualizar(id, producto);
        return "redirect:/inventario?success";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        productoService.eliminar(id);
        return "redirect:/inventario?deleted";
    }

    @PostMapping("/{id}/stock")
    public String actualizarStock(@PathVariable Long id,
            @RequestParam int nuevoStock,
            HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        productoService.actualizarStock(id, nuevoStock);
        return "redirect:/inventario/" + id + "?stockUpdated";
    }

    @PostMapping("/{id}/aumentar-stock")
    public String aumentarStock(@PathVariable Long id,
            @RequestParam int cantidad,
            HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        productoService.aumentarStock(id, cantidad);
        return "redirect:/inventario/" + id + "?stockIncreased";
    }

    @PostMapping("/{id}/reducir-stock")
    public String reducirStock(@PathVariable Long id,
            @RequestParam int cantidad,
            HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        productoService.reducirStock(id, cantidad);
        return "redirect:/inventario/" + id + "?stockReduced";
    }

    @GetMapping("/categoria/{categoria}")
    public String listarPorCategoria(@PathVariable String categoria, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.obtenerPorCategoria(categoria));
        model.addAttribute("categoriaFiltro", categoria);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "inventario/lista";
    }

    @GetMapping("/stock-bajo")
    public String listarStockBajo(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.obtenerProductosConStockBajo());
        model.addAttribute("filtroStockBajo", true);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "inventario/lista";
    }

    @GetMapping("/dashboard-css")
    public String dashboardCss(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("pageTitle", "Gestión de Inventario");

        return "inventariotcss";
    }

    @GetMapping("/nuevo-css")
    public String mostrarFormularioNuevoCss(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("pageTitle", "Nuevo Producto");

        return "inventario/formulariotcss";
    }

    @PostMapping("/nuevo-css")
    public String crearProductoCss(@Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            model.addAttribute("pageTitle", "Nuevo Producto");
            return "inventario/formulariotcss";
        }

        productoService.crear(producto);
        return "redirect:/inventario/dashboard-css?success";
    }

    @PostMapping("/{id}/eliminar-css")
    public String eliminarProductoCss(@PathVariable Long id, HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        productoService.eliminar(id);
        return "redirect:/inventario/dashboard-css?deleted";
    }

    @GetMapping("/{id}/editar-css")
    public String mostrarFormularioEditarCss(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return productoService.obtenerPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("categorias", productoService.obtenerCategorias());
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    model.addAttribute("pageTitle", "Editar Producto");
                    return "inventario/formularioeditarcss";
                })
                .orElse("redirect:/inventario/dashboard-css?error=notfound");
    }

    @PostMapping("/{id}/actualizar-css")
    public String actualizarProductoCss(@PathVariable Long id,
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            model.addAttribute("pageTitle", "Editar Producto");
            return "inventario/formularioeditarcss";
        }

        productoService.actualizar(id, producto);
        return "redirect:/inventario/dashboard-css?success";
    }
}