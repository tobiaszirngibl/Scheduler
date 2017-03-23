from rest_framework import serializers

from backend.models import Actor, Appointment

class ActorSerializer(serializers.ModelSerializer):
	email = serializers.ReadOnlyField(source='get_name')

	class Meta:
		model = Actor
		fields = ('id', 'email', 'first_name', 'last_name', 'bio',)

class AppointmentSerializer(serializers.ModelSerializer):
	participants = ActorSerializer(many=True, read_only=True)

	class Meta:
		model = Appointment
		fields = '__all__'

