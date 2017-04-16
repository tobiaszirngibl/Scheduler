from django.conf import settings

from rest_framework import status, viewsets
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Appointment, Actor, Favorite, Group, Participation
from .response_reaction import handle_decline
from .serializers import AppointmentSerializer, ActorSerializer, FavoriteSerializer, GroupSerializer

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
				entry = Participation.objects.get_or_create(actor=request.user, appointment=Appointment.objects.get(id=id))[0]

				if answer.lower() == 'yes':  # checks value of answer
					entry.answer = 'y'
				elif answer.lower() == 'no':
					if handle_decline(request.user, entry, Appointment.objects.get(id=id)): # Other user was invited
						return Response(status=status.HTTP_204_NO_CONTENT)
					else:
						entry.answer = 'n'
				elif answer.lower() == 'pending':
					entry.answer = 'p'
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
				Participation.objects.update_or_create(actor=actor, appointment=Appointment.objects.get(id=id), is_necessary=False)
			except Actor.DoesNotExist:
				print("No user with email %s" % a)
		return Response(status=status.HTTP_204_NO_CONTENT)

class AddCriticalActorToEvent(APIView):
	"""
	Adds an actor to an event, has to be called with POST and one or more emails
	"""
	required_scopes = settings.REST_DEFAULT_SCOPES

	def post(self, request, id):
		actors = request.POST.getlist('actors')
		for a in actors:
			try:
				actor = Actor.objects.get(email=a)
				Participation.objects.update_or_create(actor=actor, appointment=Appointment.objects.get(id=id), is_necessary=True)
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

class LeaveGroup(APIView):
	"""
	The User calling the url gets deleted from the group
	"""
	required_scopes = settings.REST_DEFAULT_SCOPES

	def get(self, request, group_id):
		try:
			group = Group.objects.get(id=group_id)
			group.members.remove(request.user)
			return Response(status=status.HTTP_204_NO_CONTENT)
		except Group.DoesNotExist:
			return Response(data='No group with this id', status=status.HTTP_404_NOT_FOUND)

"""
API Viewsets start here. These are for providing basic functionality like browsing or creating
"""


class AppointmentViewSet(viewsets.ModelViewSet):
	required_scopes = settings.REST_DEFAULT_SCOPES
	serializer_class = AppointmentSerializer

	def get_queryset(self):
		"""
		Gets all Appointments which feature the current user
		"""
		user = self.request.user
		return Appointment.objects.filter(participants__id__exact=user.id)

	def get_appointment_by_status(self, request, status):
		"""
		Gets all Appointments where the requesting user answered with status
		:param request: The request to the API
		:param status: Answer of Participation, yes, no or pending
		:return: JSON-Response containing all Appointments with the specified answer
		"""
		filtered = []
		for app in self.get_queryset():
			try:
				part = Participation.objects.get(actor=request.user, appointment=app)
				if part.get_answer_display().lower() == status:
					filtered.append(app)
			except Participation.DoesNotExist:
				pass
		ser_data = self.get_serializer(filtered, many=True).data
		return Response(data=ser_data)

	def destroy(self, request, *args, **kwargs):
		"""
		Checks that only the creator of an Appointment may delete it
		"""
		instance = self.get_object()
		if request.user is instance.organizer:
			instance.delete()
			return Response(status=status.HTTP_204_NO_CONTENT)
		return Response(data='Only the organizer may delete an Appointment', status=status.HTTP_403_FORBIDDEN)


class ActorViewSet(viewsets.ModelViewSet):
	permission_classes = [AllowAny]
	queryset = Actor.objects.all()
	serializer_class = ActorSerializer


class GroupViewSet(viewsets.ModelViewSet):
	required_scopes = settings.REST_DEFAULT_SCOPES
	serializer_class = GroupSerializer

	def get_queryset(self):
		user = self.request.user
		return Group.objects.filter(members__id__exact=user.id)

class FavoriteViewSet(viewsets.ModelViewSet):
	required_scopes = settings.REST_DEFAULT_SCOPES
	serializer_class = FavoriteSerializer

	def get_queryset(self):
		user = self.request.user
		return Favorite.objects.filter(owner=user)
