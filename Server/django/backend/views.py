from django.shortcuts import render
from django.http import JsonResponse, HttpResponse

from oauth2_provider.ext.rest_framework import IsAuthenticatedOrTokenHasScope

from rest_framework import permissions, viewsets

from .models import Appointment, Actor
from .serializers import AppointmentSerializer, ActorSerializer

# Create your views here.

def getTest(request):
	result = Appointment.objects.all()
	response = list()

	for a in result:
		toAppend = {
			'name' : a.name,
			'date_begin' : a.date_begin,
			'date_end': a.date_end,
			#'participants': a.participants.all(),
			'town': a.town,
			'location': a.location,
			'description': a.description,
			'notes': a.notes,
			'last_changed' : a.last_changed,
		}
		response.append(toAppend)

	return JsonResponse(response, safe=False)

class AppointmentViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoModelPermissions]
	required_scopes = ['appointment']
	queryset = Appointment.objects.all()
	serializer_class = AppointmentSerializer

	def get_queryset(self):
		user = self.request.user
		return Appointment.objects.filter(participants__id__exact = user.id )

class ActorViewSet(viewsets.ModelViewSet):
	#permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoModelPermissions]
	#required_scopes = ['actor']
	permission_classes = [permissions.AllowAny]
	queryset = Actor.objects.all()
	serializer_class = ActorSerializer
