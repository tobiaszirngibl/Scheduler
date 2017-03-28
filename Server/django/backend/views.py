from django.contrib.auth.decorators import login_required
from django.http import HttpResponseBadRequest, HttpResponseServerError, HttpResponse
from django.views.decorators.http import require_POST

from oauth2_provider.decorators import protected_resource
from oauth2_provider.ext.rest_framework import IsAuthenticatedOrTokenHasScope
from rest_framework.permissions import AllowAny

from rest_framework import permissions, status,viewsets

from .models import Appointment, Actor, Group, Participation
from .serializers import AppointmentSerializer, ActorSerializer, GroupSerializer

# Create your views here.



@require_POST  # only accessible by POST-requests AND
@login_required()  # Django-login
def appointment_response(request, id):
	if 'answer' in request.POST:  # POST contains correct key
		answer = request.POST['answer']
		try:  # fitting participation in database
			#entry = Participation.objects.get(appointment__id= id, actor=request.user)
			entry = Participation.objects.create(actor=request.user, appointment=Appointment.objects.get(id=id))

			if answer.lower() == 'yes':  # checks value of answer
				entry.answer = 'y'
			elif answer.lower() == 'no':
				entry.answer = 'n'
			elif answer.lower() == "pending":
				entry.answer = "p"
			else:
				return HttpResponseBadRequest('answer may only be "yes" or "no"')
			entry.save()  # updates participation-object
			return HttpResponse(status=204)
		except Participation.DoesNotExist:
			return HttpResponseServerError('No participation found')
	else:
		return HttpResponseBadRequest('Post did not contain key "answer"')

@require_POST  # only accessible by POST-requests AND
@login_required()  # Django-login
def add_actor(request, id):
	actors = request.POST.getlist('actors')
	for a in actors:
		try:
			actor = Actor.objects.get(email=a)
			Participation.objects.create(actor=actor, appointment=Appointment.objects.get(id=id))
		except Actor.DoesNotExist:
			print("No user with email %s" % a)
	return HttpResponse(status=204)

@require_POST
@login_required()
def add_actor_to_group(request, id):
	actors = request.POST.getlist('actors')
	for a in actors:
		try:
			actor = Actor.objects.get(email=a)
			group = Group.objects.get(id=id)
			group.members.add(actor)
		except Actor.DoesNotExist:
			print("No user with email %s" % a)
	return HttpResponse(status=204)

class AppointmentViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoObjectPermissions]
	required_scopes = ['read', 'write']
	serializer_class = AppointmentSerializer

	def get_queryset(self):
		user = self.request.user
		return Appointment.objects.filter(participants__id__exact = user.id )

class ActorViewSet(viewsets.ModelViewSet):
	permission_classes = [AllowAny]
	required_scopes = ['read', 'write']
	queryset = Actor.objects.all()
	serializer_class = ActorSerializer

class GroupViewSet(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticatedOrTokenHasScope, permissions.DjangoObjectPermissions]
	required_scopes = ['read', 'write']
	serializer_class = GroupSerializer


	def get_queryset(self):
		user = self.request.user
		return Group.objects.filter(members__id__exact=user.id)

