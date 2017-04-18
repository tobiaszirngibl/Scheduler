from rest_framework import serializers

from .models import Actor, Appointment, Group, Participation

"""
Serializers are part of the Django-REST-Framework(DRF).
The are used to specify the way, data, in this case models are represented when calling the API
"""


class ActorSerializer(serializers.ModelSerializer):

	class Meta:
		model = Actor
		fields = ('id', 'email', 'password', 'first_name', 'last_name', 'contact_notes', 'education', 'phone', 'location', 'skills', 'spare_time', 'job')
		write_only_fields = ('password',)
		read_only_fields = ('id',)

	def create(self, validated_data):
		user = Actor()
		for key, value in validated_data.items():
			setattr(user, key, value)

		user.set_password(validated_data['password'])
		user.save()

		return user


class ActorNestedSerializer(serializers.ModelSerializer):
	"""
	Serializer for the Actor-model containing only necessary fields
	"""

	class Meta:
		model = Actor
		fields = ('id', 'email', "first_name", "last_name")
		read_only_fields = ('id', )


class ParticipationSerializer(serializers.ModelSerializer):
	id = serializers.ReadOnlyField(source='actor.id')
	email = serializers.ReadOnlyField(source='actor.email')
	first_name = serializers.ReadOnlyField(source='actor.first_name')
	last_name = serializers.ReadOnlyField(source='actor.last_name')


	class Meta:
		model = Participation
		fields = ('id', 'email', "first_name", "last_name", 'answer', 'is_necessary',)


class AppointmentSerializer(serializers.ModelSerializer):
	participants = ParticipationSerializer(source='participation_set', many=True, read_only=True)
	will_take_place = serializers.ReadOnlyField()
	own_answer = serializers.SerializerMethodField() # defaults to get_own_answer

	class Meta:
		model = Appointment
		fields = '__all__'

	def get_own_answer(self, obj):

		user = self.context['request'].user
		try:
			participation = Participation.objects.get(actor=user, appointment=obj)
			return participation.answer
		except Participation.DoesNotExist:
			return None

	def create(self, validated_data):
		instance = super(AppointmentSerializer, self).create(validated_data)
		instance.organizer = self.context['request'].user
		instance.save()
		return instance

	def update(self, instance, validated_data):
		for key, value in validated_data.items():
			if key != 'participants':
				setattr(instance, key, value)

		instance.save()
		return instance


class GroupSerializer(serializers.ModelSerializer):
	members = ActorNestedSerializer(many=True, read_only=True)

	class Meta:
		model = Group
		fields = '__all__'


class FavoriteSerializer(AppointmentSerializer):
	color = serializers.CharField()
