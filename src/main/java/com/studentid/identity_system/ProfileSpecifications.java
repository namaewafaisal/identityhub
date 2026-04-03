package com.studentid.identity_system;

import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecifications {

    public static Specification<StudentProfile> hasDepartment(String dept) {
        return (root, query, cb) ->
                cb.equal(root.get("department"), dept);
    }

    public static Specification<StudentProfile> hasYear(Integer year) {
        return (root, query, cb) ->
                cb.equal(root.get("year"), year);
    }
    public static Specification<StudentProfile> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(
                    cb.lower(root.get("fullName")),
                    "%" + name.toLowerCase() + "%"
                );
    }
}