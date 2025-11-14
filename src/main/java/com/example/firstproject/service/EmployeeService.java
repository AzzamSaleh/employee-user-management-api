package com.example.firstproject.service;

import com.example.firstproject.dto.EmployeeDTO;
import com.example.firstproject.entity.Employee;
import com.example.firstproject.exception.EmployeeNotFoundException;
import com.example.firstproject.mapper.EmployeeMapper;
import com.example.firstproject.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDTO> findAll(){
        return employeeRepository.findAll()
                .stream().map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO findById(int id){
        Optional<Employee> result = employeeRepository.findById(id);
        return result.map(EmployeeMapper::toDTO)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID - " + id));
    }

    public EmployeeDTO save(Employee e){
        Employee saved = employeeRepository.save(e);
        return EmployeeMapper.toDTO(saved);
    }

    public void deleteById(int id){
        if(!employeeRepository.existsById(id))
            throw new EmployeeNotFoundException("Employee not found with ID - " + id);
        employeeRepository.deleteById(id);
    }
}
