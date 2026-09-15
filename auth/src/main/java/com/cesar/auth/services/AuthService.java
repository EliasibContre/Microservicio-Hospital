package com.cesar.auth.services;
import com.cesar.auth.dto.LoginRequest;
import com.cesar.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
