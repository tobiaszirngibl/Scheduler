from datetime import timedelta

from django.contrib.auth.models import User
from django.test import TestCase
from django.utils import timezone

from backend.models import Actor, Appointment, Participation


# Create your tests here.

class AppointmentModelTest(TestCase):
	def test_saving_and_retieving_appointment(self):
		part1 = Actor.objects.create_user('u1@u1.com', 'pw1')
		part1.save()

		part2 = Actor.objects.create_user('u2@u1.com', 'pw1')
		part2.save()

		part3 = Actor.objects.create_user('u3@u1.com', 'pw1')
		part3.save()

		self.appointment = Appointment(name="App1", dtstart=timezone.now(),
		                               dtend=timezone.now() + timedelta(hours=2), organizer=part1)
		app1 = self.appointment
		app1.save()
		Participation.objects.create(actor=part1, appointment=app1, is_necessary=False)

		app2 = Appointment(
			name="App2",
			dtstart=timezone.now(),
			dtend=timezone.now() + timedelta(hours=5),
			organizer=part1
		)
		app2.save()
		Participation.objects.create(actor=part2, appointment=app2, is_necessary=False)
		Participation.objects.create(actor=part3, appointment=app2, is_necessary=False)

		saved_app = Appointment.objects.first()
		self.assertEqual(saved_app, app1)
		self.assertEquals(saved_app.participants.all().count(), 1)
		self.assertEquals(saved_app.participants.first().email, 'u1@u1.com')

		saved_app = Appointment.objects.get(pk=2)
		self.assertEqual(saved_app, app2)
		self.assertEquals(saved_app.participants.all().count(), 2)
		self.assertEquals(saved_app.participants.first().email, 'u2@u1.com')