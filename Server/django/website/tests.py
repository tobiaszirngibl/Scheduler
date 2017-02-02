from django.contrib.auth.models import User
from django.test import TestCase
from django.urls import reverse
# Create your tests here.

class User_Authentifications_Test(TestCase):

	def setUp(self):
		user = User.objects.create_user('root', 'temporary@gmail.com', 'rootTermin')

	def test_login(self):
		response = self.client.get(reverse('login'))
		self.assertTemplateUsed(response, 'website/auth/login.html')

	def test_login_redirects_after_login(self):
		response = self.client.post(reverse('login'), data={'username': 'root', 'password': 'rootTermin'})
		self.assertRedirects(response, reverse('login-redirect'), target_status_code=302)

	def test_login_redirect_redirects(self):
		self.client.login(username='root', password='rootTermin')
		response = self.client.get(reverse('login-redirect'))
		self.assertRedirects(response, '/profile/1/')

	def test_profile_shows_correct_information(self):
		response = self.client.get('/profile/1/')
		self.assertTemplateUsed(response, 'website/profile.html')
		self.fail('Finish the test!')
