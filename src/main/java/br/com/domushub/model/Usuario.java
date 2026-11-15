package br.com.domushub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Email @NotBlank @Column(unique = true)
    private String email;

    @NotBlank
    private String senha;

    @Enumerated(EnumType.STRING)
    private Perfil perfil = Perfil.MORADOR;

    private LocalDateTime criadoEm = LocalDateTime.now();

    // Exclusão lógica: mantém o usuário e suas movimentações no histórico.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean ativo = true;

    public Usuario() {}
    public Usuario(String nome, String email, String senha, Perfil perfil) {
        this.nome = nome; this.email = email; this.senha = senha; this.perfil = perfil;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
