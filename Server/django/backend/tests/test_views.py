from django.test import TestCase

from backend.models import Actor, Appointment, Group, Participation


class AppointmentResponseTest(TestCase):
	base_url = '/api/appointment/1/response'

	def setUp(self):
		app = Appointment.objects.create(name='test')
		actor = Actor.objects.create_user('a@b.com', 'pw1')
		Participation.objects.create(actor=actor, appointment=app)

		self.client.login(email='a@b.com', password='pw1')

	def test_accepts_only_post(self):
		response = self.client.get(self.base_url)
		self.assertEqual(response.status_code, 405)

	def test_no_post_data_raises_400(self):
		response = self.client.post(self.base_url, {})
		self.assertEqual(response.status_code, 400)

	def test_wrong_post_returns_400(self):
		# Tests for wrong key
		response = self.client.post(self.base_url, {'wrong': 'wrong'},)
		self.assertEqual(response.status_code, 400)

		# Tests for wrong value
		response = self.client.post(self.base_url, {'answer': 'wrong'}, )
		self.assertEqual(response.status_code, 400)

	def test_no_participation_returns_500(self):
		Participation.objects.first().delete()
		response = self.client.post(self.base_url, {'answer': 'y'}, )
		self.assertEqual(response.status_code, 500)

	def test_post_updates_participation(self):
		self.client.post(self.base_url, {'answer': 'yes'},)
		part = Participation.objects.first()
		self.assertEquals(part.answer, 'y')

class AddActorToAppointmentTest(TestCase):
	base_url = '/api/appointment/1/addActors'

	def setUp(self):
		app = Appointment.objects.create(name='test')
		actor = Actor.objects.create_user('a@b.com', 'pw1')

		self.client.login(email='a@b.com', password='pw1')

	def test_post_creates_participation(self):
		Actor.objects.create_user('a@c.com', 'pw1')
		Actor.objects.create_user('a@d.com', 'pw1')
		self.client.post(self.base_url, {'actors': ['a@c.com', 'a@d.com']})
		self.assertEquals(Participation.objects.all().count(), 2)

class AddActorToGroupTest(TestCase):
	base_url = '/api/group/1/addActors'

	def setUp(self):
		Group.objects.create(name='test')
		actor = Actor.objects.create_user('a@b.com', 'pw1')

		self.client.login(email='a@b.com', password='pw1')

	def test_post_creates_membership(self):
		Actor.objects.create_user('a@c.com', 'pw1')
		Actor.objects.create_user('a@d.com', 'pw1')
		self.client.post(self.base_url, {'actors': ['a@c.com', 'a@d.com']})
		group = Group.objects.first()
		self.assertEquals(group.members.all().count(), 2)