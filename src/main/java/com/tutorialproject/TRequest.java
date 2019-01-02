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
public class TRequest implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	Long id;
	@OneToOne
	TStudent requester;
	@OneToOne
	TLocation location;
	@OneToOne 
	TTeacher requestedAuthorizer;
	@Column
	Timestamp timeRequestMade;
	@Column
	Timestamp requestTime;
	@Column
	String description;
}