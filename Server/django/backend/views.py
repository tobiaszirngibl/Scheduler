from django.contrib.auth.decorators import login_required
from django.http import HttpResponseBadRequest, HttpResponseServerError, HttpResponse
from django.views.decorators.http import require_POST

from oauth2_provider.decorators import protected_resource
from oauth2_provider.ext.rest_framework import IsAuthenticatedOrTokenHasScope

from rest_framework import permissions, status,viewsets

from .models import Appointment, Actor, Group, Participation
from .serializers import AppointmentSerializer, ActorSerializer, GroupSerializer

# Create your views here.


@require_POST  # only accessible by POST-requests AND
#@protected_resource()  # OAuth-token OR
@login_required()  # Django-login
def appointment_response(request, id):
	if 'answer' in request.POST:  # POST contains correct key
		answer = request.POST['answer']
		try:  # fitting participation in database
			entry = Participation.objects.get(appointment__id= id, actor=request.user)
			if answer == 'yes':  # checks value of answer
				entry.answer = 'y'
			elif answer == 'no':
				entry.answer = 'n'
			else:
				return HttpResponseBadRequest('answer may only be "yes" or "no"')
			entry.save()  # updates participation-object
			return HttpResponse(status=204)
		except Participation.DoesNotExist:
			return HttpResponseServerError('No participation found')
	else:
		return HttpResponseBadRequest('Post did not contain key "answer"')

@require_POST  # only accessible by POST-requests AND
#@protected_resource()  # OAuth-token OR
@login_required()  # Django-login
def add_actor(request, id):
	actors = request.POST.getlist('actors')
	for a in actors:
		try:
			actor = Actor.objects.get(email=a)
			Participation.objects.create(actor=actor, appointment=Appointment.objects.get(id=id))
		except Actor.DoesNotExist:
			pass
	return HttpResponse(status=204)

class AppointmentViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoModelPermissions]
	#required_scopes = ['appointment']
	#queryset = Appointment.objects.all()
	serializer_class = AppointmentSerializer

	def get_queryset(self):
		user = self.request.user
		return Appointment.objects.filter(participants__id__exact = user.id )

class ActorViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoModelPermissions]
	#required_scopes = ['actor']
	queryset = Actor.objects.all()
	serializer_class = ActorSerializer

class GroupViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoModelPermissions]
	serializer_class = GroupSerializer

	def get_queryset(self):
		user = self.request.user
		return Group.objects.filter(members__id__exact=user.id)

