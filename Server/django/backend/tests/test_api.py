"""import json

from django.test import TestCase

from backend.models import Actor, Appointment


class AppointmentAPITest(TestCase):
	base_url = '/api/appointment/{}/'

	def setUp(self):
		Actor.objects.create_user('a@b.com', 'pw1')
		self.client.login(email='a@b.com', password='pw1')

	def test_get_returns_JSON(self):
		app = Appointment.objects.create()
		response = self.client.get(self.base_url.format(app.id))
		self.assertEqual(response.status_code, 200)
		self.assertEqual(response['content-type'], 'application/json')

	def test_get_returns_correct_Appointment(self):
		app = Appointment.objects.create(name='first')
		Appointment.objects.create(name='second')

		response = self.client.get(self.base_url.format(app.id))

		self.assertIn(
			'"name":"first"',
			response.content.decode('utf8')
		)
"""