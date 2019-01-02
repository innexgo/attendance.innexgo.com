package com.tutorialproject;

import java.io.Serializable;

import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("serial")
public class TTeacher implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	Long id;
	@Column
	String name;
}
