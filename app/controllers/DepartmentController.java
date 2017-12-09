package controllers;

import models.Department;
import models.Employee;
import org.apache.commons.lang.StringUtils;
import play.db.jpa.Model;
import play.mvc.Controller;
import play.Logger;
import play.mvc.With;
import utils.ExcelUtils;
import utils.JSONUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix
 * Date :  16/4/4.
 * Desc :
 */
@With(Secure.class)
public class DepartmentController extends Controller {

    public static void exportExcel() {
        Map<Long, Department> cityMap = new HashMap<Long, Department>();
        Map<Long, List<Employee>> cityCompanyMap = new HashMap<Long, List<Employee>>();
        List<List<Employee>> cityCompanyList = new ArrayList<List<Employee>>();

        List<Department> cities = Department.findAll();
        for (Department city : cities) {
            List<Employee> companies = Employee.find("department_id = ? ORDER BY votes DESC", city.id).fetch();
            cityCompanyList.add(companies);
        }

        ExcelUtils.export(cityCompanyList);

        redirect("/department/0");
    }

    public static void department(long departmentId) {

        List<Department> departments = Department.findAll();
        Department currentDepart = Department.findById(departmentId);
        if (null == currentDepart) {
            currentDepart = departments.get(0);
        }
        render(departments, currentDepart);
    }

    public static void departmentDelete(long selectDepartmentId) {
        Map<String, Object> responseData = new HashMap<String, Object>();
        String message = "OK";
        try {
            Department selectDepart = Department.findById(selectDepartmentId);
            if(null != selectDepart)
            {
                selectDepart.delete();
            }
//            int deleteOp = Employee.delete("department_id = ?",selectDepartmentId);
//            System.out.println("删除部门"+deleteOp);
        } catch (Exception e) {
            response.status = 500;
            message = "ERROR";
            Logger.error(e.getMessage(), e);
        }
        responseData.put("message", message);
        renderJSON(responseData);

    }

    public static void departmentAdd(String departmentName) {
        Map<String, Object> responseData = new HashMap<String, Object>();
        String message = "OK";
        try {

            String temp = params.get("temp");
            System.out.println(temp);

            if (StringUtils.isBlank(departmentName)) {
                throw new IllegalArgumentException("必填");
            }

            Department department = new Department();
            department.name = departmentName;
            department.save();
            responseData.put("departmentId", department.id);
        }catch (IllegalArgumentException e){
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

    public static void departmentChart(long departmentId) {
        List<Department> departments = Department.findAll();
        Department currentDepart = Department.findById(departmentId);
        if (null == currentDepart) {
            currentDepart = departments.get(0);
        }
        render(departments, currentDepart);
    }



    /**
     * Method
     */
    public static void top10Employee(long departmentId) {
        Map<String, Object> data = new HashMap<String, Object>();

        List<Employee> employeeList = Employee.find("department_id = ? AND status = ? ORDER BY votes DESC LIMIT 10", departmentId, 1).fetch();
        List<String> employees = new ArrayList<String>();
        List<Integer> votes = new ArrayList<Integer>();

        for (Employee employee: employeeList) {
            employees.add(employee.name);
            votes.add(employee.votes);
        }

        data.put("employees", employees);
        data.put("votes", votes);
        data.put("msg", "OK");
        renderJSON(JSONUtils.toJson(data));
    }

    /**
     * Method
     */
    public static void top10EmployeePieChart(long departmentId) {
        Map<String, Object> data = new HashMap<String, Object>();

        final int total = Integer.parseInt(Model.em().createNativeQuery("SELECT SUM(votes) FROM `employee` WHERE department_id = "+departmentId+" AND status=1;").getResultList().get(0).toString());

        List<Employee> employeeList = Employee.find("department_id = ? AND status = ? ORDER BY votes DESC LIMIT 10", departmentId, 1).fetch();
        List<String> employees = new ArrayList<String>();
        List<Map<String, Object>> votes = new ArrayList<Map<String, Object>>();

        int top10Total = 0;
        for (final Employee employee: employeeList) {
            employees.add(employee.name);
            Map<String, Object> vote = new HashMap<String, Object>(){{
                put("name", employee.name);
                put("value", employee.votes);
            }};
            top10Total += employee.votes;
            votes.add(vote);
        }
        if (employees.size() > 10) {
            employees.add("其他员工");

            final int otherVote = total - top10Total;
            votes.add(new HashMap<String, Object>() {{
                put("name", "其他员工");
                put("value", otherVote);
            }});
        }

        data.put("employees", employees);
        data.put("votes", votes);
        data.put("msg", "OK");
        renderJSON(JSONUtils.toJson(data));
    }

}
