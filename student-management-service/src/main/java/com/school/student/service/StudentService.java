package com.school.student.service;

import com.school.student.dto.StudentRequestDTO;
import com.school.student.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO);

    StudentResponseDTO getStudentById(Long id);

    List<StudentResponseDTO> getAllStudents();

    StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO);

    void deleteStudent(Long id);
}