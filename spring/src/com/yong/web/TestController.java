package com.yong.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/index.html")
	public String test(HttpServletRequest request ,HttpServletResponse response)throws Exception{
		
		/*ModelAndView view = new ModelAndView("test/test");
		view.addObject("test", "this is test page use jstl out");
		request.setAttribute("acc", "request acc");
		System.out.println("access controller success...");
		return view;*/
		request.setAttribute("acc", "request acc");
		System.out.println("access controller success...");
		return "/test/index";
	}
	
	@RequestMapping("/testRedirect.html")
	public String testRedirect(HttpServletRequest request ,HttpServletResponse response){
		System.out.println(request.getAttribute("acc"));
		System.out.println("access redirect  controller success...");
		return "test/red";
	}
	
	
	public String pathMatchMth(HttpServletRequest request ,HttpServletResponse response){
		System.out.println("match mth is accessed...");
		return "match";
	}
	
	@RequestMapping("/modelTest.html")
	public ModelAndView modelTest(HttpServletRequest request ,HttpServletResponse response){
		ModelAndView view = new ModelAndView("test/test");
		view.addObject("test", "this is jstl model");
		return view;
	}
}
