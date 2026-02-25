package com.school.student.service.impl;

import com.school.student.dto.StudentRequestDTO;
import com.school.student.dto.StudentResponseDTO;
import com.school.student.event.StudentCreatedEventPublisher;
import com.school.student.exception.StudentNotFoundException;
import com.school.student.entity.Student;
import com.school.student.repository.StudentRepository;
import com.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentCreatedEventPublisher eventPublisher;

    // Convert RequestDTO -> Entity
    private Student toEntity(StudentRequestDTO dto) {
        return new Student(
                null,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getEnrollmentDate()
        );
    }

    // Convert Entity -> ResponseDTO
    private StudentResponseDTO toResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhone(),
                student.getEnrollmentDate()
        );
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        Student student = toEntity(studentRequestDTO);
        Student savedStudent = studentRepository.save(student);

        // Publish event to Fee Service
        eventPublisher.publishStudentCreated(savedStudent);

        return toResponseDTO(savedStudent);
    }

    @Override
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
        return toResponseDTO(student);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));

        existingStudent.setFirstName(studentRequestDTO.getFirstName());
        existingStudent.setLastName(studentRequestDTO.getLastName());
        existingStudent.setEmail(studentRequestDTO.getEmail());
        existingStudent.setPhone(studentRequestDTO.getPhone());
        existingStudent.setEnrollmentDate(studentRequestDTO.getEnrollmentDate());

        Student updatedStudent = studentRepository.save(existingStudent);
        return toResponseDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
        studentRepository.delete(existingStudent);
    }
}