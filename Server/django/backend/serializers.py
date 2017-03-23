from rest_framework import serializers

from backend.models import Actor, Appointment

class ActorSerializer(serializers.ModelSerializer):

	class Meta:
		model = Actor
		fields = ('id', 'email', 'password','first_name', 'last_name', 'bio',)
		write_only_fields = ('password',)
		read_only_fields = ('id',)

	def create(self, validated_data):
		user = Actor.objects.create(
			email=validated_data['email'],
			first_name=validated_data['first_name'],
			last_name=validated_data['last_name']
		)

		user.set_password(validated_data['password'])
		user.save()

		return user

class AppointmentSerializer(serializers.ModelSerializer):
	participants = ActorSerializer(many=True, read_only=True)

	class Meta:
		model = Appointment
		fields = '__all__'

