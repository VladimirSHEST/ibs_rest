package com.ibs;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@NoArgsConstructor // генерирует конструктор без аргументов
@AllArgsConstructor // генерирует конструктор со всеми аргументами
public class ProductData {
    private String name;
    private String type;
    private boolean exotic;


    public String getName() {
        return name;
    }

    public boolean isExotic() {
        return exotic;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExotic(boolean exotic) {
        this.exotic = exotic;
    }
}
