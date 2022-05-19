package com.example.springbootessentials.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Anime {

    private Long id;
    private String name;

}
