package com.github.jpaquerydslmybatis.web.controller.employees;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import com.github.jpaquerydslmybatis.service.employees.EmpService;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponseWrap;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/emp")
@RequiredArgsConstructor
public class EmpViewController {
    private final EmpService empService;

    @GetMapping("/list")
    public String empListEmpManager(
                          HttpServletRequest request,
                          @RequestParam(required = false) List<Long> departmentIds,
                          @RequestParam(required = false) Gender gender
    ) {
        EmpListResponseWrap employees = empService.getAllEmployees(departmentIds, gender);
        request.setAttribute("empListWrap", employees);

        return "mycontent2/emp/employeeList"; // employees/empList.html
    }
    @GetMapping("/chart")
    public String chartEmpManager(HttpServletRequest request) {
        return "mycontent2/emp/chart";
    }
}
