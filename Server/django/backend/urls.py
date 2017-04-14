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

get_by_status_view = views.AppointmentViewSet.as_view({'get': 'get_appointment_by_status'},)

router = DefaultRouter()
router.register(r'appointment', views.AppointmentViewSet, 'appointment')
router.register(r'actor', views.ActorViewSet, 'actor')
router.register(r'group', views.GroupViewSet, 'group')
router.register(r'favorites', views.FavoriteViewSet, 'favorite')



urlpatterns = [
	url(r'^', include(router.urls)),
	url(r'appointment/(yes|no|pending)', get_by_status_view, name='get_by_status'),
	url(r'appointment/(\d+)/addActors', views.AddActorToEvent.as_view(), name='add_actor'),
	url(r'appointment/(\d+)/addCriticalActors', views.AddCriticalActorToEvent.as_view(), name='add_critical_actor'),
	url(r'appointment/(\d+)/response', views.AppointmentResponse.as_view() ,name='appointment_response'),
	url(r'group/(\d+)/addActors', views.AddActorToGroup.as_view(), name='add_group_actor'),
	url(r'group/(\d+)/leave', views.LeaveGroup.as_view(), name='leave_group')
]
