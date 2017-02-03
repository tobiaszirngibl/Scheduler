from django.db import models
from django.contrib.auth.models import User

# Create your models here.


class Actor(models.Model):
	user = models.OneToOneField(User, on_delete=models.CASCADE)
	bio = models.TextField(max_length=500, blank=True, null=True)

	def __str__(self):
		return self.user.username


class Appointment(models.Model):
	# Name of the Appointment
	name = models.CharField(max_length=150)
	# Marks the start of the Appointment
	date_begin = models.DateTimeField()
	# Marks the end of the Appointment
	date_end = models.DateTimeField()
	# All participants
	participants = models.ManyToManyField(
		Actor,
		through="Participation"	
	)
	# The town in which the Appointment will take place
	# May require own model
	town = model.CharField(max_length=100, blank=True)
	# Building the Appointment will take place in
	# May require own model
	location = model.CharField(max_length=100, blank=True)
	# Description
	description = model.TextField(blank=True)
	# Important notes for the participants
	notes = model.TextField(blank=True)
	# Timestamp used for marking last modification date
	last_changed = models.DateTimeField(auto_now=True, auto_now_add=True)
	
	def __str__(self):
		return self.name

class Participation(models.Model):
	answer_choices=(
		('y', 'Yes'),
		('n', 'No'),
		('p', 'Pending')
	)

	actor = models.ForeignKey(Actor, on_delete=models.CASCADE)
	appointment = models.ForeignKey(Appointment, on_delete=models.CASCADE)
	answer = models.CharField(choices=answer_choices, max_length=1)
	is_necessary = models.BooleanField()

	def __str__(self):
		return self.appointment.__str__() + ' - ' + self.actor.__str__()