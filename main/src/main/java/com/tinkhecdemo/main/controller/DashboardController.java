package com.tinkhecdemo.main.controller;

import com.tinkhecdemo.main.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        // Verificar si el usuario está logueado
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        cargarDatosDashboard(model);

        // Información del usuario logueado
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "dashboard";
    }

    @GetMapping("/dashboard_tailwind")
    public String mostrarDashboardTailwind(HttpSession session, Model model) {
        // Verificar si el usuario está logueado
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        cargarDatosDashboard(model);

        // Información del usuario logueado
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return "dashboard_tailwind";
    }

    private void cargarDatosDashboard(Model model) {
        // Obtener estadísticas generales
        model.addAttribute("estadisticas", dashboardService.obtenerEstadisticasGenerales());
        model.addAttribute("resumenVentas", dashboardService.obtenerResumenVentas());
        model.addAttribute("distribucionPedidos", dashboardService.obtenerDistribucionPedidosPorEstado());
        model.addAttribute("productosStockBajo", dashboardService.obtenerProductosConStockBajo());
        model.addAttribute("pedidosRecientes", dashboardService.obtenerPedidosRecientes(5));
        model.addAttribute("categorias", dashboardService.obtenerCategoriasProductos());
        model.addAttribute("productosPorCategoria", dashboardService.obtenerProductosPorCategoria());
    }
}