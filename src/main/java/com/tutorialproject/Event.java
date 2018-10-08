package com.tutorialproject;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="event")
public class Event implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;
	@OneToOne
	Student student;
	@OneToOne
	Location location;
	@Column(name="time")
	Timestamp time;
	@Column(name="in")
	private boolean in;
	
	public boolean isIn() {
		return in;
	}

	public void setIn(boolean in) {
		this.in = in;
	}

	public Event() 
	{
		
	}
	
	public Event(Student student, Location location, Timestamp timestamp)
	{
		this.student = student;
		this.location = location;
		this.time = timestamp;
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public Student getStudent()
	{
		return student;
	}
	
	public void setStudent(Student student)
	{
		this.student = student;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public Timestamp getTime() 
	{
		return this.time;
	}
	
	public void setTime(Timestamp timestamp)
	{
		this.time = timestamp;
	}
}
