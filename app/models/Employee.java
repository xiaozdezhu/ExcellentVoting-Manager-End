package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Created by Felix
 * Date :  16/4/4.
 * Desc :
 */
@Entity(name = "employee")
public class Employee extends Model {

    @Column(name = "department_id", insertable = false, updatable = false)
    public long departmentId;
    public String name;
    public int gender;
    public String avatar;
    public String position;
    public String description;
    public int votes;
    public int status;

    @ManyToOne
    public Department department;

}
