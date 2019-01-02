package com.tutorialproject;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.Id;

@Entity
@SuppressWarnings("serial")
public class TSchedule implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	Long id;
	@OneToOne
	TLocation period0;
	@OneToOne
	TLocation period1;
	@OneToOne
	TLocation period2;
	@OneToOne
	TLocation period3;
	@OneToOne
	TLocation period4;
	@OneToOne
	TLocation period5;
	@OneToOne
	TLocation period6;
	@OneToOne
	TLocation period7;
	@OneToOne
	TLocation period8;
	@OneToOne
	TLocation period9;
}
