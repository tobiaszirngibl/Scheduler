from rest_framework import serializers

from backend.models import Actor, Appointment, Group, Participation

class ActorSerializer(serializers.ModelSerializer):

	class Meta:
		model = Actor
		fields = ('id', 'email', 'password','first_name', 'last_name', 'bio',)
		write_only_fields = ('password')
		read_only_fields = ('id',)

	def create(self, validated_data):
		user = Actor()
		for key, value in validated_data.items():
			setattr(user, key, value)

		user.set_password(validated_data['password'])
		user.save()

		return user


class AppointmentSerializer(serializers.ModelSerializer):
	participants = ActorSerializer(many=True)

	class Meta:
		model = Appointment
		fields = '__all__'

	def update(self, instance, validated_data):
		for key, value in validated_data.items():
			if key != 'participants':
				setattr(instance, key, value)
		instance.save()
		return instance

class GroupSerializer(serializers.ModelSerializer):
	members = ActorSerializer(many=True)

	class Meta:
		model = Group
		fields = '__all__'

