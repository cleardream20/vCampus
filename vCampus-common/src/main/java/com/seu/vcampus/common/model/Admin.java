package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> modules; // module - panel
}
