package com.example.spacelab.util;

import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.role.PermissionType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

public class AuthUtil {

    public static Admin getLoggedInAdmin() {
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return admin;
    }

    public static PermissionType getPermission(String permissionName) {
        return getLoggedInAdmin().getRole().getPermission(permissionName);
    }

    public static void checkAccessToCourse(Long courseID, String permissionName) {
        Admin admin = getLoggedInAdmin();
        PermissionType permissionToCheck = getPermission(permissionName);
        if(permissionToCheck == PermissionType.FULL) return;
        else if(permissionToCheck == PermissionType.PARTIAL) {
            if(!admin.getCourses().stream().map(Course::getId).toList().contains(courseID))
                throw new AccessDeniedException("No access to creating new students for this course! (courseID: "+courseID+") !");
        }
        else throw new AccessDeniedException("No access to creating new students for this course! (courseID: "+courseID+") !");
    }

//    public static List<?> filterListByAllowedCourse(List<?> list, Long courseID, String permissionName) {
//        Admin admin = getLoggedInAdmin();
//        PermissionType permissionToCheck = getPermission(permissionName);
//        if(permissionToCheck == PermissionType.FULL) return list;
//        else if(permissionToCheck == PermissionType.PARTIAL) return list.stream().filter(item -> )
//        else throw new AccessDeniedException("No access to creating new students for this course! (courseID: "+courseID+") !");
//    }
}