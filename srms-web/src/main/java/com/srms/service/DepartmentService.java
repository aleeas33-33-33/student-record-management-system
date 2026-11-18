package com.srms.service;

import com.srms.dao.DepartmentDao;
import com.srms.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Transactional(readOnly = true)
    public List<Department> findAll() { return departmentDao.findAll(); }

    @Transactional(readOnly = true)
    public Department findById(int id) { return departmentDao.findById(id); }

    public void save(Department d) {
        Department existing = departmentDao.findByCode(d.getCode());
        if (existing != null && !existing.getId().equals(d.getId())) {
            throw new BusinessException("code", "Department code '" + d.getCode() + "' already exists");
        }
        if (d.getId() == null) departmentDao.save(d); else departmentDao.update(d);
    }

    public void delete(int id) {
        Department d = departmentDao.findById(id);
        if (d == null) throw new BusinessException("Department not found");
        long s = departmentDao.countStudents(id), c = departmentDao.countCourses(id);
        if (s > 0 || c > 0) {
            throw new BusinessException("Cannot delete " + d.getCode() + ": it still has "
                    + s + " student(s) and " + c + " course(s) (foreign key)");
        }
        departmentDao.delete(d);
    }
}
