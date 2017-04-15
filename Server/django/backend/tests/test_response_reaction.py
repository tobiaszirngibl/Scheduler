from django.test import TestCase

from backend.models import Actor, Participation, Appointment
from backend.response_reaction import handle_decline

class ResponseReactionTest(TestCase):

	user1 = ''
	user2 = ''
	app = ''
	part = ''

	def setUp(self):
		self.user1 = Actor.objects.create_user('a@b.com', 'pw')
		self.user2 = Actor.objects.create_user('a@c.com', 'pw')
		self.user1.understudy = self.user2
		self.user1.save()
		self.app = Appointment.objects.create(name='test', organizer=self.user1)
		self.part = Participation.objects.create(appointment=self.app, actor=self.user1)

	def test_does_nothing_if_user_necessary(self):
		self.part.is_necessary = True

		response = handle_decline(self.user1, self.part, self.app)
		self.assertEqual(response, False)

	def test_returns_true_if_user_not_necessary(self):
		response = handle_decline(self.user1, self.part, self.app)

	def test_no_understudy_returns_false(self):
		self.user1.understudy = None
		self.user1.save()
		response = handle_decline(self.user1, self.part, self.app)
		self.assertEqual(response, False)

	def test_understudy_is_added_to_appointment(self):
		handle_decline(self.user1, self.part, self.app)

		count = Participation.objects.filter(actor=self.user2).count()
		self.assertEqual(count, 1)
"""
	def test_declining_user_is_removed_from_event(self):
		handle_decline(self.user1, self.part, self.app)
		count = Participation.objects.filter(actor=self.user1).count()
		self.assertEqual(count, 0)
"""