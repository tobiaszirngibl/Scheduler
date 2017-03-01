from django.shortcuts import render
from django.http import JsonResponse, HttpResponse

from rest_framework import viewsets

from .models import Appointment
from .serializers import AppointmentSerializer

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
	queryset = Appointment.objects.all()
	serializer_class = AppointmentSerializer
