package com.tinkhecdemo.main.controller;

import com.tinkhecdemo.main.model.Usuario;
import com.tinkhecdemo.main.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute("usuario") Usuario usuarioForm,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        // Verificar credenciales
        if (usuarioService.autenticar(usuarioForm.getUsername(), usuarioForm.getPassword())) {
            Usuario usuario = usuarioService.login(usuarioForm.getUsername(), usuarioForm.getPassword()).orElse(null);

            if (usuario != null) {
                // Guardar usuario en sesión
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("username", usuario.getUsername());
                session.setAttribute("rol", usuario.getRol());

                return "redirect:/dashboard_tailwind";
            }
        }

        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/")
    public String redirectToDashboard(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard_tailwind";
        }
        return "redirect:/login";
    }
}