from django.contrib.auth.decorators import login_required
from django.http import HttpResponseRedirect
from django.shortcuts import redirect, render, get_object_or_404

from backend.models import Actor

from .forms import AvatarForm


# Create your views here.

@login_required
def profile_view(request, user_id):
	user = get_object_or_404(Actor, id=user_id)
	"""user = {"email": request.user.email,
	        "firstname": request.user.first_name,
	        "lastname": request.user.last_name,
	        "contact_notes": request.user.get_contact_notes,
	        "education": request.user.get_education,
	        "phone": request.user.phone,
	        "skills": request.user.skills,
	        "spare_time": request.user.spare_time,
	        "location": request.user.location,
	        "job": request.user.job,
	        "avatar": request.user.avatar,
	        }
	"""
	return render(request, 'website/profile.html', {"user": user})


@login_required
def calendar_view(request):
	"""user = {"email": request.user.email,
	        "first_name": request.user.first_name,
	        "last_name": request.user.last_name
	        }"""
	return render(request, 'website/calendar.html', {"user": request.user})


@login_required
def group_view(request):
	"""user = {"email": request.user.email,
	        "first_name": request.user.first_name,
	        "last_name": request.user.last_name
	        }"""
	return render(request, 'website/groups.html', {"user": request.user})


@login_required
def login_redirect(request):
	return HttpResponseRedirect('/accounts/private_profile/%d/' % request.user.id)


@login_required
def upload_avatar(request):
	if request.method == 'POST':
		form = AvatarForm(request.POST, request.FILES)
		if form.is_valid():
			request.user.avatar = request.FILES['avatar']
			request.user.save()

	return redirect('profile_view')
