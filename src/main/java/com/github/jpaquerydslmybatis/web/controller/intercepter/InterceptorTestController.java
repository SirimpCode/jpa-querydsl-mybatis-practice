package com.github.jpaquerydslmybatis.web.controller.intercepter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/interceptor/")
public class InterceptorTestController {

	@GetMapping(value="anyone/anyone_a")
	public String anyone_a() {
	
		return "mycontent1/interceptor_test/anyone/anyone_a";
	}
	
	@GetMapping(value="anyone/anyone_b")
	public String anyone_b() {
	
		return "mycontent1/interceptor_test/anyone/anyone_b";
	}
	
	@GetMapping(value="member_only/member_a")
	public String member_a() {
	
		return "mycontent1/interceptor_test/member/member_a";
	}
	
	@GetMapping(value="member_only/member_b")
	public String member_b() {
	
		return "mycontent1/interceptor_test/member/member_b";
	}
	
	@GetMapping(value="special_member/special_member_a")
	public String special_member_a() {
	
		return "mycontent1/interceptor_test/special_member/special_member_a";
	}
	
	@GetMapping(value="special_member/special_member_b")
	public String special_member_b() {
	
		return "mycontent1/interceptor_test/special_member/special_member_b";
	}	
	
}
