from django.shortcuts import render
from django.http import JsonResponse

from .models import Appointment

# Create your views here.

def getTest(request):
	result = Appointment.objects.all()
	response = list()

	for a in result:
		toAppend = {
			'name' : a.name,
			'date' : a.date,
			'last_changed' : a.last_changed,
		}
		response.append(toAppend)

	return JsonResponse(response, safe=False)