from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect, render


# Create your views here.

def profile_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name,
			"bio": request.user.get_bio}
	return render(request, 'website/profile.html', {"user": user})

def calendar_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name}
	return render(request, 'website/calendar.html', {"user": user})

def group_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name}
	return render(request, 'website/groups.html', {"user": user})



@login_required
def login_redirect(request):
    return HttpResponseRedirect('/accounts/private_profile/%d/'%request.user.id)