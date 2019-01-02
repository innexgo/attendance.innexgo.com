package com.tutorialproject;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@SuppressWarnings("serial")
public class TEvent implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	Long id;
	@OneToOne
	TStudent student;
	@OneToOne
	TLocation location;
	@Column
	Timestamp time;
	@Column
	String type;
}