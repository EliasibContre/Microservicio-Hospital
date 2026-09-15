package com.cesar.auth.services;

import java.util.Set;

import com.cesar.auth.dto.UsuarioRequest;
import com.cesar.auth.dto.UsuarioResponse;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}