package com.example;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
@AllArgsConstructor
public class Member {
    private Long id;
    private String name;
    private Long money;
}
