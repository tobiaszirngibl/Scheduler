from rest_framework import serializers

from backend.models import Actor, Appointment, User

class ActorSerializer(serializers.ModelSerializer):
	user = serializers.ReadOnlyField(source='get_name')

	class Meta:
		model = Actor
		fields = ('id', 'user', 'bio',)

class AppointmentSerializer(serializers.ModelSerializer):
	participants = ActorSerializer(many=True, read_only=True)

	class Meta:
		model = Appointment
		fields = '__all__'

