from .models import Actor, Appointment, Participation

def handle_decline(user, participation, event):
	"""
	This method determines what to do when a user declines an invitation.
	If he is necessary for the event, it will be cancelled, otherwise his understudy is invited
	:param user: The user which has declined the invitation
	:param event: The Appointment
	:return: False, if user was necessary and nothing could be done, else True
	"""
	if participation.is_necessary is True or user.understudy is None: # Nothing to handle, continue logic from view
		return False

	# Adds understudy to Appointment
	Participation.objects.create(actor=user.understudy, appointment=event)
	# Removes declining user from Appointment
	# currently not used because we may want another behaviour
	#participation.delete()

	return True

