from datetime import datetime, timedelta

from django.contrib.auth.models import User
from django.test import TestCase
from django.utils import timezone

# Create your tests here.

from backend.models import Appointment, Actor, Participation

class AppointmentModelTest(TestCase):

	def test_saving_and_retieving_appointment(self):
		part1 = Actor()
		part1.user = User.objects.create_user('u1', 'u1@u1.com', 'pw1')
		part1.save()

		part2 = Actor()
		part2.user = User.objects.create_user('u2', 'u1@u1.com', 'pw1')
		part2.save()

		part3 = Actor()
		part3.user = User.objects.create_user('u3', 'u1@u1.com', 'pw1')
		part3.save()

		app1 = Appointment(
			name = "App1",
			date_begin = timezone.now(),
			date_end = timezone.now() + timedelta(hours=2),
		)
		app1.save()
		Participation.objects.create(actor=part1, appointment=app1, is_necessary=False)

		app2 = Appointment(
			name = "App2",
			date_begin = timezone.now(),
			date_end = timezone.now() + timedelta(hours=5),
		)
		app2.save()
		Participation.objects.create(actor=part2, appointment=app2, is_necessary=False)
		Participation.objects.create(actor=part3, appointment=app2, is_necessary=False)

		saved_app = Appointment.objects.first()
		self.assertEqual(saved_app, app1)
		self.assertEquals(saved_app.participants.all().count(), 1)
		self.assertEquals(saved_app.participants.first().user.username, 'u1')

		saved_app = Appointment.objects.get(pk=2)
		self.assertEqual(saved_app, app2)
		self.assertEquals(saved_app.participants.all().count(), 2)
		self.assertEquals(saved_app.participants.first().user.username, 'u2')



class UserModelTest(TestCase):

	def test_creating_and_retrieving_users(self):
		part1 = Actor()
		part1.user = User.objects.create_user('u1', 'u1@u1.com', 'pw1')
		part1.bio = "Es war einmal ein grosses Schloss und Kunibert, so hiess der Boss"
		part1.save()

		part2 = Actor()
		part2.user = User.objects.create_user('u2', 'u1@u1.com', 'pw1')
		part2.save()

		saved = Actor.objects.first()
		self.assertEqual(saved, part1)
		self.assertIn('Kunibert', saved.bio)

		saved2 = Actor.objects.get(pk=2)
		self.assertEqual(saved2, part2)