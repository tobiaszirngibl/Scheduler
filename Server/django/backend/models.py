from django.conf import settings
from django.db import models
from django.contrib.auth.models import AbstractBaseUser, PermissionsMixin
from django.utils.timezone import now

from .actorUserManager import ActorManager
# Create your models here.


class Actor(AbstractBaseUser, PermissionsMixin):
	email = models.EmailField(unique=True)
	first_name = models.CharField(max_length=25, blank=True)
	last_name = models.CharField(max_length=50, blank=True)
	is_staff = models.BooleanField(default=False)
	is_superuser = models.BooleanField(default=False)
	is_active = models.BooleanField(default=True)
	bio = models.TextField(max_length=500, blank=True, null=True)


	USERNAME_FIELD = 'email'
	REQUIRED_FIELDS = []

	objects = ActorManager()

	def get_short_name(self):
		return self.email

	def get_full_name(self):
		return '%s %s' % (self.first_name, self.last_name)

	def __str__(self):
		return self.email

	@property
	def get_name(self):
		return self.email

class Appointment(models.Model):
	# Name of the Appointment
	name = models.CharField(max_length=150)
	# Marks the start of the Appointment
	date_begin = models.DateTimeField(blank=True, default=now)
	# Marks the end of the Appointment
	date_end = models.DateTimeField(blank=True, default=now)
	# All participants
	participants = models.ManyToManyField(
		Actor,
		through="Participation"	
	)
	# The town in which the Appointment will take place
	# May require own model
	town = models.CharField(max_length=100, blank=True)
	# Building the Appointment will take place in
	# May require own model
	location = models.CharField(max_length=100, blank=True)
	# Description
	description = models.TextField(blank=True)
	# Important notes for the participants
	notes = models.TextField(blank=True)
	# Timestamp used for marking last modification date
	last_changed = models.DateTimeField(auto_now=True)

	def __str__(self):
		return self.name

class Participation(models.Model):
	answer_choices=(
		('y', 'Yes'),
		('n', 'No'),
		('p', 'Pending')
	)

	actor = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
	appointment = models.ForeignKey(Appointment, on_delete=models.CASCADE)
	answer = models.CharField(choices=answer_choices, max_length=1, default='p')
	is_necessary = models.BooleanField(default=False)

	def __str__(self):
		return self.appointment.__str__() + ' - ' + self.actor.__str__()