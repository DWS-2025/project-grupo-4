package com.casino.grupo4_dws.casinoweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Blob;

public record PrizeDTO(int id,
                       String title,
                       String description,
                       int price,
                       int owner,
                       @JsonIgnore
                       Blob image) {
}