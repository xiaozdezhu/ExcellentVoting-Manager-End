package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by Felix
 * Date :  16/4/4.
 * Desc :
 */
@Entity(name = "department")
public class Department extends Model {

    public String name;


}
