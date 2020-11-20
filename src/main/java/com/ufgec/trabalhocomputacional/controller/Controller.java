package com.ufgec.trabalhocomputacional.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class Controller {

    @RequestMapping(value = "/")
    public ModelAndView inicial() {
        return new ModelAndView("index");
    }
}
