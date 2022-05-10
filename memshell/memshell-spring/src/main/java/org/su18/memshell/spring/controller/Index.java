package org.su18.memshell.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(value = "/")
public class Index {

    @GetMapping()
    public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().println("spring start/");
    }
}