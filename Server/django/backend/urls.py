"""Backend URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf import settings
from django.conf.urls import include, url
from django.conf.urls.static import static

from rest_framework.routers import DefaultRouter

from backend import views

# Router for rest_framework API

router = DefaultRouter()
router.register(r'appointment', views.AppointmentViewSet, 'appointment')
router.register(r'actor', views.ActorViewSet, 'actor')
router.register(r'group', views.GroupViewSet, 'group')

urlpatterns = [
	url(r'^', include(router.urls)),
	url(r'appointment/(\d+)/addActors', views.add_actor, name='add_actor'),
	url(r'appointment/(\d+)/response', views.appointment_response ,name='appointment_response'),
	url(r'group/(\d+)/addActors', views.add_actor_to_group, name='add_group_actor')
]
