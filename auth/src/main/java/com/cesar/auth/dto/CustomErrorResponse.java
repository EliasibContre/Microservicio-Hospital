package com.cesar.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
