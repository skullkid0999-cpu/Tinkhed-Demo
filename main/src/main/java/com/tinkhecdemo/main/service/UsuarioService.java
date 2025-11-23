package com.tinkhecdemo.main.service;

import com.tinkhecdemo.main.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public UsuarioService() {
        // Usuario por defecto para pruebas
        Usuario admin = new Usuario(idCounter.getAndIncrement(), "admin", "admin123", "Administrador", "ADMIN");
        usuarios.add(admin);
        
        Usuario mesero = new Usuario(idCounter.getAndIncrement(), "mesero", "mesero123", "Juan Pérez", "MESERO");
        usuarios.add(mesero);
    }

    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public Usuario crear(Usuario usuario) {
        usuario.setId(idCounter.getAndIncrement());
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        usuarios.add(usuario);
        return usuario;
    }

    public Optional<Usuario> actualizar(Long id, Usuario usuarioActualizado) {
        return obtenerPorId(id).map(usuario -> {
            usuario.setUsername(usuarioActualizado.getUsername());
            usuario.setPassword(usuarioActualizado.getPassword());
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setRol(usuarioActualizado.getRol());
            usuario.setActivo(usuarioActualizado.isActivo());
            return usuario;
        });
    }

    public boolean eliminar(Long id) {
        return usuarios.removeIf(u -> u.getId().equals(id));
    }

    public boolean autenticar(String username, String password) {
        return obtenerPorUsername(username)
                .map(usuario -> usuario.getPassword().equals(password) && usuario.isActivo())
                .orElse(false);
    }

    public Optional<Usuario> login(String username, String password) {
        return obtenerPorUsername(username)
                .filter(usuario -> usuario.getPassword().equals(password) && usuario.isActivo());
    }

    public long contarUsuarios() {
        return usuarios.size();
    }

    public long contarUsuariosActivos() {
        return usuarios.stream().filter(Usuario::isActivo).count();
    }
}