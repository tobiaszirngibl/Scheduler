from django.conf import settings

from rest_framework import status, viewsets
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Appointment, Actor, Group, Participation
from .serializers import AppointmentSerializer, ActorSerializer, GroupSerializer

# Create your views here.


class AppointmentResponse(APIView):
	"""
	Saves the response of a user pertaining to his participation of an event
	Has to be called with POST and an Appointment-id. User will be determined by the request
	"""
	required_scopes = settings.REST_DEFAULT_SCOPES

	def post(self, request, id):
		if 'answer' in request.POST:  # POST contains correct key
			answer = request.POST['answer']
			try:  # fitting participation in database
				entry = Participation.objects.get(appointment__id=id, actor=request.user)

				if answer.lower() == 'yes':  # checks value of answer
					entry.answer = 'y'
				elif answer.lower() == 'no':
					entry.answer = 'n'
				else:
					return Response(data='answer may only be "yes" or "no"', status=status.HTTP_400_BAD_REQUEST)
				entry.save()  # updates participation-object
				return Response(status=status.HTTP_204_NO_CONTENT)
			except Participation.DoesNotExist:
				return Response(data='No participation found', status=status.HTTP_500_INTERNAL_SERVER_ERROR)
		else:
			return Response(data='Post did not contain key "answer"', status=status.HTTP_400_BAD_REQUEST)


class AddActorToEvent(APIView):
	"""
	Adds an actor to an event, has to be called with POST and one or more emails
	"""
	required_scopes = settings.REST_DEFAULT_SCOPES

	def post(self, request, id):
		actors = request.POST.getlist('actors')
		for a in actors:
			try:
				actor = Actor.objects.get(email=a)
				Participation.objects.create(actor=actor, appointment=Appointment.objects.get(id=id))
			except Actor.DoesNotExist:
				print("No user with email %s" % a)
		return Response(status=status.HTTP_204_NO_CONTENT)


class AddActorToGroup(APIView):
	"""
	Adds an actor to a group, has to be called with POST and one or more emails
	"""
	required_scopes = settings.REST_DEFAULT_SCOPES

	def post(self, request, id):
		actors = request.POST.getlist('actors')
		for a in actors:
			try:
				actor = Actor.objects.get(email=a)
				group = Group.objects.get(id=id)
				group.members.add(actor)
			except Actor.DoesNotExist:
				print("No user with email %s" % a)
		return Response(status=status.HTTP_204_NO_CONTENT)


"""
API Viewsets start here. These are for providing basic functionality like browsing or creating
"""


class AppointmentViewSet(viewsets.ModelViewSet):
	required_scopes = settings.REST_DEFAULT_SCOPES
	serializer_class = AppointmentSerializer

	def get_queryset(self):
		user = self.request.user
		return Appointment.objects.filter(participants__id__exact=user.id)


class ActorViewSet(viewsets.ModelViewSet):
	permission_classes = (AllowAny)
	queryset = Actor.objects.all()
	serializer_class = ActorSerializer


class GroupViewSet(viewsets.ModelViewSet):
	required_scopes = settings.REST_DEFAULT_SCOPES
	serializer_class = GroupSerializer

	def get_queryset(self):
		user = self.request.user
		return Group.objects.filter(members__id__exact=user.id)
