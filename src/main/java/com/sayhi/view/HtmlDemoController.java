package com.sayhi.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlDemoController {

  @GetMapping("/")
  String getHomePage() {
    return "index";
  }

}
