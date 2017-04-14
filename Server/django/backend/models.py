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
	understudy = models.ForeignKey(settings.AUTH_USER_MODEL, blank=True, null=True)

	USERNAME_FIELD = 'email'
	REQUIRED_FIELDS = []

	objects = ActorManager()

	def get_short_name(self):
		return self.email

	def get_bio(self):
		return self.bio

	def get_full_name(self):
		return '%s %s' % (self.first_name, self.last_name)

	def __str__(self):
		return self.email

	@property
	def get_name(self):
		return self.email


class Group(models.Model):
	name = models.CharField(max_length=150)
	members = models.ManyToManyField(settings.AUTH_USER_MODEL)

	def __str__(self):
		return self.name


class Appointment(models.Model):
	# Name of the Appointment
	name = models.CharField(max_length=150)
	# Actor who created the appointment
	organizer = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='owner')
	# Marks the start of the Appointment
	dtstart = models.DateTimeField(blank=True, default=now)
	# Marks the end of the Appointment
	dtend = models.DateTimeField(blank=True, default=now)
	# All participants
	participants = models.ManyToManyField(
		settings.AUTH_USER_MODEL,
		through="Participation"	
	)
	# The town in which the Appointment will take place
	# May require own model
	town = models.CharField(max_length=100, blank=True)
	# Building the Appointment will take place in
	# May require own model
	location = models.CharField(max_length=100, blank=True)
	# Description
	summary = models.TextField(blank=True)
	# Timestamp used for marking last modification date
	last_changed = models.DateTimeField(auto_now=True)

	@property
	def will_take_place(self):
		for p in self.participation_set.all():
			if p.answer == 'n':
				return False
		return True

	def __str__(self):
		return self.name



class Participation(models.Model):
	answer_choices = (
		('y', 'Yes'),
		('n', 'No'),
		('p', 'Pending')
	)

	actor = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
	appointment = models.ForeignKey(Appointment, on_delete=models.CASCADE)
	answer = models.CharField(choices=answer_choices, max_length=1, default='p')
	is_necessary = models.BooleanField(default=False)

	class Meta:
		unique_together = ('actor', 'appointment',)

	def __str__(self):
		return self.appointment.__str__() + ' - ' + self.actor.__str__()

class Favorite(Appointment):
	owner = models.ForeignKey(Actor, on_delete=models.CASCADE)
	color = models.CharField(max_length=6)

	def __str__(self):
		return self.owner.__str__() + ' - ' + self.name