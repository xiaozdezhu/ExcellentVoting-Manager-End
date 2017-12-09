package controllers;

import models.Department;
import models.Employee;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.tools.shell.InteractiveShellRunner;
import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;
import utils.JSONUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Felix
 * Date :  16/4/4.
 * Desc :
 */
@With(Secure.class)
public class EmployeeController extends Controller {


    /**
     * PAGE
     */
    public static void employeeAdd(long departmentId) {
        List<Department> departments = Department.findAll();
        Department currentDepart = Department.findById(departmentId);
        if (null == currentDepart) {
            currentDepart = departments.get(0);
        }
        render(departments, currentDepart);
    }

    /**
     * PAGE
     */
    public static void employeeEdit(long employeeId) {
        Employee employee = Employee.findById(employeeId);
        List<Department> departments = Department.findAll();
        Department currentDepart = employee.department;
        render(employee, departments, currentDepart);
    }


    /**
     * Method
     */
    public static void employeeDelete(long employeeId) {
        Employee employee = Employee.findById(employeeId);
        if (null != employee) {
            employee.delete();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("msg", "OK");
        renderJSON(data);
    }

    /**
     * Method
     */
    public static void employeeQuery(int ps, int pn) {
        String keyword = null;
        long departmentId = 0;

        int gender = -1;
        int status = -1;
        int sortOrder = -1;
        try {
            String body =  IOUtils.toString(request.body);
            if (!StringUtils.isBlank(body)) {
                Logger.info("PostBody: " + body);
                JsonNode postBody = JSONUtils.toJsonNode(body);
                departmentId = postBody.get("departmentId").asLong();
                if (postBody.has("keyword")) {
                    keyword = postBody.get("keyword").asText();
                }
                if (postBody.has("gender")) {
                    gender = postBody.get("gender").asInt(-1);
                }
                if (postBody.has("status")) {
                    status = postBody.get("status").asInt(-1);
                }
                if (postBody.has("sortOrder")) {
                    sortOrder = postBody.get("sortOrder").asInt(-1);
                }
            }
        } catch (IOException e) {
            Logger.error(e, "搜索失败[postBody解析失败]");
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        if (ps <= 0 || pn <=0) {
            Logger.warn("页大小和页码不能小于零");
            renderJSON(data);
        }
        if (0 == departmentId) {
            throw new IllegalArgumentException("DepartmentID");
        }

        final int start = ps * (pn -1);
        long total = 0L;
        List<Employee> employeeList = null;
        StringBuilder queryBuilder = new StringBuilder("1=1 AND department_id = " + departmentId);
        if (-1 != gender) {
            queryBuilder.append(" AND gender = " + gender);
        }
        if (-1 != status) {
            queryBuilder.append(" AND status = "+status);
        }
        if (!StringUtils.isBlank(keyword)) {
            queryBuilder.append(" AND (name LIKE '%"+keyword+"%')");
        }
        if (0 == sortOrder) {
            queryBuilder.append(" ORDER BY votes DESC");
        } else if (1 == sortOrder) {
            queryBuilder.append(" ORDER BY votes ASC");
        }

        total = Employee.count(queryBuilder.toString());
        employeeList = Employee.find(queryBuilder.toString()).from(start).fetch(ps);

        long maxPN = (total%ps == 0 ? total/ps : total/ps + 1);

        data.put("total", total);
        data.put("ps", ps);
        data.put("pn", pn);
        data.put("maxPN", maxPN);
        data.put("employeeList", employeeList);
//        data.put("imageURLSuffix", imageURLSuffix);
        renderJSON(data);
    }

    /**
     * Method
     */
    public static void employeeSave() {
        Map<String, Object> responseData = new HashMap<String, Object>();
        String message = "OK";
        try {
            Long id = params.get("employee_id", Long.class);
            Long departmentId = params.get("department_id", Long.class);
            String name = params.get("employee_name");
            File avatar = params.get("employee_avatar", File.class);
            Integer gender = params.get("employee_gender", Integer.class);
            String position = params.get("employee_position");
            String description = params.get("employee_description");
            Integer votes = params.get("employee_vote", Integer.class);
            Integer status = params.get("employee_status", Integer.class);


            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("必填");
            }
            if (null == gender){
                gender = 1;
            }
            if (StringUtils.isBlank(position)) {
                throw new IllegalArgumentException("必填");
            }
            if (StringUtils.isBlank(description)) {
                throw new IllegalArgumentException("必填");
            }
            if (null == votes) {
                votes = 0;
            }
            if (null == departmentId || 0 == departmentId) {
                throw new IllegalArgumentException("必填");
            }

            Department department = Department.findById(departmentId);
            if (null == department) {
                throw new IllegalArgumentException("不可用");
            }
            String fileName = "";
            if (null != avatar) {
//                fileName = "toupiao.png";
                fileName = "employee-"+System.currentTimeMillis();
                FileInputStream fileInputStream = new FileInputStream(avatar);
//                FileReader fileReader = new FileReader(avatar);
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
                FileOutputStream fileOutputStream = new FileOutputStream("public/images/employee/"+fileName);
                IOUtils.copy(fileInputStream,fileOutputStream);
            }


            Employee employee = null;
            if (null == id || 0 == id) {
                // 新店员 头像必传
                if (StringUtils.isBlank(fileName)) {
                    throw new IllegalArgumentException("头像必传");
                }
                employee = new Employee();
                employee.department = department;
            } else {
                employee = Employee.findById(id);
            }
            employee.name = name;
            employee.gender = gender;
            if (!StringUtils.isBlank(fileName)) {
                employee.avatar = fileName;
            }
            employee.position = position;
            employee.description = description;
            employee.status = status;
            employee.votes = votes;

            employee.save();
        } catch (IllegalArgumentException e){
            response.status = 400;
            message = e.getMessage();
            Logger.warn(message);
        } catch (Exception e) {
            response.status = 500;
            message = "ERROR";
            Logger.error(e.getMessage(), e);
        }

        responseData.put("message", message);
        renderJSON(responseData);
    }


}
