from django.contrib.staticfiles.testing import StaticLiveServerTestCase
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.firefox.firefox_binary import FirefoxBinary

import unittest

class NewVisitorTest(StaticLiveServerTestCase):

	def setUp(self):
		"""self.browser = webdriver.Firefox(firefox_binary=FirefoxBinary(
			firefox_path='/home/matt/Programme/LTS-Firefox/firefox'
		))
		self.browser.implicitly_wait(3)"""
		self.browser = webdriver.Firefox(executable_path="../webdrivers/geckodriver")
		self.browser.implicitly_wait(3)

	def tearDown(self):
		self.browser.quit()

	def test_can_log_in(self):
		pass

