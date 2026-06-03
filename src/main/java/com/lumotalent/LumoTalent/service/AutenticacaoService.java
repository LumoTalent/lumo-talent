package com.lumotalent.LumoTalent.service;

import com.lumotalent.LumoTalent.model.Usuario;
import com.lumotalent.LumoTalent.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacaoService(UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder,
                               JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }
        return jwtService.gerarToken(email);
    }

    public Usuario registrar(String nome, String email, String senha, String perfil) {
        if (usuarioRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email já cadastrado");
        }
        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .perfil(perfil)
                .build();
        return usuarioRepository.save(usuario);
    }
}