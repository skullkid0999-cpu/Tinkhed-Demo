package com.tinkhecdemo.main.controller;

import com.tinkhecdemo.main.model.Pedido;
import com.tinkhecdemo.main.model.PedidoItem;
import com.tinkhecdemo.main.model.Producto;
import com.tinkhecdemo.main.model.Usuario;
import com.tinkhecdemo.main.service.PedidoService;
import com.tinkhecdemo.main.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProductoService productoService;

    private boolean verificarSesion(HttpSession session) {
        return session.getAttribute("usuarioLogueado") != null;
    }

    @GetMapping
    public String listarPedidos(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("pedidos", pedidoService.obtenerTodos());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("pedido", new Pedido());
        model.addAttribute("productos", productoService.obtenerActivos());
        model.addAttribute("estados", Pedido.Estado.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "pedidos/formulario";
    }

    @PostMapping
    public String crearPedido(@Valid @ModelAttribute("pedido") Pedido pedido,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerActivos());
            model.addAttribute("estados", Pedido.Estado.values());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            return "pedidos/formulario";
        }

        // Asignar usuario actual al pedido
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        pedido.setUsuarioId(usuario.getId());

        pedidoService.crear(pedido);
        return "redirect:/pedidos?success";
    }

    @GetMapping("/{id}")
    public String verPedido(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return pedidoService.obtenerPorId(id)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    return "pedidos/detalle";
                })
                .orElse("redirect:/pedidos?error=notfound");
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return pedidoService.obtenerPorId(id)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("productos", productoService.obtenerActivos());
                    model.addAttribute("estados", Pedido.Estado.values());
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    return "pedidos/formulario";
                })
                .orElse("redirect:/pedidos?error=notfound");
    }

    @PostMapping("/{id}")
    public String actualizarPedido(@PathVariable Long id,
            @Valid @ModelAttribute("pedido") Pedido pedido,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerActivos());
            model.addAttribute("estados", Pedido.Estado.values());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            return "pedidos/formulario";
        }

        pedidoService.actualizar(id, pedido);
        return "redirect:/pedidos?success";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id, HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        pedidoService.eliminar(id);
        return "redirect:/pedidos?deleted";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable Long id,
            @RequestParam String estado,
            HttpSession session) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        pedidoService.cambiarEstado(id, estado);
        return "redirect:/pedidos/dashboard-css?statusChanged";
    }

    @GetMapping("/estado/{estado}")
    public String listarPorEstado(@PathVariable String estado, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        model.addAttribute("pedidos", pedidoService.obtenerPorEstado(estado));
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "pedidos/lista";
    }

    @GetMapping("/dashboard-css")
    public String dashboardCss(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        List<Pedido> todos = pedidoService.obtenerTodos();

        List<Pedido> nuevos = todos.stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado()))
                .toList();

        List<Pedido> preparacion = todos.stream()
                .filter(p -> "EN_PREPARACION".equals(p.getEstado()))
                .toList();

        List<Pedido> listos = todos.stream()
                .filter(p -> "LISTO".equals(p.getEstado()))
                .toList();

        model.addAttribute("nuevos", nuevos);
        model.addAttribute("preparacion", preparacion);
        model.addAttribute("listos", listos);

        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("pageTitle", "Gestión de Pedidos");

        return "pedidostcss";
    }

    @GetMapping("/nuevo-css")
    public String mostrarFormularioNuevoCss(HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        Pedido pedido = new Pedido();
        pedido.getItems().add(new PedidoItem());
        model.addAttribute("pedido", pedido);
        model.addAttribute("productos", productoService.obtenerActivos());
        model.addAttribute("estados", Pedido.Estado.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("pageTitle", "Nuevo Pedido");

        return "pedidos/formulariotcss";
    }

    @PostMapping("/nuevo-css")
    public String crearPedidoCss(@Valid @ModelAttribute("pedido") Pedido pedido,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerActivos());
            model.addAttribute("estados", Pedido.Estado.values());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", session.getAttribute("rol"));
            model.addAttribute("pageTitle", "Nuevo Pedido");
            return "pedidos/formulariotcss";
        }

        // Asignar usuario actual al pedido
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        pedido.setUsuarioId(usuario.getId());

        pedidoService.crear(pedido);
        return "redirect:/pedidos/dashboard-css?success";
    }

    @GetMapping("/detalle-css/{id}")
    public String verPedidoCss(@PathVariable Long id, HttpSession session, Model model) {
        if (!verificarSesion(session)) {
            return "redirect:/login";
        }

        return pedidoService.obtenerPorId(id)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
                    model.addAttribute("username", session.getAttribute("username"));
                    model.addAttribute("rol", session.getAttribute("rol"));
                    return "pedidos/detalletcss";
                })
                .orElse("redirect:/pedidos/dashboard-css?error=notfound");
    }
}
