package com.example.firstproject.controller;

import com.example.firstproject.dto.EmployeeDTO;
import com.example.firstproject.entity.Employee;
import com.example.firstproject.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> findAll(){ return employeeService.findAll(); }

    @GetMapping("/employees/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable int employeeId){ return employeeService.findById(employeeId); }

    @PostMapping("/employees")
    public EmployeeDTO addEmployee(@Valid @RequestBody Employee theEmployee){
        theEmployee.setId(0); // force create
        return employeeService.save(theEmployee);
    }

    @PutMapping("/employees")
    public EmployeeDTO updateEmployee(@Valid @RequestBody Employee theEmployee){
        return employeeService.save(theEmployee);
    }

    @DeleteMapping("/employees/{employeeId}")
    public String delete(@PathVariable int employeeId){
        employeeService.deleteById(employeeId);
        return "Deleted employee id - " + employeeId;
    }
}
