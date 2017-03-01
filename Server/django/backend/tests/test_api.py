import json

from django.test import TestCase

from backend.models import Appointment


class AppointmentAPITest(TestCase):
	base_url = '/api/appointment/{}/'

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
