from django.db import models
from django.contrib.auth.models import User

# Create your models here.


class Actor(models.Model):
	user = models.OneToOneField(User, on_delete=models.CASCADE)
	bio = models.TextField(max_length=500, blank=True, null=True)

	def __str__(self):
		return self.user.username

class Appointment(models.Model):
	name = models.CharField(max_length=150)
	date = models.DateTimeField(auto_now=False, auto_now_add=False)
	last_changed = models.DateTimeField(auto_now=False, auto_now_add=False)
	participants = models.ManyToManyField(
		Actor,
		through="Participation"	
	)

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