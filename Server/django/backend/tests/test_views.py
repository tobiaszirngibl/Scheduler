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

	def test_no_participation_creates_new_and_returns_204(self):
		Participation.objects.first().delete()
		response = self.client.post(self.base_url, {'answer': 'yes'}, )
		self.assertEqual(response.status_code, 204)
		self.assertEqual(Participation.objects.count(), 1)

	def test_post_updates_participation(self):
		self.client.post(self.base_url, {'answer': 'yes'},)
		part = Participation.objects.first()
		self.assertEquals(part.answer, 'y')

	def test_answer_is_no_after_necessary_person_declines(self):
		part = Participation.objects.first()
		part.is_necessary = True
		part.save()

		response = self.client.post(self.base_url, {'answer': 'no'})
		app = Appointment.objects.first()
		self.assertEqual(app.will_take_place, False)

	def test_user_is_removed_from_event_after_decline(self):
		user = Actor.objects.first()
		user.understudy = Actor.objects.create_user('a@c.com', 'pw')
		user.save()
		response = self.client.post(self.base_url, {'answer': 'no'})
		self.assertEqual(Participation.objects.filter(actor=Actor.objects.get(email='a@b.com')).count(), 0)

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

class GroupTest(TestCase):
	base_url = '/api/group/1/'

	def setUp(self):
		Group.objects.create(name='test')
		actor = Actor.objects.create_user('a@b.com', 'pw1')

		self.client.login(email='a@b.com', password='pw1')

	def test_post_creates_membership(self):
		Actor.objects.create_user('a@c.com', 'pw1')
		Actor.objects.create_user('a@d.com', 'pw1')
		self.client.post(self.base_url+'addActors', {'actors': ['a@c.com', 'a@d.com']})
		group = Group.objects.first()
		self.assertEquals(group.members.all().count(), 2)

	def test_remove_from_group(self):
		group = Group.objects.first()
		actor = Actor.objects.first()

		self.client.post(self.base_url+'addActors', {'actors': ['a@b.com']})

		self.client.get(self.base_url+'leave')
		self.assertEqual(group.members.all().count(), 0)